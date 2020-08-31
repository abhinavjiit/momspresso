package com.mycity4kids.ui.livestreaming;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LiveStreamPagerAdapter extends FragmentPagerAdapter {

    private LiveStreamAboutTabFragment liveStreamAboutTabFragment;
    private LiveStreamCommentTabFragment liveStreamCommentTabFragment;

    public LiveStreamPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                if (liveStreamAboutTabFragment == null) {
                    liveStreamAboutTabFragment = new LiveStreamAboutTabFragment();
                }
                liveStreamAboutTabFragment.setArguments(bundle);
                return liveStreamAboutTabFragment;
            case 1:
                if (liveStreamCommentTabFragment == null) {
                    liveStreamCommentTabFragment = new LiveStreamCommentTabFragment();
                }
                liveStreamCommentTabFragment.setArguments(bundle);
                return liveStreamCommentTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
