package com.example.isdmessenger.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.isdmessenger.fragment.ChatFragment;
import com.example.isdmessenger.fragment.ContactFragment;
import com.example.isdmessenger.fragment.GroupsFragment;
import com.example.isdmessenger.fragment.ProfileFragment;
import com.example.isdmessenger.fragment.RequestFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;

            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 4:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){

            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Request";

            case 4:
                return "Profile";
            default:
                return null;

        }
    }
}
