package com.pancoit.mod_parse.Utils;

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

    public static String string2Hex(String str) {
        String hex;
        try {
            byte[] bytes = string2bytes(str, GB18030);
            hex = bytes2Hex(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return hex.toUpperCase();
    }

    public static byte[] string2bytes(String str, String charset) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return str.getBytes(charset);
    }

    public static byte char2HexByte(char chr) {
        byte chrRet = -1;
        if (chr >= '0' && chr <= '9') {
            chrRet = (byte) chr;
        } else if (chr >= 'A' && chr <= 'F') {
            chrRet = (byte) (chr - 65 + 10);
        } else if (chr >= 'a' && chr <= 'f') {
            chrRet = (byte) (chr - 97 + 10);
        }
        return chrRet;
    }


    public static String getCheckCode0007(String strProtocol) {
        strProtocol.replace(" ",  "");
        byte chrCheckCode = 0;
        for (int i = 0; i < strProtocol.length(); i += 2) {
            char chrTmp ;
            chrTmp = strProtocol.charAt(i);
            if (chrTmp == ' ') continue;
            byte chTmp1 = (byte) (DataUtils.char2HexByte(chrTmp) << 4);
            chrTmp = strProtocol.charAt(i + 1);
            byte chTmp2 = (byte) (chTmp1 + (DataUtils.char2HexByte(chrTmp) & 15));
            chrCheckCode = i == 0 ? chTmp2 : (byte) (chrCheckCode ^ chTmp2);
        }
        String strHexCheckCode = String.format("%x", Byte.valueOf(chrCheckCode));
        if ((strHexCheckCode = strHexCheckCode.toUpperCase()).length() != 2) {
            if (strHexCheckCode.length() > 2) {
                strHexCheckCode = strHexCheckCode.substring(strHexCheckCode.length() - 2);
            } else if (strHexCheckCode.length() < 2 && strHexCheckCode.length() > 0) {
                strHexCheckCode = "0" + strHexCheckCode;
            }
        }
        return strHexCheckCode;
    }

    public static double analysisLonlat(String value){
        double lonlat = Double.valueOf(value);
        int dd = (int)lonlat / 100;
        int mm = (int)lonlat % 100;
        double ms = lonlat - (int)lonlat;
        return dd+((mm+ms)/60.0);
    }

    // 打包，加上 $ 和 * 和 校验和，输出 hex_str
    public static String packaging(String tmp){
        String hexCommand = DataUtils.string2Hex(tmp);
        String hh = DataUtils.getCheckCode0007(hexCommand).toUpperCase();  // 检验和
        return "24"+hexCommand+"2A"+ DataUtils.string2Hex(hh)+"0D0A";
    }

}
