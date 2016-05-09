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

/**
 * Created by hemant on 19/4/16.
 */
public class SearchArticlesAndAuthorsPagerAdapter extends FragmentStatePagerAdapter {

    ArticleModelNew.AllArticles articlelist;
    Activity activity;
    Context context;
    private SearchArticlesTabFragment mArticlefragment;
    private SearchArticlesTabFragment mPopularArticlefragment;
    private SearchArticlesTabFragment mTrendingArticlefragment;
    private SearchAuthorsTabFragment mAuthorsFragment;
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
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Articles";
//        }
//        else if (position == 1) {
//            return "TRENDING";
//        } else if (position == 2) {
//            return "POPULAR";
        } else {
            //PERSONAL
            return "Authors";
        }
    }

    @Override
    public Fragment getItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mArticlefragment == null) {
                    mArticlefragment = new SearchArticlesTabFragment();
                    bundle.putString(Constants.SEARCH_PARAM, searchName);
                    mArticlefragment.setArguments(bundle);
                }
                return mArticlefragment;
//            case 1:
//                if (mTrendingArticlefragment == null) {
//                    mTrendingArticlefragment = new SearchArticlesTabFragment();
//                    if (articlelist != null)
//                        bundle.putParcelableArrayList(Constants.ARTICLES_LIST, articlelist.getTrending());
//                    bundle.putInt(Constants.TAB_POSITION, 1);
//                    bundle.putString(Constants.SORT_TYPE, "trending");
//                    bundle.putString(Constants.SEARCH_PARAM, searchName);
//                    mTrendingArticlefragment.setArguments(bundle);
//                }
//                return mTrendingArticlefragment;
//
//            case 2:
//                if (mPopularArticlefragment == null) {
//                    mPopularArticlefragment = new SearchArticlesTabFragment();
//                    if (articlelist != null)
//                        bundle.putParcelableArrayList(Constants.ARTICLES_LIST, articlelist.getPopular());
//                    bundle.putInt(Constants.TAB_POSITION, 2);
//                    bundle.putString(Constants.SORT_TYPE, "popular");
//                    bundle.putString(Constants.SEARCH_PARAM, searchName);
//                    mPopularArticlefragment.setArguments(bundle);
//                }
//                return mPopularArticlefragment;

            case 1:
                if (mAuthorsFragment == null) {
                    mAuthorsFragment = new SearchAuthorsTabFragment();
                    bundle.putString(Constants.SEARCH_PARAM, searchName);
                    mAuthorsFragment.setArguments(bundle);
                }
                return mAuthorsFragment;
        }

        return null;
    }

    public void refreshArticlesAuthors(String searchText, int pos) {
        switch (pos) {

            case 0:
                mArticlefragment.refreshAllArticles(searchText, Constants.KEY_RECENT);
//                mTrendingArticlefragment.resetOnceLoadedFlag(searchText);
//                mPopularArticlefragment.resetOnceLoadedFlag(searchText);
                mAuthorsFragment.resetOnceLoadedFlag(searchText);
                break;
//            case 1:
//                mTrendingArticlefragment.refreshAllArticles(searchText, Constants.KEY_TRENDING);
//                mArticlefragment.resetOnceLoadedFlag(searchText);
//                mPopularArticlefragment.resetOnceLoadedFlag(searchText);
//                mAuthorsFragment.resetOnceLoadedFlag(searchText);
//                break;
//            case 2:
//                mPopularArticlefragment.refreshAllArticles(searchText, Constants.KEY_POPULAR);
//                mArticlefragment.resetOnceLoadedFlag(searchText);
//                mTrendingArticlefragment.resetOnceLoadedFlag(searchText);
//                mAuthorsFragment.resetOnceLoadedFlag(searchText);
//                break;
            case 1:
                mAuthorsFragment.refreshAllAuthors(searchText);
                mArticlefragment.resetOnceLoadedFlag(searchText);
//                mTrendingArticlefragment.resetOnceLoadedFlag(searchText);
//                mPopularArticlefragment.resetOnceLoadedFlag(searchText);
                break;
        }


    }

}
