package com.example.cpen321mappost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.util.Calendar;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText titleEditText, mainTextEditText;
    private Button uploadImageButton;
    private Button saveButton;
    final static String TAG = "PostActivity";

    public interface JsonPostCallback {
        void onSuccess(JSONObject postedData);

        void onFailure(Exception e);
    }


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
        double latitude = Double.parseDouble(receivedIntent.getStringExtra("latitude"));
        double longitude = Double.parseDouble(receivedIntent.getStringExtra("longitude"));


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
                //TODO: for testing only put real useridafterwards
                postData.put("userId", User.getInstance().getUserId());

                postData.put("time", getCurrentDateUsingCalendar());

                JSONObject coordinate = new JSONObject();
                coordinate.put("latitude", latitude);
                coordinate.put("longitude", longitude);
                postData.put("coordinate", coordinate);

                JSONObject content = new JSONObject();
                content.put("title", titleEditText.getText().toString());
                content.put("body", mainTextEditText.getText().toString());
                postData.put("content", content);
//                postJsonData( postData, PostActivity.this);
                postJsonData(postData, PostActivity.this, new JsonPostCallback() {
                    @Override
                    public void onSuccess(JSONObject postedData) {
                        // Handle success here
                        Intent intent=new Intent(PostActivity.this, MapsActivity.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    public void postJsonData(JSONObject postData, final Activity activity, final JsonPostCallback callback){

        String url = "http://4.204.251.146:8081/posts";

        OkHttpClient httpClient = HttpClient.getInstance();

        // Convert the JSONObject to a string representation
        String jsonStrData = postData.toString();

        Log.d(TAG, "This is the posted data: " + jsonStrData);

        // Create a request body with the string representation of the JSONObject
        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Failed to post data", e);
                        callback.onFailure(e); // Notify callback about the failure
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                if (response.code() == 200) {
                                    // The operation was successful.
                                    Log.d(TAG, "Data posted successfully!");
                                    callback.onSuccess(postData); // Notify callback about the success

                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
                                    Log.e(TAG, "Error posting data", exception);
                                    callback.onFailure(exception); // Notify callback about the failure
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            IOException exception = new IOException("Unexpected code " + response);
                            Log.e(TAG, "Error posting data", exception);
                            callback.onFailure(exception); // Notify callback about the failure
                        }
                    }
                });
            }
        });
    }


    public String getCurrentDateUsingCalendar () {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }

}