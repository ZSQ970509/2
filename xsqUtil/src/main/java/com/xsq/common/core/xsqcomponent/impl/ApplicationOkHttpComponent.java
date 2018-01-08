package com.xsq.common.core.xsqcomponent.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.apache.OkApacheClient;
import com.xsq.common.core.exception.NoFoundHttpProtocolException;
import com.xsq.common.core.xsqcomponent.ComponentType;
import com.xsq.common.core.xsqcomponent.IHttpComponent;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.core.xsqcomponent.model.HttpObject.RequestType;
import com.xsq.common.core.xsqcomponent.model.HttpObject.ResultType;
import com.xsq.common.core.xsqcomponent.util.CustomInputStreamEntity;

/** Http协议应用组件
 * @author liuj
 *
 */
public class ApplicationOkHttpComponent implements IHttpComponent {

	private ExecutorService executeProvider = null; 
	private ExecutorService processProvider = null; 
	private BlockingQueue<Runnable> executeQueue = null;
	private Handler handle = null;
	private String sessionId = null;
	private String authName = null;
	private String authValue = null;
	private OkApacheClient httpclient;
	private OkHttpClient okClient;
	
	@Override
	public String getConponentName() {
		return IHttpComponent.class.getName();
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.HTTP;
	}
	
	@Override
	public void setHeadAuthCode(String authCode, String value) {
		authName = authCode;
		authValue = value;
	}
	
	@Override
	public void setUserSession(String session) {
		sessionId = session;
	}

	@Override
	public String getUserSession() {
		return sessionId;
	}

	@Override
	public void onAttach(XsqComponentInfo info) {
		final int maxNetConcurrent = 2;//最大网络并发
		final int maxProcessConcurrent = 4;//最大处理并发
		executeQueue = new ArrayBlockingQueue<Runnable>(10);
		executeProvider = new ThreadPoolExecutor(maxNetConcurrent, maxNetConcurrent, 0, TimeUnit.MILLISECONDS,
				executeQueue);
		processProvider = Executors.newFixedThreadPool(maxProcessConcurrent);
		handle = new Handler(Looper.getMainLooper());
		httpclient = new OkApacheClient();
		okClient = httpclient.getOkClient();
		okClient.setConnectTimeout(10, TimeUnit.SECONDS);
		okClient.setReadTimeout(10, TimeUnit.SECONDS);
		okClient.setWriteTimeout(10, TimeUnit.SECONDS);
	}

	@Override
	public void onDetach(XsqComponentInfo info) {
		executeProvider.shutdown();
		executeProvider = null;
		executeQueue = null;
		processProvider.shutdown();
		processProvider = null;
		handle = null;
		httpclient = null;
		okClient = null;
		//executeProvider = null;
	}
	
	private void executeRequest(final HttpObject httpObj,final resultCallBack fcb){
		executeRequest(httpObj,fcb,-1);
	}
	
	private void executeRequest(final HttpObject httpObj,final resultCallBack fcb,long requestLenth){
		executeRequest(httpObj,fcb,null,requestLenth);
	}
	
