package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.observablescrollview.CacheFragmentStatePagerAdapter;
import com.mycity4kids.ui.fragment.BlogListingViewFragment;


/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogTapPagerAdapter extends CacheFragmentStatePagerAdapter {

    Activity activity;
    Context context;
    private BlogListingViewFragment mRecentBlogFragment;
    private BlogListingViewFragment mPopularBlogFragment;

    int currentPosition = 0;
    String authorId = "";

    public BlogTapPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    public BlogTapPagerAdapter(FragmentManager fragmentManager, Activity activity,
                               Context context, String authorId) {
        super(fragmentManager);
        this.activity = activity;
        this.context = context;
        this.authorId = authorId;
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
                    bundle.putInt(Constants.TAB_POSITION, 0);
                    bundle.putString(Constants.SORT_TYPE, "recent");
                    bundle.putString(Constants.AUTHOR_ID, authorId);
                    mRecentBlogFragment.setArguments(bundle);
                }
                return mRecentBlogFragment;
            case 1:
                if (mPopularBlogFragment == null) {
                    mPopularBlogFragment = new BlogListingViewFragment();
                    bundle.putInt(Constants.TAB_POSITION, 1);
                    bundle.putString(Constants.SORT_TYPE, "popular");
                    bundle.putString(Constants.AUTHOR_ID, authorId);
                    mPopularBlogFragment.setArguments(bundle);
                }
                return mPopularBlogFragment;
        }
        return null;
    }

}


