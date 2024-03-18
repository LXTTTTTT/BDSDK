package com.pancoit.mod_parse.Parameter;

import java.util.Arrays;

public class ResponseCommand {
    public String[] Raw = null;  // 原始指令
    public String Command = "-";  // 指令
    public boolean Result = false;  // 指令响应结果
    public String Content = "-";  // 指令响应内容
    public String Remark = "-";  // 备注

    @Override
    public String toString() {
        return "ResponseCommand{" +
                "Raw=" + Arrays.toString(Raw) +
                ", Command='" + Command + '\'' +
                ", Result=" + Result +
                ", Content='" + Content + '\'' +
                ", Remark='" + Remark + '\'' +
                '}';
    }
}
