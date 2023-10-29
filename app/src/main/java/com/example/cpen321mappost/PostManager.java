package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostManager {

    // Assuming HttpClient is your custom class that provides an OkHttpClient instance
    private static final String TAG = "UserDataFetcher"; // Tag for your log messages

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


}
