package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UserDraftArticleTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UserDraftsContentPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private UserDraftArticleTabFragment userDraftArticleTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public UserDraftsContentPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId, boolean isPrivateProfile) {
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
                userDraftArticleTabFragment = new UserDraftArticleTabFragment();
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                userDraftArticleTabFragment.setArguments(bundle);
                return userDraftArticleTabFragment;
            case 1:
                userDraftArticleTabFragment = new UserDraftArticleTabFragment();
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