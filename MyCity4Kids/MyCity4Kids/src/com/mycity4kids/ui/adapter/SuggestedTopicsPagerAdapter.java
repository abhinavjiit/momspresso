package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.SuggestedTopicsResult;
import com.mycity4kids.ui.fragment.LanguageSpecificArticlesTabFragment;
import com.mycity4kids.ui.fragment.SuggestedTopicsTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 24/5/17.
 */
public class SuggestedTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<ArrayList<String>> trendingListingResults;
    private ArrayList<String> languageNameList;

    public SuggestedTopicsPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<ArrayList<String>> trendingListingResults, ArrayList<String> languageNameList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.trendingListingResults = trendingListingResults;
        this.languageNameList = languageNameList;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("languageData", trendingListingResults.get(position));
        bundle.putString("languageName", languageNameList.get(position));
        SuggestedTopicsTabFragment tab1 = new SuggestedTopicsTabFragment();
        tab1.setArguments(bundle);
        return tab1;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}