package com.sofi.smartlocker.ble.util;

/**
 * Created by lan on 2016/5/25.
 */
public class CmdUtil {
    public static final int CMD_GET_BIKE = 1;
    public static final int CMD_OPEN_BIKE = 2;
    public static final int CMD_CLOSE_BIKE = 3;
    public static final int CMD_GET_TAMPER = 4;

    /**
     * 按格式组装数据
     * @param code 功能码
     * @param data 数据
     * @return 组装好的数据
     */
    private static byte[] assemebleData(byte code, byte[] data) {
        byte[] head = new byte[3];
        head[0] = 0x67;
        head[1] = 0x74;
        int len = data != null ? data.length : 0;
        head[2] = Decoder.intToByte(len);

        byte[] pkg = null;
        if(len > 0) {
            pkg = Decoder.byteMerger(code, data);
            byte checkCode = Decoder.checkCode(pkg);

            pkg = Decoder.byteMerger(pkg, checkCode);
        }
        else {
            byte checkCode = code;
            pkg = Decoder.byteMerger(code, checkCode);
        }

        pkg = Decoder.byteMerger(head, pkg);

        return pkg;
    }

    /**
     * 获取状态命令0x81
     */
    public static byte[] getBike() {
        byte code = (byte) 0x81;

        return assemebleData(code, null);
    }

    /**
     * 开锁命令0x82
     */
    public static byte[] openBike() {
        byte code = (byte) 0x82;

        return assemebleData(code, null);
    }

    /**
     * 关锁命令0x83
     */
    public static byte[] closeBike() {
        byte code = (byte) 0x83;

        return assemebleData(code, null);
    }

    /**
     * 查询锁防拆开关命令0x84
     */
    public static byte[] getTamper() {
        byte code = (byte) 0x84;

        return assemebleData(code, null);
    }

}
