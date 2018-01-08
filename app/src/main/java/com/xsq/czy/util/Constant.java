package com.xsq.czy.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/4/24.
 */
public class Constant extends Application {

    private static final String TAG = "Constant";
    public static final boolean DEBUG = true;
    public static final String APP_PREFERENCES = "app_preferences";
    public static final String TEMP_PATH = "IconLoadDemo"; 	// SD卡中的临时文件夹路径
    private static SharedPreferences shared;
    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    public static void setContext(Context mContext){
        Constant.mContext = mContext;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        // 不知为啥，这个模块抽取出来后，在这里getApplicationContext为null，所以临时在Activity中传值过来
//		mContext = getApplicationContext();
    }

    public static SharedPreferences getShared(){
        if(shared == null){
            shared = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        }
        Utils.printLogi(TAG, "shared = " + shared);
        return shared;
    }

    /**
     * 获取用户头像保存地址
     * @return
     */
    public static String getUserIconPath(){
        return getShared().getString("userIconPath", null);
    }

    /**
     * 设置用户头像保存地址(相对路径)
     * @param userIconPath
     */
    public static void setUserIconPath(String userIconPath){
        getShared().edit().putString("userIconPath", userIconPath).commit();
    }

    /**
     * 区分用户名来保存头像
     * @return 返回当前登录的用户名或昵称
     */
    public static Object getUserName(){
        return "testUserName";
    }

}
