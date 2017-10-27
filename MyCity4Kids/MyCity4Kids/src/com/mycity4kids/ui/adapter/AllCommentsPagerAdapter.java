package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.FacebookCommentsFragment;
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

    public AllCommentsPagerAdapter(FragmentManager fm, int NumOfTabs, String mycityCommentURL, String fbCommentURL, String articleId, String author, String type) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.mycityCommentURL = mycityCommentURL;
        this.fbCommentURL = fbCommentURL;
        this.articleId = articleId;
        this.author = author;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString(Constants.ARTICLE_ID, articleId);
        bundle.putString(Constants.AUTHOR, author);
        if (position == 0) {
            MyCityCommentsFragment tab1 = new MyCityCommentsFragment();
            bundle.putString("commentURL", mycityCommentURL);
            tab1.setArguments(bundle);
            return tab1;
        } else {
            FacebookCommentsFragment tab1 = new FacebookCommentsFragment();
            bundle.putString("commentURL", fbCommentURL);
            tab1.setArguments(bundle);
            return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}