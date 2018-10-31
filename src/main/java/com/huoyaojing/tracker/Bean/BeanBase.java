package com.huoyaojing.tracker.Bean;

import com.huoyaojing.tracker.Utils.HexUtils;
import io.vertx.core.buffer.Buffer;

import java.util.Arrays;

public class BeanBase {
    private int off = 0;
    private Buffer mBuff = null;

    public long connection_id;
    public int action;
    public int transaction_id;

    public int getOff(){return off;}

    public Buffer getBuff()
    {
        return null;
    }

    protected boolean initData(Buffer buff){
        return false;
    }

    public  boolean setBuff(long connection_id_in, int action_in, int transaction_id_in,  Buffer buff)
    {
        connection_id = connection_id_in;
        action = action_in;
        transaction_id = transaction_id_in;
        mBuff = buff;
        off = 16;
        return  initData(buff);
    }

    protected String getString(int len){
        String ret = mBuff.getString(off, off + len);
        off = off + len;
        return ret;
    }

    protected String getHexString(int len){
        String ret =  HexUtils.encode(mBuff.getBytes(off, off + len));
        off = off + len;
        return ret;

    }

    protected short getShort(){
        short ret = mBuff.getShort(off);
        off += 2;
        return   ret;
    }

    protected int getInt(){
        int  ret = mBuff.getInt(off);
        off += 4;
        return ret;
    }

    protected  long getLong(){
        Long ret = mBuff.getLong(off);
        off += 8;
        return ret;
    }


}
