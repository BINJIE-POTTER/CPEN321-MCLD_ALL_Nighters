package com.example.cpen321mappost;
public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userBirthdate;

    // Callback interface for user update
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

}