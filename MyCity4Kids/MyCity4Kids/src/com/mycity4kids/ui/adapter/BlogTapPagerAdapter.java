package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.observablescrollview.CacheFragmentStatePagerAdapter;
import com.mycity4kids.ui.fragment.BlogListingViewFragment;


/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogTapPagerAdapter extends CacheFragmentStatePagerAdapter {

    BlogArticleListResponse.BlogArticleListing articlelist;
    Activity activity;
    Context context;
    private BlogListingViewFragment mRecentBlogFragment;
    private BlogListingViewFragment mPopularBlogFragment;
    String searchName = "";

    int currentPosition = 0;
    BlogArticleListResponse.BlogArticleListing tempList;
    String blog_title = "";


    public BlogTapPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    public BlogTapPagerAdapter(FragmentManager fragmentManager, Activity activity, BlogArticleListResponse.BlogArticleListing articlelist, Context context, String blog_title) {
        super(fragmentManager);
        this.activity = activity;
        this.articlelist = articlelist;
        this.context = context;
        this.blog_title = blog_title;
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
            return "RECENT";
        } else {
            return "MOST POPULAR";
        }
    }

    @Override
    public Fragment createItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mRecentBlogFragment == null) {
                    mRecentBlogFragment = new BlogListingViewFragment();
                    if (articlelist != null)
                        bundle.putParcelable(Constants.BLOG_ARTICLES_LIST, articlelist.getRecent());
                    bundle.putInt(Constants.TAB_POSITION, 0);
                    bundle.putString(Constants.SORT_TYPE, "recent");
                    bundle.putString(Constants.BLOG_TITLE, blog_title);
                    mRecentBlogFragment.setArguments(bundle);
                }
                return mRecentBlogFragment;
            case 1:
                if (mPopularBlogFragment == null) {
                    mPopularBlogFragment = new BlogListingViewFragment();
                    if (articlelist != null)
                        bundle.putParcelable(Constants.BLOG_ARTICLES_LIST, articlelist.getPopular());
                    bundle.putInt(Constants.TAB_POSITION, 1);
                    bundle.putString(Constants.SORT_TYPE, "popular");
                    bundle.putString(Constants.BLOG_TITLE, blog_title);
                    mPopularBlogFragment.setArguments(bundle);
                }
                return mPopularBlogFragment;
        }
        return null;
    }


    public void setNewData(BlogArticleListResponse.BlogArticleListing articlelist) {
        this.articlelist = articlelist;
    }

//
//    public void setSearchString(String msearch) {
//        searchName = msearch;
//    }
//
//    public Boolean isSearchActive() {
//
//        if (searchName.equalsIgnoreCase("")) {
//            return false;
//        } else {
//            tempFlag = true;
//            return true;
//        }
//
//    }

//    public void refreshCurrentPageBySearch(int pos, ArrayList<CommonParentingList> searchList) {
//
//
//        switch (pos) {
//            case 0:
//
//                this.articlelist.setRecent(searchList);
//                mRecentBlogFragment.refreshSubListBySearch(true, searchName, 0, articlelist);
//                break;
//            case 1:
//                this.articlelist.setPopular(searchList);
//                mRecentBlogFragment.refreshSubListBySearch(true, searchName, 1, articlelist);
//
//                break;
//        }


//    }
//
//    public BlogListingViewFragment getFragmentByPosition(int currentPagePosition) {
//        switch (currentPagePosition) {
//            case 0:
//                return mRecentBlogFragment;
//            case 1:
//                return mPopularBlogFragment;
//            default:
//                return mRecentBlogFragment;
//        }
//    }
}


