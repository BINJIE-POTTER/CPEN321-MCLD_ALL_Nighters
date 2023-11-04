package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
    private static final String TAG = "UserDataFetcher";
    final Gson gson = new Gson();

    //ChatGPT usage: Partial
    public void getSinglePostData(String pid, final Activity activity, final JsonCallback<Post> callback){

        String url = "http://4.204.251.146:8081/posts/single/?pid=" + pid;
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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(() -> {

                    if (!response.isSuccessful()) {

                        Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                        callback.onFailure(new IOException("Unexpected response " + response));

                    } else {

                        Log.d(TAG, "GET POST SUCCEED!");

                        assert response.body() != null;
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }

                        Post post = gson.fromJson(responseData, Post.class);

                        callback.onSuccess(post);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Yes
    public void deletePostData(String pid, final Activity activity, final JsonCallback<Void> callback){

        String url = "http://4.204.251.146:8081/posts/?pid=" + pid;
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
    public void likePostData(String pid, final Activity activity, final JsonCallback<Void> callback) {

        String url = "http://4.204.251.146:8081/posts/like";
        OkHttpClient httpClient = HttpClient.getInstance();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pid", pid);
        String jsonBody = jsonObject.toString();

        Log.d(TAG, "This is the liked post data: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE LIKE POST: " + e);

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

                        Log.d(TAG, "LIKE POST SUCCEED");

                        callback.onSuccess(null);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getTagsData(String latitude, String longitute, final Activity activity, final JsonCallback< ArrayList<String>> callback){

        String url = "http://4.204.251.146:8081/tags/nearby/?latitude=" + latitude + "&longitude=" + longitute;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET TAGS: " + e);

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

                        Log.d(TAG, "GET TAGS SUCCEED");

                        assert response.body() != null;
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                        ArrayList<String> tagsList = gson.fromJson(responseData, listType);

                        callback.onSuccess(tagsList);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getSearchedPosts(String searchText, final Activity activity, final PostCallback callback) {

        String url = "http://4.204.251.146:8081/posts/search/?keyword=" + searchText;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET SEARCHED POSTS: " + e);

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

                        Log.d(TAG, "GET SEARCHED POSTS SUCCEED");

                        List<Post> posts;

                        assert response.body() != null;
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                        posts = gson.fromJson(responseData, postListType);

                        callback.onSuccess(posts);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getTagsSelectedPosts(String latitude, String longitute, ArrayList<String> tagsList, final Activity activity, final PostCallback callback) {

        String url = "http://4.204.251.146:8081/posts/has-tags/?latitude=" + latitude + "&longitude=" + longitute + "&tags=";
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
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET POSTS FILTERED BY TAGS: " + e);

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

                        Log.d(TAG, "GET POSTS FILTERED BY TAGS SUCCEED");

                        List<Post> posts;

                        assert response.body() != null;
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                        posts = gson.fromJson(responseData, postListType);

                        callback.onSuccess(posts);

                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void getUserAllPosts(String userId, final Activity activity, final PostCallback callback) {

        String url = "http://4.204.251.146:8081/posts/from-user/?userId=" + userId;
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET POSTS BY USER: " + e);

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

                        Log.d(TAG, "GET POSTS BY USER SUCCEED");

                        List<Post> posts;

                        assert response.body() != null;
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                        posts = gson.fromJson(responseData, postListType);

                        callback.onSuccess(posts);

                    }
                });
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
