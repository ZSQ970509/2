package com.sofi.smartlocker.ble.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by lan on 2016/5/25.
 */
public class Decoder {
    private static final String TAG = Decoder.class.getSimpleName();
    private static final String ENCODE = "GBK";

    public static String byte2HexStr(byte b) {
        String stmp = Integer.toHexString(b & 0xFF);
        return (stmp.length() == 1) ? "0" + stmp : stmp;
    }
    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte[]数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            sb.append(byte2HexStr(b[n]));
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        try {
            src = src.replaceAll("0x", "");
            src = src.replaceAll(" ", "");
            int m = 0, n = 0;
            int l = src.length() / 2;
            // System.out.println(l);
            byte[] ret = new byte[l];
            for (int i = 0; i < l; i++) {
                m = i * 2 + 1;
                n = m + 1;
                ret[i] = (byte) (Integer.parseInt(
                        src.substring(i * 2, m) + src.substring(m, n), 16));
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.E("sean", "^^^^^:" + e.toString());
            return new byte[1];
        }
    }

    /**
     * 数字字符串转换为Byte值
     *
     * @param src 数字字符串
     * @return byte[]
     */
    public static String intStr2Bytes(String src) {
        Long value = 0L;
        try {
            value = Long.valueOf(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
        return Long.toHexString(value);
    }

    /**
     * 把12位数字转换成6位BCD码
     */
    public static byte[] int2BCD2(String str, int len) {
        if (str.length() < len) {
            int cha = len - str.length();
            for (int i = 0; i < cha; i++) {
                str = "0" + str;
            }
        } else if (str.length() > len) {
            str = str.substring(0, len);
        }

        byte[] ret = str2Bcd(str);

        return ret;
    }

    /**
     * 合并 两个byte数据
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] byteMerger(byte[] b1, byte[] b2) {
        byte[] b = new byte[b1.length + b2.length];

        try {
            System.arraycopy(b1, 0, b, 0, b1.length);
            System.arraycopy(b2, 0, b, b1.length, b2.length);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return b;
    }
    /**
     * 合并 两个byte数据
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] byteMerger(byte[] b1, byte b2) {
        byte[] b = new byte[b1.length + 1];

        try {
            System.arraycopy(b1, 0, b, 0, b1.length);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        b[b1.length] = b2;

        return b;
    }
    /**
     * 合并 两个byte数据
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] byteMerger(byte b1, byte[] b2) {
        byte[] b = new byte[b2.length + 1];

        try {
            b[0] = b1;
            System.arraycopy(b2, 0, b, 1, b2.length);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return b;
    }
    /**
     * 合并 两个byte数据
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] byteMerger(byte b1, byte b2) {
        byte[] b = new byte[2];

        b[0] = b1;
        b[1] = b2;

        return b;
    }
    /**
     * 截取byte数组指定长度
     * @param b1
     * @param start
     * @param len
     * @return
     */
    public static byte[] byteCut(byte[] b1, int start, int len) {
        try {
            byte[] b = new byte[len];
            System.arraycopy(b1, start, b, 0, len);
            return b;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将byte数据转换为255之内的int类型
     * @param b byte数据
     * @return 生成int
     */
    public static int ByteToInt(byte b) {
        return b;
    }

    /**
     * 将255之内的int类型的数据转换为byte
     * @param n int数据
     * @return 生成的byte数组
     */
    public static byte intToByte(int n) {
        String hexStr = Integer.toHexString(n);
        String str = Decoder.str2Len3(hexStr, 2);
        byte[] iByte = Decoder.hexStr2Bytes(str);

        return iByte[0];
    }

    /**
     * 将int类型的数据转换为byte数组 原理：将int数据中的四个byte取出，分别存储
     *
     * @param n   int数据
     * @param len 长度，不能大于4
     * @return 生成的byte数组
     */
    public static byte[] intToBytes(int n, int len) {
        byte[] ret = new byte[len];
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        for (int i = 0; i < len; i++) {
            ret[i] = b[4 - len + i];
        }
        return ret;
    }

    /**
     * 把字符串转换成相应位数
     */
    public static String str2Len(String str, int len) {
        if (str.length() < len) {
            int cha = len - str.length();
            for (int i = 0; i < cha; i++) {
                str = str + 'F';
            }
        } else if (str.length() > len) {
            str = str.substring(0, len);
        }

        return str;
    }

    /**
     * 把字符串转换成相应位数
     */
    public static String str2Len2(String str, int len) {
        if (str.length() < len) {
            int cha = len - str.length();
            for (int i = 0; i < cha; i++) {
                str = str + ' ';
            }
        } else if (str.length() > len) {
            str = str.substring(0, len);
        }

        return str;
    }


    /**
     * 把字符串转换成相应位数
     */
    public static String str2Len3(String str, int len) {
        if (str.length() < len) {
            int cha = len - str.length();
            for (int i = 0; i < cha; i++) {
                str = "0" + str;
            }
        } else if (str.length() > len) {
            str = str.substring(0, len);
        }

        return str;
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        try {
            int len = asc.length();
            int mod = len % 2;
            if (mod != 0) {
                asc = "0" + asc;
                len = asc.length();
            }

            if (len >= 2) {
                len = len / 2;
            }
            byte bbt[] = new byte[len];
            byte abt[]  = asc.getBytes();
            int j, k;
            for (int p = 0; p < asc.length() / 2; p++) {
                if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                    j = abt[2 * p] - '0';
                } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                    j = abt[2 * p] - 'a' + 0x0a;
                } else {
                    j = abt[2 * p] - 'A' + 0x0a;
                }

                if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                    k = abt[2 * p + 1] - '0';
                } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                    k = abt[2 * p + 1] - 'a' + 0x0a;
                } else {
                    k = abt[2 * p + 1] - 'A' + 0x0a;
                }
                int a = (j << 4) + k;
                byte b = (byte) a;
                bbt[p] = b;
            }
            return bbt;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.E("sean", "^^^^^:" + e.toString());
            return new byte[6];
        }
    }
    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static byte[] double2Byte(double data, int len) {
        String str = String.valueOf(data);

        if (str.length() < len) {
            int cha = len - str.length();
            for (int i = 0; i < cha; i++) {
                str = "0" + str;
            }
        } else if (str.length() > len) {
            str = str.substring(0, len);
        }

        return str.getBytes();
    }

