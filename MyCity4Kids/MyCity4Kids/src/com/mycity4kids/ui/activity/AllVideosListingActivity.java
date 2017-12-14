package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.AllVideosPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 8/8/17.
 */
public class AllVideosListingActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout videosTabLayout;
    private ViewPager videosViewPager;
    private TextView toolbarTitleTextView;
    private ImageView menuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_videos_listing_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        videosTabLayout = (TabLayout) findViewById(R.id.videosTabLayout);
        videosViewPager = (ViewPager) findViewById(R.id.videosViewPager);
        menuImageView = (ImageView) toolbar.findViewById(R.id.menuImageView);
        toolbarTitleTextView = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        setSupportActionBar(toolbar);

        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbarTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        videosTabLayout.addTab(videosTabLayout.newTab().setText(getString(R.string.all_videos_tabbar_momspresso_label)));
        videosTabLayout.addTab(videosTabLayout.newTab().setText(getString(R.string.all_videos_tabbar_funny_label)));
        AppUtils.changeTabsFont(this, videosTabLayout);
        AllVideosPagerAdapter adapter = new AllVideosPagerAdapter(getSupportFragmentManager(), videosTabLayout.getTabCount());
        videosViewPager.setAdapter(adapter);
        videosViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(videosTabLayout));
        videosTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                videosViewPager.setCurrentItem(tab.getPosition());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
