package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.AddArticleTopicsTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class AddArticleTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> selectTopicsList;
    private ArrayList<String> previouslyFollowedTopics;

    public AddArticleTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> selectTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.selectTopicsList = selectTopicsList;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selectTopicList", selectTopicsList.get(position).getChild());
        bundle.putInt("position", position);
        AddArticleTopicsTabFragment tab1 = new AddArticleTopicsTabFragment();

        tab1.setArguments(bundle);
        return tab1;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}