package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
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
import com.mycity4kids.constants.AppConstants;
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

    private static final int MAX_WORDS = 200;
    private InputFilter filter;

    private ProgressBar progress_bar;
    Toolbar mToolBar;
    String bio, firstName, lastName, phoneNumber, blogTitle;
    EditText editFirstName, editLastName, userBioEditText, phoneEditText;
    TextView noDataFoundTextView;
    private LinearLayout fieldsLinearLayout;
    private Menu menu;
    private boolean userDetailsFetchSuccess = false;
    private EditText blogTitleEditText;
//    private boolean isBlogSetUp;

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
        blogTitleEditText = (EditText) findViewById(R.id.blogTitleEditText);
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

        userBioEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int wordsLength = countWords(s.toString());// words.length;
                Log.d("onTextChanged", "" + wordsLength);
                if (wordsLength > MAX_WORDS) {
                    userBioEditText.setText(s.toString().trim().replaceAll(" [^ ]+$", ""));
                    userBioEditText.setSelection(userBioEditText.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                Log.d("beforeTextChanged", "" + wordsLength);
                if (count == 0 && wordsLength >= MAX_WORDS) {
                    //setCharLimit(userBioEditText, userBioEditText.getText().length());
                } else {
                    removeFilter(userBioEditText);
                }

//                textLimit.setText(String.valueOf(wordsLength) + "/" + MAX_WORDS);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                Log.d("afterTextChanged", "" + wordsLength);
//                int wLength = countWords(s.toString());
//                Log.d("afterTextChanged", "Length = " + wLength + " --- " + s.toString());
//                if (wLength > MAX_WORDS) {
//                    bloggerBio.setText(removeExtraWords(s.toString()));
//                }

            }
        });

        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    private TextWatcher OnTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (userDetailsFetchSuccess && menu != null && menu.getItem(0) != null) {
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
//                if (AppConstants.USER_TYPE_USER.equals(responseData.getData().get(0).getResult().getUserType())) {
//                    isBlogSetUp = false;
//                } else {
//                    isBlogSetUp = true;
//                }

                bio = responseData.getData().get(0).getResult().getUserBio();
                firstName = responseData.getData().get(0).getResult().getFirstName();
                lastName = responseData.getData().get(0).getResult().getLastName();
                blogTitle = responseData.getData().get(0).getResult().getBlogTitle();

                if (null == responseData.getData().get(0).getResult().getPhone()) {
                    phoneNumber = " ";
                } else {
                    phoneNumber = responseData.getData().get(0).getResult().getPhone().getMobile();
                }
                editFirstName.setText(firstName);
                editLastName.setText(lastName);
                userBioEditText.setText(bio);
                blogTitleEditText.setText(blogTitle);
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
                    updateUserDetail.setBlogTitle(blogTitleEditText.getText().toString().trim() + "");

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
            return false;
        } else if (StringUtils.isNullOrEmpty(blogTitleEditText.getText().toString().trim())) {
            showToast("Blog Title cannot be empty");
            return false;
        } else if (countWords(userBioEditText.getText().toString()) > MAX_WORDS) {
            showToast("Maximum limit for bio is " + MAX_WORDS + " words");
            return false;
        }
        return true;
    }

    @Override
    protected void updateUi(Response response) {
    }
}
