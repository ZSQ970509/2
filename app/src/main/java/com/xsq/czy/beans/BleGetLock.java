package com.xsq.czy.beans;

/**
 * Created by lan on 2017/3/16.
 */

public class BleGetLock {
    private int state;

    public BleGetLock(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
