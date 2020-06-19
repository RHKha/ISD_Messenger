package com.example.isdmessenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isdmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateUserAccountBtn;
    private EditText userNameEt,userStatusEt;
    private CircleImageView userProfileCimgv;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();

        currentUserId = mAuth.getCurrentUser().getUid();

        updateUserAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });

        retrieveUserInfo();

    }

    private void init() {
        updateUserAccountBtn = findViewById(R.id.updateSettingBtnId);
        userNameEt = findViewById(R.id.setUserNameEtId);
        userStatusEt = findViewById(R.id.setUserStatusEtId);
        userProfileCimgv = findViewById(R.id.settingsProfileCIVId);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void updateUserData() {
        String setUserName = userNameEt.getText().toString();
        String setUserStatus = userStatusEt.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this,"Please write your name first......",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this,"Please write your status......",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();
                profileMap.put("userId",currentUserId);
                profileMap.put("userName",setUserName);
                profileMap.put("userStatus",setUserStatus);
            rootRef.child("Social App Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this,"Please write your name first......",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void retrieveUserInfo() {
        rootRef.child("Social App Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("userName") && (dataSnapshot.hasChild("userImg")))){
                            String retrieveName = dataSnapshot.child("userName").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("userStatus").getValue().toString();
                            String retrieveImage = dataSnapshot.child("userImg").getValue().toString();

                            userNameEt.setText(retrieveName);
                            userStatusEt.setText(retrieveStatus);
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveName = dataSnapshot.child("userName").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("userStatus").getValue().toString();

                            userNameEt.setText(retrieveName);
                            userStatusEt.setText(retrieveStatus);
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
