package com.xsq.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.xsq.common.core.XsqCommon;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/1/6 0006.
 */
public class PreferencesUtil {

    private SharedPreferences pref;
    private Map<String, Object> caches = new ConcurrentHashMap<>();

    public PreferencesUtil(String name, int mode) {
        pref = XsqCommon.getInstance().getApplicationContext().getSharedPreferences(name, mode);
    }

    public PreferencesUtil(String name) {
        this(name, Context.MODE_PRIVATE);
    }

    /**
     * 设置属性配置
     *
     * @param key
     * @param value
     */
    public void setPropVal(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        synchronized (this) {
            Object oriVal = null;
            if (value != null) {
                try {
                    oriVal = caches.put(key, value);
                    editor.putString(key, value);
                    editor.apply();
                } catch (Exception e) {
                    if (oriVal != null) {
                        caches.put(key, oriVal);
                    }
                    editor.commit();
                    LogUtil.debug("setPropVal error.", e);
                }
            } else {
                try {
                    oriVal = caches.remove(key);
                    editor.remove(key);
                    editor.apply();
                } catch (Exception e) {
                    if (oriVal != null) {
                        caches.put(key, oriVal);
                    }
                    editor.commit();
                    LogUtil.debug("setPropVal error.", e);
                }
            }

        }

    }

    /**
     * 获取属性配置
     *
     * @param key
     * @return
     */
    public String getPropVal(String key) {
        return getPropVal(key, null);
    }

    /**
     * 获取属性配置
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public String getPropVal(String key, String defaultVal) {
        String result = (String) caches.get(key);

        if (result == null) {
            synchronized (this) {
                result = (String) caches.get(key);

                if (result == null) {
                    result = pref.getString(key, defaultVal);

                    if (result != null) {
                        caches.put(key, result);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 移除属性配置
     *
     * @param key
     */
    public void removeProp(String key) {
        setPropVal(key, null);
    }

    /**
     * 移除所有属性配置
     */
    public void clearProp() {
        SharedPreferences.Editor editor = pref.edit();
        synchronized (this) {
            caches.clear();
            editor.clear();
            editor.apply();
        }
    }

    /**
     * 判断是否存在属性配置
     *
     * @param key
     * @return
     */
    public boolean containProp(String key) {
        return getPropVal(key, null) != null;
    }

    /**
     * 获取所有属性配置
     *
     * @return
     */
    public Map<String, String> getAllPropVal() {
        Map<String, String> result = new HashMap<>();
        synchronized (this) {
            Map<String, ?> prefSet = pref.getAll();

            for (String key : prefSet.keySet()) {
                String propVal = getPropVal(key);

                if (propVal != null) {
                    result.put(key, propVal);
                }
            }
        }

        return result;
    }
}
