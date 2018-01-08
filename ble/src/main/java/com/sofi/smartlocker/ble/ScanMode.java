package com.sofi.smartlocker.ble;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by lan on 2017/2/28.
 */
@IntDef(flag=true, value = {ScanMode.LOW, ScanMode.HIGH})
@Retention(RetentionPolicy.SOURCE)
public @interface ScanMode {
    int LOW = 1;
    int HIGH = 2;
}
