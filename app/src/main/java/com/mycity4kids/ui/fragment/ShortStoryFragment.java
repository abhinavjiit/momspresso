package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.CommentPostButtonColorChangeInterface;
import com.mycity4kids.models.BlockUserModel;
import com.mycity4kids.models.TopCommentData;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.LikeReactionModel;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ShortStoryDetailResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.tagging.MentionsResponse;
import com.mycity4kids.tagging.mentions.MentionSpan;
import com.mycity4kids.tagging.mentions.MentionsEditable;
import com.mycity4kids.tagging.suggestions.SuggestionsResult;
import com.mycity4kids.tagging.tokenization.QueryToken;
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver;
import com.mycity4kids.tagging.ui.RichEditorView;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.ShortStoriesDetailRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddArticleCommentReplyDialogFragment.MentionIndex;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryFragment extends BaseFragment implements View.OnClickListener,
        ShortStoriesDetailRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction, QueryTokenReceiver, CommentPostButtonColorChangeInterface {

    private static final int REQUEST_INIT_PERMISSION = 2;
    private SimpleTooltip simpleTooltip;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ShortStoryCommentRepliesDialogFragment shortStoryCommentRepliesDialogFragment;
    private String paginationCommentId = null;
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private Handler handler;
    private ShortStoryAPI shortStoryApi;
    private boolean isFollowing = false;
    private String authorId;
    private String author;
    private String userType;
    private String articleId;
    private String blogSlug;
    private String titleSlug;
    private String userDynamoId;
    private TextView followAuthorTextView;
    private View fragmentView;
    private RecyclerView shortStoryRecyclerView;
    private ImageView userImageView;

    private ShortStoryDetailAndCommentModel headerModel;
    private ArrayList<ShortStoryDetailAndCommentModel> consolidatedList;
    private List<CommentListData> consolidatedCommentList;
    ShortStoriesDetailRecyclerAdapter adapter;
    private int actionItemPosition;
    private String editContent;
    private String editReplyParentCommentId;
    private String editReplyId;
    private int deleteCommentPos;
    private int deleteReplyPos;
    private int colorPosition = 0;
    private RelativeLayout rootLayout;
    private String shareMedium;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ShortStoryDetailResult sharedStoryItem;
    private TextView viewAllTextView;
    private LinearLayout suggestionContainer;
    private RichEditorView typeHere;
    private ImageView disableStatePostTextView;
    private String comingFrom;
    private ShortStoryDetailResult responseData;
    private HorizontalScrollView horizontalCommentSuggestionsContainer;

    private int pos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.short_story_fragment, container, false);
        rootLayout = (RelativeLayout) fragmentView.findViewById(R.id.rootLayout);
        shortStoryRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.shortStoryRecyclerView);
        viewAllTextView = fragmentView.findViewById(R.id.viewAllTextView);
        typeHere = fragmentView.findViewById(R.id.typeHere);
        disableStatePostTextView = fragmentView.findViewById(R.id.disableStatePostTextView);
        suggestionContainer = fragmentView.findViewById(R.id.suggestionContainer);
        horizontalCommentSuggestionsContainer = fragmentView.findViewById(R.id.horizontalCommentSuggestionsContainer);
        userImageView = fragmentView.findViewById(R.id.userImageView);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                colorPosition = bundle.getInt("colorPosition", 0);
            }
            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(RecyclerView.VERTICAL);
            shortStoryRecyclerView.setLayoutManager(llm);
            headerModel = new ShortStoryDetailAndCommentModel();
            consolidatedList = new ArrayList<>();
            consolidatedCommentList = new ArrayList<>();
            if (bundle != null) {
                articleId = bundle.getString(Constants.ARTICLE_ID);
                authorId = bundle.getString(Constants.AUTHOR_ID, "");
                blogSlug = bundle.getString(Constants.BLOG_SLUG);
                titleSlug = bundle.getString(Constants.TITLE_SLUG);
                comingFrom = bundle.getString(Constants.FROM_SCREEN);
                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.error_network));
                }
                Retrofit retro = BaseApplication.getInstance().getRetrofit();
                shortStoryApi = retro.create(ShortStoryAPI.class);
                getShortStoryDetails();
            }
            adapter = new ShortStoriesDetailRecyclerAdapter(getActivity(), this);
            adapter.setListData(consolidatedList);
            shortStoryRecyclerView.setAdapter(adapter);

        } catch (Exception e) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        viewAllTextView.setOnClickListener(this);
        typeHere.setMaxLines();
        typeHere.displayTextCounter(false);
        typeHere.setOnClickListener(this);
        disableStatePostTextView.setOnClickListener(this);
        typeHere.requestFocus();
        typeHere.setQueryTokenReceiver(this);
        typeHere.changeButtonColorOnTextChanged(this);
        try {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))
                    .error(R.drawable.default_commentor_img).into(userImageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.default_commentor_img).into(userImageView);
        }

        return fragmentView;
    }

    private void getShortStoryDetails() {
        Call<ShortStoryDetailResult> call = shortStoryApi.getShortStoryDetails(articleId, "articleId");
        call.enqueue(storyDetailResponseCallbackRedis);
    }

    private void getShortStoryDetailsFallback() {
        Call<ShortStoryDetailResponse> call = shortStoryApi.getShortStoryDetailsFallback(articleId);
        call.enqueue(storyDetailResponseCallbackFallback);
    }

    private void getViewCountApi() {
        Call<ViewCountResponse> call = shortStoryApi.getViewCount(articleId);
        call.enqueue(getViewCountResponseCallback);
    }

    private void hitBookmarkFollowingStatusApi() {
        ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
        articleDetailRequest.setArticleId(articleId);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI bookmarkFollowingStatusApi = retro.create(ArticleDetailsAPI.class);

        Call<ArticleDetailResponse> callBookmark = bookmarkFollowingStatusApi
                .checkFollowingBookmarkStatus(articleId, authorId);
        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);
    }

    private void hitUpdateViewCountApi(String userId, ArrayList<Map<String, String>> tagsList,
            ArrayList<Map<String, String>> cityList) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(userId);
        updateViewCountRequest.setTags(tagsList);
        updateViewCountRequest.setCities(cityList);
        updateViewCountRequest.setContentType("1");
        Call<ResponseBody> callUpdateViewCount = shortStoryApi.updateViewCount(articleId, updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private void recommendUnrecommentArticleApi() {
        Utils.pushLikeStoryEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId,
                authorId + "~" + author);
        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest =
                new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = shortStoryApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
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
                responseData = response.body();
                headerModel.setSsResult(responseData);
                headerModel.setType(0);
                author = responseData.getUserName();
                userType = responseData.getUserType();
                authorId = responseData.getUserId();
                blogSlug = responseData.getBlogTitleSlug();
                titleSlug = responseData.getTitleSlug();
                hitBookmarkFollowingStatusApi();
                adapter.setAuthorId(authorId);
                consolidatedList.add(headerModel);
                getStoryComments(articleId, null);
                getViewCountApi();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountApi(responseData.getUserId(), responseData.getTags(), responseData.getCities());
                if (isAdded()) {
                    updateGtmEvent(responseData.getLang());
                }

                if (SharedPrefUtils.getCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext())) {
                    getCommentSuggestions();
                } else {
                    horizontalCommentSuggestionsContainer.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
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
        public void onResponse(Call<ShortStoryDetailResponse> call,
                retrofit2.Response<ShortStoryDetailResponse> response) {
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
                hitBookmarkFollowingStatusApi();
                getStoryComments(articleId, null);
                getViewCountApi();
                adapter.notifyDataSetChanged();
                hitUpdateViewCountApi(responseData.getData().getUserId(), responseData.getData().getTags(),
                        responseData.getData().getCities());
                updateGtmEvent(responseData.getData().getLang());
            } catch (Exception e) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ShortStoryDetailResponse> call, Throwable t) {
            removeProgressDialog();
            handleExceptions(t);
        }
    };

    private void updateGtmEvent(String lang) {
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            if ("0".equals(lang)) {
                Utils.pushStoryLoadedEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId,
                        authorId + "~" + author, "English");
            } else {
                LanguageConfigModel languageConfigModel = retMap.get(lang);
                Utils.pushStoryLoadedEvent(getActivity(), "ShortStoryDetailsScreen", userDynamoId + "", articleId,
                        authorId + "~" + author, languageConfigModel.getDisplay_name());
            }
        } catch (FileNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void getCommentSuggestions() {
        ArrayList<String> tagList = new ArrayList<>();
        for (int i = 0; i < responseData.getTags().size(); i++) {
            for (Map.Entry<String, String> mapEntry : responseData.getTags().get(i)
                    .entrySet()) {
                if (mapEntry.getKey().startsWith("category-")) {
                    tagList.add(mapEntry.getKey());
                }
            }
        }
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<ResponseBody> call = articleDetailsApi.getCommentSuggestions(tagList);
        call.enqueue(commentSuggestinsListCallback);
    }

    private Callback<ResponseBody> commentSuggestinsListCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject((resData));
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {

                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray result = data.getJSONArray("result");
                    setHorizontalCommentSuggestions(result);
                }


            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded()) {
                ((BaseActivity) getActivity()).apiExceptions(t);
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setHorizontalCommentSuggestions(JSONArray result) {
        //suggestionContainer
        for (int i = 0; i < result.length(); i++) {
            try {
                CustomFontTextView textView = new CustomFontTextView(getActivity());
                MarginLayoutParams params = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
                        MarginLayoutParams.WRAP_CONTENT);
                params.leftMargin = 25;
                textView.setLayoutParams(params);
                textView.setText(result.getString(i));
                textView.setTag(result.getString(i));
                textView.setOnClickListener(view -> {
                    MentionsEditable commentText = typeHere.getText();
                    commentText.append(view.getTag().toString());
                    Selection.setSelection(commentText, commentText.length());
                    SharedPrefUtils.setCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext(), false);
                });
                Typeface face = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf");
                textView.setTypeface(face);
                textView.setTextSize(14f);
                textView.setTextColor(getResources().getColor(R.color.campaign_4A4A4A));
                textView.setPadding(10, 10, 10, 10);
                textView.setBackground(getResources().getDrawable(R.drawable.comment_suggestions_background_layout));

                suggestionContainer.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getStoryComments(String id, String commentType) {
        showProgressDialog("please wait");
        Call<CommentListResponse> call = shortStoryApi.getStoryComments(id, commentType, paginationCommentId);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<CommentListResponse> ssCommentsResponseCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                CommentListResponse shortStoryCommentListResponse = response.body();
                consolidatedCommentList = shortStoryCommentListResponse.getData();
                if (shortStoryCommentListResponse.getCount() == 0) {
                    getFacebookComments();
                } else {
                    showComments(consolidatedCommentList);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getFacebookComments() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<FBCommentResponse> call = articleDetailsApi.getFBComments(articleId, "");
        call.enqueue(fbCommentsCallback);
    }

    private Callback<FBCommentResponse> fbCommentsCallback = new Callback<FBCommentResponse>() {
        @Override
        public void onResponse(Call<FBCommentResponse> call, Response<FBCommentResponse> response) {
            removeProgressDialog();
            if (null == response.body()) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), getString(R.string.server_went_wrong));
                }
                return;
            }
            try {
                FBCommentResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<CommentsData> dataList = responseData.getData().getResult();
                    if (dataList.size() != 0) {
                        showFbComments(dataList);
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<FBCommentResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showFbComments(ArrayList<CommentsData> commentList) {
        if (commentList.size() != 0) {
            for (int i = 0; i < commentList.size(); i++) {
                if (consolidatedList.size() < 3) {
                    ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                    commentModel.setFbComment(commentList.get(i));
                    consolidatedList.add(commentModel);
                }
            }
            adapter.setListData(consolidatedList);
            paginationCommentId = commentList.get(commentList.size() - 1).getId();
        }
        adapter.notifyDataSetChanged();
    }

    private void showComments(List<CommentListData> commentList) {
        if (commentList.size() != 0) {
            for (int i = 0; i < commentList.size(); i++) {
                if (i < 2) {
                    ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                    commentModel.setSsComment(commentList.get(i));
                    consolidatedList.add(commentModel);
                }
            }
            adapter.setListData(consolidatedList);
            paginationCommentId = commentList.get(commentList.size() - 1).getId();
        }
        if (consolidatedList.size() < 3) {
            getFacebookComments();
        }
        adapter.notifyDataSetChanged();
    }

    private Callback<ViewCountResponse> getViewCountResponseCallback = new Callback<ViewCountResponse>() {
        @Override
        public void onResponse(Call<ViewCountResponse> call, retrofit2.Response<ViewCountResponse> response) {
            if (response.body() == null) {
                return;
            }
            try {
                ViewCountResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    headerModel.getSsResult().setCount(responseData.getData().get(0).getCount());
                    headerModel.getSsResult().setCommentCount(responseData.getData().get(0).getCommentCount());
                    headerModel.getSsResult().setLikeCount(responseData.getData().get(0).getLikeCount());
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ViewCountResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ArticleDetailResponse> isBookmarkedFollowedResponseCallback =
            new Callback<ArticleDetailResponse>() {
                @Override
                public void onResponse(Call<ArticleDetailResponse> call,
                        retrofit2.Response<ArticleDetailResponse> response) {
                    if (null == response.body()) {
                        if (!isAdded()) {
                            return;
                        }
                        ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                        return;
                    }

                    ArticleDetailResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        if (!isAdded()) {
                            return;
                        }
                        if (!userDynamoId.equals(authorId)) {
                            if (!responseData.getData().getResult().getIsFollowed()) {
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
                        if (isAdded()) {
                            ((ShortStoryContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
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
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback =
            new Callback<RecommendUnrecommendArticleResponse>() {
                @Override
                public void onResponse(Call<RecommendUnrecommendArticleResponse> call,
                        retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
                    isRecommendRequestRunning = false;
                    if (null == response.body()) {
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
                                    headerModel.getSsResult().setLikeCount(
                                            "" + (Integer.parseInt(headerModel.getSsResult().getLikeCount()) + 1));
                                }
                                headerModel.getSsResult().setLiked(true);
                            } else {
                                if (!responseData.getData().isEmpty()) {
                                    headerModel.getSsResult().setLikeCount(
                                            "" + (Integer.parseInt(headerModel.getSsResult().getLikeCount()) - 1));
                                }
                                headerModel.getSsResult().setLiked(false);
                            }
                            adapter.notifyDataSetChanged();
                            if (isAdded()) {
                                ((ShortStoryContainerActivity) getActivity()).showToast("" + responseData.getReason());
                            }
                        } else {
                            if (responseData.getCode() == 401) {
                                ((ShortStoryContainerActivity) getActivity())
                                        .showToast(responseData.getReason());
                            } else {
                                ((ShortStoryContainerActivity) getActivity())
                                        .showToast(getString(R.string.server_went_wrong));
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded()) {
                            ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
                    isRecommendRequestRunning = false;
                    handleExceptions(t);
                }
            };

    private void handleExceptions(Throwable t) {
        if (isAdded()) {
            if (t instanceof UnknownHostException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
                ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.connection_timeout));
            }
        }
        FirebaseCrashlytics.getInstance().recordException(t);
        Log.d("MC4kException", Log.getStackTraceString(t));
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.typeHere:
                    Bundle args = new Bundle();
                    AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                            new AddArticleCommentReplyDialogFragment();
                    addArticleCommentReplyDialogFragment.setArguments(args);
                    addArticleCommentReplyDialogFragment.setCancelable(true);
                    FragmentManager fm = getChildFragmentManager();
                    addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
                    break;
                case R.id.disableStatePostTextView:
                    if (isValid()) {
                        formatMentionDataForApiRequest();
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(typeHere.getWindowToken(), 0);
                    }
                    break;
                case R.id.viewAllTextView:
                    try {
                        Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                                "StoryDetail_Viewmore_Comment");
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.ARTICLE_ID, articleId);
                        bundle.putString(Constants.BLOG_SLUG, blogSlug);
                        bundle.putString(Constants.TITLE_SLUG, titleSlug);
                        bundle.putString(Constants.AUTHOR_ID, authorId);
                        bundle.putString("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY);
                        ArrayList<String> tagList = new ArrayList<>();
                        for (int i = 0; i < responseData.getTags().size(); i++) {
                            for (Map.Entry<String, String> mapEntry : responseData.getTags().get(i)
                                    .entrySet()) {
                                if (mapEntry.getKey().startsWith("category-")) {
                                    tagList.add(mapEntry.getKey());
                                }
                            }
                        }
                        bundle.putStringArrayList("tags", tagList);
                        ViewAllCommentsFragment viewAllCommentsFragment = new ViewAllCommentsFragment();
                        viewAllCommentsFragment.setArguments(bundle);
                        if (isAdded()) {
                            ((ShortStoryContainerActivity) getActivity()).addFragment(viewAllCommentsFragment, bundle);
                            ((ShortStoryContainerActivity) getActivity()).setToolbarTitle("Comments");
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded() && getActivity() != null) {
                            ((ShortStoryContainerActivity) getActivity())
                                    .showToast(getString(R.string.unable_to_load_comment));
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private boolean isValid() {
        if (StringUtils.isNullOrEmpty(typeHere.getText().toString())) {
            if (isAdded()) {
                Toast.makeText(getActivity(), getString(R.string.ad_comments_toast_empty_comment), Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    private void formatMentionDataForApiRequest() {
        Map<String, Mentions> mentionsMap = new HashMap<>();
        StringBuilder commentBody = new StringBuilder();
        try {
            MentionsEditable mentionsEditable = typeHere.getText();
            List<MentionIndex> marker = new ArrayList<>();
            marker.add(new MentionIndex(0, null));
            List<MentionSpan> mentionsList = typeHere.getMentionSpans();
            for (int i = 0; i < mentionsList.size(); i++) {
                Mentions mention = (Mentions) mentionsList.get(i).getMention();
                marker.add(new MentionIndex(mentionsEditable.getSpanStart(mentionsList.get(i)),
                        mention));
                mentionsMap.put(mention.getUserId(), mention);
            }
            marker.add(new MentionIndex(mentionsEditable.length(), null));
            Collections.sort(marker);
            ArrayList<MentionIndex> splittedComment = new ArrayList<>();
            for (int i = 0; i < marker.size() - 1; i++) {
                CharSequence value = mentionsEditable
                        .subSequence(marker.get(i).index, marker.get(i + 1).index);
                splittedComment.add(new MentionIndex(value, marker.get(i).mention));
            }
            for (int i = 0; i < splittedComment.size(); i++) {
                if (splittedComment.get(i).mention != null) {
                    commentBody.append(org.apache.commons.lang3.StringUtils
                            .replaceFirst(splittedComment.get(i).charSequence.toString(),
                                    splittedComment.get(i).mention.getName(),
                                    "[~userId:" + splittedComment.get(i).mention.getUserId() + "]"));
                } else {
                    commentBody.append(splittedComment.get(i).charSequence);
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        addComment(commentBody.toString(), mentionsMap);
    }


    @Override
    public void onClick(View view, int position, View whatsappShare) {
        switch (view.getId()) {
            case R.id.topCommentMarkedTextView:
                if (!consolidatedList.get(position).getSsComment().isTopCommentMarked()) {
                    TopCommentData commentListData = new TopCommentData(
                            consolidatedList.get(position).getSsComment().getPostId(),
                            consolidatedList.get(position).getSsComment().getId(), true);
                    markedUnMarkedTopComment(commentListData);
                    for (int i = 1; i < consolidatedList.size(); i++) {
                        if (i == position) {
                            consolidatedList.get(i).getSsComment().setTopCommentMarked(true);
                            consolidatedList.get(i).getSsComment().setIs_top_comment(false);
                        } else {
                            consolidatedList.get(i).getSsComment().setTopCommentMarked(false);
                            consolidatedList.get(i).getSsComment().setIs_top_comment(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.viewMoreTextView:
                try {
                    Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                            "StoryDetail_Viewall_Comment");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.ARTICLE_ID, articleId);
                    bundle.putString(Constants.BLOG_SLUG, blogSlug);
                    bundle.putString(Constants.TITLE_SLUG, titleSlug);
                    bundle.putString(Constants.AUTHOR_ID, authorId);
                    bundle.putString("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY);
                    ArrayList<String> tagList = new ArrayList<>();
                    for (int i = 0; i < responseData.getTags().size(); i++) {
                        for (Map.Entry<String, String> mapEntry : responseData.getTags().get(i)
                                .entrySet()) {
                            if (mapEntry.getKey().startsWith("category-")) {
                                tagList.add(mapEntry.getKey());
                            }
                        }
                    }
                    bundle.putStringArrayList("tags", tagList);
                    ViewAllCommentsFragment viewAllCommentsFragment = new ViewAllCommentsFragment();
                    viewAllCommentsFragment.setArguments(bundle);
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity()).addFragment(viewAllCommentsFragment, bundle);
                        ((ShortStoryContainerActivity) getActivity()).setToolbarTitle("Comments");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    if (isAdded() && getActivity() != null) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast(getString(R.string.unable_to_load_comment));
                    }
                }
                break;
            case R.id.commentorImageView:
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, consolidatedList.get(position).ssComment.getUserId());
                startActivity(intent);
                break;
            case R.id.likeTextView:
                pos = position;
                if (consolidatedList.get(position).getSsComment().getLiked()) {
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("0");
                    consolidatedList.get(position).getSsComment()
                            .setLikeCount(consolidatedList.get(position).getSsComment().getLikeCount() - 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(consolidatedList.get(position).getSsComment().getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                } else {
                    Utils.shareEventTracking(getActivity(), "100WS Detail", "Like_Android", "StoryDetail_Like_Comment");
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("1");
                    consolidatedList.get(position).getSsComment()
                            .setLikeCount(consolidatedList.get(position).getSsComment().getLikeCount() + 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(consolidatedList.get(position).getSsComment().getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                }
                break;
            case R.id.menuItem:
                chooseMenuOptionsItem(view, position);
                break;
            case R.id.moreOptionImageView:
            case R.id.commentRootLayout: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putString("authorId", consolidatedList.get(position).getSsComment().getUserId());
                args.putString("responseType", "COMMENT");
                args.putString("blogWriterId", authorId);
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment(this);
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android", "StoryDetail_Reply_Comment");
                if (consolidatedList.get(position).getSsComment().getRepliesCount() == 0) {
                    openAddCommentReplyDialog(consolidatedList.get(position).getSsComment(), null);
                } else {
                    shortStoryCommentRepliesDialogFragment = new ShortStoryCommentRepliesDialogFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("commentReplies", consolidatedList.get(position).getSsComment());
                    args.putInt("totalRepliesCount", consolidatedList.get(position).getSsComment().getRepliesCount());
                    args.putInt("position", position);
                    args.putString("blogWriterId", authorId);
                    shortStoryCommentRepliesDialogFragment.setArguments(args);
                    shortStoryCommentRepliesDialogFragment.setCancelable(true);
                    FragmentManager fm = getChildFragmentManager();
                    shortStoryCommentRepliesDialogFragment.show(fm, "View Replies");
                }
            }
            break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (headerModel.getSsResult().isLiked()) {
                        likeStatus = "0";
                        recommendUnrecommentArticleApi();
                    } else {
                        likeStatus = "1";
                        tooltipForShare(whatsappShare);
                        recommendUnrecommentArticleApi();
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
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM);
            }
            break;
            case R.id.genericShareImageView: {
                if (isAdded()) {
                    getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC);
                }
            }
            break;
            case R.id.authorNameTextView: {
                Intent intentnn = new Intent(getActivity(), UserProfileActivity.class);
                intentnn.putExtra(Constants.USER_ID, headerModel.getSsResult().getUserId());
                intentnn.putExtra(AppConstants.AUTHOR_NAME, headerModel.getSsResult().getUserName());
                intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryDetailsScreen");
                startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
            }
            break;
            case R.id.followAuthorTextView: {
                followAuthorTextView = (TextView) view;
                followApiCall();
            }
            break;
            default:
                break;
        }
    }

    private void markedUnMarkedTopComment(TopCommentData commentListData) {
        Utils.shareEventTracking(getActivity(), "100WS Detail", "TopComment_Android", "SD_TopComment");
        BaseApplication.getInstance().getRetrofit().create(ArticleDetailsAPI.class).markedTopComment(commentListData)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.d("MARKED--UNMARKED", responseBody.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Callback<ResponseBody> likeDisLikeCommentCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), getResources().getString(R.string.server_went_wrong));
                }
            }
            try {
                String res = new String(response.body().bytes());
                JSONObject responsee = new JSONObject(res);
                if (responsee.getInt("code") == 200 && responsee.get("status").equals("success")) {
                    if (consolidatedList.get(pos).getSsComment().getLiked()) {
                        consolidatedList.get(pos).getSsComment().setLiked(false);
                    } else {
                        consolidatedList.get(pos).getSsComment().setLiked(true);
                    }
                    adapter.notifyDataSetChanged();
                    JSONObject data = responsee.getJSONObject("data");
                    JSONObject result = data.getJSONObject("result");
                    String msg = result.getString("msg");
                    ToastUtils.showToast(getActivity(), msg);
                } else {
                    JSONObject data = responsee.getJSONObject("data");
                    JSONObject result = data.getJSONObject("result");
                    String msg = result.getString("msg");
                    ToastUtils.showToast(getActivity(), msg);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                ToastUtils.showToast(getActivity(), e.getMessage());
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    };

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
        if (isAdded()) {
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.all_insta_share_clipboard_msg));
        }
    }

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = shortStoryRecyclerView.getLayoutManager().findViewByPosition(position)
                .findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = headerModel.ssResult;
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        if (isAdded()) {
            Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
            shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
            // Bh**d**a facebook caches shareIntent. Need different name for all files
            String tempName = "" + System.currentTimeMillis();
            AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME + tempName);
            shareStory(tempName);
        }
    }

    private void shareStory(String tempName) {
        Uri uri = Uri.parse("file://" + BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator
                + AppConstants.STORY_SHARE_IMAGE_NAME + tempName + ".jpg");
        if (isAdded()) {
            switch (shareMedium) {
                case AppConstants.MEDIUM_FACEBOOK: {
                    SharingUtils.shareViaFacebook(getActivity(), uri);
                    Utils.shareEventTracking(getActivity(), "100WS Detail", "Share_Android", "WSD100_Facebook_Share");
                }
                break;
                case AppConstants.MEDIUM_WHATSAPP: {
                    if (AppUtils.shareImageWithWhatsApp(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(), AppUtils.getUtmParamsAppendedShareUrl(
                                    AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId(),
                                    "WSD100_Whatsapp_Share", "Share_Android")))) {
                        Utils.shareEventTracking(getActivity(), "100WS Detail", "Share_Android",
                                "WSD100_Whatsapp_Share");
                    }
                }
                break;
                case AppConstants.MEDIUM_INSTAGRAM: {
                    if (AppUtils.shareImageWithInstagram(getActivity(), uri)) {
                        Utils.shareEventTracking(getActivity(), "100WS Detail", "Share_Android",
                                "WSD100_Instagram_Share");
                    }
                }
                break;
                case AppConstants.MEDIUM_GENERIC: {
                    if (AppUtils.shareGenericImageAndOrLink(getActivity(), uri,
                            getString(R.string.ss_follow_author, sharedStoryItem.getUserName(),
                                    AppUtils.getUtmParamsAppendedShareUrl(
                                            AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId(),
                                            "WSD100_Generic_Share", "Share_Android")))) {
                        Utils.shareEventTracking(getActivity(), "100WS Detail", "Share_Android",
                                "WSD100_Generic_Share");
                    }
                }
                break;
                default:
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
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        } else {
            try {
                createBitmapForSharingStory();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }
    }

    void openAddCommentReplyDialog(CommentListData commentListData, CommentListData currentReplyData) {
        AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment =
                new AddShortStoryCommentReplyDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("currentReplyData", currentReplyData);
        args.putParcelable("parentCommentData", commentListData);
        addGpPostCommentReplyDialogFragment.setArguments(args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addGpPostCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    void addComment(String content, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Adding Comment");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id("0");
        addEditShortStoryCommentOrReplyRequest.setType("story");
        addEditShortStoryCommentOrReplyRequest.setMentions(mentionsMap);
        Call<CommentListResponse> call = shortStoryApi.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<CommentListResponse> addCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    typeHere.setText("");
                    CommentListData shortStoryCommentListData = new CommentListData();
                    shortStoryCommentListData.setId(responseData.getData().get(0).getId());
                    shortStoryCommentListData.setMessage(responseData.getData().get(0).getMessage());
                    shortStoryCommentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    shortStoryCommentListData.setPostId(responseData.getData().get(0).getPostId());
                    shortStoryCommentListData.setParentCommentId("0");
                    shortStoryCommentListData.setReplies(new ArrayList<>());
                    shortStoryCommentListData.setRepliesCount(0);
                    shortStoryCommentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    shortStoryCommentListData.setUserName(responseData.getData().get(0).getUserName());
                    shortStoryCommentListData.setUserId(responseData.getData().get(0).getUserId());
                    shortStoryCommentListData.setMentions(responseData.getData().get(0).getMentions());

                    ShortStoryDetailAndCommentModel commentModel = new ShortStoryDetailAndCommentModel();
                    commentModel.setSsComment(shortStoryCommentListData);
                    consolidatedCommentList.add(0, shortStoryCommentListData);
                    consolidatedList.clear();
                    consolidatedList.add(headerModel);
//                    consolidatedList.add(1, commentModel);
                    showComments(consolidatedCommentList);
//                    adapter.notifyDataSetChanged();
                    /*if (!StringUtils.isNullOrEmpty(userType) && !StringUtils.isNullOrEmpty(titleSlug) && !StringUtils
                            .isNullOrEmpty(blogSlug)) {
                        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);
                        shareCommentOnFacebook(shareUrl, responseData.getData().get(0).getMessage(),
                                responseData.getData().get(0).getMentions());
                    }*/
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "add", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editComment(String content, String responseId, int position, Map<String, Mentions> mentions) {
        showProgressDialog("Editing your response");
        actionItemPosition = position;
        editContent = content;
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setMentions(mentions);
        Call<CommentListResponse> call = shortStoryApi
                .editCommentOrReply(responseId, addEditShortStoryCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.get(actionItemPosition).getSsComment().setMessage(editContent);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment
                                .updateRepliesList(consolidatedList.get(actionItemPosition).getSsComment());
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "edit", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to edit comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void addReply(String content, String parentCommentId, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Adding Reply");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setParent_id(parentCommentId);
        addEditShortStoryCommentOrReplyRequest.setType("story");
        addEditShortStoryCommentOrReplyRequest.setMentions(mentionsMap);
        Call<CommentListResponse> call = shortStoryApi.addCommentOrReply(addEditShortStoryCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<CommentListResponse> addReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    CommentListData shortStoryCommentListData = new CommentListData();
                    shortStoryCommentListData.setId(responseData.getData().get(0).getId());
                    shortStoryCommentListData.setMessage(responseData.getData().get(0).getMessage());
                    shortStoryCommentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    shortStoryCommentListData.setPostId(responseData.getData().get(0).getPostId());
                    shortStoryCommentListData.setParentCommentId(responseData.getData().get(0).getParentCommentId());
                    shortStoryCommentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    shortStoryCommentListData.setUserName(responseData.getData().get(0).getUserName());
                    shortStoryCommentListData.setUserId(responseData.getData().get(0).getUserId());
                    shortStoryCommentListData.setMentions(responseData.getData().get(0).getMentions());
                    for (int i = 1; i < consolidatedList.size(); i++) {
                        if (consolidatedList.get(i).getSsComment().getId()
                                .equals(responseData.getData().get(0).getParentCommentId())) {
                            consolidatedList.get(i).getSsComment().getReplies().add(0, shortStoryCommentListData);
                            consolidatedList.get(i).getSsComment()
                                    .setRepliesCount(consolidatedList.get(i).getSsComment().getRepliesCount() + 1);
                            if (shortStoryCommentRepliesDialogFragment != null) {
                                shortStoryCommentRepliesDialogFragment
                                        .updateRepliesList(consolidatedList.get(i).getSsComment());
                            }
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "add", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to add reply. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void editReply(String content, String parentCommentId, String replyId, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditShortStoryCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditShortStoryCommentOrReplyRequest.setPost_id(articleId);
        addEditShortStoryCommentOrReplyRequest.setMessage(content);
        addEditShortStoryCommentOrReplyRequest.setMentions(mentionsMap);
        Call<CommentListResponse> call = shortStoryApi
                .editCommentOrReply(replyId, addEditShortStoryCommentOrReplyRequest);
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
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    boolean isReplyUpdated = false;
                    for (int i = 1; i < consolidatedList.size(); i++) {
                        if (consolidatedList.get(i).getSsComment().getId().equals(editReplyParentCommentId)) {
                            for (int j = 0; j < consolidatedList.get(i).getSsComment().getReplies().size(); j++) {
                                if (consolidatedList.get(i).getSsComment().getReplies().get(j).getId()
                                        .equals(editReplyId)) {
                                    consolidatedList.get(i).getSsComment().getReplies().get(j).setMessage(editContent);
                                    if (shortStoryCommentRepliesDialogFragment != null) {
                                        shortStoryCommentRepliesDialogFragment
                                                .updateRepliesList(consolidatedList.get(i).getSsComment());
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
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "edit", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to edit reply. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Call<CommentListResponse> call = shortStoryApi.deleteCommentOrReply(
                consolidatedList.get(commentPos).getSsComment().getReplies().get(replyPos).getId());
        call.enqueue(deleteReplyResponseListener);
    }


    @Override
    public void onTextChanged(@NotNull String text) {
        if (text.isEmpty()) {
            disableStatePostTextView
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_post_comment_disabled_state));
        } else {
            horizontalCommentSuggestionsContainer.setVisibility(View.GONE);
            SharedPrefUtils.setCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext(), false);
            disableStatePostTextView
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_post_comment_enabled_state));
        }
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                }

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedList.get(deleteCommentPos).getSsComment().getReplies().remove(deleteReplyPos);
                    consolidatedList.get(deleteCommentPos).getSsComment().setRepliesCount(
                            consolidatedList.get(deleteCommentPos).getSsComment().getRepliesCount() - 1);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment
                                .updateRepliesList(consolidatedList.get(deleteCommentPos).getSsComment());
                        if (consolidatedList.get(deleteCommentPos).getSsComment().getRepliesCount() == 0) {
                            shortStoryCommentRepliesDialogFragment.dismiss();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "delete", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to delete reply. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseDelete(int position, String responseType) {
        Call<CommentListResponse> call = shortStoryApi
                .deleteCommentOrReply(consolidatedList.get(position).getSsComment().getId());
        call.enqueue(deleteCommentResponseListener);
        actionItemPosition = position;
    }

    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity())
                            .showToast("Failed to delete comment. Please try again");
                }

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    consolidatedCommentList.remove(actionItemPosition - 1);
                    if (shortStoryCommentRepliesDialogFragment != null) {
                        shortStoryCommentRepliesDialogFragment.dismiss();
                    }
                    consolidatedList.clear();
                    consolidatedList.add(headerModel);
                    showComments(consolidatedCommentList);
                    adapter.notifyDataSetChanged();
                    if (isAdded()) {
                        Utils.pushShortStoryCommentReplyChangeEvent(getActivity(), "ShortStoryDetailsScreen",
                                userDynamoId, articleId, "delete", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity())
                                .showToast("Failed to delete comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ((ShortStoryContainerActivity) getActivity())
                            .showToast("Failed to delete comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ((ShortStoryContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseEdit(int position, String responseType) {
        Bundle args = new Bundle();
        args.putString("action", "EDIT_COMMENT");
        args.putParcelable("parentCommentData", consolidatedList.get(position).getSsComment());
        args.putInt("position", position);
        AddShortStoryCommentReplyDialogFragment addGpPostCommentReplyDialogFragment =
                new AddShortStoryCommentReplyDialogFragment();
        addGpPostCommentReplyDialogFragment.setArguments(args);
        addGpPostCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addGpPostCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        Bundle args = new Bundle();
        args.putString("postId", consolidatedList.get(position).getSsComment().getId());
        args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        reportContentDialogFragment.setArguments(args);
        reportContentDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        reportContentDialogFragment.show(fm, "Report Content");
    }

    @Override
    public void onBlockUser(int position, String responseType) {
        showProgressDialog("please wait");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
        BlockUserModel blockUserModel = new BlockUserModel();
        blockUserModel.setBlocked_user_id(consolidatedList.get(position).getSsComment().getUserId());
        Call<ResponseBody> call = articleDetailsAPI.blockUserApi(blockUserModel);
        call.enqueue(blockUserCallBack);
       /* consolidatedList.remove(position);
        adapter.setListData(consolidatedList);
        adapter.notifyDataSetChanged();*/
    }

    private Callback<ResponseBody> blockUserCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Please try again");
                }
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                if (jsonObject.getInt("code") == 200 && jsonObject.getString("status").equals(Constants.SUCCESS)) {
                    ToastUtils.showToast(getActivity(), jsonObject.getJSONObject("data").getString("msg").toString());
                }


            } catch (Exception t) {
                removeProgressDialog();
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }


        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        final QueryTokenReceiver receiver = typeHere;
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsApi = retro.create(SearchArticlesAuthorsAPI.class);
        Call<MentionsResponse> call = searchArticlesAuthorsApi.searchUserHandles(queryToken.getKeywords());
        call.enqueue(new Callback<MentionsResponse>() {
            @Override
            public void onResponse(Call<MentionsResponse> call, Response<MentionsResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        MentionsResponse responseModel = response.body();
                        List<Mentions> suggestions = new ArrayList<>(responseModel.getData().getResult());
                        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
                        typeHere.onReceiveSuggestionsResult(result, "dddd");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<MentionsResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });

        return Arrays.asList("dddd");
    }

    public class ShortStoryDetailAndCommentModel {

        private int type;
        private ShortStoryDetailResult ssResult;
        private CommentListData ssComment;
        private CommentsData fbComment;

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

        public CommentsData getFbComment() {
            return fbComment;
        }

        public void setFbComment(CommentsData fbComment) {
            this.fbComment = fbComment;
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions()).show();
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
                createBitmapForSharingStory();
                if (isAdded()) {
                    if (AppConstants.MEDIUM_WHATSAPP.equals(shareMedium)) {
                        AppUtils.shareStoryWithWhatsApp(getActivity(), headerModel.getSsResult().getUserType(),
                                headerModel.getSsResult().getBlogTitleSlug(), headerModel.getSsResult().getTitleSlug(),
                                "ShortStoryDetailsScreen", userDynamoId, articleId, authorId, author);
                    } else if (AppConstants.MEDIUM_INSTAGRAM.equals(shareMedium)) {
                        AppUtils.shareStoryWithInstagram(getActivity(), "ShortStoryDetailsScreen", userDynamoId,
                                articleId, authorId, author);
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

    private void followApiCall() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowee_id(authorId);
        if (isFollowing) {
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_100WS_Detail", userDynamoId, "ShortStoryFragment");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi.unfollowUserV2(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            Utils.shareEventTracking(getActivity(), "100WS Detail", "Follow_Android", "StoryDetail_Follow");
            Call<FollowUnfollowUserResponse> followUnfollowUserResponseCall = followApi.followUserV2(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<FollowUnfollowUserResponse> unfollowUserResponseCallback =
            new Callback<FollowUnfollowUserResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowUserResponse> call,
                        retrofit2.Response<FollowUnfollowUserResponse> response) {
                    if (response.body() == null) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowUserResponse responseData = response.body();
                        if (responseData.getCode() == 200 || Constants.SUCCESS.equals(responseData.getStatus())) {
                            if (getActivity() != null) {
                                ((BaseActivity) getActivity()).syncFollowingList();
                                ToastUtils.showToast(getActivity(), responseData.getData().getMsg());
                            }
                            isFollowing = false;
                            adapter.setAuthorFollowingStatus(AppConstants.STATUS_NOT_FOLLOWING);
                            adapter.notifyItemChanged(0);
                        }
                        if (responseData.getCode() != 200 || !Constants.SUCCESS.equals(responseData.getStatus())) {
                            adapter.setAuthorFollowingStatus(AppConstants.STATUS_FOLLOWING);
                            adapter.notifyItemChanged(0);
                            ToastUtils.showToast(getActivity(), responseData.getData().getMsg());
                            isFollowing = true;
                        }
                    } catch (Exception e) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                    }
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private Callback<FollowUnfollowUserResponse> followUserResponseCallback =
            new Callback<FollowUnfollowUserResponse>() {
                @Override
                public void onResponse(Call<FollowUnfollowUserResponse> call,
                        retrofit2.Response<FollowUnfollowUserResponse> response) {
                    if (response.body() == null) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowUnfollowUserResponse responseData = response.body();
                        if (responseData.getCode() == 200 || Constants.SUCCESS.equals(responseData.getStatus())) {
                            if (getActivity() != null) {
                                ((BaseActivity) getActivity()).syncFollowingList();
                                ToastUtils.showToast(getActivity(), responseData.getData().getMsg());
                            }
                            isFollowing = true;
                            adapter.setAuthorFollowingStatus(AppConstants.STATUS_FOLLOWING);
                            adapter.notifyItemChanged(0);
                        }

                        if (responseData.getCode() != 200 || !Constants.SUCCESS.equals(responseData.getStatus())) {
                            adapter.setAuthorFollowingStatus(AppConstants.STATUS_NOT_FOLLOWING);
                            adapter.notifyItemChanged(0);
                            isFollowing = false;
                            ToastUtils.showToast(getActivity(), responseData.getData().getMsg());

                        }

                    } catch (Exception e) {
                        if (isAdded()) {
                            ((ArticleDetailsContainerActivity) getActivity())
                                    .showToast(getString(R.string.server_went_wrong));
                        }
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {
                    if (isAdded()) {
                        ((ShortStoryContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                    }
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };


    private void tooltipForShare(View shareImageView) {
        simpleTooltip = new SimpleTooltip.Builder(getContext())
                .anchorView(shareImageView)
                .backgroundColor(getResources().getColor(R.color.app_blue))
                .text(getResources().getString(R.string.ad_bottom_bar_generic_share))
                .textColor(getResources().getColor(R.color.white_color))
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
        handler.postDelayed(() -> {
            if (simpleTooltip.isShowing()) {
                simpleTooltip.dismiss();
            }
        }, 3000);

    }


    @SuppressLint("RestrictedApi")
    private void chooseMenuOptionsItem(View view, int position) {

        final androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(getActivity(),
                view);
        popupMenu.getMenuInflater().inflate(R.menu.choose_short_story_menu, popupMenu.getMenu());
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            Drawable drawable = popupMenu.getMenu().getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
            }
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.addCollection) {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                            new AddCollectionAndCollectionItemDialogFragment();
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
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                return true;
            } else if (item.getItemId() == R.id.bookmarkShortStory) {
                return true;
            } else if (item.getItemId() == R.id.copyLink) {
                AppUtils.copyToClipboard(
                        AppUtils.getShortStoryShareUrl(consolidatedList.get(position).getSsResult().getUserType(),
                                consolidatedList.get(position).getSsResult().getBlogTitleSlug(),
                                consolidatedList.get(position).getSsResult().getTitleSlug()));
                if (isAdded()) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ss_story_link_copied),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                Bundle args = new Bundle();
                args.putString("postId", consolidatedList.get(position).getSsResult().getId());
                args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(args);
                reportContentDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                reportContentDialogFragment.show(fm, "Report Content");
                return true;
            }
            return false;
        });

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(),
                view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
    }
}
