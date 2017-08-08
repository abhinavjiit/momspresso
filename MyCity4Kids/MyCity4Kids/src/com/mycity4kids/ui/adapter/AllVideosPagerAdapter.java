package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.ui.fragment.FunnyVideosTabFragment;
import com.mycity4kids.ui.fragment.MomspressoVideosTabFragment;

/**
 * Created by hemant on 8/8/17.
 */
public class AllVideosPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private MomspressoVideosTabFragment momspressoVideosTabFragment;
    private FunnyVideosTabFragment funnyVideosTabFragment;

    public AllVideosPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                if (momspressoVideosTabFragment == null) {
                    momspressoVideosTabFragment = new MomspressoVideosTabFragment();
                }
                momspressoVideosTabFragment.setArguments(bundle);
                return momspressoVideosTabFragment;
            case 1:
                if (funnyVideosTabFragment == null) {
                    funnyVideosTabFragment = new FunnyVideosTabFragment();
                }
                funnyVideosTabFragment.setArguments(bundle);
                return funnyVideosTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}