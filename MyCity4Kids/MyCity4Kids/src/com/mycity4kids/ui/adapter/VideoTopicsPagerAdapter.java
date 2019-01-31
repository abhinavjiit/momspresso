package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.CategoryVideosTabFragment;
import com.mycity4kids.ui.fragment.ChallengeCategoryVideoTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class VideoTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;

    public VideoTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        if (subTopicsList != null && subTopicsList.get(position).getId() != null && subTopicsList.get(position).getId().equals("category-ee7ea82543bd4bc0a8dad288561f2beb")) {
            bundle.putString("video_challenge_category_id", subTopicsList.get(position).getId());
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            ChallengeCategoryVideoTabFragment tab2 = new ChallengeCategoryVideoTabFragment();
            tab2.setArguments(bundle);
            return tab2;

        } else {
            bundle.putString("video_category_id", subTopicsList.get(position).getId());
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            CategoryVideosTabFragment tab1 = new CategoryVideosTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}