	private void executeRequest(final HttpObject httpObj,final resultCallBack fcb,InputStream in,long requestLenth){
		String fUrl = httpObj.getUrl();
		Map<String, Object> fParams = httpObj.getParams();
		
		StringBuilder requestUrl = new StringBuilder(fUrl);
		boolean bSucess = false;
		boolean bCancel = false;
		HttpObject.Operator operator = null;
		HttpRequestBase request = null;
		try {
			if(httpObj.isFinish()){
				bCancel = true;
				return;
			}
			
			if(httpObj.getRequestType() == RequestType.GET){
				
				int delta = 0;
				if (fParams != null && fParams.size() > 0) {
					delta = 1;
					int index = requestUrl.indexOf("?");
					if (index == -1) {
						requestUrl.append("?");
					} else {
						requestUrl.append("&");
					}
					for (@SuppressWarnings("rawtypes") Map.Entry entry : fParams.entrySet()) {
						requestUrl
						.append((String) entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue().toString(),"utf-8"))
						.append("&");
					}
				}
				
				String finalUrl = requestUrl.substring(0, requestUrl.length() - delta);
				
				// 创建一个GET请求
				request = new HttpGet(finalUrl){

					private AtomicBoolean isAbort = new AtomicBoolean(false);
					@Override
					public void abort() {
						if(isAbort.compareAndSet(false, true)){
							okClient.getDispatcher().cancel(this);
						}
					}
					
				};
			}
			else if(httpObj.getRequestType() == RequestType.POST){
				// 创建一个POST请求
				request = new HttpPost(fUrl){

					private AtomicBoolean isAbort = new AtomicBoolean(false);
					@Override
					public void abort() {
						if(isAbort.compareAndSet(false, true)){
							okClient.getDispatcher().cancel(this);
						}
					}
					
				};
				
				if(fParams!=null && fParams.size()>0){
					List<NameValuePair> formParams = new ArrayList<NameValuePair>();
					
					for(String arg : fParams.keySet()){
						NameValuePair postElement = new BasicNameValuePair(arg,String.valueOf(fParams.get(arg)));
						formParams.add(postElement);
					}
					
					
					UrlEncodedFormEntity contentEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
					((HttpPost)request).setEntity(contentEntity);
				}
				
			}
			else if(httpObj.getRequestType() == RequestType.POST_BYTE_STREAM){
				request = new HttpPost(fUrl){

					private AtomicBoolean isAbort = new AtomicBoolean(false);
					@Override
					public void abort() {
						if(isAbort.compareAndSet(false, true)){
							okClient.getDispatcher().cancel(this);
						}
					}
					
				};
				InputStream ins = (in == null)?new CustomInputStreamEntity.NullInputStream():in;
				CustomInputStreamEntity inEntity = new CustomInputStreamEntity(ins,requestLenth){

					@Override
					public int onSend(ByteBuffer buffer, int length) throws IOException {
						return ((resultCallbackForByte)fcb).onSend(buffer, length);
					}

					@Override
					public void onSent(long sentSize, long sentTotal) {
						((resultCallbackForInputStream)fcb).onSent(sentSize, sentTotal);
					}

					@Override
					public void onComplete(long sentSize) throws IOException{
						((resultCallbackForInputStream)fcb).onComplete(sentSize);
					}
					
				};
				inEntity.setContentType("binary/octet-stream");
				if(fParams!=null && fParams.size()>0){
					request.addHeader("AttachStream-Data", new Gson().toJson(fParams));
				}
				((HttpPost)request).setEntity(inEntity);
			}
			else{
				throw new NoFoundHttpProtocolException();
			}
			if(authName!=null && authValue!=null){
				request.addHeader(authName, authValue);
			}
			
			// 创建 HttpParams 设置 HTTP 参数
			//HttpParams httpParams = new BasicHttpParams();
			// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
			//HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
			//HttpConnectionParams.setSoTimeout(httpParams, 15 * 1000);
			//HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			//HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			//HttpProtocolParams.setUseExpectContinue(httpParams, true);
			//ConnManagerParams.setTimeout(httpParams, 10 * 1000);
			
			// 创建一个默认的HttpClient
			//HttpClient httpclient = new DefaultHttpClient(httpParams);
			//HttpClient httpclient = new CustomHttpClient(httpParams);

			//httpclient.getConnectionManager().closeIdleConnections(10, TimeUnit.SECONDS);
			
			if(sessionId!=null){
				request.addHeader("Cookie", "JSESSIONID=" + sessionId);
			}
			// 支持的语言控制，XT800在3G网络下不加下面语句会丢包
			request.addHeader("Accept-Language", "zh-CN, en-US");
			request.addHeader("Cache-Control", "no-cache");
//			request.addHeader("Pragma", "no-cache");
			
			operator = new HttpObject.Operator(request);
			httpObj.setOperator(operator);
			if(httpObj.isFinish()){
				bCancel = true;
				return;
			}
			
			HttpResponse httpresponse = httpclient.execute(request);
			int statusCode = httpresponse.getStatusLine().getStatusCode();
			httpObj.setResultCode(statusCode);
			for (Header h : httpresponse.getAllHeaders()) {
				if (h.getName().equals("Set-Cookie")){
					StringTokenizer st = new StringTokenizer(h.getValue(), ";");
					if (st.countTokens() != 0) {
						StringTokenizer st1 = new StringTokenizer(
								st.nextToken(), "=");
						if (st1.countTokens() == 2) {
							st1.nextToken();
							sessionId = st1.nextToken();
						}
					}
					break;
				}
			}
			

			HttpEntity entity = httpresponse.getEntity();
			String contentType = null;
			if(entity.getContentType()!=null){
				contentType = entity.getContentType().getValue();
			}

			if(contentType!=null && contentType.contains("binary/octet-stream")){
				if(fcb instanceof resultReadProcessCallback){
					InputStream readIn = entity.getContent();
					if(readIn!=null){
						try {
							((resultReadProcessCallback)fcb).onReadData(readIn,entity.getContentLength(),httpObj);
						} catch (Throwable e){
							request.abort();
							throw e;
						}
						finally{
							readIn.close();
						}
					}
				}else{
					byte[] result = EntityUtils.toByteArray(entity);
					httpObj.setResultBytes(true);
					httpObj.setResult(result);
				}
			}else{
				String result = EntityUtils.toString(entity);
				httpObj.setResultBytes(false);
				httpObj.setResult(result);
			}

			bSucess = true;
			
		} catch (UnsupportedEncodingException e) {
			ResultType rt = ResultType.ErrorUnsupportEncode;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		} catch (HttpHostConnectException e){
			//refuse connect
			ResultType rt = ResultType.ErrorHttpHostConnect;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		} catch (SocketException e) {
			ResultType rt = ResultType.ErrorConnect;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		}catch (SocketTimeoutException e){
			//data timeout
			ResultType rt = ResultType.ErrorSocketTimeOut;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		} catch (ClientProtocolException e) {
			ResultType rt = ResultType.ErrorClientProto;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		} catch (ConnectTimeoutException e) {
			ResultType rt = ResultType.ErrorConnectTimeOut;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		}catch (IOException e) {
			ResultType rt = ResultType.ErrorIO;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		} catch (Throwable e){
			ResultType rt = ResultType.ErrorOther;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
		}finally{
			if(operator!=null){
				if(operator.cancel){
					bCancel = true;
				}else{
					//httpObj.abortRequest();
				}
			}
			
			//request.abort();//fix by liuj in 2015-1-20
			httpObj.setOperator(null);
			
			if(fcb!=null){
				if(bSucess){
					final ExecutorService processService = this.processProvider;
					if(processService!=null){
						final HttpObject httpObjArg =  httpObj;
						processService.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									fcb.onDataRoutine(httpObjArg);
									if(handle!=null){
										handle.post(new Runnable() {
											
											@Override
											public void run() {
												fcb.onUIRoutine(httpObj);
											}
										});
									}
								} catch (Throwable e) {
									ResultType rt = ResultType.ErrorDataProcess;
									httpObj.setResultType(rt);
									httpObj.setResultDetail(e);
									if(handle!=null){
										handle.post(new Runnable() {
											
											@Override
											public void run() {
												fcb.onExceptionRoutine(httpObj);
											}
										});
									}
								}
							}
						});
					}
				}else{
					if(bCancel){
						if(handle!=null){
							handle.post(new Runnable() {
								
								@Override
								public void run() {
									fcb.onCancelRoutine(httpObj);
								}
							});
						}
					}else{
						if(handle!=null){
							handle.post(new Runnable() {
								
								@Override
								public void run() {
									fcb.onExceptionRoutine(httpObj);
								}
							});
						}
					}
				}
			}
		}
	}

	@Override
	public boolean postRequestForGet(HttpObject httpObject,resultCallBack cb)
	{	
		final ExecutorService executeProvider = this.executeProvider;
		if(executeProvider!=null && httpObject!=null && httpObject.isFinish()!=true){
			final HttpObject httpObj = httpObject;
			final resultCallBack fcb = cb;
			httpObj.setRequestType(RequestType.GET);
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					executeRequest(httpObj,fcb);
				}
			};
			try {
				executeProvider.execute(r);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForPost(HttpObject httpObject,resultCallBack cb)
	{
		final ExecutorService executeProvider = this.executeProvider;
		if(executeProvider!=null  && httpObject!=null && httpObject.isFinish()!=true){
			final HttpObject httpObj = httpObject;
			final resultCallBack fcb = cb;
			httpObj.setRequestType(RequestType.POST);
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					executeRequest(httpObj,fcb);
				}
			};
			try {
				executeProvider.execute(r);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForBytes(HttpObject httpObject,long contentLenth,
			resultCallbackForByte cb) {
		final ExecutorService executeProvider = this.executeProvider;
		if(executeProvider!=null  && httpObject!=null && httpObject.isFinish()!=true && contentLenth>0){
			final HttpObject httpObj = httpObject;
			final resultCallbackForByte fcb = cb;
			final long lenth = contentLenth;
			httpObj.setRequestType(RequestType.POST_BYTE_STREAM);
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					executeRequest(httpObj,fcb,lenth);
				}
			};
			try {
				executeProvider.execute(r);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForInputStram(HttpObject httpObject,
			InputStream in,long contentLenth, resultCallbackForInputStream cb) {
		final ExecutorService executeProvider = this.executeProvider;
		if(executeProvider!=null  && httpObject!=null && httpObject.isFinish()!=true &&
				in!=null && contentLenth>0){
			final HttpObject httpObj = httpObject;
			final resultCallbackForInputStream fcb = cb;
			final InputStream ins = in;
			final long lenth = contentLenth;
			httpObj.setRequestType(RequestType.POST_BYTE_STREAM);
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					executeRequest(httpObj,fcb,ins,lenth);
				}
			};
			try {
				executeProvider.execute(r);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

}
