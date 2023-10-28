package com.example.cpen321mappost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userBirthdate;

    public User() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        this.userId = firebaseUser.getUid();
        this.userName = firebaseUser.getDisplayName();
        this.userEmail = firebaseUser.getEmail();
        this.userGender = "none";
        this.userBirthdate = "none";

    }

    // Callback interface for user update
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

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

}