package com.mycity4kids.ui.videochallengenewui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.fragment.ChallengeDetailFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.utils.PermissionUtil;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class NewVideoChallengeActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private Toolbar toolbar;
    private String jsonMyObject;
    private String selectedId, mappedId;
    String screen;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    String challengeRules = "";
    private int pos;
    private Topics topic;
    private CoordinatorLayout rootLayout;
    private String challengeId;
    private String mappedCategory;
    private int max_Duration;
    private String parentName, parentId;
    private CoordinatorLayout coordinatorLayout;
    private ImageView thumbNail;
    private int duration;
    private String comingFrom = "";
    private CoordinatorLayout momVlogCoachMark;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_video_listing_detail);
        rootLayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
        frameLayout = findViewById(R.id.container_layout);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("comingFrom")) {
                comingFrom = intent.getStringExtra("comingFrom");
            }
            challengeId = intent.getStringExtra("challenge");
        }
        fetchChallengeDetail(challengeId);
    }


    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    private void fetchChallengeDetail(String challengeId) {

        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<Topics> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogChallengeDetails(challengeId);
        callRecentVideoArticles.enqueue(vlogChallengeDetailsResponseCallBack);

    }

    private Callback<Topics> vlogChallengeDetailsResponseCallBack = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, retrofit2.Response<Topics> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    Topics responseData = response.body();
                    if (responseData != null) {
                        selected_Name = responseData.getDisplay_name();
                        selectedActiveUrl = responseData.getExtraData().get(0).getChallenge().getImageUrl();
                        selectedId = responseData.getId();
                        topic = responseData;
                        selectedStreamUrl = responseData.getExtraData().get(0).getChallenge().getVideoUrl();
                        challengeRules = responseData.getExtraData().get(0).getChallenge().getRules();
                        max_Duration = responseData.getExtraData().get(0).getChallenge().getMax_duration();
                        if (StringUtils.isNullOrEmpty(responseData.getExtraData().get(0).getChallenge().getMapped_category())) {
                            mappedCategory = "category-6dfcf8006c794d4e852343776302f588";
                        } else {
                            mappedCategory = responseData.getExtraData().get(0).getChallenge().getMapped_category();
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("selected_Name", selected_Name);
                        bundle.putString("selectedActiveUrl", selectedActiveUrl);
                        bundle.putString("selectedId", selectedId);
                        bundle.putString("selectedStreamUrl", selectedStreamUrl);
                        bundle.putString("challengeRules", challengeRules);
                        bundle.putString("mappedCategory", mappedCategory);
                        bundle.putInt("max_Duration", max_Duration);
                        bundle.putParcelable("topic", topic);
                        bundle.putString("comingFrom", comingFrom);
                        Fragment fragment = new ChallengeDetailFragment();
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container_layout, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }


        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case AppConstants.REQUEST_VIDEO_TRIMMER:
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    ChallengeDetailFragment fragment = (ChallengeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container_layout);
                    if (fragment != null && fragment.isAdded()) {
                        fragment.startTrimActivity(selectedUri);
                    }
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
                break;
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


    public void chooseAndpermissionDialog(int max_Duration) {
        ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("activity", "challengeDetailFragment");
        _args.putString("duration", String.valueOf(max_Duration));
        chooseVideoUploadOptionDialogFragment.setArguments(_args);
        chooseVideoUploadOptionDialogFragment.setCancelable(true);
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");


    }
}


