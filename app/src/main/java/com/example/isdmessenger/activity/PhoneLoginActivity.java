package com.example.isdmessenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isdmessenger.MainActivity;
import com.example.isdmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private String mVerificationId;

    private Button sendVerificationCodBtn, verifyBtn;
    private EditText inputPhnNumberEt,inputVerifyCodeEt;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        init();

        sendVerificationCodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String phoneNumber = inputPhnNumberEt.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this,"Please Enter Phone Number First.....",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("Please wait, while we are authenticating your phone....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks
                    );
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendVerificationCodBtn.setVisibility(View.INVISIBLE);
                inputPhnNumberEt.setVisibility(View.INVISIBLE);

                String verificationCode = inputVerifyCodeEt.getText().toString();
                if(TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this,"Please write code first....",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Verification Code");
                    progressDialog.setMessage("Please wait, while we are verifying code....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"Error is:"+e,Toast.LENGTH_SHORT).show();

                sendVerificationCodBtn.setVisibility(View.VISIBLE);
                inputPhnNumberEt.setVisibility(View.VISIBLE);

                verifyBtn.setVisibility(View.INVISIBLE);
                inputVerifyCodeEt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendingToken = token;

                progressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"Code has been sent, please check message",Toast.LENGTH_SHORT).show();

                sendVerificationCodBtn.setVisibility(View.INVISIBLE);
                inputPhnNumberEt.setVisibility(View.INVISIBLE);

                verifyBtn.setVisibility(View.VISIBLE);
                inputVerifyCodeEt.setVisibility(View.VISIBLE);

            }
        };

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCodBtn = findViewById(R.id.sendVerifyCodeBtnId);
        verifyBtn = findViewById(R.id.verifyBtnId);
        inputPhnNumberEt = findViewById(R.id.phoneNumberEtId);
        inputVerifyCodeEt = findViewById(R.id.verifyCodeEtId);
        progressDialog = new ProgressDialog(this);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneLoginActivity.this,"Congratulation you are login successfully",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this,"Error: " +message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
