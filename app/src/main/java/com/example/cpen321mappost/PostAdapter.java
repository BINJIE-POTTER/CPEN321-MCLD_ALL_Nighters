package com.example.cpen321mappost;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;

    private static final String TAG = "PostAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAuthor, textViewTitle, textViewContent, textViewLikes;

        public ViewHolder(View view) {
            super(view);
            textViewAuthor = view.findViewById(R.id.textViewAuthor);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewContent = view.findViewById(R.id.textViewContent);
            textViewLikes = view.findViewById(R.id.textViewLikes);
        }
    }

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postList.get(position);
        post.getAuthor(post.getUserId(), new Post.AuthorCallback() {
            @Override
            public void onAuthorRetrieved(String authorName) {
                holder.textViewAuthor.setText(authorName);
            }

            @Override
            public void onError(Exception e) {
                // Handle the error (e.g., logging, display a default name, etc.)
            }
        });

        holder.textViewTitle.setText(post.getContent().getTitle());
        holder.textViewContent.setText(post.getContent().getBody());
        holder.textViewLikes.setText(String.valueOf(post.getLikeCount()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
