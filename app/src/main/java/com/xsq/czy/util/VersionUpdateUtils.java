package com.xsq.czy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.j256.ormlite.misc.VersionUtils;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.common.util.JsonUtil;
import com.xsq.czy.R;
import com.xsq.czy.activity.FaceRecognitionActivity;
import com.xsq.czy.activity.HomeActivity;
import com.xsq.czy.activity.LoginActivity;
import com.xsq.czy.beans.BlueDevice;
import com.xsq.czy.beans.VersionBean;
import com.xsq.czy.net.AloginPackage;
import com.xsq.czy.network.INetCall;
import com.xsq.czy.network.INetCallback;
import com.xsq.czy.network.NetworkApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 版本更新
 * Created by Administrator on 2017-12-27.
 */

public class VersionUpdateUtils {
    /**
     * 获取版本更新信息
     * @param call
     */
    public static void getVersionInfo(final INetCall<VersionBean> call) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = NetworkApi.getUpdataInfo();
                    if (result == null) {
                        call.onCallback(false, null);
                    } else {
                        JSONArray dataArray = new JSONArray(result);
                        JSONObject data = dataArray.getJSONObject(0);
                        VersionBean version = new Gson().fromJson(data.toString(), VersionBean.class);
                        call.onCallback(true, version);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新App
     * @param versionBean
     * @param activity
     */
    public static void updateApp(final VersionBean versionBean, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        BigDecimal bg = new BigDecimal(Double.parseDouble(versionBean.getUpdateFileSize()) / 1024 / 1024);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        builder.setTitle("发现新版本").
                setMessage("最新版本：V" + versionBean.getUpdateVersionName() + "\n" + "新版本大小：" + f1 + "M\n\n" + "更新内容：\n" + versionBean.getUpdateLogMsg()).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadNewVersionProgress(versionBean, activity);//下载最新的版本程序
                    }
                }).
                setNegativeButton("取消", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        // 显示对话框
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(88, 190, 252));

    }

    private static ProgressDialog pd;    //进度条对话框

    /**
     * 显示更新进度条
     * @param versionBean
     * @param activity
     */
    private static void loadNewVersionProgress(VersionBean versionBean, final Activity activity) {
        final String uri = versionBean.getUpdateDownLoadUrl();
        pd = new ProgressDialog(activity);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgressDrawable(activity.getResources().getDrawable(R.drawable.progessbar));
        pd.setMessage("正在下载更新");
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressNumberFormat("");
        pd.show();

        //启动子线程下载任务
        new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    File file = getFileFromServer(uri);
                    sleep(3000);
                    installApk(file, activity);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    //下载apk失败
                    Toast.makeText(activity, "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 下载apk
     * @param uri
     * @return
     * @throws Exception
     */
    public static File getFileFromServer(String uri) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(uri);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    conn.disconnect();
                }
            });
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "tk_update.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
                //pd.setProgressNumberFormat(String.format("%.2fM/%.2fM", total/1024/1024, conn.getContentLength()/1024/1024));
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     * @param file
     * @param activity
     */
    protected static void installApk(File file, Activity activity) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //适配安卓7.0
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkFileUri = FileProvider.getUriForFile(activity.getApplicationContext(),
                    activity.getPackageName(), file);
            i.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + file.toString()),
                    "application/vnd.android.package-archive");// File.toString()会返回路径信息
        }
        activity.startActivity(i);
        activity.finish();
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
