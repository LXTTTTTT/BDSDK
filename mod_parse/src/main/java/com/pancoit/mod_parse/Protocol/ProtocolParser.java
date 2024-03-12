package com.pancoit.mod_parse.Protocol;

import android.util.Log;

import com.pancoit.mod_parse.CallBack.ParameterListener;
import com.pancoit.mod_parse.Global.Variable;
import com.pancoit.mod_parse.Parameter.BD_Location;
import com.pancoit.mod_parse.Parameter.BD_Parameter;
import com.pancoit.mod_parse.Parameter.CommunicationFeedback;
import com.pancoit.mod_parse.Parameter.FDParameter;
import com.pancoit.mod_parse.Parameter.ReceivedMessage;
import com.pancoit.mod_parse.Parameter.ResponseCommand;
import com.pancoit.mod_parse.Parameter.SatelliteStatus;
import com.pancoit.mod_parse.Parameter.XYParameter;
import com.pancoit.mod_parse.Utils.DataUtils;

import java.util.Arrays;

public class ProtocolParser {

    public static String TAG = "ProtocolParser";

    private static ProtocolParser parser;
    public static ProtocolParser getInstance() {
        if(parser == null){
            parser = new ProtocolParser();
        }
        return parser;
    }

    private BD_Parameter bd_parameter;  // 北斗参数
    private XYParameter xyParameter;  // XY设备参数
    private FDParameter fdParameter;  // FD设备参数
    private CommunicationFeedback communicationFeedback;  // FKI反馈信息
    private ReceivedMessage receivedMessage;  // 接收到的通信信息
    private BD_Location bd_location;  // 北斗位置
    private SatelliteStatus satelliteStatus;  // 卫星状态
    public ProtocolParser(){
        Init();
    }

    // 初始化数据，需要在每次断开连接时进行一次数据初始化
    public void Init(){
        bd_parameter = new BD_Parameter();
        xyParameter = new XYParameter();
        fdParameter = new FDParameter();
        communicationFeedback = new CommunicationFeedback();
        receivedMessage = new ReceivedMessage();
        bd_location = new BD_Location();
        satelliteStatus = new SatelliteStatus();
    }

    // 执行回调传出参数
    private void BDParameterChange(){
        if(parameterListener!=null){parameterListener.OnBDParameterChange(bd_parameter);}
    }
    private void XYParameterChange(){
        if(parameterListener!=null){parameterListener.OnDeviceAParameterChange(xyParameter);}
    }
    private void FDParameterChange(){
        if(parameterListener!=null){parameterListener.OnDeviceBParameterChange(fdParameter);}
    }
    private void CommunicationFeedback(){
        if(parameterListener!=null){parameterListener.OnCommunicationFeedback(communicationFeedback);}
    }
    private void ReceivedMessage(){
        if(parameterListener!=null){parameterListener.OnMessageReceived(receivedMessage);}
    }
    private void LocationChange(){
        if(parameterListener!=null){parameterListener.OnBDLocationChange(bd_location);}
    }
    private void SatelliteStatusChange(){
        if(parameterListener!=null){parameterListener.OnSatelliteStatusChange(satelliteStatus);}
    }
    private void ReceivedResponseCommand(ResponseCommand command){
        if(parameterListener!=null){parameterListener.OnCommandResponse(command);}
    }

    public void showLog(boolean show){Variable.showLog = show;}

    private void loge(String log){
        if(Variable.showLog){
            Log.e(TAG, log);
        }
    }
    private void logi(String log){
        if(Variable.showLog){
            Log.i(TAG, log);
        }
    }



    // 处理碎片化数据
    private StringBuilder dataBuilder = new StringBuilder();
    public void parseData_fragment(String str) {
        dataBuilder.append(str);
        int startIndex = dataBuilder.indexOf("$");
        if (startIndex < 0) {
            dataBuilder.setLength(0);  // 重置 StringBuilder
            return;
        }
        if (startIndex > 0) {
            dataBuilder.delete(0, startIndex);  // 删除 $ 之前的数据
        }
        if (dataBuilder.length() < 6) return;
        int endIndex = dataBuilder.indexOf("*");
        if (endIndex < 1) return;
        String intactData = dataBuilder.substring(0, endIndex + 3);
        dataBuilder.delete(0, endIndex + 3);
        parseData(intactData);
    }

