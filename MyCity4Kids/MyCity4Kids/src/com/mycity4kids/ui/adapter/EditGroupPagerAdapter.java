package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.internal.LinkedTreeMap;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.ui.fragment.EditGpDescTabFragment;
import com.mycity4kids.ui.fragment.EditGpJoiningFormTabFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 5/7/18.
 */

public class EditGroupPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private final GroupResult result;
    private EditGpDescTabFragment editGpDescTabFragment;
    private EditGpJoiningFormTabFragment editGpJoiningFormTabFragment;

    public EditGroupPagerAdapter(FragmentManager fm, int NumOfTabs, GroupResult result) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.result = result;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("groupItem", result);
        bundle.putSerializable("groupQA", (LinkedTreeMap<String, String>) result.getQuestionnaire());
        switch (position) {
            case 0:
                if (editGpDescTabFragment == null) {
                    editGpDescTabFragment = new EditGpDescTabFragment();
                }
                editGpDescTabFragment.setArguments(bundle);
                return editGpDescTabFragment;
            case 1:
                if (editGpJoiningFormTabFragment == null) {
                    editGpJoiningFormTabFragment = new EditGpJoiningFormTabFragment();
                }
                editGpJoiningFormTabFragment.setArguments(bundle);
                return editGpJoiningFormTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public GroupResult getAllUpdatedFields() {
        GroupResult updatedGroupItem = null;
        if (editGpDescTabFragment != null) {
            updatedGroupItem = editGpDescTabFragment.getUpdatedDetails();
        }
        if (updatedGroupItem != null && editGpJoiningFormTabFragment != null) {
            updatedGroupItem.setQuestionnaire(editGpJoiningFormTabFragment.getUpdatedDetails());
        }
        return updatedGroupItem;
    }

    public ArrayList<GroupsCategoryMappingResult> getUpdatedCategories() {
        if (editGpDescTabFragment != null) {
            return editGpDescTabFragment.getUpdatedCategories();
        }
        return null;
    }
}