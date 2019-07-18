package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.SearchVideoResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.UserReadArticlesContentActivity;
import com.mycity4kids.ui.activity.VideoTrimmerActivity;
import com.mycity4kids.ui.adapter.MyFunnyVideosListingAdapter;
import com.mycity4kids.ui.adapter.SearchVideosListingAdapter;
import com.mycity4kids.ui.adapter.SeenVideosAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hemant on 13/1/17.
 */
public class UserSeenFunnyVideosTabFragment extends BaseFragment implements View.OnClickListener, SeenVideosAdapter.RecyclerViewClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    SeenVideosAdapter articlesListingAdapter;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    private boolean isLastPageReached = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String searchName = "";
    private int nextPageNumber = 1;
    private boolean isReuqestRunning = true;

    private RelativeLayout mLodingView;
    SearchVideosListingAdapter searchVideosListingAdapter;
    ArrayList<SearchVideoResult> videoList;
    RecyclerView recyclerView;
    TextView noBlogsTextView;
    private ProgressBar progressBar;
    private ListView listView;

    private View rootLayout;
    private RelativeLayout firstUploadLayout;
    private TextView getStartedTextView;
    int chunk = 0;
    RecyclerView seenVideosRecyclerView;
    private int sortType = 0;

    private int limit = 0;

    private String authorId;
    private String fromScreen;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_seen_videos, container, false);

        rootLayout = view.findViewById(R.id.rootLayout);
        listView = (ListView) view.findViewById(R.id.vlogsListView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        firstUploadLayout = (RelativeLayout) view.findViewById(R.id.firstUploadLayout);
        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);
        listView.setVisibility(View.GONE);
        seenVideosRecyclerView = (RecyclerView) view.findViewById(R.id.seenVideos);
        authorId = getArguments().getString(Constants.AUTHOR_ID);
        seenVideosRecyclerView.setVisibility(View.VISIBLE);

        if (StringUtils.isNullOrEmpty(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        }

        getStartedTextView.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);

//        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        seenVideosRecyclerView.setLayoutManager(llm);
        articleDataModelsNew = new ArrayList<VlogsListingAndDetailResult>();


        articlesListingAdapter = new SeenVideosAdapter(getActivity(), this);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        seenVideosRecyclerView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();
        hitArticleListingApi();
        seenVideosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitArticleListingApi();
                        }
                    }
                }
            }
        });

     /*   listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        });*/


     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (view.getId() == R.id.shareImageView) {
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);

                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareUrl = parentingListData.getUrl();
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage = getString(R.string.check_out_blog) + "\"" +
                                parentingListData.getTitle() + "\" by " + parentingListData.getAuthor().getFirstName() + ".";
                    } else {
                        shareMessage = getString(R.string.check_out_blog) + "\"" +
                                parentingListData.getTitle() + "\" by " + parentingListData.getAuthor().getFirstName() + ".\nRead Here: " + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                }
                if (view.getId() == R.id.articleImageView || view.getId() == R.id.articleTitleTextView || view.getId() == R.id.dateTextView) {
                    Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
                    if (adapterView.getAdapter() instanceof MyFunnyVideosListingAdapter) {
                        VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);
                        switch (parentingListData.getPublication_status()) {
                            case AppConstants.VIDEO_STATUS_DRAFT: {
                                if (isAdded())
                                    ((UserReadArticlesContentActivity) getActivity()).showToast("This video is draft");
                                break;
                            }
                            case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
                                if (isAdded())
                                    ((UserReadArticlesContentActivity) getActivity()).showToast("This video is Pending For Approval. Playing is disabled");
                                break;
                            }
                            case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
                                if (isAdded())
                                    ((UserReadArticlesContentActivity) getActivity()).showToast("This video's approval has been cancelled. Playing is disabled");
                                break;
                            }
                            case AppConstants.VIDEO_STATUS_PUBLISHED: {
//                            showToast("This video is Published");
                                intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                                intent.putExtra(Constants.STREAM_URL, parentingListData.getUrl());
                                intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                                intent.putExtra(Constants.FROM_SCREEN, "My Funny Videos Screen");
                                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "My Funny Videos");
                                intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                                intent.putExtra(Constants.AUTHOR, parentingListData.getAuthor().getId() + "~" + parentingListData.getAuthor().getFirstName() + " " + parentingListData.getAuthor().getLastName());
                                startActivity(intent);
                                break;
                            }
                            case AppConstants.VIDEO_STATUS_UNPUBLISHED: {
                                if (isAdded())
                                    ((UserReadArticlesContentActivity) getActivity()).showToast("This video has been unpublished");
                                break;
                            }
                        }

                    }
                }*/

        // });
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi() {


        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getAuthorsSeenVideos(authorId, 10, chunk, "videos");
        callRecentVideoArticles.enqueue(userVideosListResponseCallback);
        progressBar.setVisibility(View.VISIBLE);

    }

    private Callback<VlogsListingResponse> userVideosListResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    chunk = responseData.getData().get(0).getChunks();
                    processResponse(responseData);
