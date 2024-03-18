package com.pancoit.mod_parse.Protocol;

import android.util.Log;

import com.pancoit.mod_parse.Utils.DataUtils;

// 协议封装
public class ProtocolPackager {

// 标准北斗 -----------------------------------------------------------------------------------
    /**
     * 查询 IC 信息：
     * @param type 指令类型 0：检测本机IC信息 1：检测本机编组信息 2：检测下属用户 3：检测IC模块工作模式
     * @param info 下属用户信息帧 当指令类型等于0，3时，下属用户信息帧号填00
     * @return string_hex
     */
    public static String CCICR(int type, String info) {
        String command = "CCICR," + type + "," + info;
        return DataUtils.packaging(command);
    }

    /**
     * 通信申请：
     * @param cardNumber 目标卡号
     * @param type 发送模式：1、汉字 2、代码 3、混合 4、压缩汉字 5、压缩代码
     * @param content 消息内容
     * 1.编码类别为“1”时，传输内容为计算机内码，每个汉字16bit，高位在前；
     * 2.编码类别为“2”时，传输内容为ASCII码字符，如代码8以ASCII码字符‘8′(HEX38)表示；
     * 3.当编码类别为“3”时，传输内容为汉字代码混合，输出的BCD码起始位“A4”
     * @return string_hex
     */
    public static String CCTCQ(String cardNumber, int type, String content){
        String command = "CCTCQ," + cardNumber + ",2,1," + type + "," + content + ",0";
        return DataUtils.packaging(command);
    }

    /**
     * 开关指令：
     * @param instruce 目标语句
     * @param mode 操作：1关闭指定语句，2打开指定语句，3关闭全部语句，4打开全部语句  3-4目标语句为空
     * @param fre 输出频度
     * @return string_hex
     */
    public static String CCRMO(String instruce, int mode, int fre) {
        String command = (mode == 1 || mode == 3)? ("CCRMO,"+instruce+"," + mode + ","):("CCRMO,"+instruce+"," + mode + "," + fre);
        return DataUtils.packaging(command);
    }

// 自定义 -----------------------------------------------------------------------------------
    // XY ----------------------------------------------------------------
    /**
     * 登录
     * @param type
     * 0-设置不需要登录
     * 1-设置需要登录
     * 2-修改密码
     * 3-限制RD
     * 4-接触限制
     * 5-登录设备
     * 0,1,3,4 不需要密码参数，5登录设备时，如果设备为不需要登录时反馈登录成功。
     * @param password 密码：六位数字或者英文字母，默认密码为：000020
     * @return string_hex
     */
    public static String CCPWD(int type, String password) {
        String command;
        if(type==2 || type==5){
            command = "CCPWD,"+type+","+password;
        } else {
            command = "CCPWD,"+type;
        }
        return DataUtils.packaging(command);
    }

    /**
     * 版本查询
     * @return string_hex
     */
    public static String CCVRQ() {
        String command = "CCVRQ,";
        return DataUtils.packaging(command);
    }

    /**
     * 设备重启
     * @param mode 复位模式：2 RDSS复位，3 RNSS复位，4 系统复位（无外电则关机） 5 网络重启
     * @return string_hex
     */
    public static String CCRST(int mode) {
        String command = "CCRST,"+mode;
        return DataUtils.packaging(command);
    }

    /**
     * 恢复出厂设置
     * @return string_hex
     */
    public static String CCDEF() {
        String command = "CCDEF,";
        return DataUtils.packaging(command);
    }

    /**
     * 终端自检
     * @param fre 向外输出终端自检信息(BDZDX)的频度
     * @return string_hex
     */
    public static String CCZDC(int fre) {
        String command = "CCZDC,"+fre;
        return DataUtils.packaging(command);
    }

    /**
     * 蓝牙名设置
     * @param name 蓝牙名称：长度可变,最大18字节
     * @return string_hex
     */
    public static String CCBTI(String name) {
        String command = "CCBTI,"+name;
        return DataUtils.packaging(command);
    }

    /**
     * 位置上报参数查询
     * @return string_hex
     */
    public static String CCPRQ() {
        String command = "CCPRQ,";
        return DataUtils.packaging(command);
    }

    /**
     * 工作协议设置/查询：
     * @param type 0-查询 1-设置
     * @param rdss RDSS协议：0-4.0格式协议 1-2.1格式协议
     * @param rnss RNSS协议：0-自定义4.0格式协议 1-2.1格式协议
     * @return string_hex
     */
    public static String CCYPS(int type, int rdss, int rnss){
        String command = "CCYPS," + type + "," + rdss + "," + rnss;
        return DataUtils.packaging(command);
    }

