package com.xsq.common.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author liuj
 * @date 2014.9.18 created
 */
public abstract class BaseActivity extends Activity {

	/** 获取当前根view
	 * @return
	 */
	public View getSelf(){
		return findViewById(android.R.id.content);
	}
	
	/** 获取当前layout的view
	 * @return
	 */
	public View getSelfContainer(){
		View v = getSelf();
		if(v != null){
			if(((ViewGroup)v).getChildCount() == 1){
				v = ((ViewGroup)v).getChildAt(0);
				return v;
			}
		}
		return null;
	}
	
	/** activity初始化函数
	 * @param savedInstanceState
	 */
	protected abstract void onActivityInit(Bundle savedInstanceState);
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			// Translucent status bar
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		onActivityInit(savedInstanceState);
		
		
	}

	
}
