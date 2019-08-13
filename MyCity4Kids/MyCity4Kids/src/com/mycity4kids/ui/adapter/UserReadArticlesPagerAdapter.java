package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UserDraftArticleTabFragment;
import com.mycity4kids.ui.fragment.UserReadArticleTabFragment;
import com.mycity4kids.ui.fragment.UserSeenFunnyVideosTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UserReadArticlesPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private UserReadArticleTabFragment userReadArticleTabFragment;
    private UserDraftArticleTabFragment userDraftArticleTabFragment;
    private UserSeenFunnyVideosTabFragment userFunnyVideosTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public UserReadArticlesPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId, boolean isPrivateProfile) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.authorId = authorId;
        this.isPrivateProfile = isPrivateProfile;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                userReadArticleTabFragment = new UserReadArticleTabFragment();
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                userReadArticleTabFragment.setArguments(bundle);
                return userReadArticleTabFragment;
            case 1:
                userReadArticleTabFragment = new UserReadArticleTabFragment();
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                bundle.putString("contentType", "shortStory");
                userReadArticleTabFragment.setArguments(bundle);
                return userReadArticleTabFragment;
            case 2:
                if (userFunnyVideosTabFragment == null) {
                    userFunnyVideosTabFragment = new UserSeenFunnyVideosTabFragment();
                }
                bundle.putString(Constants.AUTHOR_ID, authorId);
                userFunnyVideosTabFragment.setArguments(bundle);
                return userFunnyVideosTabFragment;

        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}