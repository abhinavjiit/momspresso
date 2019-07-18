package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.MyFunnyVideosListingAdapter;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/1/17.
 */
public class MyFunnyVideosListingActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    MyFunnyVideosListingAdapter articlesListingAdapter;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

//    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
//    private FloatingActionsMenu fabMenu;
    private ListView listView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private Toolbar mToolbar;
//    private FloatingActionButton popularSortFAB, recentSortFAB, fabSort;
//    private FrameLayout frameLayout;
    private View rootLayout;
    private ImageView searchAllImageView;
    private RelativeLayout firstUploadLayout;
    private TextView getStartedTextView;

    private int sortType = 0;
    private int nextPageNumber;
    private int limit = 10;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private String authorId;
    private String fromScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vlogs_listing_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        rootLayout = findViewById(R.id.rootLayout);
        ((BaseApplication) getApplication()).setView(rootLayout);
        listView = (ListView) findViewById(R.id.vlogsListView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchAllImageView = (ImageView) findViewById(R.id.searchAllImageView);
//        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
//        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
//        popularSortFAB = (FloatingActionButton) findViewById(R.id.popularSortFAB);
//        recentSortFAB = (FloatingActionButton) findViewById(R.id.recentSortFAB);
//        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        firstUploadLayout = (RelativeLayout) findViewById(R.id.firstUploadLayout);
        getStartedTextView = (TextView) findViewById(R.id.getStartedTextView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN);
        authorId = getIntent().getStringExtra(Constants.AUTHOR_ID);
        if (StringUtils.isNullOrEmpty(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        }

        searchAllImageView.setOnClickListener(this);
        getStartedTextView.setOnClickListener(this);
//        frameLayout.getBackground().setAlpha(0);
//        popularSortFAB.setOnClickListener(this);
//        recentSortFAB.setOnClickListener(this);
//        fabSort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fabMenu.isExpanded()) {
//                    fabMenu.collapse();
//                } else {
//                    fabMenu.expand();
//                }
//            }
//        });
//        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
//            @Override
//            public void onMenuExpanded() {
//                frameLayout.getBackground().setAlpha(240);
//                frameLayout.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        fabMenu.collapse();
//                        return true;
//                    }
//                });
//            }
//
//            @Override
//            public void onMenuCollapsed() {
//                frameLayout.getBackground().setAlpha(0);
//                frameLayout.setOnTouchListener(null);
//            }
//        });
//
//        popularSortFAB.setVisibility(View.INVISIBLE);
//        recentSortFAB.setVisibility(View.INVISIBLE);
//        fabSort.setVisibility(View.INVISIBLE);
//        fabMenu.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        articleDataModelsNew = new ArrayList<VlogsListingAndDetailResult>();
        nextPageNumber = 1;
        hitArticleListingApi();

//        swipeRefreshLayout.setOnRefreshListener(this);

        articlesListingAdapter = new MyFunnyVideosListingAdapter(this);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        listView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitArticleListingApi();
                    isReuqestRunning = true;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MyFunnyVideosListingActivity.this, VlogsDetailActivity.class);
                if (adapterView.getAdapter() instanceof MyFunnyVideosListingAdapter) {
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);
                    switch (parentingListData.getPublication_status()) {
                        case AppConstants.VIDEO_STATUS_DRAFT: {
                            showToast("This video is draft");
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
                            showToast("This video is Pending For Approval. Playing is disabled");
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
                            showToast("This video's approval has been cancelled. Playing is disabled");
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_PUBLISHED: {
//                            showToast("This video is Published");
                            intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                            intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                            intent.putExtra(Constants.FROM_SCREEN, "My Funny Videos Screen");
                            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "My Funny Videos");
                            intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                            intent.putExtra(Constants.AUTHOR, parentingListData.getAuthor().getId() + "~" + parentingListData.getAuthor().getFirstName() + " " + parentingListData.getAuthor().getLastName());
                            startActivity(intent);
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_UNPUBLISHED: {
                            showToast("This video has been unpublished");
                            break;
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            //   swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        int from = (nextPageNumber - 1) * limit;

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getPublishedVlogs(authorId, from, from + limit - 1, sortType);
        callRecentVideoArticles.enqueue(userVideosListResponseCallback);
        progressBar.setVisibility(View.VISIBLE);

    }

    private Callback<VlogsListingResponse> userVideosListResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
//            swipeRefreshLayout.setRefreshing(false);
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
//                    notificationCenterResultArrayList.addAll(responseData.getData().getResult());
//                    notificationCenterListAdapter.notifyDataSetChanged();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
//            swipeRefreshLayout.setRefreshing(false);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = true;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
                firstUploadLayout.setVisibility(View.VISIBLE);
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
//                popularSortFAB.setVisibility(View.VISIBLE);
//                recentSortFAB.setVisibility(View.VISIBLE);
//                fabSort.setVisibility(View.VISIBLE);
//                fabMenu.setVisibility(View.VISIBLE);
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
//            swipeRefreshLayout.setRefreshing(false);
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        isLastPageReached = false;
        nextPageNumber = 1;
        hitArticleListingApi();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSortFAB:
//                fabMenu.collapse();
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitArticleListingApi();
                break;
            case R.id.popularSortFAB:
//                fabMenu.collapse();
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitArticleListingApi();
                break;
            case R.id.searchAllImageView:
                Intent searchIntent = new Intent(this, SearchAllActivity.class);
                searchIntent.putExtra(Constants.FILTER_NAME, "");
                searchIntent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(searchIntent);
                break;
            case R.id.getStartedTextView:
                SharedPrefUtils.setFirstVideoUploadFlag(this, true);
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "myfunnyvideos");
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
                break;
        }
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
        if (null != filepath && (filepath.endsWith(".mp4") || filepath.endsWith(".MP4"))) {
            intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
            startActivity(intent);
        } else {
            showToast(getString(R.string.choose_mp4_file));
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