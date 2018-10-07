package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.UserProfilePagerAdapter;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EditProfileNewActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    UserProfilePagerAdapter viewPagerAdapter;
    public ArrayList<CityInfoItem> mDatalist;
    private ImageView profileImageView;
    private UserDetailResult userDetails;
    private TextView saveTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainprofile);

        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.id_tabs);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        saveTextView = (TextView) findViewById(R.id.saveTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveTextView.setOnClickListener(this);
        try {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).into(profileImageView);
        } catch (Exception e) {
            profileImageView.setImageResource(R.drawable.family_xxhdpi);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);

    }

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

    private void saveUserDetails() {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        if (viewPager.getCurrentItem() == 0) {
            updateUserDetail.setUserBio(viewPagerAdapter.getAbout().getAboutEditText().getText().toString().trim() + "");
        } else {
            String[] nameArr = viewPagerAdapter.getContactdetails().getFullNameEditText().toString().split(" ");
            updateUserDetail.setFirstName((nameArr[0]));
            updateUserDetail.setBlogTitle(viewPagerAdapter.getContactdetails().getHandleNameEditText().getText().toString().trim() + "");

            if (nameArr.length < 2 || StringUtils.isNullOrEmpty(nameArr[1].trim())) {
                updateUserDetail.setLastName(" ");
            } else {
                updateUserDetail.setLastName(nameArr[1]);
            }

            if (StringUtils.isNullOrEmpty(viewPagerAdapter.getContactdetails().getPhoneEditText().getText().toString().trim())) {
                updateUserDetail.setMobile(" ");
            } else {
                updateUserDetail.setMobile(viewPagerAdapter.getContactdetails().getPhoneEditText().getText().toString().trim() + "");
            }

            if (viewPagerAdapter.getContactdetails().getSelectedCityId() != 0) {
                updateUserDetail.setCityId("" + viewPagerAdapter.getContactdetails().getSelectedCityId());
                updateUserDetail.setCityName("" + viewPagerAdapter.getContactdetails().getCurrentCityName());
            }
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(updateUserDetail);
        call.enqueue(userDetailsUpdateResponseListener);
    }

    private Callback<UserDetailResponse> userDetailsUpdateResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.went_wrong));
                return;
            }
            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                Toast.makeText(EditProfileNewActivity.this, getString(R.string.app_settings_edit_profile_update_success), Toast.LENGTH_SHORT).show();
                UserInfo model = SharedPrefUtils.getUserDetailModel(EditProfileNewActivity.this);
                String[] nameArr = viewPagerAdapter.getContactdetails().getFullNameEditText().toString().split(" ");
                model.setFirst_name(nameArr[0]);
                if (nameArr.length < 2 || StringUtils.isNullOrEmpty(nameArr[1].trim())) {
                    model.setLast_name(" ");
                } else {
                    model.setLast_name(nameArr[1]);
                }
                SharedPrefUtils.setUserDetailModel(EditProfileNewActivity.this, model);
            } else {
//                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveTextView:
                if (validateFields()) {
                    saveUserDetails();
//                    addCityDetails();
                }
                break;
        }
    }

    private boolean validateFields() {

        if (viewPager.getCurrentItem() == 0) {
            if (TextUtils.isEmpty(viewPagerAdapter.getAbout().getAboutEditText().getText())) {
                Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_user_bio_empty), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (StringUtils.isNullOrEmpty(viewPagerAdapter.getContactdetails().getFullNameEditText().getText().toString().trim())) {
                Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_fn_empty), Toast.LENGTH_SHORT).show();
                return false;
            } else if (StringUtils.isNullOrEmpty(viewPagerAdapter.getContactdetails().getHandleNameEditText().getText().toString().trim())) {
                Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_blog_title_empty), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

//    private void addCityDetails() {
//        UpdateUserDetailsRequest addCityAndKidsInformationRequest = new UpdateUserDetailsRequest();
////        addCityAndKidsInformationRequest.setKids(kidsModelArrayList);
//
//        if (selectedCityId != 0) {
//            addCityAndKidsInformationRequest.setCityId("" + selectedCityId);
//            addCityAndKidsInformationRequest.setCityName("" + currentCityName);
//        }
//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
//        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateCityAndKids(addCityAndKidsInformationRequest);
//        call.enqueue(addCityAndKidsResponseReceived);
//    }
}