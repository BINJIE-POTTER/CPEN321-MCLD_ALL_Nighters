package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ProfileEditingSpinnerActivity extends AppCompatActivity {
    private ProfileManager profileManager;
    private User user;
    final static String TAG = "ProfileEditingSpinner Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_spinner);

        user = User.getInstance();
        profileManager = new ProfileManager();

        Button saveButton;
        Button cancelButton;

        saveButton = findViewById(R.id.edit_profile_save_button);
        cancelButton = findViewById(R.id.edit_profile_cancel_button);

        Spinner genderSpinner = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(view -> {

            String newGender = genderSpinner.getSelectedItem().toString();

            user.setUserGender(newGender);

            profileManager.putUserData(user, this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {

                    Toast.makeText(ProfileEditingSpinnerActivity.this, "Saved!", Toast.LENGTH_LONG).show();

                    Intent ProfileIntent = new Intent(ProfileEditingSpinnerActivity.this, ProfileActivity.class);
                    startActivity(ProfileIntent);

                    finish();

                    return null;

                }

                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(ProfileEditingSpinnerActivity.this, "Failed to upload new user info!", Toast.LENGTH_LONG).show();
                    finish();

                }
            });

        });

        cancelButton.setOnClickListener(view -> {

            Intent ProfileIntent = new Intent(ProfileEditingSpinnerActivity.this, ProfileActivity.class);
            startActivity(ProfileIntent);

            finish();

        });
    }
}