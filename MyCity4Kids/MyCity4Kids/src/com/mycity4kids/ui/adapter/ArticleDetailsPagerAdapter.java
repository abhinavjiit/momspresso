package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.ui.fragment.ArticleDetailsFragment;

import java.util.ArrayList;

import static com.mycity4kids.ui.activity.ArticleDetailsContainerActivity.NEW_ARTICLE_DETAIL_FLAG;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsPagerAdapter extends FragmentStatePagerAdapter {
    private final String fromScreen;
    private int mNumOfTabs;
    ArrayList<ArticleListingResult> articleList;
    private boolean isSwipeNextAvailable = false;
    private String parentId;
    private boolean newArticleDetailFlag;

    public ArticleDetailsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<ArticleListingResult> articleList, String fromScreen, String parentId, boolean newArticleDetailFlag) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
        this.articleList = articleList;
        this.fromScreen = fromScreen;
        this.parentId = parentId;
        this.newArticleDetailFlag = newArticleDetailFlag;
        if (articleList != null && articleList.size() > 1) {
            isSwipeNextAvailable = true;
        }
    }

    @Override
    public Fragment getItem(int position) {
        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTICLE_ID, articleList.get(position).getId());
        bundle.putString(Constants.AUTHOR_ID, articleList.get(position).getUserId());
        bundle.putString(Constants.BLOG_SLUG, articleList.get(position).getBlogPageSlug());
        bundle.putString(Constants.TITLE_SLUG, articleList.get(position).getTitleSlug());
        bundle.putString(Constants.FROM_SCREEN, fromScreen);
        bundle.putBoolean(NEW_ARTICLE_DETAIL_FLAG, newArticleDetailFlag);
        bundle.putString(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
        bundle.putString(Constants.ARTICLE_INDEX, "-1");
        bundle.putString("parentId", parentId);
        bundle.putBoolean("fromNotification", false);
        bundle.putBoolean("swipeNext", isSwipeNextAvailable);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            Log.e("TAG", "Error Restore State of Fragment : " + e.getMessage(), e);
        }
    }
}
