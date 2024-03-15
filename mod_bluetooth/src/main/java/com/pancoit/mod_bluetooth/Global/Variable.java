package com.pancoit.mod_bluetooth.Global;


// 项目使用的全局变量
public class Variable {
    public static boolean showLog = true;
    public static boolean enableCommonInstructionFiltering = false;  // 常用指令过滤
    public static String matchingRules = "";  // 过滤字符匹配规则
    public static void setMatchingRules(String[] rules){
        matchingRules = "";
        for (int i = 0; i < rules.length; i++) {
            if(i==0){
                matchingRules = rules[i];
            } else {
                matchingRules = matchingRules+"|"+rules[i];
            }
        }
    }
}
