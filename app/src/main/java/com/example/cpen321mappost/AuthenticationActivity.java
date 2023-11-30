package com.example.cpen321mappost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationHandler authenticationHandler;
    final static String TAG = "Authentication Activity";
    public static boolean TEST_MODE = false;


    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (TEST_MODE) {
//            // Bypass authentication and directly start MapsActivity
//            Intent intent = new Intent(AuthenticationActivity.this, MapsActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
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

            User.setInstance(null);
            User.setLoggedIn(true);
            User.getInstance();

        }

        Intent intent = new Intent(AuthenticationActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();

    }

}
