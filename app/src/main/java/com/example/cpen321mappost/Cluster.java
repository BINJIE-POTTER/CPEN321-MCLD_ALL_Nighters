package com.example.cpen321mappost;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private int clusterId;
    private double latitude;
    private double longitude;
    private List<Post> posts;

    //ChatGPT usage: No
    public int getClusterId() {
        return clusterId;
    }

    //ChatGPT usage: No
    public double getLatitude() {
        return latitude;
    }

    //ChatGPT usage: No
    public double getLongitude() {
        return longitude;
    }

    //ChatGPT usage: No
    public List<Post> getPosts() {
        return posts;
    }

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
    private ArrayList<String> likeList = new ArrayList<>();

    //ChatGPT usage: No
    public String getId() {
        return _id;
    }

    //ChatGPT usage: No
    public String getPid() {
        return pid;
    }

    //ChatGPT usage: No
    public String getUserId() {
        return userId;
    }

    //ChatGPT usage: No
    public String getTime() {
        return time;
    }

    //ChatGPT usage: No
    public String getLocation() {
        return location;
    }

    //ChatGPT usage: No
    public Coordinate getCoordinate() {
        return coordinate;
    }

    //ChatGPT usage: No
    public Content getContent() {
        return content;
    }

    //ChatGPT usage: No
    public int getLikeCount() {
        return likeCount;
    }
    public int getLikeListSize() {
        return likeList.size();
    }
    public ArrayList<String> getLikeList(){
        return likeList;
    }

    //ChatGPT usage: Yes
    public interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(Exception e);
    }

}

class Coordinate {
    private double latitude;
    private double longitude;

    //ChatGPT usage: No
    public double getLatitude() {
        return latitude;
    }

    //ChatGPT usage: No
    public double getLongitude() {
        return longitude;
    }

}

class Content {
    private String title;
    private String body;
    private List<String> tags;

    //ChatGPT usage: No
    public String getTitle() {
        return title;
    }

    //ChatGPT usage: No
    public String getBody() {
        return body;
    }

    //ChatGPT usage: No
    public List<String> getTags() {
        return tags;
    }

}