    // 处理完整指令
    // 蓝牙收到数据时已经开启了线程池接收，已在子线程中
    public void parseData(String intactData) {
        loge("需要解析的原始数据："+intactData);
        int xorIndex = intactData.indexOf("*");
        if (xorIndex == -1) return;
        String data_str = intactData.substring(0, xorIndex);
        if (!data_str.contains(",")) return;
        String[] values = data_str.split(",", -1);
        // 北斗标准
        if (data_str.contains("ICP")) BDICP(values);
        else if (data_str.contains("FKI")) BDFKI(values);
        else if (data_str.contains("PWI")) BDPWI(values);
        else if (data_str.contains("TCI") || data_str.startsWith("TXR")) BDMessage(values);
        // 其他自定义
        else if (data_str.contains("ZDX")) BDZDX(values);
        else if (data_str.contains("ICI")) BDICI(values);  // 北二
        else if (data_str.contains("SNR")) BDSNR(values);

        else if (data_str.contains("BDVRX")) BDVRX(values);
        else if (data_str.contains("BDRSX")) BDRSX(values);
        else if (data_str.contains("BDDEF")) BDDEF(values);

        else if (data_str.contains("BDRSX")) BDRSX(values);
        else if (data_str.contains("BDRSX")) BDRSX(values);
        else if (data_str.contains("BDRSX")) BDRSX(values);
        else if (data_str.contains("BDRSX")) BDRSX(values);
        else if (data_str.contains("BDRSX")) BDRSX(values);

        // RN
        else parseRNSS(values);
    }

