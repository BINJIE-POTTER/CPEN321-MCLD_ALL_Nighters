package com.example.cpen321mappost;
import java.util.List;

public class Cluster {
    private int clusterId;
    private double latitude;
    private double longitude;
    private List<Post> posts;

    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }

    // getters, setters, and any other methods you need
}

class Post {
    private String _id;
    private String pid;
    private String userId;
    private String time;
    private String location;
    private Coordinate coordinate;
    private Content content;
    private int likeCount;

    // getters, setters, and any other methods you need
}

class Coordinate {
    private double latitude;
    private double longitude;

    // getters, setters, and any other methods you need
}

class Content {
    private String title;
    private String body;
    private List<String> tags;

    // getters, setters, and any other methods you need
}

