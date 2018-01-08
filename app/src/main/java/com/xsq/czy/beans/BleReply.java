package com.xsq.czy.beans;

import com.sofi.smartlocker.ble.util.VerifyUtil;

/**
 * Created by lan on 2016/8/12.
 */
public class BleReply {
    private int cmd;

    public BleReply(int cmd) {
        this.cmd = cmd;
    }

    public boolean isOpenBike() {
        return cmd == VerifyUtil.CMD_OPEN_BIKE;
    }

    public boolean isCloseBike() {
        return cmd == VerifyUtil.CMD_CLOSE_BIKE;
    }

}
