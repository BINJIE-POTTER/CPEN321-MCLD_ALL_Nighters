package com.example.cpen321mappost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class UserListActivity extends AppCompatActivity {
    private final static String TAG = "UserList Activity";
    private final static ProfileManager profileManager = new ProfileManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        TextView title = findViewById(R.id.user_list_title);

        List<User> users = new ArrayList<>();
        UserAdapter adapter = new UserAdapter(this, users);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        String userId = intent.getStringExtra("userId");

        profileManager.getUserData(userId == null ? User.getInstance() : new User(userId), this, new User.UserCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public String onSuccess(User user) {

                String userName = user.getUserName();
                title.setText(Objects.equals(mode, "followings") ? userName + "'s followings" : userName + "'s followers");

                users.clear();

                List<String> userIds = Objects.equals(mode, "followings") ? user.getFollowing() : user.getFollowers();
                AtomicInteger completedRequests = new AtomicInteger(0);

                for (String userId : userIds) {

                    User user1 = new User(userId);
                    profileManager.getUserData(user1, UserListActivity.this, new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            users.add(user);

                            if (completedRequests.incrementAndGet() == userIds.size()) {

                                runOnUiThread(adapter::notifyDataSetChanged);

                            }

                            return null;

                        }

                        @Override
                        public void onFailure(Exception e) {

                            Log.e(TAG, e.toString());

                        }
                    });

                }

                return null;

            }

            @Override
            public void onFailure(Exception e) {

                Log.e(TAG, e.toString());

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//
//        Intent ProfileIntent = new Intent(this, ProfileActivity.class);
//        startActivity(ProfileIntent);
//
//        finish();
//
//    }

}