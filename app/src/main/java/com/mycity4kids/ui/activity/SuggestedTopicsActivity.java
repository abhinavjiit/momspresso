package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.SuggestedTopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.SuggestedTopicsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsActivity extends BaseActivity {

    private TabLayout languagesTabLayout;
    private ArrayList<String> languageNameList = new ArrayList<>();
    private ViewPager languagesViewPager;
    private ArrayList<String> languageKeyList;
    private ArrayList<String> languageTagList;
    private Toolbar toolbar;
    int tabPosition = 0;
    String lang;
    private LinearLayout root;

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggested_topics_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "SuggestedTopicScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        languagesTabLayout = (TabLayout) findViewById(R.id.languagesTabLayout);
        languagesViewPager = (ViewPager) findViewById(R.id.languagesViewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSuggestedTopics();
    }

    private void getSuggestedTopics() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI suggestedTopicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<SuggestedTopicsResponse> suggestedTopicsCAll = suggestedTopicsAPI.getSuggestedTopics("0,1,2,3,4,5,6,7,8");
        suggestedTopicsCAll.enqueue(suggestedTopicsResponseCallback);
    }

    private Callback<SuggestedTopicsResponse> suggestedTopicsResponseCallback = new Callback<SuggestedTopicsResponse>() {
        @Override
        public void onResponse(Call<SuggestedTopicsResponse> call, retrofit2.Response<SuggestedTopicsResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                SuggestedTopicsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    populateLanguagesTabs(responseData);
                    getUserPublishedArticles();
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }

        }

        @Override
        public void onFailure(Call<SuggestedTopicsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void getUserPublishedArticles() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);
        final Call<ArticleListingResponse> call = userpublishedArticlesAPI.getAuthorsPublishedArticles(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), 0, 1, 1);
        call.enqueue(userPublishedArticleResponseListener);
    }

    private Callback<ArticleListingResponse> userPublishedArticleResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processPublisedArticlesResponse(responseData);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

        } else {
            for (int i = 0; i < languageKeyList.size(); i++) {
                if (languageKeyList.get(i).equals(dataList.get(0).getLang())) {
                    languagesViewPager.setCurrentItem(i);
                    break;
                }
            }
        }

    }

    private void populateLanguagesTabs(SuggestedTopicsResponse response) {
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            ArrayList<ArrayList<String>> languageConfigModelArrayList = new ArrayList<>();
            languageTagList = new ArrayList<>();
            languageKeyList = new ArrayList<>();
            if (response.getData().get(0).getResult().get("0") != null && !response.getData().get(0).getResult().get("0").isEmpty()) {
                languagesTabLayout.addTab(languagesTabLayout.newTab().setText("ENGLISH"));
                languageNameList.add("ENGLISH");
                languageConfigModelArrayList.add(response.getData().get(0).getResult().get("0"));
                languageKeyList.add("0");
            }
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                if (response.getData().get(0).getResult().get(entry.getKey()) != null && !response.getData().get(0).getResult().get(entry.getKey()).isEmpty()) {
                    languagesTabLayout.addTab(languagesTabLayout.newTab().setText(entry.getValue().getDisplay_name().toUpperCase()));
                    languageNameList.add(entry.getValue().getDisplay_name().toUpperCase());
                    languageConfigModelArrayList.add(response.getData().get(0).getResult().get(entry.getKey()));
                    languageKeyList.add(entry.getKey());
                    languageTagList.add(entry.getValue().getTag());
                }
            }
            lang = SharedPrefUtils.getAppLocale(this);
            for (int i = 0; i < languageTagList.size(); i++) {
                if (lang.equals(languageTagList.get(i))) {
                    tabPosition = i + 1;
                    break;
                }
            }

            AppUtils.changeTabsFont(languagesTabLayout);
            final SuggestedTopicsPagerAdapter adapter = new SuggestedTopicsPagerAdapter(getSupportFragmentManager(), languagesTabLayout.getTabCount(), languageConfigModelArrayList, languageNameList);
            languagesViewPager.setAdapter(adapter);

            languagesViewPager.setCurrentItem(tabPosition);

            languagesViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(languagesTabLayout));

          /*  lang = SharedPrefUtils.getAppLocale(this);
            for (int i = 0; i < retMap.size(); i++) {
                if (lang.equals(retMap.get(i).getTag())) {
                    tabPosition = i;
                }

            }*/


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
                    languagesViewPager.setCurrentItem(tab.getPosition());

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
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}