package com.example.cpen321mappost;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView, emailTextView, genderTextView, birthdateTextView, userIdTextView;
    private Button nameEditButton, emailEditButton, genderEditButton, birthdateEditButton, userIdEditButton;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileManager profileManager = new ProfileManager();

        //String userId = firebaseUser.getUid();;

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
        userIdEditButton = findViewById(R.id.user_id_edit_button_id);

        //currentUserId = ();

//        emailTextView.setText(currentUser.getUserEmail());
//        nameTextView.setText(currentUser.getUserName());
//        genderTextView.setText(currentUser.getUserGender());
//        birthdateTextView.setText(currentUser.getUserBirthdate());
//        userIdTextView.setText(currentUser.getUserId());

    }



}