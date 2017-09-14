package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.ui.fragment.TrendingTopicsAllTabFragment;
import com.mycity4kids.ui.fragment.TrendingTopicsTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class TrendingTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<TrendingListingResult> trendingListingResults;

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<TrendingListingResult> trendingListingResults) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.trendingListingResults = trendingListingResults;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            TrendingTopicsAllTabFragment tab1 = new TrendingTopicsAllTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else {
            bundle.putParcelable("trendingTopicsData", trendingListingResults.get(position - 1));
            TrendingTopicsTabFragment tab1 = new TrendingTopicsTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}