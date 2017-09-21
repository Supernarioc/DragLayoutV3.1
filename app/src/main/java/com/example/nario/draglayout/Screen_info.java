package com.example.nario.draglayout;

/**
 * Created by nario on 2017/9/19.
 */

public class Screen_info {
    private int Width;
    private int Height;

    public Screen_info(int width,int height){
        this.Width=width;
        this.Height=height;
    }

    public int getWid(){
        return Width;
    }
    public int getHei(){
        return Height;
    }
}
