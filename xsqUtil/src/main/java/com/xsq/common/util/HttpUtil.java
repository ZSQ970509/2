package com.xsq.common.util;

import android.util.Log;

import com.xsq.common.core.XsqCommon;
import com.xsq.common.core.xsqcomponent.IHttpComponent;
import com.xsq.common.core.xsqcomponent.IHttpComponent.resultCallBack;
import com.xsq.common.core.xsqcomponent.IHttpComponent.resultCallbackForByte;
import com.xsq.common.core.xsqcomponent.IHttpComponent.resultCallbackForInputStream;
import com.xsq.common.core.xsqcomponent.model.HttpObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class HttpUtil {

	private static IHttpComponent http = null;

	//private static String urlPrefix = "http://120.35.11.49:8088/WebBackend/app/android/";
	private static String urlPrefix = "http://tkgl.jsqqy.com:8088/WebBackend/app/android/";
	
	private static String authCodeName = "authCode";
	
	public static String getUrlPrefix() {
		return urlPrefix;
	}

	public static void setUrlPrefix(String urlPrefix) {
		HttpUtil.urlPrefix = urlPrefix;
	}
	
	private static String getFullUrlPath(String method){
		StringBuilder urlFull = new StringBuilder(urlPrefix);
		urlFull.append(method);
		urlFull.append(".invoke");
		return urlFull.toString();
	}
	
	/** http异步json返回结果调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param cb 结果回调
	 * @return true成功,否则false.
	 */
	public static boolean executeRequestForJsonResult(String method,Map<String, Object> params,ResultEventJson<?> cb){
		return executeRequest(method,params,cb);
	}
	
	/** http异步json返回结果调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param cb 结果回调
	 * @return HttpObject.
	 */
	public static HttpObject executeRequestForJsonResultEx(String method,Map<String, Object> params,ResultEventJson<?> cb){
		return executeRequestEx(method,params,cb);
	}
	
	/** http异步调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param cb 结果回调
	 * @return true成功,否则false
	 */
	public static boolean executeRequest(String method,Map<String, Object> params,ResultEvent cb){
		if(urlPrefix!=null && method!=null){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);
			return postRequestForPost(httpObj,cb);
		}
		return false;
	}
	
	/** http异步调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param cb 结果回调
	 * @return HttpObject
	 */
	public static HttpObject executeRequestEx(String method,Map<String, Object> params,ResultEvent cb){
		if(urlPrefix!=null && method!=null){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);

			if(postRequestForPost(httpObj,cb)){
				return httpObj;
			}
		}
		return null;
	}

	/** http字节流上传调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param contentLenth 总发送尺寸
	 * @param cb 发送与结果回调
	 * @return true成功,否则失败.
	 */
	public static boolean executeBytesRequest(String method,Map<String, Object> params,long contentLenth,resultCallbackForByte cb){
		if(urlPrefix!=null && method!=null && contentLenth>0){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);
			return postRequestForBytes(httpObj, contentLenth, cb);
		}
		return false;
	}
	
	/** http字节流上传调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param contentLenth 总发送尺寸
	 * @param cb 发送与结果回调
	 * @return HttpObject.
	 */
	public static HttpObject executeBytesRequestEx(String method,Map<String, Object> params,long contentLenth,resultCallbackForByte cb){
		if(urlPrefix!=null && method!=null && contentLenth>0){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);
			if(postRequestForBytes(httpObj, contentLenth, cb)){
				return httpObj;
			}
		}
		return null;
	}
	
//	public static boolean executeBytesRequest(String method,Map<String, Object> params,long contentLenth,BytesResultEvent cb){
//		return executeBytesRequest(method,params,contentLenth,(resultCallbackForByte)cb);
//	}
	
	/** http字节流上传调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param in 输入流
	 * @param contentLenth 发送尺寸
	 * @param cb 发送与结果回调
	 * @return true成功,否则false.
	 */
	public static boolean executeStreamRequest(String method,Map<String, Object> params,InputStream in,long contentLenth,resultCallbackForInputStream cb){
		if(urlPrefix!=null && method!=null && in!=null && contentLenth>0){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);
			return postRequestForInputStream(httpObj, in, contentLenth, cb);
		}
		return false;
	}
	
	/** http字节流上传调用.
	 * @param method 方法名
	 * @param params 参数
	 * @param in 输入流
	 * @param contentLenth 发送尺寸
	 * @param cb 发送与结果回调
	 * @return HttpObject.
	 */
	public static HttpObject executeStreamRequestEx(String method,Map<String, Object> params,InputStream in,long contentLenth,resultCallbackForInputStream cb){
		if(urlPrefix!=null && method!=null && in!=null && contentLenth>0){
			HttpObject httpObj = new HttpObject(getFullUrlPath(method),params);
			Log.d("sss", "dddd: " + httpObj.getUrl());
			if(postRequestForInputStream(httpObj, in, contentLenth, cb)){
				return httpObj;
			}
		}
		return null;
	}
	
