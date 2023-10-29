package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostManager {

    // Assuming HttpClient is your custom class that provides an OkHttpClient instance
    private static final String TAG = "UserDataFetcher"; // Tag for your log messages

    //Sara's insert

    public void getSinglePostData(String pid, final Activity activity, final JsonCallback<Post> callback){

        JSONObject postData = new JSONObject();
        try {
            postData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String url = "http://4.204.251.146:8081/posts/single" ;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        // Convert the JSONObject to query parameters
        Iterator<String> keys = postData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = postData.optString(key);
            if (value != null) {
                urlBuilder.addQueryParameter(key, value);
            }
        }
        String fullUrl = urlBuilder.build().toString();

        // Build the GET request
        Request request = new Request.Builder()
                .url(fullUrl)
                .build();

        OkHttpClient httpClient = HttpClient.getInstance();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Failed to post data", e);
                        callback.onFailure(e); // Notify callback about the failure
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                if (response.code() == 200) {
                                    // The operation was successful.
                                    Log.d(TAG, "Data posted successfully!");
                                    String responseData = response.body().string();
                                    Gson gson = new Gson();
                                    Post post = gson.fromJson(responseData, Post.class);
                                    callback.onSuccess(post); // already on UI thread, safe to call directly


                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
                                    Log.e(TAG, "Error posting data", exception);
                                    callback.onFailure(exception); // Notify callback about the failure
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            IOException exception = new IOException("Unexpected code " + response);
                            Log.e(TAG, "Error posting data", exception);
                            callback.onFailure(exception); // Notify callback about the failure
                        }
                    }
                });
            }
        });
    }

    public void deletePostData(String pid, final Activity activity, final JsonCallback<Void> callback){

        JSONObject postData = new JSONObject();
        try {
            postData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String url = "http://4.204.251.146:8081/posts" ;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        // Convert the JSONObject to query parameters
        Iterator<String> keys = postData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = postData.optString(key);
            if (value != null) {
                urlBuilder.addQueryParameter(key, value);
            }
        }
        String fullUrl = urlBuilder.build().toString();

        // Build the GET request
        Request request = new Request.Builder()
                .url(fullUrl)
                .delete()
                .build();

        OkHttpClient httpClient = HttpClient.getInstance();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {
                    Log.e(TAG, "Failed to delete post", e);
                    callback.onFailure(e); // Notify callback about the failure
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                Log.d(TAG, "Post deleted successfully!");
                                callback.onSuccess(null); // Notify callback about the successful deletion
                            } else {
                                IOException exception = new IOException("Unexpected response when deleting post: " + response);
                                Log.e(TAG, "Error deleting post", exception);
                                callback.onFailure(exception); // Notify callback about the failure
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            response.close();
                        }
                    } else {
                        IOException exception = new IOException("Unexpected code " + response);
                        Log.e(TAG, "Error deleting post", exception);
                        callback.onFailure(exception); // Notify callback about the failure
                    }
                });
            }
        });
    }


    public void likePostData(String pid, final Activity activity, final JsonCallback<Void> callback) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String url = "http://4.204.251.146:8081/posts/like";
        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the JSONObject to a string representation
        String jsonStrData = postData.toString();

        Log.d(TAG, "This is the liked post data: " + jsonStrData);

        // Create a request body with the string representation of the JSONObject
        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));

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
                        Log.e(TAG, "Failed to post data", e);
                        callback.onFailure(e); // Notify callback about the failure
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                if (response.code() == 200) {
                                    // The operation was successful.
                                    Log.d(TAG, "Data posted successfully!");
                                    callback.onSuccess(null); // Notify callback about the success

                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
                                    Log.e(TAG, "Error posting data", exception);
                                    callback.onFailure(exception); // Notify callback about the failure
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            IOException exception = new IOException("Unexpected code " + response);
                            Log.e(TAG, "Error posting data", exception);
                            callback.onFailure(exception); // Notify callback about the failure
                        }
                    }
                });
            }
        });
    }






    //Sara;s insert end



    public void getUserAllPosts(String userId, final Activity activity, final PostCallback callback) {
        String url = "http://4.204.251.146:8081/posts/from-user/?userId=" + userId; // Replace with your actual posts endpoint
        OkHttpClient httpClient = HttpClient.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to get user posts", e);
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
                            } else if (response.code() == 200) {
                                Log.d(TAG, "Succeed on get");

                                List<Post> posts = null;

                                try {

                                    assert response.body() != null;
                                    String responseData = response.body().string();
                                    Gson gson = new Gson();
                                    Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                                    posts = gson.fromJson(responseData, postListType);

                                } catch (JsonSyntaxException e) {

                                    Log.e(TAG, "JSON Parsing error", e);
                                    throw new IOException("Error parsing JSON", e); // Convert to IOException to handle later

                                }

                                if (posts != null) {
                                    callback.onSuccess(posts); // use the correct method with List<Post>
                                } else {
                                    throw new IOException("Error in response data"); // Handle the scenario of unsuccessful parsing
                                }

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

    // Define the callback interface
    public interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(Exception e);
    }
    public interface JsonCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }



}
