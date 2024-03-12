package com.pancoit.mod_parse.Parameter;

import java.util.Arrays;

public class SatelliteStatus {
    public String Time = "000000";  // 时间
    public String Date = "000000";  // 日期 ddMMyy
    public String Smode = "";  // 定位模式指定状态
    public int FS = -1;  // 定位模式
    public int[] PositioningSatellite = {0,0,0,0,0,0,0,0,0,0,0,0};  // 参与了定位的卫星
    public double PDOP = 0.0d;  // 位置精度因子
    public double VDOP = 0.0d;  // 垂向精度因子
    public double HDOP = 0.0d;  // 水平精度因子
    public int NoMsg = -1;  // GSV消息总数
    public int MsgNo = -1;  // 本条GSV消息编号
    public int NoSv = -1;  // 可见卫星总数
    public int[] SV1_4 = {0,0,0,0};  // 第1-4颗卫星的卫星号
    public int[] ELV1_4 = {0,0,0,0};  // 第1-4颗卫星的仰角（ 0 ~ 90 度）
    public int[] AZ1_4 = {0,0,0,0};  // 第1-4颗卫星的方位角（ 0 ~ 359 度）
    public int[] CNO1_4 = {0,0,0,0};  // 第1-4颗卫星的载噪比（ 0 ~ 99 dBHz）

    @Override
    public String toString() {
        return "SatelliteStatus{" +
                "Time='" + Time + '\'' +
                ", Date='" + Date + '\'' +
                ", Smode='" + Smode + '\'' +
                ", FS=" + FS +
                ", PositioningSatellite=" + Arrays.toString(PositioningSatellite) +
                ", PDOP=" + PDOP +
                ", VDOP=" + VDOP +
                ", HDOP=" + HDOP +
                ", NoMsg=" + NoMsg +
                ", MsgNo=" + MsgNo +
                ", NoSv=" + NoSv +
                ", SV1_4=" + Arrays.toString(SV1_4) +
                ", ELV1_4=" + Arrays.toString(ELV1_4) +
                ", AZ1_4=" + Arrays.toString(AZ1_4) +
                ", CNO1_4=" + Arrays.toString(CNO1_4) +
                '}';
    }
}
