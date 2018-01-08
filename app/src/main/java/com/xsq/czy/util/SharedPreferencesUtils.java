package com.xsq.czy.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
	public static final String KEY_TIMER = "userID";
	public static final String APP_ID = "mmp";
	private final static String SHARED_FILE = "xsq";

	// 获取缓存
	public static String getCachedUserID(Context context) {
		return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
				.getString(KEY_TIMER, null);
	}

	// 缓存
	public static void cachedUserID(Context context, String userID) {
		Editor editor = context.getSharedPreferences(APP_ID,
				Context.MODE_PRIVATE).edit();
		editor.putString(KEY_TIMER, userID);
		editor.commit();
	}
	
	/** 获取string类型数据 */
	public static String getString(Context context, String key,
			String object) {
		SharedPreferences sp = context.getSharedPreferences(SHARED_FILE,
				Context.MODE_PRIVATE);
		return sp.getString(key, object);
	}
	
	/** 存入string类型数据 */
	public static void putString(Context context, Map<String, String> keyValues) {
		SharedPreferences sp = context.getSharedPreferences(SHARED_FILE,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		for (Map.Entry<String, String> me : keyValues.entrySet()) {
			editor.putString(me.getKey(), me.getValue());
		}
		editor.commit();
	}
	
	public static Boolean getBoolean(Context context, String key, Boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}
	
	public static void putBoolean(Context context, String key, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
	
}
