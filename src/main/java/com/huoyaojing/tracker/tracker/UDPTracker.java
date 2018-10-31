package com.huoyaojing.tracker.tracker;

import com.huoyaojing.tracker.Bean.*;
import com.huoyaojing.tracker.config.Config;
import com.huoyaojing.tracker.db.SqliteDB;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPTracker {

    private long mConnectionIdIndex = 0;
    private HashMap<Long, SocketAddress> mConnectList = new HashMap<Long, SocketAddress>();
    private HashMap<Long, List<Long>> mRmConnect = new HashMap<Long, List<Long>>();
    private HashMap<Long, Long> mRmConnectTimeList = new HashMap<Long, Long>();

    private SqliteDB mDb;
    private long mNowTime = new Date().getTime();

    public UDPTracker(Vertx vertx, SqliteDB db) {
        refreshConnectId(vertx);
        mDb = db;
    }

    public void sendError(DatagramSocket socket,
                     int transaction_id, String error, SocketAddress sender) {
        ErrorRespondBean respondBean = new ErrorRespondBean();
        respondBean.action =  3;
        respondBean.transaction_id = transaction_id;
        respondBean.errorStr = error;
        socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
            System.out.println("Send  error ");
        });
    }


    public void onConnect(DatagramSocket socket, ConnectRequestBean requestBean, SocketAddress sender) {
        ConnectRespondBean respondBean = new ConnectRespondBean();
        respondBean.action = requestBean.action;
        respondBean.transaction_id = requestBean.transaction_id;
        respondBean.connection_id = genConnectionId(sender);
        Buffer buffer = respondBean.getBuff();
        updateConnectId(respondBean.connection_id);
        socket.send(buffer, sender.port(), sender.host(), asyncResult -> {
            System.out.println("Send succeeded? " + asyncResult.succeeded());
        });


    }

    public void onAnnounce(DatagramSocket socket, AnnounceRequestBean requestBean, SocketAddress sender) {
        //验证connect不成功，直接断开
        if (!verifyConnectionId(requestBean.connection_id, sender)) {
            sendError(socket,
                    requestBean.transaction_id, "Please send connect request", sender);
            return;
        }
        updateConnectId(requestBean.connection_id);
        // 0: none; 1: completed; 2: started; 3: stopped
        int event = requestBean.event;

        mDb.updatePeer(requestBean.peer_id, requestBean.info_hash,
                requestBean.IP_address, requestBean.port, requestBean.downloaded,
                requestBean.left, requestBean.uploaded, requestBean.event, res -> {
                    if (res.succeeded()) {
                        System.out.println("update perr");
                    }

                }
        );

        AnnounceRespondBean respondBean = new AnnounceRespondBean();
        respondBean.interval = Config.announce_interval;
        respondBean.action = requestBean.action;
        respondBean.transaction_id = requestBean.transaction_id;

        mDb.getTorrentInfo(requestBean.info_hash, res -> {
            if (res.failed()) {
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
                return;
            }
            List<JsonObject> dbRetObjectLists = res.result().getRows();
            if (dbRetObjectLists.size() > 0) {
                JsonObject dbRetObject = dbRetObjectLists.get(0);
                respondBean.leechers = dbRetObject.getInteger("leechers");
                respondBean.seeders = dbRetObject.getInteger("seeders");
                //respondBean.completed = dbRetObject.getInteger("completed");
            }

            //返回给客户端的数据
            int num = requestBean.num_want;
            if (num < 0) {
                num = 30;
            }

            //stop 3
            if (event == 3 || num == 0) {
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
                return;
            }

            mDb.getPeers(requestBean.info_hash, num, ret -> {
                if (ret.succeeded()) {
                    List<JsonObject> dbRetObjectLists2 = ret.result().getRows();
                    for (int i = 0; i < dbRetObjectLists2.size(); ++i) {
                        JsonObject dbRetObject = dbRetObjectLists2.get(i);
                        int ip = dbRetObject.getInteger("ip");
                        int port = dbRetObject.getInteger("port");
                        respondBean.IP_address.add(ip);
                        respondBean.TCP_port.add((short) port);
                    }
                }
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
            });
        });
    }

    public void onScrape(DatagramSocket socket, ScrapeRequestBean requestBean, SocketAddress sender) {
        ScrapeRespondBean respondBean = new ScrapeRespondBean();
        updateConnectId(respondBean.connection_id);
        if (requestBean.hashIdList.size() == 0) {
            socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                System.out.println("Send succeeded? " + asyncResult.succeeded());
            });
            return;
        }

        AtomicInteger total = new AtomicInteger();
        for (int i = 0; i < requestBean.hashIdList.size(); ++i) {
            mDb.getTorrentInfo(requestBean.hashIdList.get(i), res -> {
                total.addAndGet(1);

                List<JsonObject> dbRetObjectLists = res.result().getRows();
                if (dbRetObjectLists.size() > 0) {
                    JsonObject dbRetObject = dbRetObjectLists.get(0);
                    respondBean.completed.add(dbRetObject.getInteger("leechers"));
                    respondBean.seeders.add(dbRetObject.getInteger("seeders"));
                    respondBean.completed.add(dbRetObject.getInteger("completed"));
                } else {
                    respondBean.completed.add(0);
                    respondBean.seeders.add(0);
                    respondBean.completed.add(0);
                }

                if (total.get() == requestBean.hashIdList.size()) {
                    socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                        System.out.println("Send succeeded? " + asyncResult.succeeded());
                    });
                }
            });
        }
    }

    private void refreshConnectId(Vertx vertx) {
        vertx.setPeriodic(1000, v -> {
            mNowTime += 1L;
            if (!mRmConnect.containsKey(mNowTime)) {
                return;
            }
            List<Long> willDeleteList = mRmConnect.get(mNowTime);
            for (int i = 0; i < willDeleteList.size(); ++i) {
                long connect_id = willDeleteList.get(i);

                if (mConnectList.containsKey(connect_id) &&
                        (!mRmConnectTimeList.containsKey(connect_id) || mRmConnectTimeList.get(connect_id) <= mNowTime)) {
                    mConnectList.remove(connect_id);
                    mRmConnectTimeList.remove(connect_id);
                }
            }
            mRmConnect.remove(mNowTime);
        });
    }


    private void updateConnectId(long cId) {
        long delTime = mNowTime + Config.announce_interval + Config.timeout_interval;
        mRmConnectTimeList.put(cId, delTime);
        if (mRmConnect.containsKey(delTime)) {
            mRmConnect.get(delTime).add(cId);
        } else {
            List<Long> tmp = new ArrayList<Long>();
            tmp.add(cId);
            mRmConnect.put(delTime, tmp);
        }
    }

    private boolean verifyConnectionId(long cId, SocketAddress address) {
        if (!mConnectList.containsKey(cId)) {
            return false;
        }
        SocketAddress cAddress = mConnectList.get(cId);
        return cAddress.host().equals(address.host()) && cAddress.port() == address.port();
    }

    private long genConnectionId(SocketAddress address) {
        long ret;
        while (true) {
            ret = mConnectionIdIndex++;

            if (!mConnectList.containsKey(ret)) {
                mConnectList.put(ret, address);
                break;
            }
        }
        return ret;
    }

}
