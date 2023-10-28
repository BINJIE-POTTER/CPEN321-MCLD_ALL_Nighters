package com.example.cpen321mappost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private static User instance = null;

    // User attributes
    private String userId;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userBirthdate;

    // Private constructor so no other class can instantiate
    private User() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null; // Consider proper error handling here

        this.userId = firebaseUser.getUid();
        this.userName = firebaseUser.getDisplayName();
        this.userEmail = firebaseUser.getEmail();
        // Default values, consider providing ways to update these fields
        this.userGender = "none";
        this.userBirthdate = "none";
    }

    // Method to get the single instance of user
    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    // Getters and possibly setters if you need to change user's attributes after instantiation

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserBirthdate() {
        return userBirthdate;
    }

    // Add other methods here...
}
