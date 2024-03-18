package com.pancoit.mod_parse.Parameter;

import java.util.Arrays;

public class BD_Parameter {
    // 常用
    public String CardID = "0";  // 北斗卡号
    public int CardFrequency = -1;  // 北斗卡频度
    public int CardLevel = -1;  // 北斗卡等级
    public int[] Signal = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};  // 信号情况
    // 其它
    public String BroadcastID = "0";  // 通播地址
    public int AuthorizationType = -1;  // 授启类型
    public int UserIdentity = -1;  // 用户标识
    public int ServiceIdentity = -1;  // 服务标记
    public String EnableConfidentiality = "";  // 启用保密
    public int DeviceType = -1;  // 终端类型
    public String IsRescue = "";  // 搜救标识
    public int Military = -1;  // 军民标识
    public int Civil_Military = -1;  // 军民交互权限
    public int Allies = -1;  // 我国/友国标识
    public int UserPriority = -1;  // 用户优先级
    public int ServiceCapabilities = -1;  // RD区域服务能力标识
    public int GlobalIdentity = -1;  // 全球标识
    public int GlobalMessageInteractionPermissions = -1;  // 全球短报文交互权限
    public int GlobalMessageFrequency = -1;  // 全球短报文服务频度
    public int GlobalMessageLevel = -1;  // 全球短报文通信长度等级
    public int NumberOfSubordinateUsers = -1;  // 下属用户数
    public int GroupPermissions = -1;  // 编组权限
    public int NumberOfSelfBuiltGroups = -1;  // 自建组数
    public int NumberOfGroupsToJoin = -1;  // 加入组数量

    @Override
    public String toString() {
        return "BD_Parameter{" +
                "CardID='" + CardID + '\'' +
                ", CardFrequency=" + CardFrequency +
                ", CardLevel=" + CardLevel +
                ", Signal=" + Arrays.toString(Signal) +
                ", BroadcastID='" + BroadcastID + '\'' +
                ", AuthorizationType=" + AuthorizationType +
                ", UserIdentity=" + UserIdentity +
                ", ServiceIdentity=" + ServiceIdentity +
                ", EnableConfidentiality='" + EnableConfidentiality + '\'' +
                ", DeviceType=" + DeviceType +
                ", IsRescue='" + IsRescue + '\'' +
                ", Military=" + Military +
                ", Civil_Military=" + Civil_Military +
                ", Allies=" + Allies +
                ", UserPriority=" + UserPriority +
                ", ServiceCapabilities=" + ServiceCapabilities +
                ", GlobalIdentity=" + GlobalIdentity +
                ", GlobalMessageInteractionPermissions=" + GlobalMessageInteractionPermissions +
                ", GlobalMessageFrequency=" + GlobalMessageFrequency +
                ", GlobalMessageLevel=" + GlobalMessageLevel +
                ", NumberOfSubordinateUsers=" + NumberOfSubordinateUsers +
                ", GroupPermissions=" + GroupPermissions +
                ", NumberOfSelfBuiltGroups=" + NumberOfSelfBuiltGroups +
                ", NumberOfGroupsToJoin=" + NumberOfGroupsToJoin +
                '}';
    }
}
