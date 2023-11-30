package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final List<Post> postList;
    private static final String TAG = "PostAdapter";
    private static final ProfileManager profileManager = new ProfileManager();


    //ChatGPT usage: Yes
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAuthor;
        public TextView textViewTitle;
        public TextView textViewContent;
        public TextView textViewLikes;
        public CardView cardViewPost;
        public ImageView imageViewPost;

        public ViewHolder(View view, List<Post> postList) {
            super(view);
            textViewAuthor = view.findViewById(R.id.textViewAuthor);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewContent = view.findViewById(R.id.textViewContent);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            cardViewPost = view.findViewById(R.id.cardViewPost);
            imageViewPost = view.findViewById(R.id.imageViewPost);

            cardViewPost.setVisibility(View.GONE);

            itemView.setOnClickListener(view1 -> {

                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    Intent PostDetailIntent = new Intent(view1.getContext(), PostDetailActivity.class);

                    Post clickedPost = postList.get(position);
                    PostDetailIntent.putExtra("pid", clickedPost.getPid()); // for example

                    view1.getContext().startActivity(PostDetailIntent);

                }
            });
        }
    }

    //ChatGPT usage: Partial
    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView, postList);
    }

    //ChatGPT usage: Partial
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
//        if()
        profileManager.getAuthor(post.getUserId(), new ProfileManager.AuthorCallback() {
            @Override
            public void onAuthorRetrieved(String authorName) {
                holder.textViewAuthor.setText(authorName);
            }

            @Override
            public void onError(Exception e) {
                // Handle the error (e.g., logging, display a default name, etc.)
                Log.e(TAG, "Failed to get author in PostAdapter", e);

            }
        });

        if (post.getImageData() != null && !Objects.equals(post.getImageData().getImage(), "")) {

            byte[] decodedString = Base64.decode(post.getImageData().getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.cardViewPost.setVisibility(View.VISIBLE);
            holder.imageViewPost.setImageBitmap(decodedByte);
            holder.textViewContent.setMaxLines(3);

        } else {

            holder.textViewContent.setMaxLines(2);
            holder.cardViewPost.setVisibility(View.GONE);

        }

        holder.textViewTitle.setText(post.getContent().getTitle());
        holder.textViewContent.setText(post.getContent().getBody());
        holder.textViewLikes.setText("❤️ " + post.getLikeCount());

    }

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return postList.size();
    }
}
