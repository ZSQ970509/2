package com.xsq.common.core.exception;

public class NoFoundHttpProtocolException extends BaseCustomException {

	public NoFoundHttpProtocolException() {
		super("不能发现匹配调用http协议使用的方法类型");
	}
	
}
