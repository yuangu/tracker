package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

public class ConnectRespondBean  extends BeanBase{
    public int  action;
    public int  transaction_id;
    public  long  connection_id;

    @Override
    public Buffer getBuff(){
        Buffer buff = Buffer.buffer();
        buff.setInt(buff.length(), action);
        buff.setInt(buff.length(), transaction_id);
        buff.setLong(buff.length(),connection_id);

        return buff;
    }
}
