package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.TopicsArticlesTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class TopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;

    public TopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
//        switch (position) {
//            case 0:
        bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
        TopicsArticlesTabFragment tab1 = new TopicsArticlesTabFragment();
        tab1.setArguments(bundle);
        return tab1;
//            case 1:
//                TabFragment2 tab2 = new TabFragment2();
//                return tab2;
//            case 2:
//                TabFragment3 tab3 = new TabFragment3();
//                return tab3;
//            default:
//                return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}