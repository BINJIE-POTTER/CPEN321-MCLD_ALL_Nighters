package com.example.cpen321mappost;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileManager {
    final static String TAG = "ProfileManager Activity";
    private final OkHttpClient httpClient = new OkHttpClient();

    public void getUserData(String userId, final User.UserCallback callback) {
        // Build the request URL. Modify this with your actual API URL.
        String url = "http://4.204.251.146:8081/users/?userId=" + userId;

        // Create a request object.
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Enqueue the request in the background.
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle the error. Be aware that this is called on a background thread.
                Log.d(TAG, "Failed to request user data");
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();

                    assert response.body() != null;
                    User user = gson.fromJson(response.body().charStream(), User.class);

                    callback.onSuccess(user);
                } else {
                    // Handle the error. Be aware that this is called on a background thread.
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    //-------------------------------------------- E N D --------------------------------------------//

}