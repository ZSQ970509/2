package com.xsq.czy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {

	private static final String PREFEREN_NAME = "preference_huixin";
	private static final String FIRST_OPEN_APP = "first_open_app";
	private static final String AUTO_LOGIN = "auto_login";
	private static final String SAVE_PASSWORD = "save_password";
	private static final String SAVE_NAME = "save_name";
	private static final String IS_SAVE_PASSWORD = "is_save_name";
	private static final String PROJECT_LAT = "project_lat";
	private static final String PROJECT_LNG = "project_lng";
	private static final String PROJECT_ADDRSTR = "project_addrstr";
	private static final String UPDATE_DATA = "update_data";
	private static final String PROVINCE = "province";
	private static final String CITY = "city";
	private static final String USERID = "UserId";

	public static void saveUserId(Context context, String value) {
		saveValue(context, USERID, value);
	}

	public static String getUserId(Context context) {
		return getValue(context, USERID, "");
	}

	public static void saveCity(Context context, String value) {
		saveValue(context, CITY, value);
	}

	public static String getCity(Context context) {
		return getValue(context, CITY, "");
	}

	public static void saveProvince(Context context, String value) {
		saveValue(context, PROVINCE, value);
	}

	public static String getProvince(Context context) {
		return getValue(context, PROVINCE, "35");
	}

	public static void saveUpdateData(Context context, String value) {
		saveValue(context, UPDATE_DATA, value);
	}

	public static String getUpdateData(Context context) {
		return getValue(context, UPDATE_DATA);
	}

	public static void saveProjectLat(Context context, String value) {
		saveValue(context, PROJECT_LAT, value);
	}

	public static String getProjectLat(Context context) {
		return getValue(context, PROJECT_LAT);
	}

	public static void saveProjectLng(Context context, String value) {
		saveValue(context, PROJECT_LNG, value);
	}

	public static String getProjectLng(Context context) {
		return getValue(context, PROJECT_LNG);
	}

	public static void saveProjectAddrStr(Context context, String value) {
		saveValue(context, PROJECT_ADDRSTR, value);
	}

	public static String getProjectAddrStr(Context context) {
		return getValue(context, PROJECT_ADDRSTR);
	}

	public static boolean isSavePassword(Context context) {
		return getBooleanValue(context, IS_SAVE_PASSWORD);
	}

	public static void setIsSavePassword(Context context, boolean value) {
		setBooleanValue(context, IS_SAVE_PASSWORD, value);
	}

	public static boolean autoLogin(Context context) {
		return getBooleanValue(context, AUTO_LOGIN);
	}

	public static void setAutoLogin(Context context, boolean value) {
		setBooleanValue(context, AUTO_LOGIN, value);
	}

	public static void saveName(Context context, String value) {
		saveValue(context, SAVE_NAME, value);
	}

	public static String getName(Context context) {
		return getValue(context, SAVE_NAME);
	}

	public static void savePassword(Context context, String value) {
		saveValue(context, SAVE_PASSWORD, value);
	}

	public static String getPassword(Context context) {
		return getValue(context, SAVE_PASSWORD);
	}

	public static boolean isFirstOpenApp(Context context) {
		String value = getValue(context, FIRST_OPEN_APP);
		if (value.equals("true")) {
			return false;
		} else {
			saveValue(context, FIRST_OPEN_APP, "true");
			return true;
		}
	}

	private static boolean getBooleanValue(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(PREFEREN_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	private static void setBooleanValue(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(PREFEREN_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private static String getValue(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(PREFEREN_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, "");
		//return "13850105150";
	}

	private static String getValue(Context context, String key, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(PREFEREN_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	private static void saveValue(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(PREFEREN_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

}
