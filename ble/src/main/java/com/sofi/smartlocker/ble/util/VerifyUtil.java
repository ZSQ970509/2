package com.sofi.smartlocker.ble.util;

/**
 * Created by lan on 2016/5/27.
 */
public class VerifyUtil {

    public static final byte CMD_GET_BIKE = (byte)0x41;          //查询状态
    public static final byte CMD_OPEN_BIKE = (byte) 0x42;          //开锁
    public static final byte CMD_CLOSE_BIKE = (byte) 0x43;          //关锁
    public static final byte CMD_GET_TAMPER = (byte) 0x44;          //查询防拆
    public static final String DEFAULT_ERROR = "协议异常";
    public static final String DEFAULT_SEND_ERROR = "发送失败，请稍等或重启蓝牙";

    public static boolean isEmpty(byte[] data) {
        if(data == null || data.length == 0) {
            return true;
        }

        return false;
    }

    //验证是否绑定返回数据
    public static boolean verifyPkg(byte[] data) {
        if(data != null && data.length >= 5 && data[0] == 0x67 && data[1] == 0x74) {
            byte len = data[2];

            byte checkCode;
            if(len == 0) {
                checkCode = data[3];
            }
            else {
                byte[] clone = Decoder.byteCut(data, 3, len+1);
                checkCode = Decoder.checkCode(clone);
            }

            if(checkCode == data[data.length-1]) {
                return true;
            }
        }
        return false;
    }

    public static byte verfiyCmd(byte[] data) {
        return data[3];
    }

    public static byte[] verifyData(byte[] data) {
        int len = Decoder.ByteToInt(data[2]);

        return Decoder.byteCut(data, 4, len);
    }

}
