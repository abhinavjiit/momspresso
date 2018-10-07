package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.ui.fragment.About;
import com.mycity4kids.ui.fragment.Contactdetails;

import java.util.ArrayList;

public class UserProfilePagerAdapter extends FragmentStatePagerAdapter {

    private UserDetailResult userDetailResult;
    private ArrayList<CityInfoItem> mDatalist;

    private About about;
    private Contactdetails contactdetails;

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
            case 0:
                if (about == null) {
                    about = new About();
                }
                about.setArguments(bundle);
                return about;
            case 1:
                if (contactdetails == null) {
                    contactdetails = new Contactdetails();
                }
                contactdetails.setArguments(bundle);
                return contactdetails;
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
}