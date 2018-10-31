package com.huoyaojing.tracker;

import com.huoyaojing.tracker.config.Config;
import com.huoyaojing.tracker.verticle.ServerVerticle;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args)
    {
        Config.init();



        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ServerVerticle.class.getName());
    }
}
