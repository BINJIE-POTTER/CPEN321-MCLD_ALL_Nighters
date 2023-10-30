package com.example.cpen321mappost;

import android.app.Activity;

public class Comment {
    private String pid;
    private String userId;
    private String time;
    private String content;
    private static final ProfileManager profileManager = new ProfileManager();

    public Comment(String pid, String uid, String time, String content) {

        this.pid = pid;
        this.userId = uid;
        this.time = time;
        this.content = content;

    }
    public String getContent() {
        return content;
    }
    public String getUid() {
        return userId;
    }

}
