package cn.edu.sjtu.se.walknshot.apimessages;

import java.util.ArrayList;
import java.util.List;

public class PGroupDetails {
    private int id;
    private List<PictureEntry> pictures = new ArrayList<>();
    private List<CommentEntry> comments = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PictureEntry> getPictures() {
        return pictures;
    }

    public void setPictures(List<PictureEntry> pictures) {
        this.pictures = pictures;
    }

    public List<CommentEntry> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntry> comments) {
        this.comments = comments;
    }
}
