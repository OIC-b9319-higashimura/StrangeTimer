package com.example.sugeetimer;

import android.graphics.Color;

public class tpStatus {
    // 保存データ構造体
    public int color;
    public Double time;
    public String soundName;
    public boolean isVibration;

    public tpStatus(){
        color = Color.BLACK;
        time = null;
        soundName = "sName";
        isVibration = false;
    }

    public tpStatus(int color, double time, String soundName, boolean isVibration){
        this.color = color;
        this.time = time;
        this.soundName = soundName;
        this.isVibration = isVibration;
    }

}
