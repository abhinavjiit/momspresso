package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 7/29/16.
 */
public class EditProfieActivity extends BaseActivity {

    private ProgressBar progress_bar;
    Toolbar mToolBar;
    String bio, firstName, lastName, phoneNumber;
    EditText editFirstName, editLastName, userBioEditText, phoneEditText;
    TextView noDataFoundTextView;
    private LinearLayout fieldsLinearLayout;
    private Menu menu;
    private boolean userDetailsFetchSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        Utils.pushOpenScreenEvent(EditProfieActivity.this, "Edit Profile", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        userBioEditText = (EditText) findViewById(R.id.userBioEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        noDataFoundTextView = (TextView) findViewById(R.id.noDataFoundTextView);
        fieldsLinearLayout = (LinearLayout) findViewById(R.id.fieldsLinearLayout);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        editFirstName.addTextChangedListener(OnTextChangeListener);
        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            noDataFoundTextView.setVisibility(View.VISIBLE);
            return;
        }

        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);
    }

    private TextWatcher OnTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (userDetailsFetchSuccess) {
                menu.getItem(0).setVisible(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                noDataFoundTextView.setVisibility(View.VISIBLE);
                showToast(getString(R.string.went_wrong));
                return;
            }

            UserDetailResponse responseData = (UserDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                userDetailsFetchSuccess = true;
                fieldsLinearLayout.setVisibility(View.VISIBLE);
                bio = responseData.getData().get(0).getResult().getUserBio();
                firstName = responseData.getData().get(0).getResult().getFirstName();
                lastName = responseData.getData().get(0).getResult().getLastName();
                if (null == responseData.getData().get(0).getResult().getPhone()) {
                    phoneNumber = " ";
                } else {
                    phoneNumber = responseData.getData().get(0).getResult().getPhone().getMobile();
                }
                editFirstName.setText(firstName);
                editLastName.setText(lastName);
                userBioEditText.setText(bio);
                phoneEditText.setText(phoneNumber);
            } else {
                noDataFoundTextView.setVisibility(View.VISIBLE);
                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            noDataFoundTextView.setVisibility(View.VISIBLE);
            removeProgressDialog();
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forgot_password, menu);
        this.menu = menu;
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                if (validateFields()) {
                    UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
                    updateUserDetail.setFirstName((editFirstName.getText().toString()).trim() + "");
                    updateUserDetail.setUserBio(userBioEditText.getText().toString().trim() + "");

                    if (StringUtils.isNullOrEmpty(editLastName.getText().toString().trim())) {
                        updateUserDetail.setLastName(" ");
                    } else {
                        updateUserDetail.setLastName(editLastName.getText().toString().trim() + "");
                    }

                    if (StringUtils.isNullOrEmpty(phoneEditText.getText().toString().trim())) {
                        updateUserDetail.setMobile(" ");
                    } else {
                        updateUserDetail.setMobile(phoneEditText.getText().toString().trim() + "");
                    }


                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    showProgressDialog(getResources().getString(R.string.please_wait));
                    UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
                    Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(updateUserDetail);
                    call.enqueue(new Callback<UserDetailResponse>() {
                        @Override
                        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                            removeProgressDialog();
                            if (response == null || response.body() == null) {
                                showToast(getString(R.string.went_wrong));
                                return;
                            }
                            UserDetailResponse responseData = (UserDetailResponse) response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                showToast("Successfully updated!");
                                UserInfo model = SharedPrefUtils.getUserDetailModel(EditProfieActivity.this);
                                model.setFirst_name(editFirstName.getText().toString());
                                model.setLast_name(editLastName.getText().toString());
                                SharedPrefUtils.setUserDetailModel(EditProfieActivity.this, model);
                            } else {
                                showToast(responseData.getReason());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                            removeProgressDialog();
                            showToast(getString(R.string.server_went_wrong));
                            Crashlytics.logException(t);
                            Log.d("MC4kException", Log.getStackTraceString(t));
                        }
                    });
                }
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateFields() {
        if (StringUtils.isNullOrEmpty(editFirstName.getText().toString().trim())) {
            showToast("First Name cannot be empty");
            return false;
        } else if (StringUtils.isNullOrEmpty(userBioEditText.getText().toString().trim())) {
            showToast("User bio cannot be empty");
        }
        return true;
    }

    @Override
    protected void updateUi(Response response) {
    }
}
