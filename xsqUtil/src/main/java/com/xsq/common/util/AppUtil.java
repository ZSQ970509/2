package com.xsq.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.zxing.common.StringUtils;
import com.xsq.common.core.XsqCommon;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/6 0006.
 */
public class AppUtil {

    /**
     * 获取当前应用程序名称
     *
     * @return
     */
    public static String getApplicationName() {
        try {
            PackageManager packageManager = XsqCommon.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    XsqCommon.getInstance().getApplicationContext().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return XsqCommon.getInstance().getApplicationContext().getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.error("getApplicationName error.", e);
        }
        return null;
    }

    /**
     * 获取当前应用程序包名称
     *
     * @return
     */
    public static String getAppPackageName(){
        try {
            PackageManager packageManager = XsqCommon.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    XsqCommon.getInstance().getApplicationContext().getPackageName(), 0);
            return packageInfo.applicationInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.error("getAppPackageName error.", e);
        }
        return null;
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager packageManager = XsqCommon.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    XsqCommon.getInstance().getApplicationContext().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.error("getVersionName error.", e);
        }
        return null;
    }

    /**
     * 获取应用程序版本号
     *
     * @return
     */
    public static int getVersionCode() {
        try {
            PackageManager packageManager = XsqCommon.getInstance().getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    XsqCommon.getInstance().getApplicationContext().getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.error("getVersionCode error.", e);
        }
        return -1;
    }

    /**
     * 获取Manifest Meta Data
     * @param metaKey
     * @return
     */
    public static String getMetaData(String metaKey) {
        String name = XsqCommon.getInstance().getApplicationContext().getPackageName();
        ApplicationInfo appInfo;
        String msg = "";
        try {
            appInfo = XsqCommon.getInstance().getApplicationContext().getPackageManager().getApplicationInfo(name,
                    PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(metaKey);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.error("getMetaData error.", e);
        }

        return msg;
    }

    /**
     * 安装一个apk文件
     * @param uriFile
     */
    public static void install(File uriFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        XsqCommon.getInstance().getApplicationContext().startActivity(intent);
    }

    /**
     * 卸载一个app
     * @param packageName
     */
    public static void uninstall(String packageName) {
        //通过程序的包名创建URI
        Uri packageURI = Uri.parse("package:" + packageName);
        //创建Intent意图
        Intent intent = new Intent(Intent.ACTION_DELETE,packageURI);
        //执行卸载程序
        XsqCommon.getInstance().getApplicationContext().startActivity(intent);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param packageName 应用包名
     * @return
     */
    public static boolean isAvilible(String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = XsqCommon.getInstance().getApplicationContext().getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    /**
     * 服务是否运行
     * @param className
     * @return
     */
    public static boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                XsqCommon.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() == 0) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 进程是否运行
     */
    public static boolean isProessRunning(String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) XsqCommon.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
                return isRunning;
            }
        }

        return isRunning;
    }

    /**
     * 获取IMEI
     * @return
     */
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) XsqCommon.getInstance().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if ( imei == null || "".equals(imei.trim()) ) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取UDID
     * @return
     */
    public static String getUDID() {
        String udid = Settings.Secure.getString(XsqCommon.getInstance().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (udid == null || "".equals(udid.trim()) || udid.equals("9774d56d682e549c")
                || udid.length() < 15) {
            SecureRandom random = new SecureRandom();
            udid = new BigInteger(64, random).toString(16);
        }

        if ( udid == null || "".equals(udid.trim()) ) {
            udid = "";
        }

        return udid;
    }

    /**
     * 获取非系统应用包名
     * @return
     */
    public static List<String> getAppPackageNamelist() {
        List<String> packList = new ArrayList<>();
        PackageManager pm = XsqCommon.getInstance().getApplicationContext().getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(0);
        for(PackageInfo packinfo:packinfos){
            String packname = packinfo.packageName;
            packList.add(packname);
        }

        return packList;
    }

    /**
     * 返回移动终端类型
     * PHONE_TYPE_NONE :0 手机制式未知
     * PHONE_TYPE_GSM :1 手机制式为GSM，移动和联通
     * PHONE_TYPE_CDMA :2 手机制式为CDMA，电信
     * PHONE_TYPE_SIP:3
     * @return
     */
    public static int getPhoneType() {
        TelephonyManager telephonyManager = (TelephonyManager) XsqCommon.getInstance().getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }

    /**
     * 获取进程名
     * @param pid 进程ID
     * @return
     */
    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) XsqCommon.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }

        return null;
    }
}
