package com.sofi.smartlocker.ble.util;

import java.text.DecimalFormat;

/**
 * Created by lan on 2016/4/22.
 */
public class StringUtils {

    public static String REG = "elev:";
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

    public static boolean isEmpty(String str) {
        return str == null || str.equalsIgnoreCase("");
    }

    public static boolean checkLen(String str, int len) {
        return str != null && str.length() == len;
    }

    public static String getBikeName(String name) {
        if (!isEmpty(name)) {
            String str = "";
            int index = name.indexOf(REG);

            if (index >= 0) {
                int start = index + REG.length();
                str = name.substring(start);
            } else {
                str = name;
            }

            if(str.length() == 9){
                return str;
            }else if (str.length() == 13) {
                return str.substring(0, 9);
            }
            else if (str.length() == 14) {
                return str.substring(0, 10);
            }
            else {
                return "";
            }
        }
        return "";
    }

    public static String getBikeVol(String name) {
        if (!isEmpty(name)) {
            String str = "";
            int index = name.indexOf(REG);

            if (index >= 0) {
                int start = index + REG.length();
                str = name.substring(start);
            } else {
                str = name;
            }

            String vol = "";

            if (str.length() == 13) {
                vol = str.substring(10, 13);
            }
            else if (str.length() == 14) {
                vol = str.substring(11, 14);
            }

            if (!isEmpty(vol)) {
                Integer iVolumn = Integer.parseInt(vol);//电压
                return decimalFormat.format(iVolumn / 100F);//format 返回的是字符串
            }
        }
        return "";
    }

}
