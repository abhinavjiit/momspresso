package com.mycity4kids.ui.videochallengenewui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengePagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewVideoChallengeActivity extends BaseActivity {
    VideoChallengePagerAdapter videoChallengePagerAdapter;
    private AppBarLayout appBarLayout;
    private RelativeLayout challengeHeaderRelative;
    private RelativeLayout mainMediaFrameLayout;
    private SimpleExoPlayerView exoplayerChallengeDetailListing;
    private LinearLayout submitButtonLinearLayout;
    private TextView challengeNameText, submitStoryText, toolbarTitleTextView;
    private TabLayout tabs;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private String jsonMyObject;
    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private int pos;
    private Topics topic;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> activeUrl = new ArrayList<>();
    private ArrayList<String> activeStreamUrl = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private String parentName, parentId;
    private CoordinatorLayout coordinatorLayout;
    private ImageView thumbNail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_video_listing_detail);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
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
        activeStreamUrl = intent.getStringArrayListExtra("StreamUrl");

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
        try {
            Picasso.with(this).load(selectedActiveUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(thumbNail);
        } catch (Exception e) {
            thumbNail.setImageResource(R.drawable.default_article);
        }

        tabs.addTab(tabs.newTab().setText("ABOUT"));
        tabs.addTab(tabs.newTab().setText("VIDEOS"));

        videoChallengePagerAdapter = new VideoChallengePagerAdapter(getSupportFragmentManager(), selected_Name, selectedActiveUrl, selectedId, topic, selectedStreamUrl);
        viewPager.setAdapter(videoChallengePagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                            if(tab.getPosition()==2){
//                                saveTextView.setVisibility(View.GONE);
//                            }else{
//                                saveTextView.setVisibility(View.VISIBLE);
//                            }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
}
