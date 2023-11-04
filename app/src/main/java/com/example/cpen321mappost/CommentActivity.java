package com.example.cpen321mappost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private String pid;

    //ChatGPT usage: Yes
    public interface CommentCallback {
        void onCommentsReceived(List<Comment> comments);
        void onPostCommentSuccess();
        void onFailure(Exception e);
    }

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager here
        editTextComment = findViewById(R.id.editTextComment);
        Button buttonSubmitComment = findViewById(R.id.buttonSubmitComment);

        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        buttonSubmitComment.setOnClickListener(v -> {

            String newComment = editTextComment.getText().toString();

            if (!newComment.isEmpty()) {

                editTextComment.setText("");

                Comment comment = new Comment(pid, User.getInstance().getUserId(), getCurrentDateUsingCalendar(), newComment);

                postCommentData(comment, CommentActivity.this, new CommentCallback() {
                    @Override
                    public void onCommentsReceived(List<Comment> comments) {

                        displayAllComments(pid);

                    }
                    @Override
                    public void onPostCommentSuccess() {

                        displayAllComments(pid);

                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure of posting a comment
                        Log.e(TAG, "Failed to post comment", e);
                    }
                });

            }
        });

        displayAllComments(pid);

    }

    //ChatGPT usage: No
    public void displayAllComments(String pid) {
        getAllCommentsData(pid, this, new CommentCallback() {
            @Override
            public void onCommentsReceived(List<Comment> comments) {
                CommentAdapter commentAdapter = new CommentAdapter(comments);
                recyclerViewComments.setAdapter(commentAdapter);
            }

            @Override
            public void onPostCommentSuccess() {
                // Not used here
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to fetch comments", e);
            }
        });
    }

    //ChatGPT usage: Partial
    public void getAllCommentsData(String pid, final Activity activity, final CommentCallback callback) {

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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(() -> {
                    try {
                        if (!response.isSuccessful()) {
                            Log.d(TAG, "Unexpected server response, the code is: " + response.code());
                            callback.onFailure(new IOException("Unexpected response " + response));
                        } else if (response.code() == 200) {
                            Log.d(TAG, "Succeed on get user all posts");

                            List<Comment> comments = null;

                            try {

                                assert response.body() != null;
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                Type commentListType = new TypeToken<ArrayList<Comment>>(){}.getType();
                                comments = gson.fromJson(responseData, commentListType);

                            } catch (JsonSyntaxException e) {

                                Log.e(TAG, "JSON Parsing error", e);
                                throw new IOException("Error parsing JSON", e); // Convert to IOException to handle later

                            }

                            if (comments != null) {
                                callback.onCommentsReceived(comments); // use the correct method with List<Post>
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
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public void postCommentData(Comment comment, final Activity activity, final CommentCallback callback){

        String url = "http://4.204.251.146:8081/comments";
        OkHttpClient httpClient = HttpClient.getInstance();

        Gson gson = new Gson();
        String jsonCommentData = gson.toJson(comment);

        Log.d(TAG, jsonCommentData);

        RequestBody body = RequestBody.create(jsonCommentData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {
                    Log.e(TAG, "Failed to post comment", e);
                    callback.onFailure(e); // Notify callback about the failure
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                // The operation was successful.
                                Log.d(TAG, "Comment posted successfully!");
                                callback.onPostCommentSuccess(); // Notify callback about the success

                            } else {
                                // Handle other response codes
                                IOException exception = new IOException("Unexpected response when posting comment: " + response);
                                Log.e(TAG, "Error posting comment", exception);
                                callback.onFailure(exception); // Notify callback about the failure
                            }
                        } finally {
                            response.close(); // Important to avoid resource leaks
                        }
                    } else {
                        IOException exception = new IOException("Unexpected response " + response);
                        Log.e(TAG, "Error posting comment", exception);
                        callback.onFailure(exception); // Notify callback about the failure
                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
    public String getCurrentDateUsingCalendar () {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }
}
