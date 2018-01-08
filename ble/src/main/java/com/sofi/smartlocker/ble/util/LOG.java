package com.sofi.smartlocker.ble.util;

import android.util.Log;

public class LOG {
	private static final String TAG = "smartlocker";
	private static final boolean D = true;

	public static void D(String debug) {
		if (D)
			Log.d(TAG, debug);
	}

	public static void D(String tag, String debug) {
		if (D) {
			Log.d(tag, debug);
		}
	}

    public static void E(String debug) {
        if (D)
            Log.e(TAG, debug);
    }

    public static void E(String tag, String debug) {
        if (D) {
            Log.e(tag, debug);
        }
    }

	public static void Exec(Exception e) {
		if (D) {
			e.printStackTrace();
		}
	}

}
