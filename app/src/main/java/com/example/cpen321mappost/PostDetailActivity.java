package com.example.cpen321mappost;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import android.graphics.BitmapFactory;


public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    private static final ProfileManager profileManager = new ProfileManager();
    private static final PostManager postManager = new PostManager();
    private final String myUserId = User.getInstance().getUserId();
    private String pid;
    private String uid;
    private boolean isLiked = false;
    private boolean isFollowed = false;

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewMainContent = findViewById(R.id.textViewMainContent);
        TextView textViewLikes = findViewById(R.id.textViewLikes);

        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonAuthor = findViewById(R.id.buttonAuthor);
        Button buttonLike = findViewById(R.id.buttonLike);
        Button buttonComment = findViewById(R.id.buttonComment);

        ImageView imageViewPost = findViewById(R.id.imageViewPost);

        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        postManager.getSinglePostData(pid, this, new PostManager.JsonCallback<Post>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Post post) {

                textViewTitle.setText(post.getContent().getTitle());
                textViewMainContent.setText(post.getContent().getBody());
                textViewLikes.setText("Likes: " + post.getLikeCount());

                isLiked = post.getLikeList().contains(myUserId);
                buttonLike.setText(isLiked ? "Unlike" : "Like");
                // Set the image
                if (post.getImageData() != null && post.getImageData().getImage() != null) {
                    byte[] decodedString = Base64.decode(post.getImageData().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageViewPost.setImageBitmap(decodedByte);
                }


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

                Toast.makeText(PostDetailActivity.this, "Deleted!", Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to delete this post!", Toast.LENGTH_LONG).show();

            }
        }));

        buttonAuthor.setOnClickListener(v -> {

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

                        textViewLikes.setText("Likes: " + post.getLikeCount());

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.e(TAG, e.toString());

                    }
                });

                if (isLiked) Toast.makeText(PostDetailActivity.this, "unliked this post!", Toast.LENGTH_SHORT).show();
                else         Toast.makeText(PostDetailActivity.this, "liked this post!", Toast.LENGTH_SHORT).show();
                isLiked = !isLiked;
                buttonLike.setText(isLiked ? "Unlike" : "Like");

            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(PostDetailActivity.this, "Failed to like this post :(", Toast.LENGTH_LONG).show();

            }
        }));

        buttonComment.setOnClickListener(v -> {

            Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
            intent.putExtra("pid", pid);
            startActivity(intent);

        });
    }
}


