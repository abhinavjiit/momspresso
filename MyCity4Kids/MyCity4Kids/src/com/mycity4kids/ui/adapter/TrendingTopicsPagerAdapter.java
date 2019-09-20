package com.mycity4kids.ui.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.ArticleListingFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class TrendingTopicsPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private String[] feedOrderArray;
    private ArticleListingFragment articleListingFragment;
    private String heading, subHeading, gpImageUrl;
    private int groupId;

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, String[] feedOrderArray) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.feedOrderArray = feedOrderArray;
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
            articleListingFragment = new ArticleListingFragment();
            bundle.putString(Constants.SORT_TYPE, feedOrderArray[position]);
            bundle.putStringArray("feedOrderArray", feedOrderArray);
            bundle.putInt(Constants.TAB_POSITION, position);
            articleListingFragment.setArguments(bundle);
            return articleListingFragment;
        } else {
            ArticleListingFragment articleListingFragment = new ArticleListingFragment();
            bundle.putString(Constants.SORT_TYPE, feedOrderArray[position]);
            bundle.putStringArray("feedOrderArray", feedOrderArray);
            bundle.putInt(Constants.TAB_POSITION, position);
            articleListingFragment.setArguments(bundle);
            return articleListingFragment;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void hideFollowTopicHeader() {
        articleListingFragment.hideFollowTopicHeader();
    }
}
