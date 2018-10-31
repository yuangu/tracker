package com.huoyaojing.tracker;

import com.huoyaojing.tracker.config.Config;
import com.huoyaojing.tracker.verticle.ServerVerticle;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Log4j2LogDelegateFactory;
import io.vertx.core.logging.LoggerFactory;

public class Main {
    public static void main(String[] args)
    {
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, Log4j2LogDelegateFactory.class.getName());

        Config.init();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ServerVerticle.class.getName());
    }
}
