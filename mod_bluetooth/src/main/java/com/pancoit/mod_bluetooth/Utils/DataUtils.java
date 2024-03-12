package com.pancoit.mod_bluetooth.Utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// 数据处理工具
public class DataUtils {

    public static final String GB18030 = "GB18030";

    public static String bytes2Hex(byte[] bytes){
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            String hexVaule = Integer.toHexString(value);
            if (hexVaule.length() < 2) {
                hexVaule = "0" + hexVaule;
            }
            hex += hexVaule;
        }
        return hex;
    }

    public static String hex2String(String hex) {
        byte[] bytes = hex2bytes(hex);
        return bytes2string(bytes);
    }

    public static byte[] hex2bytes(String hex) {
        if (hex == null || hex.length() < 1) {
            return null;
        }
        // 如果长度不是偶数，则前面补0
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        byte[] bytes = new byte[(hex.length() + 1) / 2];
        try {
            for (int i = 0, j = 0; i < hex.length(); i += 2) {
                byte hight = (byte) (Character.digit(hex.charAt(i), 16) & 0xff);
                byte low = (byte) (Character.digit(hex.charAt(i + 1), 16) & 0xff);
                bytes[j++] = (byte) (hight << 4 | low);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytes;
    }

    public static String bytes2string(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        String newStr = null;
        try {
            newStr = new String(bytes, GB18030).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newStr;
    }

}
