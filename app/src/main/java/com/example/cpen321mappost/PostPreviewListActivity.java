package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PostPreviewListActivity extends AppCompatActivity {

    final static String TAG = "PostPreviewListActivity Activity";

    @SuppressLint("SetTextI18n")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview_list);

        PostManager postManager = new PostManager();
        ProfileManager profileManager = new ProfileManager();

        RecyclerView recyclerView = findViewById(R.id.postsRecyclerView);
        TextView modeTitle = findViewById(R.id.post_preview_list_mode_name_id);

        List<Post> posts = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        String userId = intent.getStringExtra("userId");

        switch (mode) {
            case "profile":

                modeTitle.setText(User.getInstance().getUserName() + "'s Posts");
                break;

            case "":

            default:
        }

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "IN PostPreviewListActivity, " + posts.toString());

        postManager.getUserAllPosts(userId, this, new PostManager.PostCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Post> fetchedPosts) {

                Log.d(TAG, "Succeed on getUserAllPosts(), " + fetchedPosts.toString());

                //Post post = gson.fromJson(postJson, Post.class);
                posts.addAll(fetchedPosts);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Exception e) {

            }
        });


        //recyclerView.setAdapter(adapter);


    }

}
