package com.xsq.common.core.xsqcomponent.util;

import java.io.IOException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

public class CustomHttpRequestExecutor extends HttpRequestExecutor {

	public CustomHttpRequestExecutor(){
		
	}

	@Override
	protected HttpResponse doSendRequest(HttpRequest request,
			HttpClientConnection conn, HttpContext context) throws IOException,
			HttpException {
	    if(request == null)
	    	throw new IllegalArgumentException("HTTP request is null");
	    if(conn == null)
	    	throw new IllegalArgumentException("Client connection is null");
	    if(context == null)
	    	throw new IllegalArgumentException("HTTP context is null");

	    HttpResponse response = null;
	    
	    context.setAttribute("http.connection", conn);
	    context.setAttribute("http.request_sent", Boolean.FALSE);
	    
	    conn.sendRequestHeader(request);
	    if ((request instanceof HttpEntityEnclosingRequest))
	    {
	        boolean sendentity = true;
	        ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
	        
	        if ((((HttpEntityEnclosingRequest)request).expectContinue()) && (!ver.lessEquals(HttpVersion.HTTP_1_0)))
	        {
	            conn.flush();
	            
	            if (conn.isResponseAvailable(3000)) {
	            	response = conn.receiveResponseHeader();
	            	
	            	if (canResponseHaveBody(request, response)) {
	            		conn.receiveResponseEntity(response);
	            	}
	            	
	            	int status = response.getStatusLine().getStatusCode();
	            	if (status < 200) {
	            		if (status != 100) {
	            			throw new ProtocolException("Unexpected response: " + response.getStatusLine());
	            		}
	            		
	            		response = null;
	            	}else{
	            		sendentity = false;
	            	}
	            }
	            
	        }else{
//	        	conn.flush();
//	        	
//	        	if (conn.isResponseAvailable(3000)) {
//	            	response = conn.receiveResponseHeader();
//	            	
//	            	if (canResponseHaveBody(request, response)) {
//	            		conn.receiveResponseEntity(response);
//	            	}
//	            	
//	            	int status = response.getStatusLine().getStatusCode();
//	            	response = null;
//	        	}
	        }
	        
	        if (sendentity) {
	        	conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
	        }
	    }
	    
	    conn.flush();
	    context.setAttribute("http.request_sent", Boolean.TRUE);
		return response;
	}
	
	
}
