package com.sofi.smartlocker.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.sofi.smartlocker.ble.util.CmdUtil;
import com.sofi.smartlocker.ble.util.LOG;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lan on 2016/8/9.
 */
public class BleSend {
    private static final String TAG = BleSend.class.getSimpleName();
    private BluetoothGatt bleGatt;
    private BluetoothGattCharacteristic writeCharacteristic;
    private SendErrorListener onSendListener;
    private Timer timeOutTimer;
    private WriteTimeOutTask timeOutTask;

    private int cmd = -1;        //当前发送命令
    private static final long WRITE_TIME = 3000;

    BleSend() {
    }

    public BleSend(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic writeCharacteristic) {
        bleGatt = bluetoothGatt;
        this.writeCharacteristic = writeCharacteristic;
    }

    /**
     * 获取状态命令0x81
     */
    void getBike() {
        byte[] data = CmdUtil.getBike();

        if(data != null) {
            writeIn(CmdUtil.CMD_GET_BIKE, data);
        }
    }

    /**
     * 开锁命令0x82
     */
    void openBike() {
        byte[] data = CmdUtil.openBike();

        if(data != null) {
            writeIn(CmdUtil.CMD_OPEN_BIKE, data);
        }
    }

    /**
     * 关锁命令0x83
     */
    void closeBike() {
        byte[] data = CmdUtil.closeBike();

        if(data != null) {
            writeIn(CmdUtil.CMD_CLOSE_BIKE, data);
        }
    }

    /**
     * 查询锁防拆开关命令0x84
     */
    void getTamper() {
        byte[] data = CmdUtil.getTamper();

        if(data != null) {
            writeIn(CmdUtil.CMD_GET_TAMPER, data);
        }
    }

    /**
     * 写入数据
     *
     * @param type 命令类型
     * @param data 命令数据
     * @return
     */
    private void writeIn(int type, byte[] data) {
        writeTime0ut();
        cmd = type;
        LOG.E(TAG, "writeIn cmd:" + type);
        boolean result = false;

        if (bleGatt != null && writeCharacteristic != null) {
            result = writeCharacteristic.setValue(data);
            LOG.E(TAG, "writeIn:" + result);
            result = bleGatt.writeCharacteristic(writeCharacteristic);//isSetSuccess =
            LOG.E(TAG, "writeIn:" + result);
        }
        else {
            LOG.E(TAG, "writeIn null");
            cmd = -1;
        }
        cancelWriteTimeOut();

        if(!result) {         //发送失败
            if(onSendListener != null) {
                onSendListener.sendError(cmd);
            }
        }
    }

    void clear() {
        cmd = -1;
        bleGatt = null;
        writeCharacteristic = null;
    }

    void setBleGatt(BluetoothGatt bleGatt) {
        this.bleGatt = bleGatt;
    }

    void setWriteCharacteristic(BluetoothGattCharacteristic writeCharacteristic) {
        this.writeCharacteristic = writeCharacteristic;
    }

    int getCmd() {
        return cmd;
    }

    void setOnSendListener(SendErrorListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    interface SendErrorListener {
        void sendError(int cmd);
    }

    private void cancelWriteTimeOut() {
        if(timeOutTimer != null) {
            timeOutTimer.cancel();
            timeOutTimer = null;
        }
        if(timeOutTask != null) {
            timeOutTask.cancel();
            timeOutTask = null;
        }
    }

    //3s写超时自动断开
    private void writeTime0ut() {
        cancelWriteTimeOut();
        timeOutTimer = new Timer();
        timeOutTask = new WriteTimeOutTask();
        timeOutTimer.schedule(timeOutTask, WRITE_TIME);
    }

    //3秒执行蓝牙写异常
    private class WriteTimeOutTask extends TimerTask {
        @Override
        public void run() {
            LOG.E(TAG, "WriteTimeOutTask");
            bleGatt.disconnect();
        }
    }

}
