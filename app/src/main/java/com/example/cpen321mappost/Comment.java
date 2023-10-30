package com.example.cpen321mappost;

import android.app.Activity;

public class Comment {
    private String pid;
    private String userId;
    private String time;
    private String content;
    private static final ProfileManager profileManager = new ProfileManager();

    //ChatGPT usage: No
    public Comment(String pid, String uid, String time, String content) {

        this.pid = pid;
        this.userId = uid;
        this.time = time;
        this.content = content;

    }

    //ChatGPT usage: No
    public String getContent() {
        return content;
    }

    //ChatGPT usage: No
    public String getUid() {
        return userId;
    }

}
