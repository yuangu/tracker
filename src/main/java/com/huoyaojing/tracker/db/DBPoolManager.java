package com.huoyaojing.tracker.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

public class DBPoolManager {
    JDBCClient client;

    DBPoolManager(Vertx vertx) {
        client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:sqlite:data.db")
                .put("driver_class", "org.sqlite.JDBC")
                .put("max_pool_size", 30));
    }

    void execute(String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(res -> {
            if (!res.succeeded()) {
                return;
            }
            SQLConnection connection = res.result();
            connection.execute(sql, res2 -> {
                resultHandler.handle(res2);
            });
        });
    }

    void query(String sql, Handler<AsyncResult<ResultSet>>  resultHandler) {
        client.getConnection(res -> {
            if (!res.succeeded()) {
                return;
            }
            SQLConnection connection = res.result();
            connection.query(sql, res2 -> {
                resultHandler.handle(res2);
            });
        });
    }



}
