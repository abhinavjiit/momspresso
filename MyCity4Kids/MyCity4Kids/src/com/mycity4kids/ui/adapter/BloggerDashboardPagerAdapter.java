package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.observablescrollview.CacheFragmentStatePagerAdapter;
import com.mycity4kids.ui.fragment.BookmarkedBlogsListTabFragment;
import com.mycity4kids.ui.fragment.PublishedArticlesListTabFragment;


/**
 * Created by manish.soni on 27-07-2015.
 */
public class BloggerDashboardPagerAdapter extends CacheFragmentStatePagerAdapter {

    Context context;
    private BookmarkedBlogsListTabFragment mBookmarkedBlogFragment;
    private PublishedArticlesListTabFragment mPublishedBlogFragment;

    int currentPosition = 0;
    BlogArticleListResponse.BlogArticleListing tempList;
    int bookmarkedArticlesCount, publishedArticlesCount;


    public BloggerDashboardPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    public BloggerDashboardPagerAdapter(FragmentManager fragmentManager, Context context,
                                        int bookmarkedArticlesCount, int publishedArticlesCount) {
        super(fragmentManager);
        this.context = context;
        this.bookmarkedArticlesCount = bookmarkedArticlesCount;
        this.publishedArticlesCount = publishedArticlesCount;
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
            return "Bookmark";
        } else {
            return "Published";
        }
    }

    @Override
    public Fragment createItem(int position) {

        currentPosition = position;

        Bundle bundle = new Bundle();
        switch (position) {

            case 0:
                if (mBookmarkedBlogFragment == null) {
                    mBookmarkedBlogFragment = new BookmarkedBlogsListTabFragment();
                }
                return mBookmarkedBlogFragment;
            case 1:
                if (mPublishedBlogFragment == null) {
                    mPublishedBlogFragment = new PublishedArticlesListTabFragment();
                    bundle = new Bundle();
                    bundle.putInt("publishedArticleCount", publishedArticlesCount);
                    mPublishedBlogFragment.setArguments(bundle);
                }
                return mPublishedBlogFragment;
        }
        return null;
    }

}


