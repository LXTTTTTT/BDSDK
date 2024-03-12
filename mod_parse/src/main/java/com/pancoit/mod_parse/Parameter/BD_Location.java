package com.pancoit.mod_parse.Parameter;

public class BD_Location {
    // $GNGGA,070728.00,2309.30954,N,11330.00637,E,1,19,0.7,12.91,M,-6.11,M,,*56
    public String Time = "000000";
    public String Date = "000000";  // 日期 ddMMyy
    public double Longitude = 0.0d;
    public String LongitudeIndication = "E";
    public double Latitude = 0.0d;
    public String LatitudeIndication = "N";
    public double EllipsoidHeight = 0.0d;  // 椭球高
    public String UnitOfEH = "M";  // 椭球高单位
    public double Speed = 0.0d;  // 速度 km/h
    // $GNGLL,2309.30960,N,11330.00645,E,070727.00,A,A*71
    public int Valid = -1;  // 有效性
    public int NoSV = -1;  // 参与定位卫星数量
    public double HDOP = 0.0d;  // 水平精度因子
    public double Altref = 0.0d;  // 海平面分离度
    public String UnitOfAltref = "M";  // 海平面分离度单位
    public double COG = -1;  // 地面航向

    @Override
    public String toString() {
        return "BD_Location{" +
                "Time='" + Time + '\'' +
                ", Date='" + Date + '\'' +
                ", Longitude=" + Longitude +
                ", LongitudeIndication='" + LongitudeIndication + '\'' +
                ", Latitude=" + Latitude +
                ", LatitudeIndication='" + LatitudeIndication + '\'' +
                ", Speed=" + Speed +
                ", Valid=" + Valid +
                ", NoSV=" + NoSV +
                ", HDOP=" + HDOP +
                ", EllipsoidHeight=" + EllipsoidHeight +
                ", UnitOfEH='" + UnitOfEH + '\'' +
                ", Altref=" + Altref +
                ", UnitOfAltref='" + UnitOfAltref + '\'' +
                ", COG=" + COG +
                '}';
    }


}
