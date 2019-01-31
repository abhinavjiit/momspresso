package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activities_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        String authorId = getIntent().getStringExtra(Constants.AUTHOR_ID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        boolean isPrivateProfile = false;
        if (authorId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
//            isPrivateProfile = true;
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_recommended)));
           // tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_bookmark)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_watch_later)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_comment)));
            adapter = new UserActivitiesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), authorId, true);
        } else {
//            isPrivateProfile = false;
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_recommended)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.user_activities_tabs_comment)));
            adapter = new UserActivitiesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), authorId, false);
        }
        AppUtils.changeTabsFont(this, tabLayout);
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


    @Override
    protected void updateUi(Response response) {

    }


}
