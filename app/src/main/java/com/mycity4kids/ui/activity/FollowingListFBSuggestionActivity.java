package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.UsersFollowingPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowingListFBSuggestionActivity extends BaseActivity {

    private Toolbar toolbar;
    private RelativeLayout root;

    private String userId;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    UsersFollowingPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_list_fb_suggestion_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);

        userId = getIntent().getStringExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS);
        if (null == userId) {
            userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        }

        String selectedTab = getIntent().getStringExtra("selectedTab");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout.addTab(tabLayout.newTab().setText("FOLLOWING"));
        tabLayout.addTab(tabLayout.newTab().setText("SUGGESTIONS"));

        AppUtils.changeTabsFont(tabLayout);

        adapter = new UsersFollowingPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), userId, true);
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
        if ("listType=suggestionList".equals(selectedTab)) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.refreshFacebookData(requestCode, resultCode, data);
    }
}
