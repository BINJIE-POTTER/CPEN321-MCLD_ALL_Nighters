package com.example.cpen321mappost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private Button buttonSubmitComment;
    private String pid;

    public interface JsonCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager here
        editTextComment = findViewById(R.id.editTextComment);
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);

        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        buttonSubmitComment.setOnClickListener(v -> {
            String newComment = editTextComment.getText().toString();
            if (!newComment.isEmpty()) {
                editTextComment.setText(""); // Clear the EditText

                JSONObject postData = new JSONObject();
                try {
                    postData.put("pid", pid);
                    postData.put("userId", User.getInstance().getUserId());
                    postData.put("time", getCurrentDateUsingCalendar());
                    postData.put("content", newComment);

                    postCommentData(postData, CommentActivity.this, new JsonCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            // Refresh comments to show the newly added one
                            displayAllComments(pid);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Handle failure of posting a comment
                            Log.e(TAG, "Failed to post comment", e);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception", e);
                }
            }
        });

        displayAllComments(pid);
    }

    public void displayAllComments(String pid) {
        getAllCommentsData(pid, this, new JsonCallback<Comment[]>() {
            @Override
            public void onSuccess(Comment[] comments) {
                CommentAdapter commentAdapter = new CommentAdapter(comments);
                recyclerViewComments.setAdapter(commentAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to fetch comments", e);
            }
        });
    }

    public void getAllCommentsData(String pid, final Activity activity, final JsonCallback<Comment[]> callback) {

        String url = "http://4.204.251.146:8081/comments/?pid=" + pid;

        OkHttpClient httpClient = HttpClient.getInstance();

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {
                    Log.e(TAG, "Failed to get post data", e);
                    callback.onFailure(e);
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                // The operation was successful.
                                Log.d(TAG, "Data posted successfully!");
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                Comment[] comments = gson.fromJson(responseData, Comment[].class);
                                callback.onSuccess(comments); // already on UI thread, safe to call directly

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
                });
            }
        });
    }


    public void postCommentData(JSONObject postData, final Activity activity, final CommentActivity.JsonCallback callback){

        String url = "http://4.204.251.146:8081/comments";

        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the JSONObject to a string representation
        String jsonStrData = postData.toString();

        Log.d(TAG, "This is the comment data: " + jsonStrData);

        // Create a request body with the string representation of the JSONObject
        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));

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

//
//    public void postCommentData(JSONObject postData, final Activity activity, final JsonCallback<Void> callback) {
//        String url = "http://4.204.251.146:8081/comments";
//
//        OkHttpClient httpClient = HttpClient.getInstance();
//        String jsonStrData = postData.toString();
//        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));
//
//        Request request = new Request.Builder().url(url).post(body).build();
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                activity.runOnUiThread(() -> callback.onFailure(e));
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    activity.runOnUiThread(() -> callback.onSuccess(null));
//                } else {
//                    IOException exception = new IOException("Unexpected code " + response);
//                    activity.runOnUiThread(() -> callback.onFailure(exception));
//                }
//            }
//        });
//    }


    public String getCurrentDateUsingCalendar () {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }
}
