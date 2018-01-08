package com.xsq.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/21.
 */
public class JsonUtil {

    private final static Gson gson = new Gson();

    /**
     * 转换对接为json字节数据
     * @author liuj
     * @since 2015年5月14日 下午5:39:59
     *
     * @param data
     * @return
     */
    public static byte[] toBytes(Object data){
        return gson.toJson(data).getBytes();
    }

    /**
     * 转换对接为utf-8格式json字节数据
     * @author liuj
     * @since 2015年5月14日 下午5:40:34
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] toUtf8Bytes(Object data) throws UnsupportedEncodingException {
        return gson.toJson(data).getBytes("utf-8");
    }

    /**
     * 转换json字符串数据为java中map对象
     * @author liuj
     * @since 2015年5月14日 下午5:40:54
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, String> parseJsonToMap(String jsonStr){
        return gson.fromJson(jsonStr, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    /**
     * 转换json字符串数据为指定java类，生成java对象.
     * @author liuj
     * @since 2015年5月14日 下午5:41:33
     *
     * @param jsonStr
     * @param clz
     * @return
     */
    public static <T> T toObjectFromJsonStr(String jsonStr,Class<T> clz){
        return gson.fromJson(jsonStr, clz);
    }

    /**
     * 转换json字符串数据为指定java类，生成java对象
     * @author liuj
     * @since 2015年5月14日 下午5:42:15
     *
     * @param json
     * @param typeOfT
     * @return
     */
    public static <T> T toObjectFromJsonStr(String json, Type typeOfT){
        return gson.fromJson(json, typeOfT);
    }

    /**
     * 转换java对象为json字符串数据
     * @author liuj
     * @since 2015年5月14日 下午5:42:23
     *
     * @param data
     * @return
     */
    public static String toStringFromObject(Object data){
        return gson.toJson(data);
    }

}
