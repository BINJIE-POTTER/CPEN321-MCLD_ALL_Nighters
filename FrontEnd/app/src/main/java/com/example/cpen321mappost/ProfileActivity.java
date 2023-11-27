package com.example.cpen321mappost;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
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

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
                }
            });

    //ChatGPT usage: Yes
    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();

                    if (imageUri != null) {

                        File avatarFile = new File(getRealPathFromURI(imageUri));
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
            finish();

        });

        followersButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the users list activity");
            Intent UserListIntent = new Intent(this, UserListActivity.class);
            UserListIntent.putExtra("mode", "followers");
            startActivity(UserListIntent);
            finish();

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

    public String getRealPathFromURI(Uri contentUri) {

        String result = null;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                if (idx != -1) result = cursor.getString(idx);

            }

            cursor.close();

        }

        // If direct path extraction failed, try reading from the stream
        if (result == null) {

            try (InputStream inputStream = getContentResolver().openInputStream(contentUri)) {

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


}