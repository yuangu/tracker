package com.huoyaojing.tracker.verticle;

import com.huoyaojing.tracker.Bean.AnnounceRequestBean;
import com.huoyaojing.tracker.Bean.ConnectRequestBean;
import com.huoyaojing.tracker.Bean.ScrapeRequestBean;
import com.huoyaojing.tracker.db.SqliteDB;
import com.huoyaojing.tracker.tracker.UDPTracker;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;


public class ServerVerticle extends AbstractVerticle {
    UDPTracker mUDPTracker ;
    SqliteDB mDb;
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        mDb = new SqliteDB(vertx);
        mUDPTracker = new UDPTracker(mDb);
        startUDPServer();
    }

    private void startUDPServer() {
        DatagramSocket socket = vertx.createDatagramSocket(new DatagramSocketOptions());
        socket.listen(4848, "0.0.0.0", asyncResult -> {
            if (!asyncResult.succeeded()) {
                return;
            }
            socket.handler(packet -> {
                if(packet.data().length() < 16)
                {
                    return;
                }

                long connection_id = packet.data().getLong(0);
                int action = packet.data().getInt(8);
                int transaction_id_ = packet.data().getInt(12);

                switch (action) {
                    case 0: {
                        if (connection_id != 0x41727101980L) {
                            break;
                        }
                        ConnectRequestBean requestBean = new ConnectRequestBean();
                        requestBean.setBuff(connection_id, action, transaction_id_, packet.data());
                        mUDPTracker.onConnect(socket, requestBean, packet.sender());
                        break;
                    }
                    case 1:{
                        AnnounceRequestBean requestBean = new AnnounceRequestBean();
                        requestBean.setBuff(connection_id, action, transaction_id_, packet.data());
                        mUDPTracker.onAnnounce(socket, requestBean, packet.sender());
                        break;
                    }

                    case 2:{
                        ScrapeRequestBean  requestBean = new ScrapeRequestBean ();
                        requestBean.setBuff(connection_id, action, transaction_id_, packet.data());
                        mUDPTracker.onScrape(socket, requestBean, packet.sender());
                        break;
                    }
                }
            });
        });
    }
}
