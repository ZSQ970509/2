package com.xsq.czy.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.beans.BlueDevice;
import com.xsq.czy.beans.VersionBean;
import com.xsq.czy.net.AloginPackage;
import com.xsq.czy.network.INetCall;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;
import com.xsq.czy.util.VersionUpdateUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends Activity implements View.OnClickListener {

    /**
     * 帐号
     */
    private EditText userET;

    /**
     * 密码
     */
    private EditText pwdET;

    /**
     * 记住密码
     */
    private TextView rememberBtn;

    /**
     * 登录按钮
     */
    private Button loginBtn;

    private Boolean whether = false;

    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private Handler mHandler = new Handler();
    Context mContext;
    private IFlytekUpdate updManager;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    AlertDialog dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initView();

        mContext = this.getApplicationContext();
        VersionUpdateUtils.getVersionInfo(new INetCall<VersionBean>() {
            @Override
            public void onCallback(boolean value, final VersionBean result) {
                if (value) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result.getUpdateVersionCode() > VersionUpdateUtils.getVersionCode(LoginActivity.this)) {
                                VersionUpdateUtils.updateApp(result, LoginActivity.this);
                            }
                        }
                    });
                } else {
                    ToastUtil.show(LoginActivity.this, "网络异常");
                }
            }
        });
        //使用讯飞更新
//        updManager = IFlytekUpdate.getInstance(mContext);
//        updManager.setDebugMode(true);
//        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
//        // 设置通知栏icon，默认使用SDK默认
//        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "true");
//        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
//        updManager.forceUpdate(LoginActivity.this, updateListener);

        //downLoadApk();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPersimmions();
        openGPSSettings();
    }

    private void initView() {
        userET = (EditText) findViewById(R.id.activity_login_user);
        pwdET = (EditText) findViewById(R.id.activity_login_pwd);
        rememberBtn = (TextView) findViewById(R.id.activity_login_remember_pwd);
        rememberBtn.setOnClickListener(this);
        loginBtn = (Button) findViewById(R.id.activity_login_btn);
        loginBtn.setOnClickListener(this);
        whether = SharedPreferencesUtils.getBoolean(getApplicationContext(), Resource.WHETHER, false);
        String name = SharedPreferencesUtils.getString(LoginActivity.this, Resource.USERNAME, null);
        if (!TextUtils.isEmpty(name)) {
            userET.setText(name);
        }
        if (whether) {
            Drawable drawable = getResources().getDrawable(R.drawable.remember_pwd);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            rememberBtn.setCompoundDrawables(drawable, null, null, null);
            String pwd = SharedPreferencesUtils.getString(LoginActivity.this, Resource.PASSWORD, null);
            if (!TextUtils.isEmpty(pwd)) {
                pwdET.setText(pwd);
            }
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.remember_pwd_un);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            rememberBtn.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == loginBtn) { // 登录
            loginClick();
        } else if (view == rememberBtn) { // 记住密码
            if (whether) {
                whether = false;
                SharedPreferencesUtils.putBoolean(getApplicationContext(), Resource.WHETHER, whether);
                Drawable drawable = getResources().getDrawable(R.drawable.remember_pwd_un);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                rememberBtn.setCompoundDrawables(drawable, null, null, null);
            } else {
                whether = true;
                SharedPreferencesUtils.putBoolean(getApplicationContext(), Resource.WHETHER, whether);
                Drawable drawable = getResources().getDrawable(R.drawable.remember_pwd);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                rememberBtn.setCompoundDrawables(drawable, null, null, null);
            }
        }
    }

    //登陆
    private void loginClick() {
        // TODO Auto-generated method stub

        final String username = userET.getText().toString().trim();
        final String password = pwdET.getText().toString().trim();
        String registrationId = JPushInterface.getRegistrationID(this);
        if (username == null || "".equals(username)) {
            userET.setError("请输入用户名");

            return;
        }
        if (password == null || "".equals(password)) {
            pwdET.setError("请输入密码");

            return;
        }

        MessageDigest md5;
        String authCode = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            authCode = Base64.encodeToString(md5.digest(), Base64.DEFAULT).substring(0, 8);
            HttpUtil.setAuthCodeValue(authCode);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("username", username);
        param.put("password", password);
        param.put("registrationId", registrationId);
        HttpUtil.executeRequestForJsonResultEx("Alogin", param,
                new HttpUtil.ResultEventJson<AloginPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        // TODO Auto-generated method stub

                        ToastUtil.show(getApplicationContext(), "网络异常");
                    }

                    @Override
                    public void onUIRoutine(AloginPackage resultObj) {
                        // TODO Auto-generated method stub
                        if (resultObj.getResult() == 200) {
                            BlueDevice blue = new BlueDevice();
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(Resource.USERID, resultObj.getUserId());
                            if (!(resultObj.getDeviceAddress() == null || resultObj.getDeviceAddress().isEmpty())) {
                                map.put(Resource.ADDRESS, resultObj.getDeviceAddress().get(0).getDeviceAddress());
                                map.put(Resource.DEVICENAME, resultObj.getDeviceAddress().get(0).getDeviceName());
                                map.put(Resource.DEVICEID, resultObj.getDeviceAddress().get(0).getDeviceId());
                            }
                            map.put(Resource.USERNAME, username);
                            map.put(Resource.PASSWORD, password);
                            SharedPreferencesUtils.putString(LoginActivity.this, map);
//                            Intent intent = new Intent(getApplicationContext(), FaceRecognitionActivity.class);
//                            startActivity(intent);
                            if (resultObj.getType().equals("1")) {
                                FaceRecognitionActivity.actionStart(LoginActivity.this, resultObj.getDevicePwd());

                            } else {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                                startActivity(intent);

                            }
                            finish();
                        } else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {

                                ToastUtil.show(getApplicationContext(), "登录失败");
                            } else {

                                ToastUtil.show(getApplicationContext(), resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    /*
 * 从服务器中下载APK
 */
    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorcode, UpdateInfo result) {
            if (errorcode == UpdateErrorCode.OK && result != null) {
                if (result.getUpdateType() == UpdateType.NoNeed) {
                    //showTip("已经是最新版本！");
                    Toast.makeText(LoginActivity.this, "已经是最新版本",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                updManager.showUpdateInfo(LoginActivity.this, result);
            } else {
                //showTip("请求更新失败！\n更新错误码：" + errorcode);
                Toast.makeText(LoginActivity.this, "请求更新失败！\n更新错误码：" + errorcode,
                        Toast.LENGTH_SHORT).show();
            }


        }
    };
    private final int SDK_PERMISSION_REQUEST = 127;

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读取电话状态权限
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            /*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void openGPSSettings() {
        if(dialog != null){
            dialog.dismiss();
        }
        if (checkGPSIsOpen()) {

        } else {
            //没有打开则弹出对话框
            dialog  = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前应用需要打开定位功能。\n\n请点击“设置”-“定位服务”-打开定位功能。")
                    // 拒绝, 退出应用
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    openGPSSettings();
                                }
                            })

                    .setPositiveButton("设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    if (checkGPSIsOpen()) {
                                        dialog.dismiss();
                                    }else{
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent, 10);
                                    }



                                }
                            })

                    .setCancelable(false)
                    .show();

        }
    }
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            //做需要做的事情，比如再次检测是否打开GPS了 或者定位
            if(dialog != null){
                dialog.dismiss();
            }
            openGPSSettings();
        }
    }
}
