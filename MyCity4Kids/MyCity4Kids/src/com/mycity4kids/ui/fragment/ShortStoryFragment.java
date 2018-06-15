package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.request.AddEditShortStoryCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ShortStoryCommentListData;
import com.mycity4kids.models.response.ShortStoryCommentListResponse;
import com.mycity4kids.models.response.ShortStoryDetailResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.BloggerProfileActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.ShortStoriesDetailRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryFragment extends BaseFragment implements View.OnClickListener, AddEditCommentReplyFragment.IAddCommentReply, ShortStoriesDetailRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction {

    private final static int ADD_BOOKMARK = 1;
    ShortStoryCommentRepliesDialogFragment shortStoryCommentRepliesDialogFragment;
    private String paginationCommentId = null;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalCommentCount = 0;
    private int downloadedComment = 0;

    private ShortStoryAPI shortStoryAPI;
    private int bookmarkStatus;
    private int recommendStatus;
    private boolean isFollowing = false;
    private String bookmarkFlag = "0";
    private String recommendationFlag = "0";
    private String commentURL = "";
    private String bookmarkId;
    private String authorId;
    private String author;
    private String articleId;
    private String deepLinkURL;
    private String blogSlug;
    private String titleSlug;
    private String commentType = "db";
    private String commentMainUrl;
    private String userDynamoId;
    private String articleLanguageCategoryId;
    private String from;

    private TextView followClick;

    private RelativeLayout mLodingView;
    private View fragmentView;
    private LayoutInflater mInflater;
    private RecyclerView shortStoryRecyclerView;
    private TextView bookmarkArticleTextView;
    private TextView likeArticleTextView;

    private ShortStoryDetailAndCommentModel headerModel;
    private ArrayList<ShortStoryDetailAndCommentModel> consolidatedList;
    ShortStoriesDetailRecyclerAdapter adapter;
    private FloatingActionButton openAddCommentDialog;
    private int actionItemPosition;
    private String editContent;
    private String editReplyParentCommentId;
    private String editReplyId;
    private int deleteCommentPos;
    private int deleteReplyPos;
    private View shareSSView;
    private TextView titleTextView, bodyTextView, authorTextView;
    private int colorPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        fragmentView = inflater.inflate(R.layout.short_story_fragment, container, false);
        shortStoryRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.shortStoryRecyclerView);
        openAddCommentDialog = (FloatingActionButton) fragmentView.findViewById(R.id.openAddCommentDialog);
        mLodingView = (RelativeLayout) fragmentView.findViewById(R.id.relativeLoadingView);
        shareSSView = fragmentView.findViewById(R.id.shareSSView);
        titleTextView = (TextView) fragmentView.findViewById(R.id.titleTextView);
        bodyTextView = (TextView) fragmentView.findViewById(R.id.bodyTextView);
        authorTextView = (TextView) fragmentView.findViewById(R.id.authorTextView);

        userDynamoId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        deepLinkURL = "";// getIntent().getStringExtra(Constants.DEEPLINK_URL);
        try {
            openAddCommentDialog.setOnClickListener(this);

            Bundle bundle = getArguments();
            if (bundle != null) {
                colorPosition = bundle.getInt("colorPosition", 0);
            }

            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            shortStoryRecyclerView.setLayoutManager(llm);

            headerModel = new ShortStoryDetailAndCommentModel();

            consolidatedList = new ArrayList<>();
            adapter = new ShortStoriesDetailRecyclerAdapter(getActivity(), this, colorPosition);
            adapter.setListData(consolidatedList);
            shortStoryRecyclerView.setAdapter(adapter);
            isReuqestRunning = true;
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                blogSlug = bundle.getString(Constants.BLOG_SLUG);
                titleSlug = bundle.getString(Constants.TITLE_SLUG);
                if (bundle.getBoolean("fromNotification")) {
//                    Utils.pushEventNotificationClick(getActivity(), GTMEventType.NOTIFICATION_CLICK_EVENT, userDynamoId, "Notification Popup", "article_details");
//                    Utils.pushOpenArticleEvent(getActivity(), GTMEventType.ARTICLE_DETAILS_CLICK_EVENT, "Notification", userDynamoId + "", articleId, "-1" + "~Notification", "Notification");
                } else {
                    from = bundle.getString(Constants.ARTICLE_OPENED_FROM);
                }
                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.error_network));
                }
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                shortStoryAPI = retro.create(ShortStoryAPI.class);

                getShortStoryDetails();

                shortStoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                    getStoryComments(articleId, "comment");
                                }
                            }
                        }
                    }
                });

