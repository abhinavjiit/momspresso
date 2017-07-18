package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.TrendingListingResult;
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
//        switch (position) {
//            case 0:
        bundle.putParcelable("trendingTopicsData", trendingListingResults.get(position));
        bundle.putInt("position", position);
        TrendingTopicsTabFragment tab1 = new TrendingTopicsTabFragment();

        tab1.setArguments(bundle);
        return tab1;
//            case 1:
//                TabFragment2 tab2 = new TabFragment2();
//                return tab2;
//            case 2:
//                TabFragment3 tab3 = new TabFragment3();
//                return tab3;
//            default:
//                return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}