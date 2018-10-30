package com.huoyaojing.tracker.tracker;

import com.huoyaojing.tracker.Bean.AnnounceRequestBean;
import com.huoyaojing.tracker.Bean.AnnounceRespondBean;
import com.huoyaojing.tracker.Bean.ConnectRequestBean;
import com.huoyaojing.tracker.Bean.ConnectRespondBean;
import com.huoyaojing.tracker.db.SqliteDB;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;

import java.util.HashMap;
import java.util.List;

public class UDPTracker {

    private long mConnectionIdIndex = 0;
    private HashMap<Long, SocketAddress> mConnectList = new HashMap<Long, SocketAddress>();
    private HashMap<Long, List<Long>> mRmConnect = new HashMap<Long, List<Long>>();
    private SqliteDB mDb;

    public UDPTracker(SqliteDB db) {
        mDb = db;
    }

    public void onConnect(DatagramSocket socket, ConnectRequestBean requestBean, SocketAddress sender) {
        ConnectRespondBean respondBean = new ConnectRespondBean();
        respondBean.action = requestBean.action;
        respondBean.transaction_id = requestBean.transaction_id;
        respondBean.connection_id = genConnectionId(sender);
        Buffer buffer = respondBean.getBuff();

        socket.send(buffer, sender.port(), sender.host(), asyncResult -> {
            System.out.println("Send succeeded? " + asyncResult.succeeded());
        });
    }

    public void onAnnounce(DatagramSocket socket, AnnounceRequestBean requestBean, SocketAddress sender) {
        //验证connect不成功，直接断开
        if (!verifyConnectionId(requestBean.connection_id, sender)) {
            return;
        }
        // 0: none; 1: completed; 2: started; 3: stopped
        int event = requestBean.event;

        mDb.updatePeer(requestBean.peer_id, requestBean.info_hash,
                requestBean.IP_address, requestBean.port, requestBean.downloaded,
                requestBean.left, requestBean.uploaded, requestBean.event, res -> {
                    if(res.succeeded())
                    {
                        System.out.println("update perr");
                    }

                }
        );

        AnnounceRespondBean respondBean = new AnnounceRespondBean();
        respondBean.action = requestBean.action;
        respondBean.transaction_id = requestBean.transaction_id;

        mDb.getTorrentInfo(requestBean.info_hash, res->{
            if(res.failed())
            {
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
                return;
            }
            List<JsonObject> dbRetObjectLists = res.result().getRows();
            if(dbRetObjectLists.size() > 0)
            {
                JsonObject dbRetObject = dbRetObjectLists.get(0);
                respondBean.leechers = dbRetObject.getInteger("leechers");
                respondBean.seeders = dbRetObject.getInteger("seeders");
                //respondBean.completed = dbRetObject.getInteger("completed");
            }

            //返回给客户端的数据
            int num = requestBean.num_want;
            if(num < 0)
            {
                num = 30;
            }

            //stop 3
            if (event == 3 || num == 0)
            {
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
                return;
            }

            mDb.getPeers(requestBean.info_hash, num, ret->{
                if(ret.succeeded())
                {
                    List<JsonObject> dbRetObjectLists2 = ret.result().getRows();
                    for(int i =0; i < dbRetObjectLists2.size() ;++i)
                    {
                        JsonObject dbRetObject = dbRetObjectLists2.get(i);
                        int ip   = dbRetObject.getInteger("ip");
                        int port = dbRetObject.getInteger("port");
                        respondBean.IP_address.add(ip);
                        respondBean.TCP_port.add((short)port);
                    }
                }
                socket.send(respondBean.getBuff(), sender.port(), sender.host(), asyncResult -> {
                    System.out.println("Send succeeded? " + asyncResult.succeeded());
                });
            });
        });
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
