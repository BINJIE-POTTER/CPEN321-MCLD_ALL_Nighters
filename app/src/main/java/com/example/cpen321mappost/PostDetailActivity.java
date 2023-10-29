package com.example.cpen321mappost;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    private static final ProfileManager profileManager = new ProfileManager();
    private String pid;
    private String uid;

//    public interface JsonCallback<T> {
//        void onSuccess(T result);
//        void onFailure(Exception e);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        PostManager postManager = new PostManager();

        // Initialize views
        TextView textViewPostDetail = findViewById(R.id.textViewPostDetail);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewMainContent = findViewById(R.id.textViewMainContent);

        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonAuthor = findViewById(R.id.buttonAuthor);
        Button buttonLike = findViewById(R.id.buttonLike);
        Button buttonComment = findViewById(R.id.buttonComment);

        //Pass the variable as Pid:
        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        postManager.getSinglePostData(pid, this, new PostManager.JsonCallback<Post>() {
            @Override
            public void onSuccess(Post post) {
                textViewTitle.setText(post.getContent().getTitle());
                textViewMainContent.setText(post.getContent().getBody());
                profileManager.getAuthor(post.getUserId(), new ProfileManager.AuthorCallback() {
                    @Override
                    public void onAuthorRetrieved(String authorName) {
                        buttonAuthor.setText(authorName);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });//????

            }
            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postManager.deletePostData(pid, PostDetailActivity.this, new PostManager.JsonCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success in deleting a post here
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                    }
                });
            }
        });

        buttonAuthor.setOnClickListener(new View.OnClickListener() {
            //This button will navigate to PostPreviewListActivity, and show the author with a sub button
            //and display all his posts accroding to uid
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PostDetailActivity.this,PostPreviewListActivity.class);
                intent.putExtra("mode","authorInfo" );
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            //Send to backend and update the like count
            @Override
            public void onClick(View v) {
                postManager.likePostData(pid, PostDetailActivity.this, new PostManager.JsonCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success in deleting a post here
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                    }
                });
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PostDetailActivity.this,CommentActivity.class);
                intent.putExtra("pid",pid);
                startActivity(intent);


            }
        });
    }
