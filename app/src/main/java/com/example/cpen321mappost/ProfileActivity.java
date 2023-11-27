package com.example.cpen321mappost;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private User user;
    private final static String TAG = "ProfileManager Activity";
    private final ProfileManager profileManager = new ProfileManager();
    private ImageView avatarImageView;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> getImage;
    private TextView followingTextView;
    private TextView followersTextView;
    private TextView postCountTextView;

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();

                        if (imageUri != null) {

                            File avatarFile = new File(getRealPathFromURI(imageUri, this));
                            profileManager.uploadUserAvatar(avatarFile, this, new User.UserCallback() {
                                @Override
                                public String onSuccess(User user) {

                                    Log.d(TAG,"AVATAR UPLOAD SUCCEED!");

                                    profileManager.getUserData(User.getInstance(), ProfileActivity.this, new User.UserCallback() {
                                        @Override
                                        public String onSuccess(User user) {

                                            Log.d(TAG, "Succeed on get user data in profile activity");

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

                                    return null;

                                }

                                @Override
                                public void onFailure(Exception e) {

                                    Log.e(TAG, e.toString());

                                }
                            });
                        }
                    }
                });

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                    if (isGranted.containsValue(true)) {
                        Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getImage.launch(pickImage);
                    } else {
                        Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
                    }
                });

        TextView nameTextView;
        TextView emailTextView;
        TextView genderTextView;
        TextView birthdateTextView;
        TextView userIdTextView;
        Button nameEditButton;
        Button emailEditButton;
        Button genderEditButton;
        Button birthdateEditButton;
        Button viewPostsButton;
        Button followingButton;
        Button followersButton;
        Button postCountButton;
        ImageView novice;
        ImageView explorer;
        ImageView master;
        View lastDivider;
        LinearLayout achievementBoard;

        user = User.getInstance();

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

        novice = findViewById(R.id.novice);
        explorer = findViewById(R.id.explorer);
        master = findViewById(R.id.master);
        lastDivider = findViewById(R.id.last_divider);
        achievementBoard = findViewById(R.id.achievement_board);

        lastDivider.setVisibility(View.GONE);
        achievementBoard.setVisibility(View.GONE);
        novice.setVisibility(View.GONE);
        explorer.setVisibility(View.GONE);
        master.setVisibility(View.GONE);

        avatarImageView = findViewById(R.id.avatar_profile);

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

        });

        followersButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followers");
            startActivity(UserListIntent);

        });

        novice.setOnClickListener(view -> {

            displayDialog(R.drawable.novice_achievement, "Novice Achiever", "Welcome to your journey of discovery! You've taken the first step by contributing 5 posts, and we're thrilled to see your ideas taking flight. Each post is a building block of your growing community presence. Keep sharing, keep exploring, and watch your impact grow. Your next milestone, Explorer, is just around the corner. Keep posting, keep shining!");

        });

        explorer.setOnClickListener(view -> {

            displayDialog(R.drawable.explorer_achievement, "Explorer Extraordinaire",  "Bravo! You’ve reached the Explorer level with your 12th post, proving your commitment and enthusiasm. As an Explorer, you’re not just participating; you're influencing and leading the conversation. Your insights are paving the way for new discussions and discoveries. Continue on this exciting path, and soon, the prestigious Master Achievement awaits you. Your voice matters, and your journey inspires!");

        });

        master.setOnClickListener(view -> {

            displayDialog(R.drawable.master_achievement, "Master Contributor", "Outstanding! With 20 posts, you've ascended to the Master level, a testament to your dedication and expertise. Your contributions are significant, driving meaningful dialogue and shaping the essence of our community. As a Master Contributor, you embody the spirit of leadership and collaboration. Your journey doesn’t end here. Keep leading the way, keep inspiring, and continue to set a benchmark for excellence!");

        });

        avatarImageView.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});

            } else {

                Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getImage.launch(pickImage);

            }
        });
    }

    public String getRealPathFromURI(Uri contentUri, Context context) {

        String result = null;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                if (idx != -1) result = cursor.getString(idx);

            }

            cursor.close();

        }

        // If direct path extraction failed, try reading from the stream
        if (result == null) {

            try (InputStream inputStream = context.getContentResolver().openInputStream(contentUri)) {

                if (inputStream != null) {

                    File tempFile = createTemporaryFileFromStream(inputStream, contentUri.getLastPathSegment());
                    if (tempFile != null) result = tempFile.getAbsolutePath();

                }

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        return result;

    }

    private File createTemporaryFileFromStream(InputStream inputStream, String fileName) {

        try {

            File tempFile = File.createTempFile("temp_" + fileName, null, getCacheDir());

            try (OutputStream out = Files.newOutputStream(tempFile.toPath())) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {

                    out.write(buffer, 0, bytesRead);

                }
            }

            return tempFile;

        } catch (IOException e) {

            e.printStackTrace();

            return null;

        }
    }

    @SuppressLint("SetTextI18n")
    private void displayDialog(int resId, String title, String description) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_image_view, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        ImageView dialogImageView = dialogView.findViewById(R.id.dialog_image);
        TextView dialogTextView = dialogView.findViewById(R.id.dialog_text);

        dialogTitle.setText(title);
        dialogImageView.setImageResource(resId);
        dialogTextView.setText(description);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        profileManager.getUserData(user, this, new User.UserCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "Succeed on get user data in profile activity");

                followingTextView.setText(""+user.getFollowing().size());
                followersTextView.setText(""+user.getFollowers().size());
                postCountTextView.setText(""+user.getPostCount());

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