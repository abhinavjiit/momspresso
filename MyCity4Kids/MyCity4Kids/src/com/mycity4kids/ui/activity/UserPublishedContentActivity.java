package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.adapter.UserPublishedContentPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 19/7/17.
 */
public class UserPublishedContentActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView searchAllImageView;
    private TextView toolbarTitleTextView;
    private String contentType;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_published_and_drafts_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitleTextView = findViewById(R.id.toolbarTitle);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        searchAllImageView = (ImageView) findViewById(R.id.searchAllImageView);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitle);

        contentType = getIntent().getStringExtra("contentType");
        searchAllImageView.setOnClickListener(this);

        String authorId = getIntent().getStringExtra(Constants.AUTHOR_ID);
        boolean isPrivateProfile = AppUtils.isPrivateProfile(authorId);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTitleTextView.setText(getResources().getString(R.string.user_article_tabbar_published_label));

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.search_article_topic_tab_label)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.lang_setting_stories_label)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.myprofile_section_videos_label)));
        AppUtils.changeTabsFont(tabLayout);

        UserPublishedContentPagerAdapter adapter = new UserPublishedContentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), authorId, isPrivateProfile);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (AppConstants.CONTENT_TYPE_ARTICLE.equals(contentType)) {
            viewPager.setCurrentItem(0);
        } else if (AppConstants.CONTENT_TYPE_SHORT_STORY.equals(contentType)) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(2);
        }

//        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchAllImageView:
                Intent searchIntent = new Intent(this, SearchAllActivity.class);
                searchIntent.putExtra(Constants.FILTER_NAME, "");
                searchIntent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
