package com.xsq.czy.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xsq.common.core.xsqcomponent.IHttpComponent;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.activity.view.CustomDialog;
import com.xsq.czy.activity.view.LoadingDialog;
import com.xsq.czy.net.LockPwdPackage;
import com.xsq.czy.network.NetworkApi;
import com.xsq.czy.util.BitmapCompressor;
import com.xsq.czy.util.Constant;
import com.xsq.czy.util.FileUtils;
import com.xsq.czy.util.IOFormat;
import com.xsq.czy.util.ImageUtil;
import com.xsq.czy.util.PictureUtils;
import com.xsq.czy.util.PreferenceUtil;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UninstallActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "FaceRecognitionActivity";
    private ImageView uninstallBackButton;
    private Button uninstallButton;

    public static UninstallActivity uninstallActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_uninstall);
        uninstallActivityContext = this;
        Constant.setContext(getApplicationContext());
        initView();
    }

    private void  initView() {
        uninstallBackButton = (ImageView)findViewById(R.id.activity_uninstall_back_btn);
        uninstallButton = (Button) findViewById(R.id.activity_uninstall_btn);
        uninstallBackButton.setOnClickListener(this);
        uninstallButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == uninstallBackButton) {

        }else if (view == uninstallButton) { // 记住密码

        }
    }
    private void checkOldPackage() {
        String packageName = "com.xsq.czy";
        if (isAvilible(this, packageName)) {
            Intent uninstall_intent = new Intent();
            uninstall_intent.setAction(Intent.ACTION_DELETE);
            uninstall_intent.setData(Uri.parse("package:" + packageName));
            startActivity(uninstall_intent);
        }
    }
    private boolean isAvilible(Context cxt, String packagename) {
        PackageManager pm = cxt.getPackageManager();
        List<PackageInfo> pinfo = pm.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packagename)) {
                return true;
            }
        }
        return false;
    }







}
