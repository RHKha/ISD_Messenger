package com.example.isdmessenger.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.isdmessenger.R;
import com.example.isdmessenger.activity.ChatActivity;
import com.example.isdmessenger.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private String currentUserId, lastSeen = "Last seen"+"\n"+"Date "+"Time";
    private RecyclerView chatListRv;
    private FirebaseAuth mAuth;
    private DatabaseReference chatRef, userRef;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        init(view);
        currentUserId = mAuth.getCurrentUser().getUid();
        chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void init(View view) {
        mAuth = FirebaseAuth.getInstance();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Social App Users");
        chatListRv = view.findViewById(R.id.chatMessageListRv);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(chatRef, User.class)
                .build();

        FirebaseRecyclerAdapter<User, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull User model) {
                        final String userId = getRef(position).getKey();
                        final String[] profileImg = {"default_image"};
                        userRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("userImg")) {
                                        profileImg[0] = dataSnapshot.child("userImg").getValue().toString();
                                        Picasso.with(getContext()).load(profileImg[0]).placeholder(R.drawable.ic_person_24dp).into(holder.profileCiView);
                                    }

                                    final String profileName = dataSnapshot.child("userName").getValue().toString();
                                    final String profileStatus = dataSnapshot.child("userStatus").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(lastSeen);

                                    if(dataSnapshot.child("userState").hasChild("state")){

                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if(state.equals("online")){
                                            holder.userStatus.setText("online");
                                        }
                                        else if (state.equals("offline")){
                                            String lastSeen = "Last Seen: "+date+" "+time;
                                            holder.userStatus.setText(lastSeen);
                                        }
                                    }
                                    else {

                                        holder.userStatus.setText("offline");
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("visitUserId",userId);
                                            intent.putExtra("visitUserName",profileName);
                                            intent.putExtra("visitUserImage", profileImg[0]);

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_user_display_layout,parent,false);
                        return new ChatViewHolder(view);
                    }
                };
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, userStatus;
        private CircleImageView profileCiView;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUsersNameTvId);
            userStatus = itemView.findViewById(R.id.displayUsersStatusTvId);
            profileCiView = itemView.findViewById(R.id.usersProfileImageId);

        }
    }
}
