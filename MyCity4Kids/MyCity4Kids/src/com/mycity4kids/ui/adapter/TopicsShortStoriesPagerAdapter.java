package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

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
    private Fragment currentFragment ;

    public TopicsShortStoriesPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;

    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);

    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        if (!subTopicsList.get(position).getId().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
            bundle.putString("currentSubTopic", new Gson().toJson(subTopicsList.get(position)));
            TopicsShortStoriesTabFragment tab1 = new TopicsShortStoriesTabFragment();
            tab1.setArguments(bundle);
            currentFragment = tab1;
            return tab1;
        } else {
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            TopicChallengeTabFragment tab2 = new TopicChallengeTabFragment();
            tab2.setArguments(bundle);

            return tab2;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}