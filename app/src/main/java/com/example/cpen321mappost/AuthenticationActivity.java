package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseUser;


public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationHandler authenticationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (authenticationHandler.isUserAuthenticated()) {
//            updateUI();
//            return;
//        }
        setContentView(R.layout.activity_authentication);

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

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticationHandler.signInWithGoogle();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authenticationHandler.handleSignInResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        // TODO: Update UI
        // After successful authentication, you can navigate to the next page
        Log.d("AuthenticationActivity", "updateUI called");
        Intent intent = new Intent(AuthenticationActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();  // Finish the current activity

    }
}
