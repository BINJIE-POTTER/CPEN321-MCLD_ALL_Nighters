package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class User {
    private static User instance = null;
    private static ProfileManager profileManager = null;
    private static final String TAG = "User";

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
            profileManager = new ProfileManager();

            profileManager.getUserData(instance, new UserCallback() {
                @Override
                public void onSuccess(User user) {

                    Log.d(TAG, "IN the user class, succeed in getting user data");

                    Gson gson = new Gson();
                    String jsonUserData = gson.toJson(user);

                    Log.d(TAG, jsonUserData);

                    instance = gson.fromJson(jsonUserData, User.class);

                }

                @Override
                public void onFailure(Exception e) {

                    profileManager.postUserData(instance, new User.UserCallback() {
                        @Override
                        public void onSuccess(User user) {

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }

                    }, new Activity());

                }
            }, new Activity());

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserBirthdate() {
        return userBirthdate;
    }

    public void setUserBirthdate(String userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    // Add other methods here...
}
