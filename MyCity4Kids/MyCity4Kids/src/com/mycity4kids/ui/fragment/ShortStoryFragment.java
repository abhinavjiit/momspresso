package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ShortStoryDetailResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.ShortStoriesDetailRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.widget.StoryShareCardWidget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryFragment extends BaseFragment implements View.OnClickListener, ShortStoriesDetailRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction {

    private static final int REQUEST_INIT_PERMISSION = 2;
    private SimpleTooltip simpleTooltip;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int ADD_BOOKMARK = 1;
    ShortStoryCommentRepliesDialogFragment shortStoryCommentRepliesDialogFragment;
    private String paginationCommentId = null;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int totalCommentCount = 0;
    private int downloadedComment = 0;
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    Handler handler;
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
    private String userType;
    private String articleId;
    private String deepLinkURL;
    private String blogSlug;
    private String titleSlug;
    private String commentType = "db";
    private String commentMainUrl;
    private String userDynamoId;
    private String articleLanguageCategoryId;
    private String from;

    private TextView followAuthorTextView;

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
    private RelativeLayout rootLayout;
    private int sharedStoryPosition;
    private String shareMedium;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ShortStoryDetailResult sharedStoryItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        fragmentView = inflater.inflate(R.layout.short_story_fragment, container, false);
        rootLayout = (RelativeLayout) fragmentView.findViewById(R.id.rootLayout);
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
            llm.setOrientation(RecyclerView.VERTICAL);
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
        ArticleDetailsAPI bookmarkFollowingStatusAPI = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarkFollowingStatusAPI.checkFollowingBookmarkStatus(articleId, authorId);
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

    private void recommendUnrecommentArticleAPI() {
        Utils.pushLikeStoryEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId, authorId + "~" + author);
        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
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
                    //addGpPostCommentReplyDialogFragment.setTargetFragment(this, 0);
                    addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    Callback<ShortStoryDetailResult> storyDetailResponseCallbackRedis = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call, retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                getShortStoryDetailsFallback();
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                headerModel.setSsResult(responseData);
                headerModel.setType(0);
                author = responseData.getUserName();
                userType = responseData.getUserType();
                authorId = responseData.getUserId();
                blogSlug = responseData.getBlogTitleSlug();
                titleSlug = responseData.getTitleSlug();
                hitBookmarkFollowingStatusAPI();
                consolidatedList.add(headerModel);
                getStoryComments(articleId, null);
                getViewCountAPI();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountAPI(responseData.getUserId(), responseData.getTags(), responseData.getCities());
                if (isAdded())
                    updateGTMEvent(responseData.getLang());
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
            if (response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResponse responseData = response.body();
                headerModel.setSsResult(responseData.getData());
                headerModel.setType(0);
                consolidatedList.add(headerModel);
                authorId = responseData.getData().getUserId();
                hitBookmarkFollowingStatusAPI();
                getStoryComments(articleId, null);
                getViewCountAPI();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountAPI(responseData.getData().getUserId(), responseData.getData().getTags(), responseData.getData().getCities());
                updateGTMEvent(responseData.getData().getLang());
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

    private void updateGTMEvent(String lang) {
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            if ("0".equals(lang)) {
                Utils.pushStoryLoadedEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId, authorId + "~" + author, "English");
            } else {
                LanguageConfigModel languageConfigModel = retMap.get(lang);
                Utils.pushStoryLoadedEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId, authorId + "~" + author, languageConfigModel.getDisplay_name());
            }
        } catch (FileNotFoundException e) {

        }

    }

    private void getStoryComments(String id, String commentType) {
        Call<CommentListResponse> call = shortStoryAPI.getStoryComments(id, commentType, paginationCommentId);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<CommentListResponse> ssCommentsResponseCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            isReuqestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                Crashlytics.logException(nee);
                return;
            }
            try {
                CommentListResponse shortStoryCommentListResponse = response.body();
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showComments(List<CommentListData> commentList) {
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
            if (null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            ArticleDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                bookmarkFlag = responseData.getData().getResult().getBookmarkStatus();
                if (!isAdded()) {
                    return;
                }
                if (userDynamoId.equals(authorId)) {
                } else {
                    if ("0".equals(responseData.getData().getResult().getIsFollowed())) {
                        adapter.setAuthorFollowingStatus(AppConstants.STATUS_NOT_FOLLOWING);
                        adapter.notifyItemChanged(0);
                        isFollowing = false;
                    } else {
                        adapter.setAuthorFollowingStatus(AppConstants.STATUS_FOLLOWING);
                        adapter.notifyItemChanged(0);
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

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            isRecommendRequestRunning = false;
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
                    if (likeStatus.equals("1")) {
                        if (!responseData.getData().isEmpty()) {
                            headerModel.getSsResult().setLikeCount("" + (Integer.parseInt(headerModel.getSsResult().getLikeCount()) + 1));
                        }
                        headerModel.getSsResult().setLiked(true);
                    } else {
                        if (!responseData.getData().isEmpty()) {
                            headerModel.getSsResult().setLikeCount("" + (Integer.parseInt(headerModel.getSsResult().getLikeCount()) - 1));
                        }
                        headerModel.getSsResult().setLiked(false);
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity()).showToast("" + responseData.getReason());
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
            isRecommendRequestRunning = false;
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
            } else {
            }
        }
    }

    private void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.connection_timeout));
            } else {
            }
        }
        Crashlytics.logException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    @Override
    public void onClick(View view, int position, View whatsappShare) {
        switch (view.getId()) {

            case R.id.menuItem:
                chooseMenuOptionsItem(view, position);
                break;
           /* case R.id.storyOptionImageView: {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", consolidatedList.get(position).getSsResult().getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.show(fm, "Report Content");
            }
            break;*/
            case R.id.commentRootLayout: {
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
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
                shortStoryCommentRepliesDialogFragment.show(fm, "View Replies");
            }
            break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (headerModel.getSsResult().isLiked()) {
                        likeStatus = "0";
                        recommendUnrecommentArticleAPI();
                    } else {
                        likeStatus = "1";
                        tooltipForShare(whatsappShare);
                        recommendUnrecommentArticleAPI();
                    }
                }
                break;
            case R.id.facebookShareImageView: {
                if (isAdded()) {
                    getSharableViewForPosition(position, AppConstants.MEDIUM_FACEBOOK);
                }
            }
            break;
            case R.id.whatsappShareImageView: {
                getSharableViewForPosition(position, AppConstants.MEDIUM_WHATSAPP);
            }
            break;
            case R.id.instagramShareImageView: {
                try {
                    filterTags(headerModel.getSsResult().getTags());
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM);
            }
            break;
            case R.id.genericShareImageView: {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment = new AddCollectionAndCollectionItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("articleId", articleId);
                    bundle.putString("type", AppConstants.SHORT_STORY_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    addCollectionAndCollectionitemDialogFragment.setTargetFragment(this, 0);
                    addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    Utils.pushProfileEvents(getActivity(), "CTA_100WS_Add_To_Collection",
                            "ShortStoryFragment", "Add to Collection", "-");
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }

            }
            break;
            case R.id.authorNameTextView: {
                Intent intentnn = new Intent(getActivity(), UserProfileActivity.class);
                intentnn.putExtra(Constants.USER_ID, headerModel.getSsResult().getUserId());
                intentnn.putExtra(AppConstants.AUTHOR_NAME, headerModel.getSsResult().getUserName());
                intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryDetailsScreen");
                startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                break;
            }
            case R.id.followAuthorTextView: {
                followAuthorTextView = (TextView) view;
                followAPICall();
            }
        }
    }

    private void filterTags(ArrayList<Map<String, String>> tagObjectList) {
        ArrayList<String> tagList = new ArrayList<>();
        for (int i = 0; i < tagObjectList.size(); i++) {
            for (Map.Entry<String, String> mapEntry : tagObjectList.get(i).entrySet()) {
                if (mapEntry.getKey().startsWith("category-")) {
                    tagList.add(mapEntry.getKey());
                }
            }
        }

        String hashtags = AppUtils.getHasTagFromCategoryList(tagList);
        AppUtils.copyToClipboard(hashtags);
        if (isAdded())
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.all_insta_share_clipboard_msg));
    }

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = shortStoryRecyclerView.getLayoutManager().findViewByPosition(position).findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = headerModel.ssResult;
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        if (isAdded()) {
            Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
            shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
            AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME);
            shareStory();
        }
    }

    private void shareStory() {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg");
        if (isAdded()) {
            switch (shareMedium) {
                case AppConstants.MEDIUM_FACEBOOK: {
                    SharingUtils.shareViaFacebook(this);
                    Utils.pushShareStoryEvent(getActivity(), "ShortStoryFragment",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Facebook");
                }
                break;
                case AppConstants.MEDIUM_WHATSAPP: {
                    if (AppUtils.shareImageWithWhatsApp(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                        Utils.pushShareStoryEvent(getActivity(), "ShortStoryFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Whatsapp");
                    }
                }
                break;
                case AppConstants.MEDIUM_INSTAGRAM: {
                    if (AppUtils.shareImageWithInstagram(getActivity(), uri)) {
                        Utils.pushShareStoryEvent(getActivity(), "ShortStoryFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Instagram");
                    }
                }
                break;
                case AppConstants.MEDIUM_GENERIC: {
                    if (AppUtils.shareGenericImageAndOrLink(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                        Utils.pushShareStoryEvent(getActivity(), "ShortStoryFragment",
                                userDynamoId + "", sharedStoryItem.getId(),
                                sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Generic");
                    }
                }
                break;
            }
        }
    }

    private void checkPermissionAndCreateShareableImage() {
        if (Build.VERSION.SDK_INT >= 23 && isAdded()) {
            if (ActivityCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                try {
                    createBitmapForSharingStory();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        } else {
            try {
                createBitmapForSharingStory();
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
    }

    private void createBitmapForSharingStory(int position) {
        switch (position % 6) {
            case 0:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_1, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
            case 1:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_2, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
            case 2:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_3, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
            case 3:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_4, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
            case 4:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_5, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
            case 5:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_6, headerModel.getSsResult().getTitle().trim(), headerModel.getSsResult().getBody().trim(), author);
                break;
        }
    }

    void openAddCommentReplyDialog(CommentListData cData) {
        AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment = new AddShortStoryCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("parentCommentData", cData);
        addGpPostCommentReplyDialogFragment.setArguments(_args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        addGpPostCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    public void addComment(String content) {
        showProgressDialog("Adding Comment");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id("0");
        addEditShortStoryCommentOrReplyRequest.setType("story");
        Call<CommentListResponse> call = shortStoryAPI.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<CommentListResponse> addCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                    CommentListData shortStoryCommentListData = new CommentListData();
                    shortStoryCommentListData.set_id(responseData.getData().get(0).get_id());
                    shortStoryCommentListData.setMessage(responseData.getData().get(0).getMessage());
                    shortStoryCommentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    shortStoryCommentListData.setPostId(responseData.getData().get(0).getPostId());
                    shortStoryCommentListData.setParentCommentId("0");
                    shortStoryCommentListData.setReplies(new ArrayList<CommentListData>());
                    shortStoryCommentListData.setReplies_count(0);
                    shortStoryCommentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    shortStoryCommentListData.setUserName(responseData.getData().get(0).getUserName());
                    shortStoryCommentListData.setUserId(responseData.getData().get(0).getUserId());
                    commentModel.setSsComment(shortStoryCommentListData);
                    consolidatedList.add(1, commentModel);
                    adapter.notifyDataSetChanged();
                    if (StringUtils.isNullOrEmpty(userType) || StringUtils.isNullOrEmpty(titleSlug) || StringUtils.isNullOrEmpty(blogSlug)) {
                    } else {
                        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setQuote(responseData.getData().get(0).getMessage())
                                    .setContentUrl(Uri.parse(shareUrl))
                                    .build();
                            new ShareDialog(ShortStoryFragment.this).show(content);
                        }
                    }
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "add", "comment");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
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
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = shortStoryAPI.editCommentOrReply(responseId, addEditShortStoryCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.get(actionItemPosition).getSsComment().setMessage(editContent);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment.updateRepliesList(consolidatedList.get(actionItemPosition).getSsComment());
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "edit", "comment");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void addReply(String content, String parentCommentId) {
        showProgressDialog("Adding Reply");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id(parentCommentId);
        addEditShortStoryCommentOrReplyRequest.setType("story");
        Call<CommentListResponse> call = shortStoryAPI.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<CommentListResponse> addReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    CommentListData shortStoryCommentListData = new CommentListData();
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
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "add", "reply");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editReply(String content, String parentCommentId, String replyId) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = shortStoryAPI.editCommentOrReply(replyId, addEditShortStoryCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = content;
    }

    private Callback<CommentListResponse> editReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                return;
            }
            try {
                CommentListResponse responseData = response.body();
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
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "edit", "reply");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Call<CommentListResponse> call = shortStoryAPI.deleteCommentOrReply(consolidatedList.get(commentPos).getSsComment().getReplies().get(replyPos).get_id());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
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
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "delete", "reply");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
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
        Call<CommentListResponse> call = shortStoryAPI.deleteCommentOrReply(consolidatedList.get(position).getSsComment().get_id());
        call.enqueue(deleteCommentResponseListener);
        actionItemPosition = position;
    }

    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.remove(actionItemPosition);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, "delete", "comment");
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
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
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
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("postId", consolidatedList.get(position).getSsComment().get_id());
        _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        reportContentDialogFragment.setArguments(_args);
        reportContentDialogFragment.setCancelable(true);
        reportContentDialogFragment.show(fm, "Report Content");
    }

    public class ShortStoryDetailAndCommentModel {
        private int type;
        private ShortStoryDetailResult ssResult;
        private CommentListData ssComment;

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

        public CommentListData getSsComment() {
            return ssComment;
        }

        public void setSsComment(CommentListData ssComment) {
            this.ssComment = ssComment;
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    }).show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String s : PERMISSIONS_INIT) {
            if (ActivityCompat.checkSelfPermission(getActivity(), s) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                createBitmapForSharingStory(sharedStoryPosition);
                if (isAdded()) {
                    if (AppConstants.MEDIUM_WHATSAPP.equals(shareMedium)) {
                        AppUtils.shareStoryWithWhatsApp(getActivity(), headerModel.getSsResult().getUserType(), headerModel.getSsResult().getBlogTitleSlug(), headerModel.getSsResult().getTitleSlug(),
                                "ShortStoryDetailsScreen", userDynamoId, articleId, authorId, author);
                    } else if (AppConstants.MEDIUM_INSTAGRAM.equals(shareMedium)) {
                        AppUtils.shareStoryWithInstagram(getActivity(), "ShortStoryDetailsScreen", userDynamoId, articleId, authorId, author);
                    }
                }
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

    private void followAPICall() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);
        if (isFollowing) {
            isFollowing = false;
            adapter.setAuthorFollowingStatus(AppConstants.STATUS_NOT_FOLLOWING);
            adapter.notifyItemChanged(0);
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_100WS_Detail", userDynamoId, "ShortStoryFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.unfollowUser(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            isFollowing = true;
            adapter.setAuthorFollowingStatus(AppConstants.STATUS_FOLLOWING);
            adapter.notifyItemChanged(0);
            Utils.pushGenericEvent(getActivity(), "CTA_Follow_100WS_Detail", userDynamoId, "ShortStoryFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followAPI.followUser(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    adapter.setAuthorFollowingStatus(AppConstants.STATUS_FOLLOWING);
                    adapter.notifyItemChanged(0);
                    followAuthorTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_following_author));
                    isFollowing = true;
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<FollowUnfollowUserResponse> followUserResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, retrofit2.Response<FollowUnfollowUserResponse> response) {
            if (response.body() == null) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                } else {
                    adapter.setAuthorFollowingStatus(AppConstants.STATUS_NOT_FOLLOWING);
                    adapter.notifyItemChanged(0);
                    followAuthorTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_follow_author));
                    isFollowing = false;
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
            if (isAdded())
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void tooltipForShare(View shareImageView) {
        simpleTooltip = new SimpleTooltip.Builder(getContext())
                .anchorView(shareImageView)
                .backgroundColor(getResources().getColor(R.color.app_blue))
                .text(getResources().getString(R.string.ad_bottom_bar_generic_share))
                .textColor(getResources().getColor(R.color.white))
                .arrowColor(getResources().getColor(R.color.app_blue))
                .gravity(Gravity.TOP)
                .arrowWidth(60)
                .arrowHeight(20)
                .animated(false)
                .focusable(true)
                .transparentOverlay(true)
                .build();
        simpleTooltip.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (simpleTooltip.isShowing())
                    simpleTooltip.dismiss();
            }
        }, 3000);

    }


    @SuppressLint("RestrictedApi")
    private void chooseMenuOptionsItem(View view, int position) {

        final androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.choose_short_story_menu, popupMenu.getMenu());
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            Drawable drawable = popupMenu.getMenu().getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
            }
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.shareShortStory) {
                if (isAdded()) {
                    getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC);
                }
                return true;
            } else if (item.getItemId() == R.id.bookmarkShortStory) {
                return true;
            } else if (item.getItemId() == R.id.copyLink) {
                AppUtils.copyToClipboard(AppUtils.getShortStoryShareUrl(consolidatedList.get(position).getSsResult().getUserType(),
                        consolidatedList.get(position).getSsResult().getBlogTitleSlug(), consolidatedList.get(position).getSsResult().getTitleSlug()));
                if (isAdded()) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ss_story_link_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", consolidatedList.get(position).getSsResult().getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.show(fm, "Report Content");
                return true;
            }
            return false;
        });

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
    }

}
