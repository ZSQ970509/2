// IRemoteCallback.aidl
package com.sofi.smartlocker.ble.interfaces;

// Declare any non-default types here with import statements

interface IRemoteCallback {

     void bleSupportFeature(boolean isFeature);    //是否支持蓝牙4.0：true支持,false不支持

     void bleScanResult(String name, String address, String vol, int rssi);    //扫描到的蓝牙名字、地址、电压、信号强度

     void bleStatus(boolean status, String address);    //蓝牙连接状态：false关闭，true连接

     void bleCmdError(int cmd, String msg);    //读取锁状态、开锁、关锁命令失败回调，cmd参考CmdUtil, msg错误信息

     void getLockStatus(int state);    //读取锁状态成功的回调

     void getLockTamper(int state);    //读取锁防拆成功的回调

     void bleCmdReply(int cmd);    //开锁、关锁的成功回调

}