    public static byte[] str2Gbk(String str, int len) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes(ENCODE);
            int diff = bytes.length - len;

            if(diff > 0) {
                return byteCut(bytes, 0, len);
            }
            else if(diff < 0) {
                String diffStr = str2Len("", -diff);
                String wholeStr = str + diffStr;

                return wholeStr.getBytes(ENCODE);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static byte[] str2Gbk2(String str, int len) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes(ENCODE);
            int diff = bytes.length - len;

            if(diff > 0) {
                return byteCut(bytes, 0, len);
            }
            else if(diff < 0) {
                String diffStr = str2Len2("", -diff);
                String wholeStr = str + diffStr;

                return wholeStr.getBytes(ENCODE);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static String gbk2Str(byte[] bytes) {
        String str = "";

        try {
            str = new String(bytes, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 把byte[]内字符，从第一个依次异或到最后一个
     *
     * @param data
     * @return
     */
    public static byte checkCode(byte[] data) {
        byte b = 0x00;
        try {
            int len = data.length;
            for (int i = 0; i < len; i++) {
                b = (byte) (b ^ data[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sean", "^^^^^:" + e.toString());
        }

        return b;
    }
    /**
     * 交易记录号解码格式化处理
     *
     * @param tradeNo
     * @return
     */
    public static String decodeTradeNo(String tradeNo) {
        String id = tradeNo.substring(0, 8);
        byte[] bb = Decoder.hexStr2Bytes(tradeNo.substring(8, tradeNo.length()));
        id = id + new String(bb);

        return id;
    }

    /**
     * 判断是否全0异常字符串
     *
     * @param str
     * @return
     */
    public static boolean isExceStr(String str) {
        if (!StringUtils.isEmpty(str)) {
            String reg = "0";
            boolean exec = true;
            for (int i =0; i<str.length(); i++) {
                if(str.charAt(i) != '0') {
                    exec = false;
                    break;
                }
            }

            return exec;
        }
        return false;
    }

    public static int randomInt(int range) {
        Random rand = new Random();

        return rand.nextInt(range);        //int范围类的随机数
    }
}
