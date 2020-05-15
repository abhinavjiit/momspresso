package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.EditLangPrefsTabFragment;
import com.mycity4kids.ui.fragment.EditPreferencesTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class AppSettingsPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private EditPreferencesTabFragment editPreferencesTabFragment;
    private EditLangPrefsTabFragment editLangPrefsTabFragment;
    private String authorId;

    public AppSettingsPagerAdapter(FragmentManager fm, int NumOfTabs, String authorId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.authorId = authorId;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                if (editPreferencesTabFragment == null) {
                    editPreferencesTabFragment = new EditPreferencesTabFragment();
                }
                bundle.putString(Constants.AUTHOR_ID, authorId);
                editPreferencesTabFragment.setArguments(bundle);
                return editPreferencesTabFragment;
            case 1:
                if (editLangPrefsTabFragment == null) {
                    editLangPrefsTabFragment = new EditLangPrefsTabFragment();
                }
                editLangPrefsTabFragment.setArguments(bundle);
                return editLangPrefsTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}