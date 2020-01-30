package com.example.isdmessenger.model;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.isdmessenger.fragment.GroupsFragment;
import com.example.isdmessenger.fragment.ProfileFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            case 1:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){

            case 0:
                return "Groups";

            case 1:
                return "Profile";
            default:
                return null;

        }
    }
}
