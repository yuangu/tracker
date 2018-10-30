package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import sun.net.util.IPAddressUtil;

import java.util.ArrayList;
import java.util.List;

public class AnnounceRespondBean extends BeanBase{
    public int  interval;  //生命值
    public int  leechers;  //正在下载
    public int  seeders;  //完成下载

    public List<Integer> IP_address = new ArrayList<Integer>();
    public List<Short> TCP_port = new ArrayList<Short>();



    @Override
    public Buffer getBuff(){
        Buffer buff = Buffer.buffer();
        buff.setBytes(buff.length(), intToByte4(action));
        buff.setBytes(buff.length(), intToByte4(transaction_id));
        buff.setBytes(buff.length(), intToByte4(interval));
        buff.setBytes(buff.length(), intToByte4(leechers));
        buff.setBytes(buff.length(), intToByte4(seeders));

        for(int i = 0; i<IP_address.size(); ++i)
        {
            buff.setInt(buff.length(), IP_address.get(i));
            buff.setShort(buff.length(), TCP_port.get(i));
        }

        return buff;
    }
}
