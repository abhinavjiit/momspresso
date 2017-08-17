package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UsersBookmarkTabFragment;
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
    private UsersBookmarkTabFragment usersBookmarkTabFragment;
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
            case 1:
                if (isPrivateProfile) {
                    if (usersBookmarkTabFragment == null) {
                        usersBookmarkTabFragment = new UsersBookmarkTabFragment();
                    }
                    usersBookmarkTabFragment.setArguments(bundle);
                    return usersBookmarkTabFragment;
                } else {
                    if (usersCommentTabFragment == null) {
                        usersCommentTabFragment = new UsersCommentTabFragment();
                    }
                    usersCommentTabFragment.setArguments(bundle);
                    return usersCommentTabFragment;
                }
            case 2:
                if (usersWatchLaterTabFragment == null) {
                    usersWatchLaterTabFragment = new UsersWatchLaterTabFragment();
                }
                usersWatchLaterTabFragment.setArguments(bundle);
                return usersWatchLaterTabFragment;
            case 3:
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