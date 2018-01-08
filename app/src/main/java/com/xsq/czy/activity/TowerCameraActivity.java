package com.xsq.czy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xsq.czy.R;
import com.xsq.czy.activity.view.CameraSurfaceView;
import com.xsq.czy.activity.view.CustomDialog;

public class TowerCameraActivity extends AppCompatActivity {
    CameraSurfaceView mySurfaceView;// surfaceView声明
    SurfaceHolder holder;// surfaceHolder声明
    Camera myCamera;// 相机声明
    String filePath = "/sdcard/wjh.jpg";// 照片保存路径
    boolean isClicked = false;// 是否点击标识
    private Button takePicBtn;
    private int mScreenWidth;//获取屏幕宽度
    private int mScreenHeight;//获取屏幕高度
    FrameLayout fl;
    ImageView mImgMuban_Above;
    ImageView mImgMuban_Below;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_tower_crane_camera);
        getScreenMetrix(this);
        fl = (FrameLayout) findViewById(R.id.F1_photo);
        mySurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        //设置预览的画面比例
        LinearLayout.LayoutParams flParams = (LinearLayout.LayoutParams) fl.getLayoutParams();
        flParams.height = (int) (mScreenWidth * 1.33334F);
        flParams.width = mScreenWidth;
        fl.setLayoutParams(flParams);

        takePicBtn = (Button) findViewById(R.id.btn_take_photo);
        //
        takePicBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    mySurfaceView.takePicture();
                } catch (Exception e) {
                    // TODO: handle exception
                    showDialog("开锁失败!");// 识别失败
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }
    public void showDialog(String text) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(text);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置你的操作事项
            }
        });

        builder.create().show();
    }
}
