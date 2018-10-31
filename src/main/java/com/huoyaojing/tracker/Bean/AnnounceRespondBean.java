package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;

public class AnnounceRespondBean extends BeanBase{
    public int  interval = 60 * 10;  //生命值
    public int  leechers;  //正在下载
    public int  seeders;  //完成下载

    public List<Integer> IP_address = new ArrayList<Integer>();
    public List<Short> TCP_port = new ArrayList<Short>();



    @Override
    public Buffer getBuff(){
        Buffer buff = Buffer.buffer();
        buff.setInt(buff.length(), action);
        buff.setInt(buff.length(), transaction_id);

        buff.setInt(buff.length(), interval);
        buff.setInt(buff.length(), leechers);
        buff.setInt(buff.length(), seeders);

        for(int i = 0; i<IP_address.size(); ++i)
        {
            buff.setInt(buff.length(), IP_address.get(i));
            buff.setShort(buff.length(), TCP_port.get(i));
        }

        return buff;
    }
}
