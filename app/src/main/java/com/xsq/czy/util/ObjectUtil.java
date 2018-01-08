package com.xsq.czy.util;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by lan on 2016/4/22.
 */
public class ObjectUtil {

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || a != null && a.equals(b);
    }

    public static <T> boolean exist(T obj, List<T> list) {
        boolean exist = false;

        for(T object : list) {
            if(equal(obj, object)) {
                exist = true;
                break;
            }
        }

        return exist;
    }

    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if(reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

    public static void checkState(boolean expression, @Nullable Object errorMessage) {
        if(!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

}
