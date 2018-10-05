package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.animation.MyCityAnimationsUtil;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

public class PublicProfileActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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
    private Boolean isFollowing = false;

    private ImageView imgProfile, blurImageView;
    private ImageView settingImageView;
    private LinearLayout followerContainer, followingContainer, rankContainer;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView rankLanguageTextView;
    private TextView authorNameTextView, authorBioTextView;
    private TextView publishedSectionTextView, activitySectionTextView;
    private View rootView;
    private ImageView backArrowImageView;
    private String authorId;
    private TextView followAuthorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_profile_activity);

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

        backArrowImageView = (ImageView) findViewById(R.id.menuImageView);
        authorNameTextView = (TextView) findViewById(R.id.nameTextView);
        followAuthorTextView = (TextView) findViewById(R.id.followAuthorTextView);
        authorBioTextView = (TextView) findViewById(R.id.userbioTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        rankLanguageTextView = (TextView) findViewById(R.id.rankLanguageTextView);
        publishedSectionTextView = (TextView) findViewById(R.id.publishedSectionTextView);
        activitySectionTextView = (TextView) findViewById(R.id.activitySectionTextView);
        imgProfile = (ImageView) findViewById(R.id.profileImageView);
        settingImageView = (ImageView) findViewById(R.id.settingImageView);
        followerContainer = (LinearLayout) findViewById(R.id.followerContainer);
        followingContainer = (LinearLayout) findViewById(R.id.followingContainer);
        rankContainer = (LinearLayout) findViewById(R.id.rankContainer);

        authorNameTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        publishedSectionTextView.setOnClickListener(this);
        activitySectionTextView.setOnClickListener(this);
        rankContainer.setOnClickListener(this);
        settingImageView.setOnClickListener(this);
        followingContainer.setOnClickListener(this);
        followerContainer.setOnClickListener(this);
        backArrowImageView.setOnClickListener(this);
        followAuthorTextView.setOnClickListener(this);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        authorId = getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);

        getUserDetails();
        checkFollowingStatusAPI();
//        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
//            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
//                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
//
//            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).into(target);
////            GaussianBlur.with(this).put(R.drawable.groups_generic, blurImageView);
//        }


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
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(authorId);
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
                        MyCityAnimationsUtil.animate(PublicProfileActivity.this, rankContainer, multipleRankList, 0, true);
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

                    if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                        Picasso.with(PublicProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).into(imgProfile);
                    }
                    try {
                        Picasso.with(PublicProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp()).into(target);
                    } catch (Exception e) {
                        GaussianBlur.with(PublicProfileActivity.this).put(R.drawable.groups_generic, blurImageView);
                    }


                    if (responseData.getData().get(0).getResult().getUserBio() == null || responseData.getData().get(0).getResult().getUserBio().isEmpty()) {
                        authorBioTextView.setVisibility(View.GONE);
                    } else {
                        authorBioTextView.setText(responseData.getData().get(0).getResult().getUserBio());
                        authorBioTextView.setVisibility(View.VISIBLE);
                    }
                    if (null == responseData.getData().get(0).getResult().getSocialTokens()) {
                        //token already expired or yet to connect using facebook
                        SharedPrefUtils.setFacebookConnectedFlag(PublicProfileActivity.this, "1");
                    } else {
                        SharedPrefUtils.setFacebookConnectedFlag(PublicProfileActivity.this,
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

    private void checkFollowingStatusAPI() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId("");
        Call<ArticleDetailResponse> callBookmark = articleDetailsAPI.checkFollowingBookmarkStatus("0", authorId);
        callBookmark.enqueue(isFollowedResponseCallback);
    }

    private Callback<ArticleDetailResponse> isFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                    followAuthorTextView.setText(R.string.ad_follow_author);
                    isFollowing = false;
                } else {
                    followAuthorTextView.setText(R.string.ad_following_author);
                    isFollowing = true;
                }
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            if (t instanceof UnknownHostException) {
                showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                showToast(getString(R.string.connection_timeout));
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            GaussianBlur.with(PublicProfileActivity.this).put(bitmap, blurImageView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            GaussianBlur.with(PublicProfileActivity.this).put(R.drawable.groups_generic, blurImageView);
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
                            ActivityCompat.requestPermissions(PublicProfileActivity.this,
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(PublicProfileActivity.this,
                Manifest.permission.CAMERA)) {

            Snackbar.make(rootView, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(PublicProfileActivity.this, PERMISSIONS_EDIT_PICTURE,
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
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(PublicProfileActivity.this, BaseApplication.getAppContext().getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
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
                                 Picasso.with(PublicProfileActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(PublicProfileActivity.this));
                                 Picasso.with(PublicProfileActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                                 Picasso.with(PublicProfileActivity.this).load(SharedPrefUtils.getProfileImgUrl(PublicProfileActivity.this)).into(target);
                                 SharedPrefUtils.setProfileImgUrl(PublicProfileActivity.this, responseModel.getData().getResult().getUrl());
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
            case R.id.menuImageView:
                onBackPressed();
                break;
            case R.id.authorNameTextView: {
                Intent intent = new Intent(this, PublicProfileActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.followAuthorTextView:
                hitFollowUnfollowAPI();
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
                articleIntent.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(articleIntent);
                break;
            case R.id.bookmarksSectionTextView: {
                Intent intent = new Intent(this, UserActivitiesActivity.class);
                intent.putExtra(Constants.AUTHOR_ID, authorId);
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

    private void hitFollowUnfollowAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);

        if (isFollowing) {
            isFollowing = false;
            followAuthorTextView.setText(R.string.ad_follow_author);
//            Utils.pushUnfollowAuthorEvent(this, "PublicProfileScreen", userId, authorId + "~" + authorName);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followAuthorTextView.setText(R.string.ad_following_author);
//            Utils.pushFollowAuthorEvent(this, "PublicProfileScreen", userId, authorId + "~" + authorName);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followAuthorTextView.setText(R.string.ad_follow_author);
                    isFollowing = false;
                }
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followAuthorTextView.setText(R.string.ad_following_author);
                    isFollowing = true;
                }
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    @Override
    protected void updateUi(Response response) {

    }

}
