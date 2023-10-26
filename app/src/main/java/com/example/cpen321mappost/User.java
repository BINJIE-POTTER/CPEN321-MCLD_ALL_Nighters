package com.example.cpen321mappost;

import com.google.firebase.auth.FirebaseUser;

public class User {
        private String userId;
        private String userName;
        private String userEmail;
        private String gender;
        private String age;
        //TODO: gender and age could be changed in profile manager


    // ... any other attributes you want to store for a user

        public User(String userId, String userName, String userEmail) {
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
        }
    // Static method to initialize a User from a FirebaseUser
    public static User initializeUser(FirebaseUser firebaseUser) {
         String userName=firebaseUser.getDisplayName();
         String userEmail= firebaseUser.getEmail();

        //TODO: Here call Post method to create a user, pass the email and get the uid
         String userId="";

        return new User(userId, userName, userEmail);
    }


}
