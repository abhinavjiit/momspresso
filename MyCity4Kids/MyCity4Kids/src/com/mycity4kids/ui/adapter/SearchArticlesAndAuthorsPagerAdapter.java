package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.ui.fragment.SearchArticlesTabFragment;
import com.mycity4kids.ui.fragment.SearchAuthorsTabFragment;
import com.mycity4kids.ui.fragment.SearchBlogsTabFragment;
import com.mycity4kids.ui.fragment.SearchTopicsTabFragment;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchArticlesAndAuthorsPagerAdapter extends FragmentStatePagerAdapter {

    ArticleModelNew.AllArticles articlelist;
    Activity activity;
    Context context;
    private SearchArticlesTabFragment mArticlefragment;
    private SearchAuthorsTabFragment mAuthorsFragment;
    private SearchTopicsTabFragment mTopicsFragment;
    private SearchBlogsTabFragment mBlogsFragment;
    String searchName = "";

    int currentPosition = 0;

    public SearchArticlesAndAuthorsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public SearchArticlesAndAuthorsPagerAdapter(FragmentManager fragmentManager, Activity activity, ArticleModelNew.AllArticles articlelist, Context context, String searchN) {
        super(fragmentManager);
        this.activity = activity;
        this.articlelist = articlelist;
        this.context = context;
        searchName = searchN;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Articles";
        } else if (position == 1) {
            return "Authors";
        } else {
            return "Topics";
        }
//        else {
//            return "Blogs";
//        }
    }

    @Override
    public Fragment getItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mArticlefragment == null) {
                    mArticlefragment = new SearchArticlesTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mArticlefragment.setArguments(bundle);
                return mArticlefragment;
            case 1:
                if (mAuthorsFragment == null) {
                    mAuthorsFragment = new SearchAuthorsTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mAuthorsFragment.setArguments(bundle);
                return mAuthorsFragment;
            case 2:
                if (mTopicsFragment == null) {
                    mTopicsFragment = new SearchTopicsTabFragment();
                }
                bundle.putString(Constants.SEARCH_PARAM, searchName);
                mTopicsFragment.setArguments(bundle);
                return mTopicsFragment;
//            case 3:
//                if (mBlogsFragment == null) {
//                    mBlogsFragment = new SearchBlogsTabFragment();
//                }
//                bundle.putString(Constants.SEARCH_PARAM, searchName);
//                mBlogsFragment.setArguments(bundle);
//                return mBlogsFragment;
        }

        return null;
    }

    public void refreshArticlesAuthors(String searchText, int pos) {
//        switch (pos) {
//
//            case 0:
        if (null == mArticlefragment) {
            mArticlefragment = new SearchArticlesTabFragment();
        }
        mArticlefragment.refreshAllArticles(searchText, Constants.KEY_RECENT);
        mAuthorsFragment.resetOnceLoadedFlag(searchText);
//                break;
//            case 1:
        if (null == mAuthorsFragment) {
            mAuthorsFragment = new SearchAuthorsTabFragment();
        }
        mAuthorsFragment.refreshAllAuthors(searchText);
        mArticlefragment.resetOnceLoadedFlag(searchText);
//                break;
//            case 2:
        if (mTopicsFragment == null) {
            mTopicsFragment = new SearchTopicsTabFragment();
        }
        mTopicsFragment.refreshAllArticles(searchText, Constants.KEY_RECENT);
//                break;
//        }
//        if (mBlogsFragment == null) {
//            mBlogsFragment = new SearchBlogsTabFragment();
//        }
//        mBlogsFragment.refreshAllArticles(searchText, Constants.KEY_RECENT);

    }

}
