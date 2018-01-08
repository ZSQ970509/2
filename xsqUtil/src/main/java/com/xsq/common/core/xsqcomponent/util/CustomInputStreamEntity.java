package com.xsq.common.core.xsqcomponent.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import org.apache.http.entity.InputStreamEntity;

public class CustomInputStreamEntity extends InputStreamEntity {

	public static class NullInputStream extends InputStream{

		@Override
		public int read() throws IOException {
			return 0;
		}
		
	}
	
	public CustomInputStreamEntity(long length) {
		super(new NullInputStream(),length);
	}
	
	public CustomInputStreamEntity(InputStream instream, long length) {
		super(instream, length);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		if(outstream == null)
			throw new IllegalArgumentException("Output stream may not be null");
		
		final InputStream instream = getContent();
		
		byte[] buffer = new byte[2048];
		long remaining = getContentLength();
		try {
			if(instream instanceof NullInputStream){
				ByteBuffer byteBuff = ByteBuffer.wrap(buffer);
				while (remaining > 0L) {
					byteBuff.clear();
					int l = onSend(byteBuff, (int)Math.min(2048L, remaining));
					if (l == -1 || l == 0) {
						break;
					}
					outstream.write(buffer, 0, l);
					remaining -= l;
					onSent(getContentLength() - remaining,getContentLength());
				}
			}else{
				while (remaining > 0L) {
					int l = instream.read(buffer, 0, (int)Math.min(2048L, remaining));
					if (l == -1) {
						break;
					}
					outstream.write(buffer, 0, l);
					remaining -= l;
					onSent(getContentLength() - remaining,getContentLength());
				}
			}
		}finally{
			if(instream!=null){
				instream.close();
			}
			onComplete(getContentLength() - remaining);
		}
	}
	
	public int onSend(ByteBuffer buffer,int length) throws IOException{
		return -1;
	}
	
	public void onSent(long sentSize,long sentTotal){
		
	}

	public void onComplete(long sentSize) throws IOException{
		
	}
}
