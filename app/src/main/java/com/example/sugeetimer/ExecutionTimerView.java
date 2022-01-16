package com.example.sugeetimer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ExecutionTimerView extends View {

    // listener
    interface SoundPoolListener{
        void onSound();
    }

    private SoundPoolListener soundPoolListener;

    public void setSoundPoolListener(SoundPoolListener listener){
        soundPoolListener = listener;
    }


    private int loopCount;
    private int loopTime;
    private int listSize;
    private final int tpRadius = 100;

    private Paint   paint;
    private Circle  centerCircle;
    private int    startTime;
    private int    stopwatch; // msSSS[ms]
    private int     hoge;

    private ArrayList<Integer> numberList = null;

    private boolean isStart;
    private boolean isFinish;

    public ExecutionTimerView(Context context) {
        super(context);
        init();
    }

    public ExecutionTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExecutionTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        loopCount = DB.getInstance().getLoopCount();
        loopTime = (int) (DB.getInstance().getLoopTime() * 1000);
        listSize = DB.getInstance().getListSize();

        paint = new Paint();
        centerCircle = new Circle();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isFinish == false && isStart == true){ invalidate(); }
            }
        }, 0, 33); //fps30

        numberList = new ArrayList<>();
        for (int c = 0; c < loopCount; c++) {
            for (int s = 0; s < listSize; s++) {
                int t = (int) (loopTime*c + DB.getInstance().readStatus(s).time * 1000);
                numberList.add(t);
            }
        }
        Collections.sort(numberList);

        startInit();
    }

    private void startInit(){
        startTime = 0;
        stopwatch = 0;
        hoge = 0;
        isFinish = false;
        isStart = false;
    }

    private void update(){
        // 経過時間の更新
        // 開始判定
        if(isStart == true){
            stopwatch += getTime() - startTime;
            startTime = getTime();
            // 終了判定
            if(loopCount != Integer.MAX_VALUE) { // 実質無限ループ
                if(stopwatch >= loopTime * loopCount){
//                if (hoge >= numberList.size()) {
                    Log.d(TAG, "update: finish");
                    stopwatch = 0;
                    isFinish = true;
                    isStart = false;
                    return;
                }
            }

            // 音を鳴らす判定
            if(hoge < numberList.size()){
                if(numberList.get(hoge) <= stopwatch){
                    hoge++;
                    // 音鳴らす
                    if(soundPoolListener != null){
                        soundPoolListener.onSound();
                    }
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        update();

        // 描画の更新
        canvas.drawColor(Color.argb(32,0,0,0));

        centerCircle.position.x = getWidth() * 0.5;
        centerCircle.position.y = getHeight() * 0.5;
        centerCircle.radius = Math.min(getWidth(), getHeight())  * 0.5 - 50;

        // 円
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        centerCircle.Draw(canvas,paint);

        Circle c = new Circle();
        // タイマーポイントの描画
        c.radius = tpRadius;
        for (int i = 0;i < listSize;i++){
            tpStatus rStatus = DB.getInstance().readStatus(i);
            paint.setColor(rStatus.color);
            c.position = centerOffset(timeToVector(rStatus.time));
            c.Draw(canvas,paint);
        }


        long t = stopwatch % loopTime;
        Vector2 v = timeToVector(t / 1000.0);
        Vector2 sp = centerCircle.position.plus(v.times(centerCircle.radius - 75));
        Vector2 ep = centerCircle.position.plus(v.times(centerCircle.radius + 75));
        paint.setColor(Color.BLACK);
        canvas.drawLine( (float)sp.x, (float)sp.y, (float)ep.x, (float)ep.y, paint);
    }

    public boolean isStart(){
        return isStart;
    }

    public void timerStart(){
        if(isFinish == true){
            startInit();
        }
        isStart = true;
        startTime = getTime();
    }

    public void timerStop(){
        isStart = false;
    }

    public void nextPoint(){
        if(hoge < numberList.size()){
            stopwatch = numberList.get(hoge++);
        }
        else{
            isFinish = true;
            isStart = false;
            stopwatch = 0;
        }
        Log.d(TAG, "nextPoint:stopwatch "+stopwatch);
        invalidate();
    }

    public void prevPoint(){
        if(hoge > 0){
            Log.d(TAG, "prevPoint:hoge "+hoge);
            stopwatch = numberList.get(hoge-1);
        }
        else{
            stopwatch = 0;
        }
        Log.d(TAG, "prevPoint:stopwatch "+stopwatch);
        invalidate();
    }

    public void nextLoop(){
        hoge -= hoge % listSize;
        hoge += listSize;
        if(hoge < numberList.size()){
            stopwatch = loopTime * hoge / listSize;
        }
        else{
            stopwatch = 0;
            isFinish = true;
            isStart = false;
        }
        Log.d(TAG, "nextLoop:stopwatch "+stopwatch);
        invalidate();
    }

    public void prevLoop(){
        hoge -= hoge % listSize;
        stopwatch = loopTime * hoge / listSize;
        Log.d(TAG, "prevLoop:stopwatch "+stopwatch);
        invalidate();
    }



    private int getTime(){
        final DateFormat df = new SimpleDateFormat("hhmmssSSS");
        final Date date = new Date(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(df.format(date));
        int h = (Integer.parseInt(sb.substring(0,2)) / 10000000) * 3600;
        int m = (Integer.parseInt(sb.substring(2,4)) / 100000) * 60;
        int s = Integer.parseInt(sb.substring(4));
        return h*100 + m*100 + s; // 標準時 jpは,+9:00
    }

    private Vector2 centerOffset(Vector2 v){
        return centerCircle.position.plus(v.times(centerCircle.radius));
    }

    private Vector2 timeToVector(double t){
        double loop =DB.getInstance().getLoopTime();
        double n = Math.PI * 2 * t / loop - Math.PI * 0.5;
        return new Vector2(Math.cos(n), Math.sin(n));
    }
}
