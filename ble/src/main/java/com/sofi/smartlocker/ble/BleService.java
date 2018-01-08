package com.sofi.smartlocker.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.sofi.smartlocker.ble.interfaces.IRemoteCallback;
import com.sofi.smartlocker.ble.interfaces.IRemoteService;
import com.sofi.smartlocker.ble.search.BluetoothSearch;
import com.sofi.smartlocker.ble.search.classic.BluetoothClassicSerach;
import com.sofi.smartlocker.ble.search.le.BluetoothLeSearch;
import com.sofi.smartlocker.ble.util.BleConfig;
import com.sofi.smartlocker.ble.util.CmdUtil;
import com.sofi.smartlocker.ble.util.Decoder;
import com.sofi.smartlocker.ble.util.LOG;
import com.sofi.smartlocker.ble.util.VerifyUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.sofi.smartlocker.ble.util.BleConfig.address;
import static com.sofi.smartlocker.ble.util.Code.RESTART_SCAN;

/**
 * Created by lan on 2016/7/21.
 */
public class BleService extends Service {
    private final String TAG = BleService.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice currentDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BleSend bleSend;
    private BluetoothSearch mBluetoothSearch;
    private BleStateReceiver mReceiver;

    private int searchType = 0;    //0是4.0扫描，1是3.0扫描
    private Handler mHandler;
    private ConnectTask connectTask;
    private ConnectTimeOutTask connectTimeOutTask;
    private ResetTask writeTimeOutTask;
    private BleGattCallback mLeGattCallback;
    private static final int CONNECT_DELAY = 500;    //延时连接
    private static final int CONNECT_TIME = 20000;  //连接超时
    private static final int WRITE_TIME = 20000;  //交互数据超时

    private AtomicBoolean init = new AtomicBoolean(false);   //是否已初始化蓝牙
    private AtomicBoolean searchFlag = new AtomicBoolean(false);   //是否开启扫描
    private AtomicBoolean isCB = new AtomicBoolean(false);//是否正在连接蓝牙
    private int cmd = -1; //当前执行的命令：看CmdUtil
    private long timeWrite = 0;   //最后一次写数据时间，用于判断超时
    private boolean closeFlag = false;   //是否是独立服务

    private BluetoothSearch.ScanCallbackListener scanCallbackListener = new BluetoothSearch.ScanCallbackListener() {
        @Override
        public void onBleData(BluetoothDevice device, String deviceName, String vol, int rssi, long time) {
            if (cmd == -1 || cmd == 99 ) {
                LOG.E(TAG, "address = " + device.getAddress());

                broadcastBleScanResult(deviceName, device.getAddress(), vol, rssi);
            }
        }

        @Override
        public void onScanError(int code) {
            if (code == RESTART_SCAN) {
                int time = mBluetoothSearch.getTime();

                if (time > 1) {   //两次无数据，蓝牙扫描切换模式
                    if (searchType == 0) {
                        searchType = 1;
                    }
                    else {
                        searchType = 0;
                    }
                    chooseSearch();
                }
            }
            else {
           //     broadcastBleScanError(StringUtils.parseScanCode(code));
            }
            searchBle();
        }
    };

