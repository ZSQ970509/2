package com.xsq.czy.net;

import com.xsq.czy.beans.BlueDevice;

import java.util.List;

public class AloginPackage extends BasePackage {

    /**用户id*/
    private String userId;

    /**用户类型*/
    private String type;

    /**设备密码是否已经存在（0：不存在，1：存在）*/
    private String devicePwd;

    /**蓝牙地址列表*/
    private List<BlueDevice> deviceAddress;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDevicePwd(String devicePwd) {
        this.devicePwd = devicePwd;
    }

    public void setDeviceAddress(List<BlueDevice> deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDevicePwd() {
        return devicePwd;
    }

    public String getUserId() {
        return userId;
    }

    public List<BlueDevice> getDeviceAddress() {
        return deviceAddress;
    }

    public String getType() {
        return type;
    }
}
