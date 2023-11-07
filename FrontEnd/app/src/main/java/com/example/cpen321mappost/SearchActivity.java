package com.example.cpen321mappost;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText searchText;
        Button searchButton;
        searchText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(view -> {
            String newSearchStr=searchText.getText().toString();

            Intent searchIntent= new Intent(SearchActivity.this, PostPreviewListActivity.class);
            searchIntent.putExtra("mode", "search");
            searchIntent.putExtra("searchString", newSearchStr);
            startActivity(searchIntent);

        });

    }

}