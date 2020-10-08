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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.fragment.ChallengeDetailFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class NewVideoChallengeActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private String selectedId;
    private String selectedName;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    String challengeRules = "";
    private Topics topic;
    private CoordinatorLayout rootLayout;
    private String challengeId;
    private String mappedCategory;
    private int maxDuration;
    private String comingFrom = "";
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_video_listing_detail);
        rootLayout = findViewById(R.id.mainprofile_parent_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
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
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<Topics> callRecentVideoArticles = vlogsListingAndDetailsApi.getVlogChallengeDetails(challengeId);
        callRecentVideoArticles.enqueue(vlogChallengeDetailsResponseCallBack);
    }

    private Callback<Topics> vlogChallengeDetailsResponseCallBack = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, retrofit2.Response<Topics> response) {
            removeProgressDialog();
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    Topics responseData = response.body();
                    selectedName = responseData.getDisplay_name();
                    selectedActiveUrl = responseData.getExtraData().get(0).getChallenge().getImageUrl();
                    selectedId = responseData.getId();
                    topic = responseData;
                    selectedStreamUrl = responseData.getExtraData().get(0).getChallenge().getVideoUrl();
                    challengeRules = responseData.getExtraData().get(0).getChallenge().getRules();
                    maxDuration = responseData.getExtraData().get(0).getChallenge().getMax_duration();
                    if (StringUtils
                            .isNullOrEmpty(responseData.getExtraData().get(0).getChallenge().getMapped_category())) {
                        mappedCategory = "category-6dfcf8006c794d4e852343776302f588";
                    } else {
                        mappedCategory = responseData.getExtraData().get(0).getChallenge().getMapped_category();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("selected_Name", selectedName);
                    bundle.putString("selectedActiveUrl", selectedActiveUrl);
                    bundle.putString("selectedId", selectedId);
                    bundle.putString("selectedStreamUrl", selectedStreamUrl);
                    bundle.putString("challengeRules", challengeRules);
                    bundle.putString("mappedCategory", mappedCategory);
                    bundle.putInt("max_Duration", maxDuration);
                    bundle.putParcelable("topic", topic);
                    bundle.putString("comingFrom", comingFrom);
                    Fragment fragment = new ChallengeDetailFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                            .addToBackStack(null).commit();

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View v) {

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
                    ChallengeDetailFragment fragment = (ChallengeDetailFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.container_layout);
                    if (fragment != null && fragment.isAdded()) {
                        fragment.startTrimActivity(selectedUri);
                    }
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    public void requestPermissions(final String imageFrom) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions(imageFrom))
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(rootLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions(imageFrom))
                    .show();
        } else {
            requestUngrantedPermissions(imageFrom);
        }
    }

    private void requestUngrantedPermissions(String imageFrom) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_STORAGE_CAMERA.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE_CAMERA[i])
                    != PackageManager.PERMISSION_GRANTED) {
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
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.setType("video/mp4");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                        AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void chooseAndpermissionDialog(int maxDuration) {
        Utils.shareEventTracking(this, "Video Challenge", "Vlog_Challenges_Android", "H_VCD_FAB_Challenge");
        Bundle args = new Bundle();
        args.putString("activity", "challengeDetailFragment");
        args.putString("duration", String.valueOf(maxDuration));
        ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment =
                new ChooseVideoUploadOptionDialogFragment();
        chooseVideoUploadOptionDialogFragment.setArguments(args);
        chooseVideoUploadOptionDialogFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


