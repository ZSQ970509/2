package com.xsq.common.util;

import com.xsq.common.core.XsqCommon;

/**
 * Created by Administrator on 2016/1/11.
 */
public class UIEventUtil {

    public static boolean post(Runnable run){
        return XsqCommon.getInstance().post(run);
    }

    public static boolean postAtTime(Runnable r, long uptimeMillis){
        return XsqCommon.getInstance().postAtTime(r, uptimeMillis);
    }

    public static boolean postDelayed(Runnable r, long delayMillis){
        return XsqCommon.getInstance().postDelayed(r, delayMillis);
    }

}
