package com.huoyaojing.tracker.test;

import com.huoyaojing.tracker.Utils;
import org.junit.Test;

public class UtilsTest {
    @Test
    public void genCiDTest()
    {
       System.out.print(Utils.genConnectionId (100, 20));
    }

}
