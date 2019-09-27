package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.UserPublishedContentActivity;
import com.mycity4kids.ui.activity.VideoTrimmerActivity;
import com.mycity4kids.ui.adapter.MyFunnyVideosListingAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hemant on 13/1/17.
 */
public class UserFunnyVideosTabFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MyFunnyVideosListingAdapter.IEditVlog {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private final static int VIDEO_PUBLISHED_STATUS = 3;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    MyFunnyVideosListingAdapter articlesListingAdapter;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    private ProgressBar progressBar;
    private ListView listView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private View rootLayout;
    private RelativeLayout firstUploadLayout;
    private TextView getStartedTextView;

    private int sortType = 0;
    private int nextPageNumber;
    private int limit = 10;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private String authorId;
    private String fromScreen;
    private View view;
    private boolean isPrivateProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.vlogs_listing_activity, container, false);

        rootLayout = view.findViewById(R.id.rootLayout);
        listView = (ListView) view.findViewById(R.id.vlogsListView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        firstUploadLayout = (RelativeLayout) view.findViewById(R.id.firstUploadLayout);
        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);

        authorId = getArguments().getString(Constants.AUTHOR_ID);
        isPrivateProfile = getArguments().getBoolean("isPrivateProfile");

        if (StringUtils.isNullOrEmpty(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        }

        getStartedTextView.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);

        articleDataModelsNew = new ArrayList<>();
        nextPageNumber = 1;
        hitArticleListingApi();

        articlesListingAdapter = new MyFunnyVideosListingAdapter(getActivity(), this, isPrivateProfile);
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

                Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
                if (adapterView.getAdapter() instanceof MyFunnyVideosListingAdapter) {
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView.getAdapter().getItem(i);
                    switch (parentingListData.getPublication_status()) {
                        case AppConstants.VIDEO_STATUS_DRAFT: {
                            if (isAdded())
                                ((UserPublishedContentActivity) getActivity()).showToast("This video is draft");
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
                            if (isAdded())
                                ((UserPublishedContentActivity) getActivity()).showToast("This video is Pending For Approval. Playing is disabled");
                            break;
                        }
                        case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
                            if (isAdded())
                                ((UserPublishedContentActivity) getActivity()).showToast("This video's approval has been cancelled. Playing is disabled");
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
                                ((UserPublishedContentActivity) getActivity()).showToast("This video has been unpublished");
                            break;
                        }
                    }

                }
            }
        });
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void hitArticleListingApi() {
        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles;
        if (isPrivateProfile) {
            callRecentVideoArticles = vlogsListingAndDetailsAPI.getPublishedVlogs(authorId, from, from + limit - 1, sortType);
        } else {
            callRecentVideoArticles = vlogsListingAndDetailsAPI.getPublishedVlogsForPublicProfile(authorId, from, from + limit - 1, sortType, VIDEO_PUBLISHED_STATUS);
        }

        callRecentVideoArticles.enqueue(userVideosListResponseCallback);
        progressBar.setVisibility(View.VISIBLE);
    }

    private Callback<VlogsListingResponse> userVideosListResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                } else {
                    if (isAdded())
                        ((UserPublishedContentActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.went_wrong));
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
        }
    };

    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                isLastPageReached = true;
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
                if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId)) {
                    firstUploadLayout.setVisibility(View.VISIBLE);
                    noBlogsTextView.setVisibility(View.GONE);
                } else {
                    firstUploadLayout.setVisibility(View.GONE);
                    noBlogsTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
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
        isLastPageReached = false;
        nextPageNumber = 1;
        hitArticleListingApi();
    }

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
                chooseVideoUploadOptionDialogFragment.setTargetFragment(this, 1111);
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
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.toast_cannot_retrieve_selected_video));
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
                ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.choose_mp4_file));
        }
    }

    public void requestPermissions(final String imageFrom) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onVlogEdit(int position, ImageView imageView) {
        chooseImageOptionPopUp(imageView, position);
    }

    void updateTitleInList(int position, String title) {
        articleDataModelsNew.get(position).setTitle(title);
        articlesListingAdapter.notifyDataSetChanged();
    }

    @SuppressLint("RestrictedApi")
    private void chooseImageOptionPopUp(View view, int position) {
        final PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.edit_vlog_details_menu, popup.getMenu());
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + "oswald_regular.ttf");

        for (int i = 0; i < popup.getMenu().size(); i++) {
            MenuItem menuItem = popup.getMenu().getItem(i);
            SpannableString spannableString = new SpannableString(menuItem.getTitle());
            spannableString.setSpan(new CustomTypeFace("", myTypeface), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(spannableString);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.edit_vlog) {
                    EditVlogTitleDialogFragment editVlogTitleDialogFragment = new EditVlogTitleDialogFragment();
                    FragmentManager fm = getChildFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putInt("position", position);
                    _args.putString("vlogTitle", articleDataModelsNew.get(position).getTitle());
                    _args.putString("videoId", articleDataModelsNew.get(position).getId());
                    editVlogTitleDialogFragment.setArguments(_args);
                    editVlogTitleDialogFragment.setCancelable(true);
                    editVlogTitleDialogFragment.show(fm, "Choose video option");
                    return true;
                } else {
                    return true;
                }
            }
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }


    public class CustomTypeFace extends TypefaceSpan {
        private final Typeface typeface;

        CustomTypeFace(String family, Typeface type) {
            super(family);
            typeface = type;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, typeface);
        }

        @Override
        public void updateMeasureState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, typeface);
        }

        private void applyCustomTypeFace(Paint paint, Typeface typeface) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~typeface.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(typeface);
        }
    }
}
