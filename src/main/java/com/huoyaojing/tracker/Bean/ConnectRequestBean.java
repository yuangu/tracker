package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

public class ConnectRequestBean extends BeanBase {
    public long protocol_id;
    public int action;
    public int transaction_id;

    @Override
    protected boolean initData(Buffer buff){
        if(buff.getBytes().length < 16)
        {
            return false;
        }
        protocol_id = getLong();
        action = getInt();
        transaction_id = getInt();
        return true;
    }


}
