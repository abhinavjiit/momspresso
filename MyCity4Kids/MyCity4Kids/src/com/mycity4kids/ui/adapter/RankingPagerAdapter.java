package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.RankingInfoTabFragment;
import com.mycity4kids.ui.fragment.RankingStatsTabFragment;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private RankingInfoTabFragment rankingInfoTabFragment;
    private RankingStatsTabFragment rankingStatsTabFragment;

    public RankingPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (rankingInfoTabFragment == null) {
                    rankingInfoTabFragment = new RankingInfoTabFragment();
                }
                rankingInfoTabFragment.setArguments(bundle);
                return rankingInfoTabFragment;
            case 1:
                if (rankingStatsTabFragment == null) {
                    rankingStatsTabFragment = new RankingStatsTabFragment();
                }
                rankingStatsTabFragment.setArguments(bundle);
                return rankingStatsTabFragment;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}