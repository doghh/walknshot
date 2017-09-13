package cn.edu.sjtu.se.walknshot.apiserver.entities;

import java.util.HashSet;
import java.util.Set;

public class PGroup {
    int id;
    Set<Picture> pictures = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }
}
