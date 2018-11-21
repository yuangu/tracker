package com.huoyaojing.tracker.bean

import io.vertx.core.net.SocketAddress


data class Connect(var connect:Long,
                   var addr: SocketAddress, var updateTime:Long){

}
