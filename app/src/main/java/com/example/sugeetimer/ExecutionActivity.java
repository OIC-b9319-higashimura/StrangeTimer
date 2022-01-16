package com.example.sugeetimer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ExecutionActivity extends AppCompatActivity {

    ExecutionTimerView executionTimerView;

    Button nextPoint;
    Button prevPoint;
    Button nextLoop;
    Button prevLoop;

    int soundEffect;
    SoundPool soundPool;

    ImageView startStopIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution);

        setSoundPool();

        executionTimerView = findViewById(R.id.timer_view);
        executionTimerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(executionTimerView.isStart() == false){
                    executionTimerView.timerStart();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stop);
                    startStopIcon.setImageBitmap(bitmap);
                }
                else{
                    executionTimerView.timerStop();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_start);
                    startStopIcon.setImageBitmap(bitmap);
                }
            }
        });
        executionTimerView.setSoundPoolListener(new ExecutionTimerView.SoundPoolListener() {
            @Override
            public void onSound() {
                // play(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
                soundPool.play(soundEffect, 1.0f, 1.0f, 0, 0, 1);
            }
        });



        nextPoint = findViewById(R.id.next_point);
        nextPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executionTimerView.nextPoint();
            }
        });

        prevPoint = findViewById(R.id.prev_point);
        prevPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executionTimerView.prevPoint();
            }
        });

        nextLoop = findViewById(R.id.next_loop);
        nextLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executionTimerView.nextLoop();
            }
        });

        prevLoop = findViewById(R.id.prev_loop);
        prevLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executionTimerView.prevLoop();
            }
        });
        startStopIcon = findViewById(R.id.iv_start_stop_icon);
        startStopIcon.setAlpha(1/4.0f);
    }

    private void setSoundPool(){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(2)
                .build();

        // one.wav をロードしておく
        soundEffect = soundPool.load(this, R.raw.button_push, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });
    }
}