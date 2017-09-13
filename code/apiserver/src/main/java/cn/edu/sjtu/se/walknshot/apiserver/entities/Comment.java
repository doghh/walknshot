package cn.edu.sjtu.se.walknshot.apiserver.entities;

import java.util.Date;

public class Comment {
    long id;
    int userId;
    String content;
    Date time;
    int pgroupId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPgroupId() {
        return pgroupId;
    }

    public void setPgroupId(int pgroupId) {
        this.pgroupId = pgroupId;
    }
}
