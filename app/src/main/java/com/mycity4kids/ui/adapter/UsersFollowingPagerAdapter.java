package com.mycity4kids.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.fragment.UserFollowFBSuggestionTabFragment;
import com.mycity4kids.ui.fragment.UserFollowingTabFragment;

/**
 * Created by hemant on 24/5/17.
 */
public class UsersFollowingPagerAdapter extends FragmentStatePagerAdapter {

    private int tabsCount;
    private UserFollowingTabFragment userFollowingTabFragment;
    private UserFollowFBSuggestionTabFragment fbSuggestionTabFragment;
    private String authorId;
    private boolean isPrivateProfile;

    public UsersFollowingPagerAdapter(FragmentManager fm, int tabsCount, String authorId, boolean isPrivateProfile) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabsCount = tabsCount;
        this.authorId = authorId;
        this.isPrivateProfile = isPrivateProfile;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                userFollowingTabFragment = new UserFollowingTabFragment();
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                userFollowingTabFragment.setArguments(bundle);
                return userFollowingTabFragment;
            case 1:
                fbSuggestionTabFragment = new UserFollowFBSuggestionTabFragment();
                bundle.putString(Constants.AUTHOR_ID, authorId);
                bundle.putBoolean("isPrivateProfile", isPrivateProfile);
                bundle.putString("contentType", "shortStory");
                fbSuggestionTabFragment.setArguments(bundle);
                return fbSuggestionTabFragment;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    public void refreshFacebookData(int requestCode, int resultCode, Intent data) {
        fbSuggestionTabFragment.onActivityResult(requestCode, resultCode, data);
    }
}