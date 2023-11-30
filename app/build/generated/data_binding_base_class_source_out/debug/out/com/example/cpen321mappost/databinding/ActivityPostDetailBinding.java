// Generated by view binder compiler. Do not edit!
package com.example.cpen321mappost.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.cpen321mappost.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityPostDetailBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView avatarPostDetail;

  @NonNull
  public final Button buttonComment;

  @NonNull
  public final Button buttonDelete;

  @NonNull
  public final Button buttonLike;

  @NonNull
  public final CardView cardViewPost;

  @NonNull
  public final CardView cardViewPostDetail;

  @NonNull
  public final EditText editTextComment;

  @NonNull
  public final ImageView imageViewPost;

  @NonNull
  public final Button postDetailFollowButtonId;

  @NonNull
  public final RecyclerView recyclerViewComments;

  @NonNull
  public final TextView textViewMainContent;

  @NonNull
  public final TextView textViewPostDetail;

  @NonNull
  public final TextView textViewPostTime;

  @NonNull
  public final TextView textViewTitle;

  private ActivityPostDetailBinding(@NonNull ConstraintLayout rootView,
      @NonNull ImageView avatarPostDetail, @NonNull Button buttonComment,
      @NonNull Button buttonDelete, @NonNull Button buttonLike, @NonNull CardView cardViewPost,
      @NonNull CardView cardViewPostDetail, @NonNull EditText editTextComment,
      @NonNull ImageView imageViewPost, @NonNull Button postDetailFollowButtonId,
      @NonNull RecyclerView recyclerViewComments, @NonNull TextView textViewMainContent,
      @NonNull TextView textViewPostDetail, @NonNull TextView textViewPostTime,
      @NonNull TextView textViewTitle) {
    this.rootView = rootView;
    this.avatarPostDetail = avatarPostDetail;
    this.buttonComment = buttonComment;
    this.buttonDelete = buttonDelete;
    this.buttonLike = buttonLike;
    this.cardViewPost = cardViewPost;
    this.cardViewPostDetail = cardViewPostDetail;
    this.editTextComment = editTextComment;
    this.imageViewPost = imageViewPost;
    this.postDetailFollowButtonId = postDetailFollowButtonId;
    this.recyclerViewComments = recyclerViewComments;
    this.textViewMainContent = textViewMainContent;
    this.textViewPostDetail = textViewPostDetail;
    this.textViewPostTime = textViewPostTime;
    this.textViewTitle = textViewTitle;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPostDetailBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPostDetailBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_post_detail, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPostDetailBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.avatar_post_detail;
      ImageView avatarPostDetail = ViewBindings.findChildViewById(rootView, id);
      if (avatarPostDetail == null) {
        break missingId;
      }

      id = R.id.buttonComment;
      Button buttonComment = ViewBindings.findChildViewById(rootView, id);
      if (buttonComment == null) {
        break missingId;
      }

      id = R.id.buttonDelete;
      Button buttonDelete = ViewBindings.findChildViewById(rootView, id);
      if (buttonDelete == null) {
        break missingId;
      }

      id = R.id.buttonLike;
      Button buttonLike = ViewBindings.findChildViewById(rootView, id);
      if (buttonLike == null) {
        break missingId;
      }

      id = R.id.cardViewPost;
      CardView cardViewPost = ViewBindings.findChildViewById(rootView, id);
      if (cardViewPost == null) {
        break missingId;
      }

      id = R.id.cardView_post_detail;
      CardView cardViewPostDetail = ViewBindings.findChildViewById(rootView, id);
      if (cardViewPostDetail == null) {
        break missingId;
      }

      id = R.id.editTextComment;
      EditText editTextComment = ViewBindings.findChildViewById(rootView, id);
      if (editTextComment == null) {
        break missingId;
      }

      id = R.id.imageViewPost;
      ImageView imageViewPost = ViewBindings.findChildViewById(rootView, id);
      if (imageViewPost == null) {
        break missingId;
      }

      id = R.id.post_detail_follow_button_id;
      Button postDetailFollowButtonId = ViewBindings.findChildViewById(rootView, id);
      if (postDetailFollowButtonId == null) {
        break missingId;
      }

      id = R.id.recyclerViewComments;
      RecyclerView recyclerViewComments = ViewBindings.findChildViewById(rootView, id);
      if (recyclerViewComments == null) {
        break missingId;
      }

      id = R.id.textViewMainContent;
      TextView textViewMainContent = ViewBindings.findChildViewById(rootView, id);
      if (textViewMainContent == null) {
        break missingId;
      }

      id = R.id.textViewPostDetail;
      TextView textViewPostDetail = ViewBindings.findChildViewById(rootView, id);
      if (textViewPostDetail == null) {
        break missingId;
      }

      id = R.id.textViewPostTime;
      TextView textViewPostTime = ViewBindings.findChildViewById(rootView, id);
      if (textViewPostTime == null) {
        break missingId;
      }

      id = R.id.textViewTitle;
      TextView textViewTitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewTitle == null) {
        break missingId;
      }

      return new ActivityPostDetailBinding((ConstraintLayout) rootView, avatarPostDetail,
          buttonComment, buttonDelete, buttonLike, cardViewPost, cardViewPostDetail,
          editTextComment, imageViewPost, postDetailFollowButtonId, recyclerViewComments,
          textViewMainContent, textViewPostDetail, textViewPostTime, textViewTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}