package com.pancoit.mod_main.Global;


import android.os.CountDownTimer;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.util.Arrays;
import java.util.List;

// 项目使用的全局变量
public class Variable {
    private static String TAG = "Variable";

    public static boolean isARouterInit = false;

    public static int getSystemNumber(){return MMKV.defaultMMKV().decodeInt(Constant.SYSTEM_NUMBER,Constant.DEFAULT_PLATFORM_NUMBER);}
    public static int getCompressRate(){return MMKV.defaultMMKV().decodeInt(Constant.VOICE_COMPRESSION_RATE,666);}

    public static String getSwiftMsg(){return MMKV.defaultMMKV().decodeString(Constant.SWIFT_MESSAGE, "");}
    public static void setSwiftMsg(String commands){MMKV.defaultMMKV().encode(Constant.SWIFT_MESSAGE, commands);}
    public static void addSwiftMsg(String command){
        String commands = command+Constant.SWIFT_MESSAGE_SYMBOL +getSwiftMsg();
        setSwiftMsg(commands);
    }
    public static void removeSwiftMsg(int position){
        String commands_str = getSwiftMsg();
        String new_commands = "";
        Log.e(TAG, "拿到快捷消息: "+commands_str);
        if(commands_str==null||commands_str.equals("")){return;}
        String[] commands_arr = commands_str.split(Constant.SWIFT_MESSAGE_SYMBOL);
        List<String> commands_list = Arrays.asList(commands_arr);
        for (int i = 0; i < commands_list.size(); i++) {
            if(i==position){continue;}
            new_commands += commands_list.get(i)+Constant.SWIFT_MESSAGE_SYMBOL;
        }
        setSwiftMsg(new_commands);
    }

    public static String getKey(){return MMKV.defaultMMKV().decodeString(Constant.VO_ONLINE_ACTIVATION_KEY,"");}
    public static void setKey(String key){MMKV.defaultMMKV().encode(Constant.VO_ONLINE_ACTIVATION_KEY,key);}
    public static boolean isVoiceOnline(){
        String voKey = getKey();
        Log.e(TAG, "当前保存的语音key: "+voKey );
        return !"".equals(voKey);
    }


}