    /**
     * 功能模组查询
     * @return string_hex
     */
    public static String CCMDQ() {
        String command = "CCMDQ,";
        return DataUtils.packaging(command);
    }

    /**
     * 功能模组设置：
     * @param rdss RDSS使能：0-关闭模组 1-打开模组
     * @param rnss RNSS使能：0-关闭模组 1-单BDS-RNSS定位 2-单GPS定位 3-BDS-RNSS/GPS联合解算定位
     * @param ble BLE使能：0-关闭模组 1-打开模组
     * @param net NET使能：0-关闭模组 1-打开模组
     * @return string_hex
     */
    public static String CCMDS(int rdss, int rnss, int ble, int net){
        String command = "CCMDS," + rdss + "," + rnss + "," + ble + "," + net;
        return DataUtils.packaging(command);
    }

    /**
     * 位置上报参数设置：
     * @param cardNumber 目标卡号：11位十进制字符串，不足11位，前位补’0’
     * @param mode 定位方式：0、关闭上报  1/2、北斗三号定位 3、上报RNSS数据
     * @param fre 采样频度：“00001”-“65535”,其余:关闭上报
     * @param count 信息个数： 0-关闭
     * @param type 上报协议类型： 0、自定义协议 1.JT808
     * @return string_hex
     */
    public static String CCPRS(String cardNumber, int mode, int fre, int count, int type){
        String command = "CCPRS," + cardNumber + "," + mode + "," + fre + "," + count + "," + type;
        return DataUtils.packaging(command);
    }

    /**
     * 救援中心号码设置/查询：
     * @param type 0-查询 1-设置
     * @param cardNumber 接收方地址：11位十进制字符串，不足11位，前位补’0’
     * @param fre 上报频度： “00000”:关闭上报 “00600”-“65535”合法参数
     * @return string_hex
     */
    public static String CCSHM(int type, String cardNumber, int fre){
        String command;
        if(type==0){
            command = "CCSHM," + type;
        }else {
            command = "CCSHM," + type + "," + cardNumber + "," + fre;
        }
        return DataUtils.packaging(command);
    }

    /**
     * 启动救援
     * @return string_hex
     */
    public static String CCQJY() {
        String command = "CCQJY,";
        return DataUtils.packaging(command);
    }

    /**
     * 落水报警：
     * @param type 0-查询 1-设置
     * @param mode 0-关闭 1-开启
     * @return string_hex
     */
    public static String CCQJS(int type, int mode) {
        String command = type==0? ("CCQJS,"+type):("CCQJS,"+type+","+mode);
        return DataUtils.packaging(command);
    }

    /**
     * 模式设置/查询：
     * @param type 0-查询 1-设置
     * @param mode 模式: 0-预留 1-标准模式 2-RD透传模式 3-NET透传模式 4-全透传模式
     * @return string_hex
     */
    public static String CCTRA(int type, int mode) {
        String command = type==0? ("CCTRA,"+type):("CCTRA,"+type+","+mode);
        return DataUtils.packaging(command);
    }

    /**
     * OK报平安参数设置/查询：
     * @param type 0-查询 1-设置
     * @param cardNumber 接收方地址：默认15950044
     * @param content 上报内容
     * @return string_hex
     */
    public static String CCOKS(int type, String cardNumber, String content){
        String command = "CCOKS," + type + "," + cardNumber + "," + content;
        return DataUtils.packaging(command);
    }

    /**
     * 软件关机
     * @return string_hex
     */
    public static String CCZGJ() {
        String command = "CCZGJ,";
        return DataUtils.packaging(command);
    }

    /**
     * RNSS输出频度查询
     * @return string_hex
     */
    public static String CCRNQ() {
        String command = "CCRNQ,";
        return DataUtils.packaging(command);
    }

    /**
     * RNSS输出频度设置：
     * 输入对应类型RN指令的频度，0-9，0关闭输出，最高为9
     * @return string_hex
     */
    public static String  CCRNS(int GGA, int GSV, int GLL, int GSA, int RMC, int ZDA){
        String command = "CCRNS,"+GGA+","+GSV+","+GLL+","+GSA+","+RMC+","+ZDA;
        return DataUtils.packaging(command);
    }

