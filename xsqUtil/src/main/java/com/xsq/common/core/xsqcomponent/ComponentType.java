package com.xsq.common.core.xsqcomponent;

import com.xsq.common.core.IXsqComponent;

public enum ComponentType {
	DEFAULT(IXsqComponent.class),
	HTTP(IHttpComponent.class);
	
	private final Class<? extends IXsqComponent> clz;
	
	private ComponentType(Class<? extends IXsqComponent> clz){
		this.clz = clz;
	}

	public String getInterfaceName(){
		return clz.getName();
	}
	
	
	
}
