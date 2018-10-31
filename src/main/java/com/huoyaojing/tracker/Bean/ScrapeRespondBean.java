package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;

public class ScrapeRespondBean extends BeanBase {
    public List<Integer> seeders = new ArrayList<Integer>();
    public List<Integer> completed = new ArrayList<Integer>();
    public List<Integer> leechers = new ArrayList<Integer>();

    @Override
    public Buffer getBuff() {
        Buffer buff = Buffer.buffer();
        buff.setInt(buff.length(), action);
        buff.setInt(buff.length(), transaction_id);

        for (int i = 0; i < seeders.size(); ++i) {
            buff.setInt(buff.length(), seeders.get(i));
            buff.setInt(buff.length(), completed.get(i));
            buff.setInt(buff.length(), leechers.get(i));
        }

        return buff;
    }
}