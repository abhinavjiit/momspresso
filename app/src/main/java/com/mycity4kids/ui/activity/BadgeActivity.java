package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.BadgeListResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.BadgesDialogFragment;
import com.mycity4kids.retrofitAPIsInterfaces.BadgeAPI;
import com.mycity4kids.ui.adapter.BadgeListGridAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class BadgeActivity extends BaseActivity implements View.OnClickListener, BadgeListGridAdapter.BadgeSelect {

    private static final int RC_STORAGE_PERM = 123;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String FILENAME = "badgelist";
    private GridView gridview;
    private BadgeListGridAdapter adapter;
    private ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> badgeList;
    private int pageNumber = 1;
    private RelativeLayout loadingView;
    private ProgressBar progressBar;
    private String userId;
    private TextView shareTextView;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        rootLayout = findViewById(R.id.rootView);
        gridview = (GridView) findViewById(R.id.gridview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        shareTextView = (TextView) findViewById(R.id.shareTextView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getStringExtra(Constants.USER_ID);
        if (StringUtils.isNullOrEmpty(userId)) {
            showToast(getString(R.string.empty_screen));
            return;
        }
        shareTextView.setOnClickListener(this);
        if (AppUtils.isPrivateProfile(userId)) {
            Utils.pushGenericEvent(this, "Show_Private_All_Badges", userId, "BadgeActivity");
            shareTextView.setVisibility(View.VISIBLE);
        } else {
            Utils.pushGenericEvent(this, "Show_Public_All_Badges", userId, "BadgeActivity");
            if (!BuildConfig.DEBUG) {
                shareTextView.setVisibility(View.GONE);
            }
        }
        badgeList = new ArrayList<>();
        adapter = new BadgeListGridAdapter(this);
        adapter.setDatalist(badgeList);
        gridview.setAdapter(adapter);
        getBadgeList();
        showProgressDialog(getString(R.string.please_wait));
    }

    private void getBadgeList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BadgeAPI badgeApi = retrofit.create(BadgeAPI.class);
        Call<BadgeListResponse> badgeListResponseCall = badgeApi.getBadgeList(userId);
        badgeListResponseCall.enqueue(badgeListCall);
    }

    private Callback<BadgeListResponse> badgeListCall = new Callback<BadgeListResponse>() {
        @Override
        public void onResponse(Call<BadgeListResponse> call, retrofit2.Response<BadgeListResponse> response) {
            progressBar.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            BadgeListResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                if (responseModel.getData() != null && !responseModel.getData().isEmpty()
                        && responseModel.getData().get(0) != null) {
                    processResponse(responseModel.getData());
                } else {
                    showToast(responseModel.getReason());
                }
            }
        }

        @Override
        public void onFailure(Call<BadgeListResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            apiExceptions(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponse(List<BadgeListResponse.BadgeListData> data) {
        ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> datalist = data.get(0).getResult();
        if (datalist == null || datalist.size() == 0) {
            if (null == badgeList || badgeList.isEmpty()) {
                badgeList = datalist;
                adapter.setDatalist(datalist);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (pageNumber == 1) {
                badgeList = datalist;
            } else {
                badgeList.addAll(datalist);
            }
            adapter.setDatalist(badgeList);
            pageNumber = pageNumber + 1;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareTextView:
                if (badgeList.size() > 0) {
                    checkPermissionAndShareBadgeList();
                }
                break;
            default:
                break;
        }
    }

    private void checkPermissionAndShareBadgeList() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                shareBadgeList();
            }
        } else {
            shareBadgeList();
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, RC_STORAGE_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == RC_STORAGE_PERM) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                shareBadgeList();
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void shareBadgeList() {
        AppUtils.getBitmapFromView(gridview, FILENAME);
        Uri uri = Uri
                .parse("file://" + BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator + FILENAME
                        + ".jpg");
        String shareUrl = getString(R.string.badges_list_share_text,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getFirst_name(),
                "https://www.momspresso.com/user/" + userId + "/badges");
        AppUtils.shareGenericImageAndOrLink(this, uri, shareUrl);
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
    public void onBadgeSelected(String imageUrl, String shareUrl, int position) {
        BadgesDialogFragment badgesDialogFragment = new BadgesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, userId);
        bundle.putString("id", badgeList.get(position).getId());
        badgesDialogFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        badgesDialogFragment.show(fm, "BadgeDetailDialog");
    }
}
