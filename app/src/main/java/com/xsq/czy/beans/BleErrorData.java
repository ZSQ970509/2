package com.xsq.czy.beans;

import com.sofi.smartlocker.ble.util.VerifyUtil;

/**
 * Created by lan on 2016/8/12.
 */
public class BleErrorData {
    private int cmd;
    private String msg;

    public BleErrorData(int cmd, String msg) {
        this.cmd = cmd;
        this.msg = msg;
    }

    public boolean isGetBike() {
        return cmd == VerifyUtil.CMD_GET_BIKE;
    }

    public boolean isOpenBike() {
        return cmd == VerifyUtil.CMD_OPEN_BIKE;
    }

    public boolean isCloseBike() {
        return cmd == VerifyUtil.CMD_CLOSE_BIKE;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
