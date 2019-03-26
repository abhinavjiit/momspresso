package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.CategoryVideosTabFragment;
import com.mycity4kids.ui.fragment.ChallengeCategoryVideoTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class VideoTopicsPagerAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;
    private Fragment currentFragment ;

    public VideoTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
        //currentFragment = new ArrayList<>();
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
        if (subTopicsList != null && subTopicsList.get(position).getId() != null && subTopicsList.get(position).getId().equals("category-ee7ea82543bd4bc0a8dad288561f2beb")) {
            bundle.putString("video_challenge_category_id", subTopicsList.get(position).getId());
           // bundle.putString("currentSubTopic", new Gson().toJson(subTopicsList.get(position)));
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            ChallengeCategoryVideoTabFragment tab2 = new ChallengeCategoryVideoTabFragment();
            tab2.setArguments(bundle);
            //currentFragment= tab2;
            return tab2;
        } else {
            bundle.putString("video_category_id", subTopicsList.get(position).getId());
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            CategoryVideosTabFragment tab1 = new CategoryVideosTabFragment();
            tab1.setArguments(bundle);
            currentFragment = tab1;
            //currentFragment.add(position,tab1);
            Log.e("position is ", position + " ");
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}