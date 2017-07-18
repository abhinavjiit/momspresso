package com.mycity4kids.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.GTMEventType;
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
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 18/7/17.
 */
public class BloggerProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EDIT_PICTURE = 1;
    private static String[] PERMISSIONS_EDIT_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private File photoFile;
    private String mCurrentPhotoPath, absoluteImagePath;
    private Uri imageUri;

    private View rootView;
    private Toolbar toolbar;
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView authorNameTextView, authorTypeTextView, authorBioTextView;
    private ImageView imgProfile;
    private ImageView imgTopArticle_1, imgTopArticle_2, imgTopArticle_3;
    private TextView txvTopArticle_1, txvTopArticle_2, txvTopArticle_3;
    private TextView followButton, unfollowButton;

    private Boolean isFollowing = false;
    private String userId;
    private String authorId;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blogger_profile_activity);

        rootView = findViewById(R.id.rootLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        authorNameTextView = (TextView) findViewById(R.id.authorNameTextView);
        authorTypeTextView = (TextView) findViewById(R.id.authorTypeTextView);
        authorBioTextView = (TextView) findViewById(R.id.authorBioTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) findViewById(R.id.rankCountTextView);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgTopArticle_1 = (ImageView) findViewById(R.id.imgTopArticle_1);
        imgTopArticle_2 = (ImageView) findViewById(R.id.imgTopArticle_2);
        imgTopArticle_3 = (ImageView) findViewById(R.id.imgTopArticle_3);
        txvTopArticle_1 = (TextView) findViewById(R.id.txvTopArticle_1);
        txvTopArticle_2 = (TextView) findViewById(R.id.txvTopArticle_2);
        txvTopArticle_3 = (TextView) findViewById(R.id.txvTopArticle_3);
        followButton = (TextView) findViewById(R.id.followTextView);
        unfollowButton = (TextView) findViewById(R.id.unfollowTextView);

        authorNameTextView.setOnClickListener(this);
        authorTypeTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        followButton.setOnClickListener(this);
        unfollowButton.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        authorId = getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
        }

        getUserDetails();
        checkFollowingStatusAPI();
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }
        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(authorId);
        call.enqueue(userDetailsResponseListener);
    }

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

    private void hitFollowUnfollowAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(userId);

        if (isFollowing) {
            isFollowing = false;
            followButton.setVisibility(View.VISIBLE);
            unfollowButton.setVisibility(View.INVISIBLE);
            int followerCount = Integer.parseInt(followerCountTextView.getText().toString()) - 1;
            followerCountTextView.setText("" + followerCount);
//            Utils.pushAuthorFollowUnfollowEvent(BloggerProfileActivity.this, GTMEventType.UNFOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerProfileActivity.this).getDynamoId(),
//                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            followButton.setVisibility(View.INVISIBLE);
            unfollowButton.setVisibility(View.VISIBLE);
            int followerCount = Integer.parseInt(followerCountTextView.getText().toString()) + 1;
            followerCountTextView.setText("" + followerCount);
//            Utils.pushAuthorFollowUnfollowEvent(BloggerProfileActivity.this, GTMEventType.FOLLOW_AUTHOR_CLICK_EVENT, "User Profile", SharedPrefUtils.getUserDetailModel(BloggerProfileActivity.this).getDynamoId(),
//                    "", firstName + " " + lastName + "-" + userId);
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }

    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
//                showToast("Something went wrong from server");
                return;
            }
            UserDetailResponse responseData = (UserDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                    LanguageRanksModel languageRanksModel = new LanguageRanksModel();
                    languageRanksModel.setRank(-1);
                    languageRanksModel.setLangKey("");
//                    addRankView(languageRanksModel);
//                    rankViewFlipper.setAutoStart(false);
//                    rankViewFlipper.stopFlipping();
                } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
