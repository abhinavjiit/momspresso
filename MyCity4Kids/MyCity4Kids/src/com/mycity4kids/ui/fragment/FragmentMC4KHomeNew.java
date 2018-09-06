package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.TrendingListingResponse;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.TrendingTopicsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/5/17.
 */
public class FragmentMC4KHomeNew extends BaseFragment implements View.OnClickListener {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TrendingTopicsPagerAdapter adapter;

    private ArrayList<TrendingListingResult> trendingArraylist;
    private String userId;
    private int lowerLimit = 1;
    private int upperLimit = 3;
    private int articleCount = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home_new, container, false);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        trendingArraylist = new ArrayList<>();
        hitTrendingDataAPI();
        return view;
    }

    public void hitTrendingDataAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI trendinAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<TrendingListingResponse> trendingCall = trendinAPI.getTrendingTopicAndArticles("" + lowerLimit, "" + upperLimit, "" + articleCount, SharedPrefUtils.getLanguageFilters(getActivity()));
        trendingCall.enqueue(trendingResponseCallback);
    }

    private Callback<TrendingListingResponse> trendingResponseCallback = new Callback<TrendingListingResponse>() {
        @Override
        public void onResponse(Call<TrendingListingResponse> call, retrofit2.Response<TrendingListingResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                TrendingListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processTrendingResponse(responseData);
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
        public void onFailure(Call<TrendingListingResponse> call, Throwable t) {
//            forYourSection.setProgressBarVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private void processTrendingResponse(TrendingListingResponse responseData) {
        trendingArraylist.addAll(responseData.getData().get(0).getResult());
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        String allCategoryLabel = "";
        if (isAdded()) {
            allCategoryLabel = getString(R.string.all_categories_label);
        } else {
            allCategoryLabel = "All";
        }
        tabLayout.addTab(tabLayout.newTab().setText(allCategoryLabel));
        for (int i = 0; i < trendingArraylist.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(trendingArraylist.get(i).getDisplay_name()));
        }
//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                // don't forget to add Tab first before measuring..
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int widthS = displayMetrics.widthPixels;
//                tabLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                int widthT = tabLayout.getMeasuredWidth();
//
//                if (widthS > widthT) {
//                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
//                    tabLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT));
//                }
//            }
//        });
        AppUtils.changeTabsFont(getActivity(), tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new TrendingTopicsPagerAdapter
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
                FragmentManager fm = getChildFragmentManager();
                Fragment f = (Fragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
                if (f != null) {
                    if (tab.getPosition() == 0) {
                        TrendingTopicsAllTabFragment fragment = (TrendingTopicsAllTabFragment) f;
                        if (null != fragment.getRecyclerView())
                            fragment.getRecyclerView().smoothScrollToPosition(0);
                    } else {
                        TrendingTopicsTabFragment fragment = (TrendingTopicsTabFragment) f;
                        if (null != fragment.getRecyclerView())
                            fragment.getRecyclerView().smoothScrollToPosition(0);
                    }
                }
            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

    }

    private void updateUnreadNotificationCount() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);

        Call<NotificationCenterListResponse> filterCall = notificationsAPI.getNotificationCenterList(userId, 1, "");
        filterCall.enqueue(unreadNotificationCountResponseCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUnreadNotificationCount();
    }

    private Callback<NotificationCenterListResponse> unreadNotificationCountResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }

            try {
                NotificationCenterListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().getResult().get(0).getId().equals(SharedPrefUtils.getLastNotificationIdForUnreadFlag(BaseApplication.getAppContext()))) {
                        ((DashboardActivity) getActivity()).showHideNotificationCenterMark(false);
                    } else {
                        ((DashboardActivity) getActivity()).showHideNotificationCenterMark(true);
                    }
//                    ((DashboardActivity) getActivity()).updateUnreadNotificationCount(responseData.getData().getTotal());
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

    public void hideFollowTopicHeader() {
        if (adapter != null)
            adapter.hideFollowTopicHeader();
    }
}
