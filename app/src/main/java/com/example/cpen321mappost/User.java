package com.example.cpen321mappost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private static String userId;
    private static String userName;
    private static String userEmail;
    private static String userGender;
    private static String userBirthdate;

    public User() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        userId = firebaseUser.getUid();
        userName = firebaseUser.getDisplayName();
        userEmail = firebaseUser.getEmail();
        userGender = "none";
        userBirthdate = "none";

    }

    // Callback interface for user update
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getUserGender() {
        return userGender;
    }

    public static String getUserBirthdate() {
        return userBirthdate;
    }

}