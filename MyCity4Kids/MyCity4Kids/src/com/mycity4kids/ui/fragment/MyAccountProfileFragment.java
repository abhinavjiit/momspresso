package com.mycity4kids.ui.fragment;

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
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
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
public class MyAccountProfileFragment extends BaseFragment implements View.OnClickListener {

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
    private TextView followingCountTextView, followerCountTextView, rankCountTextView;
    private TextView authorNameTextView, authorTypeTextView, authorBioTextView;
    private ImageView imgProfile;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.myaccount_profile_activity, container, false);
        authorNameTextView = (TextView) rootView.findViewById(R.id.authorNameTextView);
        authorTypeTextView = (TextView) rootView.findViewById(R.id.authorTypeTextView);
        authorBioTextView = (TextView) rootView.findViewById(R.id.authorBioTextView);
        followingCountTextView = (TextView) rootView.findViewById(R.id.followingCountTextView);
        followerCountTextView = (TextView) rootView.findViewById(R.id.followerCountTextView);
        rankCountTextView = (TextView) rootView.findViewById(R.id.rankCountTextView);

        imgProfile = (ImageView) rootView.findViewById(R.id.imgProfile);

        authorNameTextView.setOnClickListener(this);
        authorTypeTextView.setOnClickListener(this);
        authorBioTextView.setOnClickListener(this);
        imgProfile.setOnClickListener(this);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(getActivity()))) {
            Picasso.with(getActivity()).load(SharedPrefUtils.getProfileImgUrl(getActivity())).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserDetails();
            }
        }, 1000);

    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }
        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
        call.enqueue(userDetailsResponseListener);
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
                    Picasso.with(getActivity()).load(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp())
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
                    SharedPrefUtils.setFacebookConnectedFlag(getActivity(), "1");
                } else {
                    SharedPrefUtils.setFacebookConnectedFlag(getActivity(),
                            responseData.getData().get(0).getResult().getSocialTokens().getFb().getIsExpired());
                }
            } else {
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {

        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authorNameTextView:
                break;
            case R.id.authorTypeTextView:
                break;
            case R.id.authorBioTextView:
                break;
            case R.id.imgProfile:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.");
                        requestCameraAndStoragePermissions();
                    } else if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
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
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
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
                                    .requestPermissions(getActivity(), PERMISSIONS_EDIT_PICTURE,
                                            REQUEST_EDIT_PICTURE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_EDIT_PICTURE, REQUEST_EDIT_PICTURE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    public void chooseImageOptionPopUp(ImageView profileImageView) {
        final PopupMenu popup = new PopupMenu(getActivity(), profileImageView);
        popup.getMenuInflater().inflate(R.menu.profile_image_upload_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.camera) {
//                    mClickListener.onBtnClick(position);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(300, 300);
        uCrop.start(getActivity());
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
                                 Picasso.with(getActivity()).invalidate(SharedPrefUtils.getProfileImgUrl(getActivity()));
                                 Picasso.with(getActivity()).load(responseModel.getData().getResult().getUrl())
                                         .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(imgProfile);
                                 SharedPrefUtils.setProfileImgUrl(getActivity(), responseModel.getData().getResult().getUrl());

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
                if (resultCode == getActivity().RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(getActivity(), resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }
}
