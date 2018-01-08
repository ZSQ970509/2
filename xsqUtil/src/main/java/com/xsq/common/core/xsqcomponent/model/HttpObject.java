package com.xsq.common.core.xsqcomponent.model;

import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class HttpObject {
	
	public static enum RequestType{
		GET("get"),
		POST("post"),
		POST_BYTE_STREAM("post_byte_stream");
		
		private String method;
		
		private RequestType(String method){
			this.method = method;
		}
		
		public String getRequestMethod(){
			return method;
		}
	}
	
	public static enum ResultType{
		/**
		 *  调用成功
		 */
		Success("成功"),
		
		/**
		 * 字符编码错误
		 */
		ErrorUnsupportEncode("不支持编码异常"),
		
		/**
		 * 连接服务器异常
		 */
		ErrorConnect("连接服务器异常"),
		
		/**
		 * 无法连接服务器错误
		 */
		ErrorHttpHostConnect("无法连接上服务器"),
		
		/**
		 * 服务器网络超时
		 */
		ErrorSocketTimeOut("服务器网络超时"),
		
		/**
		 * 连接超时错误
		 */
		ErrorConnectTimeOut("服务器连接超时"),
		
		/**
		 * 应用协议错误
		 */
		ErrorClientProto("应用协议错误"),
		
		/**
		 * IO错误
		 */
		ErrorIO("数据IO异常"),
		
		/**
		 * 客户端数据处理异常
		 */
		ErrorDataProcess("客户端数据处理异常"),
		
		/**
		 * 其他可能的未知错误
		 */
		ErrorOther("未知错误");
		
		private String message;
		
		private ResultType(String message){
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
		
	}
	
	public static class Operator{
		public Object httpMethod;
		public boolean cancel = false;
		
		public Operator(Object httpMethod){
			this.httpMethod = httpMethod;
		}
	}

	private RequestType requestType;
	private String url;
	private Map<String, Object> params;
	private Object bindData;
	private Object result;
	private Object customResult;
	private boolean isResultBytes = false;
	private ResultType resultType = ResultType.Success;
	private Object resultDetail;
	private int resultCode = -1;
	private Operator operator;
	private volatile boolean isFinish = false;

	public HttpObject(){
		this.requestType = RequestType.GET;
	}
	
	public HttpObject(String url,Map<String, Object> params){
		this.requestType = RequestType.GET;
		this.url = url;
		this.params = params;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Object getBindData() {
		return bindData;
	}

	public void setBindData(Object bindData) {
		this.bindData = bindData;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	public Object getCustomResult() {
		return customResult;
	}

	public void setCustomResult(Object customResult) {
		this.customResult = customResult;
	}
	
	public boolean isResultBytes() {
		return isResultBytes;
	}

	public void setResultBytes(boolean isResultBytes) {
		this.isResultBytes = isResultBytes;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public Object getResultDetail() {
		return resultDetail;
	}

	public void setResultDetail(Object resultDetail) {
		this.resultDetail = resultDetail;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public boolean isFinish() {
		return isFinish;
	}
	
	public void abortRequest(){
		isFinish = true;
		if(operator!=null){
			operator.cancel = true;
			Object httpMethod = operator.httpMethod;
			if(httpMethod!=null){
				if(httpMethod instanceof HttpGet){
					HttpGet methmod = (HttpGet) httpMethod;
					methmod.abort();
				}else if(httpMethod instanceof HttpPost){
					HttpPost methmod = (HttpPost) httpMethod;
					methmod.abort();
				}
			}
		}
	}
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
