package com.example.sugeetimer;

public class Vector2 {
    public double x;
    public double y;

    // コンストラクタ
    Vector2(){ x = 0; y = 0;}
    Vector2(double x, double y){this.x = x; this.y = y;}

    // 演算
    // +
    public Vector2 plus(double n){
        return plus(n,n);
    }
    public Vector2 plus(Vector2 v){
        return this.plus(v.x,v.y);
    }
    public Vector2 plus(double x, double y){
        return new Vector2(this.x + x, this.y + y);
    }
    // -
    public Vector2 minus(double n){
        return minus(n,n);
    }
    public Vector2 minus(Vector2 v){
        return this.minus(v.x,v.y);
    }
    public Vector2 minus(double x, double y){
        return new Vector2(this.x - x, this.y - y);
    }
    // *
    public Vector2 times(double n){
        return times(n,n);
    }
    public Vector2 times(double x, double y){
        return times(new Vector2(x,y));
    }
    public Vector2 times(Vector2 v){
        return new Vector2(x * v.x, y * v.y);
    }


    // 長さ
    private double Length(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }
    public double Length(){
        return this.Length(x,y);
    }
    // 正規化
    public Vector2 Normalized(){
        return new Vector2(x / this.Length(x,y), y / this.Length(x,y));
    }
}

