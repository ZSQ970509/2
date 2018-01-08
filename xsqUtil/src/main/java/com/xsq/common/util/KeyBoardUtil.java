package com.xsq.common.util;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xsq.common.core.XsqCommon;

/**
 * Created by Administrator on 2016/1/6 0006.
 */
public class KeyBoardUtil {

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     */
    public static void openKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) XsqCommon.getInstance().getApplicationContext()
                .getSystemService(XsqCommon.getInstance().getApplicationContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 打卡软键盘(切换界面时自动关闭)
     *
     * @param mEditText 输入框
     */
    public static void openKeybordAuto(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) XsqCommon.getInstance().getApplicationContext()
                .getSystemService(XsqCommon.getInstance().getApplicationContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     */
    public static void closeKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) XsqCommon.getInstance().getApplicationContext()
                .getSystemService(XsqCommon.getInstance().getApplicationContext().INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 复制到剪切板
     * @param content
     */
    @TargetApi(11)
    public static void coptyToClipBoard( String content) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager)XsqCommon.getInstance().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", content);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)XsqCommon.getInstance().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(content);
        }
    }
}
