package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.videochallengenewui.Fragment.VideoChallengeDiscription;
import com.mycity4kids.ui.videochallengenewui.Fragment.VideoChallengeListing;

public class VideoChallengePagerAdapter extends FragmentPagerAdapter {

    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private Topics topic;
    String challengeRules;
    VideoChallengeDiscription discription;
    VideoChallengeListing listing;

    public VideoChallengePagerAdapter(FragmentManager fm, String selected_Name, String selectedActiveUrl, String selectedId, Topics topic, String selectedStreamUrl, String challengeRules) {
        super(fm);
        this.selected_Name = selected_Name;
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
        bundle.putString("selected_Name", selected_Name);
        bundle.putString("selectedActiveUrl", selectedActiveUrl);
        bundle.putString("selectedStreamUrl", selectedStreamUrl);
        bundle.putString(" challengeRules ", challengeRules);
        bundle.putParcelable("topics", topic);

        switch (position) {

            case 1:
                if (discription == null) {
                    discription = new VideoChallengeDiscription();
                }
                discription.setArguments(bundle);

                return discription;
            case 0:
                if (listing == null) {
                    listing = new VideoChallengeListing();
                }
                listing.setArguments(bundle);
                return listing;
        }


        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
