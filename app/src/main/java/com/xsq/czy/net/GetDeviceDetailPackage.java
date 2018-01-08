package com.xsq.czy.net;

/**
 * Created by Administrator on 2017/6/13.
 */
public class GetDeviceDetailPackage extends BasePackage {

    /**设备编码*/
    private String deviceName;

    /**工地名称*/
    private String project;

    /**位置*/
    private String position;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
