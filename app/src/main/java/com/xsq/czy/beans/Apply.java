package com.xsq.czy.beans;

/**
 * Created by Administrator on 2017/5/11.
 */
public class Apply {

    /**请求内容*/
    private String test;

    /**请求时间*/
    private String time;

    /**设备名称*/
    private String deviceName;

    /**设备蓝牙地址*/
    private String adress;

    /**工地*/
    private String dept;

    /**操作员名称*/
    private String memberName;

    /**设备工种*/
    private String deviceType;

    /**申请图片url*/
    private String photo;

    /**底图url*/
    private String origin;

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPhoto() {
        return photo;
    }

    public String getOrigin() {
        return origin;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTest() {
        return test;
    }

    public String getTime() {
        return time;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAdress() {
        return adress;
    }

    public String getDept() {
        return dept;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
