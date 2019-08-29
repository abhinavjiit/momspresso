package com.mycity4kids.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mycity4kids.ui.fragment.GroupExistingMemberTabFragment;
import com.mycity4kids.ui.fragment.GroupMembershipRequestTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class GroupMembersPagerAdapter extends FragmentStatePagerAdapter {

    private final int groupId;
    private int mNumOfTabs;
    private GroupMembershipRequestTabFragment groupMembershipRequestTabFragment;
    private GroupExistingMemberTabFragment groupExistingMemberTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public GroupMembersPagerAdapter(FragmentManager fm, int NumOfTabs, int groupId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.groupId = groupId;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {

            case 1:
                if (groupMembershipRequestTabFragment == null) {
                    groupMembershipRequestTabFragment = new GroupMembershipRequestTabFragment();
                    bundle.putInt("groupId", groupId);
                    groupMembershipRequestTabFragment.setArguments(bundle);
                }
                return groupMembershipRequestTabFragment;
            case 0:
                if (groupExistingMemberTabFragment == null) {
                    groupExistingMemberTabFragment = new GroupExistingMemberTabFragment();
                    bundle.putInt("groupId", groupId);
                    groupExistingMemberTabFragment.setArguments(bundle);
                }
                return groupExistingMemberTabFragment;
        }

        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}