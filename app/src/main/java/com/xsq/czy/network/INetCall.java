package com.xsq.czy.network;

public interface INetCall<T> {
	public void onCallback(boolean value, T result);
}
