package com.xsq.common.util;

import com.xsq.common.core.XsqCommon;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class ApplicationInfoUtil {

	private UserInfo userInfo;

	public class UserInfo{

		private PreferencesUtil userInfoPref = new PreferencesUtil("UserInfo", Context.MODE_PRIVATE);
		
		private UserInfo(){

		}
		
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		private void setPropVal(String key,Object value){
			if("username".equals(key) || "password".equals(key) || 
					"authCode".equals(key) || "session".equals(key)){
				if(value!=null){
					userInfoPref.setPropVal(key, (String) value);
				}else{
					userInfoPref.removeProp(key);
				}
			}
		}
		
		public boolean passLogin(){
			if(getAuthCode()!=null && getSession()!=null){
				return true;
			}
			return false;
		}

		public String getNetAddress(){
			return userInfoPref.getPropVal("netAddress", null);
		}

		public UserInfo setNetAddress(String address){
			userInfoPref.setPropVal("netAddress",  address);
			return this;
		}
		
		public String getUsername() {
			return userInfoPref.getPropVal("username", null);
		}
		public UserInfo setUsername(String username) {
			setPropVal("username",username);
			return this;
		}
		public String getPassword() {
			return userInfoPref.getPropVal("password", null);
		}
		public UserInfo setPassword(String password) {
			setPropVal("password",password);
			return this;
		}
		public String getAuthCode() {
			return userInfoPref.getPropVal("authCode", null);
		}
		public UserInfo setAuthCode(String authCode) {
			setPropVal("authCode",authCode);
			return this;
		}
		public String getSession() {
			return userInfoPref.getPropVal("session", null);
		}
		public UserInfo setSession(String session) {
			setPropVal("session",session);
			return this;
		}
	}

	public UserInfo getUserInfo(){
		return userInfo;
	}
	
	private ApplicationInfoUtil(){
		userInfo = new UserInfo();
	}
	
	public static ApplicationInfoUtil getApplicationInfo(){
		return singleton;
	}

	private static ApplicationInfoUtil singleton = new ApplicationInfoUtil();
	
}
