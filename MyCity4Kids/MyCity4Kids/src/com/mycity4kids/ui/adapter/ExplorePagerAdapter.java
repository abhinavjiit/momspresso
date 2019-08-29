package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.ui.fragment.EventsTabFragment;
import com.mycity4kids.ui.fragment.ResourcesTabFragment;
import com.mycity4kids.ui.fragment.TopThingsTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class ExplorePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public ExplorePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            EventsTabFragment tab1 = new EventsTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else if (position == 1) {
            ResourcesTabFragment tab1 = new ResourcesTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else{
            TopThingsTabFragment tab1 = new TopThingsTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}