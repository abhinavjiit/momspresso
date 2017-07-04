package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.TrendingListingResponse;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.TrendingTopicsPagerAdapter;
import com.mycity4kids.utils.ArrayAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/5/17.
 */
public class FragmentMC4KHomeNew extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};

    private LayoutInflater mInflator;
    private View view;
    private TabLayout tabLayout;

    private ArrayList<TrendingListingResult> trendingArraylist;
    private HashMap<Topics, List<Topics>> topicsMap;
    private String userId;
    private int lowerLimit = 1;
    private int upperLimit = 10;
    private int articleCount = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home_new, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        trendingArraylist = new ArrayList<>();
        hitTrendingDataAPI();
        return view;
    }

    public void hitTrendingDataAPI() {
//        String url = AppConstants.LIVE_URL + "v1/categories/trending/" + lowerLimit + "/" + upperLimit + "/" + articleCount;
//        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, true);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI trendinAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<TrendingListingResponse> trendingCall = trendinAPI.getTrendingTopicAndArticles("" + lowerLimit, "" + upperLimit, "" + articleCount);
        trendingCall.enqueue(trendingResponseCallback);

    }

    private Callback<TrendingListingResponse> trendingResponseCallback = new Callback<TrendingListingResponse>() {
        @Override
        public void onResponse(Call<TrendingListingResponse> call, retrofit2.Response<TrendingListingResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                TrendingListingResponse responseData = (TrendingListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processTrendingResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<TrendingListingResponse> call, Throwable t) {
//            forYourSection.setProgressBarVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private void changeTabsFont() {
        //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/androidnation.ttf");
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + "oswald_regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                }
            }
        }
    }

    private void processTrendingResponse(TrendingListingResponse responseData) {
        trendingArraylist.addAll(responseData.getData().get(0).getResult());
        Collections.shuffle(trendingArraylist);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        for (int i = 0; i < trendingArraylist.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(trendingArraylist.get(i).getDisplay_name()));
        }
        changeTabsFont();
        wrapTabIndicatorToTitle(tabLayout, 25, 25);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        final TrendingTopicsPagerAdapter adapter = new TrendingTopicsPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount(), trendingArraylist);
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

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
//            blogProgessBar.setVisibility(View.GONE);
//            trendingSection.setProgressBarVisibility(View.GONE);
            Log.d("Response back =", " " + response.getResponseBody());
            if (isError) {
                if (null != getActivity() && response.getResponseCode() != 999)
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            } else {
                Log.d("Response = ", response.getResponseBody());
                String temp = "";
                if (response == null) {
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                TrendingListingResponse responseBlogData;
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    responseBlogData = gson.fromJson(response.getResponseBody(), TrendingListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    //clear list to avoid duplicates due to volley caching
                    trendingArraylist.clear();
                    trendingArraylist.addAll(responseBlogData.getData().get(0).getResult());

                    tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                    for (int i = 0; i < trendingArraylist.size(); i++) {
                        tabLayout.addTab(tabLayout.newTab().setText(trendingArraylist.get(i).getDisplay_name()));
                    }
                    wrapTabIndicatorToTitle(tabLayout, 25, 25);
                    final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
                    final TrendingTopicsPagerAdapter adapter = new TrendingTopicsPagerAdapter
                            (getFragmentManager(), tabLayout.getTabCount(), trendingArraylist);
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

//                    trendingSection.setmDatalist(mArticleDataListing, Constants.KEY_TRENDING, "Home Screen");
                } else {
//                    trendingSection.setEmptyListLabelVisibility(View.VISIBLE);
                }
            }
        }
    };

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        setMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        setMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        setMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void setMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

