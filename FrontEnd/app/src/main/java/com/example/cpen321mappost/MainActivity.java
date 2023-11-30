package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent authenticationIntent =new Intent(MainActivity.this,AuthenticationActivity.class);
        startActivity(authenticationIntent);

        finish();

    }
}