//
//    public void getSinglePostData(JSONObject postData, final Activity activity, final JsonCallback<Post> callback){
//
//        String url = "http://4.204.251.146:8081/posts/single" ;
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//
//        // Convert the JSONObject to query parameters
//        Iterator<String> keys = postData.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String value = postData.optString(key);
//            if (value != null) {
//                urlBuilder.addQueryParameter(key, value);
//            }
//        }
//        String fullUrl = urlBuilder.build().toString();
//
//        // Build the GET request
//        Request request = new Request.Builder()
//                .url(fullUrl)
//                .build();
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "Failed to post data", e);
//                        callback.onFailure(e); // Notify callback about the failure
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (response.isSuccessful()) {
//                            try {
//                                if (response.code() == 200) {
//                                    // The operation was successful.
//                                    Log.d(TAG, "Data posted successfully!");
//                                    String responseData = response.body().string();
//                                    Gson gson = new Gson();
//                                    Post post = gson.fromJson(responseData, Post.class);
//                                    callback.onSuccess(post); // already on UI thread, safe to call directly
//
//
//                                } else {
//                                    // Handle other response codes (like 4xx or 5xx errors)
//                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
//                                    Log.e(TAG, "Error posting data", exception);
//                                    callback.onFailure(exception); // Notify callback about the failure
//                                }
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            } finally {
//                                response.close(); // Important to avoid leaking resources
//                            }
//                        } else {
//                            IOException exception = new IOException("Unexpected code " + response);
//                            Log.e(TAG, "Error posting data", exception);
//                            callback.onFailure(exception); // Notify callback about the failure
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    public void deletePostData(JSONObject postData, final Activity activity, final JsonCallback<Void> callback){
//        String url = "http://4.204.251.146:8081/posts" ;
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//
//        // Convert the JSONObject to query parameters
//        Iterator<String> keys = postData.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String value = postData.optString(key);
//            if (value != null) {
//                urlBuilder.addQueryParameter(key, value);
//            }
//        }
//        String fullUrl = urlBuilder.build().toString();
//
//        // Build the GET request
//        Request request = new Request.Builder()
//                .url(fullUrl)
//                .delete()
//                .build();
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(() -> {
//                    Log.e(TAG, "Failed to delete post", e);
//                    callback.onFailure(e); // Notify callback about the failure
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) {
//                activity.runOnUiThread(() -> {
//                    if (response.isSuccessful()) {
//                        try {
//                            if (response.code() == 200) {
//                                Log.d(TAG, "Post deleted successfully!");
//                                callback.onSuccess(null); // Notify callback about the successful deletion
//                            } else {
//                                IOException exception = new IOException("Unexpected response when deleting post: " + response);
//                                Log.e(TAG, "Error deleting post", exception);
//                                callback.onFailure(exception); // Notify callback about the failure
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        } finally {
//                            response.close();
//                        }
//                    } else {
//                        IOException exception = new IOException("Unexpected code " + response);
//                        Log.e(TAG, "Error deleting post", exception);
//                        callback.onFailure(exception); // Notify callback about the failure
//                    }
//                });
//            }
//        });
//    }
//
//
//    public void likePostData(JSONObject postData, final Activity activity, final JsonCallback<Void> callback) {
//        String url = "http://4.204.251.146:8081/posts/like";
//
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//
//        // Convert the JSONObject to a string representation
//        String jsonStrData = postData.toString();
//
//        Log.d(TAG, "This is the liked post data: " + jsonStrData);
//
//        // Create a request body with the string representation of the JSONObject
//        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));
//
//        // Build the request
//        Request request = new Request.Builder()
//                .url(url)
//                .put(body)
//                .build();
//
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "Failed to post data", e);
//                        callback.onFailure(e); // Notify callback about the failure
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (response.isSuccessful()) {
//                            try {
//                                if (response.code() == 200) {
//                                    // The operation was successful.
//                                    Log.d(TAG, "Data posted successfully!");
//                                    callback.onSuccess(null); // Notify callback about the success
//
//                                } else {
//                                    // Handle other response codes (like 4xx or 5xx errors)
//                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
//                                    Log.e(TAG, "Error posting data", exception);
//                                    callback.onFailure(exception); // Notify callback about the failure
//                                }
//                            } finally {
//                                response.close(); // Important to avoid leaking resources
//                            }
//                        } else {
//                            IOException exception = new IOException("Unexpected code " + response);
//                            Log.e(TAG, "Error posting data", exception);
//                            callback.onFailure(exception); // Notify callback about the failure
//                        }
//                    }
//                });
//            }
//        });
//    }




}

//
//
//    public void getSinglePostData(JSONObject postData, final Activity activity, final PostDetailActivity.JsonPostCallback callback){
//
//        String url = "http://4.204.251.146:8081/posts/single" ;
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//
//        // Convert the JSONObject to query parameters
//        Iterator<String> keys = postData.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String value = postData.optString(key);
//            if (value != null) {
//                urlBuilder.addQueryParameter(key, value);
//            }
//        }
//        String fullUrl = urlBuilder.build().toString();
//
//        // Build the GET request
//        Request request = new Request.Builder()
//                .url(fullUrl)
//                .build();
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "Failed to post data", e);
//                        callback.onFailure(e); // Notify callback about the failure
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (response.isSuccessful()) {
//                            try {
//                                if (response.code() == 200) {
//                                    // The operation was successful.
//                                    Log.d(TAG, "Data posted successfully!");
//                                    String responseData = response.body().string();
//                                    Gson gson = new Gson();
//                                    Post post = gson.fromJson(responseData, Post.class);
//                                    callback.onSuccess(post); // already on UI thread, safe to call directly
//
//
//                                } else {
//                                    // Handle other response codes (like 4xx or 5xx errors)
//                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
//                                    Log.e(TAG, "Error posting data", exception);
//                                    callback.onFailure(exception); // Notify callback about the failure
//                                }
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            } finally {
//                                response.close(); // Important to avoid leaking resources
//                            }
//                        } else {
//                            IOException exception = new IOException("Unexpected code " + response);
//                            Log.e(TAG, "Error posting data", exception);
//                            callback.onFailure(exception); // Notify callback about the failure
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//
//    public void deletePostData(JSONObject postData, final Activity activity, final PostDetailActivity.JsonPostCallback callback){
//
//        String url = "http://4.204.251.146:8081/posts" ;
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//
//        // Convert the JSONObject to query parameters
//        Iterator<String> keys = postData.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String value = postData.optString(key);
//            if (value != null) {
//                urlBuilder.addQueryParameter(key, value);
//            }
//        }
//        String fullUrl = urlBuilder.build().toString();
//
//        // Build the GET request
//        Request request = new Request.Builder()
//                .url(fullUrl)
//                .delete()
//                .build();
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(() -> {
//                    Log.e(TAG, "Failed to delete post", e);
//                    callback.onFailure(e); // Notify callback about the failure
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) {
//                activity.runOnUiThread(() -> {
//                    if (response.isSuccessful()) {
//                        try {
//                            if (response.code() == 200) {
//                                Log.d(TAG, "Post deleted successfully!");
//                                callback.onDeleteSuccess(); // Notify callback about the successful deletion
//                            } else {
//                                IOException exception = new IOException("Unexpected response when deleting post: " + response);
//                                Log.e(TAG, "Error deleting post", exception);
//                                callback.onFailure(exception); // Notify callback about the failure
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        } finally {
//                            response.close();
//                        }
//                    } else {
//                        IOException exception = new IOException("Unexpected code " + response);
//                        Log.e(TAG, "Error deleting post", exception);
//                        callback.onFailure(exception); // Notify callback about the failure
//                    }
//                });
//            }
//        });
//    }



