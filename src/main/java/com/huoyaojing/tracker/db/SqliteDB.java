package com.huoyaojing.tracker.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.ResultSet;

import java.util.Date;

public class SqliteDB {
    DBPoolManager mDBPoolManager;

    public SqliteDB(Vertx vertx) {
        mDBPoolManager = new DBPoolManager(vertx);
        setup();
    }

    void setup() {
        String sql =
                "CREATE TABLE IF NOT EXISTS  stats (info_hash text(40) UNIQUE, " +
                        "completed INTEGER DEFAULT 0, " +
                        "leechers INTEGER DEFAULT 0, " +
                        "seeders INTEGER DEFAULT 0," +
                        "last_mod INTEGER DEFAULT 0 )";

        mDBPoolManager.execute(sql, res -> {
            if (!res.succeeded()) {
                return;
            }
        });

        sql = "CREATE TABLE IF NOT EXISTS torrents (" +
                "info_hash text(40) UNIQUE," +
                "created INTEGER" +
                ")";

        mDBPoolManager.execute(sql, res -> {
            if (!res.succeeded()) {
                return;
            }
        });
    }


    public void getTorrentInfo(String hashId, Handler<AsyncResult<ResultSet>> handler) {
        String sql = "SELECT seeders,leechers,completed FROM 'stats' WHERE info_hash='" + hashId + "'";
        mDBPoolManager.query(sql, handler);
    }

    public void getPeers(String hash_id, int max_number, Handler<AsyncResult<ResultSet>>  resultHandler) {
        String sql = String.format("SELECT ip,port FROM t_%s LIMIT %d", hash_id, max_number);
        mDBPoolManager.query(sql, resultHandler);
    }


    public void updatePeer(String peer_id, String info_hash, int ip, short port, long downloaded, long left, long uploaded, int event, Handler<AsyncResult<ResultSet>>  resultHandler) {
        addTorrent(info_hash, res -> {
                    if (!res.succeeded()) {
                        return;
                    }
                    if(ip == 0 || port == 0)
                    {
                        resultHandler.handle(new AsyncResult<ResultSet>(){
                            @Override
                            public ResultSet result() {
                                return null;
                            }

                            @Override
                            public Throwable cause() {
                                return null;
                            }

                            @Override
                            public boolean succeeded() {
                                return false;
                            }

                            @Override
                            public boolean failed() {
                                return true;
                            }
                        });
                        return;
                    }
                    String sql = String.format("REPLACE INTO t_"
                                    + info_hash +
                                    "  (peer_id,ip,port,uploaded,downloaded,left,last_seen) VALUES ('%s',%d,%d,%d,%d,%d,%d)"
                            , peer_id, ip, port, downloaded, left, uploaded, event);
                    mDBPoolManager.query(sql, resultHandler);
                }
        );
    }

    void removePeer() {

    }

    void addTorrent(String hash_id, Handler<AsyncResult<Void>> resultHandler) {
        //ipv4
        String sql = "CREATE TABLE IF NOT EXISTS t_" +
                hash_id +
                " (peer_id text(40)," +
                "ip INTEGER(4)," +
                "port INTEGER(2)," +
                "uploaded INTEGER(8)," +
                "downloaded INTEGER(8)," +
                "left INTEGER(8)," +
                "last_seen INT DEFAULT 0" +
                ", CONSTRAINT c1 UNIQUE (ip,port) ON CONFLICT REPLACE)";
        mDBPoolManager.execute(sql, res -> {
            resultHandler.handle(res);
            if (!res.succeeded()) {
                return;
            }
            //创建表
            String sql2 = String.format("insert into torrents (info_hash,created) select '%s','%d'" +
                    "where not exists (SELECT 1 FROM torrents where info_hash = '%s')", hash_id, new Date().getTime(), hash_id);
            mDBPoolManager.execute(sql2, res2 -> {
                if (!res2.succeeded()) {
                    return;
                }
            });
        });
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
