package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.videochallengenewui.Fragment.VideoChallengeDiscription;
import com.mycity4kids.ui.videochallengenewui.Fragment.VideoChallengeListing;
import com.mycity4kids.ui.videochallengenewui.Fragment.WinnerVlogsListingTabFragment;

public class VideoChallengePagerAdapter extends FragmentPagerAdapter {

    private String selectedId;
    private String selectedName;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private Topics topic;
    private String challengeRules;
    private VideoChallengeDiscription discription;
    private VideoChallengeListing listing;
    private WinnerVlogsListingTabFragment winnerVlogsListingTabFragment;

    public VideoChallengePagerAdapter(FragmentManager fm, String selectedName, String selectedActiveUrl,
            String selectedId, Topics topic, String selectedStreamUrl, String challengeRules) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.selectedName = selectedName;
        this.selectedActiveUrl = selectedActiveUrl;
        this.selectedId = selectedId;
        this.topic = topic;
        this.selectedStreamUrl = selectedStreamUrl;
        this.challengeRules = challengeRules;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("selectedId", selectedId);
        bundle.putString("selected_Name", selectedName);
        bundle.putString("selectedActiveUrl", selectedActiveUrl);
        bundle.putString("selectedStreamUrl", selectedStreamUrl);
        bundle.putString("challengeRules", challengeRules);
        bundle.putParcelable("topics", topic);

        switch (position) {
            case 0:
                if (discription == null) {
                    discription = new VideoChallengeDiscription();
                }
                discription.setArguments(bundle);

                return discription;
            case 1:
                if (listing == null) {
                    listing = new VideoChallengeListing();
                }
                listing.setArguments(bundle);
                return listing;
            case 2:
                if (winnerVlogsListingTabFragment == null) {
                    winnerVlogsListingTabFragment = new WinnerVlogsListingTabFragment();
                }
                winnerVlogsListingTabFragment.setArguments(bundle);
                return winnerVlogsListingTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
