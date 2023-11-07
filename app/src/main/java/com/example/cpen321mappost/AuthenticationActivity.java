package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;


public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationHandler authenticationHandler;
    final static String TAG = "Authentication Activity";

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationHandler = new AuthenticationHandler(this, new AuthenticationHandler.AuthCallback() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                updateUI();
            }

            @Override
            public void onAuthFailure(Exception exception) {
                // TODO: Handle authentication error here
                Log.d("AuthenticationActivity", "Authentication failed ");

            }
        });

        if (authenticationHandler.isUserAuthenticated()) {
            updateUI();
            return;
        }

        setContentView(R.layout.activity_authentication);



        findViewById(R.id.btnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticationHandler.signInWithGoogle();
            }
        });
    }

    //ChatGPT usage: No
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authenticationHandler.handleSignInResult(requestCode, resultCode, data);
    }

    //ChatGPT usage: Partial
    private void updateUI() {
        // TODO: Update UI
        // After successful authentication, you can navigate to the next page
        Log.d("AuthenticationActivity", "updateUI called");
        //Need to call initialize user:
        //TODO: generate a new user if not generated: if(current user==null) User currentUser= new User();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //TODO:Ask for firebase cloud mesasging token, ask for permission
        if (firebaseUser != null) {
            // User is already authenticated

            //User currentUser = User.initializeUser(firebaseUser);
            // TODO:
            //if current user by email exist in database, fetch the user
            //else create a post method to create user

        }

        User user = User.getInstance();
        ProfileManager profileManager = new ProfileManager();
        profileManager.getUserData(user, this, new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                Log.d(TAG, "OLD TOKEN: " + user.getToken());

                user.updateToken(new User.TokenCallback() {
                    @Override
                    public void onTokenReceived(String token) {

                        Log.d(TAG, "NEW TOKEN: " + user.getToken());

                        user.setToken(token);

                        Log.d(TAG, "NEW TOKEN: " + user.getToken());

                    }

                    @Override
                    public void onError(Exception e) {

                        Log.d(TAG, "TOKEN NOT RETRIEVED");

                    }
                });

                profileManager.putUserData(user, AuthenticationActivity.this, new User.UserCallback() {
                    @Override
                    public String onSuccess(User user) {

                        Log.d(TAG, "User data is sent to the database, target is updating token.");

                        Gson gson = new Gson();
                        String jsonUserData = gson.toJson(user);

                        Log.d(TAG, "user data after put is: " + jsonUserData);

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.d(TAG, "Falied to update token.");

                        finish();

                    }
                });

                return null;

            }

            @Override
            public void onFailure(Exception e) {

                Log.d(TAG, "Falied to get user data.");

            }

        });

        Intent intent = new Intent(AuthenticationActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();  // Finish the current activity

    }

}
