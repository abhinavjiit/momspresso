package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.ArticleCommentsFragment;
import com.mycity4kids.ui.fragment.MyCityCommentsFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class AllCommentsPagerAdapter extends FragmentStatePagerAdapter {
    private final String articleId;
    private int mNumOfTabs;
    private String mycityCommentURL;
    private String fbCommentURL;
    private String author;
    private String type;
    private String titleSlug, blogSlug, userType;

    public AllCommentsPagerAdapter(FragmentManager fm, int NumOfTabs, String mycityCommentURL, String fbCommentURL, String articleId, String author, String type,
                                   String titleSlug, String blogSlug, String userType) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.mycityCommentURL = mycityCommentURL;
        this.fbCommentURL = fbCommentURL;
        this.articleId = articleId;
        this.author = author;
        this.type = type;
        this.userType = userType;
        this.titleSlug = titleSlug;
        this.blogSlug = blogSlug;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString(Constants.ARTICLE_ID, articleId);
        bundle.putString(Constants.AUTHOR, author);
        bundle.putString(Constants.TITLE_SLUG, titleSlug);
        bundle.putString(Constants.BLOG_SLUG, blogSlug);
        bundle.putString("userType", userType);

        if (position == 0) {
            ArticleCommentsFragment tab1 = new ArticleCommentsFragment();
            bundle.putString("commentURL", fbCommentURL);
            tab1.setArguments(bundle);
            return tab1;
        } else {
            MyCityCommentsFragment tab1 = new MyCityCommentsFragment();
            bundle.putString("commentURL", mycityCommentURL);
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}