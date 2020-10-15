package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.fragment.CategoryVideosTabFragment;
import com.mycity4kids.ui.fragment.ChallengeCategoryVideoTabFragment;
import com.mycity4kids.ui.fragment.FollowingVideoTabFragment;
import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class VideoTopicsPagerAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;
    private ArrayList<Topics> subTopicsList;
    private Fragment currentFragment;
    private ArrayList<String> str = new ArrayList<>();

    public VideoTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Topics> subTopicsList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
        this.subTopicsList = subTopicsList;
        //currentFragment = new ArrayList<>();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
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
        if (position == 0) {
            return new FollowingVideoTabFragment();
        } else if (subTopicsList != null && subTopicsList.get(position).getId() != null && subTopicsList.get(position)
                .getId().equals("category-ee7ea82543bd4bc0a8dad288561f2beb")) {
            ChallengeCategoryVideoTabFragment tab2 = new ChallengeCategoryVideoTabFragment();
            tab2.setArguments(bundle);
            return tab2;
        } else {
            bundle.putString("video_category_id", subTopicsList.get(position).getId());
            bundle.putParcelable("currentSubTopic", subTopicsList.get(position));
            bundle.putStringArrayList("vlogSelectedlangs", str);
            CategoryVideosTabFragment tab1 = new CategoryVideosTabFragment();
            tab1.setArguments(bundle);
            currentFragment = tab1;
            Log.e("position is ", position + " ");
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void selectedVlogLangs(ArrayList<String> langs) {
        this.str = langs;
    }
}