package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO:for testing only, change this
        Intent authenticationIntent =new Intent(MainActivity.this,AuthenticationActivity.class);
        startActivity(authenticationIntent);

    }
}