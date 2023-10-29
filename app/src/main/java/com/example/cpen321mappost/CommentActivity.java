package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private Button buttonSubmitComment;
    private String pid;
    // TODO: You might want to use a custom Adapter for the comments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        //display all existing comments

        editTextComment = findViewById(R.id.editTextComment);
        //Here to input your comment
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);
        Intent receivedIntent = getIntent();
        pid = receivedIntent.getStringExtra("pid");

        buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editTextComment.getText().toString();
                if (!newComment.isEmpty()) {
                    // TODO: Add the new comment to your list and update the RecyclerView
                    editTextComment.setText(""); // Clear the EditText
                }
            }
        });

        // TODO: Load existing comments and display them in the RecyclerView


    }
}
