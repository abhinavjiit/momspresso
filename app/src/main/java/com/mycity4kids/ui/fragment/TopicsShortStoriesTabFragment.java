package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryModerationOrShareActivity;
import com.mycity4kids.ui.activity.TopicsListingFragment;
import com.mycity4kids.ui.activity.ViewAllCommentsActivity;
import com.mycity4kids.ui.adapter.ShortStoriesRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.mycity4kids.widget.TrackingData;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsShortStoriesTabFragment extends BaseFragment implements View.OnClickListener,
        ShortStoriesRecyclerAdapter.RecyclerViewClickListener {

    private static final int REQUEST_INIT_PERMISSION = 2;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final long MIN_TIME_VIEW = 3;
    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> articleListingResults;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;

    private ShortStoriesRecyclerAdapter recyclerAdapter;

    private LinearLayoutManager llm;
    private RelativeLayout lodingView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFab;
    private FloatingActionButton recentSortFab;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private String userDynamoId;
    private ShortStoryAPI shortStoryApi;
    Set<Integer> viewedStoriesSet = new HashSet<>();
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private int currentShortStoryPosition;
    private String jsonMyObject;
    private SwipeRefreshLayout pullToRefresh;
    private String shareMedium;
    private RelativeLayout rootLayout;
    private SimpleTooltip simpleTooltip;
    private Handler handler;
    private int position;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ArticleListingResult sharedStoryItem;
    private String challengeId;
    private String tabPosition = "0";
    private int start = 0;
    private TextView noBlogsTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_short_stories_tab_fragment, container, false);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                .getDynamoId();

        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        lodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        guideOverlay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) view.findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFab = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFab = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);

        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
        popularSortFab.setOnClickListener(this);
        recentSortFab.setOnClickListener(this);
        fabMenu.setVisibility(View.GONE);
        fabSort.setOnClickListener(v -> {
            if (fabMenu.isExpanded()) {
                fabMenu.collapse();
            } else {
                fabMenu.expand();
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        frameLayout.getBackground().setAlpha(240);
                        frameLayout.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                fabMenu.collapse();
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onMenuCollapsed() {
                        frameLayout.getBackground().setAlpha(0);
                        frameLayout.setOnTouchListener(null);
                    }
                });

        articleListingResults = new ArrayList<>();
        recyclerAdapter = new ShortStoriesRecyclerAdapter(getActivity(), this);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(articleListingResults);
        recyclerView.setAdapter(recyclerAdapter);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        shortStoryApi = retro.create(ShortStoryAPI.class);

        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey("currentSubTopic")) {
                jsonMyObject = extras.getString("currentSubTopic");
                currentSubTopic = new Gson().fromJson(jsonMyObject, Topics.class);
                selectedTopic = currentSubTopic;
            } else if (extras.containsKey("shortStoryChallengeId") && extras.containsKey("position")) {
                tabPosition = extras.getString("position");
                challengeId = extras.getString("shortStoryChallengeId");
                pullToRefresh.setEnabled(false);
            }
        }
        if ("0".equals(tabPosition)) {
            hitFilteredTopicsArticleListingApi(sortType);
        } else {
            writeArticleCell.setVisibility(View.GONE);
            getWinnerShortStories(start);
        }

        pullToRefresh.setOnRefreshListener(() -> {
            articleListingResults.clear();
            recyclerAdapter.notifyDataSetChanged();
            nextPageNumber = 1;
            hitFilteredTopicsArticleListingApi(sortType);
            pullToRefresh.setRefreshing(false);
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            lodingView.setVisibility(View.VISIBLE);
                            if ("0".equals(tabPosition)) {
                                hitFilteredTopicsArticleListingApi(sortType);
                            } else {
                                getWinnerShortStories(start);
                            }
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getWinnerShortStories(int start) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);
        Call<ArticleListingResponse> filterCall = topicsApi
                .getWinnerArticleChallenge(start, 10, challengeId, "1");
        filterCall.enqueue(articleListingResponseCallback);
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        if (selectedTopic != null) {
            Call<ArticleListingResponse> filterCall = topicsApi
                    .getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1, "0");
            filterCall.enqueue(articleListingResponseCallback);
        } else {
            Call<ArticleListingResponse> filterCall = topicsApi
                    .getArticlesForCategory(challengeId, sortType, from, from + limit - 1, "0");
            filterCall.enqueue(articleListingResponseCallback);
        }
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            if (lodingView.getVisibility() == View.VISIBLE) {
                lodingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (tabPosition.equals("0")) {
                        processArticleListingResponse(responseData);
                    } else {
                        processWinnerArticleListingResponse(responseData);
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (lodingView.getVisibility() == View.VISIBLE) {
                lodingView.setVisibility(View.GONE);
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processWinnerArticleListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleListingResults && !articleListingResults.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                noBlogsTextView.setVisibility(View.VISIBLE);
                articleListingResults = dataList;
                recyclerAdapter.setListData(articleListingResults);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            AppUtils.updateFollowingStatus(dataList);
            if (start == 0) {
                articleListingResults = dataList;
            } else {
                articleListingResults.addAll(dataList);
            }
            recyclerAdapter.setListData(articleListingResults);
            start = start + 10;
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void processArticleListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleListingResults && !articleListingResults.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                if (tabPosition.equals("0")) {
                    writeArticleCell.setVisibility(View.VISIBLE);
                }
                articleListingResults = dataList;
                recyclerAdapter.setListData(articleListingResults);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleListingResults = dataList;

            } else {
                articleListingResults.addAll(dataList);
            }
            recyclerAdapter.setListData(articleListingResults);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
            if (nextPageNumber == 2) {
                startTracking();
            }
        }
    }

    private void hitArticleListingSortByRecent() {
        fabMenu.collapse();
        articleListingResults.clear();
        recyclerAdapter.notifyDataSetChanged();
        sortType = 0;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(0);
    }

    private void hitArticleListingSortByPopular() {
        fabMenu.collapse();
        articleListingResults.clear();
        recyclerAdapter.notifyDataSetChanged();
        sortType = 1;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                if (isAdded()) {
                    Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                TopicsListingFragment frag = ((TopicsListingFragment) this.getParentFragment());
                frag.hideTabLayer();
                if (isAdded()) {
                    SharedPrefUtils
                            .setCoachmarksShownFlag(BaseApplication.getAppContext(),
                                    "topics_article", true);
                }
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                articleListingResults.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                articleListingResults.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view, final int position, View shareImageView) {
        switch (view.getId()) {
            case R.id.icSsComment:
                Intent commentIntent = new Intent(getActivity(), ViewAllCommentsActivity.class);
                commentIntent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                commentIntent.putExtra(Constants.AUTHOR_ID, articleListingResults.get(position).getUserId());
                commentIntent.putExtra(Constants.BLOG_SLUG, articleListingResults.get(position).getBlogPageSlug());
                commentIntent.putExtra(Constants.TITLE_SLUG, articleListingResults.get(position).getTitleSlug());
                ArrayList<String> tagList = new ArrayList<>();
                for (int i = 0; i < articleListingResults.get(position).getTags().size(); i++) {
                    for (Map.Entry<String, String> mapEntry : articleListingResults.get(position).getTags().get(i)
                            .entrySet()) {
                        if (mapEntry.getKey().startsWith("category-")) {
                            tagList.add(mapEntry.getKey());
                        }
                    }
                }
                commentIntent.putExtra("tags", tagList);
                startActivity(commentIntent);
                break;
            case R.id.menuItem: {
                chooseMenuOptionsItem(view, position);
            }
            break;
            case R.id.storyImageView1:
                Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleListingResults.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleListingResults.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleListingResults.get(position).getTitleSlug());
                if (currentSubTopic != null) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM,
                            "" + currentSubTopic.getParentName());
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM,
                            "" + "ShortStoryChallenge");
                }
                intent.putExtra(Constants.FROM_SCREEN, "TopicsShortStoryTabFragment");
                intent.putExtra(Constants.AUTHOR,
                        articleListingResults.get(position).getUserId() + "~" + articleListingResults.get(position)
                                .getUserName());
                startActivity(intent);
                break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (articleListingResults.get(position).isLiked()) {
                        likeStatus = "0";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleApi(articleListingResults.get(position).getId(),
                                articleListingResults.get(position).getUserId(),
                                articleListingResults.get(position).getUserName());
                    } else {
                        Utils.shareEventTracking(getActivity(), "100WS Listing", "Like_Android", "StoryListing_Like");
                        tooltipForShare(shareImageView);
                        likeStatus = "1";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleApi(articleListingResults.get(position).getId(),
                                articleListingResults.get(position).getUserId(),
                                articleListingResults.get(position).getUserName());
                    }
                }
                break;
            case R.id.facebookShareImageView: {
                getSharableViewForPosition(position, AppConstants.MEDIUM_FACEBOOK);
            }
            break;
            case R.id.whatsappShareImageView: {
                getSharableViewForPosition(position, AppConstants.MEDIUM_WHATSAPP);
            }
            break;
            case R.id.instagramShareImageView: {
                try {
                    filterTags(articleListingResults.get(position).getTags());
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
            case R.id.authorNameTextView:
                if (BuildConfig.DEBUG) {
                    Intent debugIntent = new Intent(getActivity(), ShortStoryModerationOrShareActivity.class);
                    debugIntent.putExtra("shareUrl", articleListingResults.get(position).getStoryImage());
                    debugIntent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                    startActivity(debugIntent);
                } else {
                    Intent pintent = new Intent(getActivity(), UserProfileActivity.class);
                    pintent.putExtra(Constants.USER_ID, articleListingResults.get(position).getUserId());
                    startActivity(pintent);
                }
                break;
            case R.id.followAuthorTextView:
                followApiCall(articleListingResults.get(position).getUserId(), position);
                break;
            default:
                break;

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
        if (isAdded()) {
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.all_insta_share_clipboard_msg));
        }
    }

    private void followApiCall(String authorId, int position) {
        this.position = position;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowee_id(authorId);
        if (articleListingResults.get(position).getIsfollowing().equals("1")) {
            Utils.pushGenericEvent(getActivity(), "CTA_Unfollow_100WS_Detail", userDynamoId,
                    "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followApi.unfollowUserInShortStoryListingV2(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            Utils.shareEventTracking(getActivity(), "100WS Listing", "Follow_Android", "StoryListing_Follow");
            Call<ResponseBody> followUnfollowUserResponseCall = followApi.followUserInShortStoryListingV2(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<ResponseBody> unfollowUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "some thing went wrong ");
                }
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity()).syncFollowingList();
                    }
                    articleListingResults.get(position).setIsfollowing("0");
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(getActivity(), reason);
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> followUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "some thing went wrong ");
                }
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    if (getActivity() != null) {
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).syncFollowingList();
                        }
                    }
                    articleListingResults.get(position).setIsfollowing("1");
                    recyclerAdapter.notifyDataSetChanged();
                } else if (code == 200 && "failure".equals(status) && "Already following!".equals(reason)) {
                    articleListingResults.get(position).setIsfollowing("1");
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(getActivity(), reason);
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "some thing went wrong at the server ");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = recyclerView.getLayoutManager().findViewByPosition(position)
                .findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = articleListingResults.get(position);
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        if (isAdded()) {
            Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
            shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
            //Bh**d**a facebook caches shareIntent. Need different name for all files
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
                    Utils.shareEventTracking(getActivity(), "100WS Listing", "Share_Android", "WSL100_Facebook_Share");
                }
                break;
                case AppConstants.MEDIUM_WHATSAPP: {
                    if (AppUtils.shareImageWithWhatsApp(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(),
                            AppUtils.getUtmParamsAppendedShareUrl(
                                    AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId(),
                                    "WSL100_Whatsapp_Share", "Share_Android")))) {
                        Utils.shareEventTracking(getActivity(), "100WS Listing", "Share_Android",
                                "WSL100_Whatsapp_Share");
                    }
                }
                break;
                case AppConstants.MEDIUM_INSTAGRAM: {
                    if (AppUtils.shareImageWithInstagram(getActivity(), uri)) {
                        Utils.shareEventTracking(getActivity(), "100WS Listing", "Share_Android",
                                "WSL100_Instagram_Share");
                    }
                }
                break;
                case AppConstants.MEDIUM_GENERIC: {
                    if (AppUtils.shareGenericImageAndOrLink(getActivity(), uri, getString(R.string.ss_follow_author,
                            sharedStoryItem.getUserName(),
                            AppUtils.getUtmParamsAppendedShareUrl(
                                    AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId(),
                                    "WSL100_Generic_Share", "Share_Android")))) {
                        Utils.shareEventTracking(getActivity(), "100WS Listing", "Share_Android",
                                "WSL100_Generic_Share");
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private void recommendUnrecommentArticleApi(String articleId, String authorId,
            String author) {
        Utils.pushLikeStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "",
                articleId,
                authorId + "~" + author);
        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest =
                new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

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
                        ((ShortStoriesListingContainerActivity) getActivity())
                                .showToast(getString(R.string.server_went_wrong));
                        return;
                    }

                    try {
                        RecommendUnrecommendArticleResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS
                                .equals(responseData.getStatus())) {
                            if (likeStatus.equals("1")) {
                                if (!responseData.getData().isEmpty()) {
                                    articleListingResults.get(currentShortStoryPosition).setLikesCount(
                                            "" + (Integer.parseInt(articleListingResults.get(currentShortStoryPosition)
                                                    .getLikesCount())
                                                    + 1));
                                }
                                articleListingResults.get(currentShortStoryPosition).setLiked(true);
                            } else {
                                if (!responseData.getData().isEmpty()) {
                                    articleListingResults.get(currentShortStoryPosition).setLikesCount(
                                            "" + (Integer.parseInt(articleListingResults.get(currentShortStoryPosition)
                                                    .getLikesCount())
                                                    - 1));
                                }
                                articleListingResults.get(currentShortStoryPosition).setLiked(false);
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            if (isAdded()) {
                                Toast.makeText(getActivity(), "" + responseData.getReason(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (isAdded()) {
                                ((ShortStoriesListingContainerActivity) getActivity())
                                        .showToast(responseData.getReason());
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded()) {
                            ((ShortStoriesListingContainerActivity) getActivity())
                                    .showToast(getString(R.string.went_wrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
                    isRecommendRequestRunning = false;
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    if (isAdded()) {
                        ((ShortStoriesListingContainerActivity) getActivity())
                                .showToast(getString(R.string.went_wrong));
                    }
                }
            };


    // Time from which a particular view has been started viewing.
    private long startTime = 0;

    // Time at which a particular view has been stopped viewing.
    private long endTime = 0;

    // Flag is required because 'addOnGlobalLayoutListener'
    // is called multiple times.
    // The flag limits the action inside 'onGlobalLayout' to only once.
    private boolean firstTrackFlag = false;

    // ArrayList of view ids that are being considered for tracking.
    private ArrayList<Integer> viewsViewed = new ArrayList<>();

    // ArrayList of TrackingData class instances.
    private ArrayList<TrackingData> trackingData = new ArrayList<>();

    // The minimum amount of area of the list item that should be on
    // the screen for the tracking to start.
    private double minimumVisibleHeightThreshold = 60;

    // Start the tracking process.
    private void startTracking() {

        // Track the views when the data is loaded into
        // recycler view for the first time.
        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> {
                    if (!firstTrackFlag) {
                        startTime = System.currentTimeMillis();
                        int firstVisibleItemPosition = ((LinearLayoutManager)
                                recyclerView.getLayoutManager())
                                .findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = ((LinearLayoutManager)
                                recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition();
                        analyzeAndAddViewData(firstVisibleItemPosition,
                                lastVisibleItemPosition);
                        firstTrackFlag = true;
                    }
                });

        // Track the views when user scrolls through the recyclerview.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    endTime = System.currentTimeMillis();
                    for (int trackedViewsCount = 0;
                            trackedViewsCount < viewsViewed.size();
                            trackedViewsCount++) {
                        if (((endTime - startTime) / 1000) >= MIN_TIME_VIEW) {
                            if (!viewedStoriesSet.contains(viewsViewed.get(trackedViewsCount))) {
                                try {
                                    updateViewCount(viewsViewed.get(trackedViewsCount));
                                    viewedStoriesSet.add(viewsViewed.get(trackedViewsCount));
                                } catch (Exception e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    Log.d("MC4KException", Log.getStackTraceString(e));
                                }

                            }
                        }
                    }
                    viewsViewed.clear();
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    startTime = System.currentTimeMillis();
                    int firstVisibleItemPosition = ((LinearLayoutManager)
                            recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = ((LinearLayoutManager)
                            recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
                    analyzeAndAddViewData(firstVisibleItemPosition,
                            lastVisibleItemPosition);
                }
            }
        });
    }

    // Track the items currently visible and then stop the tracking process.
    public void stopTracking() {

        endTime = System.currentTimeMillis();

        int firstVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        int lastVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        analyzeAndAddViewData(firstVisibleItemPosition,
                lastVisibleItemPosition);

        for (int trackedViewsCount = 0; trackedViewsCount < viewsViewed.size();
                trackedViewsCount++) {
            if (((endTime - startTime) / 1000) >= MIN_TIME_VIEW) {
                if (!viewedStoriesSet.contains(viewsViewed.get(trackedViewsCount))) {
                    try {
                        updateViewCount(viewsViewed.get(trackedViewsCount));
                        viewedStoriesSet.add(viewsViewed.get(trackedViewsCount));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }
            }
            viewsViewed.clear();
        }
    }

    private void analyzeAndAddViewData(int firstVisibleItemPosition,
            int lastVisibleItemPosition) {

        // Analyze all the views
        for (int viewPosition = firstVisibleItemPosition;
                viewPosition <= lastVisibleItemPosition; viewPosition++) {

            Log.i("View being considered", String.valueOf(viewPosition));

            // Get the view from its position.
            View itemView = recyclerView.getLayoutManager()
                    .findViewByPosition(viewPosition);
            if (viewPosition >= 0) {
                if (getVisibleHeightPercentage(itemView) >= minimumVisibleHeightThreshold) {
                    viewsViewed.add(viewPosition);
                }
            }
        }
    }

    // Method to calculate how much of the view is visible
    // (i.e. within the screen) wrt the view height.
    // @param view
    // @return Percentage of the height visible.
    private double getVisibleHeightPercentage(View view) {

        Rect itemRect = new Rect();
        view.getLocalVisibleRect(itemRect);

        // Find the height of the item.
        double visibleHeight = itemRect.height();
        double height = view.getMeasuredHeight();

        Log.i("Visible Height", String.valueOf(visibleHeight));
        Log.i("Measured Height", String.valueOf(height));

        double viewVisibleHeightPercentage = ((visibleHeight / height) * 100);

        Log.i("Percentage visible", String.valueOf(viewVisibleHeightPercentage));

        Log.i("___", "___");

        return viewVisibleHeightPercentage;
    }

    private void updateViewCount(int position) {
        UpdateViewCountRequest updateViewCountRequest = new UpdateViewCountRequest();
        updateViewCountRequest.setUserId(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        ArrayList<Map<String, String>> tagData = articleListingResults.get(position).getTags();
        Map<String, String> tagMap = new HashMap<>();
        for (Map.Entry<String, String> entry : tagData.get(0).entrySet()) {
            if (entry.getKey().contains("category-")) {
                tagMap.put(entry.getKey(), entry.getValue());
                break;
            }
        }
        List<Map<String, String>> requestTagList = new ArrayList<>();
        requestTagList.add(tagMap);
        updateViewCountRequest.setTags(requestTagList);
        updateViewCountRequest.setContentType("1");
        Call<ResponseBody> callUpdateViewCount = shortStoryApi
                .updateViewCount(articleListingResults.get(position).getId(), updateViewCountRequest);
        callUpdateViewCount.enqueue(updateViewCountResponseCallback);
    }

    private Callback<ResponseBody> updateViewCountResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };


    public void showSortedByDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_sort_by);
            dialog.setCancelable(true);
            dialog.findViewById(R.id.linearSortByPopular)
                    .setOnClickListener(view -> {
                        hitArticleListingSortByPopular();
                        dialog.dismiss();
                    });
            dialog.findViewById(R.id.linearSortByRecent)
                    .setOnClickListener(view -> {
                        hitArticleListingSortByRecent();
                        dialog.dismiss();
                    });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.textUpdate).setOnClickListener(view -> dialog.dismiss());
            dialog.show();
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
            if (ActivityCompat.checkSelfPermission(getActivity(), s)
                    != PackageManager.PERMISSION_GRANTED) {
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
        if (!isAdded()) {
            return;
        }
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
                    bundle.putString("articleId", articleListingResults.get(position).getId());
                    bundle.putString("type", AppConstants.SHORT_STORY_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    addCollectionAndCollectionitemDialogFragment.setTargetFragment(this, 0);
                    addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    Utils.pushProfileEvents(getActivity(), "CTA_100WS_Add_To_Collection",
                            "TopicsShortStoriesTabFragment", "Add to Collection", "-");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
                return true;
            } else if (item.getItemId() == R.id.bookmarkShortStory) {
                return true;
            } else if (item.getItemId() == R.id.copyLink) {
                AppUtils.copyToClipboard(
                        AppUtils.getShortStoryShareUrl(articleListingResults.get(position).getUserType(),
                                articleListingResults.get(position).getBlogPageSlug(),
                                articleListingResults.get(position).getTitleSlug()));
                if (isAdded()) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.ss_story_link_copied),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                Bundle args = new Bundle();
                args.putString("postId", articleListingResults.get(position).getId());
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
