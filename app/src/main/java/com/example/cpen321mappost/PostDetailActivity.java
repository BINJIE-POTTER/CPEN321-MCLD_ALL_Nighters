package com.example.cpen321mappost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";


    public interface JsonPostCallback {
        void onSuccess(Post post);
        void onFailure(Exception e);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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

        String pid = receivedIntent.getStringExtra("pid");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getSinglePostData(jsonData, PostDetailActivity.this,new PostDetailActivity.JsonPostCallback() {
            @Override
            public void onSuccess(Post post) {
                textViewTitle.setText(post.getContent().getTitle());
                textViewMainContent.setText(post.getContent().getBody());
                buttonAuthor.setText(post.getUserId());


            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });




        // TODO: Set the text for textViewTitle and textViewMainContent based on your data

        // Setting up click listeners for buttons
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your delete handling code here
            }
        });

        buttonAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your author button handling code here
            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your like button handling code here
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your comment button handling code here
            }
        });
    }


    public void getSinglePostData(JSONObject postData, final Activity activity, final PostDetailActivity.JsonPostCallback callback){

        String url = "http://4.204.251.146:8081/posts/" ;
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




}
