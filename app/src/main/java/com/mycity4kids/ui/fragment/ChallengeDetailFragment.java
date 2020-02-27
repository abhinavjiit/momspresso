package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.VideoTrimmerActivity;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengePagerAdapter;
import com.mycity4kids.ui.videochallengenewui.activity.ExoplayerVideoChallengePlayViewActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.videotrimmer.utils.FileUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChallengeDetailFragment extends Fragment implements View.OnClickListener {


    VideoChallengePagerAdapter videoChallengePagerAdapter;
    AppBarLayout appBarLayout;
    RelativeLayout challengeHeaderRelative;
    RelativeLayout mainMediaFrameLayout;
    PlayerView exoplayerChallengeDetailListing;
    LinearLayout submitButtonLinearLayout;
    TextView challengeNameText, submitStoryText, toolbarTitleTextView;
    com.getbase.floatingactionbutton.FloatingActionButton saveTextView;
    TabLayout tabs;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private String selectedId, mappedId;
    String screen;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    String challengeRules = "";
    private Topics topic;
    private CoordinatorLayout rootLayout;
    private int max_Duration;
    private ImageView thumbNail, back;
    private String comingFrom = "";
    private CoordinatorLayout momVlogCoachMark;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_detail_fragment_layout, container, false);

        saveTextView = view.findViewById(R.id.saveTextView);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.id_appbar);
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.mainprofile_parent_layout);
        challengeHeaderRelative = (RelativeLayout) view.findViewById(R.id.challengeHeaderRelative);
        mainMediaFrameLayout = (RelativeLayout) view.findViewById(R.id.main_media_frame);
        exoplayerChallengeDetailListing = (PlayerView) view.findViewById(R.id.exoplayerChallengeDetailListing);
        submitButtonLinearLayout = (LinearLayout) view.findViewById(R.id.submit_challenge_relative_Layout);
        challengeNameText = (TextView) view.findViewById(R.id.ChallengeNameText);
        submitStoryText = (TextView) view.findViewById(R.id.submit_story_text);
        tabs = (TabLayout) view.findViewById(R.id.id_tabs);
        back = view.findViewById(R.id.back);
        thumbNail = (ImageView) view.findViewById(R.id.thumbNail);
        viewPager = (ViewPager) view.findViewById(R.id.id_viewpager);
        toolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
        momVlogCoachMark = view.findViewById(R.id.momVlogCoachMark);
        toolbarTitleTextView = (TextView) view.findViewById(R.id.toolbarTitleTextView);
        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));
        back.setOnClickListener(this);
        thumbNail.setOnClickListener(this);

        if (getArguments() != null) {


            selected_Name = getArguments().getString("selected_Name");
            selectedActiveUrl = getArguments().getString("selectedActiveUrl");
            challengeRules = getArguments().getString("challengeRules");
            selectedId = getArguments().getString("selectedId");
            selectedStreamUrl = getArguments().getString("selectedStreamUrl");
            mappedId = getArguments().getString("mappedCategory");
            max_Duration = getArguments().getInt("max_Duration");
            topic = getArguments().getParcelable("topic");
            comingFrom = getArguments().getString("comingFrom");


            if (comingFrom.equals("chooseVideoCategory")) {
                saveTextView.setVisibility(View.VISIBLE);
            }


        }
        Picasso.get().load(selectedActiveUrl).fit()
                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(thumbNail);
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.about_video)));
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.all_videos_toolbar_title)));
        AppUtils.changeTabsFont(tabs);
        View root = tabs.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.app_red));
            drawable.setSize(5, 1);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
        if (getActivity() != null) {


            videoChallengePagerAdapter = new VideoChallengePagerAdapter(getActivity().getSupportFragmentManager(), selected_Name, selectedActiveUrl, selectedId, topic, selectedStreamUrl, challengeRules);
            viewPager.setAdapter(videoChallengePagerAdapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {


                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                    viewPager.setCurrentItem(tab.getPosition());


                }
            });
        }

        saveTextView.setOnClickListener(t -> {
            if (max_Duration != 0) {

                ((NewVideoChallengeActivity) getActivity()).chooseAndpermissionDialog(max_Duration);
            } else {
                ToastUtils.showToast(getActivity(), "duration should be greater than 0.0");
                Crashlytics.log("max_duration is :" + String.valueOf(max_Duration));
                Log.i("ERROR", String.valueOf(max_Duration));
            }
        });


        return view;
    }


    public void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(getActivity(), uri);

        if (max_Duration != 0) {
            intent.putExtra("duration", String.valueOf(max_Duration));
        }
        intent.putExtra("ChallengeId", selectedId);
        intent.putExtra("categoryId", mappedId);
        intent.putExtra("comingFrom", "Challenge");
        intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(getActivity(), uri));
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.thumbNail) {
            Intent intent = new Intent(getActivity(), ExoplayerVideoChallengePlayViewActivity.class);
            intent.putExtra("StreamUrl", selectedStreamUrl);
            startActivity(intent);
            Utils.momVlogEvent(getActivity(), "Challenge detail", "Prompt_video_play", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Detail", "", "");
        }

        if (v.getId() == R.id.back) {
            if (getActivity() != null)
                getActivity().finish();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String click) {
        if (click.equals("showDialogBox")) {
            showDialogBox();

        }

    }

    private void showDialogBox() {
        if (comingFrom.equals("chooseVideoCategory")) {
            viewPager.setCurrentItem(0);
            saveTextView.setVisibility(View.VISIBLE);
            ((NewVideoChallengeActivity) getActivity()).chooseAndpermissionDialog(max_Duration);
        } else if ("notification".equals(comingFrom)) {
            viewPager.setCurrentItem(0);
            saveTextView.setVisibility(View.VISIBLE);
        } else {
            viewPager.setCurrentItem(1);
            saveTextView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }


}
