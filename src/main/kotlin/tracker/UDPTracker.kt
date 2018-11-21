package com.huoyaojing.tracker.tracker;

import com.huoyaojing.tracker.Utils.HexUtils
import com.huoyaojing.tracker.Utils.IpUtils
import com.huoyaojing.tracker.bean.Connect
import com.huoyaojing.tracker.config.Config
import com.huoyaojing.tracker.db.SqliteDB
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.SocketAddress
import io.vertx.kotlin.core.json.get
import kotlinx.coroutines.experimental.launch
import java.util.*


class UDPTracker(vertx: Vertx, db: SqliteDB) {
    var loger = LoggerFactory.getLogger(UDPTracker::class.java)
    private var mConnectionIdIndex: Long = 0
    private val mConnectList = HashMap<Long, Connect>();
    private val mDelConnectList = HashMap<Long, ArrayList<Long>>();
    private var mdb: SqliteDB
    private var vertx: Vertx
    private var mNowTime: Long = 0L;

    init {
        this.mdb = db;
        this.vertx = vertx

        refreshConnectId(vertx);
    }

    fun connnect(sender: SocketAddress, buffer: Buffer) {
        var connection_id = genConnectionId(sender)
        buffer.setLong(buffer.length(), connection_id)
    }

    suspend fun announce(inBuffer: Buffer, outBuffer: Buffer, connect_id: Long) {
        var info_hash: String = HexUtils.encode(inBuffer.getBytes(0, 20))  //0-20
        var peer_id: String = HexUtils.encode(inBuffer.getBytes(20, 40))  //20-40
        var downloaded: Long = inBuffer.getLong(40) //40-48
        var left: Long = inBuffer.getLong(48)   //48-56
        var uploaded: Long = inBuffer.getLong(56)   //56-64
        var event: Int = inBuffer.getInt(64)        //64-68 0: none; 1: completed; 2: started; 3: stopped
        var IP_address: Int = inBuffer.getInt(68)    // 68-72 default
        var key: Int = inBuffer.getInt(72)  //72-76
        var num_want: Int = inBuffer.getInt(76)    //76-80 -1 default
        var port: Short = inBuffer.getShort(48) //80-82

        loger.info(
                "\n=====================================================" +
                        "\npeer:" +
                        "\npeer_id:" + peer_id +
                        "\ninfo_hash:" + info_hash +
                        "\nIP_address:" + IpUtils.longtoipV4(IP_address.toLong()) +
                        "\nport:" + port.toString() +
                        "\ndownloaded:" + downloaded.toString() +
                        "\nleft:" + left.toString() +
                        "\nuploaded:" + uploaded.toString() +
                        "\nevent:" + event.toString() +
                        "====================================================="
        );

        //种子的信息
        var seeders: Int = 0;
        var leechers: Int = 0;
        var completed: Int = 0

        this.mdb!!.addTorrent(info_hash);

        var infoRet = this.mdb!!.getTorrentInfo(info_hash)
        if (infoRet.rows.size > 0) {
            completed = infoRet.rows[0]["completed"]
            leechers = infoRet.rows[0]["leechers"]
            seeders = infoRet.rows[0]["seeders"]
        }

        //获取节点信息
        var peerRet = this.mdb!!.getPeer(peer_id, info_hash)
        //不存在节点信息
        if (peerRet.rows.size == 0) {
            if (event == 1) {
                completed = completed + 1;
            } else if (event == 2) {
                leechers = leechers + 1;
            }

            if (event != 0 && event != 3 && downloaded > 0) {
                seeders = seeders + 1
            }
        } else if (peerRet.rows.size > 0 && event != 0 && event != peerRet.rows[0]["last_seen"]) {
            if (event == 3) {
                var oldDownload: Long = peerRet.rows[0]["downloaded"]
                if (oldDownload > 0) {
                    seeders = seeders - 1
                    if (seeders < 0) seeders = 0;
                }
                var last_seen: Int = peerRet.rows[0]["last_seen"]
                if (last_seen == 1) {
                    completed = completed - 1
                    if (completed < 0) completed = 0;
                } else {
                    leechers = leechers - 1
                    if (leechers < 0) leechers = 0;
                }
            }
        } else if (peerRet.rows.size > 0 && event != 3) {
            var oldDownload: Long = peerRet.rows[0]["downloaded"]
            //做种加1
            if (oldDownload == 0L && event != 0 && event != 3 && downloaded > 0) {
                seeders = seeders + 1
            }
        }
        //更新到数据里最新数据
        this.mdb!!.setTorrentInfo(info_hash, seeders, leechers, completed)

        if (event == 3) {
            this.mdb!!.removePeer(peer_id, info_hash);
            this.mdb!!.removePeerHash(connect_id, peer_id, info_hash)
        } else {
            if (event == 0) {
                event = peerRet.rows[0]["last_seen"]
            }
            this.mdb!!.updatePeer(peer_id, info_hash,
                    IP_address, port, downloaded,
                    left, uploaded, event)
            this.mdb!!.updatePeerHash(connect_id, peer_id, info_hash)
        }


        outBuffer.setInt(outBuffer.length(), Config.announce_interval);
        outBuffer.setInt(outBuffer.length(), leechers);
        outBuffer.setInt(outBuffer.length(), seeders);

        var num: Int = num_want;
        if (num == 0 || event == 3) {
            return;
        }

        if (Config.announce_max_peer_num > 0 && num > Config.announce_max_peer_num) {
            num = Config.announce_max_peer_num
        }
        var peersRet = this.mdb!!.getPeers(info_hash, num)
        var i: Int = 0
        for (i in 0..peersRet.rows.size - 1) {
            var ip: Int = peersRet.rows[i]["ip"]
            var port: Short = peersRet.rows[i]["port"]
            outBuffer.setInt(outBuffer.length(), ip)
            outBuffer.setShort(outBuffer.length(), port)
        }
    }

