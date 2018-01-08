package com.sofi.smartlocker.ble.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

/**
 * Created by lan on 2016/10/9.
 */

public class IntentUtils {

    /**
     * 获取手机型号
     * @return 版本号
     */
    private static String getModel(){
        return Build.MODEL;
    }

    /**
     * 获取系统版本号
     * @return 版本号
     */
    private static int getSDKInt(){
        return Build.VERSION.SDK_INT;
    }
    /**
     * 获取系统版本
     * @return 版本
     */
    private static String getSDKRelease(){
        return Build.VERSION.RELEASE;
    }

    public static boolean isPraAl(String model, int sdkInt, String release) {
        return model.equals("PRA-AL00") && sdkInt == 24 && release.equals("7.0");
    }

    public static boolean isA001(String model, int sdkInt, String release) {
        return model.equals("A0001") && sdkInt == 18 && release.equals("4.3");
    }

    public static boolean filterScan() {
        String model = getModel();
        int sdkInt = getSDKInt();
        String release = getSDKRelease();

        if (isPraAl(model, sdkInt, release)) {
            return false;
        }

        return true;
    }

    public static Intent makeIntentWithPackageName(Context context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return null;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);

            return intent;
        }

        return null;
    }

}
