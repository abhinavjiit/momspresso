package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.ui.fragment.About;
import com.mycity4kids.ui.fragment.Contactdetails;
import com.mycity4kids.ui.fragment.RewardsTabFragment;

import java.util.ArrayList;

public class UserProfilePagerAdapter extends FragmentStatePagerAdapter {

    private UserDetailResult userDetailResult;
    private ArrayList<CityInfoItem> mDatalist;

    private About about;
    private Contactdetails contactdetails;
    private RewardsTabFragment rewardsFragment;

    public UserProfilePagerAdapter(FragmentManager fm, UserDetailResult userDetails, ArrayList<CityInfoItem> mDatalist) {
        super(fm);
        this.userDetailResult = userDetails;
        this.mDatalist = mDatalist;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userDetail", userDetailResult);
        bundle.putParcelableArrayList("cityList", mDatalist);
        switch (position) {
            case 1:
                if (about == null) {
                    about = new About();
                }
                about.setArguments(bundle);
                return about;
            case 0:
                if (contactdetails == null) {
                    contactdetails = new Contactdetails();
                }
                contactdetails.setArguments(bundle);
                return contactdetails;
            case 2:
                if (rewardsFragment == null) {
                    rewardsFragment = new RewardsTabFragment();
                }
                rewardsFragment.setArguments(bundle);
                return rewardsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public About getAbout() {
        return about;
    }

    public Contactdetails getContactdetails() {
        return contactdetails;
    }

    public RewardsTabFragment getRewardsFragment() {
        return rewardsFragment;
    }
}