//    private void hitForYouListingApi() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        RecommendationAPI foryouAPI = retrofit.create(RecommendationAPI.class);
//
//        Call<ArticleListingResponse> filterCall = foryouAPI.getRecommendedArticlesList("" + userId, 10, "", SharedPrefUtils.getLanguageFilters(getActivity()));
//        filterCall.enqueue(forYouResponseCallback);
//    }
//
//    public void hitBlogListingApi() {
//        trendingSection.setProgressBarVisibility(View.VISIBLE);
//        String url;
//        url = AppConstants.LIVE_URL + "v1/articles/trending/" + from + "/" + to + "?lang=" + SharedPrefUtils.getLanguageFilters(getActivity());
//        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, false);
//
//    }
//
//    private void hitMomspressoListingApi(String momspressoCategoryId) {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
//        if (momspressoCategoryId == null) {
//            momspressoCategoryId = AppConstants.MOMSPRESSO_CATEGORYID;
//        }
//        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(momspressoCategoryId, 0, 1, 10, SharedPrefUtils.getLanguageFilters(getActivity()));
//        filterCall.enqueue(momspressoListingResponseCallback);
//    }
//
//    private void hitFunnyVideosListingApi() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
//        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 9, 0, 3);
//        callRecentVideoArticles.enqueue(funnyVideosResponseCallback);
//    }
//
//    private void hitEditorPicksListingApi() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
//        int end = AppUtils.randInt(AppConstants.EDITOR_PICKS_MIN_ARTICLES + 1, AppConstants.EDITOR_PICKS_ARTICLE_COUNT + 1);
//        int start = end - AppConstants.EDITOR_PICKS_MIN_ARTICLES;
//        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(AppConstants.EDITOR_PICKS_CATEGORY_ID, 0, start, end, SharedPrefUtils.getLanguageFilters(getActivity()));
//        filterCall.enqueue(editorPicksResponseCallback);
//    }
//
//    private void hitHindiArticlesListing() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
//        LanguageConfigModel hindiLangModel = AppUtils.getLangModelForLanguage(getActivity(), AppConstants.LANG_KEY_HINDI);
//        if (null != hindiLangModel && !StringUtils.isNullOrEmpty(hindiLangModel.getId())) {
//            Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(hindiLangModel.getId(), 0, 1, 10, "");
//            filterCall.enqueue(hindiArticlesListingResponseCallback);
//        } else {
//            languageSection.setVisibility(View.GONE);
//        }
//    }
//
//    public void hitInYourCityListingApi() {
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
//
//        Call<ArticleListingResponse> filterCall = topicsAPI.getBestArticlesForCity("" + SharedPrefUtils.getCurrentCityModel(getActivity()).getId(), sortType, 1, 10, SharedPrefUtils.getLanguageFilters(getActivity()));
//        filterCall.enqueue(inYourCityListingResponseCallback);
//    }
//
//    private Callback<ArticleListingResponse> forYouResponseCallback = new Callback<ArticleListingResponse>() {
//        @Override
//        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            forYourSection.setProgressBarVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processForYouResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
//            forYourSection.setProgressBarVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
//        @Override
//        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
////            blogProgessBar.setVisibility(View.GONE);
//            trendingSection.setProgressBarVisibility(View.GONE);
//            Log.d("Response back =", " " + response.getResponseBody());
//            if (isError) {
//                if (null != getActivity() && response.getResponseCode() != 999)
//                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//            } else {
//                Log.d("Response = ", response.getResponseBody());
//                String temp = "";
//                if (response == null) {
//                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                    removeProgressDialog();
//                    return;
//                }
//
//                ArticleListingResponse responseBlogData;
//                try {
//                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
//                    responseBlogData = gson.fromJson(response.getResponseBody(), ArticleListingResponse.class);
//                } catch (JsonSyntaxException jse) {
//                    Crashlytics.logException(jse);
//                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
//                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                    removeProgressDialog();
//                    return;
//                }
//
//                if (responseBlogData.getCode() == Constants.HTTP_RESPONSE_SUCCESS) {
//                    //clear list to avoid duplicates due to volley caching
//                    mArticleDataListing.clear();
//                    mArticleDataListing.addAll(responseBlogData.getData().get(0).getResult());
//                    trendingSection.setmDatalist(mArticleDataListing, Constants.KEY_TRENDING, "Home Screen");
//                } else {
//                    trendingSection.setEmptyListLabelVisibility(View.VISIBLE);
//                }
//            }
//        }
//    };
//
//    private Callback<ArticleListingResponse> momspressoListingResponseCallback = new Callback<ArticleListingResponse>() {
//        @Override
//        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processMomspressoListingResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private Callback<ArticleListingResponse> hindiArticlesListingResponseCallback = new Callback<ArticleListingResponse>() {
//        @Override
//        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processHindiArticlesListingResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private Callback<VlogsListingResponse> funnyVideosResponseCallback = new Callback<VlogsListingResponse>() {
//        @Override
//        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                VlogsListingResponse responseData = (VlogsListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processFunnyVideosResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private Callback<ArticleListingResponse> editorPicksResponseCallback = new Callback<ArticleListingResponse>() {
//        @Override
//        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            editorPicksSection.setProgressBarVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processEditorPicksResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
////            forYouProgressbar.setVisibility(View.GONE);
//            editorPicksSection.setProgressBarVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private Callback<ArticleListingResponse> inYourCityListingResponseCallback = new Callback<ArticleListingResponse>() {
//        @Override
//        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            inYourCitySection.setProgressBarVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
//                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    processInYourCityListingResponse(responseData);
//                } else {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                if (getActivity() != null && isAdded()) {
//                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
//            inYourCitySection.setProgressBarVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };
//
//    private void processForYouResponse(ArticleListingResponse responseData) {
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            forYourSection.setEmptyListLabelVisibility(View.VISIBLE);
//        } else {
//            mArticleForYouListing.clear();
//            for (int i = 0; i < responseData.getData().get(0).getResult().size(); i++) {
//                if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().get(i).getId())) {
//                    mArticleForYouListing.add(responseData.getData().get(0).getResult().get(i));
//                }
//            }
////            mArticleForYouListing.addAll(responseData.getData().get(0).getResult());
//            forYourSection.setmDatalist(mArticleForYouListing, Constants.KEY_FOR_YOU, "Home Screen");
//        }
//    }
//
//    private void processMomspressoListingResponse(ArticleListingResponse responseData) {
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            momspressoSection.setEmptyListLabelVisibility(View.VISIBLE);
//        } else {
//            mMomspressoArticleListing.clear();
//            mMomspressoArticleListing.addAll(responseData.getData().get(0).getResult());
//            momspressoSection.setmDatalist(mMomspressoArticleListing, Constants.KEY_MOMSPRESSO, "Home Screen");
//        }
//    }
//
//    private void processHindiArticlesListingResponse(ArticleListingResponse responseData) {
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            languageSection.setEmptyListLabelVisibility(View.VISIBLE);
//        } else {
//            hindiArticlesListing.clear();
//            hindiArticlesListing.addAll(responseData.getData().get(0).getResult());
//            languageSection.setmDatalist(hindiArticlesListing, Constants.KEY_HINDI, "Home Screen");
//        }
//    }
//
//    private void processFunnyVideosResponse(VlogsListingResponse responseData) {
//        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            funnyVideosSection.setEmptyListLabelVisibility(View.VISIBLE);
//        } else {
//            funnyVideosListing.clear();
//            funnyVideosListing.addAll(responseData.getData().get(0).getResult());
//            funnyVideosSection.setVlogslist(funnyVideosListing, "dashboard", "Home Screen");
//        }
//    }
//
//    private void processEditorPicksResponse(ArticleListingResponse responseData) {
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            editorPicksSection.setEmptyListLabelVisibility(View.VISIBLE);
//        } else {
//            mArticleEditorPicksListing.clear();
//            mArticleEditorPicksListing.addAll(responseData.getData().get(0).getResult());
//            editorPicksSection.setmDatalist(mArticleEditorPicksListing, Constants.KEY_EDITOR_PICKS, "Home Screen");
//        }
//    }
//
//    private void processInYourCityListingResponse(ArticleListingResponse responseData) {
//        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
//        if (dataList.size() == 0) {
//            inYourCitySection.setEmptyListLabelVisibility(View.VISIBLE);
//
//        } else {
//            inYourCitySection.setEmptyListLabelVisibility(View.GONE);
//            mArticleBestCityListing.clear();
//            mArticleBestCityListing.addAll(responseData.getData().get(0).getResult());
//            inYourCitySection.setmDatalist(mArticleBestCityListing, Constants.KEY_IN_YOUR_CITY + "~" + SharedPrefUtils.getCurrentCityModel(getActivity()).getName(), "Home Screen");
//        }
//    }

