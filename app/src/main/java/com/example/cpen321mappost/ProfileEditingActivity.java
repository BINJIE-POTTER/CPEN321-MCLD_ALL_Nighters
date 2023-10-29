package com.example.cpen321mappost;

import android.text.NoCopySpan;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ProfileEditingActivity extends AppCompatActivity {
    private TextInputEditText newValueText;
    private Button saveButton, cancelButton;
    private ProfileManager profileManager;
    private String hint;
    private User user;
    final static String TAG = "ProfileEditing Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = User.getInstance();
        profileManager = new ProfileManager();

        saveButton = findViewById(R.id.edit_profile_save_button);
        cancelButton = findViewById(R.id.edit_profile_cancel_button);

        newValueText = findViewById(R.id.textInputEditText);

        Intent intent = getIntent();

        String item = intent.getStringExtra("item");

        profileManager.getUserData(user, new User.UserCallback() {
            @Override
            public void onSuccess(User user) {

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

            }
            @Override
            public void onFailure(Exception e) {

            }
        }, this);

        saveButton.setOnClickListener(view -> {

            String newInput = newValueText.getText().toString();

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

            Log.d(TAG, "1");

            profileManager.putUserData(user, new User.UserCallback() {
                @Override
                public void onSuccess(User user) {

                    Log.d(TAG, "2");
                    Toast.makeText(ProfileEditingActivity.this, newInput + " Changed!", Toast.LENGTH_LONG).show();

                    Intent ProfileIntent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
                    startActivity(ProfileIntent);

                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileEditingActivity.this, "Failed to upload new user info!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, this);


        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
