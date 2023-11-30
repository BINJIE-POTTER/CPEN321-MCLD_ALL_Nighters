package com.example.cpen321mappost;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
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

public class CommentManager {
    final static String TAG = "CommentManager Activity";

    //ChatGPT usage: No
    public void displayAllComments(Context context, String pid, RecyclerView recyclerViewComments) {
        getAllCommentsData(pid, new Activity(), new CommentCallback() {
            @Override
            public void onCommentsReceived(List<Comment> comments) {

                CommentAdapter commentAdapter = new CommentAdapter(context, comments);
                recyclerViewComments.setAdapter(commentAdapter);

            }
            @Override
            public void onPostCommentSuccess() {

                Log.d(TAG, "should not reach here.");

            }
            @Override
            public void onFailure(Exception e) {

                Log.e(TAG, "Failed to fetch comments", e);

            }
        });
    }

    //ChatGPT usage: Partial
    public void getAllCommentsData(String pid, final Activity activity, final CommentCallback callback) {

        String url = "https://4.204.251.146:3000/comments/?pid=" + pid;
        OkHttpClient httpClient = HttpClient.getInstance();

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "Failed to get post data", e);

                activity.runOnUiThread(() ->callback.onFailure(e));

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "GET COMMENT SUCCEED");

                    List<Comment> comments;

                    assert response.body() != null;
                    String responseData = null;
                    try {
                        responseData = response.body().string();
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }

                    Gson gson = new Gson();
                    Type commentListType = new TypeToken<ArrayList<Comment>>(){}.getType();
                    comments = gson.fromJson(responseData, commentListType);

                    List<Comment> finalComments = comments;
                    activity.runOnUiThread(() -> callback.onCommentsReceived(finalComments));

                }
            }
        });
    }

    //ChatGPT usage: Partial
    public void postCommentData(Comment comment, final Activity activity, final CommentCallback callback){

        String url = "https://4.204.251.146:3000/comments";
        OkHttpClient httpClient = HttpClient.getInstance();

        Gson gson = new Gson();
        String jsonCommentData = gson.toJson(comment);

        Log.d(TAG, jsonCommentData);

        RequestBody body = RequestBody.create(jsonCommentData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.e(TAG, "Failed to post comment", e);

                activity.runOnUiThread(() ->callback.onFailure(e));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {

                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());

                    activity.runOnUiThread(() -> callback.onFailure(new IOException("Unexpected response " + response)));

                } else {

                    Log.d(TAG, "POST COMMENT SUCCEED");

                    activity.runOnUiThread(callback::onPostCommentSuccess);

                }
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

    public interface CommentCallback {
        public void onCommentsReceived(List<Comment> comments);
        public void onPostCommentSuccess();
        public void onFailure(Exception e);
    }

}
