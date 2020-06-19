package com.example.isdmessenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.isdmessenger.R;
import com.example.isdmessenger.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findFriendsRv;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        init();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");


    }



    private void init() {
        findFriendsRv = findViewById(R.id.findFriendsRvId);
        mToolbar = findViewById(R.id.find_friend_toolbar);
        userRef = FirebaseDatabase.getInstance().getReference().child("Social App Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(userRef,User.class)
                .build();
        FirebaseRecyclerAdapter<User,FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull User user) {
                        holder.userName.setText(user.getUserName());
                        holder.userStatus.setText(user.getUserStatus());
                        Picasso.with(FindFriendsActivity.this).load(user.getUserImg()).into(holder.profileImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visitUserId = getRef(position).getKey();
                                Intent intent = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                                intent.putExtra("visitUserId",visitUserId);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_display_layout,parent,false);

                        return new FindFriendViewHolder(view);
                    }
                };
        findFriendsRv.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        private TextView userName,userStatus;
        private CircleImageView profileImageView;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUsersNameTvId);
            userStatus = itemView.findViewById(R.id.displayUsersStatusTvId);
            profileImageView = itemView.findViewById(R.id.usersProfileImageId);
        }
    }
}
