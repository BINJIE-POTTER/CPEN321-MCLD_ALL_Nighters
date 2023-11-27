package com.example.cpen321mappost;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import android.graphics.BitmapFactory;


public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    private static final ProfileManager profileManager = new ProfileManager();
    private static final PostManager postManager = new PostManager();
    private static final CommentManager commentManager = new CommentManager();
    private final String myUserId = User.getInstance().getUserId();
    private String pid;
    private String uid;
    private boolean isLiked = false;
    private boolean isFollowing = false;
    private int postlikes;

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TextView authorName = findViewById(R.id.textViewPostDetail);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewMainContent = findViewById(R.id.textViewMainContent);

        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonLike = findViewById(R.id.buttonLike);
        Button buttonComment = findViewById(R.id.buttonComment);
        Button followButton = findViewById(R.id.post_detail_follow_button_id);

        CardView cardView = findViewById(R.id.cardViewPost);
        ImageView imageViewPost = findViewById(R.id.imageViewPost);
        ImageView avatarImageView = findViewById(R.id.avatar_post_detail);

        RecyclerView recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        EditText editTextComment = findViewById(R.id.editTextComment);

        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        postManager.getSinglePostData(pid, this, new PostManager.JsonCallback<Post>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Post post) {

                textViewTitle.setText(post.getContent().getTitle());
                textViewMainContent.setText(post.getContent().getBody());

                isLiked = post.getLikeList().contains(myUserId);
                buttonLike.setText(isLiked ? "❤️ " + post.getLikeCount() : "🩶 " + post.getLikeCount());

                if (post.getImageData() != null && !Objects.equals(post.getImageData().getImage(), "")) {

                    byte[] decodedString = Base64.decode(post.getImageData().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    cardView.setVisibility(View.VISIBLE);
                    imageViewPost.setImageBitmap(decodedByte);

                } else {

                    cardView.setVisibility(View.GONE);

                }

                uid = post.getUserId();

                if (Objects.equals(uid, User.getInstance().getUserId())){

                    buttonDelete.setVisibility(View.VISIBLE);

                } else {

                    buttonDelete.setVisibility(View.GONE);

                    followButton.setVisibility(View.VISIBLE);
                    profileManager.getUserData(User.getInstance(), PostDetailActivity.this, new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            isFollowing = user.getFollowing().contains(uid);

                            followButton.setText(isFollowing ? "following" : "follow");

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.e(TAG, e.toString());

                        }
                    });
                }

                profileManager.getUserData(new User(uid), PostDetailActivity.this, new User.UserCallback() {
                    @Override
                    public String onSuccess(User user) {

                        authorName.setText(user.getUserName());

                        if (user.getUserAvatar() != null && !Objects.equals(user.getUserAvatar().getImage(), "")) {

                            byte[] decodedString = Base64.decode(user.getUserAvatar().getImage(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatarImageView.setImageBitmap(decodedByte);

                        }

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.e(TAG, e.toString());

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to fetch this post!", Toast.LENGTH_LONG).show();

            }
        });

        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll("<", "");

                if (!s.toString().equals(result)) {

                    editTextComment.setText(result);
                    editTextComment.setSelection(result.length());

                }
            }
        });

        buttonDelete.setOnClickListener(v -> postManager.deletePostData(pid, PostDetailActivity.this, new PostManager.JsonCallback<Void>() {
            @Override
            public void onSuccess(Void result) {

                Toast.makeText(PostDetailActivity.this, "Deleted!", Toast.LENGTH_LONG).show();

                finish();

            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to delete this post!", Toast.LENGTH_LONG).show();

            }
        }));

        avatarImageView.setOnClickListener(v -> {

            Intent intent = new Intent(PostDetailActivity.this, PostPreviewListActivity.class);
            intent.putExtra("mode", "authorInfo");
            intent.putExtra("userId", uid);
            startActivity(intent);

        });

        buttonLike.setOnClickListener(v -> postManager.likePostData(!isLiked, pid, myUserId, PostDetailActivity.this, new PostManager.JsonCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                postManager.getSinglePostData(pid, PostDetailActivity.this, new PostManager.JsonCallback<Post>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Post post) {

                        postlikes = post.getLikeCount();

                        isLiked = !isLiked;
                        buttonLike.setText(isLiked ? "❤️ " + postlikes : "🩶 " + postlikes);

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.e(TAG, e.toString());

                    }
                });

                if (!isLiked) Toast.makeText(PostDetailActivity.this, "liked this post!", Toast.LENGTH_SHORT).show();
                else         Toast.makeText(PostDetailActivity.this, "unliked this post!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to like this post :(", Toast.LENGTH_LONG).show();

            }
        }));

        buttonComment.setOnClickListener(v -> {

            String newComment = editTextComment.getText().toString();

            if (!newComment.isEmpty()) {

                editTextComment.setText("");

                Comment comment = new Comment(pid, User.getInstance().getUserId(), commentManager.getCurrentDateUsingCalendar(), newComment);

                commentManager.postCommentData(comment, this, new CommentManager.CommentCallback() {
                    @Override
                    public void onCommentsReceived(List<Comment> comments) {

                        commentManager.displayAllComments(PostDetailActivity.this, pid, recyclerViewComments);

                    }
                    @Override
                    public void onPostCommentSuccess() {

                        commentManager.displayAllComments(PostDetailActivity.this, pid, recyclerViewComments);

                    }
                    @Override
                    public void onFailure(Exception e) {

                        Log.e(TAG, "Failed to post comment", e);

                    }
                });
            }
        });

        commentManager.displayAllComments(this, pid, recyclerViewComments);

        followButton.setOnClickListener(view -> {
            profileManager.followAuthor(isFollowing, uid, this, new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {
                    profileManager.getUserData(User.getInstance(), PostDetailActivity.this, new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            isFollowing = user.getFollowing().contains(userId);
                            followButton.setText(isFollowing ? "following" : "follow");
                            profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                                @Override
                                public void onAuthorRetrieved(String authorName) {

                                    if (isFollowing) Toast.makeText(PostDetailActivity.this, "Succeed following "+ authorName +" rn!", Toast.LENGTH_SHORT).show();
                                    else             Toast.makeText(PostDetailActivity.this, "Succeed to unfollow "+ authorName +" !", Toast.LENGTH_SHORT).show();

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

                    Toast.makeText(PostDetailActivity.this, "Unable to follow this user :(", Toast.LENGTH_LONG).show();
                    Log.d(TAG, String.valueOf(e));

                }
            });
        });
    }
}


