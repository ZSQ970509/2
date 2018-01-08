package com.xsq.czy.net;

import com.xsq.czy.beans.Device;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class GetDeviceListPackage extends BasePackage {

    /**设备名称列表*/
    private List<Device> deviceList;

    public void setNameList(List<Device> nameList) {
        this.deviceList = nameList;
    }

    public List<Device> getNameList() {
        return deviceList;
    }
}
