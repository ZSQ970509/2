package com.xsq.common.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.xsq.common.core.xsqcomponent.impl.ApplicationExceptionListener;
import com.xsq.common.core.xsqcomponent.impl.ApplicationOkHttpComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XsqCommon {

	private static XsqCommon singleton = null;
	
	private Context appCtx = null;
	
	private Map<String, IXsqComponent> comList = null;
	
	private Handler h = null;
	
	public static void initApp(Context applicationCtx){
		singleton.appCtx = applicationCtx;
		singleton.innerRegisterComponent();
		singleton.h = new Handler();
	}
	
	public static XsqCommon getInstance(){
		Log.v("参数fffffff",":"+singleton);
		return singleton;
	}
	
	public Context getApplicationContext(){
		return appCtx;
	}
	
	public Context getCommonLibContext(){
		try {
			return appCtx.createPackageContext("com.xsq.common", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			Log.w("XsqTip", e);
		}
		return null;
	}
	
	public void startActivity(Intent intent){
		appCtx.startActivity(intent);
	}

	public boolean post(Runnable run){
		return  h.post(run);
	}

	public boolean postAtTime(Runnable r, long uptimeMillis){
		return  h.postAtTime(r, uptimeMillis);
	}

	public boolean postDelayed(Runnable r, long delayMillis){
		return h.postDelayed(r, delayMillis);
	}
	
	public void showToastMsg(final CharSequence msg,final int duration){
		h.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(appCtx, msg, duration).show();
			}
		});
	}
	
	public void showToastMsg(final int resId,final int duration){
		h.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(appCtx, resId, duration).show();
			}
		});
	}
	
	public boolean registerComponent(IXsqComponent component){
		return registerComponent(component,null);
	}
	
	public boolean registerComponent(IXsqComponent component,Object attachData){
		if(component!=null && component instanceof IXsqComponent && 
				component.getConponentName()!=null && component.getComponentType()!=null){
			IXsqComponent.XsqComponentInfo info = new IXsqComponent.XsqComponentInfo();
			info.componentName = component.getConponentName();
			info.context = appCtx;
			info.type = component.getComponentType();
			info.attachData = attachData;
			if(info.componentName!=null){
				if(comList.get(info.componentName)==null){
					comList.put(info.componentName, component);
					component.onAttach(info);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean ungisterComponent(String componentName){
		return ungisterComponent(componentName,null);
	}
	
	public boolean ungisterComponent(String componentName,Object attachData){
		if(componentName !=null){
			IXsqComponent component = comList.get(componentName);
			if(component!=null){
				comList.remove(componentName);
				IXsqComponent.XsqComponentInfo info = new IXsqComponent.XsqComponentInfo();
				info.componentName = component.getConponentName();
				info.context = appCtx;
				info.type = component.getComponentType();
				info.attachData = attachData;
				component.onDetach(info);
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IXsqComponent> T getRegistedComponent(Class<T> clz){
		T result;
		try {
			result = (T) getRegistedComponent(clz.getName());
			Log.v("参数fssssffsss",":"+clz.getName());
		} catch (Exception e) {
			result = null;
		}

		return result;
	}
	
	public IXsqComponent getRegistedComponent(String componentName){
		if(componentName == null) {
			return null;
		}
		Log.v("sd",":"+comList.get(componentName));
		return comList.get(componentName);
	}
	
	private void innerRegisterComponent(){
		singleton.registerComponent(new ApplicationExceptionListener());
//		singleton.registerComponent(new ApplicationHttpComponent());
		singleton.registerComponent(new ApplicationOkHttpComponent());
	}
	
	static{
		singleton = new XsqCommon();
		singleton.comList = Collections.synchronizedMap(new HashMap<String, IXsqComponent>());
	}
}
