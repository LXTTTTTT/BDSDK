package com.pancoit.mod_main.Global;


import android.media.AudioFormat;

// 项目使用的全局常量
public class Constant {

// 路由路径 ---------------------------------------------------------
    public static final String MESSAGE_ACTIVITY = "/main/activity/message";
    public static final String STATE_ACTIVITY = "/main/activity/state";
    public static final String MAP_ACTIVITY = "/main/activity/map";
    public static final String HEALTHY_ACTIVITY = "/main/activity/healthy";
    public static final String SOS_ACTIVITY = "/main/activity/sos";
    public static final String SETTING_ACTIVITY = "/main/activity/setting";
    public static final String VOICE_AUTH_ACTIVITY = "/main/activity/setting/voice_auth";
    public static final String COMPRESSION_RATE_ACTIVITY = "/main/activity/setting/compression_rate";
    public static final String PLATFORM_SETTING_ACTIVITY = "/main/activity/setting/platform_setting";
    public static final String SWIFT_MESSAGE_ACTIVITY = "/main/activity/setting/swift_message";
    public static final String ABOUT_US_ACTIVITY = "/main/activity/setting/about_us";
    public static final String COMMUNICATION_LINK_ACTIVITY = "/main/activity/setting/communication_link";
    public static final String CONNECT_BLUETOOTH_ACTIVITY = "/main/activity/connect_bluetooth";
    public static final String CONNECT_USB_HOST_ACTIVITY = "/main/activity/connect_usb_host";
    public static final String CONNECT_USB_ACCESSORY_ACTIVITY = "/main/activity/connect_usb_accessory";
    public static final String CONNECT_SERIAL_PORT = "/main/activity/connect_serial_port";


// 通用常量 ---------------------------------------------------------

    public static final String PLATFORM_IDENTIFIER = "110110110";  // 平台标识
    public static final int DEFAULT_PLATFORM_NUMBER = 15950044;  // 默认平台号码

    public static final String CONTACT_ID = "contact_id";  // 联系人唯一标识（卡号）
    public static final String NEW_CHAT = "new_chat";  // 新消息页面标识
    public static final String MAP_LONGITUDE = "map_longitude";  // 地图页经度
    public static final String MAP_LATITUDE = "map_latitude";  // 地图页纬度
    public static final String SOS_STATUS = "sos_status";  // sos紧急状态
    public static final String SOS_STATUS_OTHER = "其他";
    public static final String SOS_STATUS_LOST = "迷路";
    public static final String SOS_STATUS_FLOOD = "山洪";
    public static final String SOS_STATUS_FALL = "滑坠";
    public static final String SOS_STATUS_DAMAGED = "线路受损";
    public static final String SOS_STATUS_ROCKFALL = "落石";
    public static final String SOS_STATUS_ACCIDENT = "交通事故";
    public static final String SOS_STATUS_HYPOTHERMIA = "失温";
    public static final String SOS_STATUS_HEATSTROKE = "中暑";
    public static final String SOS_STATUS_SICKNESS = "高山病";
    public static final String SOS_STATUS_HEARTATTACK = "心脏病";
    public static final String SOS_STATUS_POISON = "中毒";
    public static final String BODY_STATUS = "body_status";  // 身体状况
    public static final String BODY_STATUS_GREAT = "良好";
    public static final String BODY_STATUS_WALK = "无法行走";
    public static final String BODY_STATUS_BLOOD = "失血过多";
    public static final String BODY_STATUS_HUNGRY = "饥渴饥饿";
    public static final String BODY_STATUS_INJURED = "皮外伤";

    public static int MESSAGE_TEXT = 1;  // 文本消息
    public static int MESSAGE_VOICE = 2;  // 语音消息

    public static int TYPE_SEND = 1;  // 发送消息
    public static int TYPE_RECEIVE = 2;  // 接收消息

    public static int STATE_SENDING = 20;  // 发送中
    public static int STATE_SUCCESS = 21;  // 发送成功
    public static int STATE_FAILURE = 22;  // 发送失败

    // 页面跳转resultcode
    public static int RESULT_CODE_EMERGENCY = 1;
    public static int RESULT_CODE_BODY = 2;

    // SharedPreferences 字符变量
    public static final String VOICE_COMPRESSION_RATE = "voice_compression_rate";  // 改变压缩码率
    public static final String SYSTEM_NUMBER = "system_number";  // 平台号码

    public static final String VO_ONLINE_ACTIVATION_KEY = "vo_online_activation_key";  // 语音在线激活key
    public static final String PIC_ONLINE_ACTIVATION_KEY = "pic_online_activation_key";  // 图片在线激活key
    public static final String VO_OFF_ACTIVATION_VALUE = "A90A411BDBF02DBEBV";  // 离线语音key
    public static final String PIC_OFF_ACTIVATION_VALUE = "A90A411BDBF02DBEBP";  // 离线图片key
    public static final String SWIFT_MESSAGE = "swift_message";  // 快捷消息
    public static final String SWIFT_MESSAGE_SYMBOL = "/lxt/";
    // 权限请求码
    public static final int REQUEST_CODE_LOCATION = 6660;

// 音频常量 ---------------------------------------------------------
    public static int sampleRateInHz = 8000;
    // CHANNEL_IN_STEREO 双声道，CHANNEL_CONFIGURATION_MONO 单声道
    public final static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public final static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public final static int channelCount = channelConfig == AudioFormat.CHANNEL_IN_STEREO ? 2:1;
    //比特率
    public final static int BIT_RATE = 64;
    //读取数据的最大字节数
    public final static int KEY_MAX_INPUT_SIZE =20 * 1024;
}
