package com.mycity4kids.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.ui.adapter.LanguageSpecificArticlePagerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hemant on 4/7/17.
 */
public class LanguageSpecificArticleListingActivity extends BaseActivity {


    private Toolbar toolbar;
    private TabLayout languagesTabLayout;
    private ViewPager languagesViewPager;
    private ImageView menuImageView;
    private TextView toolbarTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_specific_list_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        languagesTabLayout = (TabLayout) findViewById(R.id.languagesTabLayout);
        languagesViewPager = (ViewPager) findViewById(R.id.languagesViewPager);
        menuImageView = (ImageView) toolbar.findViewById(R.id.menuImageView);
        toolbarTitleTextView = (TextView) toolbar.findViewById(R.id.toolbarTitle);

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

        setSupportActionBar(toolbar);

        populateLanguagesTabs();
    }

    private void populateLanguagesTabs() {
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            ArrayList<LanguageConfigModel> languageConfigModelArrayList = new ArrayList<>();
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                languagesTabLayout.addTab(languagesTabLayout.newTab().setText(entry.getValue().getDisplay_name().toUpperCase()));
                languageConfigModelArrayList.add(entry.getValue());
            }

            AppUtils.changeTabsFont(this, languagesTabLayout);
//            wrapTabIndicatorToTitle(tabLayout, 25, 25);
            final LanguageSpecificArticlePagerAdapter adapter = new LanguageSpecificArticlePagerAdapter
                    (getSupportFragmentManager(), languagesTabLayout.getTabCount(), languageConfigModelArrayList);
            languagesViewPager.setAdapter(adapter);
            languagesViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(languagesTabLayout));
            languagesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    languagesViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }
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
