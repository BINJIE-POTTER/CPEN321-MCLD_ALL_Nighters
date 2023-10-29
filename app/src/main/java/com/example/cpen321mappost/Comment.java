package com.example.cpen321mappost;

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
        postManager.getSinglePostData(this.pid, this, new PostManager.JsonCallback<Post>() {
            @Override
            public void onSuccess(Post post) {

                uid=post.getUserId();
                userName[0] =post.getAuthor(uid);

            }
            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });
        return userName[0];
    }

}
