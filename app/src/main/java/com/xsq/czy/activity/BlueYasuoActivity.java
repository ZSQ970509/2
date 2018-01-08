package com.xsq.czy.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.luopingelec.permission.PermissionsManager;
import com.luopingelec.permission.PermissionsResultAction;
import com.sofi.smartlocker.ble.BleService;
import com.sofi.smartlocker.ble.interfaces.IRemoteCallback;
import com.sofi.smartlocker.ble.interfaces.IRemoteService;
import com.sofi.smartlocker.ble.util.LOG;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.beans.BleErrorData;
import com.xsq.czy.beans.BleGetLock;
import com.xsq.czy.beans.BleReply;
import com.xsq.czy.beans.BleScanResult;
import com.xsq.czy.beans.LockStatusData;
import com.xsq.czy.net.ApplyOpertaionPackage;
import com.xsq.czy.net.CheckStatePackage;
import com.xsq.czy.net.GetDeviceDetailPackage;
import com.xsq.czy.net.LockOperationPackage;
import com.xsq.czy.network.NetworkApi;
import com.xsq.czy.state.ApplicationState;
import com.xsq.czy.state.BaseState;
import com.xsq.czy.util.Constants;
import com.xsq.czy.util.Globals;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;
import com.xsq.czy.util.UIHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sofi.smartlocker.ble.util.BleConfig.address;

public class BlueYasuoActivity extends Activity implements View.OnClickListener {

    /**蓝牙锁状态*/
    private TextView lockTV;

    /**蓝牙链接状态*/
    private TextView stateTV;

    /**开锁按钮*/
    private TextView openBtn;

    /**关锁按钮*/
    private TextView offBtn;

    /**请求操作按钮*/
    private Button requestBtn;

    private ImageView backBtn;

    private String state = "";

    private String type = "";

    private final String TAG = BlueYasuoActivity.class.getSimpleName();

    private Toolbar mToolbar;

    private String recondData;

    private String blueToothName;

    private static final int CODE_SCAN = 1;

    private static final int CODE_CONNECT = 2;

    private BleHandler mHandler;

    private BaseState mState;

    private AtomicBoolean connect = new AtomicBoolean(false); // 是否已经连接

    private static String deviceAddress = "";

    /**设备编号*/
    private TextView numberTv;
    private String deviceId;
    /**工地*/
    private TextView siteTv;

    /**作业区*/
    private TextView areaTv;

    private static Boolean isFirst = true;

