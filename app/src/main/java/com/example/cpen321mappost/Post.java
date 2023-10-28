package com.example.cpen321mappost;

public class Post {
    private String pid;
    private String userId;
    private String userName;  //need to request using userID
    private String time;
//    private String location;

    // Getter methods for the above attributes

        private double latitude;
        private double longitude;

        // Getter methods for the above attributes


        private String title;
        private String body;
        private String[] comments; // need to request using PID
        private int likeCount;

        // Getter methods for the above attributes

   public double getLatitude()
   {
       return latitude;
   }
   public double getLongitude()
   {
       return longitude;
   }
}

