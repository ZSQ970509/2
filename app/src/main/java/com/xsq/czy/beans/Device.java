package com.xsq.czy.beans;

/**
 * Created by Administrator on 2017/5/11.
 */
public class Device {

    /**设备名称*/
    private String name;

    /**工地*/
    private String dept;

    /**操作员名称*/
    private String memberName;

    /**设备状态（0：开锁状态，1：关锁状态）*/
    private String state;

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }
}
