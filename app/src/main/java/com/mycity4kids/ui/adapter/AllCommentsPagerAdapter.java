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
    private int numOfTabs;
    private String mycityCommentUrl;
    private String fbCommentUrl;
    private String author;
    private String contentType;
    private String titleSlug;
    private String blogSlug;
    private String userType;
    private String authorId;

    public AllCommentsPagerAdapter(FragmentManager fm, int numOfTabs, String mycityCommentUrl, String fbCommentUrl,
            String articleId, String author, String contentType,
            String titleSlug, String blogSlug, String userType, String authorId) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.mycityCommentUrl = mycityCommentUrl;
        this.fbCommentUrl = fbCommentUrl;
        this.articleId = articleId;
        this.author = author;
        this.contentType = contentType;
        this.userType = userType;
        this.titleSlug = titleSlug;
        this.blogSlug = blogSlug;
        this.authorId = authorId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("contentType", contentType);
        bundle.putString(Constants.ARTICLE_ID, articleId);
        bundle.putString(Constants.AUTHOR, author);
        bundle.putString(Constants.TITLE_SLUG, titleSlug);
        bundle.putString(Constants.BLOG_SLUG, blogSlug);
        bundle.putString("userType", userType);
        bundle.putString(Constants.AUTHOR_ID, authorId);

        if (position == 0) {
            ArticleCommentsFragment tab1 = new ArticleCommentsFragment();
            bundle.putString("commentURL", fbCommentUrl);
            tab1.setArguments(bundle);
            return tab1;
        } else {
            MyCityCommentsFragment tab1 = new MyCityCommentsFragment();
            bundle.putString("commentURL", mycityCommentUrl);
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}