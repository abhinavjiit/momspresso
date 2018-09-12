package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kelltontech.utils.StringUtils;
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
    private TrendingTopicsAllTabFragment trendingTopicsAllTabFragment;
    private String heading, subHeading, gpImageUrl;
    private int groupId;

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<TrendingListingResult> trendingListingResults) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.trendingListingResults = trendingListingResults;
    }


    public void setGroupInfo(String heading, String subHeading, String gpImageUrl, int groupId) {
        if (!StringUtils.isNullOrEmpty(heading)) {
            this.heading = heading;
        }
        if (!StringUtils.isNullOrEmpty(subHeading)) {
            this.subHeading = subHeading;
        }
        this.gpImageUrl = gpImageUrl;
        this.groupId = groupId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("gpHeading", heading);
        bundle.putString("gpSubHeading", subHeading);
        bundle.putString("gpImageUrl", gpImageUrl);
        bundle.putInt("groupId", groupId);
        if (position == 0) {
            trendingTopicsAllTabFragment = new TrendingTopicsAllTabFragment();
            trendingTopicsAllTabFragment.setArguments(bundle);
            return trendingTopicsAllTabFragment;
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

    public void hideFollowTopicHeader() {
        trendingTopicsAllTabFragment.hideFollowTopicHeader();
    }
}