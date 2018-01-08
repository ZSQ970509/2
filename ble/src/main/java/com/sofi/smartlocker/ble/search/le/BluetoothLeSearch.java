package com.sofi.smartlocker.ble.search.le;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import com.sofi.smartlocker.ble.ScanMode;
import com.sofi.smartlocker.ble.search.BluetoothSearch;
import com.sofi.smartlocker.ble.search.ScanCallback;
import com.sofi.smartlocker.ble.search.ScanFilter;
import com.sofi.smartlocker.ble.search.ScanRecord;
import com.sofi.smartlocker.ble.search.ScanResult;
import com.sofi.smartlocker.ble.search.ScanSettings;
import com.sofi.smartlocker.ble.util.BleConfig;
import com.sofi.smartlocker.ble.util.LOG;
import com.sofi.smartlocker.ble.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;
import static com.sofi.smartlocker.ble.ScanMode.HIGH;
import static com.sofi.smartlocker.ble.ScanMode.LOW;
import static com.sofi.smartlocker.ble.util.StringUtils.REG;

/**
 * Created by lan on 2016/5/25.
 */
public class BluetoothLeSearch extends BluetoothSearch {
    private final String TAG = BluetoothLeSearch.class.getSimpleName();
    private static final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final UUID[] serviceUuids = new UUID[]{UUID.fromString(serviceUUID)};

    private BluetoothLeScannerCompat scannerCompat;
    private ScanBleTask scanTask;             //扫描任务
    private BleScanCallback mScanCallback;
    private @ScanMode int scanMode = LOW;    //0是省电模式，1是高速模式

    public BluetoothLeSearch(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
        scannerCompat = BluetoothLeScannerCompat.getScanner();
    }

    @Override
    public void startSearch() {
        stopSearch();
        timerScan = new Timer();
        scanTask = new ScanBleTask();
        timerScan.schedule(scanTask, SCAN_START_TIME);
    }

    @Override
    public void stopSearch() {
        cancelScanRestartTime();
        cancelScanOutTime();
        cancelScanTime();
        closeScan();
    }

    private void closeScan() {
        if(mScanning.compareAndSet(true, false)) {
            scannerCompat.stopScan(mScanCallback);
        }
    }

    @Override
    public void close() {
        stopSearch();
        onScanCallbackListener = null;
        mScanCallback = null;
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

    private void onBleScan(BluetoothDevice device, String deviceName, int rssi, long time) {
        if (BleConfig.isConnected || !mScanning.get()) {
            return;
        }
        if (device.getName() == null || !device.getName().contains(REG)) {
            return;
        }
        cancelScanOutTime();
        LOG.E(TAG, "name = " + deviceName + " address = " + device.getAddress() + " rssi: " +
                rssi + " isConnected: " + BleConfig.isConnected);

        String devicename = StringUtils.getBikeName(deviceName);
        String Vol = StringUtils.getBikeVol(deviceName);

        if(onScanCallbackListener != null) {
            onScanCallbackListener.onBleData(device, devicename, Vol, rssi, time);
        }
    }

    public void setScanMode(int scanMode) {
        this.scanMode = scanMode;
    }

    private class BleScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            long newTime = System.currentTimeMillis();
            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi();
            ScanRecord scanRecord = result.getScanRecord();

            String deviceName = "";
            if (scanRecord != null) {
                deviceName = scanRecord.getDeviceName();
            }

            onBleScan(device, deviceName, rssi, newTime);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            if(onScanCallbackListener != null) {
                onScanCallbackListener.onScanError(errorCode);
            }
        }

    }

    @TargetApi(21)
    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
        if (serviceUuids != null && serviceUuids.length > 0) {
            for (UUID uuid : serviceUuids) {
                ScanFilter filter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(uuid)).build();
                scanFilters.add(filter);
            }
        }
        return scanFilters;
    }

    @TargetApi(21)
    private ScanSettings getHighScanSetting() {
        return new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build();
    }

    @TargetApi(21)
    private ScanSettings getLowScanSetting() {
        return new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_POWER).build();
    }

    //间隔200毫秒执行蓝牙搜索
    private class ScanBleTask extends TimerTask {
        @Override
        public void run() {
            if (btAdapter != null) {
                mScanning.set(true);

                if (mScanCallback == null) {
                    mScanCallback = new BleScanCallback();
                }
                ScanSettings scanSettings = null;

                switch (scanMode) {
                    case LOW:
                        scanSettings = getLowScanSetting();
                        break;
                    case HIGH:
                        scanSettings = getHighScanSetting();
                        break;
                    default:
                        scanSettings = getLowScanSetting();
                        break;
                }
                scannerCompat.startScan(getScanFilters(), scanSettings,
                        mScanCallback);

                scanTimeOutReset();  //5秒没结果就重启
            }
        }
    }

}
