package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

public class ErrorRespondBean  extends BeanBase {
    public String errorStr = "";
    @Override
    public Buffer getBuff() {
        Buffer buff = Buffer.buffer();
        buff.setInt(buff.length(), action);
        buff.setInt(buff.length(), transaction_id);

        buff.setString(buff.length(), errorStr);

        return buff;
    }
}
