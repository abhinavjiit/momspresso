package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.animation.MyCityAnimationsUtil;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vansuita.gaussianblur.GaussianBlur;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/9/18.
 */

public class PrivateProfileActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EDIT_PICTURE = 1;
    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private GoogleApiClient mGoogleApiClient;

    private String userId;
    private ArrayList<LanguageRanksModel> multipleRankList = new ArrayList<>();
    private File photoFile;
    private String mCurrentPhotoPath, absoluteImagePath;
    private Uri imageUri;

    private ImageView imgProfile, blurImageView;
    private ImageView settingImageView;
    private LinearLayout followerContainer, followingContainer, rankContainer;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView rankLanguageTextView;
    private TextView authorNameTextView, locationTextView, authorBioTextView;
    private TextView publishedSectionTextView, draftSectionTextView, activitySectionTextView, signoutSectionTextView;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {

        }

        rootView = findViewById(R.id.rootView);
        blurImageView = (ImageView) findViewById(R.id.blurImageView);

        authorNameTextView = (TextView) findViewById(R.id.nameTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        authorBioTextView = (TextView) findViewById(R.id.userbioTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        rankLanguageTextView = (TextView) findViewById(R.id.rankLanguageTextView);
        publishedSectionTextView = (TextView) findViewById(R.id.publishedSectionTextView);
        draftSectionTextView = (TextView) findViewById(R.id.draftSectionTextView);
//        bookmarksSectionTextView = (TextView) findViewById(R.id.bookmarksSectionTextView);
        activitySectionTextView = (TextView) findViewById(R.id.activitySectionTextView);
//        rankingSectionTextView = (TextView) findViewById(R.id.rankingSectionTextView);
//        settingsSectionTextView = (TextView) findViewById(R.id.settingsSectionTextView);
        signoutSectionTextView = (TextView) findViewById(R.id.signoutSectionTextView);
        imgProfile = (ImageView) findViewById(R.id.profileImageView);
        settingImageView = (ImageView) findViewById(R.id.settingImageView);
        followerContainer = (LinearLayout) findViewById(R.id.followerContainer);
        followingContainer = (LinearLayout) findViewById(R.id.followingContainer);
        rankContainer = (LinearLayout) findViewById(R.id.rankContainer);

        authorNameTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        publishedSectionTextView.setOnClickListener(this);
        draftSectionTextView.setOnClickListener(this);
//        bookmarksSectionTextView.setOnClickListener(this);
        activitySectionTextView.setOnClickListener(this);
//        rankingSectionTextView.setOnClickListener(this);
        rankContainer.setOnClickListener(this);
//        settingsSectionTextView.setOnClickListener(this);
        signoutSectionTextView.setOnClickListener(this);
        settingImageView.setOnClickListener(this);
        followingContainer.setOnClickListener(this);
        followerContainer.setOnClickListener(this);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);

            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).into(target);
//            GaussianBlur.with(this).put(R.drawable.groups_generic, blurImageView);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserDetails();
            }
        }, 200);
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
        call.enqueue(userDetailsResponseListener);
    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                return;
            }
            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                        rankCountTextView.setText("--");
                        rankLanguageTextView.setText(getString(R.string.myprofile_rank_label));
                    } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                        rankCountTextView.setText("" + responseData.getData().get(0).getResult().getRanks().get(0).getRank());
                        if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(0).getLangKey())) {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in) + " ENGLISH");
                        } else {
                            rankLanguageTextView.setText(getString(R.string.blogger_profile_rank_in)
                                    + " " + AppUtils.getLangModelForLanguage(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getRanks().get(0).getLangKey()).getDisplay_name());
                        }
                    } else {
                        for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                            if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                                break;
                            }
                        }
                        Collections.sort(responseData.getData().get(0).getResult().getRanks());
                        for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                            if (!AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                            }
                        }
                        MyCityAnimationsUtil.animate(PrivateProfileActivity.this, rankContainer, multipleRankList, 0, true);
                    }

                    int followerCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowersCount());
                    if (followerCount > 999) {
                        float singleFollowerCount = ((float) followerCount) / 1000;
                        followerCountTextView.setText("" + singleFollowerCount + "k");
                    } else {
                        followerCountTextView.setText("" + followerCount);
                    }

                    int followingCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowingCount());
                    if (followingCount > 999) {
                        float singleFollowingCount = ((float) followingCount) / 1000;
                        followingCountTextView.setText("" + singleFollowingCount + "k");
                    } else {
                        followingCountTextView.setText("" + followingCount);
                    }
                    authorNameTextView.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());

