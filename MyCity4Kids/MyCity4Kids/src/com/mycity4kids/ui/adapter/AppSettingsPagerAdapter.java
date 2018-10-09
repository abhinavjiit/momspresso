package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.ChangePasswordTabFragment;
import com.mycity4kids.ui.fragment.EditLangPrefsTabFragment;
import com.mycity4kids.ui.fragment.EditPreferencesTabFragment;
import com.mycity4kids.ui.fragment.EditProfileTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class AppSettingsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private EditProfileTabFragment editProfileTabFragment;
    private EditPreferencesTabFragment editPreferencesTabFragment;
    private EditLangPrefsTabFragment editLangPrefsTabFragment;
    private ChangePasswordTabFragment changePasswordTabFragment;
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

//            case 0:
//                if (editProfileTabFragment == null) {
//                    editProfileTabFragment = new EditProfileTabFragment();
//                }
//                bundle.putString(Constants.AUTHOR_ID, authorId);
//                editProfileTabFragment.setArguments(bundle);
//                return editProfileTabFragment;
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
//            case 3:
//                if (changePasswordTabFragment == null) {
//                    changePasswordTabFragment = new ChangePasswordTabFragment();
//                }
//                bundle.putString(Constants.AUTHOR_ID, authorId);
//                changePasswordTabFragment.setArguments(bundle);
//                return changePasswordTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}