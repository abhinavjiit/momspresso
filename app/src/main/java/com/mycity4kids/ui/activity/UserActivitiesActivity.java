package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.UserActivitiesPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 3/8/17.
 */
public class UserActivitiesActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private UserActivitiesPagerAdapter adapter;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activities_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        String authorId = getIntent().getStringExtra(Constants.AUTHOR_ID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (authorId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_recommended)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_comment)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_watch_later)));

            adapter = new UserActivitiesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), authorId,
                    true);
        } else {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_recommended)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_comment)));
            adapter = new UserActivitiesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), authorId,
                    false);
        }
        AppUtils.changeTabsFont(tabLayout);
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
}
