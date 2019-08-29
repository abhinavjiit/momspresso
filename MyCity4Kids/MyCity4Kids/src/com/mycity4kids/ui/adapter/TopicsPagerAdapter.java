package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.TopicsArticlesTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class TopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;
    private Fragment currentFragment ;
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    public TopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("currentSubTopic", new Gson().toJson(subTopicsList.get(position)));
        bundle.putString("currentSubTopic", new Gson().toJson(subTopicsList.get(position)));
        TopicsArticlesTabFragment tab1 = new TopicsArticlesTabFragment();
        tab1.setArguments(bundle);
        currentFragment = tab1;
        return tab1;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}