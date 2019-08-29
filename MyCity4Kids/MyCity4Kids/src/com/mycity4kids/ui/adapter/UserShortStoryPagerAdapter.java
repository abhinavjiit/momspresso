package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UserDraftArticleTabFragment;
import com.mycity4kids.ui.fragment.UserPublishedArticleTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UserShortStoryPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private UserPublishedArticleTabFragment userPublishedArticleTabFragment;
    private UserDraftArticleTabFragment userDraftArticleTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public UserShortStoryPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId, boolean isPrivateProfile) {
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
                if (userPublishedArticleTabFragment == null) {
                    userPublishedArticleTabFragment = new UserPublishedArticleTabFragment();
                }
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                bundle.putString("contentType", "shortStory");
                userPublishedArticleTabFragment.setArguments(bundle);
                return userPublishedArticleTabFragment;
            case 1:
                if (userDraftArticleTabFragment == null) {
                    userDraftArticleTabFragment = new UserDraftArticleTabFragment();
                }
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                bundle.putString("contentType", "shortStory");
                userDraftArticleTabFragment.setArguments(bundle);
                return userDraftArticleTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}