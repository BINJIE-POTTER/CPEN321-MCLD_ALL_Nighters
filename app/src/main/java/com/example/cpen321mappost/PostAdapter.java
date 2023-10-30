package com.example.cpen321mappost;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static List<Post> postList;
    private static final String TAG = "PostAdapter";
    private static final ProfileManager profileManager = new ProfileManager();

    //ChatGPT usage: Yes
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAuthor, textViewTitle, textViewContent, textViewLikes;

        public ViewHolder(View view) {
            super(view);
            textViewAuthor = view.findViewById(R.id.textViewAuthor);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewContent = view.findViewById(R.id.textViewContent);
            textViewLikes = view.findViewById(R.id.textViewLikes);

            // Set an onClickListener on the entire row view
            itemView.setOnClickListener(view1 -> {
                int position = getAdapterPosition(); // Get the position of the view holder

                // Ensure the position is valid (exists in the dataset)
                if (position != RecyclerView.NO_POSITION) {
                    // You can start your new activity here and pass it the necessary extras.
                    Intent PostDetailIntent = new Intent(view1.getContext(), PostDetailActivity.class);

                    // Assuming 'post' has a method 'getId' to get a unique identifier for the post
                    Post clickedPost = postList.get(position);
                    PostDetailIntent.putExtra("pid", clickedPost.getPid()); // for example

                    // Other data to pass to activity...

                    view1.getContext().startActivity(PostDetailIntent);
                }
            });
        }
    }

    //ChatGPT usage: Partial
    public PostAdapter(List<Post> postList) {
        PostAdapter.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    //ChatGPT usage: Partial
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postList.get(position);
        profileManager.getAuthor(post.getUserId(), new ProfileManager.AuthorCallback() {
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

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return postList.size();
    }
}
