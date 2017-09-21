package com.example.nario.draglayout;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;

public class ChatModel implements Serializable{
    private String icon="";
    private Uri img;
    private String content="";
    private String type="";
    private Bitmap bitmap;

    public String getIcon() {
        return icon;
    }
    public Uri getimg(){
        return img;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }


    public void setImg(Uri img){
        this.img = img;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
