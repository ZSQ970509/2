package com.xsq.common.core.xsqcomponent.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.xsq.common.core.exception.NoFoundHttpProtocolException;
import com.xsq.common.core.xsqcomponent.ComponentType;
import com.xsq.common.core.xsqcomponent.IHttpComponent;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.core.xsqcomponent.model.HttpObject.RequestType;
import com.xsq.common.core.xsqcomponent.model.HttpObject.ResultType;
import com.xsq.common.core.xsqcomponent.util.CustomHttpClient;
import com.xsq.common.core.xsqcomponent.util.CustomInputStreamEntity;

/** Http协议应用组件
 * @author liuj
 *
 */
public class ApplicationHttpComponent implements IHttpComponent {

	private ExecutorService executeProvider = null; 
	private Handler handle = null;
	private String sessionId = null;
	private String authName = null;
	private String authValue = null;
	private HttpClient httpclient;
	
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
		executeProvider = Executors.newFixedThreadPool(2);
		handle = new Handler(Looper.getMainLooper());
		SchemeRegistry schemeRegistry = new SchemeRegistry();  
		schemeRegistry.register(  
		         new Scheme("http", PlainSocketFactory.getSocketFactory(),80));  
		schemeRegistry.register(  
		         new Scheme("https", SSLSocketFactory.getSocketFactory(),443)); 
		BasicHttpParams httpParams = new BasicHttpParams();
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(5); 
		ConnManagerParams.setMaxTotalConnections(httpParams, 50);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		ConnManagerParams.setTimeout(httpParams, 30000);
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		
		httpclient = new CustomHttpClient(connectionManager,httpParams);
	}

	@Override
	public void onDetach(XsqComponentInfo info) {
		executeProvider.shutdown();
		handle = null;
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
				
				if (fParams != null && fParams.size() > 0) {
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
				
				String finalUrl = requestUrl.substring(0, requestUrl.length()-1);
				
				// 创建一个GET请求
				request = new HttpGet(finalUrl);
			}
			else if(httpObj.getRequestType() == RequestType.POST){
				// 创建一个POST请求
				request = new HttpPost(fUrl);
				
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
				request = new HttpPost(fUrl);
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
				inEntity.isRepeatable();
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
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		}  catch (SocketException e) {
			ResultType rt = ResultType.ErrorConnect;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		}  catch (SocketTimeoutException e){
			//data timeout
			ResultType rt = ResultType.ErrorSocketTimeOut;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		} catch (ClientProtocolException e) {
			ResultType rt = ResultType.ErrorClientProto;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		} catch (ConnectTimeoutException e) {
			ResultType rt = ResultType.ErrorConnectTimeOut;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		}catch (IOException e) {
			ResultType rt = ResultType.ErrorIO;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		} catch (Throwable e){
			ResultType rt = ResultType.ErrorOther;
			httpObj.setResultType(rt);
			httpObj.setResultDetail(e);
			httpclient.getConnectionManager().closeIdleConnections(6, TimeUnit.SECONDS);
		}finally{
			if(operator!=null){
				if(operator.cancel){
					bCancel = true;
				}else{
					httpObj.abortRequest();
				}
			}
			
			request.abort();//fix by liuj in 2015-1-20
			httpObj.setOperator(null);
			
			if(fcb!=null){
				if(bSucess){
					try {
						fcb.onDataRoutine(httpObj);
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
			executeProvider.execute(r);
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForPost(HttpObject httpObject,resultCallBack cb)
	{
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
			executeProvider.execute(r);
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForBytes(HttpObject httpObject,long contentLenth,
			resultCallbackForByte cb) {
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
			executeProvider.execute(r);
			return true;
		}
		return false;
	}

	@Override
	public boolean postRequestForInputStram(HttpObject httpObject,
			InputStream in,long contentLenth, resultCallbackForInputStream cb) {
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
			executeProvider.execute(r);
			return true;
		}
		return false;
	}

}
