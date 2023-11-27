package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

public class ProfileEditingBirthdateActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private NumberPicker yearPicker;
    private String birthdate;
    private final User user = User.getInstance();
    private final ProfileManager profileManager = new ProfileManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing_birthdate);

        calendarView = findViewById(R.id.calendarView);
        yearPicker = findViewById(R.id.picker_year);
        Button saveButton = findViewById(R.id.edit_profile_save_button);
        Button cancelButton  = findViewById(R.id.edit_profile_cancel_button);

        initYearPicker();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            int correctedMonth = month + 1;

            birthdate = dayOfMonth + "/" + correctedMonth + "/" + year;

            yearPicker.setValue(year);

        });

        saveButton.setOnClickListener(view -> {

            user.setUserBirthdate(birthdate);
            profileManager.putUserData(user, this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {

                    Toast.makeText(ProfileEditingBirthdateActivity.this, "Saved!", Toast.LENGTH_SHORT).show();

                    Intent ProfileIntent = new Intent(ProfileEditingBirthdateActivity.this, ProfileActivity.class);
                    startActivity(ProfileIntent);

                    finish();

                    return null;

                }

                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(ProfileEditingBirthdateActivity.this, "Failed to upload new user info!", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });

        });

        cancelButton.setOnClickListener(view -> {

            Intent ProfileIntent = new Intent(ProfileEditingBirthdateActivity.this, ProfileActivity.class);
            startActivity(ProfileIntent);

            finish();

        });

    }

    private void initYearPicker() {
        // Set the year range for the picker
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 100); // for example
        yearPicker.setMaxValue(currentYear);

        // Set the current year as default
        yearPicker.setValue(currentYear);

        // Add a listener to update the calendar view when the year changes
        yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            // Update the calendar view to reflect the new year
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, newVal);
            calendarView.setDate(calendar.getTimeInMillis());
        });
    }

    @Override
    public void onBackPressed() {

        Intent ProfileIntent = new Intent(this, ProfileActivity.class);
        startActivity(ProfileIntent);

        finish();

    }

}