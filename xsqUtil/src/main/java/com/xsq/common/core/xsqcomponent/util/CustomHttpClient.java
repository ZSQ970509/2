package com.xsq.common.core.xsqcomponent.util;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpRequestExecutor;

public class CustomHttpClient extends DefaultHttpClient {

	public CustomHttpClient(HttpParams params) {
		super(params);
	}
	
	public CustomHttpClient() {
		super();
	}

	public CustomHttpClient(ClientConnectionManager conman, HttpParams params) {
		super(conman, params);
	}

	@Override
	protected HttpRequestExecutor createRequestExecutor() {
		return new CustomHttpRequestExecutor();
	}

	
}
