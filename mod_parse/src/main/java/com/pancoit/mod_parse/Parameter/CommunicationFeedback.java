package com.pancoit.mod_parse.Parameter;

public class CommunicationFeedback {
    public String Time = "000000";
    public String Type = "unknown";
    public boolean Result = false;
    public String Reason = "0";
    public String Reason_str = "";
    public int Remain = 0;

    @Override
    public String toString() {
        return "CommunicationFeedback{" +
                "Time='" + Time + '\'' +
                ", Type='" + Type + '\'' +
                ", Result=" + Result +
                ", Reason='" + Reason + '\'' +
                ", Reason_str='" + Reason_str + '\'' +
                ", Remain=" + Remain +
                '}';
    }
}