//    private void hitBusinessListingApiRetro(int categoryId, int page) {
//        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
//            return;
//        }
//
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        EventsAPI topicsAPI = retrofit.create(EventsAPI.class);
//        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
//        double _latitude = getCurrentLocation.getLatitude();
//        double _longitude = getCurrentLocation.getLongitude();
//
//        Call<BusinessListResponse> filterCall = topicsAPI.getEventList("" + (SharedPrefUtils.getCurrentCityModel(getActivity())).getId(), "" + categoryId,
//                "" + _latitude, "" + _longitude, "", SharedPrefUtils.getUserDetailModel(getActivity()).getId(), 1);
//        filterCall.enqueue(eventListingResponseCallback);
//    }
//
//    private Callback<BusinessListResponse> eventListingResponseCallback = new Callback<BusinessListResponse>() {
//        @Override
//        public void onResponse(Call<BusinessListResponse> call, retrofit2.Response<BusinessListResponse> response) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            if (response == null || response.body() == null) {
//                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
//                return;
//            }
//
//            try {
//                progressBar.setVisibility(View.GONE);
//                BusinessListResponse responseData = (BusinessListResponse) response.body();
//                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
//
//                    mBusinessListCount = responseData.getResult().getData().getTotal();
//                    mTotalPageCount = responseData.getResult().getData().getPage_count();
//                    //to add in already created list
//                    // we neew to clear this list in case of sort by and filter
//                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());
//
//                    BaseApplication.setBusinessREsponse(mBusinessDataListings);
//
//                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
//
//                    businessAdapter.notifyDataSetChanged();
//                    inflateEventCardsScroll();
//
//                    if (mBusinessDataListings.isEmpty()) {
//                        ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
//                    }
//                    baseScroll.smoothScrollTo(0, 0);
//
//                } else if (responseData.getResponseCode() == 400) {
//                    ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
//                }
//            } catch (Exception e) {
//                Crashlytics.logException(e);
//                Log.d("MC4KException", Log.getStackTraceString(e));
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<BusinessListResponse> call, Throwable t) {
////            momspressoProgressbar.setVisibility(View.GONE);
//            Crashlytics.logException(t);
//            Log.d("MC4KException", Log.getStackTraceString(t));
//            if (null != getActivity()) {
//                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
//            }
//        }
//    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

//            case R.id.go_to_events:
//            case R.id.img_go_to_events:
//            case R.id.txtEvents:
//                Constants.IS_SEARCH_LISTING = false;
//                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
//                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
//                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
//                fragment.setArguments(bundle);
//                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
//                break;
        }

    }

