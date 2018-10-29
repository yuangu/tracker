package com.huoyaojing.tracker.verticle;

import com.huoyaojing.tracker.Bean.ConnectRequestBean;
import com.huoyaojing.tracker.Bean.ConnectRespondBean;
import com.huoyaojing.tracker.Utils.BuffUtils;
import com.huoyaojing.tracker.Utils.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramPacket;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;

public class ServerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        startUDPServer();
    }

    private void onConnect(DatagramSocket socket, DatagramPacket packet){
        ConnectRequestBean requestBean = new ConnectRequestBean();
        requestBean.setBuff(packet.data());

        ConnectRespondBean respondBean = new ConnectRespondBean();
        respondBean.action = 0;
        respondBean.transaction_id = requestBean.transaction_id;
        respondBean.connection_id = Utils.genConnectionId(packet.sender().host(), packet.sender().port());
        Buffer buffer = Buffer.buffer();
        respondBean.setBuff(buffer);

        socket.send(buffer,packet.sender().port(), packet.sender().host(), asyncResult -> {
            System.out.println("Send succeeded? " + asyncResult.succeeded());
        });
    }


    private void startUDPServer() {
        DatagramSocket socket = vertx.createDatagramSocket(new DatagramSocketOptions());
        socket.listen(1818, "0.0.0.0", asyncResult -> {
            if (asyncResult.succeeded()) {
                socket.handler(packet -> {
                   long connection_id =  BuffUtils.getLong(packet.data().getBytes(), 0);
                   int action = BuffUtils.getInt(packet.data().getBytes(), 8);
                    if(connection_id  == 0x41727101980L && action == 0)
                    {
                        onConnect(socket,packet);
                    }


                });
            } else {
                System.out.println("Listen failed" + asyncResult.cause());
            }
        });
    }
}
