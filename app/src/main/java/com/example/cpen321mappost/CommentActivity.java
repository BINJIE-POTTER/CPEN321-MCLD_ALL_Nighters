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

    public interface JsonCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);

        Intent receivedIntent = getIntent();
        String pid = receivedIntent.getStringExtra("pid");

        buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editTextComment.getText().toString();
                if (!newComment.isEmpty()) {
                    editTextComment.setText(""); // Clear the EditText

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("pid", pid);
                        postData.put("uid", User.getInstance().getUserId());
                        postData.put("time", getCurrentDateUsingCalendar());
                        postData.put("content", newComment);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    postCommentData(postData, CommentActivity.this, new JsonCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            // TODO: Handle success of posting a comment
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // TODO: Handle failure of posting a comment
                        }
                    });
                }
            }
        });

        displayAllComments(pid);
    }

    public void displayAllComments(String pid) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        getAllCommentsData(postData, this, new JsonCallback<Comment[]>() {
            @Override
            public void onSuccess(Comment[] comments) {
                CommentAdapter commentAdapter = new CommentAdapter(comments);
                recyclerViewComments.setAdapter(commentAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Handle failure of fetching comments
            }
        });
    }

    public void getAllCommentsData(JSONObject postData, final Activity activity, final JsonCallback<Comment[]> callback) {
        String url = "http://4.204.251.146:8081/comments";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        Iterator<String> keys = postData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = postData.optString(key);
            if (value != null) {
                urlBuilder.addQueryParameter(key, value);
            }
        }

        String fullUrl = urlBuilder.build().toString();
        Request request = new Request.Builder().url(fullUrl).build();

        OkHttpClient httpClient = HttpClient.getInstance();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        Comment[] comments = gson.fromJson(responseData, Comment[].class);
                        activity.runOnUiThread(() -> callback.onSuccess(comments));
                    } finally {
                        response.close();
                    }
                } else {
                    IOException exception = new IOException("Unexpected code " + response);
                    activity.runOnUiThread(() -> callback.onFailure(exception));
                }
            }
        });
    }

    public void postCommentData(JSONObject postData, final Activity activity, final JsonCallback<Void> callback) {
        String url = "http://4.204.251.146:8081/comments";

        OkHttpClient httpClient = HttpClient.getInstance();
        String jsonStrData = postData.toString();
        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder().url(url).post(body).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    activity.runOnUiThread(() -> callback.onSuccess(null));
                } else {
                    IOException exception = new IOException("Unexpected code " + response);
                    activity.runOnUiThread(() -> callback.onFailure(exception));
                }
            }
        });
    }


    public String getCurrentDateUsingCalendar () {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }
}
