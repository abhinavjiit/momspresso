package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.ui.fragment.SearchAllArticlesAndTopicsTabFragment;
import com.mycity4kids.ui.fragment.SearchAllAuthorsTabFragment;
import com.mycity4kids.ui.fragment.SearchArticlesTabFragment;
import com.mycity4kids.ui.fragment.SearchAuthorsTabFragment;
import com.mycity4kids.ui.fragment.SearchBlogsTabFragment;
import com.mycity4kids.ui.fragment.SearchTopicsTabFragment;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAllPagerAdapter extends FragmentStatePagerAdapter {

    ArticleModelNew.AllArticles articlelist;
    Activity activity;
    Context context;
    private SearchAllArticlesAndTopicsTabFragment mArticlefragment;
    private SearchAllAuthorsTabFragment mAuthorsFragment;
    private SearchTopicsTabFragment mTopicsFragment;
    private SearchBlogsTabFragment mBlogsFragment;
    String searchName = "";

    int currentPosition = 0;

    public SearchAllPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public SearchAllPagerAdapter(FragmentManager fragmentManager, Activity activity, ArticleModelNew.AllArticles articlelist, Context context, String searchN) {
        super(fragmentManager);
        this.activity = activity;
        this.articlelist = articlelist;
        this.context = context;
        searchName = searchN;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return activity.getString(R.string.search_article_topic_tab_label);
        } else {
            return activity.getString(R.string.search_author_tab_label);
        }
    }

    @Override
    public Fragment getItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mArticlefragment == null) {
                    mArticlefragment = new SearchAllArticlesAndTopicsTabFragment();
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
        }

        return null;
    }

    public void refreshArticlesAuthors(String searchText, int pos) {
        if (null == mArticlefragment) {
            mArticlefragment = new SearchAllArticlesAndTopicsTabFragment();
        }
        mArticlefragment.refreshAllArticles(searchText, Constants.KEY_RECENT);
        mAuthorsFragment.resetOnceLoadedFlag(searchText);

        if (null == mAuthorsFragment) {
            mAuthorsFragment = new SearchAllAuthorsTabFragment();
        }
        mAuthorsFragment.refreshAllAuthors(searchText);
        mArticlefragment.resetOnceLoadedFlag(searchText);

    }

}