    private BleGattCallback.GattCallbackListener gattCallbackListener = new BleGattCallback.GattCallbackListener() {
        @Override
        public void onConnect() {
            cancelConnectTimeOut();
            isCB.set(false);
            cmd = -1;

            if(bluetoothGatt != null) {
                bleSend.setBleGatt(bluetoothGatt);
            }
        }

        @Override
        public void onReConnect() {
            broadcastBleStatus(false, "");
            LOG.E(TAG, "currentDevice = " + currentDevice);
            if (currentDevice != null && init.get()) {
                isCB.set(false);
                connectBle(currentDevice.getAddress());
            }
        }

        @Override
        public void onClose() {
            cancelConnectTimeOut();
            isCB.set(false);
            cmd = -1;

            if(init.get()) {
                resumeCheck();
            }
        }

        @Override
        public void onServiceReady(BluetoothGattCharacteristic characteristic) {
            broadcastBleStatus(true, address);
            writeCharacteristic = characteristic;

            if(writeCharacteristic != null) {
                bleSend.setWriteCharacteristic(writeCharacteristic);
            }
        }

        @Override
        public void onServiceFail() {
            isCB.set(false);
            cmd = -1;
        }

        @Override
        public void onRead(byte[] pkg) {
            if(bleSend != null) {
                cmd = bleSend.getCmd();
            }

            if(VerifyUtil.isEmpty(pkg)) {
                return;
            }

            if(VerifyUtil.verifyPkg(pkg)){   //绑定
                byte[] data = VerifyUtil.verifyData(pkg);
                byte bCmd = VerifyUtil.verfiyCmd(pkg);
                LOG.E(TAG, "cmd:" + cmd + " bCmd:" + bCmd);

                if(bCmd == VerifyUtil.CMD_GET_BIKE) {
                    getBikeHandle(data);
                }
                else if(bCmd == VerifyUtil.CMD_GET_TAMPER) {
                    getTamperHandle(data);
                }
                else if((cmd == CmdUtil.CMD_OPEN_BIKE) && (bCmd == VerifyUtil.CMD_OPEN_BIKE)) {
                    openBikeHandle(data);
                }
                else if((cmd == CmdUtil.CMD_CLOSE_BIKE) && (bCmd == VerifyUtil.CMD_CLOSE_BIKE)) {
                    closeBikeHandle(data);
                }
                else {
                    broadcastCmdError(bCmd, VerifyUtil.DEFAULT_ERROR);
                }

                cmd = 99;
            }

        //    writeTime0ut(WRITE_TIME);
        }

        @Override
        public void onWrite(byte[] data) {
            if(bleSend != null) {
                cmd = bleSend.getCmd();
            }
        }
    };

    //处理请求租车数据
    private void getBikeHandle(byte[] data) {
        if(data != null && data.length == 1) {        //开锁数据成功
            int state = Decoder.ByteToInt(data[0]);
            broadcastGetBike(state);
        }
        else{
            LOG.E(TAG, "error:" + Decoder.byte2HexStr(data));
            broadcastCmdError(VerifyUtil.CMD_GET_BIKE, VerifyUtil.DEFAULT_ERROR);
        }
    }

    //处理开锁数据
    private void openBikeHandle(byte[] data) {
        if(data != null && data.length == 1 && data[0] == 0) {        //开锁数据成功
            broadcastCmdReply(VerifyUtil.CMD_OPEN_BIKE);
        }
        else{
            LOG.E(TAG, "error:" + Decoder.byte2HexStr(data));
            broadcastCmdError(VerifyUtil.CMD_OPEN_BIKE, VerifyUtil.DEFAULT_ERROR);
        }
    }

    //处理关锁数据
    private void closeBikeHandle(byte[] data) {
        if(data != null && data.length == 1 && data[0] == 0) {        //开锁数据成功
            broadcastCmdReply(VerifyUtil.CMD_CLOSE_BIKE);
        }
        else{
            LOG.E(TAG, "error:" + Decoder.byte2HexStr(data));
            broadcastCmdError(VerifyUtil.CMD_CLOSE_BIKE, VerifyUtil.DEFAULT_ERROR);
        }
    }

    //处理查询防拆数据
    private void getTamperHandle(byte[] data) {
        if(data != null && data.length == 1) {
            int state = Decoder.ByteToInt(data[0]);
            broadcastGetTamper(state);
        }
        else{
            LOG.E(TAG, "error:" + Decoder.byte2HexStr(data));
            broadcastCmdError(VerifyUtil.CMD_GET_TAMPER, VerifyUtil.DEFAULT_ERROR);
        }
    }

