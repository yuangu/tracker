package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

public class AnnounceRequestBean extends BeanBase {
    public String info_hash; //20字节
    public String peer_id; //20字节
    public long downloaded;
    public long left;
    public long uploaded;
    public int event;        // 0: none; 1: completed; 2: started; 3: stopped
    public int IP_address;    // default
    public int key;
    public int num_want;      //-1 default
    public short port;

    @Override
    protected boolean initData(Buffer buff){
        if(buff.length() < 98)
        {
            return false;
        }
        info_hash = getHexString(20);
        peer_id = getHexString(20);
        downloaded = getLong();
        left= getLong();
        uploaded = getLong();
        event = getInt();
        IP_address  = getInt();
        key = getInt();
        num_want = getInt();
        port = getShort();
        return true;
    }
}
