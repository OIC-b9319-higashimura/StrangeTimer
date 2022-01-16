package com.example.sugeetimer;

// ？？　疑問　？？
// 各ViewでDBへの更新を検知できないから、再描画のタイミング分からないんじゃないか？
// A => 更新時に呼ばれるリスナーを自作すれば出来そう
// Re:A => そもそもおなじView内で行うので関係ない


import java.util.ArrayList;

public class DB {

    enum TimerType{
        Seconds,
        Minutes,
        BPM,
    }

    public String timerName;    // タイマーの名前
    public TimerType type;      // １周の時間タイプ(秒、分秒、BPM)
    private double loopTime;        // １周の時間
    private int loopCount;
    private int selectNumber;    // 現在選択しているtp番号
    private ArrayList<tpStatus> tpStatusList; // 各tpの情報

    // シングルトン
    public static DB singleton = new DB();

    private DB(){
        timerName = "timer_0";
        selectNumber = -1;
        tpStatusList = new ArrayList<>();
    }

    public static DB getInstance(){
        return singleton;
    }

    // listener
    interface UpdateListener{
        void onUpdate();
    }

    private UpdateListener updateListener;

    public void setUpdateListener(UpdateListener listener){
        updateListener = listener;
    }

    // 読み取り用
    public double getLoopTime(){return loopTime;}
    public int getLoopCount(){return loopCount;}
    public tpStatus readStatus(int n){
        if(n < 0 || tpStatusList.size() <= n){
            return null;
        }
        return tpStatusList.get(n);
    }
    public tpStatus readStatus(){
        return readStatus(selectNumber);
    }
    public int getListSize(){
        return tpStatusList.size();
    }
    public int getSelectNumber(){ return selectNumber; }

    // 更新用
    private void update(){
        if(updateListener != null){
            updateListener.onUpdate();
        }
    }
    public void setLoopTime(double time){
        loopTime = time;
        update();
    }
    public void setLoopCount(int count){
        loopCount = count;
        update();
    }
    public void setColor(int n, int c){
        if(n < 0 || tpStatusList.size() <= n){
            return;
        }
        tpStatusList.get(n).color = c;
        update();
    }
    public void setColor(int c){
        setColor(selectNumber, c);
    }
    public void setTime(int n, double t){
        if(n < 0 || tpStatusList.size() <= n){
            return;
        }
        tpStatusList.get(n).time = t;
        update();
    }
    public void setTime(double t){
        setTime(selectNumber, t);
    }
    public void setSoundName(int n){}
    public void setSoundName(){}
    public void setVibration(int n){}
    public void setVibration(){}

    public void setSelectNumber(int n){
        selectNumber = n;
        update();
    }
    // 削除用
    public void deleteItem(int n){
        if(n < 0 || tpStatusList.size() <= n){
            return;
        }
        tpStatusList.remove(n);
        if(n >= getListSize()){
            n = getListSize() - 1;
        }
        selectNumber = n;
        update();
    }
    public void deleteItem(){
        deleteItem(selectNumber);
    }


    // 単位ベクトルで受け取ることを前提とする
    // 登録関数
    public void Register(double time){
        tpStatusList.add(new tpStatus());
        selectNumber = tpStatusList.size() - 1;
        tpStatusList.get(selectNumber).time = time;
        update();
    }
}