    private BleSend.SendErrorListener onSendError = new BleSend.SendErrorListener() {
        @Override
        public void sendError(int cmd) {
            broadcastCmdError(cmd, VerifyUtil.DEFAULT_SEND_ERROR);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.E(TAG, "onCreate");
        bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();

        chooseSearch();
        if (bleSend == null) {
            bleSend = new BleSend();
            bleSend.setOnSendListener(onSendError);
        }
        mHandler = new Handler();
        initReceiver();
    }

    private void chooseSearch() {
        if (mBluetoothSearch != null) {
            mBluetoothSearch.close();
            mBluetoothSearch = null;
        }

        if (searchType == 0) {
            mBluetoothSearch = new BluetoothLeSearch(btAdapter);
        }
        else {
            mBluetoothSearch = new BluetoothClassicSerach(this, btAdapter);
        }
        mBluetoothSearch.setOnScanCallbackListener(scanCallbackListener);
    }

    private void initReceiver() {
        mReceiver = new BleStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.E(TAG, "onStartCommand");

        return START_STICKY;
    }

    private void isBleFeature() {
        boolean isFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        broadcastBleFeature(isFeature);
    }

    private boolean isBleEnable() {
        if(btAdapter != null && btAdapter.isEnabled()) {
            init.set(true);
            return true;
        }
        else {
            return false;
        }
    }

    private void enableBle() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableBtIntent);
    }

    private void setScanMode(@ScanMode int mode) {
        if (mBluetoothSearch != null && mBluetoothSearch instanceof BluetoothLeSearch) {
            ((BluetoothLeSearch) mBluetoothSearch).setScanMode(mode);
        }
    }

    private void startBleScan() {
        if(btAdapter == null) {
            LOG.D(TAG, "btAdapter is null");
            return;
        }
        if (btAdapter.isEnabled() && btAdapter.getState() == BluetoothAdapter.STATE_ON) {
            LOG.D(TAG, "resumeCheck");
            closeBLE();
            initScanLEDevice();
        }
    }

    private void stopBleScan() {
        closeBLE();
    }

    private boolean isBleScaning() {
        return init.get() && searchFlag.get();
    }

    private void connectLock(String address) {
        BleConfig.address = address;
        connectBle(address);
    }

    private void disconnectLock() {
        closeBLE();
    }

    /**
     * 重启蓝牙，接收广播
     */
    private synchronized void resumeCheck() {
        if(btAdapter == null) {
            LOG.D(TAG, "btAdapter is null");
            return;
        }
        if (btAdapter.isEnabled() && btAdapter.getState() == BluetoothAdapter.STATE_ON) {
            LOG.D(TAG, "resumeCheck");
            closeBLE();

            if(closeFlag) {
                stopSelf();
            }
            else {
                initScanLEDevice();
            }
        }
    }

    private synchronized void closeBLE() {
        LOG.D(TAG, "#$#$#$#$#$:closeBLE");
        isCB.set(false);
        searchFlag.set(false);
        BleConfig.isConnected = false;
        address = "";
        broadcastBleStatus(false, "");

        stopScan();
        cancelConnectBle();
        cancelConnectTimeOut();
        cancelConnectTimeOut();
        cancelWriteTimeOut();

        if(mLeGattCallback != null) {
            mLeGattCallback.close();
            mLeGattCallback = null;
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        if(currentDevice != null)
            currentDevice = null;

        if(bleSend != null) {
            bleSend.clear();
        }

        cmd = -1;
    }

    /**
     * 开启蓝牙，接收广播
     */
    private void initScanLEDevice() {
        searchFlag.set(true);

        if (mBluetoothSearch != null) {
            mBluetoothSearch.setTime(0);
        }
        searchBle();
    }

    private void searchBle() {
        if (mBluetoothSearch != null) {
            mBluetoothSearch.startSearch();
        }
    }

    private void cancelConnectBle() {
        if (connectTask != null) {
            connectTask.cancel();
            mHandler.removeCallbacks(connectTask);
            connectTask = null;
        }
    }

    private void connectBle(String bleAddress) {
        cancelConnectBle();
        connectTask = new ConnectTask(bleAddress);
        mHandler.postDelayed(connectTask, CONNECT_DELAY);     //几秒后开始，每隔几秒
    }

    private void cancelConnectTimeOut() {
        if(connectTimeOutTask != null) {
            connectTimeOutTask.cancel();
            mHandler.removeCallbacks(connectTimeOutTask);
            connectTimeOutTask = null;
        }
    }

    //20s后扫描断开
    private void connectTimeOut() {
        cancelConnectTimeOut();
        connectTimeOutTask = new ConnectTimeOutTask();
        mHandler.postDelayed(connectTimeOutTask, CONNECT_TIME);
    }

    private void cancelWriteTimeOut() {
        if(writeTimeOutTask != null) {
            writeTimeOutTask.cancel();
            mHandler.removeCallbacks(writeTimeOutTask);
            writeTimeOutTask = null;
        }
    }

    //10s写超时自动断开
    private void writeTime0ut(long delay) {
        cancelWriteTimeOut();
        writeTimeOutTask = new ResetTask();
        mHandler.postDelayed(writeTimeOutTask, delay);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LOG.E(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LOG.E(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LOG.E(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    private void unregisterReceiver() {
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onDestroy() {
        LOG.E(TAG, "onDestroy");
        unregisterReceiver();
        closeBLE();
        closeFlag = true;
        mCallbacks.kill();

        if(mBluetoothSearch != null) {
            mBluetoothSearch.close();
            mBluetoothSearch = null;
        }
        if(mLeGattCallback != null) {
            mLeGattCallback.close();
            mLeGattCallback = null;
        }

        super.onDestroy();
    }

    private ReentrantLock lock = new ReentrantLock();
    //广播是否支持蓝牙4.0
    private void broadcastBleFeature(final boolean isFeature) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).bleSupportFeature(isFeature);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }
    //广播蓝牙状态
    private void broadcastBleScanResult(final String name, final String address, final String vol, final int rssi) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).bleScanResult(name, address, vol, rssi);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }
    //广播蓝牙连接状态
    private void broadcastBleStatus(final boolean status, final String address) {
        if(!lock.isLocked()) {
            lock.lock();
            try {
                final int N = mCallbacks.beginBroadcast();

                for (int i = 0; i < N; i++) {
                    try {
                        mCallbacks.getBroadcastItem(i).bleStatus(status, address);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallbacks.finishBroadcast();
            } finally {
                lock.unlock();
            }
        }
    }
    //广播数据返回错误结果
    private void broadcastCmdError(final int cmd, final String msg) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).bleCmdError(cmd, msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }
    //广播数据返回租车请求结果
    private void broadcastGetBike(final int state) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).getLockStatus(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }
    //广播数据返回查询防拆结果
    private void broadcastGetTamper(final int state) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).getLockTamper(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }
    //广播数据返回无数据返回的正确结果
    private void broadcastCmdReply(final int cmd) {
        lock.lock();
        try {
            final int N = mCallbacks.beginBroadcast();

            for (int i=0; i<N; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).bleCmdReply(cmd);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        } finally {
            lock.unlock();
        }
    }

    private final IRemoteService.Stub mBinder = new LocalBinder(this);

    private RemoteCallbackList<IRemoteCallback> mCallbacks = new RemoteCallbackList<>();
    public class LocalBinder extends IRemoteService.Stub {
        private final WeakReference<BleService> weakReference;

        LocalBinder(BleService bleService) {
            weakReference = new WeakReference<>(bleService);
        }

        @Override
        public boolean isBleEnable() throws RemoteException {
            if(weakReference == null) {
                return false;
            }
            return weakReference.get().isBleEnable();
        }

        @Override
        public void enableBle() throws RemoteException {
            if(weakReference != null) {
                weakReference.get().enableBle();
            }
        }

        @Override
        public void setHighMode() throws RemoteException {
            setScanMode(ScanMode.HIGH);
        }

        @Override
        public void setLowMode() throws RemoteException {
            setScanMode(ScanMode.LOW);
        }

        @Override
        public void startBleScan() throws RemoteException {
            if(weakReference != null) {
                weakReference.get().startBleScan();
            }
        }

        @Override
        public void stopBleScan() throws RemoteException {
            if(weakReference != null) {
                weakReference.get().stopBleScan();
            }
        }

        @Override
        public boolean isBleScaning() throws RemoteException {
            if(weakReference == null) {
                return false;
            }
            return weakReference.get().isBleScaning();
        }

        @Override
        public void connectLock(String address) throws RemoteException {
            if(weakReference != null) {
                weakReference.get().connectLock(address);
            }
        }

        @Override
        public void disconnectLock() throws RemoteException {
            if(weakReference != null) {
                weakReference.get().disconnectLock();
            }
        }

        @Override
        public void getLockStatus() throws RemoteException {
            if(weakReference != null) {
                if(weakReference.get().bleSend != null) {
                    weakReference.get().bleSend.getBike();
                }
            }

        }

        @Override
        public void openLock() throws RemoteException {
            if(weakReference != null) {
                if(weakReference.get().bleSend != null) {
                    weakReference.get().bleSend.openBike();
                }
            }
        }

        @Override
        public void closeLock() throws RemoteException {
            if(weakReference != null) {
                if(weakReference.get().bleSend != null) {
                    weakReference.get().bleSend.closeBike();
                }
            }
        }

        @Override
        public void getTamper() throws RemoteException {
            if(weakReference != null) {
                if(weakReference.get().bleSend != null) {
                    weakReference.get().bleSend.getTamper();
                }
            }
        }

        @Override
        public void registerCallback(IRemoteCallback callback) throws RemoteException {
            if(weakReference.get() == null) {
                return ;
            }
            boolean result = weakReference.get().mCallbacks.register(callback);
            if(result) {
                isBleFeature();
            }
        }

        @Override
        public void unregisterCallback(IRemoteCallback callback) throws RemoteException {
            if(weakReference.get() == null) {
                return ;
            }
            boolean result = weakReference.get().mCallbacks.unregister(callback);

            if(result) {
                int count = weakReference.get().mCallbacks.getRegisteredCallbackCount();

                if(count == 0) {
                    weakReference.get().closeBLE();
                }
            }
        }
    }

    private void stopScan() {
        if (mBluetoothSearch != null) {
            mBluetoothSearch.stopSearch();
        }
    }

    //间隔300毫秒执行蓝牙连接
    private class ConnectTask implements Runnable {
        private AtomicBoolean task = new AtomicBoolean(true);
        private String bleAddress;

        ConnectTask(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public void cancel() {
            task.set(false);
        }

        @Override
        public void run() {
            if (task.get()) {
                stopScan();

                if (isCB.compareAndSet(false, true)) {
                    if (btAdapter == null) {
                        return;
                    }
                    currentDevice = btAdapter.getRemoteDevice(bleAddress);
                    if (currentDevice != null) {
                        if (bluetoothGatt != null) {
                            bluetoothGatt.close();
                            bluetoothGatt = null;
                        }
                        if (mLeGattCallback != null) {
                            mLeGattCallback.close();
                            mLeGattCallback = null;
                        }
                        mLeGattCallback = new BleGattCallback();
                        mLeGattCallback.setOnGattCallbackListener(gattCallbackListener);
                        bluetoothGatt = currentDevice.connectGatt(BleService.this,
                                false, mLeGattCallback);

                      //  connectTimeOut();      //20秒后断开连接尝试
                        LOG.E(TAG, "address = " + currentDevice.getAddress());
                    } else {
                        resumeCheck();
                    }
                }
            }
        }
    }

    //连接超时任务
    private class ConnectTimeOutTask implements Runnable {
        private AtomicBoolean task = new AtomicBoolean(true);

        public void cancel() {
            task.set(false);
        }

        @Override
        public void run() {
            if (task.get()) {
                LOG.E(TAG, "connect timeout : " + isCB);
                if (isCB.get()) {
                    resumeCheck();
                    cmd = 99;
                }
            }
        }
    }
    //通话超时任务
    private class ResetTask implements Runnable {
        private AtomicBoolean task = new AtomicBoolean(true);

        public void cancel() {
            task.set(false);
        }

        @Override
        public void run() {
            if (task.get()) {
                LOG.E(TAG, "timout isConnected: " + BleConfig.isConnected);
                if (BleConfig.isConnected && timeWrite != 0) {
                    resumeCheck();
                    cmd = 99;
                }
            }
        }
    }

    private class BleStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                if(bleState == BluetoothAdapter.STATE_ON) {
                    LOG.E(TAG, "onReceive---------STATE_ON");
                    init.set(true);
                    initScanLEDevice();
                }
                else if(bleState == BluetoothAdapter.STATE_OFF) {
                    LOG.E(TAG, "onReceive---------STATE_OFF");
                    init.set(false);
                    closeBLE();
                }
            }
        }
    }

}
