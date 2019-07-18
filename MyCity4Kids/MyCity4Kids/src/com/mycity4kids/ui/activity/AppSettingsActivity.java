package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.adapter.AppSettingsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 19/7/17.
 */
public class AppSettingsActivity extends BaseActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout root;
    //private ImageView searchAllImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getApplication()).setActivity(this);

        setContentView(R.layout.app_settings_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        //  searchAllImageView = (ImageView) findViewById(R.id.searchAllImageView);
        //searchAllImageView.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//      tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.app_settings_tabbar_edit_profile)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.app_settings_tabbar_edit_prefs)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.app_settings_tabbar_lang_prefs)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.app_settings_tabbar_change_password)));
        AppUtils.changeTabsFont(this, tabLayout);
        AppSettingsPagerAdapter adapter = new AppSettingsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), "");
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
    protected void updateUi(Response response) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.searchAllImageView:
//                setNewLocale("hi", false);
                Intent searchIntent = new Intent(this, SearchAllActivity.class);
                searchIntent.putExtra(Constants.FILTER_NAME, "");
                searchIntent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(searchIntent);
                break;

        }*/
    }

//    private boolean setNewLocale(String language, boolean restartProcess) {
//        LocaleManager.setNewLocale(this, language);
//
//        Intent i = new Intent(this, SplashActivity.class);
//        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//
//        if (restartProcess) {
//            System.exit(0);
//        } else {
//            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show();
//        }
//        return true;
//    }
}
