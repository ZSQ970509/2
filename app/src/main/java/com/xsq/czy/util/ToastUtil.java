package com.xsq.czy.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 让Toast存在时不再重复显示
 * @author Administrator
 */
public class ToastUtil {
	/**
	 * 
	 * @param context使用时的上下文
	 * @param hint
	 *            在提示框中需要显示的文本
	 * @return 返回一个不会重复显示的toast
	 * */
	private static Toast mToast;
	public static void show(Context ctx, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

}