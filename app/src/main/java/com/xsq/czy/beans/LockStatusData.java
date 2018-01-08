package com.xsq.czy.beans;

/**
 * Created by chenyan on 2017/6/16.
 */

public class LockStatusData {
    private int state;

    public LockStatusData(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
