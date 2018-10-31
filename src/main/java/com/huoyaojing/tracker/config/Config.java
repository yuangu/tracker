package com.huoyaojing.tracker.config;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;


public class Config {
    public static int port = 4848;   //端口号
    public static boolean allow_iana_ips = false;
    public static int announce_interval= 10;
    public static int timeout_interval = 15;

    public static void init()
    {
        org.ini4j.Config cfg = new org.ini4j.Config();
        // 设置Section允许出现重复
        cfg.setMultiSection(true);
        Ini ini = new Ini();
        ini.setConfig(cfg);

        try {
            File file = new File("udpt.ini");
            ini.load(file);

            Section section = ini.get("tracker");
            if(section.containsKey("port"))
            {
                port = Integer.parseInt(section.get("port"));
            }

            if(section.containsKey("announce_interval"))
            {
                announce_interval = Integer.parseInt(section.get("announce_interval"));
            }

            if(section.containsKey("allow_iana_ips"))
            {
                allow_iana_ips  = section.get("allow_iana_ips").equals("yes");
            }

            if(section.containsKey("timeout_interval"))
            {
                timeout_interval = Integer.parseInt(section.get("timeout_interval"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
