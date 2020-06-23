package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/7/17.
 */
public class BlogSetupActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EDIT_PICTURE = 1;
    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int MAX_WORDS = 200;

    private String currentPhotoPath;
    private File photoFile;
    private Uri imageUri;

    private LinearLayout introLinearLayout;
    private RelativeLayout detailsRelativeLayout;
    private TextView okayTextView;
    private TextView cityTextView;
    private TextView savePublishTextView;
    private EditText blogTitleEditText;
    private EditText aboutSelfEditText;
    private EditText phoneEditText;
    private ImageView profilePicImageView;
    private ImageView changeProfilePicImageView;
    private View mainLayout;
    private TextView emailLabelTextView;
    private TextView blogTitlesLabelTextView;
    private TextView blogHandleLabelTextView;
    private EditText emailEditText;
    private String comingFrom = "Normal";
    private String blogTitle;
    private String email;
    private Place cityPlaceObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setup_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "BlogSetupScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        mainLayout = findViewById(R.id.rootLayout);
        ((BaseApplication) getApplication()).setView(mainLayout);
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
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        emailLabelTextView = (TextView) findViewById(R.id.emailLabelTextView);
        blogTitlesLabelTextView = (TextView) findViewById(R.id.blogTitlesLabelTextView);
        blogHandleLabelTextView = (TextView) findViewById(R.id.blogHandleLabelTextView);
        Intent intent = getIntent();
        comingFrom = intent.getStringExtra("comingFrom");
        blogTitle = intent.getStringExtra("BlogTitle");
        email = intent.getStringExtra("email");
        if (blogTitle != null && !blogTitle.isEmpty()) {
            blogTitleEditText.setText(blogTitle);
        } else {
            blogTitleEditText.setText(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        }
        blogTitleEditText.setVisibility(View.GONE);
        if (email != null && !email.isEmpty()) {
            emailEditText.setText(email);
            emailEditText.setEnabled(false);
        } else {
            emailEditText.setEnabled(true);
        }

        okayTextView.setOnClickListener(this);
        cityTextView.setOnClickListener(this);
        savePublishTextView.setOnClickListener(this);
        profilePicImageView.setOnClickListener(this);
        changeProfilePicImageView.setOnClickListener(this);

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))) {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))
                    .placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profilePicImageView);
        }

        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getUserDetailModel(this).getEmail())) {
            emailEditText.setVisibility(View.VISIBLE);
            emailLabelTextView.setVisibility(View.VISIBLE);
        } else {
            emailEditText.setVisibility(View.GONE);
            emailLabelTextView.setVisibility(View.GONE);
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardApi
                .getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);

        if ("Videos".equals(comingFrom)) {
            blogHandleLabelTextView.setVisibility(View.VISIBLE);
            blogTitlesLabelTextView.setVisibility(View.GONE);
            aboutSelfEditText.setText("Hey,I Am A Vlogger");
        } else {
            blogTitlesLabelTextView.setVisibility(View.VISIBLE);
            blogHandleLabelTextView.setVisibility(View.GONE);
        }
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (comingFrom.equals("ShortStoryAndArticle")) {
                    if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getBlogTitle())) {
                        blogTitleEditText
                                .setText(SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
                    } else {
                        blogTitleEditText.setText(responseData.getData().get(0).getResult().getBlogTitle());
                    }
                    if (responseData.getData().get(0).getResult().getEmail() != null && !responseData.getData().get(0)
                            .getResult().getEmail().isEmpty()) {
                        emailEditText.setText(responseData.getData().get(0).getResult().getEmail());
                    }
                }
                if (responseData.getData().get(0).getResult().getUserBio() != null && !responseData.getData().get(0)
                        .getResult().getUserBio().isEmpty()) {
                    aboutSelfEditText.setText(responseData.getData().get(0).getResult().getUserBio());
                }
                if (null != responseData.getData().get(0).getResult().getPhone() && !StringUtils
                        .isNullOrEmpty(responseData.getData().get(0).getResult().getPhone().getMobile())) {
                    if (responseData.getData().get(0).getResult().getPhone().getMobile().contains("+91")) {
                        phoneEditText.setText(
                                responseData.getData().get(0).getResult().getPhone().getMobile().replace("+91", ""));
                    } else {
                        phoneEditText.setText(responseData.getData().get(0).getResult().getPhone().getMobile());
                    }
                }
                if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getCityName())) {
                    cityTextView.setText(responseData.getData().get(0).getResult().getCityName());
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okayTextView:
                introLinearLayout.setVisibility(View.GONE);
                detailsRelativeLayout.setVisibility(View.VISIBLE);
                aboutSelfEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(aboutSelfEditText, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.cityTextView:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setTypeFilter(TypeFilter.CITIES)
                        .build(this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
                break;
            case R.id.savePublishTextView:
                Utils.pushBlogSetupSubmitEvent(BlogSetupActivity.this, "BlogSetupScreen",
                        SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
                if (validateFields()) {
                    saveUserDetails();
                }
                break;
            case R.id.profilePicImageView:
            case R.id.changeProfilePicImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat
                            .checkSelfPermission(BlogSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat
                            .checkSelfPermission(BlogSetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestCameraAndStoragePermissions();
                    } else if (ActivityCompat.checkSelfPermission(BlogSetupActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat
                            .checkSelfPermission(BlogSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat
                            .checkSelfPermission(BlogSetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        requestCameraPermission();
                    } else {
                        chooseImageOptionPopUp(profilePicImageView);
                    }
                } else {
                    chooseImageOptionPopUp(profilePicImageView);
                }
                break;
            default:
                break;
        }
    }

    private void requestCameraAndStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            Snackbar.make(mainLayout, R.string.permission_storage_rationale,
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
            ActivityCompat.requestPermissions(this, PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BlogSetupActivity.this,
                                    new String[] {Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
                chooseImageOptionPopUp(profilePicImageView);
            } else {
                Snackbar.make(mainLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
        } else if (requestCode == REQUEST_EDIT_PICTURE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mainLayout, R.string.permision_available_storage,
                        Snackbar.LENGTH_SHORT)
                        .show();
                chooseImageOptionPopUp(profilePicImageView);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mainLayout, R.string.permissions_not_granted,
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
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Log.i("TAG", "IOException");
                        }
                        if (photoFile != null) {
                            try {
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider
                                        .getUriForFile(BlogSetupActivity.this,
                                                getApplicationContext().getPackageName() + ".my.package.name.provider",
                                                createImageFile()));
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private boolean validateFields() {
        if (comingFrom.equals("ShortStoryAndArticle")) {
            if (StringUtils.isNullOrEmpty(aboutSelfEditText.getText().toString().trim())) {
                Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_user_bio_empty),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (StringUtils.isNullOrEmpty(blogTitleEditText.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.app_settings_edit_profile_toast_blog_title_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (comingFrom.equals("ShortStoryAndArticle")) {
            if (countWords(aboutSelfEditText.getText().toString()) > MAX_WORDS) {
                Toast.makeText(this,
                        getString(R.string.app_settings_edit_profile_toast_user_bio_max) + " " + MAX_WORDS + " "
                                + getString(R.string.app_settings_edit_profile_toast_user_bio_words),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (comingFrom.equals("ShortStoryAndArticle")) {
            if (emailEditText.getVisibility() == View.VISIBLE) {
                if ((!StringUtils.isValidEmail(emailEditText.getText().toString()))) {
                    Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty()) {
            return 0;
        }
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void saveUserDetails() {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        if (!StringUtils.isNullOrEmpty(aboutSelfEditText.getText().toString())) {
            updateUserDetail.setUserBio(aboutSelfEditText.getText().toString().trim() + "");
        } else {
            updateUserDetail.setUserBio(" ");
        }
        updateUserDetail.setBlogTitle(blogTitleEditText.getText().toString().trim() + "");
        if (emailEditText.getVisibility() == View.VISIBLE) {
            updateUserDetail.setEmail(emailEditText.getText().toString().trim() + "");
        }
        if (null == phoneEditText.getText() || StringUtils.isNullOrEmpty(phoneEditText.getText().toString().trim())) {
            updateUserDetail.setMobile(" ");
        } else {
            updateUserDetail.setMobile(phoneEditText.getText().toString().replace("+91", "") + "");
        }
        if (null == cityTextView.getText() || StringUtils.isNullOrEmpty(cityTextView.getText().toString().trim())) {
            updateUserDetail.setCityName(" ");
        } else {
            updateUserDetail.setCityName(cityTextView.getText().toString().trim());
            if (cityPlaceObject != null && cityPlaceObject.getLatLng() != null) {
                updateUserDetail.setLatitude(cityPlaceObject.getLatLng().latitude);
                updateUserDetail.setLongitude(cityPlaceObject.getLatLng().longitude);
            }
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog(getResources().getString(R.string.please_wait));
        UserAttributeUpdateAPI userAttributeUpdateApi = retrofit.create(UserAttributeUpdateAPI.class);
        Call<ResponseBody> call = userAttributeUpdateApi.updateBlogProfile(updateUserDetail);
        call.enqueue(userDetailsUpdateResponseListener);
    }

    private Callback<ResponseBody> userDetailsUpdateResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject blogUpdateJson = new JSONObject(resData);
                int code = blogUpdateJson.getInt("code");
                String status = blogUpdateJson.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    Utils.pushBlogSetupSuccessEvent(BlogSetupActivity.this, "BlogSetupScreen",
                            SharedPrefUtils.getUserDetailModel(BlogSetupActivity.this).getDynamoId());
                    finish();
                } else {
                    showToast("" + blogUpdateJson.getString("reason"));
                }
            } catch (IOException | JSONException e) {
                showToast(getString(R.string.went_wrong));
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
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
                        startCropActivity(Uri.parse(currentPhotoPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_SELECT_PLACE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    if (!StringUtils.isNullOrEmpty(place.getName())) {
                        cityTextView.setText(place.getName());
                        cityPlaceObject = place;
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
            break;
            default:
                break;
        }
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        MediaType mediaTypePng = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(mediaTypePng, file);
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "0");
        ImageUploadAPI imageUploadApi = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadApi.uploadImage(imageType, requestBodyFile);
        call.enqueue(
                new Callback<ImageUploadResponse>() {
                    @Override
                    public void onResponse(Call<ImageUploadResponse> call,
                            retrofit2.Response<ImageUploadResponse> response) {
                        ImageUploadResponse responseModel = response.body();
                        removeProgressDialog();
                        if (responseModel.getCode() != 200) {
                            showToast(getString(R.string.toast_response_error));
                        } else {
                            if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                            }
                            setProfileImage(responseModel.getData().getResult().getUrl());
                            Picasso.get().invalidate(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()));
                            Picasso.get().load(responseModel.getData().getResult().getUrl())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.family_xxhdpi)
                                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation())
                                    .into(profilePicImageView);
                            SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(),
                                    responseModel.getData().getResult().getUrl());
                            showToast(getString(R.string.image_upload_success));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                        showToast(getString(R.string.image_upload_fail));
                        FirebaseCrashlytics.getInstance().recordException(t);
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
        UserAttributeUpdateAPI userAttributeUpdateApi = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateApi.updateProfilePic(updateUserDetail);
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                if (!response.body().getStatus().equals("success")) {
                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");
        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        ucrop.withAspectRatio(1, 1);
        ucrop.withMaxResultSize(300, 300);
        ucrop.start(BlogSetupActivity.this);
    }
}