//                    switch (responseData.getData().get(0).getResult().getUserType()) {
//                        case AppConstants.USER_TYPE_BLOGGER:
//                            authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
//                            rankingSectionTextView.setVisibility(View.VISIBLE);
//                            findViewById(R.id.underline_5).setVisibility(View.VISIBLE);
//                            break;
//                        case AppConstants.USER_TYPE_EDITOR:
//                            rankingSectionTextView.setVisibility(View.GONE);
//                            findViewById(R.id.underline_5).setVisibility(View.GONE);
//                            authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
//                            break;
//                        case AppConstants.USER_TYPE_EDITORIAL:
//                            rankingSectionTextView.setVisibility(View.GONE);
//                            findViewById(R.id.underline_5).setVisibility(View.GONE);
//                            authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
//                            break;
//                        case AppConstants.USER_TYPE_EXPERT:
//                            rankingSectionTextView.setVisibility(View.GONE);
//                            findViewById(R.id.underline_5).setVisibility(View.GONE);
//                            authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
//                            break;
//                        case AppConstants.USER_TYPE_USER:
//                            rankingSectionTextView.setVisibility(View.GONE);
//                            findViewById(R.id.underline_5).setVisibility(View.GONE);
//                            authorTypeTextView.setVisibility(View.GONE);
//                            break;
//                        default:
//                            rankingSectionTextView.setVisibility(View.GONE);
//                            findViewById(R.id.underline_5).setVisibility(View.GONE);
//                    }

                    if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                        Picasso.with(PrivateProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                    }

                    if (responseData.getData().get(0).getResult().getUserBio() == null || responseData.getData().get(0).getResult().getUserBio().isEmpty()) {
                        authorBioTextView.setVisibility(View.GONE);
                    } else {
                        authorBioTextView.setText(responseData.getData().get(0).getResult().getUserBio());
                        authorBioTextView.setVisibility(View.VISIBLE);
                    }
                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or yet to connect using facebook
                        SharedPrefUtils.setFacebookConnectedFlag(PrivateProfileActivity.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(PrivateProfileActivity.this,
                                responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                    }
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            GaussianBlur.with(PrivateProfileActivity.this).put(bitmap, blurImageView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            GaussianBlur.with(PrivateProfileActivity.this).put(R.drawable.groups_generic, blurImageView);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
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
                            ActivityCompat.requestPermissions(PrivateProfileActivity.this,
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

    /**
     * Requests the Storage permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraAndStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(PrivateProfileActivity.this,
                Manifest.permission.CAMERA)) {

            Snackbar.make(rootView, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(PrivateProfileActivity.this, PERMISSIONS_EDIT_PICTURE,
                                            REQUEST_EDIT_PICTURE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
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
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(PrivateProfileActivity.this, BaseApplication.getAppContext().getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
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
                                 Picasso.with(PrivateProfileActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(PrivateProfileActivity.this));
                                 Picasso.with(PrivateProfileActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                                 Picasso.with(PrivateProfileActivity.this).load(SharedPrefUtils.getProfileImgUrl(PrivateProfileActivity.this)).into(target);
                                 SharedPrefUtils.setProfileImgUrl(PrivateProfileActivity.this, responseModel.getData().getResult().getUrl());
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authorNameTextView: {
                Intent intent = new Intent(this, PrivateProfileActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.authorTypeTextView:
                break;
//            case R.id.authorBioTextView:
//                break;
            case R.id.settingImageView:
            case R.id.imgProfile:
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
                        chooseImageOptionPopUp(imgProfile);
                    }
                } else {
                    chooseImageOptionPopUp(imgProfile);
                }
                break;
            case R.id.publishedSectionTextView:
                Intent articleIntent = new Intent(this, UserPublishedContentActivity.class);
                articleIntent.putExtra("isPrivateProfile", true);
                articleIntent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(articleIntent);
                break;
            case R.id.draftSectionTextView:
                Intent ssIntent = new Intent(this, UserDraftsContentActivity.class);
                ssIntent.putExtra("isPrivateProfile", true);
                ssIntent.putExtra("contentType", "shortStory");
                ssIntent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(ssIntent);
                break;
            case R.id.bookmarksSectionTextView: {
                Intent intent = new Intent(this, UserActivitiesActivity.class);
                intent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(intent);
            }
            case R.id.activitySectionTextView: {
                Intent intent = new Intent(this, UserActivitiesActivity.class);
                intent.putExtra(Constants.AUTHOR_ID, userId);
                startActivity(intent);
            }
            break;
            case R.id.rankContainer:
                if (AppConstants.DEBUGGING_USER_ID.contains(userId)) {
                    rankContainer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            BaseApplication.getInstance().toggleGroupBaseURL();
                            return false;
                        }
                    });
                    Intent _intent = new Intent(this, IdTokenLoginActivity.class);
                    startActivity(_intent);
                    return;
                }
//                if (rankingSectionTextView.getVisibility() == View.VISIBLE) {
//                    Intent intent = new Intent(this, RankingActivity.class);
//                    startActivity(intent);
//                }
                break;
            case R.id.rankingSectionTextView: {
                Intent intent = new Intent(this, RankingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.settingsSectionTextView:
                Intent settingsIntent = new Intent(this, AppSettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.signoutSectionTextView:
                logoutUser();
                break;
            case R.id.followingContainer: {
                Utils.pushOpenScreenEvent(this, "FollowingListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                Intent intent = new Intent(this, FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWING_LIST);
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent);
            }
            break;
            case R.id.followerContainer: {
                Utils.pushOpenScreenEvent(this, "FollowersListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                Intent intent = new Intent(this, FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWER_LIST);
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, userId);
                startActivity(intent);
            }
        }
    }

    private void logoutUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            final LogoutController _controller = new LogoutController(this, this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

            dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showProgressDialog(getResources().getString(R.string.please_wait));
                            _controller.getData(AppConstants.LOGOUT_REQUEST, "");
                        }
                    }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    dialog.cancel();
                }
            }).setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert11 = dialog.create();
            alert11.show();
            alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
            alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network));
        }
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
        String message = responseData.getResult().getMessage();
        if (responseData.getResponseCode() == 200) {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                mixpanel.track("UserLogout", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FacebookUtils.logout(this);
            gPlusSignOut();

            String pushToken = SharedPrefUtils.getDeviceToken(this);
            boolean homeCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "home");
            boolean topicsCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "topics");
            boolean topicsArticleCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "topics_article");
            boolean articleCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "article_details");
            boolean groupsCoach = SharedPrefUtils.isCoachmarksShownFlag(this, "groups");
            String appLocale = SharedPrefUtils.getAppLocale(this);

            SharedPrefUtils.clearPrefrence(this);
            SharedPrefUtils.setDeviceToken(this, pushToken);
            SharedPrefUtils.setCoachmarksShownFlag(this, "home", homeCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "topics", topicsCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "topics_article", topicsArticleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "article_details", articleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(this, "groups", groupsCoach);
            SharedPrefUtils.setAppLocale(this, appLocale);
            /**
             * delete table from local also;
             */
            UserTable _tables = new UserTable((BaseApplication) this.getApplicationContext());
            _tables.deleteAll();

            TableFamily _familytables = new TableFamily((BaseApplication) this.getApplicationContext());
            _familytables.deleteAll();

            TableAdult _adulttables = new TableAdult((BaseApplication) this.getApplicationContext());
            _adulttables.deleteAll();

            TableKids _kidtables = new TableKids((BaseApplication) this.getApplicationContext());
            _kidtables.deleteAll();

            new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
            new TableNotes(BaseApplication.getInstance()).deleteAll();
            new TableFile(BaseApplication.getInstance()).deleteAll();
            new TableAttendee(BaseApplication.getInstance()).deleteAll();
            new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


            new TableTaskData(BaseApplication.getInstance()).deleteAll();
            new TableTaskList(BaseApplication.getInstance()).deleteAll();
            new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
            new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
            new TaskTableFile(BaseApplication.getInstance()).deleteAll();
            new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
            new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
            new TableApiEvents(BaseApplication.getInstance()).deleteAll();

            new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

            // clear cachee
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }

            // set logout flag
            SharedPrefUtils.setLogoutFlag(this, true);
            Intent intent = new Intent(this, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            this.finish();

        } else if (responseData.getResponseCode() == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gPlusSignOut() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }
}
