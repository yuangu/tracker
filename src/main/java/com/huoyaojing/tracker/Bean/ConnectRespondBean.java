package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

public class ConnectRespondBean  extends BeanBase{
    public int  action;
    public int  transaction_id;
    public  long  connection_id;

    @Override
    protected boolean initData(Buffer buff){
        buff.setBytes(buff.length(), intToByte4(action));
        buff.setBytes(buff.length(), intToByte4(transaction_id));
        buff.setBytes(buff.length(), longToByte8(connection_id));
        return true;
    }
}
