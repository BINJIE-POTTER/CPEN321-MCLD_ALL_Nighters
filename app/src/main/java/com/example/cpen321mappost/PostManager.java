package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostManager {
    private static final String TAG = "PostManager";
    final Gson gson = new Gson();

    //ChatGPT usage: Partial
    public void getSinglePostData(String pid, final Activity activity, final JsonCallback<Post> callback){

        String url = "https://4.204.251.146:3000/posts/single/?pid=" + pid;
        OkHttpClient httpClient = HttpClient.getInstance();

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET POST: " + e);

                    callback.onFailure(e);

                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response " + response);
                }

                // Process the response in the background thread
                assert response.body() != null;
                final String responseData = response.body().string();
                Post post = gson.fromJson(responseData, Post.class);

                // Switch to the main thread to update UI
                activity.runOnUiThread(() -> {
                    Log.d(TAG, "GET POST SUCCEED");
                    callback.onSuccess(post);
                });
            }
        });
    }

    //ChatGPT usage: Yes
    public void deletePostData(String pid, final Activity activity, final JsonCallback<Void> callback){

        String url = "https://4.204.251.146:3000/posts/?pid=" + pid;
        OkHttpClient httpClient = HttpClient.getInstance();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE DELETE USER: " + e);

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

                        Log.d(TAG, "DELETE POST SUCCEED");

                        callback.onSuccess(null);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void likePostData(boolean like, String pid, String userId, final Activity activity, final JsonCallback<Void> callback) {

        String url;
        if (like) url = "https://4.204.251.146:3000/posts/like";
        else      url = "https://4.204.251.146:3000/posts/unlike";
        OkHttpClient httpClient = HttpClient.getInstance();

        JsonObject likeInfo = new JsonObject();
        likeInfo.addProperty("userId", userId);
        likeInfo.addProperty("pid", pid);
        String likeInfoString = likeInfo.toString();

        if (like) Log.d(TAG, "This is the LIKED post data: " + likeInfoString);
        else      Log.d(TAG, "This is the UNLIKED post data: " + likeInfoString);

        RequestBody body = RequestBody.create(likeInfoString, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    if (like) Log.e(TAG, "FAILURE LIKE POST: " + e);
                    else      Log.e(TAG, "FAILURE UNLIKE POST: " + e);

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

                        if (like) Log.d(TAG, "LIKE POST SUCCEED");
                        else      Log.d(TAG, "UNLIKE POST SUCCEED");

                        callback.onSuccess(null);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getTagsData(String latitude, String longitute, final Activity activity, final JsonCallback< ArrayList<String>> callback){

        String url = "https://4.204.251.146:3000/tags/nearby/?latitude=" + latitude + "&longitude=" + longitute;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE GET TAGS: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response " + response);
                }

                // Process the response in the background thread
                final String responseData = response.body().string();
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> tagsList = gson.fromJson(responseData, listType);

                // Switch to the main thread to update UI
                activity.runOnUiThread(() -> {
                    Log.d(TAG, "GET POST SUCCEED");
                    callback.onSuccess(tagsList);
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getSearchedPosts(String searchText, final Activity activity, final PostCallback callback) {

        String url = "https://4.204.251.146:3000/posts/search/?keyword=" + searchText;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE GET SEARCHED POSTS: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response " + response);
                }

                assert response.body() != null;
                final String responseData = response.body().string();
                    Log.d(TAG, "GET SEARCHED POSTS SUCCEED");
                    Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                    List<Post> posts = gson.fromJson(responseData, postListType);

                // Switch to the main thread to update UI
                activity.runOnUiThread(() -> {
                    Log.d(TAG, "GET POST SUCCEED");
                    callback.onSuccess(posts);
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getTagsSelectedPosts(String latitude, String longitute, ArrayList<String> tagsList, final Activity activity, final PostCallback callback) {

        String url = "https://4.204.251.146:3000/posts/has-tags/?latitude=" + latitude + "&longitude=" + longitute + "&tags=";
        StringBuilder urlBuilder = new StringBuilder(url);

        for(int i = 0; i < tagsList.size(); i++ ) {

            urlBuilder.append(tagsList.get(i));

            if( i != tagsList.size() - 1 ) {

                urlBuilder.append(",");

            }

        }

        String newUrl = urlBuilder.toString();

        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(newUrl)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE GET POSTS FILTERED BY TAGS: " + e);

                activity.runOnUiThread(() -> callback.onFailure(e));

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response " + response);
                }

                // Process the response in the background thread
                final String responseData = response.body().string();
                Log.d(TAG, "GET SEARCHED POSTS SUCCEED");
                Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                List<Post> posts = gson.fromJson(responseData, postListType);

                // Switch to the main thread to update UI
                activity.runOnUiThread(() -> {
                    Log.d(TAG, "GET POST SUCCEED");
                    callback.onSuccess(posts);
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getUserAllPosts(String userId, final Activity activity, final PostCallback callback) {

        String url = "https://4.204.251.146:3000/posts/from-user/?userId=" + userId;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "FAILURE GET POSTS BY USER: " + e);

                activity.runOnUiThread(() ->callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "GET POSTS BY USER SUCCEED");
                    List<Post> posts;

                    String responseData;
                    assert response.body() != null;
                    try {
                        responseData = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                    posts = gson.fromJson(responseData, postListType);

                    activity.runOnUiThread(() -> callback.onSuccess(posts));

                }
            }
        });
    }

    //ChatGPT usage: Yes
    public interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(Exception e);
    }

    //ChatGPT usage: Yes
    public interface JsonCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

}
