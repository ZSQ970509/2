package com.sofi.smartlocker.ble.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.sofi.smartlocker.ble.util.LOG;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sofi.smartlocker.ble.util.Code.RESTART_SCAN;

/**
 * Created by heyong on 2017/5/19.
 */

public abstract class BluetoothSearch {
    private final String TAG = BluetoothSearch.class.getSimpleName();
    protected static final int SCAN_START_TIME = 400;         //扫描开始时间
    protected static final int SCAN_RESTART_TIME = 30000;         //扫描重启时间
    protected int SCAN_OUT_TIME = 10000;         //扫描没响应的时间

    protected BluetoothAdapter btAdapter;
    protected int time = 0;                     //重启次数
    protected AtomicBoolean mScanning = new AtomicBoolean(false);     //是否在扫描蓝牙
    protected Timer timerScan;                   //扫描
    protected Timer timerScanRestart;           //扫描的过时重启
    protected Timer timerOut;                   //扫描的没响应重启
    protected ScanTimeRestartTask restartTask;    //重启任务
    protected ScanTimeOutTask outTask;             //没响应任务
    protected ScanCallbackListener onScanCallbackListener;

    protected void cancelScanRestartTime() {
        if (restartTask != null) {
            restartTask.cancel();
            restartTask = null;
        }
        if(timerScanRestart != null) {
            timerScanRestart.cancel();
            timerScanRestart = null;
        }
    }

    //每2分钟重启扫描
    protected void scanRestartTimeReset() {
        cancelScanRestartTime();
        timerScanRestart = new Timer();
        restartTask = new ScanTimeRestartTask();
        timerScanRestart.schedule(restartTask, SCAN_RESTART_TIME);
    }

    protected void cancelScanOutTime() {
        if (outTask != null) {
            outTask.cancel();
            outTask = null;
        }
        if(timerOut != null) {
            timerOut.cancel();
            timerOut = null;
        }
    }

    //每5秒，扫描没结果重启
    public void scanTimeOutReset() {
        cancelScanOutTime();
        timerOut = new Timer();
        outTask = new ScanTimeOutTask();
        if (timerOut != null) {
            timerOut.schedule(outTask, SCAN_OUT_TIME);
        }
    }

    public abstract void startSearch();

    public abstract void stopSearch();

    public abstract void close();

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setOnScanCallbackListener(ScanCallbackListener onScanCallbackListener) {
        this.onScanCallbackListener = onScanCallbackListener;
    }

    public interface ScanCallbackListener {
        void onBleData(BluetoothDevice device, String deviceName, String vol, int rssi, long time);
        void onScanError(int code);
    }

    private class ScanTimeRestartTask extends TimerTask {
        @Override
        public void run() {
            LOG.D(TAG, "scan restart : " + mScanning.get());
            if(mScanning.get()) {
                startSearch();
            }
        }
    }

    private class ScanTimeOutTask extends TimerTask {
        @Override
        public void run() {
            LOG.D(TAG, "scan restart : " + mScanning.get());
            if(mScanning.get()) {
                time++;

                if (onScanCallbackListener != null) {
                    onScanCallbackListener.onScanError(RESTART_SCAN);
                }
            }
        }
    }

}
