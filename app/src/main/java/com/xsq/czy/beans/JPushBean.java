package com.xsq.czy.beans;

/**
 * Created by Administrator on 2017/5/21.
 */
public class JPushBean {

    private String msg;     //消息类型 0：无多余消息。1：普通消息 2：人脸识别消息；
    private String photo;   //现场照片URL
    private String origin;  //识别底图URL

    public String getMsg() {
        return msg;
    }

    public String getPhoto() {
        return photo;
    }

    public String getOrigin() {
        return origin;
    }
}
