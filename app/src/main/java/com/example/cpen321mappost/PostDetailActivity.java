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
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

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
    private static final PostManager postManager = new PostManager();
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

                uid = post.getUserId();

                if (Objects.equals(uid, User.getInstance().getUserId())){

                    buttonDelete.setVisibility(View.VISIBLE);

                } else {

                    buttonDelete.setVisibility(View.GONE);

                }
                profileManager.getAuthor(post.getUserId(), new ProfileManager.AuthorCallback() {
                    @Override
                    public void onAuthorRetrieved(String authorName) {

                        buttonAuthor.setText(authorName);

                    }

                    @Override
                    public void onError(Exception e) {

                        Log.d(TAG, String.valueOf(e));

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to fetch this post!", Toast.LENGTH_LONG).show();

            }
        });

        buttonDelete.setOnClickListener(v -> postManager.deletePostData(pid, PostDetailActivity.this, new PostManager.JsonCallback<Void>() {
            @Override
            public void onSuccess(Void result) {

                Intent PostPreviewListIntent = new Intent(PostDetailActivity.this, PostPreviewListActivity.class);
                startActivity(PostPreviewListIntent);

            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to delete this post!", Toast.LENGTH_LONG).show();

            }
        }));

        buttonAuthor.setOnClickListener(new View.OnClickListener() {
            //This button will navigate to PostPreviewListActivity, and show the author with a sub button
            //and display all his posts accroding to uid
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, PostPreviewListActivity.class);
                intent.putExtra("mode", "authorInfo");
                intent.putExtra("userId", uid);
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
                        Toast.makeText(PostDetailActivity.this, "liked this post!", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                        Toast.makeText(PostDetailActivity.this, "Failed to like this post :(", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                intent.putExtra("pid", pid);
                startActivity(intent);


            }
        });
    }
}


