package com.example.cpen321mappost;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationHandler {

    private static final int RC_SIGN_IN = 9001;

    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final GoogleSignInClient mGoogleSignInClient;

    public interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthFailure(Exception exception);
    }

    private AuthCallback callback;

    public AuthenticationHandler(Activity activity, AuthCallback callback) {
        this.activity = activity;
        this.callback = callback;

        mAuth = FirebaseAuth.getInstance();

        //TODO: Add FacebookSignInOptions  here      FacebookSignInOptions fso = ...

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }
    //TODO: Add signInWithFacebook here

    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                callback.onAuthFailure(e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        callback.onAuthSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onAuthFailure(task.getException());
                    }
                });
    }
    public boolean isUserAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }


}