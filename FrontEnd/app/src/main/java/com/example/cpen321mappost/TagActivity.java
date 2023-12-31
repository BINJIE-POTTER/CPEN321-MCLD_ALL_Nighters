package com.example.cpen321mappost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TagActivity extends AppCompatActivity {
    private static final PostManager postManager = new PostManager();
    private static final String TAG = "TagActivity";
    private static final Set<String> tagsSet = new HashSet<>();
    private RecyclerView recyclerViewTags;

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        Button buttonSaveTags;

        Intent receivedIntent = getIntent();
        String latitude = receivedIntent.getStringExtra("latitude");
        String longitude = receivedIntent.getStringExtra("longitude");

        recyclerViewTags = findViewById(R.id.RecyclerViewTags);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        buttonSaveTags = findViewById(R.id.saveButton);

        postManager.getTagsData(latitude, longitude, this, new PostManager.JsonCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> tags) {

                Log.d(TAG, "Tags are obtained successfully: ");
                Log.d(TAG, String.join(", ", tags));

                TagAdapter tagAdapter = new TagAdapter(tags, tagsSet);
                recyclerViewTags.setAdapter(tagAdapter);

            }
            @Override
            public void onFailure(Exception e) {

                Toast.makeText(TagActivity.this, "Failed to fetch tags!", Toast.LENGTH_LONG).show();

            }

        });

        buttonSaveTags.setOnClickListener(view -> {

            Intent tagIntent = new Intent(TagActivity.this, PostPreviewListActivity.class);
            tagIntent.putExtra("mode", "tag");
            tagIntent.putExtra("userCurrentLat", latitude);
            tagIntent.putExtra("userCurrentLon", longitude);
            tagIntent.putStringArrayListExtra("tagsList", new ArrayList<>(tagsSet));
            startActivity(tagIntent);

        });

    }

}