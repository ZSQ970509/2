package com.sofi.smartlocker.ble.search.classic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sofi.smartlocker.ble.search.BluetoothSearch;
import com.sofi.smartlocker.ble.util.BleConfig;
import com.sofi.smartlocker.ble.util.Code;
import com.sofi.smartlocker.ble.util.LOG;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.sofi.smartlocker.ble.util.StringUtils.REG;

/**
 * Created by heyong on 2017/5/19.
 */

public class BluetoothClassicSerach extends BluetoothSearch {
    private final String TAG = BluetoothClassicSerach.class.getSimpleName();
    private Context mContext;
    private BluetoothSearchReceiver mReceiver;
    private ScanBleTask scanTask;             //扫描任务

    public BluetoothClassicSerach(Context context, BluetoothAdapter adapter) {
        mContext = context;
        this.btAdapter = adapter;
        SCAN_OUT_TIME = 8000;
    }

    @Override
    public void startSearch() {
        registerReceiver();

        closeScan();
        timerScan = new Timer();
        scanTask = new ScanBleTask();
        timerScan.schedule(scanTask, SCAN_START_TIME);
    }

    @Override
    public void stopSearch() {
        unregisterReceiver();

        closeScan();
    }

    private void closeScan() {
        cancelScanRestartTime();
        cancelScanOutTime();
        cancelScanTime();

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
    }

    @Override
    public void close() {
        stopSearch();
        onScanCallbackListener = null;
    }

    private void cancelScanTime() {
        if (scanTask != null) {
            scanTask.cancel();
            scanTask = null;
        }
        if(timerScan != null) {
            timerScan.cancel();
            timerScan = null;
        }
    }

    private void onBleScan(BluetoothDevice device, int rssi) {
        if (BleConfig.isConnected || !mScanning.get()) {
            return;
        }
        if (device.getName() == null || !device.getName().contains(REG)) {
            return;
        }
        cancelScanOutTime();
        LOG.E(TAG, "name = " + device.getName() + " address = " + device.getAddress() + " rssi: " +
                rssi + " isConnected: " + BleConfig.isConnected);

        String name = device.getName().replace(REG, "");
        String devicename = name.substring(0, 9);

        String Vol = "";
        if (name.length() > 13) {
            String vol = name.substring(10, 13);
            Integer iVolumn = Integer.parseInt(vol);//电压
            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            Vol = decimalFormat.format(iVolumn / 100F);//format 返回的是字符串
        }

        if(onScanCallbackListener != null) {
            onScanCallbackListener.onBleData(device, devicename, Vol, rssi, 0);
        }
    }

    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new BluetoothSearchReceiver();
            mContext.registerReceiver(mReceiver,
                    new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    //间隔200毫秒执行蓝牙搜索
    private class ScanBleTask extends TimerTask {
        @Override
        public void run() {
            LOG.D(TAG, "&&&&&&&&&&& btAdapter.startDiscovery");
            if (btAdapter != null) {
                boolean result = btAdapter.startDiscovery();

                if (!result) {
                    if (onScanCallbackListener != null) {
                        onScanCallbackListener.onScanError(Code.ERROR_SCAN);
                    }
                }

                scanTimeOutReset();      //5秒没结果就重启
            }
        }
    }

    private class BluetoothSearchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
                        Short.MIN_VALUE);

                onBleScan(device, rssi);
            }
        }
    };
}
