package com.example.sugeetimer;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity{

    // ボタン
    private Button btAllSelect;
    private Button btCopy;
    private Button btDelete;
    private Button btCancel;
    private Button btRun;

    // 詳細情報画面
    private boolean isInfoShow;
    private DrawFillView _color;
    private TextView _time;
    private Switch swVibration;
    // ダイアログ
    private ColorPickerDialogFragment colorPickerDialogFragment;
    private TimeDialogFragment timeDialogFragment;
    // クリック判定取得用
    private ConstraintLayout informationView_color;
    private ConstraintLayout informationView_time;

    // Drew画面
    private SettingTimerView sugeeTimer;
    //
    private EditText loopTime;
    private EditText loopCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sugeeTimer = findViewById(R.id.testView);
        Init();

        DB.getInstance().setLoopTime(Integer.parseInt(loopTime.getText().toString()));
        DB.getInstance().setLoopCount(Integer.parseInt(loopCount.getText().toString()));
        DB.getInstance().type = DB.TimerType.Seconds;
        DB.getInstance().setUpdateListener(new DB.UpdateListener(){
            @Override
            public void onUpdate() {
                updateInformation();
                sugeeTimer.CallUpdate();
            }
        });
    }

    private void Init(){
        setBtAllSelect();
        setBtCopy();
        setBtDelete();
        setBtCancel();
        setBtRun();

        setEditText();

        setViewInformation();
        updateInformation();
    }


    // ボタンのイベントリスナー初期化
    private void setBtAllSelect(){
//        btAllSelect = findViewById(R.id.btAllSelect);
//        View.OnClickListener buttonClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sugeeTimer.AllSelect();
//            }
//        };
//        btAllSelect.setOnClickListener(buttonClick);
    }
    private void setBtCopy(){
        btCopy = findViewById(R.id.btCopy);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugeeTimer.Copy();
            }
        };
        btCopy.setOnClickListener(clickListener);
    }
    private void setBtDelete(){
        btDelete = findViewById(R.id.btDelete);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugeeTimer.Delete();
            }
        };
        btDelete.setOnClickListener(clickListener);
    }
    private void setBtCancel(){
        btCancel = findViewById(R.id.btCancel);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug","click");
            }
        };
        btCancel.setOnClickListener(clickListener);
    }
    private void setBtRun(){
        btRun = findViewById(R.id.btRun);
        btRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loopCount.getText().toString() == "0"){DB.getInstance().setLoopCount(Integer.MAX_VALUE);}
                Intent intent = new Intent(MainActivity.this, ExecutionActivity.class);
                startActivity(intent);
            }
        });
    }

    //
    private void setEditText() {
        loopTime = findViewById(R.id.et_loopTime);
        loopTime.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(OnEnterKeyEvent(keyCode, event, loopTime)){
                    String s = loopTime.getText().toString();
                    if(s == null){return false;}
                    DB.getInstance().setLoopTime(Integer.parseInt(s));
                    return true;
                }
                return false;
            }
        });
        loopCount = findViewById(R.id.et_loopCount);
        loopCount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(OnEnterKeyEvent(keyCode, event, loopCount)){
                    String s = loopCount.getText().toString();
                    if(s == null){return false;}
                    DB.getInstance().setLoopCount(Integer.parseInt(s));
                    return true;
                }
                return false;
            }
        });
    }
    private boolean OnEnterKeyEvent(int keyCode, KeyEvent event, EditText editText){
        //イベントを取得するタイミングには、ボタンが押されてなおかつエンターキーだったときを指定
        if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
            //キーボードを閉じる
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            editText.clearFocus();
            return true;
        }
        return false;
    }

    // 詳細画面
    private void setViewInformation(){
        _color = findViewById(R.id.drawFillView);
        _time = findViewById(R.id.information_time_text);
        informationView_color = findViewById(R.id.information_color_layout);
        informationView_time = findViewById(R.id.information_time_layout);

        informationView_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { clickColor(); }
        });
        informationView_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { clickTime(); }
        });
    }
    private void clickColor(){
        Log.d("", "clickColor: ");
        ColorPickerDialogFragment dialogFragment = null;
        dialogFragment.getInstance().show(getSupportFragmentManager(), "my_dialog");
    }
    private void clickTime(){
        TimeDialogFragment dialogFragment = null;
        dialogFragment.getInstance().show(getSupportFragmentManager(), "my_dialog");

    }

    private void setSwVibration(){

        swVibration = findViewById(R.id.swVibration);
        swVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //db.getInstance().updateStatus().isVibration = isChecked;
            }
        });
    }

    private void updateInformation(){
        int size = DB.getInstance().getListSize();
        int number = DB.getInstance().getSelectNumber();
        if(size == 0) {
            isInfoShow = false;
            changLayout(R.layout.infomation_unselected, R.string.unregistered);
        }
        else if(number < 0 || size <= number){
            isInfoShow = false;
            changLayout(R.layout.infomation_unselected, R.string.unselected);
        }
        else if(isInfoShow == false){
            isInfoShow = true;
            changLayout(R.layout.information_custom_cons);
            setViewInformation();
        }

        tpStatus rStatus = DB.getInstance().readStatus();
        if(rStatus == null){return;}
        _color.set_color(rStatus.color);
        _time.setText(String.valueOf(rStatus.time));
    }

    void changLayout(@LayoutRes int id_layout){
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.information_view_layout);
        layout.removeAllViews();
        getLayoutInflater().inflate(id_layout, layout);
    }

    void changLayout(@LayoutRes int id_layout, @StringRes int id_text){
        changLayout(id_layout);
        TextView textView = findViewById(R.id.info_unselected_textView);
        textView.setText(id_text);
    }


}
