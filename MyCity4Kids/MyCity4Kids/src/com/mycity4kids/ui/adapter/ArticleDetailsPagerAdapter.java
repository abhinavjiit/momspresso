package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.ui.fragment.ArticleDetailsFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    ArrayList<ArticleListingResult> articleList;
    private boolean isSwipeNextAvailable = false;

    public ArticleDetailsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<ArticleListingResult> articleList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.articleList = articleList;
        if (articleList != null && articleList.size() > 1) {
            isSwipeNextAvailable = true;
        }
    }

    @Override
    public Fragment getItem(int position) {

//        Bundle bundle = new Bundle();
//        switch (position) {
//            case 0:
//        bundle.putParcelable("trendingTopicsData", trendingListingResults.get(position));

        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        Bundle bundle = new Bundle();

//        bundle.putString(Constants.ARTICLE_ID, "article-38e15582702c4ad9a18354179bc8c320");
//        bundle.putString(Constants.AUTHOR_ID, "71acf1433617424cbbbece853b9b769d");
//        bundle.putString(Constants.BLOG_SLUG, null);
//        bundle.putString(Constants.TITLE_SLUG, "join-the-inclusive-india-initiative-for-a-better-tomorrow");

        bundle.putString(Constants.ARTICLE_ID, articleList.get(position).getId());
        bundle.putString(Constants.AUTHOR_ID, articleList.get(position).getUserId());
        bundle.putString(Constants.BLOG_SLUG, articleList.get(position).getBlogPageSlug());
        bundle.putString(Constants.TITLE_SLUG, articleList.get(position).getTitleSlug());
        bundle.putString(Constants.FROM_SCREEN, "Notification");
        bundle.putString(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
        bundle.putString(Constants.ARTICLE_INDEX, "-1");
        bundle.putBoolean("fromNotification", false);
        bundle.putBoolean("swipeNext", isSwipeNextAvailable);

        fragment.setArguments(bundle);
        return fragment;
//            case 1:
//                TabFragment2 tab2 = new TabFragment2();
//                return tab2;
//            case 2:
//                TabFragment3 tab3 = new TabFragment3();
//                return tab3;
//            default:
//                return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
