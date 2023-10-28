package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.util.Calendar;


import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class PostActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText titleEditText, mainTextEditText;
    private Button uploadImageButton;
    private Button saveButton;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    imgPreview.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imgPreview = findViewById(R.id.imgPreview);
        titleEditText = findViewById(R.id.titleEditText);
        mainTextEditText = findViewById(R.id.mainTextEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveButton = findViewById(R.id.saveButton);
        Intent receivedIntent = getIntent();
        String latitude = receivedIntent.getStringExtra("latitude");
        String longitude = receivedIntent.getStringExtra("longitude");
        User currentuser= new User();

        // Check permissions at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }

        //Upload the image

        uploadImageButton.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getImage.launch(pickImage);
        });
        //Save the content of post




        saveButton.setOnClickListener(v -> {

            try {
                // Construct the JSON object
                JSONObject postData = new JSONObject();
//                postData.put("pid", "12345");
                //TODO: for testing only put real useridafterwards
                postData.put("userId", "1233");
                postData.put("time", getCurrentDateUsingCalendar());
//                postData.put("location", "Mountain View");

                JSONObject coordinate = new JSONObject();
                coordinate.put("latitude", latitude);
                coordinate.put("longitude", longitude);
                postData.put("coordinate", coordinate);

                JSONObject content = new JSONObject();
                content.put("title", titleEditText.getText().toString());
                content.put("body", mainTextEditText.getText().toString());
                postData.put("content", content);

                // Make the POST request
                new Thread(() -> {
                    try {
                        // Your backend API endpoint
                        URL url = new URL("http://4.204.251.146:8081/posts");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                            byte[] data = postData.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(data, 0, data.length);
                        }

                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            // Here you can handle the server's response if needed
                            runOnUiThread(() -> {
                                Toast.makeText(PostActivity.this, "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                            });
                        }

                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public String getCurrentDateUsingCalendar() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }




}

