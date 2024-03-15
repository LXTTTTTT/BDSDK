package com.pancoit.mod_parse.Parameter;

import java.util.Arrays;

public class XYParameter {
    public String Version = "-";  // 版本信息
    public int RestartMode = -1;  // 复位模式
    public int BatteryLevel = -1;  // 设备电量 (%)
    public int ContentLength = -1;  // 报文长度限制 (bit)
    public double Temperature = 0.0d;  // 温度 (°)
    public int Humidity = -1;  // 湿度 (%)
    public int Pressure = -1;  // 气压 (pa)
    public String LocationReportID = "0";  // 位置上报目标地址
    public int PositionMode = -1;  // 定位方式
    public int CollectionFrequency = -1;  // 采集频度
    public int PositionCount = -1;  // 位置个数
    public int ReportType = -1;  // 上报协议类型
    public String SOSID = "0";  // 紧急救援中心号码
    public int SOSFrequency = -1;  // SOS频度
    public String OKID = "0";  // 报平安中心号码
    public String OKContent = "-";  // 报平安内容
    public int RDSSProtocolVersion = -1;  // 输出的RDSS语句版本
    public int RNSSProtocolVersion = -1;  // 输出的RNSS语句版本
    public int RDSSMode = -1;  // RDSS工作模式
    public int RNSSMode = -1;  // RNSS工作模式
    public int BLEMode = -1;  // 蓝牙工作模式
    public int NETMode = -1;  // 网络工作模式
    public int WorkMode = -1;  // 当前工作模式
    public int GGAFrequency = -1;  // GGA指令输出频度
    public int GSVFrequency = -1;  // GSV指令输出频度
    public int GLLFrequency = -1;  // GLL指令输出频度
    public int GSAFrequency = -1;  // GSA指令输出频度
    public int RMCFrequency = -1;  // RMC指令输出频度
    public int ZDAFrequency = -1;  // ZDA指令输出频度
    public int TimeZone = -1;  // 时区

    @Override
    public String toString() {
        return "XYParameter{" +
                "Version='" + Version + '\'' +
                ", RestartMode=" + RestartMode +
                ", BatteryLevel=" + BatteryLevel +
                ", ContentLength=" + ContentLength +
                ", Temperature=" + Temperature +
                ", Humidity=" + Humidity +
                ", Pressure=" + Pressure +
                ", LocationReportID='" + LocationReportID + '\'' +
                ", PositionMode=" + PositionMode +
                ", CollectionFrequency=" + CollectionFrequency +
                ", PositionCount=" + PositionCount +
                ", ReportType=" + ReportType +
                ", SOSID='" + SOSID + '\'' +
                ", SOSFrequency=" + SOSFrequency +
                ", OKID='" + OKID + '\'' +
                ", OKContent='" + OKContent + '\'' +
                ", RDSSProtocolVersion=" + RDSSProtocolVersion +
                ", RNSSProtocolVersion=" + RNSSProtocolVersion +
                ", RDSSMode=" + RDSSMode +
                ", RNSSMode=" + RNSSMode +
                ", BLEMode=" + BLEMode +
                ", NETMode=" + NETMode +
                ", WorkMode=" + WorkMode +
                ", GGAFrequency=" + GGAFrequency +
                ", GSVFrequency=" + GSVFrequency +
                ", GLLFrequency=" + GLLFrequency +
                ", GSAFrequency=" + GSAFrequency +
                ", RMCFrequency=" + RMCFrequency +
                ", ZDAFrequency=" + ZDAFrequency +
                ", TimeZone=" + TimeZone +
                '}';
    }
}
