package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;

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
    // TODO: You might want to use a custom Adapter for the comments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);

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
