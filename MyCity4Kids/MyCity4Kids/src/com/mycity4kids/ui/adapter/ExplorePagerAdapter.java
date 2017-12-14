package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.ui.fragment.EventsTabFragment;
import com.mycity4kids.ui.fragment.ResourcesTabFragment;
import com.mycity4kids.ui.fragment.TopThingsTabFragment;
import com.mycity4kids.ui.fragment.TrendingTopicsAllTabFragment;
import com.mycity4kids.ui.fragment.TrendingTopicsTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class ExplorePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public ExplorePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            EventsTabFragment tab1 = new EventsTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else if (position == 1) {
            ResourcesTabFragment tab1 = new ResourcesTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else{
            TopThingsTabFragment tab1 = new TopThingsTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}