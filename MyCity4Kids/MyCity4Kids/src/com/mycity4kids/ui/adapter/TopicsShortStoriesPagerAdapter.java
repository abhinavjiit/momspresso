package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.TopicChallengeTabFragment;
import com.mycity4kids.ui.fragment.TopicsShortStoriesTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class TopicsShortStoriesPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;
    private int challengeIdPostion;

    public TopicsShortStoriesPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;

    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
//        switch (position) {

//            case 0:
        if (!subTopicsList.get(position).getId().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
            bundle.putString("currentSubTopic", new Gson().toJson(subTopicsList.get(position)));
            TopicsShortStoriesTabFragment tab1 = new TopicsShortStoriesTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        } else {
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            TopicChallengeTabFragment tab2 = new TopicChallengeTabFragment();
            tab2.setArguments(bundle);
            return tab2;
        }
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