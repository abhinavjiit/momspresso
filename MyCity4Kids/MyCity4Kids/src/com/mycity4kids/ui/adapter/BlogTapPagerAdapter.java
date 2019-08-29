package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mycity4kids.observablescrollview.CacheFragmentStatePagerAdapter;


/**
 * Created by manish.soni on 27-07-2015.
 */
public class BlogTapPagerAdapter extends CacheFragmentStatePagerAdapter {

    Activity activity;
    Context context;

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

        }
        return null;
    }

}