//    public void refreshList() throws ParseException {
//        if (SharedPrefUtils.isChangeCity(getActivity()) && SharedPrefUtils.getCurrentCityModel(getActivity()).getId() != AppConstants.OTHERS_CITY_ID) {
//            inYourCitySection.setVisibility(View.VISIBLE);
//            view.findViewById(R.id.eventsss).setVisibility(View.VISIBLE);
//            hitInYourCityListingApi();
//            inYourCitySection.setCityName(SharedPrefUtils.getCurrentCityModel(getActivity()).getName().toUpperCase());
//            SharedPrefUtils.setChangeCityFlag(getActivity(), false);
//            if (mBusinessDataListings == null) {
//                mBusinessDataListings = new ArrayList<>();
//            }
//            mBusinessDataListings.clear();
//            BaseApplication.setBusinessREsponse(mBusinessDataListings);
//            businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
//            businessAdapter.notifyDataSetChanged();
//            hzScrollLinearLayoutEvent.removeAllViews();
//            hitBusinessListingApiRetro(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
//        } else if (SharedPrefUtils.getCurrentCityModel(getActivity()).getId() == AppConstants.OTHERS_CITY_ID) {
//            inYourCitySection.setVisibility(View.GONE);
//            view.findViewById(R.id.eventsss).setVisibility(View.GONE);
//        }
//
//        if (BaseApplication.isHasLanguagePreferrenceChanged()) {
//            hitForYouListingApi();
//            hitBlogListingApi();
//            hitEditorPicksListingApi();
//            hitInYourCityListingApi();
//            BaseApplication.setHasLanguagePreferrenceChanged(false);
//        }
//
//        updateUnreadNotificationCount();
//        Calendar calendar = Calendar.getInstance();
//        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            appointmentListData = getAppointmentByDay(formatter.format(calendar.getTime()));
//            // first change according to time
//            appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);
//            adapterHomeAppointment.notifyList(appointmentListData);
//            businessAdapter.refreshEventIdList();
//            businessAdapter.notifyDataSetChanged();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void updateUnreadNotificationCount() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);

        Call<NotificationCenterListResponse> filterCall = notificationsAPI.getUnreadNotificationCount(userId);
        filterCall.enqueue(unreadNotificationCountResponseCallback);
    }

