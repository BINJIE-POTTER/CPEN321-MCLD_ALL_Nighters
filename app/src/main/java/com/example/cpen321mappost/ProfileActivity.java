package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private User user;
    final static String TAG = "ProfileManager Activity";

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView nameTextView;
        TextView emailTextView;
        TextView genderTextView;
        TextView birthdateTextView;
        TextView userIdTextView;
        TextView followingTextView;
        TextView followersTextView;
        TextView postCountTextView;
        Button nameEditButton;
        Button emailEditButton;
        Button genderEditButton;
        Button birthdateEditButton;
        Button viewPostsButton;
        Button followingButton;
        Button followersButton;
        Button postCountButton;

        user = User.getInstance();
        ProfileManager profileManager = new ProfileManager();

        Log.d(TAG, "User ID: " + user.getUserId() + "," + "User Email: " + user.getUserEmail() + "," + "User name: " + user.getUserName() + "," + "User gender: " + user.getUserGender());

        // Initialize UI display
        nameTextView = findViewById(R.id.user_name_value_id);
        emailTextView = findViewById(R.id.user_email_value_id);
        genderTextView = findViewById(R.id.user_gender_value_id);
        birthdateTextView = findViewById(R.id.user_birthdate_value_id);
        userIdTextView = findViewById(R.id.user_id_value_id);
        followingTextView = findViewById(R.id.user_following_count_text_id);
        followersTextView = findViewById(R.id.user_follower_count_text_id);
        postCountTextView = findViewById(R.id.user_post_count_text_id);

        nameEditButton = findViewById(R.id.user_name_edit_button_id);
        emailEditButton = findViewById(R.id.user_email_edit_button_id);
        genderEditButton = findViewById(R.id.user_gender_edit_button_id);
        birthdateEditButton = findViewById(R.id.user_birthdate_edit_button_id);
        viewPostsButton = findViewById(R.id.user_view_posts_button_id);
        followingButton = findViewById(R.id.user_following_count_id);
        followersButton = findViewById(R.id.user_follower_count_id);
        postCountButton = findViewById(R.id.user_post_count_id);

        profileManager.getUserData(user, this, new User.UserCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "Succeed on get user data in profile activity");

                emailTextView.setText(user.getUserEmail());
                nameTextView.setText(user.getUserName());
                genderTextView.setText(user.getUserGender());
                birthdateTextView.setText(user.getUserBirthdate());
                userIdTextView.setText(user.getUserId());
                followingTextView.setText(""+user.getFollowing().size());
                followersTextView.setText(""+user.getFollowers().size());
                postCountTextView.setText(""+user.getPostCount());

                return null;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Exception e) {

                emailTextView.setText("error");
                nameTextView.setText("error");
                genderTextView.setText("error");
                birthdateTextView.setText("error");
                userIdTextView.setText("error");

                //Toast.makeText(ProfileActivity.this, "Failed to load user info!", Toast.LENGTH_LONG).show();

            }
        });

        nameEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userName");
            startActivity(ProfileEditingIntent);
            finish();

        });

        genderEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingSpinnerActivity.class);
            startActivity(ProfileEditingIntent);
            finish();

        });

        birthdateEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingBirthdateActivity.class);
            startActivity(ProfileEditingIntent);
            finish();

        });

        emailEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userEmail");
            startActivity(ProfileEditingIntent);
            finish();

        });

        viewPostsButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the posts preview list activity");
            Intent PostPreviewListIntent = new Intent(this, PostPreviewListActivity.class);
            PostPreviewListIntent.putExtra("mode", "profile");
            PostPreviewListIntent.putExtra("userId", user.getUserId());
            startActivity(PostPreviewListIntent);

        });

        postCountButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the posts preview list activity");
            Intent PostPreviewListIntent = new Intent(this, PostPreviewListActivity.class);
            PostPreviewListIntent.putExtra("mode", "profile");
            PostPreviewListIntent.putExtra("userId", user.getUserId());
            startActivity(PostPreviewListIntent);

        });

        followingButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followings");
            startActivity(UserListIntent);
            finish();

        });

        followersButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followers");
            startActivity(UserListIntent);
            finish();

        });

    }

}