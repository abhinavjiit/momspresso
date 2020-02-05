package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.SearchAllArticlesTabFragment;
import com.mycity4kids.ui.fragment.SearchAllAuthorsTabFragment;
import com.mycity4kids.ui.fragment.SearchAllTopicsTabFragment;
import com.mycity4kids.ui.fragment.SearchAllVideosTabFragment;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAllPagerAdapter extends FragmentStatePagerAdapter {

    Activity activity;
    Context context;
    private SearchAllArticlesTabFragment mArticlefragment;
    private SearchAllAuthorsTabFragment mAuthorsFragment;
    private SearchAllTopicsTabFragment mSearchAllTopicsTabFragment;
    private SearchAllVideosTabFragment mSearchAllVideosTabFragment;
    String searchName = "";

    int currentPosition = 0;

    public SearchAllPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public SearchAllPagerAdapter(FragmentManager fragmentManager, Activity activity, Context context, String searchN) {
        super(fragmentManager);
        this.activity = activity;
        this.context = context;
        searchName = searchN;
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return activity.getString(R.string.search_article_topic_tab_label);
        } else if (position == 1) {
            return activity.getString(R.string.search_author_tab_label);
        } else if (position == 2) {
            return activity.getString(R.string.search_topic_label);
        } else {
            return activity.getString(R.string.search_video_label);
        }
    }

    @Override
    public Fragment getItem(int position) {
        currentPosition = position;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                if (mArticlefragment == null) {
                    mArticlefragment = new SearchAllArticlesTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mArticlefragment.setArguments(bundle);
                return mArticlefragment;
            case 1:
                if (mAuthorsFragment == null) {
                    mAuthorsFragment = new SearchAllAuthorsTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mAuthorsFragment.setArguments(bundle);
                return mAuthorsFragment;
            case 2:
                if (mSearchAllTopicsTabFragment == null) {
                    mSearchAllTopicsTabFragment = new SearchAllTopicsTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mSearchAllTopicsTabFragment.setArguments(bundle);
                return mSearchAllTopicsTabFragment;
            case 3:
                if (mSearchAllVideosTabFragment == null) {
                    mSearchAllVideosTabFragment = new SearchAllVideosTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mSearchAllVideosTabFragment.setArguments(bundle);
                return mSearchAllVideosTabFragment;

        }

        return null;
    }

    public void refreshArticlesAuthors(String searchText) {
        if (null == mArticlefragment) {
            mArticlefragment = new SearchAllArticlesTabFragment();
        }
        mArticlefragment.refreshAllArticles(searchText);
        mArticlefragment.resetOnceLoadedFlag(searchText);

        if (null == mAuthorsFragment) {
            mAuthorsFragment = new SearchAllAuthorsTabFragment();
        }
        mAuthorsFragment.refreshAllAuthors(searchText);
        mAuthorsFragment.resetOnceLoadedFlag(searchText);

        if (null == mSearchAllTopicsTabFragment) {
            mSearchAllTopicsTabFragment = new SearchAllTopicsTabFragment();
        }
        mSearchAllTopicsTabFragment.refreshAllTopics(searchText);
        mSearchAllTopicsTabFragment.resetOnceLoadedFlag(searchText);

        if (null == mSearchAllVideosTabFragment) {
            mSearchAllVideosTabFragment = new SearchAllVideosTabFragment();
        }
        mSearchAllVideosTabFragment.refreshAllArticles(searchText);
        mSearchAllVideosTabFragment.resetOnceLoadedFlag(searchText);
    }

}
