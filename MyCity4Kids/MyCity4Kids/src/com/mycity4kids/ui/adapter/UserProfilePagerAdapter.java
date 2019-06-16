package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.About;
import com.mycity4kids.ui.fragment.Contactdetails;
import com.mycity4kids.ui.fragment.RewardsTabFragment;

import java.util.ArrayList;

public class UserProfilePagerAdapter extends FragmentStatePagerAdapter {

    private UserDetailResult userDetailResult;
    private ArrayList<CityInfoItem> mDatalist;

    private About about;
    Context context;
    private Contactdetails contactdetails;
    private RewardsTabFragment rewardsFragment;
    private String isRewardAdded = "0";

    public UserProfilePagerAdapter(FragmentManager fm, UserDetailResult userDetails, ArrayList<CityInfoItem> mDatalist, String isRewardAdded, Context context) {
        super(fm);
        this.userDetailResult = userDetails;
        this.mDatalist = mDatalist;
        this.isRewardAdded = isRewardAdded;
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userDetail", userDetailResult);
        bundle.putParcelableArrayList("cityList", mDatalist);
        switch (position) {
            case 0:
                if (about == null) {
                    about = new About();
                }
                about.setArguments(bundle);
                return about;
            case 1:
                Utils.campaignEvent(context, "Rewards 1st screen", "Edit Profile", "Rewards", "", "android", SharedPrefUtils.getAppLocale(context), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Rewards_Detail");
                if (rewardsFragment == null) {
                    rewardsFragment = new RewardsTabFragment();
                }
                bundle.putString("isRewardsAdded", isRewardAdded);
                rewardsFragment.setArguments(bundle);
                return rewardsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
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