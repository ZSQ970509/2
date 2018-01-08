package com.xsq.czy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/24.
 */
public class Utils {

    /**
     * 打印Log.i日志
     *
     * @param tag
     * @param msg
     */
    public static void printLogi(String tag, String msg){
        if (Constant.DEBUG) {
            if (tag != null && msg != null) {
                Log.i(tag, msg);
            }
        }
    }

    /**
     * 获取当前系统完整日期，如 2014-01-01 10:20:55
     */
    @SuppressLint("SimpleDateFormat")
    public static String getLongDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    /**
     * 把单位dip转换为px
     *
     * @param context 上下文
     * @param dip 单位值
     * @return 返回转换后的px单位值
     */
    public static int getPX(final Context context, float dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    /**
     * 跳转界面
     *
     * @param clazz 跳转的目标Activity，带数据
     */
    public static void openActivity(Context context, Class<?> clazz){
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

}
