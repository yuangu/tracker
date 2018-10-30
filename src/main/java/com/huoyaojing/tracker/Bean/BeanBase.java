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
        byte[] bytes = mBuff.getBytes();
        String ret = new String(Arrays.copyOfRange(bytes, off, off + len));
        off = off + len;
        return ret;
    }

    protected String getHexString(int len){
        byte[] bytes = mBuff.getBytes();


        String ret =  HexUtils.encode(Arrays.copyOfRange(bytes, off, off + len));
        off = off + len;
        return ret;

    }

    protected short getShort(){
        byte[] bytes = mBuff.getBytes();
        int b2 = bytes[off + 0] & 0xFF;
        int b3 = bytes[off + 1] & 0xFF;
        off += 2;
        return  (short)((b2 << 8) | b3);
    }

    protected int getInt(){
        byte[] bytes = mBuff.getBytes();

        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        off += 4;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    protected  long getLong(){
        byte[] bytes = mBuff.getBytes();

        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (bytes[i + off] & 0xff);
        }
        off += 8;
        return values;
    }

    protected  static byte[] shortToByte2(short i)
    {
        byte[] targets = new byte[2];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    protected  static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * long整数转换为8字节的byte数组
     *
     * @param lo  long整数
     * @return byte数组
     */
    protected  static byte[] longToByte8(long lo) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((lo >>> offset) & 0xFF);
        }
        return targets;
    }

}
