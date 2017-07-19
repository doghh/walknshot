package cn.edu.sjtu.se.walknshot.androidclient.model;


import android.graphics.Bitmap;

public class Post {

    private int postId;
    private String title;
    private String body;
    private Bitmap cover;

    public Post(int postId, String title, String body, Bitmap cover) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.cover = cover;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
