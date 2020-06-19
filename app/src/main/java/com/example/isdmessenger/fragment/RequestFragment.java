package com.example.isdmessenger.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.isdmessenger.R;
import com.example.isdmessenger.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {

    private String currentUserId,reqMsg, sentReqMsg;
    private RecyclerView mChatRequestRv;
    private DatabaseReference chatRequestRef,userRef,contactsRef;
    private FirebaseAuth mAuth;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        init(view);
        currentUserId = mAuth.getCurrentUser().getUid();
        mChatRequestRv.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void init(View view) {
        userRef = FirebaseDatabase.getInstance().getReference().child("Social App Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        mChatRequestRv = view.findViewById(R.id.requestChatRvId);
        reqMsg = "Want to connect with you...";
        sentReqMsg = "you have sent a request to ";
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(chatRequestRef.child(currentUserId),User.class)
                .build();

        FirebaseRecyclerAdapter<User, ChatRequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, ChatRequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatRequestViewHolder holder, int position, @NonNull User model) {
                        holder.itemView.findViewById(R.id.requestAcceptBtnId).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.requestCancelBtnId).setVisibility(View.VISIBLE);

                        final String listUserId = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();

                                    if(type.equals("received")){
                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("userImg"))
                                                {
                                                    String profileImg = dataSnapshot.child("userImg").getValue().toString();
                                                    Picasso.with(getContext()).load(profileImg).placeholder(R.drawable.ic_person_24dp).into(holder.profileCiView);
                                                }

                                                final String profileName = dataSnapshot.child("userName").getValue().toString();
                                                final String profileStatus = dataSnapshot.child("userStatus").getValue().toString();

                                                holder.userName.setText(profileName);
                                                holder.userStatus.setText(sentReqMsg+profileName);


                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence[] option = new CharSequence[]{
                                                                "Cancel chat request"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(" Already sent request ");

                                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                if(i==0){
                                                                    chatRequestRef.child(currentUserId).child(listUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        chatRequestRef.child(listUserId).child(currentUserId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                     builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    else if(type.equals("sent")){
                                        Button requestSentBtn = holder.itemView.findViewById(R.id.requestAcceptBtnId);
                                        requestSentBtn.setText("Request sent");

                                        holder.itemView.findViewById(R.id.requestCancelBtnId).setVisibility(View.INVISIBLE);

                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("userImg"))
                                                {
                                                    String profileImg = dataSnapshot.child("userImg").getValue().toString();
                                                    Picasso.with(getContext()).load(profileImg).placeholder(R.drawable.ic_person_24dp).into(holder.profileCiView);
                                                }

                                                final String profileName = dataSnapshot.child("userName").getValue().toString();
                                                final String profileStatus = dataSnapshot.child("userStatus").getValue().toString();

                                                holder.userName.setText(profileName);
                                                holder.userStatus.setText(reqMsg);


                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence[] option = new CharSequence[]{
                                                                "Accept",
                                                                "Cancel"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(profileName+" Chat request");

                                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                if(i==0){
                                                                    contactsRef.child(currentUserId).child(listUserId).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                contactsRef.child(listUserId).child(currentUserId).child("Contact")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            chatRequestRef.child(currentUserId).child(listUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                chatRequestRef.child(listUserId).child(currentUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                if(i==1){
                                                                    chatRequestRef.child(currentUserId).child(listUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        chatRequestRef.child(listUserId).child(currentUserId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
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
                    public ChatRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_display_layout,parent,false);

                        return new ChatRequestViewHolder(view);
                    }
                };
        mChatRequestRv.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatRequestViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, userStatus;
        private Button acceptRequestBtn, cancelRequestBtn;
        private CircleImageView profileCiView;
        public ChatRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUsersNameTvId);
            userStatus = itemView.findViewById(R.id.displayUsersStatusTvId);
            profileCiView = itemView.findViewById(R.id.usersProfileImageId);
            acceptRequestBtn = itemView.findViewById(R.id.requestAcceptBtnId);
            cancelRequestBtn = itemView.findViewById(R.id.requestCancelBtnId);

        }
    }
}
