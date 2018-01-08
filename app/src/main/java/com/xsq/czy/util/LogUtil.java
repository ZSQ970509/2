package com.xsq.czy.util;

import android.util.Log;

public class LogUtil {

	private static final String LOG_TAG = "huixin";
	private static boolean isLog = true;

	public static void logD(String msg) {
		if (isLog) {
			Log.d(LOG_TAG, msg);
		}
	}

	public static void logE(String msg) {
		if (isLog) {
			Log.e(LOG_TAG, msg);
		}
	}

}
