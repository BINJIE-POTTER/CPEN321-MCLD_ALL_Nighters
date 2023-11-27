package com.example.cpen321mappost;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ProfileEditingActivity extends AppCompatActivity {
    private ProfileManager profileManager;
    private User user;
    final static String TAG = "ProfileEditing Activity";

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextInputEditText newValueText;
        Button saveButton;
        Button cancelButton;

        user = User.getInstance();
        profileManager = new ProfileManager();

        newValueText = findViewById(R.id.textInputEditText);
        saveButton = findViewById(R.id.edit_profile_save_button);
        cancelButton = findViewById(R.id.edit_profile_cancel_button);

        //Implement the live checking:
        newValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement anything here for this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No need to implement anything here for this case
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("<", "");
                if (!s.toString().equals(result)) {
                    newValueText.setText(result);
                    newValueText.setSelection(result.length()); // Set cursor to the end
                }
            }
        });


        Intent intent = getIntent();

        String item = intent.getStringExtra("item");

        profileManager.getUserData(user, this, new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                String hint;

                switch (Objects.requireNonNull(item)) {

                    case "userName":

                        hint = user.getUserName();
                        newValueText.setHint(hint);

                        break;
                    case "userGender":

                        hint = user.getUserGender();
                        newValueText.setHint(hint);

                        break;
                    case "userBirthdate":

                        hint = user.getUserBirthdate();
                        newValueText.setHint(hint);

                        break;
                    case "userEmail":

                        hint = user.getUserEmail();
                        newValueText.setHint(hint);

                        break;
                    default:

                        Log.d(TAG, "Cannot resolve passed-in item.");

                        break;

                }

                return null;
            }
            @Override
            public void onFailure(Exception e) {

                Toast.makeText(ProfileEditingActivity.this, "Failed to get User Data in ProfileEditingActivity", Toast.LENGTH_SHORT).show();
                final Toast toast = Toast.makeText(ProfileEditingActivity.this, "Failed to get User Data in ProfileEditingActivity", Toast.LENGTH_LONG);
                final Handler handler = new Handler();
                handler.postDelayed(toast::show, 3000); // 3000ms delay to show the toast again after the initial showing

            }
        });

        saveButton.setOnClickListener(view -> {

            String newInput = Objects.requireNonNull(newValueText.getText()).toString();

            switch (Objects.requireNonNull(item)) {

                case "userName":

                    Log.d(TAG, "going to set the new user name: " + newInput);

                    user.setUserName(newInput);

                    Log.d(TAG, "Setted the name!");

                    break;
                case "userGender":

                    user.setUserGender(newInput);

                    break;
                case "userBirthdate":

                    user.setUserBirthdate(newInput);

                    break;
                case "userEmail":

                    user.setUserEmail(newInput);

                    break;
                default:

                    Log.d(TAG, "Cannot resolve passed-in item.");

                    break;

            }

            profileManager.putUserData(user, this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {

                    Toast.makeText(ProfileEditingActivity.this, "Saved!", Toast.LENGTH_LONG).show();

                    Intent ProfileIntent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
                    startActivity(ProfileIntent);

                    finish();

                    return null;

                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileEditingActivity.this, "Failed to upload new user info!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        });

        cancelButton.setOnClickListener(view -> {

            Intent ProfileIntent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
            startActivity(ProfileIntent);

            finish();

        });

    }
    @Override
    public void onBackPressed() {

        Intent ProfileIntent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
        startActivity(ProfileIntent);

        finish();

    }

}
