package com.sofi.smartlocker.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.SystemClock;

import com.sofi.smartlocker.ble.util.BleConfig;
import com.sofi.smartlocker.ble.util.Decoder;
import com.sofi.smartlocker.ble.util.LOG;

import java.util.List;
import java.util.UUID;

/**
 * Created by lan on 2016/5/24.
 */
public class BleGattCallback extends BluetoothGattCallback {
    private final String TAG = BleGattCallback.class.getSimpleName();
    private List<BluetoothGattCharacteristic> characterList;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService theService;
    private boolean firstConnect = false;
    private GattCallbackListener onGattCallbackListener;

    private static final String myUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String READ_UUID = "0000fff6-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_UUID = "0000fff6-0000-1000-8000-00805f9b34fb";

    //蓝牙连接变动
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        LOG.E(TAG, "gatt = " + gatt + " status = " + status + ", newstate = " + newState);
        if(status == BluetoothGatt.GATT_SUCCESS) {
            if (BluetoothProfile.STATE_CONNECTED == newState) {
                gatt.discoverServices();
                BleConfig.isConnected = true;
                firstConnect = true;

                if (onGattCallbackListener != null) {
                    onGattCallbackListener.onConnect();
                }
            } else if (BluetoothProfile.STATE_DISCONNECTED == newState) {
                if (gatt != null) {
                    gatt.disconnect();
                    gatt.close();
                }

                LOG.E(TAG, "isConnected = " + BleConfig.isConnected + " firstConnect = " + firstConnect);
                if(BleConfig.isConnected && firstConnect) {        //做一次重连
                    firstConnect = false;
                    SystemClock.sleep(100);

                    if (onGattCallbackListener != null) {
                        onGattCallbackListener.onReConnect();
                    }
                }
                else if(BleConfig.isConnected) {     //直接断开
                    if (onGattCallbackListener != null) {
                        onGattCallbackListener.onClose();
                    }
                }
                else if(!BleConfig.isConnected){      //正常关闭
                    firstConnect = false;

                    if (onGattCallbackListener != null) {
                        onGattCallbackListener.onClose();
                    }
                }
            }
        }
        else {
            if (gatt != null) {
                gatt.disconnect();
                gatt.close();
            }
            SystemClock.sleep(100);

            if (onGattCallbackListener != null) {
                onGattCallbackListener.onReConnect();
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        LOG.E(TAG, "onServicesDiscovered status :" + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // 获取SERVICE列表
            theService = gatt.getService(UUID.fromString(myUUID));

            if (theService != null) {
                LOG.E(TAG, "ServiceName:" + theService.getUuid());
                readCharacteristic = theService.getCharacteristic(UUID.fromString(READ_UUID));
                writeCharacteristic = theService.getCharacteristic(UUID.fromString(WRITE_UUID));

                if (writeCharacteristic != null) {
                    boolean rr = gatt.setCharacteristicNotification(writeCharacteristic, true);
                    LOG.E(TAG, "%%%%%%%%%%" + rr);
                    List<BluetoothGattDescriptor> llls = writeCharacteristic.getDescriptors();
                    BluetoothGattDescriptor descriptor = llls.get(0);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }
        }
        else {
            if (onGattCallbackListener != null) {
                onGattCallbackListener.onServiceFail();
            }
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        LOG.E(TAG, "onDescriptorWrite status :" + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (writeCharacteristic != null) {
                if (onGattCallbackListener != null) {
                    onGattCallbackListener.onServiceReady(writeCharacteristic);
                }
            }
        }
        else {
            if (onGattCallbackListener != null) {
                onGattCallbackListener.onServiceFail();
            }
        }
    }

    //数据变化
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        LOG.E(TAG, "onCharacteristicChanged");
        doRead(characteristic);
    }

    //读数据
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        LOG.E(TAG, "onCharacteristicRead status :" + status);
        doRead(characteristic);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        LOG.E(TAG, "onCharacteristicWrite status :" + status);
        byte[] data = characteristic.getValue();

        String msg = Decoder.byte2HexStr(data);
        LOG.E(TAG, "Write=======" + msg);

        SystemClock.sleep(20);

        if(onGattCallbackListener != null) {
            onGattCallbackListener.onWrite(data);
        }
    }

    private void doRead(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();

        if (data != null) {
            String msg = Decoder.byte2HexStr(data);
            LOG.E(TAG, "!!!!@@@@@@Read:" + msg);

            if(onGattCallbackListener != null) {
                onGattCallbackListener.onRead(data);
            }
        }
    }

    void close() {
        firstConnect = false;
        onGattCallbackListener = null;
        readCharacteristic = null;
        writeCharacteristic = null;

        if(characterList != null) {
            characterList.clear();
            characterList = null;
        }
        theService = null;
    }

    void setOnGattCallbackListener(GattCallbackListener onGattCallbackListener) {
        this.onGattCallbackListener = onGattCallbackListener;
    }

    interface GattCallbackListener {
        void onConnect();
        void onReConnect();
        void onClose();
        void onServiceReady(BluetoothGattCharacteristic characteristic);
        void onServiceFail();
        void onRead(byte[] data);
        void onWrite(byte[] data);
    }
}
