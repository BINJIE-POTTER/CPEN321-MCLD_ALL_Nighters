package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileManager {
    final static String TAG = "ProfileManager Activity";

    //ChatGPT usage: Yes
    public void getUserData(User user, final Activity activity, final User.UserCallback callback) {

        String url = "http://4.204.251.146:8081/users/?userId=" + user.getUserId();
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // runOnUiThread means safe to update UI components like text
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to get user data");
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!response.isSuccessful()) {
                                Log.d(TAG, "Unexpected server response, the code is: " + response.code());
                                callback.onFailure(new IOException("Unexpected response " + response));
                                return;
                            }
                            if (response.code() == 200) {
                                Log.d(TAG, "Succeed on get");

                                assert response.body() != null;
                                String responseData = response.body().string(); // your response data
                                Gson gson = new Gson();
                                User user = gson.fromJson(responseData, User.class);
                                callback.onSuccess(user); // already on UI thread, safe to call directly
                            } else {
                                callback.onFailure(new IOException("Unexpected response " + response));
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception handling response", e);
                            callback.onFailure(e); // handle or pass IOException
                        } finally {
                            response.close(); // Important to avoid resource leaks
                        }
                    }
                });
            }
        });
    }

    //ChatGPT usage: Yes
    public void putUserData(User user, final Activity activity, final User.UserCallback callback) {

        // chage this to PUT
        String url = "http://4.204.251.146:8081/users/update-profile";

        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the User object to JSON format
        Gson gson = new Gson();
        String jsonUserData = gson.toJson(user); // 'user' is your User instance

        Log.d(TAG, "PUT: changing user data");
        Log.d(TAG, jsonUserData);

        // Create a request body with the JSON representation of the user
        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to update user data");
                        callback.onFailure(e); // must be run on UI thread if updating UI components
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Here, you can also parse the response and create a User object if your API returns the updated user as a response.
                // Otherwise, you might just want to confirm the success without parsing the response.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // If you're just confirming the success, you can create a new 'success' method in your callback or pass a null user.
                        // Or, if you've parsed a response, pass the updated user here.
                        if (response.isSuccessful()) {
                            try {
                                // Here, we're not parsing a JSON response body, so we check the response directly.
                                if (response.code() == 200) {
                                    // The operation was successful. Notify the callback.
                                    callback.onSuccess(user);
                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    callback.onFailure(new IOException("Unexpected response when updating user: " + response));
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            callback.onFailure(new IOException("Unexpected code " + response));
                        }
                    }
                });
            }
        });
    }

    //ChatGPT usage: Yes
    public void postUserData(User user, final Activity activity, final User.UserCallback callback){

        String url = "http://4.204.251.146:8081/users";

        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the User object to JSON format
        Gson gson = new Gson();
        String jsonUserData = gson.toJson(user); // 'user' is your User instance

        Log.d(TAG, "This is user data: " + jsonUserData);

        // Create a request body with the JSON representation of the user
        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Failed to post user data", e);
                        callback.onFailure(e); // must be run on UI thread if updating UI components
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Here, you can also parse the response and create a User object if your API returns the updated user as a response.
                // Otherwise, you might just want to confirm the success without parsing the response.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // If you're just confirming the success, you can create a new 'success' method in your callback or pass a null user.
                        // Or, if you've parsed a response, pass the updated user here.
                        if (response.isSuccessful()) {
                            try {
                                // Here, we're not parsing a JSON response body, so we check the response directly.
                                if (response.code() == 200) {
                                    // The operation was successful. Notify the callback.
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(user); // must be run on UI thread if updating UI components
                                        }
                                    });

                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    callback.onFailure(new IOException("Unexpected response when updating user: " + response));
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            callback.onFailure(new IOException("Unexpected code " + response));
                        }
                    }
                });
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
                    Log.d(TAG,"preparing to send author name");
                    callback.onAuthorRetrieved(user.getUserName());
                } else {
                    // Handle the case where the user data is not available or parsing failed
                    callback.onError(new Exception("User data is not available"));
                }

                return null;
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    //ChatGPT usage: Partial
    public void followAuthor(String userId, final Activity activity, final FollowingUserCallback callback) {

        String url = "http://4.204.251.146:8081/users/follow";
        OkHttpClient httpClient = HttpClient.getInstance();

        FollowingUser followingUser = new FollowingUser(User.getInstance().getUserId(), userId);

        Gson gson = new Gson();
        String jsonFollowingUserData = gson.toJson(followingUser);

        Log.d(TAG, jsonFollowingUserData);

        RequestBody body = RequestBody.create(jsonFollowingUserData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {
                    Log.d(TAG, "Failed to update user data");
                    callback.onFailure(e);
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                activity.runOnUiThread(() -> {
                    // If you're just confirming the success, you can create a new 'success' method in your callback or pass a null user.
                    // Or, if you've parsed a response, pass the updated user here.
                    if (response.isSuccessful()) {
                        try {
                            // Here, we're not parsing a JSON response body, so we check the response directly.
                            if (response.code() == 200) {
                                // The operation was successful. Notify the callback.
                                callback.onSuccess(userId);
                            } else {
                                // Handle other response codes (like 4xx or 5xx errors)
                                callback.onFailure(new IOException("Unexpected response when updating user: " + response));
                            }
                        } finally {
                            response.close(); // Important to avoid leaking resources
                        }
                    } else {
                        callback.onFailure(new IOException("Unexpected code " + response));
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