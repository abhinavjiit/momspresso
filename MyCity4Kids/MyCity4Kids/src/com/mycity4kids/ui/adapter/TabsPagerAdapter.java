package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.ui.fragment.ArticleViewFragment;

import java.util.ArrayList;

/**
 * Created by manish.soni on 20-07-2015.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    ArticleModelNew.AllArticles articlelist;
    Activity activity;
    Context context;
    private ArticleViewFragment mRecentArticlefragment;
    private ArticleViewFragment mPopularArticlefragment;
    private ArticleViewFragment mTrendingArticlefragment;
    String searchName = "";

    int currentPosition = 0;
    ArticleModelNew.AllArticles tempList;
    Boolean tempFlag = false;


    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public TabsPagerAdapter(FragmentManager fragmentManager, Activity activity, ArticleModelNew.AllArticles articlelist, Context context, String searchN) {
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
            return "RECENT";
        } else if (position == 1) {
            return "TRENDING";
        } else {
            return "POPULAR";
        }
    }

    @Override
    public Fragment getItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mRecentArticlefragment == null) {
                    mRecentArticlefragment = new ArticleViewFragment();
                    if (articlelist != null)
                        bundle.putParcelableArrayList(Constants.ARTICLES_LIST, articlelist.getRecent());
                    bundle.putInt(Constants.TAB_POSITION, 0);
                    bundle.putString(Constants.SORT_TYPE, "recent");
                    bundle.putBoolean(Constants.IS_SEARCH_ACTIVE, false);
                    mRecentArticlefragment.setArguments(bundle);
                } else {
                    if (articlelist != null)

//                        if (isSearchActive() == false)
                        mRecentArticlefragment.refreshSubList(articlelist.getRecent());


                }
                return mRecentArticlefragment;
            case 1:
                if (mPopularArticlefragment == null) {
                    mPopularArticlefragment = new ArticleViewFragment();
                    if (articlelist != null)
                        bundle.putParcelableArrayList(Constants.ARTICLES_LIST, articlelist.getTrending());
                    bundle.putInt(Constants.TAB_POSITION, 1);
                    bundle.putString(Constants.SORT_TYPE, "trending");
                    bundle.putBoolean(Constants.IS_SEARCH_ACTIVE, false);
                    mPopularArticlefragment.setArguments(bundle);
                } else {
                    if (articlelist != null)
                        mPopularArticlefragment.refreshSubList(articlelist.getTrending());
                }
                return mPopularArticlefragment;

            case 2:
                if (mTrendingArticlefragment == null) {
                    mTrendingArticlefragment = new ArticleViewFragment();
                    if (articlelist != null)
                        bundle.putParcelableArrayList(Constants.ARTICLES_LIST, articlelist.getPopular());
                    bundle.putInt(Constants.TAB_POSITION, 2);
                    bundle.putString(Constants.SORT_TYPE, "popular");
                    bundle.putBoolean(Constants.IS_SEARCH_ACTIVE, false);
                    mTrendingArticlefragment.setArguments(bundle);
                } else {
                    if (articlelist != null)
                        mTrendingArticlefragment.refreshSubList(articlelist.getPopular());
                }

                return mTrendingArticlefragment;
        }

        return null;
    }

    public void setNewData(ArticleModelNew.AllArticles articlelist) {
        this.articlelist = articlelist;
    }

    public void setSearchString(String msearch) {
        searchName = msearch;
    }

    public Boolean isSearchActive() {

        if (searchName.equalsIgnoreCase("")) {
            return false;
        } else {
            tempFlag = true;
            return true;
        }

    }

    public void refreshCurrentPageBySearch(int pos, ArrayList<CommonParentingList> searchList) {


        switch (pos) {
            case 0:

                this.articlelist.setRecent(searchList);
                mRecentArticlefragment.refreshSubListBySearch(true, searchName, 0, articlelist);
                break;
            case 1:
                this.articlelist.setPopular(searchList);
                mRecentArticlefragment.refreshSubListBySearch(true, searchName, 1, articlelist);

                break;
            case 2:
                mRecentArticlefragment.refreshSubListBySearch(true, searchName, 2, articlelist);
                this.articlelist.setTrending(searchList);

                break;
        }


    }

    public ArticleViewFragment getFragmentByPosition(int currentPagePosition) {
        switch (currentPagePosition) {
            case 0:
                return mRecentArticlefragment;
            case 1:
                return mPopularArticlefragment;
            case 2:
                return mTrendingArticlefragment;
            default:
                return mRecentArticlefragment;
        }
    }
}
