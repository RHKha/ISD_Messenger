package com.example.isdmessenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.isdmessenger.R;
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

public class ProfileActivity extends AppCompatActivity {

    private String receiveUserId, currentState,senderUserId;
    private CircleImageView userProfileCIv;
    private TextView profileUserName, profileUserStatus;
    private Button sendMessageRequestBtn,declineMessageRequestBtn;
    private DatabaseReference userRef,chatRequestRef,contactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Social App Users");
       chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
       contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        receiveUserId = getIntent().getExtras().get("visitUserId").toString();
        currentState = "new";
        senderUserId = mAuth.getCurrentUser().getUid();
        init();

        retrieveUserInfo();

    }


    private void init() {
        userProfileCIv = findViewById(R.id.visitProfileCivId);
        profileUserName = findViewById(R.id.visitProfileNameTvId);
        profileUserStatus = findViewById(R.id.visitProfileStatusTvId);
        sendMessageRequestBtn = findViewById(R.id.visitProfileSendMessageRequestBtnId);
        declineMessageRequestBtn = findViewById(R.id.visitProfileDeclineRequestBtnId);
    }

    private void retrieveUserInfo() {
        userRef.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("userImg"))){
                    String userImg = dataSnapshot.child("userImg").getValue().toString();
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String userStatus = dataSnapshot.child("userStatus").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(userImg).placeholder(R.drawable.ic_person_24dp).into(userProfileCIv);
                    profileUserName.setText(userName);
                    profileUserStatus.setText(userStatus);
                    
                    manageChatRequest();
                }
                else {
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String useStatus = dataSnapshot.child("userStatus").getValue().toString();

                    profileUserName.setText(userName);
                    profileUserStatus.setText(useStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiveUserId)){
                            String request_type = dataSnapshot.child(receiveUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                currentState = "request_sent";
                                sendMessageRequestBtn.setText("Cancel chat request");
                            }
                            else if(request_type.equals("received")){
                                currentState = "request_received";
                                sendMessageRequestBtn.setText("Accept chat request");

                                declineMessageRequestBtn.setVisibility(View.VISIBLE);
                                declineMessageRequestBtn.setEnabled(true);

                                declineMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelChatRequest();
                                    }
                                });
                            }
                        }
                        else {
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiveUserId)){
                                                currentState = "friends";
                                                sendMessageRequestBtn.setText("Remove this contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if(!senderUserId.equals(receiveUserId)){
            sendMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageRequestBtn.setEnabled(false);
                    if(currentState.equals("new")){
                        sendChatRequest();
                    }
                    if(currentState.equals("request_sent")){
                        cancelChatRequest();
                    }
                    if(currentState.equals("request_received")){
                        acceptChatRequest();
                    }

                    if(currentState.equals("friends")){
                        removeSpecificContact();
                    }
                }
            });
        }
        else {
            sendMessageRequestBtn.setVisibility(View.INVISIBLE);
        }
    }



    private void sendChatRequest()
    {
        chatRequestRef.child(senderUserId).child(receiveUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatRequestRef.child(receiveUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestBtn.setEnabled(true);
                                                currentState = "request_sent";
                                                sendMessageRequestBtn.setText("Cancel chat request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receiveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatRequestRef.child(receiveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestBtn.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestBtn.setText("Send Message");

                                                declineMessageRequestBtn.setVisibility(View.INVISIBLE);
                                                declineMessageRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void acceptChatRequest() {
        contactsRef.child(senderUserId).child(receiveUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiveUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRequestRef.child(senderUserId).child(receiveUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    chatRequestRef.child(receiveUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestBtn.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    sendMessageRequestBtn.setText("Remove this contact");

                                                                                    declineMessageRequestBtn.setVisibility(View.INVISIBLE);
                                                                                    declineMessageRequestBtn.setEnabled(false);
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


    private void removeSpecificContact() {
        contactsRef.child(senderUserId).child(receiveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageRequestBtn.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestBtn.setText("Send Message");

                                                declineMessageRequestBtn.setVisibility(View.INVISIBLE);
                                                declineMessageRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
