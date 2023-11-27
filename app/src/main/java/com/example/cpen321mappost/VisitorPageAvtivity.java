package com.example.cpen321mappost;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VisitorPageAvtivity extends AppCompatActivity {
    private final static String TAG = "VisitorPage Activity";
    private final ProfileManager profileManager = new ProfileManager();
    private final PostManager postManager = new PostManager();
    private PostAdapter adapter;
    private List<Post> posts;
    private String userId;
    private boolean isFollowing;
    private boolean isFollowed;
    private TextView followingTextView;
    private TextView followersTextView;
    private TextView postCountTextView;
    private Button followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_page_avtivity);

        TextView nameTextView = findViewById(R.id.user_name_id);
        TextView genderTextView = findViewById(R.id.user_gender_id);
        TextView birthdateTextView = findViewById(R.id.user_birthdate_id);
        followingTextView = findViewById(R.id.user_following_count_text_id);
        followersTextView = findViewById(R.id.user_follower_count_text_id);
        postCountTextView = findViewById(R.id.user_post_count_text_id);

        Button followingButton = findViewById(R.id.user_following_count_id);
        Button followersButton = findViewById(R.id.user_follower_count_id);
        Button postCountButton = findViewById(R.id.user_post_count_id);
        followButton = findViewById(R.id.visit_page_follow_button_id);

        ImageView novice = findViewById(R.id.novice);
        ImageView explorer = findViewById(R.id.explorer);
        ImageView master = findViewById(R.id.master);
        ImageView avatarImageView = findViewById(R.id.avatar_visit_page);

        View lastDivider = findViewById(R.id.last_divider);
        LinearLayout achievementBoard = findViewById(R.id.achievement_board);
        RecyclerView recyclerView = findViewById(R.id.postsRecyclerView);

        lastDivider.setVisibility(View.GONE);
        achievementBoard.setVisibility(View.GONE);
        novice.setVisibility(View.GONE);
        explorer.setVisibility(View.GONE);
        master.setVisibility(View.GONE);

        posts = new ArrayList<>();
        adapter = new PostAdapter(posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        profileManager.getUserData(new User(userId), this, new User.UserCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "Succeed on get user data in profile activity");

                nameTextView.setText(user.getUserName());
                genderTextView.setText(user.getUserGender());
                birthdateTextView.setText(user.getUserBirthdate());
                followingTextView.setText(""+user.getFollowing().size());
                followersTextView.setText(""+user.getFollowers().size());
                postCountTextView.setText(""+user.getPostCount());

                if (user.getPostCount() >= 5) {


                    lastDivider.setVisibility(View.VISIBLE);
                    achievementBoard.setVisibility(View.VISIBLE);
                    achievementBoard.setWeightSum(1);
                    novice.setVisibility(View.VISIBLE);

                }

                if (user.getPostCount() >= 12) {

                    achievementBoard.setWeightSum(2);
                    explorer.setVisibility(View.VISIBLE);

                }

                if (user.getPostCount() >= 20) {

                    achievementBoard.setWeightSum(3);
                    master.setVisibility(View.VISIBLE);

                }

                if (user.getUserAvatar() != null && !Objects.equals(user.getUserAvatar().getImage(), "")) {

                    byte[] decodedString = Base64.decode(user.getUserAvatar().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    avatarImageView.setImageBitmap(decodedByte);

                }

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
}