package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

public class Cluster {
    private int clusterId;
    private double latitude;
    private double longitude;
    private List<Post> posts;

    public int getClusterId() {
        return clusterId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Post> getPosts() {
        return posts;
    }

    // Additional methods like setters or others can be added here
}

class Post {
    final static String TAG = "Post Activity";
    private String _id;
    private String pid;
    private String userId;
    private String time;
    private String location;
    private Coordinate coordinate;
    private Content content;
    private int likeCount;

    public String getId() {
        return _id;
    }

    public String getPid() {
        return pid;
    }

    public String getUserId() {
        return userId;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Content getContent() {
        return content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public interface AuthorCallback {
        void onAuthorRetrieved(String authorName);
        void onError(Exception e);
    }

    // Additional methods like setters or others can be added here
    public void getAuthor(String userId, AuthorCallback callback) {

        User author = new User(userId);

        ProfileManager profileManager = new ProfileManager();

        profileManager.getUserData(author, new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                if (user != null) {
                    callback.onAuthorRetrieved(user.getUserName());
                } else {
                    // Handle the case where the user data is not available or parsing failed
                    callback.onError(new Exception("User data is not available"));
                }

                return null;
            }

            @Override
            public void onFailure(Exception e) {

            }
        }, new Activity());

    }

    public interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(Exception e);
    }
}

class Coordinate {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Additional methods like setters or others can be added here
}

class Content {
    private String title;
    private String body;
    private List<String> tags;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public List<String> getTags() {
        return tags;
    }

    // Additional methods like setters or others can be added here
}

