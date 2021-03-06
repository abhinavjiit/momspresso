package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.UserPublishedContentActivity;
import com.mycity4kids.ui.adapter.UserPublishedVideosListingAdapter;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.vlogs.VideoCategoryAndChallengeSelectionActivity;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/1/17.
 */
public class UserPublishedVideosTabFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, UserPublishedVideosListingAdapter.IEditVlog {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static final int VIDEO_PUBLISHED_STATUS = 3;
    private static final int RESULT_OK = -1;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private UserPublishedVideosListingAdapter articlesListingAdapter;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    private ProgressBar progressBar;
    private ListView listView;
    private RelativeLayout loadingView;
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
    private boolean isPrivateProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.vlogs_listing_activity, container, false);

        rootLayout = view.findViewById(R.id.rootLayout);
        listView = (ListView) view.findViewById(R.id.vlogsListView);
        loadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
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

        articlesListingAdapter = new UserPublishedVideosListingAdapter(this, isPrivateProfile);
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
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning
                        && !isLastPageReached) {
                    loadingView.setVisibility(View.VISIBLE);
                    hitArticleListingApi();
                    isReuqestRunning = true;
                }
            }
        });

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {

            Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
            if (adapterView.getAdapter() instanceof UserPublishedVideosListingAdapter) {
                VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) adapterView
                        .getAdapter().getItem(i);
                switch (parentingListData.getPublication_status()) {
                    case AppConstants.VIDEO_STATUS_DRAFT: {
                        if (isAdded()) {
                            ((UserPublishedContentActivity) getActivity()).showToast("This video is draft");
                        }
                        break;
                    }
                    case AppConstants.VIDEO_STATUS_APPROVAL_PENDING: {
                        if (isAdded()) {
                            ((UserPublishedContentActivity) getActivity())
                                    .showToast("This video is Pending For Approval. Playing is disabled");
                        }
                        break;
                    }
                    case AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED: {
                        if (isAdded()) {
                            ((UserPublishedContentActivity) getActivity())
                                    .showToast("This video's approval has been cancelled. Playing is disabled");
                        }
                        break;
                    }
                    case AppConstants.VIDEO_STATUS_PUBLISHED: {
                        intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                        intent.putExtra(Constants.STREAM_URL, parentingListData.getUrl());
                        intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                        intent.putExtra(Constants.FROM_SCREEN, "My Funny Videos Screen");
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "My Funny Videos");
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                        intent.putExtra(Constants.AUTHOR,
                                parentingListData.getAuthor().getId() + "~" + parentingListData.getAuthor()
                                        .getFirstName() + " " + parentingListData.getAuthor().getLastName());
                        startActivity(intent);
                        break;
                    }
                    case AppConstants.VIDEO_STATUS_UNPUBLISHED: {
                        if (isAdded()) {
                            ((UserPublishedContentActivity) getActivity())
                                    .showToast("This video has been unpublished");
                        }
                        break;
                    }
                    default:
                        break;
                }

            }
        });
        return view;
    }

    private void hitArticleListingApi() {
        int from = (nextPageNumber - 1) * limit;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles;
        if (isPrivateProfile) {
            callRecentVideoArticles = vlogsListingAndDetailsApi
                    .getPublishedVlogs(authorId, from, from + limit - 1, sortType);
        } else {
            callRecentVideoArticles = vlogsListingAndDetailsApi
                    .getPublishedVlogsForPublicProfile(authorId, from, from + limit - 1, sortType,
                            VIDEO_PUBLISHED_STATUS);
        }

        callRecentVideoArticles.enqueue(userVideosListResponseCallback);
        progressBar.setVisibility(View.VISIBLE);
    }

    private Callback<VlogsListingResponse> userVideosListResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
                } else {
                    if (isAdded()) {
                        ((UserPublishedContentActivity) getActivity()).showToast(responseData.getReason());
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded()) {
                    ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            if (nextPageNumber == 1) {
                firstUploadLayout.setVisibility(View.GONE);
                noBlogsTextView.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.INVISIBLE);
            FirebaseCrashlytics.getInstance().recordException(t);
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
                if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                        .equals(authorId)) {
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
                Intent intent = new Intent(getActivity(), VideoCategoryAndChallengeSelectionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
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
            spannableString.setSpan(new CustomTypeFace("", myTypeface), 0, spannableString.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(spannableString);
            if (AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED
                    .equals(articleDataModelsNew.get(position).getPublication_status())) {
                if (menuItem.getItemId() == R.id.deleteVlog) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }
        }
        popup.setOnMenuItemClickListener(item -> {
            int i = item.getItemId();
            if (i == R.id.edit_vlog) {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putString("vlogTitle", articleDataModelsNew.get(position).getTitle());
                args.putString("videoId", articleDataModelsNew.get(position).getId());
                EditVlogTitleDialogFragment editVlogTitleDialogFragment = new EditVlogTitleDialogFragment();
                editVlogTitleDialogFragment.setArguments(args);
                editVlogTitleDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                editVlogTitleDialogFragment.show(fm, "Choose video option");
                return true;
            }
            if (i == R.id.deleteVlog) {
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
                Call call = vlogsListingAndDetailsApi.deleteVlog(articleDataModelsNew.get(position).getId());
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        isLastPageReached = false;
                        articleDataModelsNew.clear();
                        articlesListingAdapter.notifyDataSetChanged();
                        sortType = 0;
                        nextPageNumber = 1;
                        hitArticleListingApi();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        isLastPageReached = false;
                        articleDataModelsNew.clear();
                        articlesListingAdapter.notifyDataSetChanged();
                        sortType = 0;
                        nextPageNumber = 1;
                        hitArticleListingApi();
                    }
                });
                return true;
            } else {
                return true;
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
