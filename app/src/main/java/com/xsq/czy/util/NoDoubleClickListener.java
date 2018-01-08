package com.xsq.czy.util;

import android.view.View;

/**
 * Created by xc on 2017/6/26.
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    public static final int DELAY = 10000;  //连击事件间隔
    private long lastClickTime = 0; //记录最后一次时间

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {  //判断时间差
            lastClickTime = currentTime;  //记录最后一次点击时间
            onNoDoubleClick(v);
        }
    }

    //抽象一个无连击事件方法，用于实现内容
    public abstract void onNoDoubleClick(View v);
}