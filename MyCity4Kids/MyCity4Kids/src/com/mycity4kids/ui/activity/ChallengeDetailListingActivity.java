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
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
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
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.mycity4kids.ui.fragment.ReportContentDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.SharingUtils;
import com.mycity4kids.widget.StoryShareCardWidget;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ChallengeDetailListingActivity extends BaseActivity implements View.OnClickListener, ChallengeListingRecycleAdapter.RecyclerViewClickListener {

    private static final int REQUEST_INIT_PERMISSION = 2;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ChallengeListingRecycleAdapter challengeListingRecycleAdapter;
    private Toolbar mToolbar;
    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private int pos;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> activeUrl = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private String challenge = "challenge";
    private String parentName, parentId;
    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> mDatalist;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private boolean showGuide = false;
    private String userDynamoId;
    private View shareSSView;
    private TextView titleTextView, bodyTextView, authorTextView;
    private ShortStoryAPI shortStoryAPI;
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private int currentShortStoryPosition;
    private RelativeLayout chooseLayout;
    int count = -1;
    private TextView toolbartitle;
    private RelativeLayout relative_frame;
    private FrameLayout frameLayout_report;
    private View overlayLayout;
    private String ssTopicsText;
    private TextView startWriting;
    private String challengeComingFrom;
    private RelativeLayout root;
    private SwipeRefreshLayout pullToRefresh;
    private String shareMedium;
    private int sharedStoryPosition;
    int position;
    private StoryShareCardWidget storyShareCardWidget;
    private ImageView shareStoryImageView;
    private ArticleListingResult sharedStoryItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challnege_detail_listing);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        startWriting = (TextView) findViewById(R.id.start_writing);
        relative_frame = (RelativeLayout) findViewById(R.id.framelayout_relative);
        chooseLayout = (RelativeLayout) findViewById(R.id.choose_layout);
        frameLayout_report = (FrameLayout) findViewById(R.id.framelayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_listing);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        toolbartitle = (TextView) findViewById(R.id.toolbarTitleTextView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        guideOverlay = (RelativeLayout) findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        shareSSView = findViewById(R.id.shareSSView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        bodyTextView = (TextView) findViewById(R.id.bodyTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        pullToRefresh = findViewById(R.id.pullToRefresh);

        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        RadioGroup chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);
        RadioGroup.LayoutParams rprms;

        ssTopicsList = new ArrayList<>();
        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                } else {
                    fabMenu.expand();
                }
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
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

        mDatalist = new ArrayList<>();
        Intent intent = getIntent();
        pos = intent.getIntExtra("position", 0);
        challengeId = intent.getStringArrayListExtra("challenge");
        Display_Name = intent.getStringArrayListExtra("Display_Name");
        activeUrl = intent.getStringArrayListExtra("StringUrl");
        parentId = intent.getStringExtra("parentId");
        parentName = intent.getStringExtra("topics");
        challengeComingFrom = intent.getStringExtra("selectedrequest");
        if (challengeComingFrom == null) {
            challengeComingFrom = "challenge";
        }
        if ("FromDeepLink".equals(challengeComingFrom)) {
            challengeId = intent.getStringArrayListExtra("challenge");
            Display_Name = intent.getStringArrayListExtra("Display_Name");
            activeUrl = intent.getStringArrayListExtra("StringUrl");
            chooseLayout.setVisibility(View.VISIBLE);
        } else {
            chooseLayout.setVisibility(View.INVISIBLE);
        }
        if (challengeId != null && challengeId.size() != 0) {
            selectedId = challengeId.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeUrl != null && activeUrl.size() != 0) {
            selectedActiveUrl = activeUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (Display_Name != null && Display_Name.size() != 0) {
            selected_Name = Display_Name.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        toolbartitle.setText(getString(R.string.article_listing_type_short_story_label));
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        shortStoryAPI = retro.create(ShortStoryAPI.class);
        hitFilteredTopicsArticleListingApi(sortType);
        challengeListingRecycleAdapter = new ChallengeListingRecycleAdapter(this, this, pos, selected_Name, selectedActiveUrl);
        recyclerView.setAdapter(challengeListingRecycleAdapter);
        challengeListingRecycleAdapter.setListData(mDatalist);
        challengeListingRecycleAdapter.notifyDataSetChanged();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDatalist.clear();
                hitFilteredTopicsArticleListingApi(sortType);
                pullToRefresh.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });


        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);
            addShortStoryCategories(chooseoptionradioButton);
        } catch (FileNotFoundException e) {
            Retrofit retr = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retr.create(TopicsCategoryAPI.class);

            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();

            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());

                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                        createTopicsData(res);
                        addShortStoryCategories(chooseoptionradioButton);
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        }


        chooseoptionradioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ssTopicsText = ssTopicsList.get(i).getDisplay_name();
            }
        });

    }

    private void addShortStoryCategories(RadioGroup chooseoptionradioButton) {
        RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < ssTopicsList.size(); i++) {
            AppCompatRadioButton rbn = new AppCompatRadioButton(this);
            rbn.setId(i);
            rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
            rbn.setText(ssTopicsList.get(i).getDisplay_name());
            chooseoptionradioButton.addView(rbn, rprms);
            rbn.setPadding(10, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{
                                getResources().getColor(R.color.app_red)//// disabled
                                , getResources().getColor(R.color.app_red) //enabled
                        }
                );
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
                        //DO NOT REMOVE below commented check -- showInMenu 0 from backend --might be used to show/hide in future
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getPublicVisibility())) {
                            ssTopicsList.add(responseData.getData().get(i).getChild().get(j));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                chooseLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.recentSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;

            case R.id.start_writing:
                if (ssTopicsText != null) {
                    Intent intentt = new Intent(this, AddShortStoryActivity.class);
                    intentt.putExtra("selectedrequest", challenge);
                    intentt.putExtra("challengeId", selectedId);
                    intentt.putExtra("challengeName", selected_Name);
                    intentt.putExtra("Url", selectedActiveUrl);
                    intentt.putExtra("selectedCategory", ssTopicsText);
                    startActivity(intentt);
                    //    chooseLayout.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.overlayView_choose_story_challenge:
                chooseLayout.setVisibility(View.GONE);
        }
    }


    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedId, sortType, from, from + limit - 1, "0");

        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != mDatalist && !mDatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                mDatalist = dataList;
                challengeListingRecycleAdapter.setListData(mDatalist);
                challengeListingRecycleAdapter.notifyDataSetChanged();
            }
        } else {
//            noBlogsTextView.setVisibility(View.GONE);
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;

            } else {
                mDatalist.addAll(dataList);
            }
            challengeListingRecycleAdapter.setListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            challengeListingRecycleAdapter.notifyDataSetChanged();
            if (nextPageNumber == 2) {
                //startTracking();
            }
        }

    }


    @Override
    public void
    onClick(View view, final int position, String ActiveUrl) {
        switch (view.getId()) {
            case R.id.menuItem:
                chooseMenuOptionsItem(view, position);
                break;
            case R.id.storyImageView1:
                Intent intent = new Intent(this, ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + parentName);
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", mDatalist);
                intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (mDatalist.get(position).isLiked()) {
                        likeStatus = "0";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleAPI("0", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                    } else {
                        likeStatus = "1";
                        currentShortStoryPosition = position;
                        recommendUnrecommentArticleAPI("1", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
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
                getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM);
            }
            break;
            case R.id.genericShareImageView: {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment = new AddCollectionAndCollectionItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("articleId", mDatalist.get(position).getId());
                    bundle.putString("type", AppConstants.SHORT_STORY_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    addCollectionAndCollectionitemDialogFragment.show(getSupportFragmentManager(), "collectionAdd");
                    Utils.pushProfileEvents(this, "CTA_100WS_Add_To_Collection",
                            "ChallengeDetailListingActivity", "Add to Collection", "-");
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
            break;
            case R.id.authorNameTextView:
                if (userDynamoId.equals(mDatalist.get(position).getUserId())) {
                    Intent pIntent = new Intent(this, UserProfileActivity.class);
                    startActivity(pIntent);
                } else {
                    Intent intentnn = new Intent(this, UserProfileActivity.class);
                    intentnn.putExtra(Constants.USER_ID, mDatalist.get(position).getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;

            case R.id.submit_story_text:
                chooseLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.followAuthorTextView:
                followAPICall(mDatalist.get(position).getUserId(), position);

        }
    }

    private void followAPICall(String authorId, int position) {
        this.position = position;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followAPI = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest request = new FollowUnfollowUserRequest();
        request.setFollowerId(authorId);
        if (mDatalist.get(position).getIsfollowing().equals("1")) {
            Utils.pushGenericEvent(this, "CTA_Unfollow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followAPI.unfollowUserInShortStoryListing(request);
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback);
        } else {
            Utils.pushGenericEvent(this, "CTA_Follow_100WS_Detail", userDynamoId, "TopicsShortStoryTabFragment");
            Call<ResponseBody> followUnfollowUserResponseCall = followAPI.followUserInShortStoryListing(request);
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback);
        }
    }

    private Callback<ResponseBody> unfollowUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {

                ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                String reason = jObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    mDatalist.get(position).setIsfollowing("0");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(ChallengeDetailListingActivity.this, reason);
                }
            } catch (Exception e) {
                ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong at the server ");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong at the server ");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> followUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong ");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                String reason = jObject.getString("reason");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    mDatalist.get(position).setIsfollowing("1");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else if (code == 200 && "failure".equals(status) && "Already following!".equals(reason)) {
                    mDatalist.get(position).setIsfollowing("1");
                    challengeListingRecycleAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(ChallengeDetailListingActivity.this, reason);
                }
            } catch (Exception e) {
                ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong at the server ");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            ToastUtils.showToast(ChallengeDetailListingActivity.this, "some thing went wrong at the server ");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void createBitmapForSharingStory(int position) {
        switch (position % 6) {
            case 0:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_1, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
            case 1:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_2, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
            case 2:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_3, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
            case 3:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_4, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
            case 4:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_5, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
            case 5:
                AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_6, mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                break;
        }
    }

    private void recommendUnrecommentArticleAPI(String status, String articleId, String authorId, String author) {
        Utils.pushLikeStoryEvent(this, "ChallengeDetailListingScreen", userDynamoId + "", articleId, authorId + "~" + author);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

        isRecommendRequestRunning = true;
        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(likeStatus);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            isRecommendRequestRunning = false;
            if (null == response.body()) {
              /*  if (!isAdded()) {
                    return;
                }*/
                showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (likeStatus.equals("1")) {
                        if (!responseData.getData().isEmpty()) {
                            mDatalist.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(mDatalist.get(currentShortStoryPosition).getLikesCount()) + 1));
                        }
                        mDatalist.get(currentShortStoryPosition).setLiked(true);
                    } else {
                        if (!responseData.getData().isEmpty()) {
                            mDatalist.get(currentShortStoryPosition).setLikesCount("" + (Integer.parseInt(mDatalist.get(currentShortStoryPosition).getLikesCount()) - 1));
                        }
                        mDatalist.get(currentShortStoryPosition).setLiked(false);
                    }
                    challengeListingRecycleAdapter.notifyDataSetChanged();

                    showToast("" + responseData.getReason());
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.server_went_wrong));

            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            isRecommendRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


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
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
        //chooseLayout.setVisibility(View.INVISIBLE);
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
                createBitmapForSharingStory(sharedStoryPosition);
                if (AppConstants.MEDIUM_WHATSAPP.equals(shareMedium)) {
                    AppUtils.shareStoryWithWhatsApp(this, mDatalist.get(sharedStoryPosition).getUserType(), mDatalist.get(sharedStoryPosition).getBlogPageSlug(),
                            mDatalist.get(sharedStoryPosition).getTitleSlug(), "ChallengeDetailListingScreen", userDynamoId, mDatalist.get(sharedStoryPosition).getId(),
                            mDatalist.get(sharedStoryPosition).getUserId(), mDatalist.get(sharedStoryPosition).getUserName());
                } else if (AppConstants.MEDIUM_INSTAGRAM.equals(shareMedium)) {
                    AppUtils.shareStoryWithInstagram(this, "ChallengeDetailListingScreen", userDynamoId, mDatalist.get(sharedStoryPosition).getId(),
                            mDatalist.get(sharedStoryPosition).getUserId(), mDatalist.get(sharedStoryPosition).getUserName());
                }
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
            if (item.getItemId() == R.id.shareShortStory) {
                getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC);
                return true;
            } else if (item.getItemId() == R.id.bookmarkShortStory) {
                return true;
            } else if (item.getItemId() == R.id.reportContentShortStory) {
                relative_frame.setVisibility(View.VISIBLE);
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                _args.putString("postId", mDatalist.get(position).getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.show(fm, "report_dialog");
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
    }

    private void getSharableViewForPosition(int position, String medium) {
        storyShareCardWidget = recyclerView.getLayoutManager().findViewByPosition(position + 1).findViewById(R.id.storyShareCardWidget);
        shareStoryImageView = storyShareCardWidget.findViewById(R.id.storyImageView);
        shareMedium = medium;
        sharedStoryItem = mDatalist.get(position);
        checkPermissionAndCreateShareableImage();
    }

    private void createBitmapForSharingStory() {
        Bitmap bitmap1 = ((BitmapDrawable) shareStoryImageView.getDrawable()).getBitmap();
        shareStoryImageView.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap1, AppUtils.dpTopx(4.0f)));
        AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME);
        shareStory();
    }

    private void shareStory() {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg");
        switch (shareMedium) {
            case AppConstants.MEDIUM_FACEBOOK: {
                SharingUtils.shareViaFacebook(this);
                Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                        userDynamoId + "", sharedStoryItem.getId(),
                        sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Facebook");
            }
            break;
            case AppConstants.MEDIUM_WHATSAPP: {
                if (AppUtils.shareImageWithWhatsApp(this, uri, getString(R.string.ss_follow_author,
                        sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
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
                        sharedStoryItem.getUserName(), AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.getUserId()))) {
                    Utils.pushShareStoryEvent(this, "ChallengeDetailListingScreen",
                            userDynamoId + "", sharedStoryItem.getId(),
                            sharedStoryItem.getUserId() + "~" + sharedStoryItem.getUserName(), "Generic");
                }
            }
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
}
