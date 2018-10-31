package com.huoyaojing.tracker.Bean;

import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;

public class ScrapeRequestBean  extends BeanBase {
    public List<String> hashIdList = new ArrayList<String>();

    @Override
    protected boolean initData(Buffer buff){
        if(buff.length() < 98)
        {
            return false;
        }
        if(buff.length() - getOff() >= 20)
        {
            hashIdList.add(getHexString(20));
        }
        return true;
    }
}
