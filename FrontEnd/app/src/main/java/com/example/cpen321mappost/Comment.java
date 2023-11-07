package com.example.cpen321mappost;

public class Comment {
    private String pid;
    private String userId;
    private String time;
    private String content;

    //ChatGPT usage: No
    public Comment(String pid, String uid, String time, String content) {

        this.pid = pid; //This is false positive
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
    public String getPid() {
        return pid;
    }
    public String getTime() {
        return time;
    }




}
