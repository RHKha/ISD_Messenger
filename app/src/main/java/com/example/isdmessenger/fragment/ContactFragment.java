package com.example.isdmessenger.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.isdmessenger.R;
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
public class ContactFragment extends Fragment {

    private String currentUserId;
    private RecyclerView contactsRv;
    private DatabaseReference contactsRf,usersRef;
    private FirebaseAuth mAuth;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        init(view);

        contactsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        currentUserId = mAuth.getCurrentUser().getUid();

        return view;
    }

    private void init(View view) {
        contactsRv = view.findViewById(R.id.contactsRvId);
        contactsRf = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Social App Users");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(contactsRf,User.class)
                .build();
        FirebaseRecyclerAdapter<User,ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull User model) {
                        String userId = getRef(position).getKey();

                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    if(dataSnapshot.child("userState").hasChild("state")){

                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if(state.equals("online")){
                                            holder.onlineActiveIcon.setVisibility(View.VISIBLE);
                                        }
                                        else if (state.equals("offline")){
                                            holder.onlineActiveIcon.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    else {
                                        holder.onlineActiveIcon.setVisibility(View.INVISIBLE);
                                    }

                                    if(dataSnapshot.hasChild("userImg")){
                                        String profileImg = dataSnapshot.child("userImg").getValue().toString();
                                        String profileName = dataSnapshot.child("userName").getValue().toString();
                                        String profileStatus = dataSnapshot.child("userStatus").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);
                                        Picasso.with(getContext()).load(profileImg).placeholder(R.drawable.ic_person_24dp).into(holder.profileCiView);
                                    }
                                    else {
                                        String profileName = dataSnapshot.child("userName").getValue().toString();
                                        String profileStatus = dataSnapshot.child("userStatus").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_display_layout,parent,false);
                        return new ContactsViewHolder(view);
                    }
                };
                contactsRv.setAdapter(adapter);
                adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, userStatus;
        private CircleImageView profileCiView;
        private ImageView onlineActiveIcon;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUsersNameTvId);
            userStatus = itemView.findViewById(R.id.displayUsersStatusTvId);
            profileCiView = itemView.findViewById(R.id.usersProfileImageId);
            onlineActiveIcon = itemView.findViewById(R.id.activeUserIconIvId);
        }
    }
}
