package com.pancoit.mod_parse.Parameter;

public class ReceivedMessage {
    public String SendID = "0";
    public String ReceiveID = "0";
    public int FrequencyPoint = 0;
    public String Time = "000000";
    public int EncodeType = 0;
    public String Content = "";

    @Override
    public String toString() {
        return "ReceivedMessage{" +
                "SendID='" + SendID + '\'' +
                ", ReceiveID='" + ReceiveID + '\'' +
                ", FrequencyPoint=" + FrequencyPoint +
                ", Time='" + Time + '\'' +
                ", EncodeType=" + EncodeType +
                ", Content='" + Content + '\'' +
                '}';
    }
}
