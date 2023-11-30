package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileManager {
    final static String TAG = "ProfileManager Activity";
    final Gson gson = new Gson();

    //ChatGPT usage: Yes
    public void getUserData(User user, final Activity activity, final User.UserCallback callback) {

        String url = "https://4.204.251.146:3000/users/?userId=" + user.getUserId();
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE GET USER: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "GET USER SUCCEED");

                    assert response.body() != null;
                    String responseData = null;
                    try {
                        responseData = response.body().string();
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }

                    User user = gson.fromJson(responseData, User.class);

                    activity.runOnUiThread(() -> callback.onSuccess(user));

                }
            }
        });
    }

    //ChatGPT usage: Yes
    public void putUserData(User user, final Activity activity, final User.UserCallback callback) {

        String url = "https://4.204.251.146:3000/users/update-profile";
        OkHttpClient httpClient = HttpClient.getInstance();

        String jsonUserData = gson.toJson(user);

        Log.d(TAG, "PUT NEW DATA: " + jsonUserData);

        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE PUT USER: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "PUT USER SUCCEED");

                    activity.runOnUiThread(() -> callback.onSuccess(user));

                }
            }
        });
    }

    //ChatGPT usage: Yes
    public void postUserData(User user, final Activity activity, final User.UserCallback callback){

        String url = "https://4.204.251.146:3000/users";
        OkHttpClient httpClient = HttpClient.getInstance();

        String jsonUserData = gson.toJson(user);

        Log.d(TAG, "POST USER DATA: " + jsonUserData);

        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE POST USER: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "POST USER SUCCEED");

                    activity.runOnUiThread(() -> callback.onSuccess(user));

                }
            }
        });
    }

    //ChatGPT usage: Yes
    public interface AuthorCallback {
        void onAuthorRetrieved(String authorName);
        void onError(Exception e);
    }

    //ChatGPT usage: Partial
    public void getAuthor(String userId, AuthorCallback callback) {

        User author = new User(userId);

        ProfileManager profileManager = new ProfileManager();
        profileManager.getUserData(author, new Activity(), new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                if (user != null) {

                    Log.d(TAG,"AUTHOR fetched, returning author name...");

                    callback.onAuthorRetrieved(user.getUserName());

                } else {

                    callback.onError(new Exception("User data is not available"));

                }

                return "";

            }

            @Override
            public void onFailure(Exception e) {

                callback.onError(e);

            }
        });
    }

    //ChatGPT usage: Partial
    public void followAuthor(boolean isFollowing, String userId, final Activity activity, final FollowingUserCallback callback) {

        String url;
        if (isFollowing) url= "https://4.204.251.146:3000/users/unfollow";
        else             url= "https://4.204.251.146:3000/users/follow";
        OkHttpClient httpClient = HttpClient.getInstance();

        FollowingUser followingUser = new FollowingUser(User.getInstance().getUserId(), userId);

        String jsonFollowingUserData = gson.toJson(followingUser);

        if (isFollowing) Log.d(TAG, "UNFOLLOW AUTHOR data: " + jsonFollowingUserData);
        else             Log.d(TAG, "FOLLOW AUTHOR data: " + jsonFollowingUserData);

        RequestBody body = RequestBody.create(jsonFollowingUserData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                if (isFollowing) Log.e(TAG, "FAILURE UNFOLLOWING AUTHOR: " + e);
                else             Log.e(TAG, "FAILURE FOLLOWING AUTHOR: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    if (isFollowing) Log.d(TAG, "UNFOLLOW AUTHOR SUCCEED");
                    else             Log.d(TAG, "FOLLOW AUTHOR SUCCEED");

                    activity.runOnUiThread(() -> callback.onSuccess(userId));

                }
            }
        });
    }

    public void uploadUserAvatar(File avatarFile, final Activity activity, final User.UserCallback callback) {

        String url = "https://4.204.251.146:3000/users/update-avatar";
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Increase connect timeout
                .writeTimeout(10, TimeUnit.SECONDS) // Increase write timeout
                .readTimeout(10, TimeUnit.SECONDS) // Increase read timeout
                .build();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), avatarFile);
        builder.addFormDataPart("image", avatarFile.getName(), fileBody);
        builder.addFormDataPart("userId", User.getInstance().getUserId());

        RequestBody requestBody = builder.build();

        Log.d(TAG, requestBody.toString());

        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE UPLOAD USER AVATAR: " + e);

                    callback.onFailure(e);

                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(() -> {

                    if (!response.isSuccessful()) {

                        Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                        callback.onFailure(new IOException("Unexpected response " + response));

                    } else {

                        Log.d(TAG, "PUT USER SUCCEED");

                        callback.onSuccess(null);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Yes
    public interface FollowingUserCallback {
        void onSuccess(String userId);
        void onFailure(Exception e);
    }

    //ChatGPT usage: Yes
    public static class FollowingUser {
        private String followingId;
        private String userId;

        // Constructor
        public FollowingUser(String userId, String followingId) {
            this.userId = userId;
            this.followingId = followingId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFollowingId() {
            return followingId;
        }

        public void setFollowingId(String followingId) {
            this.followingId = followingId;
        }

    }

}