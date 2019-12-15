package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.ui.fragment.MilestonesFragment;
import com.mycity4kids.ui.fragment.RankingInfoTabFragment;
import com.mycity4kids.ui.fragment.RankingStatsTabFragment;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingPagerAdapter extends FragmentStatePagerAdapter {
    private String authorId;
    private int mNumOfTabs;
    private RankingInfoTabFragment rankingInfoTabFragment;
    private RankingStatsTabFragment rankingStatsTabFragment;
    private MilestonesFragment fragmentMilestones;

    public RankingPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    public RankingPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.authorId = authorId;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("authorId", authorId);
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
            case 2:
                if (fragmentMilestones == null) {
                    fragmentMilestones = new MilestonesFragment();
                }
                fragmentMilestones.setArguments(bundle);
                return fragmentMilestones;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}