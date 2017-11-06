package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.SuggestedTopicsResponse;
import com.mycity4kids.models.response.TrendingListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.LanguageSpecificArticlePagerAdapter;
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

public class SuggestedTopicsFragment extends BaseFragment {

    private TabLayout languagesTabLayout;
    private ViewPager languagesViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggested_topics_fragment, container, false);
        languagesTabLayout = (TabLayout) view.findViewById(R.id.languagesTabLayout);
        languagesViewPager = (ViewPager) view.findViewById(R.id.languagesViewPager);

        getSuggestedTopics();
        return view;
    }

    private void getSuggestedTopics() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI suggestedTopicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<SuggestedTopicsResponse> suggestedTopicsCAll = suggestedTopicsAPI.getSuggestedTopics("0,1,2,3,4,5,6");
        suggestedTopicsCAll.enqueue(suggestedTopicsResponseCallback);
    }

    private Callback<SuggestedTopicsResponse> suggestedTopicsResponseCallback = new Callback<SuggestedTopicsResponse>() {
        @Override
        public void onResponse(Call<SuggestedTopicsResponse> call, retrofit2.Response<SuggestedTopicsResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                SuggestedTopicsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    populateLanguagesTabs(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                if (isAdded())
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }

        }

        @Override
        public void onFailure(Call<SuggestedTopicsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void populateLanguagesTabs(SuggestedTopicsResponse response) {
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            ArrayList<ArrayList<String>> languageConfigModelArrayList = new ArrayList<>();
            if (response.getData().get(0).getResult().get("0") != null && !response.getData().get(0).getResult().get("0").isEmpty()) {
                languagesTabLayout.addTab(languagesTabLayout.newTab().setText("ENGLISH"));
                languageConfigModelArrayList.add(response.getData().get(0).getResult().get("0"));
            }

            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                if (response.getData().get(0).getResult().get(entry.getKey()) != null && !response.getData().get(0).getResult().get(entry.getKey()).isEmpty()) {
                    languagesTabLayout.addTab(languagesTabLayout.newTab().setText(entry.getValue().getDisplay_name().toUpperCase()));
                    languageConfigModelArrayList.add(response.getData().get(0).getResult().get(entry.getKey()));
                }
            }

            AppUtils.changeTabsFont(getActivity(), languagesTabLayout);
            final SuggestedTopicsPagerAdapter adapter = new SuggestedTopicsPagerAdapter(getChildFragmentManager(), languagesTabLayout.getTabCount(), languageConfigModelArrayList);
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
    protected void updateUi(Response response) {

    }
}
