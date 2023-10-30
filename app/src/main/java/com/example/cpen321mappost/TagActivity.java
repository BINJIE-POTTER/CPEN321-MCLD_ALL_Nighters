package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class TagActivity extends AppCompatActivity {
    private static final PostManager postManager = new PostManager();
    private static final String TAG = "TagActivity";

    private ArrayList<String> tagsList;



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
                tagsList=tags;


            }
            @Override
            public void onFailure(Exception e) {

                Toast.makeText(TagActivity.this, "Failed to fetch this post!", Toast.LENGTH_LONG).show();

            }
        });


        //sara's work
        ArrayList<String> test = new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3")); // Example tags
        Intent tagIntent = new Intent(TagActivity.this, PostPreviewListActivity.class);
        tagIntent.putExtra("userCurrentLat", latitude);
        tagIntent.putExtra("userCurrentLon", longitude);

//        tagIntent.putStringArrayListExtra("tagsList", tagsList);
        tagIntent.putStringArrayListExtra("tagsList", test);


        startActivity(tagIntent);



        //end sara's work



    }

//         Intent tagIntent= new Intent(MapsActivity.this,PostPreviewListActivity.class);
//                    tagIntent.putExtra("mode", "tag");
//                    startActivity(tagIntent);





}