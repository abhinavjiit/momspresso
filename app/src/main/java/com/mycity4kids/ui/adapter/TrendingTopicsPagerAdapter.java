package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.ArticleListingFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class TrendingTopicsPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private String[] feedOrderArray;
    private ArticleListingFragment articleListingFragment;

    public TrendingTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, String[] feedOrderArray) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
        this.feedOrderArray = feedOrderArray;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
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
}
