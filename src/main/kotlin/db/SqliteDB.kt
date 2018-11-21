package com.huoyaojing.tracker.db

import io.vertx.core.Vertx
import io.vertx.ext.sql.ResultSet
import io.vertx.kotlin.core.json.get
import kotlinx.coroutines.experimental.launch

import java.util.Date

class SqliteDB(vertx: Vertx) {
    internal var mDBPoolManager: DBPoolManager

    init {
        mDBPoolManager = DBPoolManager(vertx)
        launch {
            setup()
        }
    }

    suspend fun setup() {
        var sql = "CREATE TABLE IF NOT EXISTS  stats (info_hash text(40) UNIQUE, " +
                "completed INTEGER DEFAULT 0, " +
                "leechers INTEGER DEFAULT 0, " +
                "seeders INTEGER DEFAULT 0," +
                "last_mod INTEGER DEFAULT 0 )"

        mDBPoolManager.execute(sql)

        sql = "delete from stats"
        mDBPoolManager.execute(sql)

        sql = "CREATE TABLE IF NOT EXISTS torrents (" +
                "info_hash text(40) UNIQUE," +
                "created INTEGER" +
                ")"

        mDBPoolManager.execute(sql)

        sql = "SELECT name FROM sqlite_master"
        var ret = mDBPoolManager.query(sql)
        for (row in ret.rows){
            var name:String = row["name"]
            if(name.startsWith("t_") || name.startsWith("c_")){
                sql = "DROP TABLE $name"
                mDBPoolManager.execute(sql)
            }
        }
    }

    suspend fun getTorrentInfo(hashId: String) :ResultSet {
        val sql = "SELECT seeders,leechers,completed FROM 'stats' WHERE info_hash='$hashId'"
        return  mDBPoolManager.query(sql)
    }

    suspend fun setTorrentInfo(hashId: String,seeders:Int,leechers:Int,completed:Int) {
        val time = Date().time;
        val sql = "REPLACE INTO  'stats' (info_hash, seeders,leechers,completed,last_mod) VALUES ('$hashId',$seeders,$leechers,$completed,$time)"
        mDBPoolManager.execute(sql)
    }

    suspend  fun getPeer(peer_id: String, hash_id: String):ResultSet {
        val sql = String.format("SELECT * FROM t_%s where peer_id='%s'", hash_id, peer_id)
        return mDBPoolManager.query(sql);
    }

    suspend fun getPeers(hash_id: String, max_number: Int):ResultSet {
        val sql = String.format("SELECT ip,port FROM t_%s LIMIT %d", hash_id, max_number)
        return mDBPoolManager.query(sql)
    }

    suspend fun updatePeer(peer_id: String, info_hash: String, ip: Int, port: Short, downloaded: Long, left: Long, uploaded: Long, event: Int):ResultSet {

        val sql = String.format("REPLACE INTO t_"
                + info_hash +
                "  (peer_id,ip,port,uploaded,downloaded,left,last_seen) VALUES ('%s',%d,%d,%d,%d,%d,%d)", peer_id, ip, port, downloaded, left, uploaded, event)
        return mDBPoolManager.query(sql)
    }

    suspend  fun removePeer(peer_id: String, info_hash: String)
    {
        val sql = "DELETE FROM t_$info_hash WHERE peer_id='$peer_id'";
        mDBPoolManager.execute(sql)
    }

    suspend  fun updatePeerHash(connect_id:Long, peer_id: String, info_hash: String)
    {
        val sql = "CREATE TABLE IF NOT EXISTS c_$connect_id (peer_id text(40), info_hash text(40),CONSTRAINT c1 UNIQUE (peer_id,info_hash) ON CONFLICT REPLACE)"
        mDBPoolManager.execute(sql)

        val sql2 = "REPLACE INTO c_$connect_id  (peer_id,info_hash) VALUES ('$peer_id', '$info_hash')"
        mDBPoolManager.execute(sql2)
    }

    suspend  fun getPeerHash(connect_id:Long):ResultSet {
        val sql = "SELECT peer_id,info_hash FROM c_$connect_id "
        return mDBPoolManager.query(sql)
    }

    suspend  fun clearPeerHash(connect_id: Long){
        val sql = "DROP TABLE c_$connect_id"
        mDBPoolManager.execute(sql)
    }

    suspend  fun removePeerHash(connect_id: Long, peer_id: String, info_hash: String)
    {
        val sql = "DELETE FROM c_$connect_id WHERE peer_id='$peer_id' and info_hash='$info_hash'";
        mDBPoolManager.execute(sql)
    }


    suspend fun addTorrent(hash_id: String) {
        //ipv4
        val sql = "CREATE TABLE IF NOT EXISTS t_" +
                hash_id +
                " (peer_id text(40)," +
                "ip INTEGER(4) ," +
                "port INTEGER(2)," +
                "uploaded INTEGER(8)," +
                "downloaded INTEGER(8)," +
                "left INTEGER(8)," +
                "last_seen INT DEFAULT 0," +
                "CONSTRAINT c1 UNIQUE (ip,port) ON CONFLICT REPLACE)"
        mDBPoolManager.execute(sql)

        val sql2 = String.format("insert into torrents (info_hash,created) select '%s','%d'" + "where not exists (SELECT 1 FROM torrents where info_hash = '%s')", hash_id, Date().time, hash_id)
        mDBPoolManager.execute(sql2);

        //ipv6
        //        sql = "CREATE TABLE IF NOT EXISTS t_v6_" +
        //                hash_id +
        //                " (peer_id text(40)," +
        //                "ip_highBits INTEGER(8)," +
        //                "ip_lowBits INTEGER(8)," +
        //                "port INTEGER(2)," +
        //                "uploaded INTEGER(8)," +
        //                "downloaded INTEGER(8)," +
        //                "left INTEGER(8)," +
        //                "last_seen INT DEFAULT 0" +
        //                ", CONSTRAINT c1 UNIQUE (ip_highBits, ip_lowBits,port) ON CONFLICT REPLACE)";
        //        mDBPoolManager.execute(sql, res -> {
        //            if (!res.succeeded()) {
        //                return;
        //            }
        //        });
    }
}