//    public void inflateEventCardsScroll() {
//        for (int i = 0; i < Math.min(10, mBusinessDataListings.size()); i++) {
//            final View view = mInflator.inflate(R.layout.card_item_event_dashboard, null);
//            view.setTag(i);
//            ImageView articleImage = (ImageView) view.findViewById(R.id.eventThumbnail);
//            TextView title = (TextView) view.findViewById(R.id.title);
//            TextView ageGroup = (TextView) view.findViewById(R.id.ageGroup);
//            TextView address = (TextView) view.findViewById(R.id.addresstxt);
//            TextView durationtxt = (TextView) view.findViewById(R.id.durationtxt);
//            TextView category = (TextView) view.findViewById(R.id.category);
//            final ImageView call = (ImageView) view.findViewById(R.id.call);
//            ImageView addEvent = (ImageView) view.findViewById(R.id.addEvent);
//            cardView = (CardView) view.findViewById(R.id.cardViewWidget);
//            LinearLayout middleContainer = (LinearLayout) view.findViewById(R.id.middleContainer);
//            LinearLayout lowerContainer = (LinearLayout) view.findViewById(R.id.lowerContainer);
//            Picasso.with(getActivity()).load(mBusinessDataListings.get(i).getThumbnail()).placeholder(R.drawable.thumbnail_eventsxxhdpi).into(articleImage);
//            title.setText(mBusinessDataListings.get(i).getName());
//            ageGroup.setText(mBusinessDataListings.get(i).getAgegroup_text() + " years");
//            address.setText(mBusinessDataListings.get(i).getLocality());
//            final Calendar cal = Calendar.getInstance();
//            cal.setTime(DateTimeUtils.stringToDate(mBusinessDataListings.get(i).getStart_date()));
//            int startmonth = cal.get(Calendar.MONTH);
//            int startDay = cal.get(Calendar.DAY_OF_MONTH);
//            String orgmnth = getMonth(startmonth);
//            String startDaystr = String.valueOf(startDay);
//            String orgmnthstr = String.valueOf(orgmnth);
//            Calendar cal1 = Calendar.getInstance();
//            cal1.setTime(DateTimeUtils.stringToDate(mBusinessDataListings.get(i).getEnd_date()));
//            int startmonth1 = cal1.get(Calendar.MONTH);
//            int startDay1 = cal1.get(Calendar.DAY_OF_MONTH);
//            String orgmnth1 = getMonth(startmonth1);
//            String startDaystr1 = String.valueOf(startDay1);
//            String orgmnthstr1 = String.valueOf(orgmnth1);
//            durationtxt.setText(startDaystr + " " + orgmnthstr + "-" + startDaystr1 + " " + orgmnthstr1);
//            category.setText(mBusinessDataListings.get(i).getActivities());
//            final int finalI = i;
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent5 = new Intent(getActivity(), BusinessDetailsActivity.class);
//                    String businessId = mBusinessDataListings.get(finalI).getId();
//                    intent5.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
//                    intent5.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
//                    intent5.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
//                    intent5.putExtra(Constants.DISTANCE, mBusinessDataListings.get(finalI).getDistance());
//                    startActivity(intent5);
//
//                }
//            });
//            eventPosition = i;
//            addEvent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
//                                != PackageManager.PERMISSION_GRANTED
//                                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            Log.i("PERMISSIONS", "Calendar permissions has NOT been granted. Requesting permissions.");
//                            requestCalendarPermissions();
//                        } else {
//                            addCalendarEvent(eventPosition);
//                        }
//                    } else {
//                        addCalendarEvent(eventPosition);
//                    }
//                }
//            });
//            final int finalI2 = i;
//            call.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String[] telList = null;
//                    if (null != mBusinessDataListings.get(finalI2).getPhone()) {
//                        telList = mBusinessDataListings.get(finalI2).getPhone().split("/");
//                    }
//                    if (telList == null || telList.length == 0) {
//                        call.setVisibility(View.GONE);
//                    } else {
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telList[0]));
//                        Utils.pushEvent(getActivity(), GTMEventType.CALL_RESOURCES_CLICKED_EVENT, userId + "", "Dashboard");
//                        startActivity(intent);
//                    }
//
//                }
//            });
//
//            hzScrollLinearLayoutEvent.addView(view);
//        }
//        View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
//        DisplayMetrics metrics = new DisplayMetrics();
//        if (getActivity() != null) {
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int widthPixels = metrics.widthPixels;
//            float width = (float) (widthPixels * 0.45);
//            customViewMore.setMinimumWidth((int) width);
//        }
//        hzScrollLinearLayoutEvent.addView(customViewMore);
//        customViewMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Constants.IS_SEARCH_LISTING = false;
//                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
//                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
//                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
//                fragment.setArguments(bundle);
//                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
//            }
//        });
//    }

