package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.fragment.TutorialFragment;


public class TutorialAdapter extends FragmentStatePagerAdapter {

    Context mContext;
    private final int PAGE_COUNT = 4;

    public TutorialAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        bundle.putInt(AppConstants.SLIDER_POSITION, position);
        TutorialFragment viewPagerFragment = new TutorialFragment();
        viewPagerFragment.setArguments(bundle);
        return viewPagerFragment;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
