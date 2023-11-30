package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VisitorPageAvtivity extends AppCompatActivity {
    private final static String TAG = "VisitorPage Activity";
    private final static ProfileManager profileManager = new ProfileManager();
    private final static PostManager postManager = new PostManager();
    private PostAdapter adapter;
    private final List<Post> posts = new ArrayList<>();
    private String userId;
    private boolean isFollowing;
    private boolean isFollowed;
    private TextView followingTextView;
    private TextView followersTextView;
    private TextView postCountTextView;
    private TextView nameTextView;
    private TextView genderTextView;
    private TextView birthdateTextView;
    private Button followButton;
    private Button followingButton;
    private Button followersButton;
    private Button postCountButton;
    private ImageView novice;
    private ImageView explorer;
    private ImageView master;
    private ImageView avatarImageView;
    private View lastDivider;
    private LinearLayout achievementBoard;
    private RecyclerView recyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_page_avtivity);

        initializeUI();

        adapter = new PostAdapter(posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        loadUserData();
        setOnClickers();

    }

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

                Toast.makeText(VisitorPageAvtivity.this, "Failed to get user's all posts ", Toast.LENGTH_SHORT).show();
                final Toast toast = Toast.makeText(VisitorPageAvtivity.this, "Failed to get user's all posts in PostPreviewList", Toast.LENGTH_LONG);
                final Handler handler = new Handler();
                handler.postDelayed(toast::show, 3000); // 3000ms delay to show the toast again after the initial showing

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        profileManager.getUserData(new User(userId), this, new User.UserCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "Succeed on get user data in profile activity");

                followingTextView.setText(""+user.getFollowing().size());
                followersTextView.setText(""+user.getFollowers().size());
                postCountTextView.setText(""+user.getPostCount());

                profileManager.getUserData(User.getInstance(), new Activity(), new User.UserCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public String onSuccess(User me) {

                        isFollowing = me.getFollowing().contains(user.getUserId());
                        isFollowed  = user.getFollowing().contains(me.getUserId());

                        if (isFollowing) {

                            if (isFollowed) followButton.setText("mutual");

                            else followButton.setText("following");

                        } else followButton.setText("follow");

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.d(TAG, String.valueOf(e));

                    }
                });

                return null;

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Exception e) {

                Log.e(TAG, e.toString());

            }
        });
    }

    private void initializeUI() {

        nameTextView = findViewById(R.id.user_name_id);
        genderTextView = findViewById(R.id.user_gender_id);
        birthdateTextView = findViewById(R.id.user_birthdate_id);
        followingTextView = findViewById(R.id.user_following_count_text_id);
        followersTextView = findViewById(R.id.user_follower_count_text_id);
        postCountTextView = findViewById(R.id.user_post_count_text_id);

        followingButton = findViewById(R.id.user_following_count_id);
        followersButton = findViewById(R.id.user_follower_count_id);
        postCountButton = findViewById(R.id.user_post_count_id);
        followButton = findViewById(R.id.visit_page_follow_button_id);

        novice = findViewById(R.id.novice);
        explorer = findViewById(R.id.explorer);
        master = findViewById(R.id.master);
        avatarImageView = findViewById(R.id.avatar_visit_page);

        lastDivider = findViewById(R.id.last_divider);
        achievementBoard = findViewById(R.id.achievement_board);
        recyclerView = findViewById(R.id.postsRecyclerView);

        lastDivider.setVisibility(View.GONE);
        achievementBoard.setVisibility(View.GONE);
        novice.setVisibility(View.GONE);
        explorer.setVisibility(View.GONE);
        master.setVisibility(View.GONE);

    }

    private void loadUserData() {
        profileManager.getUserData(new User(userId), this, new User.UserCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "Succeed on get user data in profile activity");

                nameTextView.setText(user.getUserName());
                genderTextView.setText(user.getUserGender().equals("none") ? "" : user.getUserGender());
                birthdateTextView.setText(user.getUserBirthdate().equals("none") ? "" : user.getUserBirthdate());
                followingTextView.setText(""+user.getFollowing().size());
                followersTextView.setText(""+user.getFollowers().size());
                postCountTextView.setText(""+user.getPostCount());

                if (user.getPostCount() >= 1) {

                    lastDivider.setVisibility(View.VISIBLE);
                    achievementBoard.setVisibility(View.VISIBLE);
                    achievementBoard.setWeightSum(1);
                    novice.setVisibility(View.VISIBLE);

                }

                if (user.getPostCount() >= 2) {

                    achievementBoard.setWeightSum(2);
                    explorer.setVisibility(View.VISIBLE);

                }

                if (user.getPostCount() >= 3) {

                    achievementBoard.setWeightSum(3);
                    master.setVisibility(View.VISIBLE);

                }

                if (user.getUserAvatar() != null && !Objects.equals(user.getUserAvatar().getImage(), "")) {

                    byte[] decodedString = Base64.decode(user.getUserAvatar().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    avatarImageView.setImageBitmap(decodedByte);

                }

                if (!User.isLoggedIn()) followButton.setText("follow");

                else {

                    profileManager.getUserData(User.getInstance(), new Activity(), new User.UserCallback() {
                        @Override
                        public String onSuccess(User me) {

                            isFollowing = me.getFollowing().contains(user.getUserId());
                            isFollowed  = user.getFollowing().contains(me.getUserId());

                            if (isFollowing) {

                                if (isFollowed) followButton.setText("mutual");

                                else followButton.setText("following");

                            } else followButton.setText("follow");

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.e(TAG, e.toString());

                        }
                    });
                }

                return null;

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Exception e) {

                nameTextView.setText("error");
                genderTextView.setText("error");
                birthdateTextView.setText("error");

            }
        });

        showProfile(userId);

    }

    private void setOnClickers() {

        postCountButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the posts preview list activity");
            Intent PostPreviewListIntent = new Intent(this, PostPreviewListActivity.class);
            PostPreviewListIntent.putExtra("mode", "profile");
            PostPreviewListIntent.putExtra("userId", userId);
            startActivity(PostPreviewListIntent);

        });

        followingButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followings");
            UserListIntent.putExtra("userId", userId);
            startActivity(UserListIntent);

        });

        followersButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followers");
            UserListIntent.putExtra("userId", userId);
            startActivity(UserListIntent);

        });

        followButton.setOnClickListener(view -> {

            if (!User.isLoggedIn()) {

                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();

                return;

            }
            profileManager.followAuthor(isFollowing, userId, new Activity(), new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {
                    profileManager.getUserData(new User(userId), new Activity(), new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {
                            profileManager.getUserData(User.getInstance(), new Activity(), new User.UserCallback() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public String onSuccess(User me) {

                                    isFollowing = me.getFollowing().contains(user.getUserId());
                                    isFollowed  = user.getFollowing().contains(me.getUserId());

                                    if (isFollowing) {

                                        if (isFollowed) followButton.setText("mutual");

                                        else followButton.setText("following");

                                    } else followButton.setText("follow");

                                    profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                                        @Override
                                        public void onAuthorRetrieved(String authorName) {

                                            if (isFollowing) Toast.makeText(VisitorPageAvtivity.this, "Succeed to follow "+ authorName +"!", Toast.LENGTH_SHORT).show();
                                            else             Toast.makeText(VisitorPageAvtivity.this, "Succeed to unfollow "+ authorName +"!", Toast.LENGTH_SHORT).show();

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

                                    Log.d(TAG, String.valueOf(e));

                                }
                            });

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.d(TAG, String.valueOf(e));

                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(VisitorPageAvtivity.this, "Unable to follow this user :(", Toast.LENGTH_LONG).show();
                    Log.d(TAG, String.valueOf(e));

                }
            });
        });
    }
}