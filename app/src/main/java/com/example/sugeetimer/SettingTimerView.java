package com.example.sugeetimer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class SettingTimerView extends View {

    // 描画用
    private Paint paint;
    // 中央の円（タイマーの土台）
    private Circle              centerCircle;
    // タイムポイント管理用
    private int tpRadius = 100;

    // タイムポイントの選択番号
    public int selectTpNumber;

    // ガイドの数
    private int                 guideCount = 8;
    // ガイド管理用
    private ArrayList<Vector2>  guideList;
    // ガイドの半径
    private int                 correctionRadius = 10;

    // コンストラクタ
    public SettingTimerView(Context context){
        this(context, null);
    }

    public SettingTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void CallUpdate(){
        int n = DB.getInstance().getSelectNumber();
        selectTpNumber = n;
        invalidate();
    }

    private void init(){
        paint = new Paint();
        centerCircle = new Circle();
        guideList = new ArrayList<>();
        for(int i = 0; i < guideCount; i++){
            double angle = Math.toRadians(360.0 * i / guideCount);
            Vector2 v = new Vector2(Math.cos(angle), Math.sin(angle));
            guideList.add(v);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.argb(32,0,0,0));

        centerCircle.position.x = canvas.getWidth() * 0.5f;
        centerCircle.position.y = canvas.getHeight() * 0.5f;
        centerCircle.radius = Math.min(canvas.getWidth(), canvas.getHeight())  * 0.5f - 50;

        // 円
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        centerCircle.Draw(canvas,paint);

        Circle c = new Circle();
        // ガイドの描画
        paint.setColor(Color.BLACK);
        c.radius = correctionRadius;
        for (Vector2 p : guideList){
            c.position = centerOffset(p);
            c.Draw(canvas, paint);
        }

        // タイマーポイントの描画
        c.radius = tpRadius;
        for (int i = 0;i < DB.getInstance().getListSize();i++){
            tpStatus rStatus = DB.getInstance().readStatus(i);
            paint.setColor(rStatus.color);
            c.position = centerOffset(timeToVector(rStatus.time));
            c.Draw(canvas,paint);
        }
    }

    //画面（View）が操作されると呼び出される
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            // タップ処理
            case MotionEvent.ACTION_DOWN: this.TouchProcessing(event); break;
            // ドラッグ処理
            case MotionEvent.ACTION_MOVE: this.DragProcessing(event); break;
        }

        //画面の更新（onDrawの呼び出し）
        invalidate();
        return true;
    }


    // タイムポイント配列に新たに登録する
    private void ToRegister(MotionEvent event){
        //X座標を変数にセット //Y座標を変数にセット
        Vector2 v = new Vector2(event.getX(), event.getY());
        v = v.minus(centerCircle.position);
        if(centerCircle.radius - 75 < v.Length() && v.Length() < centerCircle.radius + 75){
            Vector2 vn = v.Normalized();
            Circle tp = new Circle();
            tp.radius = tpRadius;
            Vector2 position = centerOffset(vn);
            // ガイドに近ければ補正する
            //tp.position = this.GuideCorrection(position);
            position = this.GuideCorrection(position);
            //tpList.add(tp);
            position = position.minus(centerCircle.position).Normalized();
            DB.getInstance().Register(vectorToTime(position)); // 時間が点対象に反転している可能性アリ
        }
    }

    // タッチ処理
    private void TouchProcessing(MotionEvent event){
        // 既存のタイムポイントをタッチしたか否か
        boolean isTouch = false;
        // 選択番号初期化
        int count = 0;
        Circle c = new Circle();
        c.radius = tpRadius;
        for (int i = 0; i < DB.getInstance().getListSize(); i++){
            tpStatus rs = DB.getInstance().readStatus(i);
            c.position = centerOffset(timeToVector(rs.time));
            if(c.Collision(event.getX(), event.getY())){
                isTouch = true;
                break;
            }
            // 次の選択番号へ
            ++count;
        }
//        for(Circle tp : tpList){
//            if(tp.Collision(event.getX(), event.getY())){
//                isTouch = true;
//                break;
//            }
//            // 次の選択番号へ
//            ++count;
//        }
        if(isTouch == false){
            // タイムポイント配列に新たに登録する
            this.ToRegister(event);
        }
        // 選択判定
        selectTpNumber = count;
        DB.getInstance().setSelectNumber(selectTpNumber);
    }

    private void DragProcessing(MotionEvent event){
        if(selectTpNumber >= DB.getInstance().getListSize()){ return; }
        // タップの蓄積データ数取得
        int historySize = event.getHistorySize() - 1;
        if(historySize < 0){ return; }
        // 最新のタップ位置の取得
        float x = event.getHistoricalX(0,historySize);
        float y = event.getHistoricalY(0,historySize);
        Vector2 v = new Vector2(x, y);
        v = v.minus(centerCircle.position).Normalized();
        // 位置の更新
        //tpList.get(selectTpNumber).position = centerCircle.position.plus(v.times(centerCircle.radius));
        DB.getInstance().setTime(vectorToTime(v));
    }

    private Vector2 GuideCorrection(Vector2 position){
        Circle circle = new Circle(0, 0, correctionRadius);
        for (Vector2 g : guideList){
            circle.position = centerOffset(g);
            if(circle.Collision(position)){
                return circle.position;
            }
        }
        return position;
    }

    public void Copy(){
//        // tp のコピー
//        ArrayList<Circle> buf = new ArrayList<>();
//        for (int i = tpSelectList.size() - 1; i >= 0; i--){
//            if(tpSelectList.get(i) == true){
//                buf.add(new Circle(tpList.get(i)));
//            }
//        }
//        tpList.addAll(buf);
//        // selectList増やす
//        Boolean[] b = new Boolean[buf.size()];
//        Arrays.fill(b, false);
//        tpSelectList.addAll(new ArrayList<Boolean>(Arrays.asList(b)));
//        //再描画
//        invalidate();
    }

    public void Delete(){
        DB.getInstance().deleteItem();
        //再描画
        invalidate();
    }

    private Vector2 centerOffset(Vector2 v){
        return centerCircle.position.plus(v.times(centerCircle.radius));
    }

    private Vector2 timeToVector(double t){
        double loop = DB.getInstance().getLoopTime();
        double n = Math.PI * 2 * t / loop - Math.PI * 0.5;
        return new Vector2(Math.cos(n), Math.sin(n));
    }

    private double vectorToTime(Vector2 v){
        double r = Math.atan2(v.x,-v.y) / Math.PI;
        double loop = DB.getInstance().getLoopTime();
        if(r >= 0){
            return loop * 0.5 * r;
        }
        else{
//            return (int)(loop + loop * 0.5 * r);
            return loop * (1 + 0.5 * r);
        }
    }
}

