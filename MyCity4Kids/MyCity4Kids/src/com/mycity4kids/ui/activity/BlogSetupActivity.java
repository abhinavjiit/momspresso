package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.sync.PushTokenService;
import com.mycity4kids.ui.fragment.CityListingDialogFragment;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/7/17.
 */
public class BlogSetupActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EDIT_PICTURE = 1;
    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int MAX_WORDS = 200;

    private String mCurrentPhotoPath;
    private File photoFile;
    private Uri imageUri;
    public ArrayList<CityInfoItem> mDatalist;
    private String otherCityName;

    private LinearLayout introLinearLayout;
    private RelativeLayout detailsRelativeLayout;
    private TextView okayTextView;
    private TextView cityTextView;
    private TextView savePublishTextView;
    private EditText blogTitleEditText, aboutSelfEditText;
    private EditText phoneEditText;
    private CityListingDialogFragment cityFragment;
    private ImageView profilePicImageView, changeProfilePicImageView;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setup_activity);
        Utils.pushOpenScreenEvent(this, "BlogSetupScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        mLayout = findViewById(R.id.rootLayout);
        introLinearLayout = (LinearLayout) findViewById(R.id.introLinearLayout);
        detailsRelativeLayout = (RelativeLayout) findViewById(R.id.detailsRelativeLayout);
        okayTextView = (TextView) findViewById(R.id.okayTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        savePublishTextView = (TextView) findViewById(R.id.savePublishTextView);
        blogTitleEditText = (EditText) findViewById(R.id.blogTitleEditText);
        aboutSelfEditText = (EditText) findViewById(R.id.aboutSelfEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        profilePicImageView = (ImageView) findViewById(R.id.profilePicImageView);
        changeProfilePicImageView = (ImageView) findViewById(R.id.changeProfilePicImageView);

        okayTextView.setOnClickListener(this);
        cityTextView.setOnClickListener(this);
        savePublishTextView.setOnClickListener(this);
        profilePicImageView.setOnClickListener(this);
        changeProfilePicImageView.setOnClickListener(this);

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profilePicImageView);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);

        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> cityCall = cityConfigAPI.getCityConfig();
        cityCall.enqueue(cityConfigResponseCallback);
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                blogTitleEditText.setText(responseData.getData().get(0).getResult().getBlogTitle());
                aboutSelfEditText.setText(responseData.getData().get(0).getResult().getUserBio());

                if (null == responseData.getData().get(0).getResult().getPhone() || StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getPhone().getMobile())) {
//                    phoneEditText.setText(" ");
                } else {
                    phoneEditText.setText(responseData.getData().get(0).getResult().getPhone().getMobile());
                }
            } else {
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
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
//                gotToProfile();
                return;
            }
            try {
                CityConfigResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    mDatalist = responseData.getData().getResult().getCityData();
                    mDatalist = new ArrayList<>();
                    if (mDatalist == null) {
//                        gotToProfile();
                        return;
                    }
                    MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(BlogSetupActivity.this);
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
                            cityTextView.setText(mDatalist.get(i).getCityName());
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }

                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okayTextView:
                introLinearLayout.setVisibility(View.GONE);
                detailsRelativeLayout.setVisibility(View.VISIBLE);
                blogTitleEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(blogTitleEditText, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.cityTextView:
                cityFragment = new CityListingDialogFragment();
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", mDatalist);
                cityFragment.setArguments(_args);
                FragmentManager fm = getSupportFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
            case R.id.savePublishTextView:
                Utils.pushBlogSetupSubmitEvent(BlogSetupActivity.this, "BlogSetupScreen", SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
                if (validateFields()) {
//                    saveCityData();
                    saveUserDetails();
                }
                break;
            case R.id.profilePicImageView:
            case R.id.changeProfilePicImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                        requestCameraAndStoragePermissions();
                    } else if (ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                        requestCameraPermission();
                    } else {
                        chooseImageOptionPopUp(profilePicImageView);
                    }
                } else {
                    chooseImageOptionPopUp(profilePicImageView);
                }
                break;
        }
    }

    private void requestCameraAndStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying stoage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(BlogSetupActivity.this, PERMISSIONS_EDIT_PICTURE,
                                            REQUEST_EDIT_PICTURE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    private void requestCameraPermission() {
        Log.i("Permissions", "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i("Permissions",
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BlogSetupActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i("Permissions", "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("Permissions", "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
                chooseImageOptionPopUp(profilePicImageView);
            } else {
                Log.i("Permissions", "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_EDIT_PICTURE) {
            Log.i("Permissions", "Received response for storage permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_storage,
                        Snackbar.LENGTH_SHORT)
                        .show();
                chooseImageOptionPopUp(profilePicImageView);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void chooseImageOptionPopUp(ImageView profileImageView) {
        final PopupMenu popup = new PopupMenu(this, profileImageView);
        popup.getMenuInflater().inflate(R.menu.profile_image_upload_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.camera) {
//                    mClickListener.onBtnClick(position);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.i("TAG", "IOException");
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            try {
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(BlogSetupActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            startActivityForResult(cameraIntent, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE);
                        }
                    }
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    return true;
                }
            }

        });
        popup.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void changeCityText(CityInfoItem cityInfoItem) {
        cityTextView.setText(cityInfoItem.getCityName());
    }

    public void setOtherCityName(final int pos, final String cityName) {
        otherCityName = cityName;
        mDatalist.get(pos).setCityName("Others(" + cityName + ")");
        cityTextView.setText(mDatalist.get(pos).getCityName());
    }

    private boolean validateFields() {
        if (StringUtils.isNullOrEmpty(aboutSelfEditText.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_user_bio_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtils.isNullOrEmpty(blogTitleEditText.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_blog_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (countWords(aboutSelfEditText.getText().toString()) > MAX_WORDS) {
            Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_user_bio_max)
                    + " " + MAX_WORDS + " " + getString(R.string.app_settings_edit_profile_toast_user_bio_words), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void saveUserDetails() {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        updateUserDetail.setUserBio(aboutSelfEditText.getText().toString().trim() + "");
        updateUserDetail.setBlogTitle(blogTitleEditText.getText().toString().trim() + "");

        if (null == phoneEditText.getText() || StringUtils.isNullOrEmpty(phoneEditText.getText().toString().trim())) {
            updateUserDetail.setMobile(" ");
        } else {
            updateUserDetail.setMobile(phoneEditText.getText().toString().trim() + "");
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog(getResources().getString(R.string.please_wait));
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
//        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(updateUserDetail);
//        call.enqueue(userDetailsUpdateResponseListener);

        Call<ResponseBody> call = userAttributeUpdateAPI.updateBlogProfile(updateUserDetail);
        call.enqueue(userDetailsUpdateResponseListener);
    }

    private Callback<ResponseBody> userDetailsUpdateResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject blogUpdateJson = new JSONObject(resData);
                int code = blogUpdateJson.getInt("code");
                String status = blogUpdateJson.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    Utils.pushBlogSetupSuccessEvent(BlogSetupActivity.this, "BlogSetupScreen", SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
                    saveCityData();
                } else {
                    showToast("" + blogUpdateJson.getString("reason"));
                }
            } catch (IOException e) {
                showToast(getString(R.string.went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));

            } catch (JSONException e) {
                showToast(getString(R.string.went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

//            ResponseBody responseData = response.body();
//
//            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                Utils.pushBlogSetupSuccessEvent(BlogSetupActivity.this, "BlogSetupScreen", SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
//                saveCityData();
//            } else {
//                showToast(responseData.getReason());
//            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void saveCityData() {

        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(this);
        final ConfigurationController _controller = new ConfigurationController(this, this);
        if (null == mDatalist || mDatalist.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.change_city_fetch_available_cities));
            return;
        }
        showProgressDialog(getString(R.string.please_wait));
        double _latitude = 0;
        double _longitude = 0;
        for (int i = 0; i < mDatalist.size(); i++) {
            if (mDatalist.get(i).isSelected()) {
                _latitude = mDatalist.get(i).getLat();
                _longitude = mDatalist.get(i).getLon();
            }
        }
        new NearMyCity(this, _latitude, _longitude, new NearMyCity.FetchCity() {

            @Override
            public void nearCity(City cityModel) {
                int cityId = cityModel.getCityId();

                /**
                 * save current city in shared preference
                 */
                MetroCity model = new MetroCity();
                model.setId(cityModel.getCityId());
                if (AppConstants.OTHERS_CITY_ID == cityModel.getCityId()) {
                    cityModel.setCityName(otherCityName);
                    model.setName(otherCityName);
                } else {
                    model.setName(cityModel.getCityName());
                }

                model.setNewCityId(cityModel.getNewCityId());

                SharedPrefUtils.setCurrentCityModel(BlogSetupActivity.this, model);
                SharedPrefUtils.setChangeCityFlag(BlogSetupActivity.this, true);

                if (cityId > 0) {
                    versionApiModel.setCityId(cityId);
//                    mFirebaseAnalytics.setUserProperty("CityId", cityId + "");
                    /**
                     * get current version code ::
                     */
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String version = pInfo.versionName;
                    if (!StringUtils.isNullOrEmpty(version)) {
                        versionApiModel.setAppUpdateVersion(version);
                    }

                    if (!ConnectivityUtils.isNetworkEnabled(BlogSetupActivity.this)) {
                        ToastUtils.showToast(BlogSetupActivity.this, getString(R.string.error_network));
                        return;

                    }
                    _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);

                    UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
                    updateUserDetail.setAttributeName("cityId");
                    updateUserDetail.setAttributeType("S");
                    updateUserDetail.setAttributeValue("" + cityModel.getCityId());
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
                    Call<UserDetailResponse> call = userAttributeUpdateAPI.updateCity(updateUserDetail);
                    call.enqueue(new Callback<UserDetailResponse>() {
                        @Override
                        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                            removeProgressDialog();
                            if (response == null || response.body() == null) {
                                finish();
                                return;
                            }

                            UserDetailResponse responseData = response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                Toast.makeText(BlogSetupActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BlogSetupActivity.this, PushTokenService.class);
                                startService(intent);
                                finish();
                            } else {
                                Toast.makeText(BlogSetupActivity.this, responseData.getReason(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                            removeProgressDialog();
                            Toast.makeText(BlogSetupActivity.this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(t);
                            Log.d("MC4kException", Log.getStackTraceString(t));
                            finish();
                        }
                    });

                    UpdateUserDetailsRequest addOtherCityNameRequest = new UpdateUserDetailsRequest();
                    addOtherCityNameRequest.setCityId("" + cityModel.getCityId());
                    addOtherCityNameRequest.setCityName(cityModel.getCityName());
                    Call<UserDetailResponse> callNew = userAttributeUpdateAPI.updateCityAndKids(addOtherCityNameRequest);
                    callNew.enqueue(addOtherCityNameResponseCallback);
                }
            }
        });
    }

    Callback<UserDetailResponse> addOtherCityNameResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                return;
            }
            try {
                if (null != cityFragment) {
                    cityFragment.dismiss();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(Uri.parse(mCurrentPhotoPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(this, resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "0");
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);

        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             int statusCode = response.code();
                             ImageUploadResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }
                                 setProfileImage(responseModel.getData().getResult().getUrl());
                                 Picasso.with(BlogSetupActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(BlogSetupActivity.this));
                                 Picasso.with(BlogSetupActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profilePicImageView);
                                 SharedPrefUtils.setProfileImgUrl(BlogSetupActivity.this, responseModel.getData().getResult().getUrl());

                                 showToast(getString(R.string.image_upload_success));
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             showToast(getString(R.string.image_upload_fail));
                             Crashlytics.logException(t);
                             Log.d("MC4kException", Log.getStackTraceString(t));
                         }
                     }
        );

    }

    public void setProfileImage(String url) {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        updateUserDetail.setAttributeName("profilePicUrl");
        updateUserDetail.setAttributeValue(url);
        updateUserDetail.setAttributeType("S");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfilePic(updateUserDetail);
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                if (!response.body().getStatus().equals("success")) {
                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });

    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(300, 300);
        uCrop.start(BlogSetupActivity.this);
    }

}
