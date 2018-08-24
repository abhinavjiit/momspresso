package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.LeafTopicArticlesTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class LeafTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;

    public LeafTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
        LeafTopicArticlesTabFragment tab1 = new LeafTopicArticlesTabFragment();
        tab1.setArguments(bundle);
        return tab1;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}