package com.example.cpen321mappost;

import android.app.Activity;

public class Comment {

    private String pid;
    private String uid;
    private String time;
    private String content;

    public String getContent()
    {
        return content;
    }
    public String getUserName()
    {
        PostManager postManager=new PostManager();
        final String[] userName = new String[1];
        // change this -> new Activity()
        postManager.getSinglePostData(this.pid, new Activity(), new PostManager.JsonCallback<Post>() {
            @Override
            public void onSuccess(Post post) {

                uid=post.getUserId();
                //userName[0] =post.getAuthor(uid);
                post.getAuthor(uid, new Post.AuthorCallback() {
                    @Override
                    public void onAuthorRetrieved(String authorName) {
                        userName[0] = authorName;
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

            }
            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });
        return userName[0];
    }

}
