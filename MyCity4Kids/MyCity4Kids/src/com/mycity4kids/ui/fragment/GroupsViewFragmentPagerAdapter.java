package com.mycity4kids.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 0:
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

    /*@Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add("");
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }*/
}
