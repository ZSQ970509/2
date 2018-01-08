package com.xsq.common.core.xsqcomponent.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.xsq.common.core.IXsqComponent;
import com.xsq.common.core.XsqCommon;
import com.xsq.common.core.xsqcomponent.ComponentType;
import com.xsq.common.util.LogUtil;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

/** 监控应用全局异常组件
 * @author liuj
 *
 */
public class ApplicationExceptionListener implements IXsqComponent,UncaughtExceptionHandler{

	private UncaughtExceptionHandler defaultPro = Thread.getDefaultUncaughtExceptionHandler();

	private Context context;

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.w("xsqTip", "异常测试");
		handleException(t, e);
		defaultPro.uncaughtException(t, e);
	}
	
	// 程序异常处理方法
    private void handleException(Thread thread, Throwable ex) {
		StringBuilder errorHeaderInfo = new StringBuilder("\n");
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				errorHeaderInfo.append("versionName:").append(versionName).append("\n");
				errorHeaderInfo.append("versionCode:").append(versionCode).append("\n");
			}
		} catch (PackageManager.NameNotFoundException e) {
			LogUtil.error("handleException error.", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				errorHeaderInfo.append(field.getName()).append(":").append(field.get(null).toString()).append("\n");
			} catch (Exception e) {
				LogUtil.error("handleException error.", e);
			}
		}

		fields = Build.VERSION.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				errorHeaderInfo.append(field.getName()).append(":").append(field.get(null).toString()).append("\n");
			} catch (Exception e) {
				LogUtil.error("handleException error.", e);
			}
		}

		errorHeaderInfo.append("Exception info");
		LogUtil.logFatalToFile(errorHeaderInfo.toString(), ex);
    }
	
	@Override
	public void onAttach(XsqComponentInfo info) {
		this.context = info.context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void onDetach(XsqComponentInfo info) {
		Thread.setDefaultUncaughtExceptionHandler(defaultPro);
	}

	@Override
	public String getConponentName() {
		return this.getClass().getName();
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.DEFAULT;
	}


}
