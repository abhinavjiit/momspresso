package com.mycity4kids.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.widget.HorizontalScrollCustomView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import life.knowledge4.videotrimmer.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 5/1/17.
 */
public class AllVideoSectionActivity extends BaseActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private Toolbar mToolbar;
    private HorizontalScrollCustomView momspressoSection, funnyVideosSection;
    private View rootLayout;

    private ArrayList<ArticleListingResult> mMomspressoArticleListing;
    private ArrayList<VlogsListingAndDetailResult> funnyVideosListing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_video_section_activity);
        rootLayout = findViewById(R.id.rootLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        momspressoSection = (HorizontalScrollCustomView) findViewById(R.id.momspressoSection);
        funnyVideosSection = (HorizontalScrollCustomView) findViewById(R.id.funnyVideosSection);
        momspressoSection.setSectionTitle(getString(R.string.home_sections_title_momspresso));
        funnyVideosSection.setSectionTitle(getString(R.string.home_sections_title_funny_videos));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VIDEOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMomspressoArticleListing = new ArrayList<>();
        funnyVideosListing = new ArrayList<>();

//        String momspressoCategoryId = getMomspressoCategory();
        hitMomspressoListingApi(AppConstants.MOMSPRESSO_CATEGORYID);
        hitFunnyVideosListingApi();
    }

    private void hitMomspressoListingApi(String momspressoCategoryId) {
//        momspressoProgressbar.setVisibility(View.VISIBLE);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(momspressoCategoryId, 0, 1, 10, SharedPrefUtils.getLanguageFilters(this));
        filterCall.enqueue(momspressoListingResponseCallback);
    }

    private void hitFunnyVideosListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 9, 0, 3);
        callRecentVideoArticles.enqueue(funnyVideosResponseCallback);
    }

    private Callback<ArticleListingResponse> momspressoListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            momspressoProgressbar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processMomspressoListingResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private Callback<VlogsListingResponse> funnyVideosResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            if (response == null || response.body() == null) {
                showToast("Something went wrong from server");
                return;
            }

            try {
                VlogsListingResponse responseData = (VlogsListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processFunnyVideosResponse(responseData);
                } else {
                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processMomspressoListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            momspressoSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            mMomspressoArticleListing.clear();
            mMomspressoArticleListing.addAll(responseData.getData().get(0).getResult());
            momspressoSection.setmDatalist(mMomspressoArticleListing, Constants.KEY_MOMSPRESSO);
        }
    }

    private void processFunnyVideosResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            funnyVideosSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            funnyVideosListing.clear();
            funnyVideosListing.addAll(responseData.getData().get(0).getResult());
            funnyVideosSection.setVlogslist(funnyVideosListing, "allvideosection");
        }
    }

//    private String getMomspressoCategory() {
//        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getMomspressoCategory(this).getId())) {
//            try {
//                FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
//                String fileContent = AppUtils.convertStreamToString(fileInputStream);
//                TopicsResponse responseData = new Gson().fromJson(fileContent, TopicsResponse.class);
//
//                for (int i = 0; i < responseData.getData().size(); i++) {
//                    if (AppConstants.MOMSPRESSO_CATEGORYID.equals(responseData.getData().get(i).getId())) {
//                        SharedPrefUtils.setMomspressoCategory(this, responseData.getData().get(i));
//                        return responseData.getData().get(i).getId();
//                    }
//                }
//            } catch (FileNotFoundException fnfe) {
//
//            }
//        } else {
//            return SharedPrefUtils.getMomspressoCategory(this).getId();
//        }
//        return null;
//    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == AppConstants.REQUEST_VIDEO_TRIMMER) {
            final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(this, uri);
        if (null != filepath && filepath.endsWith(".mp4")) {
            intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
            startActivity(intent);
        } else {
            showToast("please choose a .mp4 format file");
        }
    }

    public void requestPermissions(final String imageFrom) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(rootLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions(imageFrom);
        }
    }

    private void requestUngrantedPermissions(String imageFrom) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_STORAGE_CAMERA.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE_CAMERA[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        if ("gallery".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_GALLERY_PERMISSION);
        } else if ("camera".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            Log.i("Permissions", "Received response for camera permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.setType("video/mp4");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
