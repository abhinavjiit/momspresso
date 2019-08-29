package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.ui.fragment.ShortStoryFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryPagerAdapter extends FragmentStatePagerAdapter {
    private final String fromScreen;
    private int mNumOfTabs;
    ArrayList<ArticleListingResult> articleList;
    private boolean isSwipeNextAvailable = false;

    public ShortStoryPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<ArticleListingResult> articleList, String fromScreen) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.articleList = articleList;
        this.fromScreen = fromScreen;
        if (articleList != null && articleList.size() > 1) {
            isSwipeNextAvailable = true;
        }
    }

    @Override
    public Fragment getItem(int position) {
        ShortStoryFragment fragment = new ShortStoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTICLE_ID, articleList.get(position).getId());
        bundle.putString(Constants.AUTHOR_ID, articleList.get(position).getUserId());
        bundle.putString(Constants.BLOG_SLUG, articleList.get(position).getBlogPageSlug());
        bundle.putString(Constants.TITLE_SLUG, articleList.get(position).getTitleSlug());
        bundle.putString(Constants.FROM_SCREEN, fromScreen);
        bundle.putString(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
        bundle.putString(Constants.ARTICLE_INDEX, "-1");
        bundle.putInt("colorPosition", position);
        bundle.putBoolean("fromNotification", false);
        bundle.putBoolean("swipeNext", isSwipeNextAvailable);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