//    private void requestCalendarPermissions() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                Manifest.permission.READ_CALENDAR) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                Manifest.permission.WRITE_CALENDAR)) {
//            Log.i("Permissions",
//                    "Displaying get accounts permission rationale to provide additional context.");
//
//            // Display a SnackBar with an explanation and a button to trigger the request.
//            Snackbar.make(baseScroll, R.string.permission_calendar_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            requestUngrantedPermissions();
//                        }
//                    })
//                    .show();
//        } else {
//            requestUngrantedPermissions();
//        }
//    }
//
//    private void requestUngrantedPermissions() {
//        ArrayList<String> permissionList = new ArrayList<>();
//        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
//            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
//                permissionList.add(PERMISSIONS_INIT[i]);
//            }
//        }
//        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
//        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        if (requestCode == REQUEST_INIT_PERMISSION) {
//            Log.i("Permissions", "Received response for storage permissions request.");
//
//            if (PermissionUtil.verifyPermissions(grantResults)) {
//                Snackbar.make(baseScroll, R.string.permision_available_init,
//                        Snackbar.LENGTH_SHORT)
//                        .show();
//                addCalendarEvent(eventPosition);
//            } else {
//                Log.i("Permissions", "storage permissions were NOT granted.");
//                Snackbar.make(baseScroll, R.string.permissions_not_granted,
//                        Snackbar.LENGTH_SHORT)
//                        .show();
//            }
//
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    private void addCalendarEvent(int finalI1) {
//        if (mBusinessDataListings.get(finalI1).isEventAdded()) {
//
//            ToastUtils.showToast(getActivity(), getActivity().getResources().getString(R.string.event_added));
//        } else {
//            final BusinessDataListing information = mBusinessDataListings.get(finalI1);
//            new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
//                    .setTitle("Add Event to calendar")
//                    .setMessage("Do you want add this event to you personal calendar?")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // continue with delete
//                            dialog.dismiss();
//                            //  onButtonClicked.onButtonCLick(0);
//                            saveCalendar(information.getName(), information.getDescription(), information.getStart_date(), information.getEnd_date(), information.getLocality());
//                            ToastUtils.showToast(getActivity(), "Successfully added to Calendar");
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                            dialog.dismiss();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//        }
//    }
//
//    private void saveCalendar(String title, String desc, String sDate, String eDate, String location) {
//
//        ContentResolver cr = (getActivity()).getContentResolver();
//        ContentValues values = new ContentValues();
//
//        values.put(CalendarContract.Events.DTSTART, "" + DateTimeUtils.getTimestampFromStringDate(sDate));
//        values.put(CalendarContract.Events.DTEND, "" + DateTimeUtils.getTimestampFromStringDate(eDate));
//        values.put(CalendarContract.Events.TITLE, title);
//        values.put(CalendarContract.Events.DESCRIPTION, desc);
//        values.put(CalendarContract.Events.EVENT_LOCATION, location);
//
//        TimeZone timeZone = TimeZone.getDefault();
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
//
//        // default calendar
//        values.put(CalendarContract.Events.CALENDAR_ID, 3);
//        values.put(CalendarContract.Events.HAS_ALARM, 1);
//
//        // insert event to calendar
//        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
//    }

    private Callback<NotificationCenterListResponse> unreadNotificationCountResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                NotificationCenterListResponse responseData = (NotificationCenterListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ((DashboardActivity) getActivity()).updateUnreadNotificationCount(responseData.getData().getTotal());
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };
}
