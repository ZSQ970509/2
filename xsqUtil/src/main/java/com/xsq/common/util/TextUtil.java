package com.xsq.common.util;

import android.content.res.Resources.NotFoundException;
import android.widget.Toast;

import com.xsq.common.core.XsqCommon;

public class TextUtil {

	/**
	 * 获取文本信息内容从资源文件
	 * @param id 资源id
	 * @return 文本内容
	 */
	public static CharSequence getText(int id){
		return XsqCommon.getInstance().getApplicationContext().getResources().getText(id);
	}

	/**
	 * 获取文本信息内容从资源文件
	 * @param id 资源id
	 * @param def 默认内容
	 * @return 文本内容
	 */
	public static CharSequence getText(int id, CharSequence def) {
		return XsqCommon.getInstance().getApplicationContext().getResources().getText(id, def);
	}
	
	public static CharSequence getFormatText(int id,Object... args){
		return String.format(getText(id).toString(), args);
	}

	/**
	 * 显示提示信息窗口提示信息
	 * @param msg 信息内容
	 * @param duration 时间
	 */
	public static void showMessage(CharSequence msg,int duration){
		XsqCommon.getInstance().showToastMsg(msg, duration);
	}

	/**
	 * 显示提示信息窗口提示信息
	 * @param resId 信息资源id
	 * @param duration 时间
	 */
	public static void showMessage(final int resId,final int duration){
		XsqCommon.getInstance().showToastMsg(resId, duration);
	}

	/**
	 * 短时间显示提示信息窗口提示信息
	 * @param msg 信息内容
	 */
	public static void showMessageForShort(CharSequence msg){
		showMessage(msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 短时间显示提示信息窗口提示信息
	 * @param resId 信息资源id
	 */
	public static void showMessageForShort(final int resId){
		showMessage(resId, Toast.LENGTH_SHORT);
	}

	/**
	 * 长时间显示提示信息窗口提示信息
	 * @param msg 信息内容
	 */
	public static void showMessageForLong(CharSequence msg){
		showMessage(msg, Toast.LENGTH_LONG);
	}

	/**
	 * 长时间显示提示信息窗口提示信息
	 * @param resId 信息资源id
	 */
	public static void showMessageForLong(final int resId){
		showMessage(resId, Toast.LENGTH_LONG);
	}

}
