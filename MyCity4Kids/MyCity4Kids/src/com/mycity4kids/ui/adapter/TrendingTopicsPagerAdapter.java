package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.ui.fragment.EditorPickFragment;
import com.mycity4kids.ui.fragment.TrendingTopicsAllTabFragment;

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

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
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
        /*Bundle bundle = new Bundle();
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
        }*/

        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("gpHeading", heading);
                bundle.putString("gpSubHeading", subHeading);
                bundle.putString("gpImageUrl", gpImageUrl);
                bundle.putInt("groupId", groupId);
                trendingTopicsAllTabFragment = new TrendingTopicsAllTabFragment();
                trendingTopicsAllTabFragment.setArguments(bundle);
                return trendingTopicsAllTabFragment;
            case 1:
                Bundle editorBundle = new Bundle();
                EditorPickFragment editorPickFragment = new EditorPickFragment();
                editorBundle.putString(Constants.SORT_TYPE,Constants.KEY_TODAYS_BEST);
                editorPickFragment.setArguments(editorBundle);
                return editorPickFragment;
            case 2:
                Bundle editorBundle1 = new Bundle();
                EditorPickFragment editorPickFragment1 = new EditorPickFragment();
                editorBundle1.putString(Constants.SORT_TYPE,Constants.KEY_EDITOR_PICKS);
                editorPickFragment1.setArguments(editorBundle1);
                return editorPickFragment1;
            case 3:
                Bundle editorBundle2 = new Bundle();
                EditorPickFragment editorPickFragment2 = new EditorPickFragment();
                editorBundle2.putString(Constants.SORT_TYPE,Constants.KEY_FOR_YOU);
                editorPickFragment2.setArguments(editorBundle2);
                return editorPickFragment2;
            case 4:
                Bundle editorBundle3 = new Bundle();
                EditorPickFragment editorPickFragment3 = new EditorPickFragment();
                editorBundle3.putString(Constants.SORT_TYPE,Constants.KEY_RECENT);
                editorPickFragment3.setArguments(editorBundle3);
                return editorPickFragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void hideFollowTopicHeader() {
        if (trendingTopicsAllTabFragment != null)
            trendingTopicsAllTabFragment.hideFollowTopicHeader();
    }
}