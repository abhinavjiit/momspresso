package com.mycity4kids.ui.videochallengenewui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengePagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewVideoChallengeActivity extends BaseActivity implements View.OnClickListener {
    VideoChallengePagerAdapter videoChallengePagerAdapter;
    AppBarLayout appBarLayout;
    RelativeLayout challengeHeaderRelative;
    RelativeLayout mainMediaFrameLayout;
    SimpleExoPlayerView exoplayerChallengeDetailListing;
    LinearLayout submitButtonLinearLayout;
    TextView challengeNameText, submitStoryText, toolbarTitleTextView;
    FloatingActionButton saveTextView;
    TabLayout tabs;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private String jsonMyObject;
    private String selectedId;
    String screen;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    String challengeRules = "";
    private int pos;
    private Topics topic;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> activeUrl = new ArrayList<>();
    private ArrayList<String> activeStreamUrl = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private ArrayList<String> rules = new ArrayList<>();
    private String parentName, parentId;
    private CoordinatorLayout coordinatorLayout;
    private ImageView thumbNail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_video_listing_detail);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
        saveTextView = (FloatingActionButton) findViewById(R.id.saveTextView);
        appBarLayout = (AppBarLayout) findViewById(R.id.id_appbar);
        challengeHeaderRelative = (RelativeLayout) findViewById(R.id.challengeHeaderRelative);
        mainMediaFrameLayout = (RelativeLayout) findViewById(R.id.main_media_frame);
        exoplayerChallengeDetailListing = (SimpleExoPlayerView) findViewById(R.id.exoplayerChallengeDetailListing);
        submitButtonLinearLayout = (LinearLayout) findViewById(R.id.submit_challenge_relative_Layout);
        challengeNameText = (TextView) findViewById(R.id.ChallengeNameText);
        submitStoryText = (TextView) findViewById(R.id.submit_story_text);
        tabs = (TabLayout) findViewById(R.id.id_tabs);
        thumbNail = (ImageView) findViewById(R.id.thumbNail);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("Topic");
        }
        topic = new Gson().fromJson(jsonMyObject, Topics.class);
        pos = intent.getIntExtra("position", 0);
        challengeId = intent.getStringArrayListExtra("challenge");
        Display_Name = intent.getStringArrayListExtra("Display_Name");
        activeUrl = intent.getStringArrayListExtra("StringUrl");
        parentId = intent.getStringExtra("parentId");
        parentName = intent.getStringExtra("topics");
        screen = intent.getStringExtra("screenName");
        activeStreamUrl = intent.getStringArrayListExtra("StreamUrl");
        rules = intent.getStringArrayListExtra("rules");

        if (challengeId != null && challengeId.size() != 0) {
            selectedId = challengeId.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeUrl != null && activeUrl.size() != 0) {
            selectedActiveUrl = activeUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeStreamUrl != null && activeStreamUrl.size() != 0) {
            selectedStreamUrl = activeStreamUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (Display_Name != null && Display_Name.size() != 0) {
            selected_Name = Display_Name.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (rules != null && rules.size() != 0) {
            if (rules.size() > pos) {
                challengeRules = rules.get(pos);
            }
        }
        try {
            Picasso.with(this).load(selectedActiveUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(thumbNail);
        } catch (Exception e) {
            thumbNail.setImageResource(R.drawable.default_article);
        }
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.about_video)));
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.all_videos_toolbar_title)));


        videoChallengePagerAdapter = new VideoChallengePagerAdapter(getSupportFragmentManager(), selected_Name, selectedActiveUrl, selectedId, topic, selectedStreamUrl, challengeRules);
        viewPager.setAdapter(videoChallengePagerAdapter);
        if (screen.equals("creation")) {
            viewPager.setCurrentItem(0);
            saveTextView.setVisibility(View.VISIBLE);
        } else {
            viewPager.setCurrentItem(1);
            saveTextView.setVisibility(View.VISIBLE);
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2 || tab.getPosition() == 1) {
                    saveTextView.setVisibility(View.VISIBLE);
                } else {
                    saveTextView.setVisibility(View.VISIBLE);
                }
                tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                tabs.setTabTextColors(getResources().getColor(R.color.grey), getResources().getColor(R.color.app_red));

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2 || tab.getPosition() == 1) {
                    saveTextView.setVisibility(View.VISIBLE);
                } else {
                    saveTextView.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(tab.getPosition());
                tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                tabs.setTabTextColors(getResources().getColor(R.color.grey), getResources().getColor(R.color.app_red));

            }
        });
        thumbNail.setOnClickListener(this);
        saveTextView.setOnClickListener(view -> {
            Intent intent1 = new Intent(NewVideoChallengeActivity.this, ChooseVideoCategoryActivity.class);
            if (selected_Name != null && !selected_Name.isEmpty() && selectedId != null && !selectedId.isEmpty()) {
                intent1.putExtra("selectedId", selectedId);
                intent1.putExtra("selectedName", selected_Name);
                intent1.putExtra("comingFrom", "Challenge");
                startActivity(intent1);

            } else {
                ToastUtils.showToast(NewVideoChallengeActivity.this, "something went wrong at the server");
            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.thumbNail) {
            Intent intent = new Intent(this, ExoplayerVideoChallengePlayViewActivity.class);
            intent.putExtra("StreamUrl", selectedStreamUrl);
            startActivity(intent);
            Utils.momVlogEvent(NewVideoChallengeActivity.this, "Challenge detail", "Prompt_video_play", "", "android", SharedPrefUtils.getAppLocale(NewVideoChallengeActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_detail", "", "");

        }


    }
}
