package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView, emailTextView, genderTextView, birthdateTextView, userIdTextView;
    private Button nameEditButton, emailEditButton, genderEditButton, birthdateEditButton;
    private User user;
    private ProfileManager profileManager;
    final static String TAG = "ProfileManager Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = User.getInstance();
        profileManager = new ProfileManager();

        Log.d(TAG, "User ID: " + user.getUserId() + "," + "User Email: " + user.getUserEmail() + "," + "User name: " + user.getUserName() + ",");

        // Initialize UI display
        nameTextView = findViewById(R.id.user_name_value_id);
        emailTextView = findViewById(R.id.user_email_value_id);
        genderTextView = findViewById(R.id.user_gender_value_id);
        birthdateTextView = findViewById(R.id.user_birthdate_value_id);
        userIdTextView = findViewById(R.id.user_id_value_id);

        nameEditButton = findViewById(R.id.user_name_edit_button_id);
        emailEditButton = findViewById(R.id.user_email_edit_button_id);
        genderEditButton = findViewById(R.id.user_gender_edit_button_id);
        birthdateEditButton = findViewById(R.id.user_birthdate_edit_button_id);

        profileManager.getUserData(user, new User.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // This is run on the UI thread, safe to update UI components
                // e.g., display user details in the UI

                Log.d(TAG, "Succeed on get user data");

                emailTextView.setText(user.getUserEmail());
                nameTextView.setText(user.getUserName());
                genderTextView.setText(user.getUserGender());
                birthdateTextView.setText(user.getUserBirthdate());
                userIdTextView.setText(user.getUserId());

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Exception e) {
                // This is run on the UI thread, safe to update UI components
                // e.g., show an error message

                emailTextView.setText("error");
                nameTextView.setText("error");
                genderTextView.setText("error");
                birthdateTextView.setText("error");
                userIdTextView.setText("error");

                //Toast.makeText(ProfileActivity.this, "Failed to load user info!", Toast.LENGTH_LONG).show();

            }
        }, this); // 'this' is the current activity, which is passed for context


        nameEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userName");
            startActivity(ProfileEditingIntent);

        });

        genderEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userGender");
            startActivity(ProfileEditingIntent);

        });

        birthdateEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userBirthdate");
            startActivity(ProfileEditingIntent);

        });

        emailEditButton.setOnClickListener(view -> {

            Log.d(TAG,"Opening the profile editing activity");
            Intent ProfileEditingIntent = new Intent(this, ProfileEditingActivity.class);
            ProfileEditingIntent.putExtra("item", "userEmail");
            startActivity(ProfileEditingIntent);

        });


    }

}