    // FD ----------------------------------------------------------------
    /**
     * 位置上报设置/查询：
     * @param type 0-查询 1-设置
     * @param cardNumber 接收方地址
     * @param fre 上报频率：默认值为300，如果该字段为0，表示不进行上报，单位是S
     * @return string_hex
     */
    public static String FCXWZ(int type, String cardNumber, int fre){
        String command = "FCXWZ," + type + "," + cardNumber + "," + fre;
        return DataUtils.packaging(command);
    }

    /**
     * SOS设置/查询：
     * @param type 0-查询 1-设置
     * @param cardNumber 接收方地址
     * @param fre 上报频率：默认值为60，如果该字段为0，表示不进行上报，单位是S
     * @param content 报警内容
     * @return string_hex
     */
    public static String FCXBJ(int type, String cardNumber, int fre, String content){
        String command = "FCXBJ," + type + "," + cardNumber + "," + fre + "," + content;
        return DataUtils.packaging(command);
    }

    /**
     * 模式查询：
     * @param type 0-查询 1-启动 2-停止
     * @param mode 0-正常模式 1-SOS模式 2-极限追踪模式
     * @return string_hex
     */
    public static String FCXMS(int type, int mode) {
        String command = "FCXMS,"+type+","+mode;
        return DataUtils.packaging(command);
    }

    /**
     * 电池信息查询
     * @return string_hex
     */
    public static String FCXDL() {
        String command = "FCXDL,0,00";
        return DataUtils.packaging(command);
    }

    /**
     * 版本查询
     * @return string_hex
     */
    public static String FCXBB() {
        String command = "FCXBB,0,00";
        return DataUtils.packaging(command);
    }

    /**
     * 查询定位状态
     * @return string_hex
     */
    public static String FCDWZT() {
        String command = "FCDWZT,0,00";
        return DataUtils.packaging(command);
    }

    /**
     * 位置保存周期设置/查询：
     * @param type 0-查询 1-设置
     * @param fre 保存周期：单位是S,如果该字段为0，默认设置为15 ,若大于上报周期，则按上报周期进行记录
     * @return string_hex
     */
    public static String FCXBCZQ(int type, int fre){
        String command = "FCXBCZQ," + type + "," + fre;
        return DataUtils.packaging(command);
    }

    /**
     * 修改蓝牙名称：
     * @param type 0-查询 1-设置
     * @param name 蓝牙名称
     * @return string_hex
     */
    public static String FLYMC(int type, String name) {
        String command = "FLYMC,"+type+","+name;
        return DataUtils.packaging(command);
    }

    /**
     * 历史位置信息查询：
     * @param type 0-批量查询位置点 1-批量删除（全部删除） 2-查询存储位置总数
     * @param count 65536：读取全部总数 0~65535：表示的是最近的报文信息
     * @return string_hex
     */
    public static String FLSWZ(int type, int count) {
        String command = "FLSWZ,"+type+","+count;
        return DataUtils.packaging(command);
    }

    /**
     * 一键参数查询
     * @return string_hex
     */
    public static String FCYJCX() {
        String command = "FCYJCX";
        return DataUtils.packaging(command);
    }

    /**
     * 模式查询：
     * @param type 0-查询 1-设置
     * @param mode 0-关闭RN指令蓝牙透传 1-开启RN指令蓝牙透传
     * @return string_hex
     */
    public static String FLYRN(int type, int mode) {
        String command = "FLYRN,"+type+","+mode;
        return DataUtils.packaging(command);
    }

    /**
     * 落水报警参数设置：
     * @param type 0-查询 1-设置
     * @param cardNumber 接收方地址
     * @param fre 上报频度
     * @param fre_s 短上报频度
     * @param fre_l 长上报频度
     * @param content 上报内容
     * @return string_hex
     */
    public static String CCWAH(int type, String cardNumber, int fre, int fre_s, int fre_l, String content){
        String command = "CCWAH," + type + "," + cardNumber + "," + fre + "," + fre_s + "," + fre_l + "," + content;
        return DataUtils.packaging(command);
    }

    /**
     * 车载设备电源控制：
     * RD/RN/BLE/4G的电源使能 0-关闭 1-打开
     * @return string_hex
     */
    public static String PWSET(int RD, int RN, int BLE, int NET) {
        String command = "PWSET,"+RD+","+RN+","+BLE+","+NET;
        return DataUtils.packaging(command);
    }

}
