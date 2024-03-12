package com.pancoit.mod_parse.Parameter;

public class FDParameter {
    public String LocationReportID = "0";  // 位置上报目标地址
    public int LocationReportFrequency = -1;  // 位置上报频度
    public String SOSID = "0";  // 紧急救援中心号码
    public int SOSFrequency = -1;  // SOS频度
    public String SOSContent = "-";  // SOS内容
    public int WorkMode = -1;  // 工作模式
    public int BatteryVoltage = -1;  // 电池电压（0.01V）
    public int BatteryLevel = -1;  // 设备电量（%）
    public boolean EnablePositioningModule = true;
    public boolean EnableBDModule = true;
    public String SoftwareVersion = "-";  // 软件版本信息
    public String HardwareVersion = "-";  // 硬件版本信息
    public int LocationStoragePeriod = -1;  // 位置保存周期
    public String BluetoothName = "-";  // 蓝牙名称
    public double Temperature = 0.0d;  // 温度
    public double Humidity = 0.0d;  // 湿度
    public int LocationsCount = -1;  // 保存位置数
    public String CardID = "0";  // 设备卡号
    public int NumberOfResets = -1;  // 复位次数
    public String Power = "-";  // 功率
}
