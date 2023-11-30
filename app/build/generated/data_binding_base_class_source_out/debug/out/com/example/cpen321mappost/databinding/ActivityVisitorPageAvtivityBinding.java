// Generated by view binder compiler. Do not edit!
package com.example.cpen321mappost.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public final class ActivityVisitorPageAvtivityBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final LinearLayout achievementBoard;

  @NonNull
  public final CardView avatarProfileCardView;

  @NonNull
  public final ImageView avatarVisitPage;

  @NonNull
  public final ImageView explorer;

  @NonNull
  public final View lastDivider;

  @NonNull
  public final ImageView master;

  @NonNull
  public final ImageView novice;

  @NonNull
  public final RecyclerView postsRecyclerView;

  @NonNull
  public final TextView userBirthdateId;

  @NonNull
  public final Button userFollowerCountId;

  @NonNull
  public final TextView userFollowerCountTextId;

  @NonNull
  public final Button userFollowingCountId;

  @NonNull
  public final TextView userFollowingCountTextId;

  @NonNull
  public final TextView userGenderId;

  @NonNull
  public final TextView userNameId;

  @NonNull
  public final Button userPostCountId;

  @NonNull
  public final TextView userPostCountTextId;

  @NonNull
  public final Button visitPageFollowButtonId;

  private ActivityVisitorPageAvtivityBinding(@NonNull ConstraintLayout rootView,
      @NonNull LinearLayout achievementBoard, @NonNull CardView avatarProfileCardView,
      @NonNull ImageView avatarVisitPage, @NonNull ImageView explorer, @NonNull View lastDivider,
      @NonNull ImageView master, @NonNull ImageView novice, @NonNull RecyclerView postsRecyclerView,
      @NonNull TextView userBirthdateId, @NonNull Button userFollowerCountId,
      @NonNull TextView userFollowerCountTextId, @NonNull Button userFollowingCountId,
      @NonNull TextView userFollowingCountTextId, @NonNull TextView userGenderId,
      @NonNull TextView userNameId, @NonNull Button userPostCountId,
      @NonNull TextView userPostCountTextId, @NonNull Button visitPageFollowButtonId) {
    this.rootView = rootView;
    this.achievementBoard = achievementBoard;
    this.avatarProfileCardView = avatarProfileCardView;
    this.avatarVisitPage = avatarVisitPage;
    this.explorer = explorer;
    this.lastDivider = lastDivider;
    this.master = master;
    this.novice = novice;
    this.postsRecyclerView = postsRecyclerView;
    this.userBirthdateId = userBirthdateId;
    this.userFollowerCountId = userFollowerCountId;
    this.userFollowerCountTextId = userFollowerCountTextId;
    this.userFollowingCountId = userFollowingCountId;
    this.userFollowingCountTextId = userFollowingCountTextId;
    this.userGenderId = userGenderId;
    this.userNameId = userNameId;
    this.userPostCountId = userPostCountId;
    this.userPostCountTextId = userPostCountTextId;
    this.visitPageFollowButtonId = visitPageFollowButtonId;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityVisitorPageAvtivityBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityVisitorPageAvtivityBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_visitor_page_avtivity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityVisitorPageAvtivityBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.achievement_board;
      LinearLayout achievementBoard = ViewBindings.findChildViewById(rootView, id);
      if (achievementBoard == null) {
        break missingId;
      }

      id = R.id.avatar_profile_cardView;
      CardView avatarProfileCardView = ViewBindings.findChildViewById(rootView, id);
      if (avatarProfileCardView == null) {
        break missingId;
      }

      id = R.id.avatar_visit_page;
      ImageView avatarVisitPage = ViewBindings.findChildViewById(rootView, id);
      if (avatarVisitPage == null) {
        break missingId;
      }

      id = R.id.explorer;
      ImageView explorer = ViewBindings.findChildViewById(rootView, id);
      if (explorer == null) {
        break missingId;
      }

      id = R.id.last_divider;
      View lastDivider = ViewBindings.findChildViewById(rootView, id);
      if (lastDivider == null) {
        break missingId;
      }

      id = R.id.master;
      ImageView master = ViewBindings.findChildViewById(rootView, id);
      if (master == null) {
        break missingId;
      }

      id = R.id.novice;
      ImageView novice = ViewBindings.findChildViewById(rootView, id);
      if (novice == null) {
        break missingId;
      }

      id = R.id.postsRecyclerView;
      RecyclerView postsRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (postsRecyclerView == null) {
        break missingId;
      }

      id = R.id.user_birthdate_id;
      TextView userBirthdateId = ViewBindings.findChildViewById(rootView, id);
      if (userBirthdateId == null) {
        break missingId;
      }

      id = R.id.user_follower_count_id;
      Button userFollowerCountId = ViewBindings.findChildViewById(rootView, id);
      if (userFollowerCountId == null) {
        break missingId;
      }

      id = R.id.user_follower_count_text_id;
      TextView userFollowerCountTextId = ViewBindings.findChildViewById(rootView, id);
      if (userFollowerCountTextId == null) {
        break missingId;
      }

      id = R.id.user_following_count_id;
      Button userFollowingCountId = ViewBindings.findChildViewById(rootView, id);
      if (userFollowingCountId == null) {
        break missingId;
      }

      id = R.id.user_following_count_text_id;
      TextView userFollowingCountTextId = ViewBindings.findChildViewById(rootView, id);
      if (userFollowingCountTextId == null) {
        break missingId;
      }

      id = R.id.user_gender_id;
      TextView userGenderId = ViewBindings.findChildViewById(rootView, id);
      if (userGenderId == null) {
        break missingId;
      }

      id = R.id.user_name_id;
      TextView userNameId = ViewBindings.findChildViewById(rootView, id);
      if (userNameId == null) {
        break missingId;
      }

      id = R.id.user_post_count_id;
      Button userPostCountId = ViewBindings.findChildViewById(rootView, id);
      if (userPostCountId == null) {
        break missingId;
      }

      id = R.id.user_post_count_text_id;
      TextView userPostCountTextId = ViewBindings.findChildViewById(rootView, id);
      if (userPostCountTextId == null) {
        break missingId;
      }

      id = R.id.visit_page_follow_button_id;
      Button visitPageFollowButtonId = ViewBindings.findChildViewById(rootView, id);
      if (visitPageFollowButtonId == null) {
        break missingId;
      }

      return new ActivityVisitorPageAvtivityBinding((ConstraintLayout) rootView, achievementBoard,
          avatarProfileCardView, avatarVisitPage, explorer, lastDivider, master, novice,
          postsRecyclerView, userBirthdateId, userFollowerCountId, userFollowerCountTextId,
          userFollowingCountId, userFollowingCountTextId, userGenderId, userNameId, userPostCountId,
          userPostCountTextId, visitPageFollowButtonId);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}