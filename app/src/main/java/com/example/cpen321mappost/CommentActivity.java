package com.example.cpen321mappost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

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
    public interface JsonCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }


    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private Button buttonSubmitComment;
    private String pid;
    final static String TAG = "CommentActivity";

    // TODO: You might want to use a custom Adapter for the comments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        //display all existing comments

        editTextComment = findViewById(R.id.editTextComment);
        //Here to input your comment
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);
        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editTextComment.getText().toString();
                if (!newComment.isEmpty()) {
                    // TODO: Add the new comment to your list and update the RecyclerView
                    editTextComment.setText(""); // Clear the EditText
                }

                // to submit a comment give them pid, uid, time, content

                JSONObject postData = new JSONObject();
                //TODO: for testing only put real useridafterwards
                try {
                    postData.put("pid", pid);

                    postData.put("uid", User.getInstance().getUserId());

                    postData.put("time", getCurrentDateUsingCalendar());

                    postData.put("content", newComment);


                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

                postCommentData(postData, CommentActivity.this, new JsonPostCallback() {
                    @Override
                    public void onSuccess(JSONObject postedData) {
                        // Handle success here


                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        });


            }


});

        displayAllComments();


    }
    public void displayAllComments()
    {
        //get all comments:  give them pid, they will give me comment object
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("pid", pid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        getAllCommentsData(jsonData, this, new CommentActivity.JsonCallback<Comment[] >() {
            @Override
            public void onSuccess(Comment[] comments) {


            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });

    }

    public void getAllCommentsData(JSONObject postData, final Activity activity, final CommentActivity.JsonCallback<Post> callback){

        String url = "http://4.204.251.146:8081/comments" ;
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
                    }
                });
            }
        });
    }

    public void postCommentData(JSONObject postData, final Activity activity, final CommentActivity.JsonCallback callback){

        String url = "http://4.204.251.146:8081/comments" ;

        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the JSONObject to a string representation
        String jsonStrData = postData.toString();

        Log.d(TAG, "This is the posted data: " + jsonStrData);

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




public String getCurrentDateUsingCalendar () {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
        }


}
