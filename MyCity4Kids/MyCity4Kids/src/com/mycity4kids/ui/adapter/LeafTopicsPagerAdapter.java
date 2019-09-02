package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.view.ViewGroup;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.LeafTopicArticlesTabFragment;
import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by hemant on 24/5/17.
 */
public class LeafTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;
    private Fragment currentFragment;

    public LeafTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
    }

    public Fragment getCurrentFragment() {
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