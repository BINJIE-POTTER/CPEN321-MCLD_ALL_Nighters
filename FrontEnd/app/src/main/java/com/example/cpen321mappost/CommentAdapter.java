package com.example.cpen321mappost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<Comment> commentList;
    private final Context context;
    private static final String TAG = "CommentAdapter";
    private static final ProfileManager profileManager = new ProfileManager();

    //ChatGPT usage: Partial
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCommentAuthor;
        public TextView textViewCommentContent;
        public TextView textViewCommentTime;
        public ImageView avatarImageView;

        public ViewHolder(View view) {
            super(view);
            textViewCommentAuthor = view.findViewById(R.id.textViewCommentAuthor);
            textViewCommentContent = view.findViewById(R.id.textViewCommentContent);
            textViewCommentTime = view.findViewById(R.id.textViewCommentTime);
            avatarImageView = view.findViewById(R.id.avatar_comment);
        }
    }

    //ChatGPT usage: No
    public CommentAdapter(Context context, List<Comment> commentList) {

        this.context = context;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        commentList.sort((c1, c2) -> {
            LocalDateTime dt1 = LocalDateTime.parse(c1.getTime(), formatter);
            LocalDateTime dt2 = LocalDateTime.parse(c2.getTime(), formatter);
            return dt2.compareTo(dt1);
        });

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

        profileManager.getUserData(new User(comment.getUid()), new Activity(), new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                holder.textViewCommentAuthor.setText(user.getUserName());
                holder.textViewCommentContent.setText(comment.getContent());
                setCommentTime(comment.getTime(), holder.textViewCommentTime);

                if (user.getUserAvatar() != null && !Objects.equals(user.getUserAvatar().getImage(), "")) {

                    byte[] decodedString = Base64.decode(user.getUserAvatar().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.avatarImageView.setImageBitmap(decodedByte);

                }

                return null;

            }

            @Override
            public void onFailure(Exception e) {

                Log.e(TAG, "Failed to get author in CommentAdapter", e);

            }
        });

        holder.avatarImageView.setOnClickListener(v -> onCommentClicked(position));

    }

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private void onCommentClicked(int position) {

        Comment comment = commentList.get(position);

        Intent intent;

        if (Objects.equals(comment.getUid(), User.getInstance().getUserId())) intent = new Intent(context, ProfileActivity.class);

        else {

            intent = new Intent(context, VisitorPageAvtivity.class);
            intent.putExtra("userId", comment.getUid());

        }

        context.startActivity(intent);

    }

    private void setCommentTime(String commentTime, TextView textViewCommentTime) {

        String convertedDate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = LocalDateTime.parse(commentTime, formatter);

        Period period = Period.between(now.toLocalDate(), time.toLocalDate());

        if (Math.abs(period.getYears()) >= 1) {

            convertedDate = time.format(DateTimeFormatter.ofPattern("MMM. dd - yyyy"));

            textViewCommentTime.setText(convertedDate);

        } else if (Math.abs(period.getDays()) >= 1) {

            convertedDate = time.format(DateTimeFormatter.ofPattern("MMM. dd"));

            textViewCommentTime.setText(convertedDate);

        } else {

            Duration duration = Duration.between(now, time);

            if (Math.abs(duration.toMinutes()) > 60) {

                Log.d(TAG, "duration: " + duration.toMinutes() + "");

                convertedDate = time.format(DateTimeFormatter.ofPattern("HH:mm"));

            } else {

                convertedDate = Math.abs(duration.toMinutes()) + " minutes ago";

            }

            textViewCommentTime.setText(convertedDate);

        }
    }
}