    // 解析 RNSS 位置数据
    public void parseRNSS(String[] values){
        try {
            // 拆分数据
            String head = values[0];
            if (head.contains("GGA")){
                if (values[6].equals("0")) return;  // 0 就是无效定位，不要
                bd_location.Time = values[1];
                bd_location.Latitude = DataUtils.analysisLonlat(values[2]);
                bd_location.LatitudeIndication = values[3];
                bd_location.Longitude = DataUtils.analysisLonlat(values[4]);
                bd_location.LongitudeIndication = values[5];
                bd_location.Valid = Integer.parseInt(values[6]);
                bd_location.NoSV = Integer.parseInt(values[7]);
                bd_location.HDOP = Double.parseDouble(values[8]);
                bd_location.EllipsoidHeight = Double.parseDouble(values[9]);
                bd_location.UnitOfEH = values[10];
                bd_location.Altref = Double.parseDouble(values[11]);
                bd_location.UnitOfAltref = values[12];
                LocationChange();
            } else if (head.contains("GLL")){
                if (values[6].equals("V")) return;  // V - 无效  A - 有效
                bd_location.Latitude = DataUtils.analysisLonlat(values[1]);
                bd_location.LatitudeIndication = values[2];
                bd_location.Longitude = DataUtils.analysisLonlat(values[3]);
                bd_location.LongitudeIndication = values[4];
                bd_location.Time = values[5];
                bd_location.Valid = values[6].equals("A")? 1:0;
                LocationChange();
            } else if (head.contains("GSA")){
                satelliteStatus.Smode = values[1];
                satelliteStatus.FS = Integer.parseInt(values[2]);
                int[] Satellite = {0,0,0,0,0,0,0,0,0,0,0,0};
                for (int i = 3; i < 15; i++) {
                    if(values[i]==null||values[i].equals("")){
                        Satellite[i-3] = 0;
                    } else {
                        Satellite[i-3] = Integer.parseInt(values[i]);
                    }
                }
                satelliteStatus.PositioningSatellite = Satellite;
                satelliteStatus.PDOP = Double.parseDouble(values[15]);
                satelliteStatus.HDOP = Double.parseDouble(values[16]);
                satelliteStatus.VDOP = Double.parseDouble(values[17]);
            } else if (head.contains("GSV")){
                // $GPGSV,3,1,10,01,,,41,02,38,034,42,03,36,113,47,07,38,195,25,1*5B
                // $GPGSV,3,3,10,20,25,214,,22,68,187,*7A
                satelliteStatus.NoMsg = Integer.parseInt(values[1]);
                satelliteStatus.MsgNo = Integer.parseInt(values[2]);
                satelliteStatus.NoSv = Integer.parseInt(values[3]);
                if(values.length>=7){
                    int[] sv1_4 = {0,0,0,0};
                    for (int i = 4; i < 8; i++) {
                        sv1_4[i-4] = (values[i]!=null&&!values[i].equals(""))? Integer.parseInt(values[i]):0;
                    }
                    satelliteStatus.SV1_4 = sv1_4;
                }
                if(values.length>=11){
                    int[] elv1_4 = {0,0,0,0};
                    for (int i = 8; i < 12; i++) {
                        elv1_4[i-8] = (values[i]!=null&&!values[i].equals(""))? Integer.parseInt(values[i]):0;
                    }
                    satelliteStatus.ELV1_4 = elv1_4;
                }
                if(values.length>=15){
                    int[] az1_4 = {0,0,0,0};
                    for (int i = 12; i < 16; i++) {
                        az1_4[i-12] = (values[i]!=null&&!values[i].equals(""))? Integer.parseInt(values[i]):0;
                    }
                    satelliteStatus.AZ1_4 = az1_4;
                }
                if(values.length>=19){
                    int[] cno1_4 = {0,0,0,0};
                    for (int i = 16; i < 20; i++) {
                        cno1_4[i-16] = (values[i]!=null&&!values[i].equals(""))? Integer.parseInt(values[i]):0;
                    }
                    satelliteStatus.CNO1_4 = cno1_4;
                }
                SatelliteStatusChange();
            } else if (head.contains("RMC")){
                // $GNRMC,070723.00,A,2309.30979,N,11330.00659,E,0.08,,070324,,,A,V*29
                //  [$GNRMC, 093021.000, A, 2309.32392, N, 11330.03365, E, 3.64, 354.48, 080324, , , A]
                if (values[2].equals("V")) return;  // V - 无效  A - 有效
                bd_location.Time = values[1];
                bd_location.Valid = values[2].equals("A")? 1:0;
                bd_location.Latitude = DataUtils.analysisLonlat(values[3]);
                bd_location.LatitudeIndication = values[4];
                bd_location.Longitude = DataUtils.analysisLonlat(values[5]);
                bd_location.LongitudeIndication = values[6];
                bd_location.Speed = Double.parseDouble(values[7]) * 1.852;
                if(!values[8].equals("")){bd_location.COG = Double.parseDouble(values[8]);}
                bd_location.Date = values[9];
                LocationChange();
            } else if (head.contains("ZDA")){
                // $GNZDA,070728.00,07,03,2024,00,00*72
                satelliteStatus.Time = values[1];
                satelliteStatus.Date = values[2]+values[3]+values[4].substring(values[4].length()-2);
                SatelliteStatusChange();
            } else {
                loge("收到其他指令: "+ Arrays.toString(values));
                return;
            }
        }catch (Exception e){
            loge("parseRNSS 解析错误: "+ Arrays.toString(values));
            e.printStackTrace();
            return;
        }

    }

// 解析协议 -----------------------------------------
    // ICP 卡号、频度等
    // $BDICP,15240507,2032472,1,3,0,N,1,Y,1,0,0,3,1,1,5,1,1,2,2,500,100,10,10*65
    public void BDICP(String[] value){
        try {
            String cardId = value[1];
            int cardFre = Integer.parseInt(value[14]);
            int cardLevel = -1;
            if(Integer.parseInt(value[15]) == 0){
                cardLevel = 5;  // 0就是5级卡
            } else {
                cardLevel = Integer.parseInt(value[15]);
            }
            loge("BDICP 解析设备信息: 卡号-"+cardId+" 频度-"+cardFre+" 等级-"+cardLevel );
            bd_parameter.CardID = cardId;
            bd_parameter.CardLevel = cardLevel;
            bd_parameter.CardFrequency = cardFre;
            bd_parameter.BroadcastID = value[2];
            bd_parameter.AuthorizationType = Integer.parseInt(value[3]);
            bd_parameter.UserIdentity = Integer.parseInt(value[4]);
            bd_parameter.ServiceIdentity = Integer.parseInt(value[5]);
            bd_parameter.EnableConfidentiality = value[6];
            bd_parameter.DeviceType = Integer.parseInt(value[7]);
            bd_parameter.IsRescue = value[8];
            bd_parameter.Military = Integer.parseInt(value[9]);
            bd_parameter.Civil_Military = Integer.parseInt(value[10]);
            bd_parameter.Allies = Integer.parseInt(value[11]);
            bd_parameter.UserPriority = Integer.parseInt(value[12]);
            bd_parameter.ServiceCapabilities = Integer.parseInt(value[13]);
            bd_parameter.GlobalIdentity = Integer.parseInt(value[16]);
            bd_parameter.GlobalMessageInteractionPermissions = Integer.parseInt(value[17]);
            bd_parameter.GlobalMessageFrequency = Integer.parseInt(value[18]);
            bd_parameter.GlobalMessageLevel = Integer.parseInt(value[19]);
            bd_parameter.NumberOfSubordinateUsers = Integer.parseInt(value[20]);
            bd_parameter.GroupPermissions = Integer.parseInt(value[21]);
            bd_parameter.NumberOfSelfBuiltGroups = Integer.parseInt(value[22]);
            bd_parameter.NumberOfGroupsToJoin = Integer.parseInt(value[23]);
            BDParameterChange();
        }catch (Exception e){
            loge("BDICP: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    // 通信申请后的反馈信息
    // [$BDFKI, TXA, N, Y, 4, 0000]  北二
    // [$BDFKI, 080432, TCQ, Y, 0, 0]  北三
    public void BDFKI(String[] values){
        try {
            String time = values[1];
            String type = values[2];  // 指令类型
            boolean message_result = false;
            String result = values[3];  // 反馈结果
            String reason = values[4];  // 失败原因
            String reason_str = "";
            int remain = Integer.parseInt(values[5]);
            if(result.equals("Y")){
                message_result = true;
            } else {
                message_result = false;
                switch ( reason ){
                    case "1":
                        reason_str = "频率未到";break;
                    case "2":
                        reason_str = "发射抑制";break;
                    case "3":
                        reason_str = "发射静默";break;
                    case "4":
                        reason_str = "功率未锁定";break;
                    case "5":
                        reason_str = "未检测到IC模块信息";break;
                    case "6":
                        reason_str = "权限不足";break;
                    default:
                        reason_str = "未知原因";
                        break;
                }
            }
            communicationFeedback.Time = time;
            communicationFeedback.Type = type;
            communicationFeedback.Result = message_result;
            communicationFeedback.Reason = reason;
            communicationFeedback.Reason_str = reason_str;
            communicationFeedback.Remain = remain;
            CommunicationFeedback();
        }catch (Exception e){
            loge("BDFKI: 解析错误");
            e.printStackTrace();
        }
    }

    // PWI 功率信息
    // $BDPWI,000000.00,00,01,51,40,33,0*7B
    public void BDPWI(String[] values){
        try {
            int rdss2Count1 = Integer.parseInt(values[2]);
            int index = 2+rdss2Count1*3+1;
            int rdss3Count = Integer.parseInt(values[index]);
            index++;
            int s21[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            for (int i =0 ;i < rdss3Count; i++){
                int id = Integer.parseInt(values[index]);
                if (id > 21) continue;
                int number = Integer.parseInt(values[index+1]);
                s21[id-1] = number;
                if(values.length>index+3 && (values[index+3].equals("0") || values[index+3].equals(""))){
                    index += 4;
                } else {
                    index += 3;
                }
            }
            loge("BDPWI 解析设备信号: "+Arrays.toString(s21) );
            bd_parameter.Signal = s21;
            BDParameterChange();
        } catch (Exception e){
            loge("BDPWI: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    // [$BDTCI, 04207733, 4207733, 2, 023242, 2, 0, 90000000000065BEF749B2E2CAD4]
    // $BDTXR,1,4207733,1,2337,90000000000065C1FAF4B2E2CAD4*3F
    public void BDMessage(String[] values){
        try {
            int from = 0;  // 带了个0，先转化为int
            String to = "0";
            int frequency_point = 0;
            String time = "000000";
            int encode_type = 0;
            String content = "";
            if(values[0].contains("TCI")){
                from = Integer.parseInt(values[1]);
                to = values[2];
                frequency_point = Integer.parseInt(values[3]);
                time = values[4];
                encode_type = Integer.parseInt(values[5]);
                content = values[7];
            }else if(values[0].contains("TXR")){
                from = Integer.parseInt(values[2]);
                encode_type = Integer.parseInt(values[3]);
                content = values[5];
            }
            receivedMessage.SendID = from+"";
            receivedMessage.ReceiveID = to;
            receivedMessage.FrequencyPoint = frequency_point;
            receivedMessage.Time = time;
            receivedMessage.EncodeType = encode_type;
            receivedMessage.Content = content;
            loge("解析北斗消息: "+receivedMessage.toString());
            ReceivedMessage();
        } catch (Exception  e){
            loge("BDTCI: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    // 收到了 ZDX 盒子信息
    // [$BDZDX, 4207733, 036, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 2, 1835, 0, 68.06, 52, 102706]
    public void BDZDX(String[] values){
        try {
            String cardId = values[1];
            int cardFre = Integer.parseInt(values[24]);
            int cardLevel = Integer.parseInt(values[25]);
            int batteryLevel = Integer.parseInt(values[2]);
            loge("BDZDX 解析设备信息: 卡号-"+cardId+" 频度-"+cardFre+" 等级-"+cardLevel+" 电量-"+batteryLevel );
            int s21[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            s21[0] = Integer.parseInt(values[3]);
            s21[1] = Integer.parseInt(values[4]);
            s21[2] = Integer.parseInt(values[5]);
            s21[3] = Integer.parseInt(values[6]);
            s21[4] = Integer.parseInt(values[7]);
            s21[5] = Integer.parseInt(values[8]);
            s21[6] = Integer.parseInt(values[9]);
            s21[7] = Integer.parseInt(values[10]);
            s21[8] = Integer.parseInt(values[11]);
            s21[9] = Integer.parseInt(values[12]);
            s21[10] = Integer.parseInt(values[13]);
            s21[11] = Integer.parseInt(values[14]);
            s21[12] = Integer.parseInt(values[15]);
            s21[13] = Integer.parseInt(values[16]);
            s21[14] = Integer.parseInt(values[17]);
            s21[15] = Integer.parseInt(values[18]);
            s21[16] = Integer.parseInt(values[19]);
            s21[17] = Integer.parseInt(values[20]);
            s21[18] = Integer.parseInt(values[21]);
            s21[19] = Integer.parseInt(values[22]);
            s21[20] = Integer.parseInt(values[23]);
            loge("BDZDX 解析设备信号: "+Arrays.toString(s21) );
            bd_parameter.CardID = cardId;
            bd_parameter.CardLevel = cardLevel;
            bd_parameter.CardFrequency = cardFre;
            xyParameter.BatteryLevel = batteryLevel;
            bd_parameter.Signal = s21;
            xyParameter.ContentLength = Integer.parseInt(values[26]);
            xyParameter.Temperature = Double.parseDouble(values[28]);
            xyParameter.Humidity = Integer.parseInt(values[29]);
            xyParameter.Pressure = Integer.parseInt(values[30]);
            BDParameterChange();
            XYParameterChange();
        }catch(Exception  e){
            loge("BDZDX: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    // ICI 卡号、频度等
    // $BDICI,4207733,0,0,3,60,2,N,22*3A
    public void BDICI(String[] value){
        try {
            String cardId = value[1];
            int cardFre = Integer.parseInt(value[5]);
            int cardLevel = -1;
            if(Integer.parseInt(value[6]) == 0){
                cardLevel = 5;  // 0就是5级卡
            }else {
                cardLevel = Integer.parseInt(value[6]);
            }
            loge("BDICI 解析设备信息: 卡号-"+cardId+" 频度-"+cardFre+" 等级-"+cardLevel );
            bd_parameter.CardID = cardId;
            bd_parameter.CardLevel = cardLevel;
            bd_parameter.CardFrequency = cardFre;
            bd_parameter.BroadcastID = value[3];
            bd_parameter.UserIdentity = Integer.parseInt(value[4]);
            bd_parameter.EnableConfidentiality = value[7];
            bd_parameter.NumberOfSubordinateUsers = Integer.parseInt(value[8]);
            BDParameterChange();
        }catch (Exception e){
            loge("BDICI: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    // $BDSNR,0,0,0,0,0,0,48,0,0,0,0,0,41,0,0,0,44,44,0,0,0*5C
    // SNR 功率信息
    public void BDSNR(String[] values){
        try {
            int s21[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            for (int i = 0; i < values.length; i++) {
                if(i==0){continue;}
                s21[i-1] = Integer.parseInt(values[i]);
            }
            loge("BDSNR 解析设备信号: "+Arrays.toString(s21) );
            bd_parameter.Signal = s21;
            BDParameterChange();
        }catch (Exception e){
            loge("BDSNR: 解析错误");
            e.printStackTrace();
            return;
        }
    }



    public void BDVRX(String[] values){
        try {
            xyParameter.Version = values[1];
            XYParameterChange();
        }catch (Exception e){
            loge("BDVRX: 解析错误");
            e.printStackTrace();
            return;
        }
    }


    public void BDRSX(String[] values){
        try {
            ResponseCommand command = new ResponseCommand();
            command.Command = values[0].replace("$","");
            command.Result = true;
            command.Content = values[1];
            command.Remark = "设备复位信息反馈";
            ReceivedResponseCommand(command);
            xyParameter.RestartMode = Integer.parseInt(values[1]);
            XYParameterChange();
        }catch (Exception e){
            loge("BDRSX: 解析错误");
            e.printStackTrace();
            return;
        }
    }

    public void BDDEF(String[] values){
        try {
            ResponseCommand command = new ResponseCommand();
            command.Command = values[0].replace("$","");
            command.Result = true;
            command.Content = values[1];
            command.Remark = "设备恢复出厂设置反馈";
            ReceivedResponseCommand(command);
        }catch (Exception e){
            loge("BDDEF: 解析错误");
            e.printStackTrace();
            return;
        }
    }

// 接口 ---------------------------------------
    public ParameterListener parameterListener;
    public void setParameterListener(ParameterListener parameterListener){
        this.parameterListener = parameterListener;
    }

}
