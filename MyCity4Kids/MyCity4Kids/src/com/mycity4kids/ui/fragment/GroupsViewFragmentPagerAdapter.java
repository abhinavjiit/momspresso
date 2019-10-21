package com.mycity4kids.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupsViewFragmentPagerAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public GroupsViewFragmentPagerAdapter(FragmentManager manager, int NumOfTabs) {
        super(manager);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 1:
                GroupMyFeedFragment groupMyFeedFragment = new GroupMyFeedFragment();
                return groupMyFeedFragment;
            case 2:
                GroupsPollFragment groupsPollFragment = new GroupsPollFragment();
                return groupsPollFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
