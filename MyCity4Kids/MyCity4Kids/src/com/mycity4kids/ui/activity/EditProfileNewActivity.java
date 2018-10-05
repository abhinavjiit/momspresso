package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.ui.adapter.UserProfilePagerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EditProfileNewActivity extends AppCompatActivity {
    ViewPager viewPager;
    android.support.v7.widget.Toolbar toolbar;
    TabLayout tabLayout;
    UserProfilePagerAdapter viewPagerAdapter;
    NestedScrollView nestedScrollView;
    CoordinatorLayout mainprofileparentlayout;
    public ArrayList<CityInfoItem> mDatalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainprofile);

        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.id_toolbar);
//        mainprofileparentlayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
        tabLayout = (TabLayout) findViewById(R.id.id_tabs);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(0xFFFFFFFF);
        nestedScrollView.setFillViewport(true);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);

    }

    private UserDetailResult userDetails;
    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                userDetails = responseData.getData().get(0).getResult();

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
                Call<CityConfigResponse> cityCall = cityConfigAPI.getCityConfig();
                cityCall.enqueue(cityConfigResponseCallback);

//                if (responseData.getData().get(0).getResult().getKids() == null) {
//                } else {
//                    int position = 0;
//                    for (KidsModel km : responseData.getData().get(0).getResult().getKids()) {
//                        addKidView(km, position);
//                        position++;
//                    }
//
//                }
//                firstNameEditText.setText(responseData.getData().get(0).getResult().getFirstName());
//                emailTextView.setText(responseData.getData().get(0).getResult().getEmail());
//                lastNameEditText.setText(responseData.getData().get(0).getResult().getLastName());
//                blogTitleEditText.setText(responseData.getData().get(0).getResult().getBlogTitle());
//                describeSelfEditText.setText(responseData.getData().get(0).getResult().getUserBio());
//
//                if (null == responseData.getData().get(0).getResult().getPhone() || StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getPhone().getMobile())) {
//                } else {
//                    phoneEditText.setText(responseData.getData().get(0).getResult().getPhone().getMobile());
//                }
            } else {
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<CityConfigResponse> cityConfigResponseCallback = new Callback<CityConfigResponse>() {

        @Override
        public void onResponse(Call<CityConfigResponse> call, retrofit2.Response<CityConfigResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                CityConfigResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    mDatalist = new ArrayList<>();
                    MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(EditProfileNewActivity.this);
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                        if (AppConstants.OTHERS_NEW_CITY_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            if (currentCity.getName() != null && !"Others".equals(currentCity.getName()) && currentCity.getId() == AppConstants.OTHERS_CITY_ID) {
                                mDatalist.get(mDatalist.size() - 1).setCityName("Others(" + currentCity.getName() + ")");
                            }
                        }
                    }

                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCity.getId() == cId) {
                            mDatalist.get(i).setSelected(true);
//                            cityNameTextView.setText(mDatalist.get(i).getCityName());
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }

                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.about_txt)));
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.contact_txt)));

                    viewPagerAdapter = new UserProfilePagerAdapter(getSupportFragmentManager(), userDetails, mDatalist);
                    viewPager.setAdapter(viewPagerAdapter);

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


                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
//        setupUI(mainprofileparentlayout);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EditProfileNewActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}