//	public static boolean executeStreamRequest(String method,Map<String, Object> params,InputStream in,long contentLenth,StreamResultEvent cb){
//		return executeStreamRequest(method,params,in,contentLenth,(resultCallbackForInputStream)cb);
//	}

	/** get方式http调用.
	 * @param httpObject
	 * @param cb
	 * @return
	 */
	public static boolean postRequestForGet(HttpObject httpObject, resultCallBack cb){
		return getHttpComponent().postRequestForGet(httpObject, cb);
	}
	
	/** post方式http调用。
	 * @param httpObject
	 * @param cb
	 * @return
	 */
	public static boolean postRequestForPost(HttpObject httpObject, resultCallBack cb){
		Log.v("参数11",":"+httpObject.getUrl());
		Log.v("参数12",":"+httpObject.getParams());
		Log.v("参数13",":"+cb);
		return getHttpComponent().postRequestForPost(httpObject, cb);
	}
	
	/** 字节流上传http调用.
	 * @param httpObject
	 * @param contentLenth
	 * @param cb
	 * @return
	 */
	public static boolean postRequestForBytes(HttpObject httpObject,long contentLenth,resultCallbackForByte cb){
		return getHttpComponent().postRequestForBytes(httpObject, contentLenth, cb);
	}
	
	/** 流形式上传http调用.
	 * @param httpObject
	 * @param in
	 * @param contentLenth
	 * @param cb
	 * @return
	 */
	public static boolean postRequestForInputStream(HttpObject httpObject,InputStream in,long contentLenth,resultCallbackForInputStream cb){
		return getHttpComponent().postRequestForInputStram(httpObject, in, contentLenth, cb);
	}
	
	public static void setAuthCode(String authCode, String value){
		getHttpComponent().setHeadAuthCode(authCode, value);
	}
	
	public static void setAuthCodeValue(String value){
		setAuthCode(authCodeName,value);
	}
	
	public static void setUserSession(String session){
		getHttpComponent().setUserSession(session);
	}
	
	public static String getUserSession(){
		return getHttpComponent().getUserSession();
	}
	
	public static abstract class ResultEvent implements resultCallBack{
		
		public void onExceptionRoutine(HttpObject httpObject){};
		
		public void onCancelRoutine(HttpObject httpObject){};
	}
	
	public static abstract class ResultEventJson<T> extends ResultEvent{

		private int httpCode = -1;
		private HttpObject httpResultObj = null;
		private Object attObj = null;
		public void onDataRoutine(T resultObj){};
		public abstract void onUIRoutine(T resultObj);
		
		@Override
		public final void onDataRoutine(HttpObject httpObject) {
			if(httpObject.getResult() == null)
				return;

			httpCode = httpObject.getResultCode();
			httpResultObj = httpObject;
			if(httpObject.isResultBytes() == false){
				String result = (String) httpObject.getResult();
				Class<T> entityClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
				T retObj = JsonUtil.toObjectFromJsonStr(result, entityClass);
				attObj = retObj;
				onDataRoutine(retObj);
			}

		}

		@Override
		public final void onUIRoutine(HttpObject httpObject) {
			if(attObj!=null){
				onUIRoutine((T) attObj);
			}
			attObj = null;
		}

		public HttpObject getHttpObject() {
			return httpResultObj;
		}
		
		public int getHttpResult(){
			return httpCode;
		}
		
	}

	public static abstract class StreamResultEvent extends ResultEvent implements resultCallbackForInputStream{

		@Override
		public void onSent(long sentSize, long sentTotal){};

		@Override
		public void onComplete(long sentSize) throws IOException {};
		
	}
	
	public static abstract class BytesResultEvent extends StreamResultEvent implements resultCallbackForByte{
		
	}
	
	private static IHttpComponent getHttpComponent(){
		if(http == null){

			http =XsqCommon.getInstance().getRegistedComponent(IHttpComponent.class);
		}

		return http;
	}
}
