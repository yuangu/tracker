package com.huoyaojing.tracker.config

import io.vertx.core.logging.LoggerFactory
import org.ini4j.Ini
import java.io.File




object Config {
    var port = 4848   //端口号
    var allow_iana_ips = false
    var announce_interval = 10
    var timeout_interval = 15
    var announce_max_peer_num = 30
    fun init() {
        val cfg = org.ini4j.Config()
        // 设置Section允许出现重复
        cfg.isMultiSection = true
        val ini = Ini()
        ini.config = cfg

        try {
            val file = File("udpt.ini")
            ini.load(file)

            val section = ini["tracker"]

            if (section?.containsKey("port")!!) {
                port = Integer.parseInt(section["port"])
            }

            if (section?.containsKey("announce_interval")) {
                announce_interval = Integer.parseInt(section["announce_interval"])
            }

            if (section?.containsKey("allow_iana_ips")) {
                allow_iana_ips = section["allow_iana_ips"].equals("yes")
            }

            if (section?.containsKey("timeout_interval")) {
                timeout_interval = Integer.parseInt(section["timeout_interval"])
            }

            if (section?.containsKey("announce_max_peer_num")) {
                announce_max_peer_num = Integer.parseInt(section["announce_max_peer_num"])
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val loger = LoggerFactory.getLogger(Config::class.java!!)
        loger.info(
                "\n=====================================================" +
                        "\nconfig:" +
                        "\nport:" + port.toString() +
                        "\nannounce_interval:" + announce_interval.toString() +
                        "\nallow_iana_ips:" + allow_iana_ips.toString() +
                        "\ntimeout_interval:" + timeout_interval.toString() +
                        "====================================================="
        )

    }
}
