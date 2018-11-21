package com.huoyaojing.tracker.verticle;

import com.huoyaojing.tracker.config.Config
import com.huoyaojing.tracker.db.SqliteDB
import com.huoyaojing.tracker.tracker.UDPTracker
import io.vertx.core.buffer.Buffer
import io.vertx.core.datagram.DatagramPacket
import io.vertx.core.datagram.DatagramSocket
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult

import kotlinx.coroutines.experimental.launch

class  ServerVerticle: CoroutineVerticle(){
    internal var loger = LoggerFactory.getLogger(com.huoyaojing.tracker.verticle.ServerVerticle::class.java)
    internal var mUDPTracker: UDPTracker? = null;

    suspend override fun start() {
        launch {
            var db = SqliteDB(vertx);
            mUDPTracker = UDPTracker(vertx, db);
            startUDPServer()
        }
    }

    private suspend  fun onMessage(packet: DatagramPacket, socket: DatagramSocket) {
        if (packet.data().length() < 16) {
            return
        }

        val connection_id = packet.data().getLong(0)
        val action = packet.data().getInt(8)
        val transaction_id = packet.data().getInt(12)

        //验证connection_id
        if (action != 0)
        {
            if(!mUDPTracker?.verifyConnectionId(connection_id, packet.sender())!!)
            {
                loger.info("verifyConnectionId error:" + packet.sender().host() + ":" + packet.sender().port().toString())
                return;
            }
        }else{
            if (connection_id != 0x41727101980L) {
                loger.info("connect with a error connection_id :(" + connection_id.toString() + ")" + packet.sender().host() + ":" + packet.sender().port().toString())
                return;
            }
        }
        mUDPTracker?.updateConnectId(connection_id)
        val buff = Buffer.buffer()
        buff.setInt(buff.length(), action)
        buff.setInt(buff.length(), transaction_id)
        when(action){
            0 -> {
                mUDPTracker?.connnect(packet.sender(), buff);
            }

            1 -> {
                mUDPTracker?.announce(packet.data().slice(16, packet.data().length()),buff, connection_id );
            }

            2 -> {
                mUDPTracker?.scrape(packet.data().slice(16, packet.data().length()),buff)
            }

            else ->{
                loger.debug("unknown action:" + action.toString())
                return;
            }
        }

        awaitResult<DatagramSocket> { socket.send(buff, packet.sender().port(), packet.sender().host(), it)};
    }


    private suspend fun startUDPServer(){
        val socket = vertx.createDatagramSocket(DatagramSocketOptions())
        awaitResult<DatagramSocket> { socket.listen(Config.port, "0.0.0.0", it); }
        socket.handler { packet ->
            launch {
                this@ServerVerticle.onMessage(packet, socket)
            }
        }
    }

}

