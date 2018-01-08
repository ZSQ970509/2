package com.xsq.czy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.xsq.czy.net.GetAgreementlPackage;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置
 * Created by Administrator on 2017/5/2.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    /**修改密码*/
    private TextView updateBtn;
    /**检查版本*/
    private TextView UpdateVersionBtn;
    /**隐私协议*/
    private TextView UpdatePrivacyBtn;
    /**用户登出*/
    private Button exitBtn;

    private ImageView backBtn;
    Context mContext;
    private IFlytekUpdate updManager;
    private String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView() {
        updateBtn = (TextView) findViewById(R.id.activity_setting_update_pwd);
        updateBtn.setOnClickListener(this);
        exitBtn = (Button) findViewById(R.id.activity_setting_exit_btn);
        exitBtn.setOnClickListener(this);
        backBtn = (ImageView) findViewById(R.id.activity_setting_back_btn);
        backBtn.setOnClickListener(this);
        UpdateVersionBtn = (TextView) findViewById(R.id.activity_setting_update_version);
        UpdateVersionBtn.setOnClickListener(this);
        UpdatePrivacyBtn = (TextView) findViewById(R.id.activity_setting_update_privacy);
        UpdatePrivacyBtn.setOnClickListener(this);
        mContext = this.getApplicationContext();
        updManager = IFlytekUpdate.getInstance(mContext);
        updManager = IFlytekUpdate.getInstance(mContext);
        updManager.setDebugMode(true);
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
        // 设置通知栏icon，默认使用SDK默认
        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
        GetPrivacy();

    }

    @Override
    public void onClick(View view) {
        // UpdateVersionBtn  UpdaterivacyBtn
        if (view == updateBtn) {
            Intent intent = new Intent(this,UpdatePwdActivity.class);
            startActivity(intent);
        }else if (view == backBtn) {
            finish();
        }
        else if (view == UpdateVersionBtn) {
           // finish();

            updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
            updManager.forceUpdate(SettingActivity.this, updateListener);
            Toast.makeText(SettingActivity.this, "这是最新版本",
                    Toast.LENGTH_SHORT).show();
        }
        else if (view == UpdatePrivacyBtn) {


            dialog1(msg);


        }else if (view == exitBtn) {
            finish();
            if(HomeActivity.homeActivityContext != null){
                HomeActivity.homeActivityContext.finish();
            }
            if (FaceRecognitionActivity.faceActivityContext != null){
                FaceRecognitionActivity.faceActivityContext.finish();
            }
        }
    }
/*
* 检查更新
*/
    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorcode, UpdateInfo result) {
            if(errorcode == UpdateErrorCode.OK && result!= null) {
                if(result.getUpdateType() == UpdateType.NoNeed) {
                    //showTip("已经是最新版本！");
                    Toast.makeText(SettingActivity.this, "已经是最新版本",
                            Toast.LENGTH_SHORT).show();


                    return;
                }
                updManager.showUpdateInfo(SettingActivity.this, result);
                Toast.makeText(SettingActivity.this, "已经是最新版本",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                //showTip("请求更新失败！\n更新错误码：" + errorcode);
                Toast.makeText(SettingActivity.this, "请求更新失败！\n更新错误码：" + errorcode,
                        Toast.LENGTH_SHORT).show();
            }


        }
    };
/*
* 对话框
*/
private void dialog1(String msg ){
    AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
    builder.setTitle("隐私协议"); //设置标题
    //设置内容
    builder.setMessage(msg); //设置内容
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss(); //关闭dialog

        }
    });
    //参数都设置完成了，创建并显示出来
    builder.create().show();
}
/**
 * 获取隐私协议
 */
private void GetPrivacy() {
    String userId = SharedPreferencesUtils.getString(SettingActivity.this, Resource.USERID,null);
    Map<String,Object> map = new HashMap<>();
    map.put("userId",userId);

    HttpUtil.executeRequestForJsonResultEx("GetAgreement", map,
            new HttpUtil.ResultEventJson<GetAgreementlPackage>() {

                @Override
                public void onExceptionRoutine(HttpObject httpObject) {
                    ToastUtil.show(SettingActivity.this,"网络异常");
                }

                @Override
                public void onUIRoutine(GetAgreementlPackage resultObj) {

                    msg=resultObj.getAgreeMent();


                }
            });
}
}
