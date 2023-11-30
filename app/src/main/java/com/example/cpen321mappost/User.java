package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;

public class User {
    private static User instance = null;
    private static final ProfileManager profileManager = new ProfileManager();
    private static boolean loggedIn = true;
    private static final String TAG = "User";
    private String userId;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userBirthdate;
    private String token;
    private int postCount;
    private ArrayList<String> following;
    private ArrayList<String> followers;
    private ImageData userAvatar;

    //ChatGPT usage: Partial
    private User() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            this.userId = firebaseUser.getUid();
            this.userName = firebaseUser.getDisplayName();
            this.userEmail = firebaseUser.getEmail();

        } else {

            this.userId = "none";
            this.userName = "none";
            this.userEmail = "none";

        }

        this.userGender = "none";
        this.userBirthdate = "none";
        this.token = "";
        this.postCount = 0;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.userAvatar = new ImageData();

    }

    //ChatGPT usage: No
    public User (String userId) {

        this.userId = userId;
        this.userName = "none";
        this.userEmail = "none";
        this.userGender = "none";
        this.userBirthdate = "none";
        this.token = null;
        this.postCount = 0;
        this.following = null;
        this.followers = null;
        this.userAvatar = null;

    }

    public User (User user, String token, String userName, String userEmail, String userGender, String userBirthdate) {

        this.userId = user.getUserId();
        this.userName = userName == null ? user.getUserName() : userName;
        this.userEmail = userEmail == null ? user.getUserEmail() : userEmail;
        this.userGender = userGender == null ? user.getUserGender() : userGender;
        this.userBirthdate = userBirthdate == null ? user.getUserBirthdate() : userBirthdate;
        this.token = token == null ? user.getToken() : token;
        this.postCount = user.getPostCount();
        this.following = user.getFollowing();
        this.followers = user.getFollowers();
        this.userAvatar = null;

    }

    //ChatGPT usage: Partial
    public static synchronized User getInstance() {

        if (!isLoggedIn()) {

            instance = new User("-");

            return instance;

        }

        if (instance == null) {

            instance = new User();
            profileManager.getUserData(instance, new Activity(), new UserCallback() {
                @Override
                public String onSuccess(User user) {

                    Log.d(TAG, "USER LOGGED IN, DATA IS IN DATABASE");
                    Log.d(TAG, "OLD TOKEN: " + user.getToken());

                    updateToken(new TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {

                            user.setToken(token);
                            Log.d(TAG, "NEW TOKEN: " + user.getToken());

                            Gson gson = new Gson();
                            String jsonUserData = gson.toJson(user);

                            instance = gson.fromJson(jsonUserData, User.class);

                            Log.d(TAG, "USER LOGGED IN WITH UPDATED TOKEN DATA: " + jsonUserData);

                            profileManager.putUserData(new User(instance, token, null, null, null, null), new Activity(), new UserCallback() {
                                @Override
                                public String onSuccess(User user) {

                                    Log.d(TAG, "TOKEN SENT TO DATABASE: " + user.getToken());

                                    return null;

                                }

                                @Override
                                public void onFailure(Exception e) {

                                    Log.e(TAG, e.toString());

                                }
                            });

                        }

                        @Override
                        public void onError(Exception e) {

                            Log.e(TAG, e.toString());

                        }
                    });

                    return null;

                }

                @Override
                public void onFailure(Exception e) {

                    Log.d(TAG, "New User, user does not have account in database.");

                    profileManager.postUserData(instance, new Activity(), new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            Log.d(TAG, "New User account is created successfully.");
                            updateToken(new TokenCallback() {
                                @Override
                                public void onTokenReceived(String token) {

                                    user.setToken(token);
                                    Log.d(TAG, "NEW TOKEN: " + user.getToken());

                                    Gson gson = new Gson();
                                    String jsonUserData = gson.toJson(user);

                                    instance = gson.fromJson(jsonUserData, User.class);

                                    Log.d(TAG, "USER LOGGED IN WITH UPDATED TOKEN DATA: " + jsonUserData);

                                    profileManager.putUserData(instance, new Activity(), new UserCallback() {
                                        @Override
                                        public String onSuccess(User user) {

                                            Log.d(TAG, "TOKEN SENT TO DATABASE: " + user.getToken());

                                            return null;

                                        }

                                        @Override
                                        public void onFailure(Exception e) {

                                            Log.e(TAG, e.toString());

                                        }
                                    });

                                }

                                @Override
                                public void onError(Exception e) {

                                    Log.e(TAG, e.toString());

                                }
                            });

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.e(TAG, "FAILURE, new user account failed to create: " + e.toString());

                        }

                    });

                }

            });

        }

        return instance;

    }

    public static void updateToken(TokenCallback callback) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());

                        callback.onError(task.getException());

                    } else {

                        String retrievedToken = task.getResult();

                        Log.d(TAG, "FCM Token: " + retrievedToken);

                        callback.onTokenReceived(retrievedToken);

                    }

                });
    }

    //ChatGPT usage: Yes
    public interface UserCallback {
        String onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(Exception e);
    }

    //ChatGPT usage: No
    public String getUserId() {
        return userId;
    }

    //ChatGPT usage: No
    public void setUserId(String userId) {
        this.userId = userId;
    }

    //ChatGPT usage: No
    public String getUserName() {
        return userName;
    }

    //ChatGPT usage: No
    public void setUserName(String userName) {
        this.userName = userName;
    }

    //ChatGPT usage: No
    public String getUserEmail() {
        return userEmail;
    }

    //ChatGPT usage: No
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    //ChatGPT usage: No
    public String getUserGender() {
        return userGender;
    }

    //ChatGPT usage: No
    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    //ChatGPT usage: No
    public String getUserBirthdate() {
        return userBirthdate;
    }

    //ChatGPT usage: No
    public void setUserBirthdate(String userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ImageData getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(ImageData userAvatar) {
        this.userAvatar = userAvatar;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        User.loggedIn = loggedIn;
    }
    public static void setInstance(User user) {

        instance = user;

    }

}
