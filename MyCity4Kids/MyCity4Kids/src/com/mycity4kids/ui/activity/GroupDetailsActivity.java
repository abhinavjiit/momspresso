package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.GroupAboutRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupBlogsRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupsGenericPostRecyclerAdapter;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupDetailsActivity extends BaseActivity implements View.OnClickListener, GroupAboutRecyclerAdapter.RecyclerViewClickListener, GroupBlogsRecyclerAdapter.RecyclerViewClickListener,
        GroupsGenericPostRecyclerAdapter.RecyclerViewClickListener {

    private final static String[] sectionsKey = {"ABOUT", "DISCUSSION", "BLOGS", "PHOTOS", "VIDEOS", "TOP POSTS", "POLLS"};

    private int nextPageNumber = 1;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private String postType;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<GroupPostResult> postList;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private UserPostSettingResult currentPostPrefsForUser;
    private GroupResult selectedGroup;
    private GroupPostResult selectedPost;
    private int groupId;

    private Animation slideAnim, fadeAnim;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GroupAboutRecyclerAdapter groupAboutRecyclerAdapter;
    private GroupBlogsRecyclerAdapter groupBlogsRecyclerAdapter;
    private GroupsGenericPostRecyclerAdapter groupsGenericPostRecyclerAdapter;
    private TabLayout groupPostTabLayout;
    private RelativeLayout addPostContainer;
    private FloatingActionButton addPostFAB;
    private LinearLayout postContainer;
    private LinearLayout pollContainer;
    private ImageView closeImageView;
    private TextView noPostsTextView;
    private TextView groupNameTextView;
    private TextView toolbarTitle;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView;
    private ProgressBar progressBar;
    private ImageView groupSettingsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        groupPostTabLayout = (TabLayout) findViewById(R.id.groupPostTabLayout);
        addPostContainer = (RelativeLayout) findViewById(R.id.addPostContainer);
        addPostFAB = (FloatingActionButton) findViewById(R.id.addPostFAB);
        postContainer = (LinearLayout) findViewById(R.id.postContainer);
        pollContainer = (LinearLayout) findViewById(R.id.pollContainer);
        closeImageView = (ImageView) findViewById(R.id.closeImageView);
        noPostsTextView = (TextView) findViewById(R.id.noPostsTextView);
        groupNameTextView = (TextView) findViewById(R.id.groupNameTextView);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        groupSettingsImageView = (ImageView) findViewById(R.id.groupSettingsImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");
        groupId = getIntent().getIntExtra("groupId", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        addPostFAB.setOnClickListener(this);
        pollContainer.setOnClickListener(this);
        postContainer.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);
        groupSettingsImageView.setOnClickListener(this);

        String[] sections = {
                getString(R.string.groups_sections_about), getString(R.string.groups_sections_discussions), getString(R.string.groups_sections_blogs),
                getString(R.string.groups_sections_photos), getString(R.string.groups_sections_videos), getString(R.string.groups_sections_top_posts), getString(R.string.groups_sections_polls)
        };

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setUpTabLayout(sections);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        groupBlogsRecyclerAdapter = new GroupBlogsRecyclerAdapter(this, this);
        groupBlogsRecyclerAdapter.setData(articleDataModelsNew);

        postList = new ArrayList<>();
        groupsGenericPostRecyclerAdapter = new GroupsGenericPostRecyclerAdapter(this, this, selectedGroup);
        groupsGenericPostRecyclerAdapter.setData(postList);

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
                            getGroupPosts();
                        }
                    }
                }
            }
        });
        if (selectedGroup == null) {
            getGroupDetails();
        } else {
            toolbarTitle.setHint(getString(R.string.groups_search_in) + " " + selectedGroup.getTitle());
            groupNameTextView.setText(selectedGroup.getTitle());

            groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(this, this);
            groupAboutRecyclerAdapter.setData(selectedGroup);
            recyclerView.setAdapter(groupAboutRecyclerAdapter);

            getGroupPosts();
        }
    }

    private void getGroupDetails() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupDetailResponse> call = groupsAPI.getGroupById(groupId);
        call.enqueue(groupDetailsResponseCallback);
    }

    private Callback<GroupDetailResponse> groupDetailsResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupPostResponse = response.body();
                    selectedGroup = groupPostResponse.getData().getResult();
                    toolbarTitle.setText(getString(R.string.groups_search_in) + " " + selectedGroup.getTitle());
                    groupNameTextView.setText(selectedGroup.getTitle());

                    groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(GroupDetailsActivity.this, GroupDetailsActivity.this);
                    groupAboutRecyclerAdapter.setData(selectedGroup);
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);

                    getGroupPosts();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {

        }
    };

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupPostResponse> call = groupsAPI.getAllPostsForAGroup(selectedGroup.getId(), skip, limit);
        call.enqueue(groupPostResponseCallback);
    }

    private void getFilteredGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupPostResponse> call = groupsAPI.getAllFilteredPostsForAGroup(selectedGroup.getId(), skip, limit, postType);
        call.enqueue(groupPostResponseCallback);
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    processPostListingResponse(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPostListingResponse(GroupPostResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) response.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
//                noPostsTextView.setVisibility(View.VISIBLE);
//                postList = dataList;
//                groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
//                groupSummaryPostRecyclerAdapter.setData(postList);
//                groupSummaryPostRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
//            groupsGenericPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupsGenericPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupSettingsImageView: {
                Intent intent = new Intent(GroupDetailsActivity.this, GroupSettingsActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivity(intent);
            }
            break;
            case R.id.addPostFAB:
                addPostContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.postContainer: {
                Intent intent = new Intent(GroupDetailsActivity.this, AddTextOrMediaGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivity(intent);
            }
            break;
            case R.id.pollContainer: {
                Intent intent = new Intent(GroupDetailsActivity.this, AddPollGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivity(intent);
            }
            break;
            case R.id.closeImageView:
                if (addPostContainer.getVisibility() == View.VISIBLE) {
                    addPostContainer.setVisibility(View.GONE);
                }
                break;
            case R.id.savePostTextView:
                Log.d("savePostTextView", "" + selectedPost.getId());
                if (savePostTextView.getText().toString().equals("SAVE POST")) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                if (commentToggleTextView.getText().toString().equals("DISABLE COMMENTS")) {
                    updatePostCommentSettings(1);
                } else {
                    updatePostCommentSettings(0);
                }
                break;
            case R.id.notificationToggleTextView:
                Log.d("notifToggleTextView", "" + selectedPost.getId());
                if (notificationToggleTextView.getText().toString().equals("Disable Notification")) {
                    updateUserPostPreferences("enableNotif");
                } else {
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.reportPostTextView:
                Log.d("reportPostTextView", "" + selectedPost.getId());
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                groupPostReportDialogFragment.setArguments(_args);
                groupPostReportDialogFragment.setCancelable(true);
                groupPostReportDialogFragment.show(fm, "Choose video report option");
                reportPostTextView.setText("Unreport");
                break;
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void updatePostCommentSettings(int status) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateGroupPostRequest updateGroupPostRequest = new UpdateGroupPostRequest();
        updateGroupPostRequest.setGroupId(selectedGroup.getId());
        updateGroupPostRequest.setDisableComments(status);

        Call<GroupPostResponse> call = groupsAPI.disablePostComment(selectedPost.getId(), updateGroupPostRequest);
        call.enqueue(postUpdateResponseListener);
    }

    private Callback<GroupPostResponse> postUpdateResponseListener = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    if (groupPostResponse.getData().get(0).getResult().get(0).getDisableComments() == 1) {
                        commentToggleTextView.setText("ENABLE COMMENTS");
                    } else {
                        commentToggleTextView.setText("DISABLE COMMENTS");
                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void updateUserPostPreferences(String action) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateUserPostSettingsRequest request = new UpdateUserPostSettingsRequest();
        request.setPostId(selectedPost.getId());
        request.setIsAnno(selectedPost.getIsAnnon());
        request.setUserId(selectedPost.getUserId());
        Call<UserPostSettingResponse> call;
        if (currentPostPrefsForUser == null) {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(1);
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(0);
            }
            call = groupsAPI.createNewPostSettingsForUser(request);
        } else {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(0);
            }
            call = groupsAPI.updatePostSettingsForUser(currentPostPrefsForUser.getId(), request);
        }
        call.enqueue(updatePostSettingForUserResponseCallback);
    }

    private Callback<UserPostSettingResponse> updatePostSettingForUserResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                        notificationToggleTextView.setText("ENABLE NOTIFICATION");
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getIsBookmarked() == 1) {
                        savePostTextView.setText("UNSAVE POST");
                    } else {
                        savePostTextView.setText("SAVE POST");
                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {

        }
    };

    @Override
    public void onBackPressed() {
        if (addPostContainer.getVisibility() == View.VISIBLE) {
            addPostContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void setUpTabLayout(String[] sections) {
        for (int i = 0; i < sections.length; i++) {
            TabLayout.Tab tab = groupPostTabLayout.newTab();
            tab.setTag(sectionsKey[i]);
            groupPostTabLayout.addTab(tab.setText(sections[i]));
        }

        groupPostTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        AppUtils.changeTabsFont(this, groupPostTabLayout);

//        wrapTabIndicatorToTitle(groupPostTabLayout, 25, 25);
//        wrapTabIndicatorToTitle(guideTabLayout, 25, 25);

        groupPostTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupBlogsRecyclerAdapter);
                    hitFilteredTopicsArticleListingApi(0);
                } else if (AppConstants.GROUP_SECTION_PHOTOS.equalsIgnoreCase(tab.getTag().toString())) {
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_MEDIA_KEY;
                    getFilteredGroupPosts();
//                    Intent intent = new Intent(GroupDetailsActivity.this, GroupPostDetailActivity.class);
//                    startActivity(intent);
                } else if (AppConstants.GROUP_SECTION_VIDEOS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_TOP_POSTS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
//                    Intent intent = new Intent(GroupDetailsActivity.this, AddPollGroupPostActivity.class);
//                    intent.putExtra("groupItem", selectedGroup);
//                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_PHOTOS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_VIDEOS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_TOP_POSTS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                }
            }
        });
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getVernacularTrendingArticles(from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
//            swipe_refresh_layout.setRefreshing(false);
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
//            swipe_refresh_layout.setRefreshing(false);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
                groupBlogsRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
            groupBlogsRecyclerAdapter.notifyDataSetChanged();
            nextPageNumber = nextPageNumber + 1;
        }
    }


    @Override
    public void onRecyclerItemClick(View view, int position) {
        Intent intent = new Intent(GroupDetailsActivity.this, GroupsQuestionnaireActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGroupPostRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.userImageView:
            case R.id.usernameTextView: {
//                Intent intent = new Intent(GroupDetailsActivity.this, GroupDetailsActivity.class);
//                intent.putExtra("groupItem", selectedGroup);
//                startActivity(intent);
                break;
            }
            case R.id.postSettingImageView:
                selectedPost = postList.get(position);
                getCurrentPostSettingsStatus(selectedPost);
                if (selectedPost.getDisableComments() == 1) {
                    commentToggleTextView.setText("ENABLE COMMENTS");
                } else {
                    commentToggleTextView.setText("DISABLE COMMENTS");
                }
                postSettingsContainer.startAnimation(slideAnim);
                overlayView.startAnimation(fadeAnim);
                postSettingsContainerMain.setVisibility(View.VISIBLE);
                postSettingsContainer.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.VISIBLE);
                break;
            case R.id.postDataTextView:
            case R.id.postDateTextView: {
//                Intent intent = new Intent(GroupDetailsActivity.this, GroupPostDetailActivity.class);
//                intent.putExtra("groupItem", selectedGroup);
//                startActivity(intent);
                break;
            }
            case R.id.upvoteContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                break;
            case R.id.downvoteContainer:
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                break;
        }
    }

    private void markAsHelpfulOrUnhelpful(String markType, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postList.get(position).getGroupId());
        groupActionsRequest.setPostId(postList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsAPI.addAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionResponse> groupActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
//                    groupPostResult.setVoted(true);
//                    notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getCurrentPostSettingsStatus(GroupPostResult selectedPost) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsAPI.getPostSettingForUser(selectedPost.getId());
        call.enqueue(userPostSettingResponseCallback);
    }

    private Callback<UserPostSettingResponse> userPostSettingResponseCallback = new Callback<UserPostSettingResponse>() {
        @Override
        public void onResponse(Call<UserPostSettingResponse> call, retrofit2.Response<UserPostSettingResponse> response) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    UserPostSettingResponse userPostSettingResponse = response.body();
                    setPostCurrentPreferences(userPostSettingResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setPostCurrentPreferences(UserPostSettingResponse userPostSettingResponse) {

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupDetailsActivity.this).getDynamoId())) {
            commentToggleTextView.setVisibility(View.VISIBLE);
        } else {
            commentToggleTextView.setVisibility(View.GONE);
        }

        //No existing settings for this post for this user
        if (userPostSettingResponse.getData().get(0).getResult() == null || userPostSettingResponse.getData().get(0).getResult().size() == 0) {
            savePostTextView.setText("SAVE POST");
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
            currentPostPrefsForUser = null;
            return;
        }
        currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
        if (currentPostPrefsForUser.getIsBookmarked() == 1) {
            savePostTextView.setText("UNSAVE POST");
        } else {
            savePostTextView.setText("SAVE POST");
        }

        if (currentPostPrefsForUser.getNotificationOff() == 1) {
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
        } else {
            notificationToggleTextView.setText("DISABLE NOTIFICATION");
        }
    }
}