//                    addRankView(responseData.getData().get(0).getResult().getRanks().get(0));
//                    rankViewFlipper.setAutoStart(false);
//                    rankViewFlipper.stopFlipping();
                } else {
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
//                            addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                        }
                    }
                    Collections.sort(responseData.getData().get(0).getResult().getRanks());
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (!AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
//                            addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                        }
                    }
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
                toolbarTitle.setText(responseData.getData().get(0).getResult().getFirstName() + " " + responseData.getData().get(0).getResult().getLastName());

                switch (responseData.getData().get(0).getResult().getUserType()) {
                    case AppConstants.USER_TYPE_BLOGGER:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EDITOR:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EDITORIAL:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_EXPERT:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                        break;
                    case AppConstants.USER_TYPE_USER:
                        authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_USER.toUpperCase());
                        break;
                }
//                blogTitle.setText(responseData.getData().get(0).getResult().getBlogTitle());
//                getSupportActionBar().setTitle(responseData.getData().get(0).getResult().getFirstName());
//                Bio = responseData.getData().get(0).getResult().getUserBio();
//                firstName = responseData.getData().get(0).getResult().getFirstName();
//                lastName = responseData.getData().get(0).getResult().getLastName();
//                if (isPrivateProfile && AppConstants.USER_TYPE_BLOGGER.equals(responseData.getData().get(0).getResult().getUserType())) {
//                    analyticsTextView.setVisibility(View.VISIBLE);
//                    analyticsTextView.setOnClickListener(MyAccountProfileFragment.this);
//                } else if (null != analyticsTextView) {
//                    analyticsTextView.setVisibility(View.GONE);
//                }
//                if (null == responseData.getData().get(0).getResult().getPhone()) {
//                    phoneNumber = " ";
//                } else {
//                    phoneNumber = responseData.getData().get(0).getResult().getPhone().getMobile();
//                }

                if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())) {
                    Picasso.with(BloggerProfileActivity.this).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
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
                    SharedPrefUtils.setFacebookConnectedFlag(BloggerProfileActivity.this, "1");
                } else {
                    SharedPrefUtils.setFacebookConnectedFlag(BloggerProfileActivity.this,
                            responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                }
            } else {
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {

        }
    };

    private Callback<ArticleDetailResponse> isFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }

            ArticleDetailResponse responseData = (ArticleDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                followButton.setEnabled(true);
                unfollowButton.setEnabled(true);
                if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                    followButton.setVisibility(View.VISIBLE);
                    unfollowButton.setVisibility(View.INVISIBLE);
                    isFollowing = false;
                } else {
                    followButton.setVisibility(View.INVISIBLE);
                    unfollowButton.setVisibility(View.VISIBLE);
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
                showToast("connection timed out");
            } else {
                showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followButton.setVisibility(View.VISIBLE);
                    unfollowButton.setVisibility(View.INVISIBLE);
                    isFollowing = false;
                    int followerCount = Integer.parseInt(followerCountTextView.getText().toString()) - 1;
                    followerCountTextView.setText("" + followerCount);
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
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    followButton.setVisibility(View.INVISIBLE);
                    unfollowButton.setVisibility(View.VISIBLE);
                    int followerCount = Integer.parseInt(followerCountTextView.getText().toString()) + 1;
                    followerCountTextView.setText("" + followerCount);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unfollowTextView:
            case R.id.followTextView:
                hitFollowUnfollowAPI();
            case R.id.authorNameTextView:
                break;
            case R.id.authorTypeTextView:
                break;
            case R.id.authorBioTextView:
                break;
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
        }
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
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
            Snackbar.make(rootView, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BloggerProfileActivity.this,
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
    }

    /**
     * Requests the Storage permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
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
            Snackbar.make(rootView, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(BloggerProfileActivity.this, PERMISSIONS_EDIT_PICTURE,
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

    public void chooseImageOptionPopUp(ImageView profileImageView) {
        final PopupMenu popup = new PopupMenu(this, profileImageView);
        popup.getMenuInflater().inflate(R.menu.profile_image_upload_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.camera) {
//                    mClickListener.onBtnClick(position);
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
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
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
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(this.getCacheDir(), destinationFileName)));
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
                                 Picasso.with(BloggerProfileActivity.this).invalidate(SharedPrefUtils.getProfileImgUrl(BloggerProfileActivity.this));
                                 Picasso.with(BloggerProfileActivity.this).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                                 SharedPrefUtils.setProfileImgUrl(BloggerProfileActivity.this, responseModel.getData().getResult().getUrl());

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
                if (resultCode == this.RESULT_OK) {
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
