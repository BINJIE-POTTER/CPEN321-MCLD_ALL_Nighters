package com.example.cpen321mappost;

import static com.example.cpen321mappost.MapsActivity.getClusteredPostData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostPreviewListActivity extends AppCompatActivity {
    private final static String TAG = "Post Preview List Activity";
    private static final ClusterManager clusterManager = ClusterManager.getInstance();
    private String userId;
    private PostAdapter adapter;
    private List<Post> posts;
    private PostManager postManager;
    private ProfileManager profileManager;
    private TextView modeTitle;
    private Button followButton;
    private boolean isFollowing = false;

    //ChatGPT usage: Partial
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview_list);

        postManager = new PostManager();
        profileManager = new ProfileManager();

        double latitude;
        double longitude;

        RecyclerView recyclerView = findViewById(R.id.postsRecyclerView);
        modeTitle = findViewById(R.id.post_preview_list_mode_name_id);
        followButton = findViewById(R.id.post_preview_list_follow_button_id);
        followButton.setVisibility(View.GONE);

        posts = new ArrayList<>();
        adapter = new PostAdapter(posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        switch (Objects.requireNonNull(mode)) {

            case "profile":

                userId = intent.getStringExtra("userId");

                profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                    @Override
                    public void onAuthorRetrieved(String authorName) {

                        modeTitle.setText(authorName + "'s posts");

                    }

                    @Override
                    public void onError(Exception e) {

                        Log.e(TAG, e.toString());

                    }
                });

                showProfile(userId);

                break;

            case "reviewPosts":

                modeTitle.setText("All near by posts");

                latitude = intent.getDoubleExtra("latitude",0.0);
                longitude = intent.getDoubleExtra("longitude", 0.0);
                Log.d(TAG, "Coord received: " + latitude + ", " + longitude);

                showReviewPosts(latitude, longitude);

                break;

            case "authorInfo":

                userId = intent.getStringExtra("userId");

                assert userId != null;
                if (!userId.equals(User.getInstance().getUserId())) {

                    followButton.setVisibility(View.VISIBLE);

                } else {

                    followButton.setVisibility(View.GONE);

                }

                profileManager.getUserData(User.getInstance(), this, new User.UserCallback() {
                    @Override
                    public String onSuccess(User user) {

                        isFollowing = user.getFollowing().contains(userId);

                        showAuthorInfo(userId, isFollowing);

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.e(TAG, e.toString());

                    }
                });

                break;

            case "search":

                modeTitle.setText("Search result");

                String searchText = intent.getStringExtra("searchString");

                showSearch(searchText);

                break;

            case "tag":

                modeTitle.setText("Filtered Posts");

                ArrayList<String> tagsList = intent.getStringArrayListExtra("tagsList");
                String userCurrentLat = intent.getStringExtra("userCurrentLat");
                String userCurrentLon = intent.getStringExtra("userCurrentLon");

                showTag(tagsList, userCurrentLat, userCurrentLon);

                break;

            default:

        }

        followButton.setOnClickListener(view -> {

            profileManager.followAuthor(isFollowing, userId, this, new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {
                    profileManager.getUserData(User.getInstance(), PostPreviewListActivity.this, new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            isFollowing = user.getFollowing().contains(userId);
                            followButton.setText(isFollowing ? "following" : "follow");
                            profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                                @Override
                                public void onAuthorRetrieved(String authorName) {

                                    if (isFollowing) Toast.makeText(PostPreviewListActivity.this, "Succeed to follow "+ authorName +"!", Toast.LENGTH_SHORT).show();
                                    else             Toast.makeText(PostPreviewListActivity.this, "Succeed to unfollow "+ authorName +"!", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onError(Exception e) {

                                    Log.d(TAG, String.valueOf(e));

                                }
                            });

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.d(TAG, "Cannot update user info after follow" + e);

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

    //ChatGPT usage: Partial
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        if ("profile".equals(mode)) {

            userId = intent.getStringExtra("userId");

            showProfile(userId);

        } else if ("reviewPosts".equals(mode)) {

            double latitude = intent.getDoubleExtra("latitude",0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);

            showReviewPosts(latitude, longitude);

        } else if ("authorInfo".equals(mode)) {

            userId = intent.getStringExtra("userId");
            profileManager.getUserData(User.getInstance(), this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {

                    isFollowing = user.getFollowing().contains(userId);

                    showAuthorInfo(userId, isFollowing);

                    return null;
                }

                @Override
                public void onFailure(Exception e) {

                    Log.d(TAG, String.valueOf(e));

                }
            });

        } else if ("search".equals(mode)) {

            String searchText = intent.getStringExtra("searchString");

            showSearch(searchText);

        } else {

            ArrayList<String> tagsList = intent.getStringArrayListExtra("tagsList");
            String userCurrentLat = intent.getStringExtra("userCurrentLat");
            String userCurrentLon = intent.getStringExtra("userCurrentLon");

            showTag(tagsList, userCurrentLat, userCurrentLon);

        }

        followButton.setOnClickListener(view -> {

            profileManager.followAuthor(isFollowing, userId, this, new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {
                    profileManager.getUserData(User.getInstance(), PostPreviewListActivity.this, new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            isFollowing = user.getFollowing().contains(userId);
                            followButton.setText(isFollowing ? "following" : "follow");
                            profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                                @Override
                                public void onAuthorRetrieved(String authorName) {

                                    if (isFollowing) Toast.makeText(PostPreviewListActivity.this, "Succeed to follow "+ authorName +"!", Toast.LENGTH_SHORT).show();
                                    else             Toast.makeText(PostPreviewListActivity.this, "Succeed to unfollow "+ authorName +"!", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onError(Exception e) {

                                    Log.d(TAG, String.valueOf(e));

                                }
                            });

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.d(TAG, "Cannot update user info after follow" + e);

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

    //ChatGPT usage: Partial
    private void showProfile(String userId) {
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
                handler.postDelayed(toast::show, 3000); // 3000ms delay to show the toast again after the initial showing

            }
        });
    }

    //ChatGPT usage: No
    @SuppressLint("NotifyDataSetChanged")
    private void showReviewPosts(double latitude, double longitude) {

        JSONObject coordinate = new JSONObject();
        try {
            coordinate.put("latitude", latitude);
            coordinate.put("longitude", longitude);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        getClusteredPostData(coordinate, this, new MapsActivity.JsonPostCallback() {
            @Override
            public void onSuccess(Cluster[] markers) {

                clusterManager.setAllClusters(markers);

                Cluster[] clusters = clusterManager.getAllClusters();

                boolean found = false;

                for (Cluster cluster : clusters) {

                    Log.d(TAG, "Current coord: " + cluster.getLatitude() + ", " + cluster.getLongitude());

                    if (cluster.getLatitude() == latitude && cluster.getLongitude() == longitude) {

                        posts.clear();
                        posts.addAll(cluster.getPosts());
                        adapter.notifyDataSetChanged();

                        found = true;
                        break;

                    }
                }

                if (!found) {

                    posts.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(PostPreviewListActivity.this, "No nearby posts found!", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }

            @Override
            public void onFailure(Exception e) {

                Log.e(TAG, e.toString());

            }
        });
    }

    //ChatGPT usage: No
    private void showAuthorInfo(String userId, boolean isFollowing) {
        profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthorRetrieved(String authorName) {

                modeTitle.setText(authorName + "'s Posts");
                followButton.setText(isFollowing ? "following" : "follow");

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
    }

    //ChatGPT usage: No
    private void showSearch(String searchText) {
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

    }

    //ChatGPT usage: No
    private void showTag(ArrayList<String> tagsList, String userCurrentLat, String userCurrentLon) {

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
    }
}
