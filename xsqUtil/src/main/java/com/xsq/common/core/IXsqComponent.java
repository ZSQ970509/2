package com.xsq.common.core;

import com.xsq.common.core.xsqcomponent.ComponentType;

import android.content.Context;

public interface IXsqComponent {

	public String getConponentName();
	
	public ComponentType getComponentType();
	
	public void onAttach(XsqComponentInfo info);
	
	public void onDetach(XsqComponentInfo info);
	
	public static class XsqComponentInfo{
		public String componentName;
		public Context context;
		public ComponentType type;
		public Object attachData;
	}
}
