package com.pancoit.mod_parse.CallBack;

import com.pancoit.mod_parse.Parameter.BD_Location;
import com.pancoit.mod_parse.Parameter.BD_Parameter;
import com.pancoit.mod_parse.Parameter.CommandFeedback;
import com.pancoit.mod_parse.Parameter.FDParameter;
import com.pancoit.mod_parse.Parameter.ReceivedMessage;
import com.pancoit.mod_parse.Parameter.ResponseCommand;
import com.pancoit.mod_parse.Parameter.SatelliteStatus;
import com.pancoit.mod_parse.Parameter.UnresolvedCommand;
import com.pancoit.mod_parse.Parameter.XYParameter;

public interface ParameterListener {
    void OnBDParameterChange(BD_Parameter parameter);  // 北斗参数改变
    void OnDeviceAParameterChange(XYParameter parameter);  // XY设备参数改变
    void OnDeviceBParameterChange(FDParameter parameter);  // FD设备参数改变
    void OnCommandFeedback(CommandFeedback feedback);  // 指令反馈信息监听
    void OnMessageReceived(ReceivedMessage message);  // 通信信息监听
    void OnBDLocationChange(BD_Location location);  // 卫星定位改变
    void OnSatelliteStatusChange(SatelliteStatus status);  // 卫星状态改变
    void OnCommandResponse(ResponseCommand command);  // 收到指令响应
    void OnCommandUnresolved(UnresolvedCommand command);  // 收到未解析指令
}
