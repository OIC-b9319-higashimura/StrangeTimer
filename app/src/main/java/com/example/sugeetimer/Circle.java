package com.example.sugeetimer;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Circle {
    public Vector2 position;
    public double radius;

    // コンストラクタ
    Circle(){
        position = new Vector2();
        radius = 0;
    }
    Circle(double x, double y, double r){
        position = new Vector2(x,y);
        radius = r;
    }
    Circle(Vector2 v, double r){
        position = v;
        radius = r;
    }
    // コピーコンストラクタ
    Circle(Circle c){
        position = c.position;
        radius = c.radius;
    }

    // 点との当たり判定
    public boolean Collision(double x, double y){
        return Collision(new Vector2(x,y));
    }
    public boolean Collision(Vector2 p){
        Vector2 v = p.minus(position);
        return radius >= v.Length();
    }

    // 描画
    public void Draw(Canvas canvas, Paint paint){
        canvas.drawCircle((float)position.x, (float)position.y, (float)radius, paint);
    }
}
