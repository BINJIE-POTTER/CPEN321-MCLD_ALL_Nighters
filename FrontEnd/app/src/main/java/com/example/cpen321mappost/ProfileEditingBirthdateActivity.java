package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

            birthdate = dayOfMonth + "-" + correctedMonth + "-" + year;

            if (dayOfMonth < 10) birthdate = "0" + dayOfMonth + "-" + correctedMonth + "-" + year;

            if (correctedMonth < 10) birthdate = dayOfMonth + "-" + "0" + correctedMonth + "-" + year;

            if (dayOfMonth < 10 && correctedMonth < 10) birthdate = "0" + dayOfMonth + "-" + "0" + correctedMonth + "-" + year;

            yearPicker.setValue(year);

        });

        saveButton.setOnClickListener(view -> {

            if (birthdate == null) {

                LocalDate today = LocalDate.now();
                birthdate = today.format(DateTimeFormatter.ofPattern("MMM. d - yyyy"));

            } else {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(birthdate, formatter);
                birthdate = date.format(DateTimeFormatter.ofPattern("MMM. d - yyyy"));

            }

            profileManager.getUserData(user, this, new User.UserCallback() {
                @Override
                public String onSuccess(User user) {
                    profileManager.putUserData(new User(user, null, null, null, null, birthdate), ProfileEditingBirthdateActivity.this, new User.UserCallback() {
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

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 100);
        yearPicker.setMaxValue(currentYear);

        yearPicker.setValue(currentYear);

        yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
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