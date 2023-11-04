package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<Comment> commentList;
    private static final String TAG = "CommentAdapter";
    private static final ProfileManager profileManager = new ProfileManager();

    //ChatGPT usage: Partial
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewComment;

        public ViewHolder(View view) {
            super(view);
            textViewComment = view.findViewById(R.id.textViewComment);
        }
    }

    //ChatGPT usage: No
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    //ChatGPT usage: Partial
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(itemView);
    }

    //ChatGPT usage: Partial
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        profileManager.getAuthor(comment.getUid(), new ProfileManager.AuthorCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthorRetrieved(String authorName) {

                holder.textViewComment.setText(authorName + ": \n" + comment.getContent());

            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to get author in CommentAdapter", e);

            }
        });

    }

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return commentList.size();
    }

}