    private boolean isBind;//设备是否绑定
    private  String[] playList = new String[100];
    private  BleScanResult[] bleList = new BleScanResult[100];
    private int i = 0;
    private AlertDialog mAlertDialog = null;
    private BleScanResult bleScanResult;
    private int witch = 0;
    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable,1000);
            if (!Globals.isBleConnected) {
                lockTV.setText("当前状态:蓝牙未链接");
                stateTV.setText("当前状态:蓝牙未链接");
            }else {
//                Log.i(TAG, deviceAddress);
                lockTV.setText("当前状态:蓝牙已链接");
                stateTV.setText("当前状态:蓝牙已链接");
            }
        }
    };

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LOG.E(TAG, "onServiceConnected");
            try {
                Constants.bleService = IRemoteService.Stub.asInterface(service);
                Constants.bleService.setHighMode();
                //出册蓝牙状态回调，通过Handler发送状态改变蓝牙连接状态，和开锁状态
                Constants.bleService.registerCallback(mCallback);
            }catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        public void connectDevice(BleScanResult bleScanResult) {
            if (Constants.bleService != null && bleScanResult != null) {
                Globals.BLE_NAME = bleScanResult.getName();
                try {
                    Constants.bleService.connectLock(bleScanResult.getAddress());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Integer state = Integer.valueOf(CODE_CONNECT);
                sendMsg(state);
            }

        }
        public void onServiceDisconnected(ComponentName name) {
            LOG.E(TAG, "onServiceDisconnected");
            try {
                if (Constants.bleService != null) {
                    Constants.bleService.unregisterCallback(mCallback);
                    Constants.bleService = null;
                }
            }catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    IRemoteCallback.Stub mCallback = new IRemoteCallback.Stub() {
        @Override
        public void bleSupportFeature(boolean isFeature) throws RemoteException {
            Globals.isBleFeature = isFeature;
            showBleTipDialog();
        }

        @Override
        public void bleScanResult(String name, String address, String vol, int rssi) throws RemoteException {
            LOG.E(TAG, "bleScanResult name:" + name + " address:" + address + " vol:" + vol + " rssi:" + rssi);
            blueToothName = name;
            //deviceAddress = address;


            sendMsg(Integer.valueOf(CODE_SCAN));
            if (rssi < -76) {
                return;
            }
            BleScanResult bleScanResult = new BleScanResult(name, address, vol, rssi);
            sendMsg(bleScanResult);

        }

        @Override
        public void bleStatus(boolean status, String address) throws RemoteException {
            LOG.E(TAG, "bleStatus :" + status);
            connect.set(false);
            Globals.isBleConnected = status;
            if(status) {
                Globals.BLE_ADDRESS = address;
            }else {
                Globals.BLE_NAME = "";
            }
            sendMsg(status);
        }

        @Override
        public void bleCmdError(int cmd, String msg) throws RemoteException {
            LOG.E(TAG, "bleCmdError :" + cmd + " msg:" + msg);
            BleErrorData errorData = new BleErrorData(cmd, msg);
            sendMsg(errorData);
        }

        @Override
        public void getLockStatus(int state) throws RemoteException {
            LOG.E(TAG, "getLockStatus state :" + state);
            BleGetLock bleGetLock = new BleGetLock(state);
            sendMsg(bleGetLock);
        }

        @Override
        public void getLockTamper(int state) throws RemoteException {

        }

        @Override
        public void bleCmdReply(int cmd) throws RemoteException {
            LOG.E(TAG, "bleCmdReply :" + cmd);
            BleReply bleReply = new BleReply(cmd);
            sendMsg(bleReply);
        }
    };

    private class BleHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Object data = msg.obj;
            if (data != null || mState != null) {
                mState.notifyBleCallback(data);
            }
        }
    }

    private synchronized void sendMsg(final Object data) {
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    private void showBleTipDialog() {
        try {
            if(Globals.BLE_INIT && Globals.isBleFeature) {
                if(!Constants.bleService.isBleEnable()){
                    UIHelper.showBleDialog(this, R.string.ble_tip, bleListener, bleNeverListener);
                }else if (!Constants.bleService.isBleScaning()) {
                    Constants.bleService.startBleScan();
                }
            }else if(Globals.BLE_INIT){
                UIHelper.showAlertDialog(this, R.string.ble_feature_tip);
            }
        }catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 蓝牙提示监听
     */
    DialogInterface.OnClickListener bleListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            dialog.cancel();
        }
    };

    /**
     * 蓝牙提示不再提醒
     */
    DialogInterface.OnClickListener bleNeverListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Globals.BLE_INIT = false;
            dialog.cancel();
        }
    };
    public void connectDevice(BleScanResult bleScanResult) {
        if (Constants.bleService != null && bleScanResult != null) {
            Globals.BLE_NAME = bleScanResult.getName();
            try {
                // Constants.bleService.connectLock(bleScanResult.getAddress());
                address = bleScanResult.getAddress();
                deviceAddress = "";
                if (connect.compareAndSet(false, true)) {
                    if (Constants.bleService != null) {

                        blueToothName = bleScanResult.getName();
                        deviceAddress= address;
                        Constants.bleService.connectLock(address);
                        GetDeviceDetail();
                        Integer state = Integer.valueOf(CODE_CONNECT);
                        sendMsg(state);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!(deviceAddress == null || "".equals(deviceAddress))) {
            // GetDeviceDetail();
        }
        if(Constants.bleService == null) {
            requestLocationPermission();
        }else{
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showBleTipDialog();
                        }
                    });
                }
            },1000);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_blue);

        initData();
        initView();
    }

    private void initData() {
        mHandler = new BleHandler();
        Bus mBus = new Bus();
        mState = new ApplicationState(mBus);
        mState.registerEvent(this);
    }

    private void initView() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        lockTV = (TextView) findViewById(R.id.activity_blue_lock_state);
        stateTV = (TextView) findViewById(R.id.activity_blue_state);
        numberTv = (TextView) findViewById(R.id.activity_blue_number);
        areaTv = (TextView) findViewById(R.id.activity_blue_area);
        siteTv = (TextView) findViewById(R.id.activity_blue_site);
        openBtn = (TextView) findViewById(R.id.activity_blue_open_btn);
        openBtn.setOnClickListener(this);
        offBtn = (TextView) findViewById(R.id.activity_blue_off_btn);
        offBtn.setOnClickListener(this);
        requestBtn = (Button) findViewById(R.id.activity_blue_request_btn);
      //  requestBtn.setOnClickListener(this);
        backBtn = (ImageView) findViewById(R.id.activity_blue_back_btn);
        backBtn.setOnClickListener(this);
        handler.postDelayed(runnable,1000);

        numberTv.setText("设备编号:"  );
        siteTv.setText("工地:"  );
        areaTv.setText("作业区:"  );
        deviceAddress = "";
        showDialog();
    }

    @Override
    public void onClick(View view) {

        if (view == openBtn) {
            if (!judgeBluConn()) return;
            if(!isBind){
                ToastUtil.show(getApplicationContext(), "该设备未绑定");
                return;
            }
            try {
                if (Constants.bleService != null) {
                    Constants.bleService.openLock();
                }
            }catch (RemoteException e) {
                e.printStackTrace();
            }

            state = "1";

            LockOperation("0","true");
        }else if (view == offBtn) {
            if (!judgeBluConn()) return;
            try {
                if (Constants.bleService != null) {
                    Constants.bleService.closeLock();
                }
            }catch (RemoteException e) {
                e.printStackTrace();
            }
            state = "0";
//            BulidLock();
            LockOperation("1","true");
        }else if (view == requestBtn) {
            if (!judgeBluConn()) return;
            ApplyOpertaion();
        }else if (view == backBtn) {
            finish();
        }
    }
    /**判断蓝牙是否已经连接*/
    private boolean judgeBluConn() {
        if (!Globals.isBleConnected) {
            UIHelper.showAlertDialog(this, R.string.lock_connect);
            lockTV.setText("当前状态:蓝牙未链接");
            stateTV.setText("当前状态:蓝牙未链接");
            return false;
        }
        return true;
    }

    private void requestLocationPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        initService();
                    }

                    @Override
                    public void onDenied(String permission) {
                        String message = String.format(Locale.getDefault(), getString(R.string.message_denied), permission);
                        UIHelper.showToast(BlueYasuoActivity.this, message);
                    }
                });
    }

    private void initService() {
        Intent intent = new Intent(this, BleService.class);
        startService(intent);
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }
    private void stopService() {
        try {
            if(Constants.bleService != null) {
                Constants.bleService.unregisterCallback(mCallback);
                Constants.bleService = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(mConn != null) {
            unbindService(mConn);
        }
    }
    private void refreshConnect(boolean connect) {
        if (connect) {
            lockTV.setText(R.string.ble_connected);
        }else {
            lockTV.setText(R.string.ble_disconnect);

        }
    }

    private void refreshState(boolean state) {
        if (state) {
            stateTV.setText(R.string.ble_connected);
        }else {
            stateTV.setText(R.string.ble_disconnect);

        }
    }

    /**
     * 4.2.6	获取设备详细信息
     */
    private void GetDeviceDetail() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bleMac",deviceAddress);
        //ToastUtil.show(getApplicationContext(),"fffffffff:"+deviceAddress);
        HttpUtil.executeRequestForJsonResultEx("GetDeviceDetail", param,
                new HttpUtil.ResultEventJson<GetDeviceDetailPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(getApplicationContext(),"网络异常");
                    }

                    @Override
                    public void onUIRoutine(GetDeviceDetailPackage resultObj) {
                        if (resultObj.getResult() == 200) {

                            numberTv.setText("设备编号:" + resultObj.getDeviceName());
                            deviceId = resultObj.getDeviceName();
                            siteTv.setText("工地:" + resultObj.getProject() );

                            areaTv.setText("作业区:" + resultObj.getPosition());

                            if(!TextUtils.isEmpty(resultObj.getProject())){
                                isBind = true;
                            }else{
                                isBind = false;
                            }
                        }else {
                            isBind = false;
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(getApplicationContext(), "获取详细信息失败失败");
                            }else {
                                ToastUtil.show(getApplicationContext(), resultObj.getDescript());
                            }
                            numberTv.setText("设备编号:设备未绑定工地");
                            siteTv.setText("工地:设备未绑定工地");
                            areaTv.setText("作业区:设备未绑定工地");
                        }
                    }
                });
    }

    /**
     * 4.2.4	申请操作
     */
    private void ApplyOpertaion() {
        String userId = SharedPreferencesUtils.getString(BlueYasuoActivity.this, Resource.USERID,null);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId",userId);
        param.put("deviceId",deviceId);
        param.put("operation",state);
        HttpUtil.executeRequestForJsonResultEx("ApplyOpertaion", param,
                new HttpUtil.ResultEventJson<ApplyOpertaionPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(getApplicationContext(), "网络异常");
                    }

                    @Override
                    public void onUIRoutine(ApplyOpertaionPackage resultObj) {
                        if (resultObj.getResult() == 200) {

                            ToastUtil.show(BlueYasuoActivity.this,"请求操作成功");
                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(getApplicationContext(), "请求操作失败");
                            }else {
                                ToastUtil.show(getApplicationContext(), resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    /**
     * 4.2.3	上传状态
     */
    private void BulidLock() {
        // TODO Auto-generated method stub
        String userId = SharedPreferencesUtils.getString(BlueYasuoActivity.this, Resource.USERID,null);
      //  String deviceId = SharedPreferencesUtils.getString(BlueYasuoActivity.this,Resource.DEVICEID,null);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("deviceAddress",deviceAddress);
        param.put("userId",userId);
        param.put("state", state);
        HttpUtil.executeRequestForJsonResultEx("CheckState", param,
                new HttpUtil.ResultEventJson<CheckStatePackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        // TODO Auto-generated method stub
                        ToastUtil.show(getApplicationContext(), "网络异常");
                    }

                    @Override
                    public void onUIRoutine(CheckStatePackage resultObj) {
                        // TODO Auto-generated method stub
                        if (resultObj.getResult() == 200) {

                            GetDeviceDetail();
                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(getApplicationContext(), "开启或关闭蓝牙锁失败");
                            }else {
                                ToastUtil.show(getApplicationContext(), resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    /**
     * 4.2.2	操作锁时调用
     * @param operation  操作内容（0：开锁，1：关锁）
     * @param success    操作成败（true:成功，false:失败）
     */
    private void LockOperation(String operation, String success) {
        Log.i(TAG, "操作锁时调用type " + type);

        String userId = SharedPreferencesUtils.getString(BlueYasuoActivity.this, Resource.USERID,null);
//        String deviceId = SharedPreferencesUtils.getString(BlueYasuoActivity.this,Resource.DEVICEID,null);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("deviceAddress",deviceAddress);
        param.put("userId",userId);
        param.put("operation",operation);
        param.put("type",type);
        param.put("success",success);
        HttpUtil.executeRequestForJsonResultEx("LockOperation", param,
                new HttpUtil.ResultEventJson<LockOperationPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        // TODO Auto-generated method stub
                        ToastUtil.show(getApplicationContext(), "网络异常");
                    }

                    @Override
                    public void onUIRoutine(LockOperationPackage resultObj) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "操作锁时调用: " + resultObj.getResult());
                        if (resultObj.getResult() == 200) {
                            ToastUtil.show(getApplicationContext(), "操作锁时调用deviceId " + numberTv.getText().toString()+
                                    deviceAddress);
                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(getApplicationContext(), "操作锁失败");
                            }else {
                                ToastUtil.show(getApplicationContext(), resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    @Subscribe
    public void onBleCallbackListener(BaseState.BleCallbackListener event) {
        Log.e(TAG, "onBleCallbackListener" );
        Object data = event.item;

        if (data instanceof BleScanResult) {

            bleScanResult = (BleScanResult) data;
            int s= 1;
            for(int x= 0 ;x<i ;x++)
            {
                if( bleScanResult.getName().equals(playList[x]))
                {
                    s= 0;
                }
            }
            if(s==1 && i<100 )
            {
                mAlertDialog.cancel();
                playList[i] = bleScanResult.getName();
                bleList[i] = bleScanResult;
                i++;
                showDialog();
            }
        }
        if (data instanceof Boolean) {
            Log.e(TAG, "Boolean");
            //showDialog( );
            Boolean status = (Boolean) data;
            refreshConnect(status);

            if (status) {
                getLockStatus();
            }
        }

        if (data instanceof Integer) {
            Log.e(TAG, "Integer");
            Integer state = (Integer) data;

            if (state == CODE_SCAN) {
                stateTV.setText(R.string.ble_scan);
            }
            else if (state == CODE_CONNECT) {
                stateTV.setText(getString(R.string.ble_connect_equip, Globals.BLE_NAME));

                if(!TextUtils.isEmpty(Globals.BLE_ADDRESS)){
                    //deviceAddress = Globals.BLE_ADDRESS;
                    GetDeviceDetail();
                }

            }
        }else if (data instanceof BleGetLock) {
            Log.e(TAG, "BleGetLock");
            UIHelper.dismiss();
            BleGetLock bleGetLock = (BleGetLock) data;
            Integer state1 = bleGetLock.getState();//蓝牙锁状态 1为关闭 0为开启
            state = state1.toString();
            BulidLock();
//            refreshState(state == 0);
        } else if (data instanceof BleReply) {
            Log.e(TAG, "BleReply");
            UIHelper.dismiss();
            BleReply replyData = (BleReply) data;

            if(replyData.isOpenBike()) {
                refreshState(true);
                Log.i(TAG, getString(R.string.lock_result_open));
                state = "0";
//                BulidLock();
//                new GetTokenAsy().execute(recondData,blueToothName,"1");
                UIHelper.showToast(this, R.string.lock_result_open);
                stateTV.setText("开");
            }
            else if(replyData.isCloseBike()) {
                Log.i(TAG, getString(R.string.lock_result_close));
                refreshState(false);
                state = "1";
//                BulidLock();
//                new GetTokenAsy().execute(recondData,blueToothName,"2");
                UIHelper.showToast(this, R.string.lock_result_close);
                stateTV.setText("关");
//                Intent intent = new Intent(MainActivity.this,TowerCraneMainActivity.class);
//                startActivity(intent);
                finish();
            }
        }else if (data instanceof BleErrorData) {
            Log.e(TAG, "BleErrorData");
            UIHelper.dismiss();
            BleErrorData errorData = (BleErrorData) data;
            UIHelper.showToast(this, errorData.getMsg());
        }else if (data instanceof LockStatusData){
            UIHelper.dismiss();
            LockStatusData lockStatusData = (LockStatusData) data;
            if(lockStatusData.getState()== 1){
                UIHelper.showToast(this, "防拆设备被触发");
            }
            else if(lockStatusData.getState()== 0) {
                UIHelper.showToast(this, "防拆设备未触发");
            }
        }
    }

    class GetTokenAsy extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return NetworkApi.upLoadLockStatuc(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }

    }

    //间隔200毫秒执行开锁分包命令2
    private Timer getTimer;
    private void getLockStatus() {
        if(getTimer != null) {
            getTimer.cancel();
            getTimer = null;
        }
        getTimer = new Timer();
        getTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Constants.bleService != null) {
                    try {
                        Constants.bleService.getLockStatus();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500);//几秒后开始，每隔几秒
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService();
    }

    /**
     * 跳转页面
     * @param context
     * @param type  0:人脸识别 1:密码开锁
     */
    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context,BlueYasuoActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }
    /*

     */
    public void showDialog(){

        final Context context = this;
        //定义列表选项
        String[] items = new String[i];
        String title = "蓝牙设备列表";
        for (int x = 0 ; x<i ; x++ )
        {
            items[x] = playList[x];
        }

        //创建对话框
        mAlertDialog = new AlertDialog.Builder(context)
                .setTitle(title)//设置对话框标题
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  Toast.makeText(context, "Item is "+which+".", Toast.LENGTH_LONG).show();

                        connectDevice(bleList[which]);
                        //deviceAddress= bleScanResult.getAddress();
                        dialog.dismiss();

                    }
                })
                .setNeutralButton("重新扫描",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ...To-do
                                dialog.dismiss();
                                showDialog();
                            }
                        })
                .setPositiveButton("结束扫描",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ...To-do
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }


}
