package com.huoyaojing.tracker;



import io.vertx.core.Vertx
import io.vertx.core.logging.Log4j2LogDelegateFactory
import io.vertx.core.logging.LoggerFactory
import com.huoyaojing.tracker.config.Config;
import com.huoyaojing.tracker.verticle.ServerVerticle

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, Log4j2LogDelegateFactory::class.java.name)

        Config.init()
        val vertx = Vertx.vertx()
        vertx.deployVerticle(ServerVerticle::class.java.name)
    }
}
