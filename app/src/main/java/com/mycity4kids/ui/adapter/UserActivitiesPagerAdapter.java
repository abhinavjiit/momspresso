package com.mycity4kids.ui.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UsersCommentTabFragment;
import com.mycity4kids.ui.fragment.UsersRecommendationTabFragment;
import com.mycity4kids.ui.fragment.UsersWatchLaterTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UserActivitiesPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private String authorId;
    private boolean isPrivateProfile;

    private UsersRecommendationTabFragment usersRecommendationTabFragment;
    private UsersWatchLaterTabFragment usersWatchLaterTabFragment;
    private UsersCommentTabFragment usersCommentTabFragment;

    public UserActivitiesPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId, boolean isPrivateProfile) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.authorId = authorId;
        this.isPrivateProfile = isPrivateProfile;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.AUTHOR_ID, authorId);
        bundle.putBoolean("isPrivateProfile", isPrivateProfile);
        switch (position) {
            case 0:
                if (usersRecommendationTabFragment == null) {
                    usersRecommendationTabFragment = new UsersRecommendationTabFragment();
                }
                usersRecommendationTabFragment.setArguments(bundle);
                return usersRecommendationTabFragment;

            case 2:
                if (usersWatchLaterTabFragment == null) {
                    usersWatchLaterTabFragment = new UsersWatchLaterTabFragment();
                }
                usersWatchLaterTabFragment.setArguments(bundle);
                return usersWatchLaterTabFragment;
            case 1:
                if (usersCommentTabFragment == null) {
                    usersCommentTabFragment = new UsersCommentTabFragment();
                }
                usersCommentTabFragment.setArguments(bundle);
                return usersCommentTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}