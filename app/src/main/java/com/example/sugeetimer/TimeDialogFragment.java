package com.example.sugeetimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimeDialogFragment extends DialogFragment {
    private View _view = null;
    private Double _time;
    private EditText editText = null;

    private static TimeDialogFragment instance = null;


    private TimeDialogFragment(){}

    public TimeDialogFragment setEditText(EditText editText) {
        this.editText = editText;
        return this;
    }

    public static TimeDialogFragment getInstance(){
        if(instance == null){
            instance = new TimeDialogFragment();
            instance._time = 0.0;
        }
        else{
            instance._time = DB.getInstance().readStatus().time;
        }
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        _view = layoutInflater.inflate(R.layout.dialog_time, null, false);
        EditText editText = _view.findViewById(R.id.editText);
        if(_time == null){
            editText.setText("時間を入力");
        }
        else{
            editText.setText(String.valueOf(_time));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("タイトル")
                .setMessage("ここにメッセージを入力します")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        positiveClock();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .setNeutralButton("あとで", null)
                .setView(_view);
        return builder.create();
    }

    private void positiveClock(){
        EditText editText = _view.findViewById(R.id.editText);
        int in = Integer.parseInt(editText.getText().toString());
        in %= DB.getInstance().getLoopTime();
        DB.getInstance().setTime(in);
    }
}
