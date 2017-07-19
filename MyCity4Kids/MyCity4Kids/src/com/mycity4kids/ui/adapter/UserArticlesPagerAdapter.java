package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.ui.fragment.SearchAllArticlesAndTopicsTabFragment;
import com.mycity4kids.ui.fragment.SubscribeTopicsTabFragment;
import com.mycity4kids.ui.fragment.UserDraftArticleTabFragment;
import com.mycity4kids.ui.fragment.UserPublishedArticleTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class UserArticlesPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private UserPublishedArticleTabFragment userPublishedArticleTabFragment;
    private UserDraftArticleTabFragment userDraftArticleTabFragment;
    private String authorId;

    public UserArticlesPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.authorId = authorId;
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
                userPublishedArticleTabFragment.setArguments(bundle);
                return userPublishedArticleTabFragment;
            case 1:
                if (userDraftArticleTabFragment == null) {
                    userDraftArticleTabFragment = new UserDraftArticleTabFragment();
                }
                bundle.putString(Constants.AUTHOR_ID, authorId);
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