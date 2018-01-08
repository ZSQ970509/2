package com.xsq.common.core.xsqcomponent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import com.xsq.common.core.IXsqComponent;
import com.xsq.common.core.xsqcomponent.model.HttpObject;

public interface IHttpComponent extends IXsqComponent {

	/** 为http增加自定义头代码
	 * @param authCode
	 */
	public void setHeadAuthCode(String authCode,String value);
	
	/** 设置用户登陆sessionId
	 * @param session
	 */
	public void setUserSession(String session);
	
	/** 获得当前缓存的sessionId
	 * @return
	 */
	public String getUserSession();
	
	/** 异步请求http数据，使用get方法
	 * @param httpObject 
	 * @param cb 回调接口
	 * @return true成功发出请求命令,否则false
	 * <br/>
	 * 注意:返回true不代表数据已发出到服务器
	 */
	public boolean postRequestForGet(HttpObject httpObject,resultCallBack cb);
	
	/** 异步请求http数据，使用post方法
	 * @param httpObject 
	 * @param cb 回调接口
	 * @return true成功发出请求命令,否则false
	 * <br/>
	 * 注意:返回true不代表数据已发出到服务器
	 */
	public boolean postRequestForPost(HttpObject httpObject,resultCallBack cb);
	
	public boolean postRequestForBytes(HttpObject httpObject,long contentLenth ,resultCallbackForByte cb);
	
	public boolean postRequestForInputStram(HttpObject httpObject,InputStream in,long contentLenth,resultCallbackForInputStream cb);
	
	public static interface resultCallBack{
		
		/** 数据逻辑处理回调函数，在本函数内可处理高cpu流程，本函数内不允许处理UI界面操作
		 * @param httpObject
		 */
		public void onDataRoutine(HttpObject httpObject);
		
		/** UI处理回调函数,本函数会onDataRoutine函数处理完成后调用，在在本函数内处理UI界面更新操作
		 * @param httpObject
		 */
		public void onUIRoutine(HttpObject httpObject);
		
		/** 异常处理函数,当访问网络发生异常时会回调此函数,使用getResultType()检查错误类型.
		 * @param httpObject
		 */
		public void onExceptionRoutine(HttpObject httpObject);
		
		/** 取消调用回调函数,当用户取消对未完成的请求的网络调用时将回调该函数.
		 * @param httpObject
		 */
		public void onCancelRoutine(HttpObject httpObject);
	}
	
	public static interface resultCallbackForInputStream extends resultCallBack{
		
		/** 上传过程进度状态回调函数
		 * @param sentSize 已发送尺寸
		 * @param sentTotal 总发送尺寸
		 */
		public void onSent(long sentSize,long sentTotal);
		
		/** 传输结束时回调函数（可能是异常结束）
		 * @param sentSize 成功发送的数据尺寸
		 * @throws IOException
		 */
		public void onComplete(long sentSize) throws IOException;
	}
	
	public static interface resultCallbackForByte extends resultCallbackForInputStream{
		
		/** 循环发送数据尺寸
		 * @param data 数据缓存
		 * @param availableSize 缓存的最大尺寸
		 * @return 本次发送数据长度
		 */
		public int onSend(ByteBuffer data,int availableSize) throws IOException;
	}
	
	public static interface resultReadProcessCallback{
		
		/** 循环接受数据接口
		 * @param in 输入流数据
		 * @param length 数据尺寸。（当为-1时表示未确定）
		 * @param httpObject 
		 * @throws Throwable
		 */
		public void onReadData(InputStream in,long length,HttpObject httpObject) throws Throwable;
	}

}