//                hitRecommendedStatusAPI();
            }
        } catch (Exception e) {
            removeProgressDialog();
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return fragmentView;

    }

    private void getShortStoryDetails() {
        Call<ShortStoryDetailResult> call = shortStoryAPI.getShortStoryDetails(articleId, "articleId");
        call.enqueue(storyDetailResponseCallbackRedis);
    }

    private void getShortStoryDetailsFallback() {
        Call<ShortStoryDetailResponse> call = shortStoryAPI.getShortStoryDetailsFallback(articleId);
        call.enqueue(storyDetailResponseCallbackFallback);
    }

    private void getViewCountAPI() {
        Call<ViewCountResponse> call = shortStoryAPI.getViewCount(articleId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void hitBookmarkFollowingStatusAPI() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarFollowingStatusAPI = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(articleId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitUpdateViewCountAPI(String userId, ArrayList<Map<String, String>> tagsList, ArrayList<Map<String, String>> cityList) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(userId);
        updateViewCountRequest.setTags(tagsList);
        updateViewCountRequest.setCities(cityList);
        updateViewCountRequest.setContentType("1");
        Call<ResponseBody> callUpdateViewCount = shortStoryAPI.updateViewCount(articleId, updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private void hitRecommendedStatusAPI() {
        Call<ArticleRecommendationStatusResponse> checkArticleRecommendStaus = shortStoryAPI.getArticleRecommendedStatus(articleId);
        checkArticleRecommendStaus.enqueue(recommendStatusResponseCallback);
    }

    private void recommendUnrecommentArticleAPI(String status) {
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(status);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = shortStoryAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.openAddCommentDialog:
                    AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddShortStoryCommentReplyDialogFragment();
                    FragmentManager fm = getChildFragmentManager();
                    Bundle _args = new Bundle();
                    addGpPostCommentReplyDialogFragment.setArguments(_args);
                    addGpPostCommentReplyDialogFragment.setCancelable(true);
                    addGpPostCommentReplyDialogFragment.setTargetFragment(this, 0);
                    addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCommentAddition(CommentsData cd) {

    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {

    }

    @Override
    public void onReplyAddition(CommentsData cd) {

    }


    private void addRemoveBookmark() {

        if (bookmarkStatus == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleId);
            bookmarkStatus = 1;
            Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmarked);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            Call<AddBookmarkResponse> call = shortStoryAPI.addBookmark(articleDetailRequest);
            call.enqueue(addBookmarkResponseCallback);
            Utils.pushBookmarkArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(bookmarkId);
            bookmarkStatus = 0;
            Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
            bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            Call<AddBookmarkResponse> call = shortStoryAPI.deleteBookmark(deleteBookmarkRequest);
            call.enqueue(addBookmarkResponseCallback);
            Utils.pushUnbookmarkArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", articleId, authorId + "~" + author);
        }
    }

    Callback<ShortStoryDetailResult> storyDetailResponseCallbackRedis = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call, retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                getShortStoryDetailsFallback();
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                headerModel.setSsResult(responseData);
                headerModel.setType(0);
                consolidatedList.add(headerModel);
                getStoryComments(articleId, null);
                getViewCountAPI();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountAPI(responseData.getUserId(), responseData.getTags(), responseData.getCities());
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                getShortStoryDetailsFallback();
            }

        }

        @Override
        public void onFailure(Call<ShortStoryDetailResult> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
            getShortStoryDetailsFallback();
        }
    };

    Callback<ShortStoryDetailResponse> storyDetailResponseCallbackFallback = new Callback<ShortStoryDetailResponse>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResponse> call, retrofit2.Response<ShortStoryDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResponse responseData = response.body();
                headerModel.setSsResult(responseData.getData());
                headerModel.setType(0);
                consolidatedList.add(headerModel);
                getStoryComments(articleId, null);
                getViewCountAPI();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountAPI(responseData.getData().getUserId(), responseData.getData().getTags(), responseData.getData().getCities());
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ShortStoryDetailResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    private void getStoryComments(String id, String commentType) {
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.getStoryComments(id, commentType, paginationCommentId);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<ShortStoryCommentListResponse> ssCommentsResponseCallback = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                ShortStoryCommentListResponse shortStoryCommentListResponse = response.body();
                if (shortStoryCommentListResponse.getCount() != 0) {
                    totalCommentCount = shortStoryCommentListResponse.getCount();
                }
                showComments(shortStoryCommentListResponse.getData());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showComments(List<ShortStoryCommentListData> commentList) {
        if (commentList.size() == 0) {
            isLastPageReached = false;
            if (null != consolidatedList && !consolidatedList.isEmpty()) {
                //No more next results from pagination
                isLastPageReached = true;
            } else {
            }
        } else {
            for (int i = 0; i < commentList.size(); i++) {
                ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                commentModel.setSsComment(commentList.get(i));
                consolidatedList.add(commentModel);
            }
            adapter.setListData(consolidatedList);
            paginationCommentId = commentList.get(commentList.size() - 1).get_id();
            downloadedComment = downloadedComment + commentList.size();
            if (downloadedComment >= totalCommentCount) {
                isLastPageReached = true;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
        @Override
        public void onResponse(Call<ViewCountResponse> call, retrofit2.Response<ViewCountResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ViewCountResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    headerModel.getSsResult().setCount(responseData.getData().get(0).getCount());
                    headerModel.getSsResult().setCommentCount(responseData.getData().get(0).getCommentCount());
                    headerModel.getSsResult().setLikeCount(responseData.getData().get(0).getLikeCount());
                    adapter.notifyDataSetChanged();
                } else {
//                    articleViewCountTextView.setText(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ViewCountResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback = new Callback<ArticleDetailResponse>() {
        @Override
        public void onResponse(Call<ArticleDetailResponse> call, retrofit2.Response<ArticleDetailResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                if (!isAdded()) {
                    return;
                }
                if ("0".equals(bookmarkFlag)) {
                    bookmarkStatus = 0;
                    Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                    bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                } else {
                    Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmarked);
                    bookmarkArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                    bookmarkStatus = 1;
                }
                bookmarkId = responseData.getData().getResult().getBookmarkId();
                if (userDynamoId.equals(authorId)) {
                    followClick.setVisibility(View.INVISIBLE);
                } else {
                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                        followClick.setEnabled(true);
                        followClick.setText(getString(R.string.ad_follow_author));
                        isFollowing = false;
                    } else {
                        followClick.setEnabled(true);
                        followClick.setText(getString(R.string.ad_following_author));
                        isFollowing = true;
                    }
                }
            } else {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<AddBookmarkResponse> addBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
                return;
            }
            AddBookmarkResponse responseData = response.body();
            updateBookmarkStatus(ADD_BOOKMARK, responseData);
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<ArticleRecommendationStatusResponse> recommendStatusResponseCallback = new Callback<ArticleRecommendationStatusResponse>() {
        @Override
        public void onResponse(Call<ArticleRecommendationStatusResponse> call, retrofit2.Response<ArticleRecommendationStatusResponse> response) {
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
                return;
            }
            ArticleRecommendationStatusResponse responseData = response.body();
            recommendationFlag = responseData.getData().getStatus();
            if (!isAdded()) {
                return;
            }
            if ("0".equals(recommendationFlag)) {
                recommendStatus = 0;
                Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommend);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            } else {
                recommendStatus = 1;
                Drawable top = ContextCompat.getDrawable(getActivity(), R.drawable.ic_recommended);
                likeArticleTextView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            }
        }

        @Override
        public void onFailure(Call<ArticleRecommendationStatusResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (!isAdded()) {
                        return;
                    }
                    if (null != responseData.getData() && !responseData.getData().isEmpty()) {
                        ((ShortStoryContainerActivity) getActivity()).showToast(responseData.getReason());
                        headerModel.getSsResult().setLikeCount("" + (Integer.parseInt(headerModel.getSsResult().getLikeCount()) + 1));
                        adapter.notifyDataSetChanged();
                    } else {
                        ((ShortStoryContainerActivity) getActivity()).showToast(responseData.getReason());
                    }
                } else {
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            handleExceptions(t);
        }
    };

    private void updateBookmarkStatus(int status, AddBookmarkResponse responseData) {

        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
            if (status == ADD_BOOKMARK) {
                bookmarkId = responseData.getData().getResult().getBookmarkId();
            } else {
                bookmarkId = null;
            }
        } else {
            if (StringUtils.isNullOrEmpty(responseData.getReason())) {
//                showToast(responseData.getReason());
            } else {
//                showToast(getString(R.string.went_wrong));
            }
        }
    }

    public void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.connection_timeout));
            } else {
//                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.storyOptionImageView: {
                ReportStoryOrCommentDialogFragment reportStoryOrCommentDialogFragment = new ReportStoryOrCommentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", consolidatedList.get(position).getSsResult().getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportStoryOrCommentDialogFragment.setArguments(_args);
                reportStoryOrCommentDialogFragment.setCancelable(true);
                reportStoryOrCommentDialogFragment.setTargetFragment(this, 0);
                reportStoryOrCommentDialogFragment.show(fm, "Report Content");
            }
            break;
            case R.id.commentRootLayout: {
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("authorId", consolidatedList.get(position).getSsComment().getUserId());
                _args.putString("responseType", "COMMENT");
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                openAddCommentReplyDialog(consolidatedList.get(position).getSsComment());
            }
            break;
            case R.id.replyCountTextView: {
                shortStoryCommentRepliesDialogFragment = new ShortStoryCommentRepliesDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putParcelable("commentReplies", consolidatedList.get(position).getSsComment());
                _args.putInt("totalRepliesCount", consolidatedList.get(position).getSsComment().getReplies_count());
                _args.putInt("position", position);
                shortStoryCommentRepliesDialogFragment.setArguments(_args);
                shortStoryCommentRepliesDialogFragment.setCancelable(true);
                shortStoryCommentRepliesDialogFragment.setTargetFragment(this, 0);
                shortStoryCommentRepliesDialogFragment.show(fm, "View Replies");
            }
            break;
            case R.id.storyRecommendationCountTextView:
                recommendUnrecommentArticleAPI("1");
                break;
            case R.id.facebookShareImageView: {
                try {
                    authorTextView.setText("By - " + headerModel.getSsResult().getUserName());
                    titleTextView.setText(headerModel.getSsResult().getTitle());
                    bodyTextView.setText(headerModel.getSsResult().getBody());
                    shareSSView.setDrawingCacheEnabled(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap b = shareSSView.getDrawingCache();
                            try {
                                b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(b)
                                    .build();
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                            ShareDialog shareDialog = new ShareDialog(ShortStoryFragment.this);
                            shareDialog.show(content);
                            shareSSView.setDrawingCacheEnabled(false);
                        }
                    }, 200);
                } catch (Exception e) {
                    if (isAdded())
                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_facebook_fail), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.whatsappShareImageView: {
                try {
                    authorTextView.setText("By - " + headerModel.getSsResult().getUserName());
                    titleTextView.setText(headerModel.getSsResult().getTitle());
                    bodyTextView.setText(headerModel.getSsResult().getBody());
                    shareSSView.setDrawingCacheEnabled(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap b = shareSSView.getDrawingCache();
                            try {
                                b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            shareSSView.setDrawingCacheEnabled(false);
                            String shareUrl = AppUtils.getShortStoryShareUrl(headerModel.getSsResult().getUserType(),
                                    headerModel.getSsResult().getBlogTitleSlug(), headerModel.getSsResult().getTitleSlug());

                            if (StringUtils.isNullOrEmpty(shareUrl)) {
                                Toast.makeText(getActivity(), getString(R.string.moderation_or_share_facebook_fail), Toast.LENGTH_SHORT).show();
                            } else {
                                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg");
                                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                                whatsappIntent.setPackage("com.whatsapp");
                                whatsappIntent.setType("image/*");
//                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, mDatalist.get(position).getTitle() + "\n\n" + getString(R.string.ad_share_follow_author, mDatalist.get(position).getUserName()) + "\n" + shareUrl);
                                try {
                                    startActivity(Intent.createChooser(whatsappIntent, "Share image via:"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    if (isAdded())
                                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                                }
//                                Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", mDatalist.get(position).getId(), mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName(), "Whatsapp");
                            }
                        }
                    }, 200);

                } catch (Exception e) {
                    if (isAdded())
                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.genericShareImageView: {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppUtils.getShareUrl(headerModel.getSsResult().getUserType(),
                        headerModel.getSsResult().getBlogTitleSlug(), headerModel.getSsResult().getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            headerModel.getSsResult().getTitle() + "\" by " + headerModel.getSsResult().getUserName() + ".";
                } else {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            headerModel.getSsResult().getTitle() + "\" by " + headerModel.getSsResult().getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "ShortStory"));
            }
            break;
            case R.id.authorNameTextView: {
                if (userDynamoId.equals(headerModel.getSsResult().getUserId())) {
                    Intent profileIntent = new Intent(getActivity(), DashboardActivity.class);
                    profileIntent.putExtra("TabType", "profile");
                    startActivity(profileIntent);
                } else {
                    Intent intentnn = new Intent(getActivity(), BloggerProfileActivity.class);
                    intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, headerModel.getSsResult().getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, headerModel.getSsResult().getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;
            }
        }
    }

    public void openAddCommentReplyDialog(ShortStoryCommentListData cData) {
        AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddShortStoryCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("parentCommentData", cData);
        addGpPostCommentReplyDialogFragment.setArguments(_args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        addGpPostCommentReplyDialogFragment.setTargetFragment(this, 0);
        addGpPostCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    public void addComment(String content) {
        showProgressDialog("Adding Comment");
        AddEditShortStoryCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditShortStoryCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id("0");
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<ShortStoryCommentListResponse> addCommentResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                    ShortStoryCommentListData shortStoryCommentListData = new ShortStoryCommentListData();
                    shortStoryCommentListData.set_id(responseData.getData().get(0).get_id());
                    shortStoryCommentListData.setMessage(responseData.getData().get(0).getMessage());
                    shortStoryCommentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    shortStoryCommentListData.setPostId(responseData.getData().get(0).getPostId());
                    shortStoryCommentListData.setParentCommentId("0");
                    shortStoryCommentListData.setReplies(new ArrayList<ShortStoryCommentListData>());
                    shortStoryCommentListData.setReplies_count(0);
                    shortStoryCommentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    shortStoryCommentListData.setUserName(responseData.getData().get(0).getUserName());
                    shortStoryCommentListData.setUserId(responseData.getData().get(0).getUserId());

                    commentModel.setSsComment(shortStoryCommentListData);
                    consolidatedList.add(1, commentModel);
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editComment(String content, String responseId, int position) {
        showProgressDialog("Editing your response");
        actionItemPosition = position;
        editContent = content;
        AddEditShortStoryCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditShortStoryCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.editCommentOrReply(responseId, addEditShortStoryCommentOrReplyRequest);
        call.enqueue(editCommentOrReplyResponseListener);
    }

    private Callback<ShortStoryCommentListResponse> editCommentOrReplyResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.get(actionItemPosition).getSsComment().setMessage(editContent);
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void addReply(String content, String parentCommentId) {
        showProgressDialog("Adding Reply");
        AddEditShortStoryCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditShortStoryCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id(parentCommentId);
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<ShortStoryCommentListResponse> addReplyResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    ShortStoryCommentListData shortStoryCommentListData = new ShortStoryCommentListData();
                    shortStoryCommentListData.set_id(responseData.getData().get(0).get_id());
                    shortStoryCommentListData.setMessage(responseData.getData().get(0).getMessage());
                    shortStoryCommentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    shortStoryCommentListData.setPostId(responseData.getData().get(0).getPostId());
                    shortStoryCommentListData.setParentCommentId(responseData.getData().get(0).getParentCommentId());
                    shortStoryCommentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    shortStoryCommentListData.setUserName(responseData.getData().get(0).getUserName());
                    shortStoryCommentListData.setUserId(responseData.getData().get(0).getUserId());

                    for (int i = 1; i < consolidatedList.size(); i++) {
                        if (consolidatedList.get(i).getSsComment().get_id().equals(responseData.getData().get(0).getParentCommentId())) {
                            consolidatedList.get(i).getSsComment().getReplies().add(0, shortStoryCommentListData);
                            consolidatedList.get(i).getSsComment().setReplies_count(consolidatedList.get(i).getSsComment().getReplies_count() + 1);
                            if (shortStoryCommentRepliesDialogFragment != null) {
                                shortStoryCommentRepliesDialogFragment.updateRepliesList(consolidatedList.get(i).getSsComment());
                            }
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editReply(String content, String parentCommentId, String replyId) {
        showProgressDialog("Editing Reply");
        AddEditShortStoryCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditShortStoryCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.editCommentOrReply(replyId, addEditShortStoryCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = content;
    }

    private Callback<ShortStoryCommentListResponse> editReplyResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    boolean isReplyUpdated = false;
                    for (int i = 1; i < consolidatedList.size(); i++) {
                        if (consolidatedList.get(i).getSsComment().get_id().equals(editReplyParentCommentId)) {
                            for (int j = 0; j < consolidatedList.get(i).getSsComment().getReplies().size(); j++) {
                                if (consolidatedList.get(i).getSsComment().getReplies().get(j).get_id().equals(editReplyId)) {
                                    consolidatedList.get(i).getSsComment().getReplies().get(j).setMessage(editContent);
                                    if (shortStoryCommentRepliesDialogFragment != null) {
                                        shortStoryCommentRepliesDialogFragment.updateRepliesList(consolidatedList.get(i).getSsComment());
                                    }
                                    isReplyUpdated = true;
                                    break;
                                }
                            }
                        }
                        if (isReplyUpdated) {
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.deleteCommentOrReply(consolidatedList.get(commentPos).getSsComment().getReplies().get(replyPos).get_id());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<ShortStoryCommentListResponse> deleteReplyResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.get(deleteCommentPos).getSsComment().getReplies().remove(deleteReplyPos);
                    consolidatedList.get(deleteCommentPos).getSsComment().setReplies_count(consolidatedList.get(deleteCommentPos).getSsComment().getReplies_count() - 1);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment.updateRepliesList(consolidatedList.get(deleteCommentPos).getSsComment());
                        if (consolidatedList.get(deleteCommentPos).getSsComment().getReplies_count() == 0) {
                            shortStoryCommentRepliesDialogFragment.dismiss();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onResponseDelete(int position, String responseType) {
        Call<ShortStoryCommentListResponse> call = shortStoryAPI.deleteCommentOrReply(consolidatedList.get(position).getSsComment().get_id());
        call.enqueue(deleteCommentResponseListener);
        actionItemPosition = position;
    }

    private Callback<ShortStoryCommentListResponse> deleteCommentResponseListener = new Callback<ShortStoryCommentListResponse>() {
        @Override
        public void onResponse(Call<ShortStoryCommentListResponse> call, retrofit2.Response<ShortStoryCommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");

                return;
            }
            try {
                ShortStoryCommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.remove(actionItemPosition);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (isAdded())
                        ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryCommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseEdit(int position, String responseType) {
        AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddShortStoryCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("action", "EDIT_COMMENT");
        _args.putParcelable("parentCommentData", consolidatedList.get(position).getSsComment());
        _args.putInt("position", position);
        addGpPostCommentReplyDialogFragment.setArguments(_args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        addGpPostCommentReplyDialogFragment.setTargetFragment(this, 0);
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
//        ReportStoryOrCommentRequest reportStoryOrCommentRequest = new ReportStoryOrCommentRequest();
//        reportStoryOrCommentRequest.setId(consolidatedList.get(position).getSsResult());
//        shortStoryAPI.reportStoryOrComment(reportStoryOrCommentRequest);
        ReportStoryOrCommentDialogFragment reportStoryOrCommentDialogFragment = new ReportStoryOrCommentDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("postId", consolidatedList.get(position).getSsComment().get_id());
        _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        reportStoryOrCommentDialogFragment.setArguments(_args);
        reportStoryOrCommentDialogFragment.setCancelable(true);
        reportStoryOrCommentDialogFragment.setTargetFragment(this, 0);
        reportStoryOrCommentDialogFragment.show(fm, "Report Content");
    }

    public class ShortStoryDetailAndCommentModel {
        private int type;
        private ShortStoryDetailResult ssResult;
        private ShortStoryCommentListData ssComment;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public ShortStoryDetailResult getSsResult() {
            return ssResult;
        }

        public void setSsResult(ShortStoryDetailResult ssResult) {
            this.ssResult = ssResult;
        }

        public ShortStoryCommentListData getSsComment() {
            return ssComment;
        }

        public void setSsComment(ShortStoryCommentListData ssComment) {
            this.ssComment = ssComment;
        }
    }
}
