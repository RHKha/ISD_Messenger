package com.example.isdmessenger.fragment;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.isdmessenger.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class ProfileFragment extends Fragment {

    private ImageView userImg;
    private TextView userName,userEmail,userId;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        setUserInfo();
        return view;
    }


    private void init(View view) {
        userName = view.findViewById(R.id.userNameTvId);
        userEmail = view.findViewById(R.id.userEmailTvId);
        //userId = view.findViewById(R.id.userIdTvId);
        userImg = view.findViewById(R.id.userProfileIvId);
    }



    private void setUserInfo() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            userName.setText(personName);
            userEmail.setText(personEmail);
            //userId.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(userImg);
        }
    }

}
