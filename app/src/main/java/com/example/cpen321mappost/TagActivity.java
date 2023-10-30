package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class TagActivity extends AppCompatActivity {
    private static final PostManager postManager = new PostManager();
    private static final String TAG = "TagActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        Intent receivedIntent = getIntent();
        String latitude = receivedIntent.getStringExtra("latitude");
        String longitude = receivedIntent.getStringExtra("longitude");

        postManager.getTagsData(latitude,longitude, this, new PostManager.JsonCallback<ArrayList<String>>() {
            @Override
            public void onSuccess( ArrayList<String> tags) {
                Log.e(TAG, "Get tags successfully");


            }
            @Override
            public void onFailure(Exception e) {

                Toast.makeText(TagActivity.this, "Failed to fetch this post!", Toast.LENGTH_LONG).show();

            }
        });



    }

//         Intent tagIntent= new Intent(MapsActivity.this,PostPreviewListActivity.class);
//                    tagIntent.putExtra("mode", "tag");
//                    startActivity(tagIntent);





}