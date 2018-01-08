package com.xsq.common.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.xsq.common.R;
import com.xsq.common.core.XsqCommon;

public class ActivityUtil {

	private static Object activityOpt = new Object();

	/**
	 * 返回home桌面
	 */
	public static void backToHome(){
		synchronized (activityOpt){
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.addCategory(Intent.CATEGORY_HOME);
			home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			XsqCommon.getInstance().startActivity(home);
		}
	}
	

	/** 切换到指定顶级activity并清空任务栈
	 * @param cls
	 */
	public static void backToTopView(Class<? extends Activity> cls){
		synchronized (activityOpt){
			Intent i = new Intent();
			i.setClassName(XsqCommon.getInstance().getApplicationContext().getPackageName(), cls.getName());
			//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Intent ii = IntentCompat.makeRestartActivityTask(i.getComponent());
			XsqCommon.getInstance().startActivity(ii);
		}
	}

	/**
	 * 启动service.
	 * @param service
	 * @return
	 */
	public static ComponentName startService(Intent service){
		return XsqCommon.getInstance().getApplicationContext().startService(service);
	}

	/**
	 * 终止service.
	 * @param service
	 * @return
	 */
	public static boolean stopService(Intent service){
		return XsqCommon.getInstance().getApplicationContext().stopService(service);
	}

	/**
	 * 启动新的activity
	 * @param intent
	 */
	public static void launchNewActivity(Intent intent){
		synchronized (activityOpt){
			try {
				if (intent != null) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					LogUtil.debug("Launching intent: " + intent + " with extras: " + intent.getExtras());
					XsqCommon.getInstance().startActivity(intent);
				}
			} catch (ActivityNotFoundException ignored) {
				TextUtil.showMessageForShort(R.string.component_msg_intent_failed);
			}
		}
	}

	/**
	 * 启动activity
	 * @param intent
	 */
	public static void launchLocalActivity(Intent intent){
		synchronized (activityOpt){
			if(intent != null){
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				XsqCommon.getInstance().startActivity(intent);
			}
		}
	}

	public static void launchTopActivity(Intent intent){
		synchronized (activityOpt){
			if(intent != null){
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				XsqCommon.getInstance().startActivity(intent);
			}
		}
	}
}
