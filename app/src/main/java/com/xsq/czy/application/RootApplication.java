package com.xsq.czy.application;

import android.app.Application;

import com.xsq.common.core.XsqCommon;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.util.Resource;

import cn.jpush.android.api.JPushInterface;

public class RootApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);     		// 初始化 JPush

		/** 设置值 */
		XsqCommon.initApp(getApplicationContext());
		/**设置网络请求URL*/
		HttpUtil.setUrlPrefix(Resource.URL + "app/android/");
		
	}

}
