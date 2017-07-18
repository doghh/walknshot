package cn.edu.sjtu.se.walknshot.apimessages;

import java.util.ArrayList;
import java.util.List;

public class PGroupDetails {
    private int id;
    private List<PictureEntry> pictures = new ArrayList<>();

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
}
