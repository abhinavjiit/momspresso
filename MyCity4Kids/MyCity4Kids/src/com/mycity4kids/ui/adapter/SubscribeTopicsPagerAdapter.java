package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.ui.fragment.SubscribeTopicsTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class SubscribeTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<SelectTopic> selectTopicsList;
    private ArrayList<String> previouslyFollowedTopics;

    public SubscribeTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<SelectTopic> selectTopicsList, ArrayList<String> previouslyFollowedTopics) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.selectTopicsList = selectTopicsList;
        this.previouslyFollowedTopics = previouslyFollowedTopics;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selectTopicList", selectTopicsList);
        bundle.putInt("position", position);
        bundle.putStringArrayList("previouslyFollowedTopics", previouslyFollowedTopics);
        SubscribeTopicsTabFragment tab1 = new SubscribeTopicsTabFragment();

        tab1.setArguments(bundle);
        return tab1;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}