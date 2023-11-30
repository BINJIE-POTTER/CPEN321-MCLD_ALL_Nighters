package com.example.cpen321mappost;

import android.content.Intent;
import android.os.Bundle;
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
    private final ProfileManager profileManager = new ProfileManager();
    private final User user = User.getInstance();
    final static String TAG = "ProfileEditing Activity";

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextInputEditText newValueText;
        Button saveButton;
        Button cancelButton;

        newValueText = findViewById(R.id.textInputEditText);
        saveButton = findViewById(R.id.edit_profile_save_button);
        cancelButton = findViewById(R.id.edit_profile_cancel_button);

        newValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("<", "");
                if (!s.toString().equals(result)) {
                    newValueText.setText(result);
                    newValueText.setSelection(result.length());
                }
            }
        });

        Intent intent = getIntent();

        String item = intent.getStringExtra("item");

        profileManager.getUserData(user, this, new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                String hint;

                assert item != null;
                if (item.equals("userName")) {

                    hint = user.getUserName();
                    newValueText.setHint(hint);

                } else if (item.equals("userEmail")) {

                    hint = user.getUserEmail();
                    newValueText.setHint(hint);

                } else {

                    Log.d(TAG, "Cannot resolve passed-in item.");

                }

                return null;

            }
            @Override
            public void onFailure(Exception e) {

                Toast.makeText(ProfileEditingActivity.this, "Failed to get User Data in ProfileEditingActivity", Toast.LENGTH_SHORT).show();

            }
        });

        saveButton.setOnClickListener(view -> {

            String newInput = Objects.requireNonNull(newValueText.getText()).toString();

            assert item != null;
            if (item.equals("userName")) {

                if (newInput.length() > 20) {

                    Toast.makeText(ProfileEditingActivity.this, "Name length too long, please keep it in 20 characters!", Toast.LENGTH_SHORT).show();
                    return;

                }

            } else if (item.equals("userEmail")) {

                if (!isValidEmail(newInput)) {

                    Toast.makeText(ProfileEditingActivity.this, "Invalid Email, Please enter again!", Toast.LENGTH_SHORT).show();
                    return;

                }

            } else {

                Log.d(TAG, "Cannot resolve passed-in item.");

            }

            profileManager.getUserData(user, this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {
                    profileManager.putUserData(new User(user, null, item.equals("userName") ? newInput : null, item.equals("userEmail") ? newInput : null, null, null), ProfileEditingActivity.this, new User.UserCallback() {
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

    private static boolean isValidEmail(String email) {

        if (email == null) return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return email.matches(emailRegex);

    }

}
