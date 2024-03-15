package com.pancoit.mod_parse.Parameter;

public class FDParameter {
    public String LocationReportID = "0";  // 位置上报目标地址
    public int LocationReportFrequency = -1;  // 位置上报频度
    public String SOSID = "0";  // 紧急救援中心号码
    public int SOSFrequency = -1;  // SOS频度
    public String SOSContent = "-";  // SOS内容
    public String OverboardID = "0";  // 落水报警中心号码
    public int OverboardFrequency = -1;  // 落水报警频度
    public String OverboardContent = "-";  // 落水报警内容
    public int WorkMode = -1;  // 工作模式
    public int BatteryVoltage = -1;  // 电池电压 (0.01V)
    public int BatteryLevel = -1;  // 设备电量 (%)
    public int PositioningModuleStatus = -1;  // 定位模块状态
    public int BDModuleStatus = -1;  // 北斗模块状态
    public String SoftwareVersion = "-";  // 软件版本信息
    public String HardwareVersion = "-";  // 硬件版本信息
    public int LocationStoragePeriod = -1;  // 位置保存周期
    public String BluetoothName = "-";  // 蓝牙名称
    public int ExternalVoltage = -1;  // 外电 (0.01V)
    public int InternalVoltage = -1;  // 内电 (0.01V)
    public double Temperature = 0.0d;  // 温度
    public double Humidity = 0.0d;  // 湿度
    public int LocationsCount = -1;  // 保存位置数
    public String CardID = "0";  // 设备卡号
    public int NumberOfResets = -1;  // 复位次数
    public int RNBleFeedback = -1;  // RN蓝牙反馈状况
    public String Power = "-";  // 功率

}
