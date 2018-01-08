package com.xsq.czy.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkApi {
    private static final String HOST_URL = "http://120.35.11.49:26969";// 正式
    private static final String BASE_URL = HOST_URL + "/api.ashx?";
    private static final String ACTION_LOGIN = "action=checklogin";

    public static void login(final String name, final String pwd, final String imei, final INetCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = BASE_URL + ACTION_LOGIN + "&Province=" + "&Name=" + name + "&Pwd=" + pwd
                        + "&module=outside" + "&Imei=";
                String result = HttpUtil.getFromUrl(url);

                if (TextUtils.isEmpty(result)) {
                    Log.e("login", "result null");
                    callback.onCallback(false, "result null");
                    return;
                }
                try {
                    JSONArray dataArray = new JSONArray(result);
                    JSONObject json = dataArray.getJSONObject(0);
                    if (json.getString("UserAccount").equals(name)) {
                        callback.onCallback(true, result);
                    } else {
                        callback.onCallback(false, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onCallback(false, e.getMessage());
                }
            }
        }).start();
    }

    public static String upLoadLockStatuc(String recondData, String blueToochName, String lockStatus) {
        try {

            String url = "http://120.35.11.49:26969/OpenInterface/TowerCraneService.ashx?action=UpdateLockStatus&recondData=" + recondData + "&blueToochName=" + blueToochName + "&lockStatus=" + lockStatus;
            /*JSONObject json = new JSONObject();
            json.put("recondData", recondData);
			json.put("blueToochName", blueToochName);
			json.put("lockStatus",lockStatus);*/
            String result = HttpUtil.getFromUrl(url);
            // //连续失败三次 返回2
            // if ("2".equals(new JSONObject(result).getString("result"))) {
            // return 2;
            // }
            //
            // //识别失败返回0
            // if (!"1".equals(new JSONObject(result).getString("result"))) {
            // return 0;
            // }
            // //识别失败返回1
            Log.e("zsq_result", result);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String InsertConstantDataForTowerCrane(String Account, String Imgstr, String Remark) {
        // TODO Auto-generated method stub
        try {

            String url = "http://120.35.11.49:26969/OpenInterface/TowerCraneService.ashx?action=InsertConstantDataForAppNew";
            JSONObject json = new JSONObject();
            json.put("Account", Account);
            json.put("Imgstr", Imgstr);
            json.put("Remark", Remark);
            String result = HttpUtil.postToUrl(url, json.toString());
            // //连续失败三次 返回2
            // if ("2".equals(new JSONObject(result).getString("result"))) {
            // return 2;
            // }
            //
            // //识别失败返回0
            // if (!"1".equals(new JSONObject(result).getString("result"))) {
            // return 0;
            // }
            // //识别失败返回1
            Log.e("zsq_result", result);
            return result;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static String IsHasBaseDataForTowerCrane(String Account) {
        // TODO Auto-generated method stub
        try {
            String url = "http://120.35.11.49:26969/OpenInterface/TowerCraneService.ashx?action=IsHasBaseData";
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("Account", Account));
            Log.e("zsq_url", url);
            Log.e("zsq_nvps", nvps.size() + "");
            Log.e("zsq_nvps", nvps.get(0).getValue());
            String result = HttpUtil.postToUrl(url, nvps);
            Log.e("zsq_result", result);
            // //连续失败三次 返回2
            // if ("2".equals(new JSONObject(result).getString("result"))) {
            // return 2;
            // }
            //
            // //识别失败返回0
            // if (!"1".equals(new JSONObject(result).getString("result"))) {
            // return 0;
            // }
            // //识别失败返回1
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String IsLockAndGetSecondsForTowerCrane(String name) {
        // TODO Auto-generated method stub
        try {
            String url = "http://120.35.11.49:26969/OpenInterface/TowerCraneService.ashx?action=IsLockAndGetSeconds";
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("Account", name));

            String result = HttpUtil.postToUrl(url, nvps);

            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static boolean InsertBaseForTowerCrane(String account, String imgstr) {

        try {

            String url = "http://120.35.11.49:26969/OpenInterface/TowerCraneService.ashx?action=InsertBase";
            JSONObject json = new JSONObject();
            json.put("Account", account);
            json.put("Imgstr", imgstr);
            String result = HttpUtil.postToUrl(url, json.toString());
            Log.e("zsq", result);
            if (!"1".equals(new JSONObject(result).getString("result"))) {
                return false;
            }

            return true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }


    public static String getUpdataInfo() {
        try {
            String url = "http://api.jsqqy.com/api.ashx?action=update&type=com.xsq.czy";
            String result = HttpUtil.postToUrl(url);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
