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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<User> userList;
    private static final String TAG = "UserAdapter";
    private static final ProfileManager profileManager = new ProfileManager();
    private boolean isFollowing;
    private boolean isFollowed;
    private final Context context;

    //ChatGPT usage: Yes
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userInfo;
        public Button followButton;
        public CardView avatarCardView;
        public ImageView avatarImageView;
        public LinearLayout innerLinearLayout;

        public ViewHolder(View view) {
            super(view);

            userName = view.findViewById(R.id.user_list_user_name);
            userInfo = view.findViewById(R.id.user_list_user_post_count_and_followers);
            avatarCardView = view.findViewById(R.id.cardView_userList);
            avatarImageView = view.findViewById(R.id.imageView_userList);
            followButton = view.findViewById(R.id.user_list_user_follow_button);
            innerLinearLayout =view.findViewById(R.id.innerLinearLayout);

        }
    }

    //ChatGPT usage: Partial
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(itemView);
    }

    //ChatGPT usage: Partial
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        profileManager.getUserData(userList.get(position), new Activity(), new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                holder.userName.setText(user.getUserName());
                holder.userInfo.setText("Published: "+ user.getPostCount() + "      " + "Followers: "+ user.getFollowers().size());

                if (user.getUserAvatar() != null && !Objects.equals(user.getUserAvatar().getImage(), "")) {

                    byte[] decodedString = Base64.decode(user.getUserAvatar().getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.avatarImageView.setImageBitmap(decodedByte);

                }

                profileManager.getUserData(User.getInstance(), new Activity(), new User.UserCallback() {
                    @Override
                    public String onSuccess(User me) {

                        isFollowing = me.getFollowing().contains(user.getUserId());
                        isFollowed  = user.getFollowing().contains(me.getUserId());

                        if (isFollowing) {

                            if (isFollowed)holder.followButton.setText("mutual");

                            else holder.followButton.setText("following");

                        } else holder.followButton.setText("follow");

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

                return null;

            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        holder.innerLinearLayout.setOnClickListener((View.OnClickListener) view -> {

            onItemClicked(position);

        });

        holder.followButton.setOnClickListener(view -> {
            profileManager.followAuthor(isFollowing, userList.get(position).getUserId(), new Activity(), new ProfileManager.FollowingUserCallback() {
                @Override
                public void onSuccess(String userId) {
                    profileManager.getUserData(new User(userId), new Activity(), new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {
                            profileManager.getUserData(User.getInstance(), new Activity(), new User.UserCallback() {
                                @Override
                                public String onSuccess(User me) {

                                    isFollowing = me.getFollowing().contains(user.getUserId());
                                    isFollowed  = user.getFollowing().contains(me.getUserId());

                                    if (isFollowing) {

                                        if (isFollowed)holder.followButton.setText("mutual");

                                        else holder.followButton.setText("following");

                                    } else holder.followButton.setText("follow");

                                    return null;

                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });

                    profileManager.getAuthor(userId, new ProfileManager.AuthorCallback() {
                        @Override
                        public void onAuthorRetrieved(String authorName) {

                            if (!isFollowing) Toast.makeText(context, "Succeed following "+ authorName +" rn!", Toast.LENGTH_SHORT).show();
                            else             Toast.makeText(context, "Succeed to unfollow "+ authorName +" !", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(Exception e) {

                            Log.d(TAG, String.valueOf(e));

                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(context, "Unable to follow this user :(", Toast.LENGTH_LONG).show();
                    Log.d(TAG, String.valueOf(e));

                }
            });
        });
    }

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void onItemClicked(int position) {

        User clickedUser = userList.get(position);

        Intent PostPreviewListIntent = new Intent(context, PostPreviewListActivity.class);
        PostPreviewListIntent.putExtra("mode", "authorInfo");
        PostPreviewListIntent.putExtra("userId", clickedUser.getUserId());
        context.startActivity(PostPreviewListIntent);

    }


}
