package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
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
import com.mycity4kids.utils.GroupIdCategoryMap;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/5/17.
 */
public class FragmentMC4KHomeNew extends BaseFragment implements View.OnClickListener, GroupIdCategoryMap.GroupCategoryInterface {

    private static final String HOME_PAGE_FEED_ORDER = "home_page_feed_order";

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TrendingTopicsPagerAdapter adapter;

    private ArrayList<TrendingListingResult> trendingArraylist;
    private String userId;
    private int lowerLimit = 1;
    private int upperLimit = 3;
    private int articleCount = 10;
    private String gpHeading, gpSubHeading, gpImageUrl;
    private int groupId;
    private String[] feedOrderArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home_new, container, false);

        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        trendingArraylist = new ArrayList<>();
        hitTrendingDataAPI();
        return view;
    }

    private void getGroupIdForCurrentCategory() {
        GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap("", this, "listing");
        groupIdCategoryMap.getGroupIdForCurrentCategory();
    }

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading, String gpImageUrl) {
        this.groupId = groupId;
        this.gpHeading = gpHeading;
        this.gpSubHeading = gpSubHeading;
        this.gpImageUrl = gpImageUrl;
        hitTrendingDataAPI();
    }

    private void hitTrendingDataAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI trendinAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<TrendingListingResponse> trendingCall = trendinAPI.getTrendingTopicAndArticles("" + lowerLimit, "" + upperLimit, "" + articleCount, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        trendingCall.enqueue(trendingResponseCallback);
    }

    private Callback<TrendingListingResponse> trendingResponseCallback = new Callback<TrendingListingResponse>() {
        @Override
        public void onResponse(Call<TrendingListingResponse> call, retrofit2.Response<TrendingListingResponse> response) {
            if (response.body() == null) {
                if (isAdded())
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                TrendingListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processTrendingResponse(responseData);
                } else {
                    if (isAdded())
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
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private void processTrendingResponse(TrendingListingResponse responseData) {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        feedOrderArray = mFirebaseRemoteConfig.getString(HOME_PAGE_FEED_ORDER).split(",");
        for (String s : feedOrderArray) {
            switch (s) {
                case Constants.KEY_TRENDING:
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.home_screen_trending_title)));
                    break;
                case Constants.KEY_TODAYS_BEST:
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.article_listing_toolbar_title_todays_best)));
                    break;
                case Constants.KEY_EDITOR_PICKS:
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.article_listing_toolbar_title_editor_picks)));
                    break;
                case Constants.KEY_FOR_YOU:
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.article_listing_toolbar_title_for_you)));
                    break;
                case Constants.KEY_RECENT:
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.article_listing_toolbar_title_recent)));
                    break;
            }
        }

        AppUtils.changeTabsFont(tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new TrendingTopicsPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount(), feedOrderArray);
        adapter.setGroupInfo(gpHeading, gpSubHeading, gpImageUrl, groupId);
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
                viewPager.setCurrentItem(tab.getPosition());
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
            if (response.body() == null) {
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
