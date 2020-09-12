package com.mycity4kids.ui.livestreaming;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UpcomingLivesPagerAdapter extends FragmentPagerAdapter {

    private UpcomingLiveAboutTabFragment upcomingLiveAboutTabFragment;
    private LiveStreamCommentTabFragment liveStreamCommentTabFragment;
    private LiveStreamResult item;

    public UpcomingLivesPagerAdapter(FragmentManager fm, LiveStreamResult item) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.item = item;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", item);
        switch (position) {
            case 0:
                if (upcomingLiveAboutTabFragment == null) {
                    upcomingLiveAboutTabFragment = new UpcomingLiveAboutTabFragment();
                }
                upcomingLiveAboutTabFragment.setArguments(bundle);
                return upcomingLiveAboutTabFragment;
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