    suspend fun scrape(inBuffer: Buffer, outBuffer: Buffer) {
        var num: Int = inBuffer.length() / 20
        for (i in 0..num - 1) {
            var info_hash: String = HexUtils.encode(inBuffer.getBytes(0, 20))

            var seeders: Int = 0;
            var leechers: Int = 0;
            var completed: Int = 0

            var infoRet = this.mdb!!.getTorrentInfo(info_hash)
            if (infoRet.rows.size <= 0) continue;

            completed = infoRet.rows[0]["completed"]
            leechers = infoRet.rows[0]["leechers"]
            seeders = infoRet.rows[0]["seeders"]

            outBuffer.setInt(outBuffer.length(), seeders);
            outBuffer.setInt(outBuffer.length(), completed);
            outBuffer.setInt(outBuffer.length(), leechers);
        }
    }

    fun verifyConnectionId(cId: Long, address: SocketAddress): Boolean {
        if (!mConnectList.containsKey(cId)) {
            return false
        }
        val connect = mConnectList[cId]
        return connect!!.addr.host() == address.host() && connect!!.addr.port() == address.port()
    }

    fun updateConnectId(cId: Long) {

        var connect = mConnectList.get(cId)
        if (connect == null) {
            return;
        }
        connect.updateTime = mNowTime;
        val delTime = mNowTime + Config.announce_interval.toLong() + Config.timeout_interval.toLong()

        if (mDelConnectList.containsKey(delTime)) {
            mDelConnectList.get(delTime)!!.add(cId)
        } else {
            val tmp = ArrayList<Long>()
            tmp.add(cId)
            mDelConnectList.put(delTime, tmp)
        }
    }

    private fun genConnectionId(address: SocketAddress): Long {
        var ret: Long
        while (true) {
            ret = mConnectionIdIndex++

            if (!mConnectList.containsKey(ret)) {
                mConnectList.put(ret, Connect(ret, address, mNowTime))
                break
            }
        }
        return ret
    }

    suspend private fun cleanConnect(connect_id: Long) {
        var peerHashRet = this.mdb!!.getPeerHash(connect_id)
        if (peerHashRet.rows.size <= 0) return;
        for (row in peerHashRet.rows) {
            var peer_id: String = row["peer_id"];
            var info_hash: String = row["info_hash"]

            var peerRet = this.mdb.getPeer(peer_id, info_hash)
            if (peerRet.rows.size <= 0) continue;

            var infoRet = this.mdb.getTorrentInfo(info_hash)
            var seeders: Int = 0;
            var leechers: Int = 0;
            var completed: Int = 0

            if (infoRet.rows.size > 0) {
                completed = infoRet.rows[0]["completed"]
                leechers = infoRet.rows[0]["leechers"]
                seeders = infoRet.rows[0]["seeders"]
            }

            var oldDownload: Long = peerRet.rows[0]["downloaded"]
            if (oldDownload > 0) {
                seeders = seeders - 1
                if (seeders < 0) seeders = 0;
            }
            var last_seen: Int = peerRet.rows[0]["last_seen"]
            if (last_seen == 1) {
                completed = completed - 1
                if (completed < 0) completed = 0;
            } else {
                leechers = leechers - 1
                if (leechers < 0) leechers = 0;
            }
            this.mdb.setTorrentInfo(info_hash, seeders, leechers, completed)
            this.mdb.removePeer(peer_id, info_hash);
        }
        this.mdb.clearPeerHash(connect_id)
    }

    private fun refreshConnectId(vertx: Vertx) {
        vertx.setPeriodic(1000) { v ->
            mNowTime += 1L

            val willDeleteList = mDelConnectList.get(mNowTime)
            if (willDeleteList == null) {
                return@setPeriodic;
            }
            for (connect_id in willDeleteList) {
                if (!mConnectList.containsKey(connect_id)) {
                    continue;
                }
                var connect = mConnectList.get(connect_id)
                if (connect!!.updateTime <= mNowTime) {
                    mConnectList.remove(connect_id)
                    launch {
                        this@UDPTracker.cleanConnect(connect_id);
                    }
                }
            }
            mDelConnectList.remove(mNowTime)
        }
    }
}