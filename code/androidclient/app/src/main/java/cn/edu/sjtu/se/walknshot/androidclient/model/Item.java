package cn.edu.sjtu.se.walknshot.androidclient.model;

import android.graphics.Bitmap;

public class Item {

    public static final int TITLE = 0;
    public static final int IMAGE = 1;
    public static final int BODY = 2;
    public static final int BOTTOM = 3;

    private int postId;
    private int styleType;//显示类型
    private String text;
    private Bitmap cover;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getStyleType() {
        return styleType;
    }

    public void setStyleType(int styleType) {
        this.styleType = styleType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
