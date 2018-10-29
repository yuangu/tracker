package com.huoyaojing.tracker.Utils;



public class Utils {

    static public long genConnectionId  (String ipAddress, int port) {
        long ip  = IpUtils.iptolong(ipAddress)[0];
        long x;
        x = (ip ^ port);
        x <<= 16;
        x |= (~port);
        return x;
    }

    static public boolean verifyConnectionId(long cId, String ipAddress, int port){
        return  cId == genConnectionId  (ipAddress, port);
    }

}
