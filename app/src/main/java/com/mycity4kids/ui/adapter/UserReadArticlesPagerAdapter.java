package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UserReadArticleTabFragment;
import com.mycity4kids.ui.fragment.UserSeenFunnyVideosTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UserReadArticlesPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private UserReadArticleTabFragment userReadArticleTabFragment;
    private UserSeenFunnyVideosTabFragment userFunnyVideosTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public UserReadArticlesPagerAdapter(FragmentManager fm, int numOfTabs, String authorId, boolean isPrivateProfile) {
        super(fm);
        this.numOfTabs = numOfTabs;
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
            default:
                break;
        }

        return null;

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}