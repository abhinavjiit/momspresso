package com.mycity4kids.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.mycity4kids.ui.fragment.ReportContentDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ShortStoryChallengeDetailActivity extends BaseActivity implements View.OnClickListener,
        ChallengeListingRecycleAdapter.RecyclerViewClickListener {

    private static final int REQUEST_INIT_PERMISSION = 2;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ChallengeListingRecycleAdapter challengeListingRecycleAdapter;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private String parentName;
    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> articleListingResults;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private RelativeLayout lodingView;
    private String selectedId;
    private String selectedName;
    private String selectedActiveUrl;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private RelativeLayout writeArticleCell;
    private String userDynamoId;
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private int currentShortStoryPosition;
    private RelativeLayout chooseLayout;
    private RelativeLayout relativeFrame;
    private String ssTopicsText;
    private RelativeLayout root;
    private SwipeRefreshLayout pullToRefresh;
    private String shareMedium;
    private int position;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ArticleListingResult sharedStoryItem;
    private String shortStoryCategoryId;
    private TextView startWriting;
    private TextView toolbarTitle;
    private View overlayLayout;
    private RelativeLayout guideOverlay;
    private FloatingActionButton popularSortFab;
    private FloatingActionButton recentSortFab;
    private FloatingActionButton fabSort;
    private RadioGroup chooseoptionradioButton;
    private Toolbar toolbar;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challnege_detail_listing);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        startWriting = (TextView) findViewById(R.id.start_writing);
        relativeFrame = (RelativeLayout) findViewById(R.id.framelayout_relative);
        chooseLayout = (RelativeLayout) findViewById(R.id.choose_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_listing);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitleTextView);
        lodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        guideOverlay = (RelativeLayout) findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        pullToRefresh = findViewById(R.id.pullToRefresh);

        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        popularSortFab = (FloatingActionButton) findViewById(R.id.popularSortFAB);
        recentSortFab = (FloatingActionButton) findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);

        ssTopicsList = new ArrayList<>();
        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
        popularSortFab.setOnClickListener(this);
        recentSortFab.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        fabSort.setOnClickListener(v -> {
            if (fabMenu.isExpanded()) {
                fabMenu.collapse();
            } else {
                fabMenu.expand();
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener((v, event) -> {
                    fabMenu.collapse();
                    return true;
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        articleListingResults = new ArrayList<>();
        Intent intent = getIntent();
        pos = intent.getIntExtra("position", 0);
        selectedId = intent.getStringExtra("challenge");
        selectedName = intent.getStringExtra("Display_Name");
        selectedActiveUrl = intent.getStringExtra("StringUrl");
        parentName = intent.getStringExtra("topics");
        String challengeComingFrom = intent.getStringExtra("selectedrequest");
        if (challengeComingFrom == null) {
            challengeComingFrom = "challenge";
        }
        if ("FromDeepLink".equals(challengeComingFrom)) {
            selectedId = intent.getStringExtra("challenge");
            selectedName = intent.getStringExtra("Display_Name");
            selectedActiveUrl = intent.getStringExtra("StringUrl");
            chooseLayout.setVisibility(View.VISIBLE);
        } else {
            if ("storyListingCard".equals(getIntent().getStringExtra("source"))) {
                chooseLayout.setVisibility(View.VISIBLE);
            } else {
                chooseLayout.setVisibility(View.INVISIBLE);
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        toolbarTitle.setText(getString(R.string.article_listing_type_short_story_label));
        hitFilteredTopicsArticleListingApi(sortType);
        challengeListingRecycleAdapter = new ChallengeListingRecycleAdapter(this, this, pos, selectedName,
                selectedActiveUrl);
        recyclerView.setAdapter(challengeListingRecycleAdapter);
        challengeListingRecycleAdapter.setListData(articleListingResults);
        challengeListingRecycleAdapter.notifyDataSetChanged();

        pullToRefresh.setOnRefreshListener(() -> {
            nextPageNumber = 1;
            limit = 15;
            articleListingResults.clear();
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
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });

        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);
            addShortStoryCategories(chooseoptionradioButton);
        } catch (FileNotFoundException e) {
            Retrofit retr = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsApi = retr.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE,
                            response.body());
                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                        createTopicsData(res);
                        addShortStoryCategories(chooseoptionradioButton);
                    } catch (FileNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }

        chooseoptionradioButton.setOnCheckedChangeListener((radioGroup, i) -> {
            ssTopicsText = ssTopicsList.get(i).getDisplay_name();
            shortStoryCategoryId = ssTopicsList.get(i).getId();
        });
    }

    private void addShortStoryCategories(RadioGroup chooseoptionradioButton) {
        RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < ssTopicsList.size(); i++) {
            AppCompatRadioButton rbn = new AppCompatRadioButton(this);
            rbn.setId(i);
            rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
            rbn.setText(ssTopicsList.get(i).getDisplay_name());
            chooseoptionradioButton.addView(rbn, rprms);
            rbn.setPadding(10, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][] {
                                new int[] {-android.R.attr.state_enabled}, //disabled
                                new int[] {android.R.attr.state_enabled} //enabled
                        },
                        new int[] {
                                getResources().getColor(R.color.app_red),//// disabled
                                getResources().getColor(R.color.app_red) //enabled
                        });
                rbn.setButtonTintList(colorStateList);//set the color tint list
                // radio.invalidate(); //could not be necessary
            }
        }
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            for (int i = 0; i < responseData.getData().size(); i++) {
                if (AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        //DO NOT REMOVE below commented check -- showInMenu 0 from backend
                        // --might be used to show/hide in future
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getPublicVisibility())) {
                            ssTopicsList.add(responseData.getData().get(i).getChild().get(j));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsApi
                .getArticlesForCategory(selectedId, sortType, from, from + limit - 1, "0");

        filterCall.enqueue(articleListingResponseCallback);
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
                    processArticleListingResponse(responseData);
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

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleListingResults && !articleListingResults.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                articleListingResults = dataList;
                challengeListingRecycleAdapter.setListData(articleListingResults);
                challengeListingRecycleAdapter.notifyDataSetChanged();
            }
        } else {
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleListingResults = dataList;

            } else {
                articleListingResults.addAll(dataList);
            }
            challengeListingRecycleAdapter.setListData(articleListingResults);
            nextPageNumber = nextPageNumber + 1;
            challengeListingRecycleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                chooseLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                articleListingResults.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                articleListingResults.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;
            case R.id.start_writing:
                if (ssTopicsText != null) {
                    Intent intentt = new Intent(this, AddShortStoryActivity.class);
                    String challenge = "challenge";
                    intentt.putExtra("selectedrequest", challenge);
                    intentt.putExtra("challengeId", selectedId);
                    intentt.putExtra("challengeName", selectedName);
                    intentt.putExtra("Url", selectedActiveUrl);
                    intentt.putExtra("selectedCategory", ssTopicsText);
                    intentt.putExtra("shortStoryCategoryId", shortStoryCategoryId);
                    startActivity(intentt);
                } else {
                    Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.overlayView_choose_story_challenge:
                chooseLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view, final int position, String activeUrl) {
        switch (view.getId()) {
            case R.id.menuItem:
                chooseMenuOptionsItem(view, position);
                break;
            case R.id.storyImageView1:
                Intent intent = new Intent(this, ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleListingResults.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleListingResults.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleListingResults.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + parentName);
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
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
                        recommendUnrecommentArticleApi("0", articleListingResults.get(position).getId(),
                                articleListingResults.get(position).getUserId(),
                                articleListingResults.get(position).getUserName());
                    } else {
                        likeStatus = "1";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleApi("1", articleListingResults.get(position).getId(),
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
                getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC);
            }
            break;
            case R.id.authorNameTextView:
                if (userDynamoId.equals(articleListingResults.get(position).getUserId())) {
                    Intent pintent = new Intent(this, UserProfileActivity.class);
                    startActivity(pintent);
                } else {
                    Intent intentnn = new Intent(this, UserProfileActivity.class);
                    intentnn.putExtra(Constants.USER_ID, articleListingResults.get(position).getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, articleListingResults.get(position).getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;

            case R.id.submit_story_text:
                chooseLayout.setVisibility(View.VISIBLE);
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
        ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, getString(R.string.all_insta_share_clipboard_msg));
    }

    private void followApiCall(String authorId, int position) {
        this.position = position;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowee_id(authorId);
        if (articleListingResults.get(position).getIsfollowing().equals("1")) {
            Utils.pushGenericEvent(this, "CTA_Unfollow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followApi.unfollowUserInShortStoryListingV2(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            Utils.pushGenericEvent(this, "CTA_Follow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followApi.followUserInShortStoryListingV2(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<ResponseBody> unfollowUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {

                ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    articleListingResults.get(position).setIsfollowing("0");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, reason);
                }
            } catch (Exception e) {
                ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong at the server ");
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong at the server ");
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> followUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    articleListingResults.get(position).setIsfollowing("1");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else if (code == 200 && "failure".equals(status) && "Already following!".equals(reason)) {
                    articleListingResults.get(position).setIsfollowing("1");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, reason);
                }
            } catch (Exception e) {
                ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong at the server ");
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            ToastUtils.showToast(ShortStoryChallengeDetailActivity.this, "some thing went wrong at the server ");
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void recommendUnrecommentArticleApi(String status, String articleId, String authorId, String author) {
        Utils.pushLikeStoryEvent(this, "ChallengeDetailListingScreen", userDynamoId + "", articleId,
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
                        showToast(getString(R.string.server_went_wrong));
                        return;
                    }

                    try {
                        RecommendUnrecommendArticleResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                            if (likeStatus.equals("1")) {
                                if (!responseData.getData().isEmpty()) {
                                    articleListingResults.get(currentShortStoryPosition).setLikesCount(
                                            "" + (Integer.parseInt(
                                                    articleListingResults.get(currentShortStoryPosition)
                                                            .getLikesCount())
                                                    + 1));
                                }
                                articleListingResults.get(currentShortStoryPosition).setLiked(true);
                            } else {
                                if (!responseData.getData().isEmpty()) {
                                    articleListingResults.get(currentShortStoryPosition).setLikesCount(
                                            "" + (Integer.parseInt(
                                                    articleListingResults.get(currentShortStoryPosition)
                                                            .getLikesCount())
                                                    - 1));
                                }
                                articleListingResults.get(currentShortStoryPosition).setLiked(false);
                            }
                            challengeListingRecycleAdapter.notifyDataSetChanged();

                            showToast("" + responseData.getReason());
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        showToast(getString(R.string.server_went_wrong));

                    }
                }

                @Override
                public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
                    isRecommendRequestRunning = false;
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };


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

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        chooseLayout.setVisibility(View.INVISIBLE);
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(root, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions()).show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String s : PERMISSIONS_INIT) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(root, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                createBitmapForSharingStory();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(root, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("RestrictedApi")
    private void chooseMenuOptionsItem(View view, int position) {

        final androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(this, view);
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
                    addCollectionAndCollectionitemDialogFragment.show(getSupportFragmentManager(), "collectionAdd");
                    Utils.pushProfileEvents(this, "CTA_100WS_Add_To_Collection",
                            "ShortStoryChallengeDetailActivity", "Add to Collection", "-");
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
                Toast.makeText(this, getString(R.string.ss_story_link_copied), Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                relativeFrame.setVisibility(View.VISIBLE);
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                Bundle args = new Bundle();
                args.putString("postId", articleListingResults.get(position).getId());
                args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(args);
                reportContentDialogFragment.setCancelable(true);
                FragmentManager fm = getSupportFragmentManager();
                reportContentDialogFragment.show(fm, "report_dialog");
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(),
                view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
    }

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = recyclerView.getLayoutManager().findViewByPosition(position + 1)
                .findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = articleListingResults.get(position);
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
        shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
        //Bh**d**a facebook caches shareIntent. Need different name for all files
        String tempName = "" + System.currentTimeMillis();
        AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME + tempName);
        shareStory(tempName);
    }

    private void shareStory(String tempName) {
        Uri uri = Uri.parse("file://" + BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator
                + AppConstants.STORY_SHARE_IMAGE_NAME + tempName + ".jpg");
        switch (shareMedium) {
            case AppConstants.MEDIUM_FACEBOOK: {
                SharingUtils.shareViaFacebook(this, uri);
                Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                        userDynamoId + "", sharedStoryItem.getId(),
                        sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Facebook");
            }
            break;
            case AppConstants.MEDIUM_WHATSAPP: {
                if (AppUtils.shareImageWithWhatsApp(this, uri, getString(R.string.ss_follow_author,
                        sharedStoryItem.getUserName(),
                        AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                    Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Whatsapp");
                }
            }
            break;
            case AppConstants.MEDIUM_INSTAGRAM: {
                if (AppUtils.shareImageWithInstagram(this, uri)) {
                    Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Instagram");
                }
            }
            break;
            case AppConstants.MEDIUM_GENERIC: {
                if (AppUtils.shareGenericImageAndOrLink(this, uri, getString(R.string.ss_follow_author,
                        sharedStoryItem.getUserName(),
                        AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                    Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Generic");
                }
            }
            break;
            default:
                break;
        }
    }

    private void checkPermissionAndCreateShareableImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
}
