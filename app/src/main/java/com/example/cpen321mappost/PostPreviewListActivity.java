package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PostPreviewListActivity extends AppCompatActivity {
    private final static String TAG = "PostPreviewListActivity Activity";
    private static final ClusterManager clusterManager = ClusterManager.getInstance();
    private String userId;


    //ChatGPT usage: Partial
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview_list);

        PostManager postManager = new PostManager();
        ProfileManager profileManager = new ProfileManager();

        double latitude;
        double longitude;

        RecyclerView recyclerView = findViewById(R.id.postsRecyclerView);
        TextView modeTitle = findViewById(R.id.post_preview_list_mode_name_id);
        Button followButton = findViewById(R.id.post_preview_list_follow_button_id);
        followButton.setVisibility(View.GONE);

        List<Post> posts = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        switch (mode) {

            case "profile":

                followButton.setVisibility(View.GONE);

                userId = intent.getStringExtra("userId");
                modeTitle.setText(User.getInstance().getUserName() + "'s Posts");

                postManager.getUserAllPosts(userId, this, new PostManager.PostCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Post> fetchedPosts) {

                        posts.clear();
                        posts.addAll(fetchedPosts);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(PostPreviewListActivity.this, "Failed to get user's all posts ", Toast.LENGTH_SHORT).show();
                        final Toast toast = Toast.makeText(PostPreviewListActivity.this, "Failed to get user's all posts in PostPreviewList", Toast.LENGTH_LONG);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.show();
                            }
                        }, 3000); // 3000ms delay to show the toast again after the initial showing

                    }
                });

                break;

            case "reviewPosts":

                modeTitle.setText("All near by posts");

                latitude = intent.getDoubleExtra("latitude",0.0);
                longitude = intent.getDoubleExtra("longitude", 0.0);
                Log.d(TAG, "Coord received: " + latitude + ", " + longitude);

                Cluster[] clusters = clusterManager.getAllClusters();

                for (Cluster cluster : clusters) {

                    Log.d(TAG, "Current coord: " + cluster.getLatitude() + ", " + cluster.getLongitude());

                    if (cluster.getLatitude() == latitude &&
                        cluster.getLongitude() == longitude) {

                        posts.clear();
                        posts.addAll(cluster.getPosts());
                        adapter.notifyDataSetChanged();

                    }

                    else {

                        //Toast.makeText(PostPreviewListActivity.this, "No nearby posts found!", Toast.LENGTH_LONG).show();

                    }

                }

                break;

            case "authorInfo":

                userId = intent.getStringExtra("userId");

                assert userId != null;
                if (!userId.equals(User.getInstance().getUserId())) {

                    followButton.setVisibility(View.VISIBLE);

                } else {

                    followButton.setVisibility(View.GONE);

                }

                profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                    @Override
                    public void onAuthorRetrieved(String authorName) {

                        modeTitle.setText(authorName + "'s Posts");

                    }

                    @Override
                    public void onError(Exception e) {

                        Log.d(TAG, String.valueOf(e));

                    }
                });

                postManager.getUserAllPosts(userId, this, new PostManager.PostCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Post> fetchedPosts) {

                        Log.d(TAG, "Succeed on getUserAllPosts(), " + fetchedPosts.toString());

                        posts.clear();
                        posts.addAll(fetchedPosts);
                        adapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onFailure(Exception e) {

                        Log.d(TAG, String.valueOf(e));

                    }
                });

                break;

            case "search":

                modeTitle.setText("Search result");

                String searchText = intent.getStringExtra("searchString");
                postManager.getSearchedPosts(searchText, this, new PostManager.PostCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Post> fetchedPosts) {

                        Log.d(TAG, "Succeed on getSearchedPosts(), " + fetchedPosts.toString());

                        posts.clear();
                        posts.addAll(fetchedPosts);
                        adapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onFailure(Exception e) {

                        Log.d(TAG, String.valueOf(e));

                    }
                });

                break;

            case "tag":

                modeTitle.setText("Filtered Posts");

                ArrayList<String> tagsList = intent.getStringArrayListExtra("tagsList");
                String userCurrentLat = intent.getStringExtra("userCurrentLat");
                String userCurrentLon = intent.getStringExtra("userCurrentLon");

                if (tagsList != null) {
                    postManager.getTagsSelectedPosts(userCurrentLat, userCurrentLon, tagsList,this, new PostManager.PostCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(List<Post> fetchedPosts) {

                            Log.d(TAG, "Succeed on getTagsSelectedPosts(), " + fetchedPosts.toString());

                            posts.clear();
                            posts.addAll(fetchedPosts);
                            adapter.notifyDataSetChanged();

                        }
                        @Override
                        public void onFailure(Exception e) {

                            Log.d(TAG, String.valueOf(e));

                        }
                    });

                }

                break;

            default:

        }

        followButton.setOnClickListener(view -> {

            profileManager.followAuthor(userId, this, new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {

                    profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                        @Override
                        public void onAuthorRetrieved(String authorName) {

                            Toast.makeText(PostPreviewListActivity.this, "Succeed following "+ authorName +" rn!", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onError(Exception e) {

                            Log.d(TAG, String.valueOf(e));

                        }
                    });

                }

                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(PostPreviewListActivity.this, "Unable to follow this user :(", Toast.LENGTH_LONG).show();
                    Log.d(TAG, String.valueOf(e));

                }
            });

        });

    }

}