//                    notificationCenterResultArrayList.addAll(responseData.getData().getResult());
//                    notificationCenterListAdapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((UserReadArticlesContentActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
          /*  if (isAdded())
                if (!isLastPageReached && !isReuqestRunning) {
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.went_wrong));

                }*/
        }
    };

    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);

            }
        } else {

           /* if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                shortStoriesAdapter.setListData(articleDataModelsNew);
                shortStoriesAdapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            shortStoriesAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            shortStoriesAdapter.notifyDataSetChanged();*/

            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();


            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

  /*  @Override
    public void onRefresh() {
        isLastPageReached = false;
        nextPageNumber = 1;
        hitArticleListingApi();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentSortFAB:
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitArticleListingApi();
                break;
            case R.id.popularSortFAB:
                isLastPageReached = false;
                articleDataModelsNew.clear();
                articlesListingAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitArticleListingApi();
                break;
            case R.id.searchAllImageView:
                Intent searchIntent = new Intent(getActivity(), SearchAllActivity.class);
                searchIntent.putExtra(Constants.FILTER_NAME, "");
                searchIntent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(searchIntent);
                break;
            case R.id.getStartedTextView:
                SharedPrefUtils.setFirstVideoUploadFlag(BaseApplication.getAppContext(), true);
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "myfunnyvideos");
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                // chooseVideoUploadOptionDialogFragment.setTargetFragment(this, 1111);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == AppConstants.REQUEST_VIDEO_TRIMMER) {
            final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                if (isAdded())
                    ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.toast_cannot_retrieve_selected_video));
            }
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(getActivity(), uri);
        if (null != filepath && (filepath.endsWith(".mp4") || filepath.endsWith(".MP4"))) {
            intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(getActivity(), uri));
            startActivity(intent);
        } else {
            if (isAdded())
                ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.choose_mp4_file));
        }
    }

    public void requestPermissions(final String imageFrom) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
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
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
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
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_STORAGE_CAMERA[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        if ("gallery".equals(imageFrom)) {
            ActivityCompat.requestPermissions(getActivity(), requiredPermission, REQUEST_GALLERY_PERMISSION);
        } else if ("camera".equals(imageFrom)) {
            ActivityCompat.requestPermissions(getActivity(), requiredPermission, REQUEST_CAMERA_PERMISSION);
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


    @Override
    public void onPause() {
        super.onPause();
        chunk = 0;
    }

    @Override
    public void onClick(View view, int position) {

        if (view.getId() == R.id.shareImageView) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareUrl = articleDataModelsNew.get(position).getSharing_url();
            String shareMessage;
            if (StringUtils.isNullOrEmpty(shareUrl)) {
                shareMessage = getString(R.string.check_out_blog) + "\"" +
                        articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".";
            } else {
                shareMessage = getString(R.string.check_out_blog) + "\"" +
                        articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".\nRead Here: " + articleDataModelsNew.get(position).getSharing_url();
            }
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Momspresso"));
        } else {
            Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);

            intent.putExtra(Constants.VIDEO_ID, articleDataModelsNew.get(position).getId());
            intent.putExtra(Constants.FROM_SCREEN, "Search Screen");
            startActivity(intent);
        }


    }
}