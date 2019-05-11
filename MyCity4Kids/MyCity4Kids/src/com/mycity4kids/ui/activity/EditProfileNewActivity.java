package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.PopupMenu;
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
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.UserProfilePagerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EditProfileNewActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EDIT_PICTURE = 1;
    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    UserProfilePagerAdapter viewPagerAdapter;
    public ArrayList<CityInfoItem> mDatalist;
    private ImageView profileImageView;
    private UserDetailResult userDetails;
    private TextView saveTextView;
    private ImageView editImageView;
    private View rootView;
    private File photoFile;
    private String mCurrentPhotoPath, absoluteImagePath;
    private Uri imageUri;
    private String isRewardsAdded = "0";
    private boolean isComingFromReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainprofile);
        rootView = findViewById(R.id.mainprofile_parent_layout);

        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.id_tabs);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        editImageView = (ImageView) findViewById(R.id.editImageView);
        saveTextView = (TextView) findViewById(R.id.saveTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if (getIntent() != null) {
            if (getIntent().getExtras().containsKey("isRewardAdded")) {
                isRewardsAdded = getIntent().getStringExtra("isRewardAdded");
            }
        }

        if (getIntent() != null) {
            if (getIntent().getExtras().containsKey("isComingFromReward")) {
                isComingFromReward = getIntent().getBooleanExtra("isComingFromReward", false);
            }
        }

        saveTextView.setOnClickListener(this);
        editImageView.setOnClickListener(this);
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

                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.contact_details)));
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.about_txt)));
                    tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.rewards_tab_detail)));

                    AppUtils.changeTabsFont(EditProfileNewActivity.this, tabLayout);

                    viewPagerAdapter = new UserProfilePagerAdapter(getSupportFragmentManager(), userDetails, mDatalist, isRewardsAdded, EditProfileNewActivity.this);
                    viewPager.setAdapter(viewPagerAdapter);
                    if (isComingFromReward) {
                        viewPager.setCurrentItem(2);
                        saveTextView.setVisibility(View.GONE);
                    }

                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            if (tab.getPosition() == 2) {
                                saveTextView.setVisibility(View.GONE);
                            } else {
                                saveTextView.setVisibility(View.VISIBLE);
                            }
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
        if (viewPager.getCurrentItem() == 1) {
            updateUserDetail.setUserBio(viewPagerAdapter.getAbout().getAboutEditText().getText().toString().trim() + "");
        } else {
            String[] nameArr = viewPagerAdapter.getContactdetails().getFullNameEditText().getText().toString().trim().split("\\s+");
            updateUserDetail.setFirstName((nameArr[0]));

            if (nameArr.length < 2 || StringUtils.isNullOrEmpty(nameArr[1].trim())) {
                updateUserDetail.setLastName(" ");
            } else {
                updateUserDetail.setLastName(nameArr[1]);
            }

            if (StringUtils.isNullOrEmpty(viewPagerAdapter.getContactdetails().getPhoneEditText().getText().toString().trim())) {
//                updateUserDetail.setMobile(" ");
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
                String[] nameArr = viewPagerAdapter.getContactdetails().getFullNameEditText().getText().toString().trim().split("\\s+");
                model.setFirst_name(nameArr[0]);
                if (nameArr.length < 2 || StringUtils.isNullOrEmpty(nameArr[1].trim())) {
                    model.setLast_name(" ");
                } else {
                    model.setLast_name(nameArr[1]);
                }
                SharedPrefUtils.setUserDetailModel(EditProfileNewActivity.this, model);
                if (viewPagerAdapter.getContactdetails().getSelectedCityId() != 0) {
                    updateEventsResourcesConfigForCity();
                }
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

    private void updateEventsResourcesConfigForCity() {
        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(this);
        final ConfigurationController _controller = new ConfigurationController(this, this);

        MetroCity model = new MetroCity();

        model.setId(viewPagerAdapter.getContactdetails().getSelectedCityId());
        model.setName(viewPagerAdapter.getContactdetails().getCurrentCityName());
        model.setNewCityId(viewPagerAdapter.getContactdetails().getCurrentCityName());

        SharedPrefUtils.setCurrentCityModel(this, model);
        SharedPrefUtils.setChangeCityFlag(this, true);

        if (viewPagerAdapter.getContactdetails().getSelectedCityId() > 0) {
            versionApiModel.setCityId(viewPagerAdapter.getContactdetails().getSelectedCityId());
//            mFirebaseAnalytics.setUserProperty("CityId", cityModel.getCityId() + "");

            String version = AppUtils.getAppVersion(this);
            if (!StringUtils.isNullOrEmpty(version)) {
                versionApiModel.setAppUpdateVersion(version);
            }

            if (!ConnectivityUtils.isNetworkEnabled(this)) {
                ToastUtils.showToast(this, getString(R.string.error_network));
                return;

            }
//            if (null != cityFragment) {
//                cityFragment.dismiss();
//            }
            _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setupUI(mainprofileparentlayout);
    }

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
//            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.CONFIGURATION_REQUEST:
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;
                    BaseApplication.setBusinessREsponse(null);
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this,
                            _configurationResponse, new OnUIView() {
                        @Override
                        public void comeBackOnUI() {
//                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    _heavyDbTask.execute();
                }
                break;
            default:
                break;
        }
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
                }
                break;
            case R.id.editImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                        requestCameraAndStoragePermissions();
                    } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                        requestCameraPermission();
                    } else {
                        chooseImageOptionPopUp();
                    }
                } else {
                    chooseImageOptionPopUp();
                }
                break;
        }
    }

    private boolean validateFields() {
        if (viewPager.getCurrentItem() == 1) {
            if (TextUtils.isEmpty(viewPagerAdapter.getAbout().getAboutEditText().getText())) {
                Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_user_bio_empty), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            try {
                if (StringUtils.isNullOrEmpty(viewPagerAdapter.getContactdetails().getFullNameEditText().getText().toString().trim())) {
                    Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_fn_empty), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {

                return false;
            }
        }
        return true;
    }

    private void requestCameraAndStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileNewActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileNewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileNewActivity.this,
                Manifest.permission.CAMERA)) {

            Snackbar.make(rootView, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(EditProfileNewActivity.this, PERMISSIONS_EDIT_PICTURE,
                                            REQUEST_EDIT_PICTURE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(rootView, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(EditProfileNewActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    public void chooseImageOptionPopUp() {
        final PopupMenu popup = new PopupMenu(this, profileImageView);
        popup.getMenuInflater().inflate(R.menu.profile_image_upload_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.camera) {
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
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(EditProfileNewActivity.this, BaseApplication.getAppContext().getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
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

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(300, 300);
        uCrop.start(this);
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
//                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }
                                 setProfileImage(responseModel.getData().getResult().getUrl());
                                 Picasso.with(EditProfileNewActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(EditProfileNewActivity.this));
                                 Picasso.with(EditProfileNewActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).into(profileImageView);
                                 SharedPrefUtils.setProfileImgUrl(EditProfileNewActivity.this, responseModel.getData().getResult().getUrl());

//                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
//                             showToast("unable to upload image, please try again later");
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
//                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

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


}