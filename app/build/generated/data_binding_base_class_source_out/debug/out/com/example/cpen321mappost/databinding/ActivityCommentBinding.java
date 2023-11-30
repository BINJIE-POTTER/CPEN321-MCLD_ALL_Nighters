// Generated by view binder compiler. Do not edit!
package com.example.cpen321mappost.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.cpen321mappost.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCommentBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonSubmitComment;

  @NonNull
  public final EditText editTextComment;

  @NonNull
  public final RecyclerView recyclerViewComments;

  private ActivityCommentBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button buttonSubmitComment, @NonNull EditText editTextComment,
      @NonNull RecyclerView recyclerViewComments) {
    this.rootView = rootView;
    this.buttonSubmitComment = buttonSubmitComment;
    this.editTextComment = editTextComment;
    this.recyclerViewComments = recyclerViewComments;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCommentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCommentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_comment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCommentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonSubmitComment;
      Button buttonSubmitComment = ViewBindings.findChildViewById(rootView, id);
      if (buttonSubmitComment == null) {
        break missingId;
      }

      id = R.id.editTextComment;
      EditText editTextComment = ViewBindings.findChildViewById(rootView, id);
      if (editTextComment == null) {
        break missingId;
      }

      id = R.id.recyclerViewComments;
      RecyclerView recyclerViewComments = ViewBindings.findChildViewById(rootView, id);
      if (recyclerViewComments == null) {
        break missingId;
      }

      return new ActivityCommentBinding((ConstraintLayout) rootView, buttonSubmitComment,
          editTextComment, recyclerViewComments);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
