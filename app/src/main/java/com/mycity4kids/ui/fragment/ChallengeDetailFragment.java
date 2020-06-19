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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.VideoTrimmerActivity;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengePagerAdapter;
import com.mycity4kids.ui.videochallengenewui.activity.ExoplayerVideoChallengePlayViewActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.videotrimmer.utils.FileUtils;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChallengeDetailFragment extends Fragment implements View.OnClickListener {

    private VideoChallengePagerAdapter videoChallengePagerAdapter;
    private TextView toolbarTitleTextView;
    private FloatingActionButton saveTextView;
    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView shareChallengeImageView;
    private String selectedId;
    private String mappedId;
    private String selectedName;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private String challengeRules = "";
    private Topics topic;
    private int maxDuration;
    private ImageView thumbNail;
    private ImageView back;
    private String comingFrom = "";
    private ImageView videoIndicatorImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_detail_fragment_layout, container, false);

        saveTextView = view.findViewById(R.id.saveTextView);
        tabs = view.findViewById(R.id.id_tabs);
        back = view.findViewById(R.id.back);
        thumbNail = view.findViewById(R.id.thumbNail);
        shareChallengeImageView = view.findViewById(R.id.shareChallengeImageView);
        videoIndicatorImageView = view.findViewById(R.id.videoIndicatorImageView);
        viewPager = view.findViewById(R.id.id_viewpager);
        toolbarTitleTextView = view.findViewById(R.id.toolbarTitleTextView);
        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));
        back.setOnClickListener(this);
        shareChallengeImageView.setOnClickListener(this);
        if (getArguments() != null) {
            selectedName = getArguments().getString("selected_Name");
            selectedActiveUrl = getArguments().getString("selectedActiveUrl");
            challengeRules = getArguments().getString("challengeRules");
            selectedId = getArguments().getString("selectedId");
            selectedStreamUrl = getArguments().getString("selectedStreamUrl");
            mappedId = getArguments().getString("mappedCategory");
            maxDuration = getArguments().getInt("max_Duration");
            topic = getArguments().getParcelable("topic");
            comingFrom = getArguments().getString("comingFrom");
            if ("chooseVideoCategory".equals(comingFrom)) {
                saveTextView.setVisibility(View.VISIBLE);
            }
        }
        if (!StringUtils.isNullOrEmpty(selectedStreamUrl)) {
            thumbNail.setOnClickListener(this);
        } else {
            videoIndicatorImageView.setVisibility(View.GONE);
        }
        Picasso.get().load(selectedActiveUrl).fit()
                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(thumbNail);
        tabs.addTab(tabs.newTab().setText(tabs.getContext().getResources().getString(R.string.about_video)));
        tabs.addTab(
                tabs.newTab().setText(tabs.getContext().getResources().getString(R.string.all_videos_toolbar_title)));
        tabs.addTab(tabs.newTab().setText(tabs.getContext().getResources().getString(R.string.all_winners)));
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
            videoChallengePagerAdapter = new VideoChallengePagerAdapter(getActivity().getSupportFragmentManager(),
                    selectedName, selectedActiveUrl, selectedId, topic, selectedStreamUrl, challengeRules);
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
            if (maxDuration != 0) {
                ((NewVideoChallengeActivity) getActivity()).chooseAndpermissionDialog(maxDuration);
            } else {
                ToastUtils.showToast(getActivity(), "duration should be greater than 0.0");
                Log.i("ERROR", String.valueOf(maxDuration));
            }
        });
        return view;
    }

    public void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
        if (maxDuration != 0) {
            intent.putExtra("duration", String.valueOf(maxDuration));
        }
        intent.putExtra("ChallengeId", selectedId);
        intent.putExtra("categoryId", mappedId);
        intent.putExtra("comingFrom", "Challenge");
        intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(getActivity(), uri));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.thumbNail) {
                Intent intent = new Intent(getActivity(), ExoplayerVideoChallengePlayViewActivity.class);
                intent.putExtra("StreamUrl", selectedStreamUrl);
                startActivity(intent);
                Utils.momVlogEvent(getActivity(), "Challenge detail", "Prompt_video_play", "", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Show_Video_Detail", "", "");
            } else if (v.getId() == R.id.back) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else if (v.getId() == R.id.shareChallengeImageView) {
                AppUtils.shareGenericLinkWithSuccessStatus(v.getContext(),
                        AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL + selectedId);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
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
            if (getActivity() != null) {
                ((NewVideoChallengeActivity) getActivity()).chooseAndpermissionDialog(maxDuration);
            }
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
