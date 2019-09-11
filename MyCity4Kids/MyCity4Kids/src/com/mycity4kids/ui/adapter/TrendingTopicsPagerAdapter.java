package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.ui.fragment.EditorPickFragment;
import com.mycity4kids.ui.fragment.TrendingTopicsAllTabFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hemant on 24/5/17.
 */
public class TrendingTopicsPagerAdapter extends FragmentStatePagerAdapter {

    private static final String HOME_PAGE_FEED_ORDER = "home_page_feed_order";
    private int mNumOfTabs;
    private ArrayList<TrendingListingResult> trendingListingResults;
    private TrendingTopicsAllTabFragment trendingTopicsAllTabFragment;
    private String heading, subHeading, gpImageUrl;
    private int groupId;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String[] feedOrderArray;

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        feedOrderArray = mFirebaseRemoteConfig.getString(HOME_PAGE_FEED_ORDER).split(",");
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
        if (feedOrderArray[position].equals(Constants.KEY_TRENDING)) {
            Bundle bundle = new Bundle();
            bundle.putString("gpHeading", heading);
            bundle.putString("gpSubHeading", subHeading);
            bundle.putString("gpImageUrl", gpImageUrl);
            bundle.putInt("groupId", groupId);
            trendingTopicsAllTabFragment = new TrendingTopicsAllTabFragment();
            trendingTopicsAllTabFragment.setArguments(bundle);
            return trendingTopicsAllTabFragment;
        } else {
            Bundle editorBundle = new Bundle();
            EditorPickFragment editorPickFragment = new EditorPickFragment();
            editorBundle.putString(Constants.SORT_TYPE, feedOrderArray[position]);
            editorPickFragment.setArguments(editorBundle);
            return editorPickFragment;
        }
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
