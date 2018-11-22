package com.huoyaojing.tracker.db

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLConnection
import io.vertx.kotlin.coroutines.awaitResult

class DBPoolManager internal constructor(vertx: Vertx) {
    val loger = LoggerFactory.getLogger(DBPoolManager::class.java!!)

    internal var client: JDBCClient

    init {
        client = JDBCClient.createShared(vertx, JsonObject()
                .put("url", "jdbc:sqlite:data.db")
                .put("driver_class", "org.sqlite.JDBC")
                .put("max_pool_size", 30))
    }

    suspend fun execute(sql: String) {
        var connect = awaitResult<SQLConnection>{
            client.getConnection(it);
        }

        try {
            awaitResult<Void>{connect.execute(sql, it);}
        }catch (e:Exception){
            loger.error("run sql:(" + sql + ")  error:" + e.toString())
        }
        awaitResult<Void>{connect.close(it)}
    }

    suspend  fun query(sql: String): ResultSet {
        var connect = awaitResult<SQLConnection>{
            client.getConnection(it);
        }
        var ret: ResultSet? = null;
        try {
            ret = awaitResult<ResultSet>{connect.query(sql, it);}
        }catch (e:Exception){

            loger.error("run sql:(" + sql + ")  error:" + e.toString())
        }
        awaitResult<Void>{connect.close(it)}
        if(ret == null)
        {
            ret = ResultSet()
        }
        return ret!!;
    }
}
