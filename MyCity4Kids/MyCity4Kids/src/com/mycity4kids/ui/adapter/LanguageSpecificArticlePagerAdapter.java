package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.ui.fragment.LanguageSpecificArticlesTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class LanguageSpecificArticlePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<LanguageConfigModel> trendingListingResults;

    public LanguageSpecificArticlePagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<LanguageConfigModel> trendingListingResults) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.trendingListingResults = trendingListingResults;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("languageData", trendingListingResults.get(position));
        LanguageSpecificArticlesTabFragment tab1 = new LanguageSpecificArticlesTabFragment();
        tab1.setArguments(bundle);
        return tab1;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}