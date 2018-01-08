package com.sofi.smartlocker.ble;

import com.sofi.smartlocker.ble.util.CmdUtil;
import com.sofi.smartlocker.ble.util.Decoder;

import org.junit.Test;

/**
 * Created by lan on 2017/5/10.
 */

public class ExampleTest {

    @Test
    public void testCmd() throws Exception {
        byte[] pkg = CmdUtil.openBike();
        System.out.println(Decoder.byte2HexStr(pkg));
    }

}
