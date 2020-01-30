package com.example.isdmessenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.isdmessenger.MainActivity;
import com.example.isdmessenger.R;
import com.example.isdmessenger.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEt,emailEt,passEt,phoneEt;
    private TextView signInTv;
    private Button signUpBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        signInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString();
                String email = emailEt.getText().toString();
                String phone = phoneEt.getText().toString();
                String password = passEt.getText().toString();

                if(TextUtils.isEmpty(name))
                    Toast.makeText(SignUpActivity.this,"Please Enter Name",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(email))
                    Toast.makeText(SignUpActivity.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(phone))
                    Toast.makeText(SignUpActivity.this,"Please Enter Phone",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(password))
                    Toast.makeText(SignUpActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                else
                    signUp(name,email,phone,password);

            }
        });

    }



    private void signUp(final String name, final String email, final String phone, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    User userData = new User();
                    userData.setUserName(name);
                    userData.setUserEmail(email);
                    userData.setUserPhone(phone);
                    DatabaseReference userRef = databaseReference.child("Users").child(userId).child("userInfo");
                    userRef.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            }
                        }
                    });
                    Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        signInTv = findViewById(R.id.gotoSignupTvId);
        nameEt = findViewById(R.id.signUpNameEtId);
        emailEt = findViewById(R.id.signUpEmailEtId);
        passEt = findViewById(R.id.signUpPasswordEtId);
        phoneEt = findViewById(R.id.signUpPhoneEtId);
        signUpBtn = findViewById(R.id.signUpBtnId);

    }

}
