package com.example.sugeetimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jaredrummler.android.colorpicker.ColorPickerView;

public class ColorPickerDialogFragment extends DialogFragment{

    private View _view = null;
    private int _color;

    private static ColorPickerDialogFragment instance = null;

    private ColorPickerDialogFragment(){}

    public static ColorPickerDialogFragment getInstance(){
        if(instance == null){
            instance = new ColorPickerDialogFragment();
            instance._color = Color.WHITE;
        }
        else{
            instance._color = DB.getInstance().readStatus().color;
        }
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        _view = layoutInflater.inflate(R.layout.dialog_colorpicker, null, false);
        ColorPickerView colorPicker = _view.findViewById(R.id.colorPicker);
        colorPicker.setColor(_color);
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
        ColorPickerView colorPicker = _view.findViewById(R.id.colorPicker);
        _color = colorPicker.getColor();
        DB.getInstance().setColor(_color);
    }
}


