package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {
    private EditText searchText;
    private Button searchButton;

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newSearchStr=searchText.getText().toString();

                Intent searchIntent= new Intent(SearchActivity.this,PostPreviewListActivity.class);
                searchIntent.putExtra("mode", "search");
                searchIntent.putExtra("searchString", newSearchStr);
                startActivity(searchIntent);
            }
        });

    }


}