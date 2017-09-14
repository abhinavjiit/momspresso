package com.mycity4kids.ui.fragment;

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
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
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

    private ArrayList<TrendingListingResult> trendingArraylist;
    private String userId;
    private int lowerLimit = 1;
    private int upperLimit = 3;
    private int articleCount = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home_new, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

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
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
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
//        Collections.shuffle(trendingArraylist);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        tabLayout.addTab(tabLayout.newTab().setText("All"));
        for (int i = 0; i < trendingArraylist.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(trendingArraylist.get(i).getDisplay_name()));
        }
        AppUtils.changeTabsFont(getActivity(), tabLayout);
//        wrapTabIndicatorToTitle(tabLayout, 25, 25);
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

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

    }

    private void updateUnreadNotificationCount() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);

        Call<NotificationCenterListResponse> filterCall = notificationsAPI.getUnreadNotificationCount(userId);
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
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                NotificationCenterListResponse responseData = response.body();
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
