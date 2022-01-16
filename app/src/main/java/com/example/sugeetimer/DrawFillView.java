package com.example.sugeetimer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawFillView extends View {

    private Paint paint;
    private int color = Color.WHITE;

    public DrawFillView(Context context) {
        super(context);
    }

    public DrawFillView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawFillView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawFillView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public int get_color() {
        return color;
    }

    public void set_color(int color) {
        this.color = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 下地
        canvas.drawColor(color);
        // 枠
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        Rect r = new Rect(0,0,w,h);
        canvas.drawRect(r,paint);
    }
}
