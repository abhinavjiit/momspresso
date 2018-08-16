package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupDetailsActivity extends BaseActivity implements View.OnClickListener, GroupAboutRecyclerAdapter.RecyclerViewClickListener, GroupBlogsRecyclerAdapter.RecyclerViewClickListener,
        GroupsGenericPostRecyclerAdapter.RecyclerViewClickListener {

    private static final int EDIT_POST_REQUEST_CODE = 1010;
    private ArrayList<GroupsCategoryMappingResult> groupMappedCategories;
    private final static String[] sectionsKey = {"ABOUT", "DISCUSSION", "BLOGS", "PHOTOS", "POLLS"};

    private int categoryIndex = 0;
    private int nextPageNumber = 1;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private String postType;
    private boolean isRequestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<GroupPostResult> postList;
    private ArrayList<GroupPostResult> postSearchResult;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private UserPostSettingResult currentPostPrefsForUser;
    private GroupResult selectedGroup;
    private GroupPostResult selectedPost;
    private String memberType;
    private int groupId;
    private String commaSepCategoryList = "";

    private Handler handler = new Handler();

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
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView, editPostTextView, deletePostTextView, blockUserTextView, pinPostTextView;
    private ProgressBar progressBar;
    private ImageView groupSettingsImageView;
    private TextView memberCountTextView;
    private ImageView groupImageView;
    private ImageView clearSearchImageView;
    private ImageView shareGroupImageView;
    private String source;

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
        clearSearchImageView = (ImageView) findViewById(R.id.clearSearchImageView);
        groupSettingsImageView = (ImageView) findViewById(R.id.groupSettingsImageView);
        groupImageView = (ImageView) findViewById(R.id.groupImageView);
        shareGroupImageView = (ImageView) findViewById(R.id.shareGroupImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        deletePostTextView = (TextView) findViewById(R.id.deletePostTextView);
        editPostTextView = (TextView) findViewById(R.id.editPostTextView);
        blockUserTextView = (TextView) findViewById(R.id.blockUserTextView);
        pinPostTextView = (TextView) findViewById(R.id.pinPostTextView);
        memberCountTextView = (TextView) findViewById(R.id.memberCountTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");
        groupId = getIntent().getIntExtra("groupId", 0);
        memberType = getIntent().getStringExtra(AppConstants.GROUP_MEMBER_TYPE);
        source = getIntent().getStringExtra("source");

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
        editPostTextView.setOnClickListener(this);
        deletePostTextView.setOnClickListener(this);
        blockUserTextView.setOnClickListener(this);
        pinPostTextView.setOnClickListener(this);
        clearSearchImageView.setOnClickListener(this);
        shareGroupImageView.setOnClickListener(this);
//        blockedCloseImageView.setOnClickListener(this);

        String[] sections = {
                getString(R.string.groups_sections_about), getString(R.string.groups_sections_discussions), getString(R.string.onboarding_desc_array_tutorial_1_blogs),
                getString(R.string.groups_sections_photos), getString(R.string.groups_sections_polls)
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
        postSearchResult = new ArrayList<>();
        groupsGenericPostRecyclerAdapter = new GroupsGenericPostRecyclerAdapter(this, this, selectedGroup, memberType);
        groupsGenericPostRecyclerAdapter.setData(postList);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isRequestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isRequestRunning = true;
                            if (recyclerView.getAdapter() instanceof GroupsGenericPostRecyclerAdapter) {
                                getGroupPosts();
                            } else {
                                hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                            }
                        }
                    }
                }
            }
        });

        toolbarTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
//                tab.select();
                if (TextUtils.isEmpty(editable)) {
                    clearSearchImageView.setVisibility(View.GONE);
                    skip = 0;
                    limit = 10;
                    isRequestRunning = false;
                    isLastPageReached = false;
                    postList.clear();
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                    getGroupPosts();
                } else {
                    clearSearchImageView.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    skip = 0;
                                    limit = 10;
                                    isRequestRunning = false;
                                    isLastPageReached = false;
                                    postList.clear();
                                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                                    requestSearch();
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
        getGroupDetails();
    }

    private void requestSearch() {
        if (StringUtils.isNullOrEmpty(toolbarTitle.getText().toString())) {
//            showToast("Please enter a valid search parameter");
        } else {
            Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
            GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
            Call<GroupPostResponse> call = groupsAPI.searchWithinGroup(toolbarTitle.getText().toString(), "post", 1, groupId, skip, limit);
            call.enqueue(searchResultResponseCallback);
        }
    }

    private Callback<GroupPostResponse> searchResultResponseCallback = new Callback<GroupPostResponse>() {
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
                    processSearchResultListing(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                    toolbarTitle.setHint(getString(R.string.groups_search_in));
                    memberCountTextView.setText(selectedGroup.getMemberCount() + " " + getString(R.string.groups_member_label));
                    groupNameTextView.setText(selectedGroup.getTitle());

                    Picasso.with(GroupDetailsActivity.this).load(selectedGroup.getHeaderImage())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(groupImageView);

                    groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(GroupDetailsActivity.this, GroupDetailsActivity.this);
                    groupAboutRecyclerAdapter.setData(selectedGroup);
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        if (selectedGroup != null) {
            Call<GroupPostResponse> call = groupsAPI.getAllPostsForAGroup(selectedGroup.getId(), skip, limit);
            call.enqueue(groupPostResponseCallback);
        }
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
            isRequestRunning = false;
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
            isRequestRunning = false;
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
            }
        } else {
            formatPostData(dataList);
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            groupsGenericPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void processSearchResultListing(GroupPostResponse postSearchResponse) {
        totalPostCount = postSearchResponse.getTotal();
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) postSearchResponse.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results
            }
        } else {
            formatPostData(dataList);
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            groupsGenericPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void formatPostData(ArrayList<GroupPostResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            if (dataList.get(j).getCounts() != null) {
                for (int i = 0; i < dataList.get(j).getCounts().size(); i++) {
                    switch (dataList.get(j).getCounts().get(i).getName()) {
                        case "helpfullCount":
                            dataList.get(j).setHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "notHelpfullCount":
                            dataList.get(j).setNotHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "responseCount":
                            dataList.get(j).setResponseCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "votesCount": {
                            for (int k = 0; k < dataList.get(j).getCounts().get(i).getCounts().size(); k++) {
                                dataList.get(j).setTotalVotesCount(dataList.get(j).getTotalVotesCount() + dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                switch (dataList.get(j).getCounts().get(i).getCounts().get(k).getName()) {
                                    case "option1":
                                        dataList.get(j).setOption1VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option2":
                                        dataList.get(j).setOption2VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option3":
                                        dataList.get(j).setOption3VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option4":
                                        dataList.get(j).setOption4VoteCount(dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.blockedCloseImageView: {
//                onBackPressed();
//            }
//            break;
            case R.id.shareGroupImageView: {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppConstants.GROUPS_BASE_SHARE_URL + selectedGroup.getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            }
            break;
            case R.id.clearSearchImageView: {
                toolbarTitle.setText("");
            }
            break;
            case R.id.groupSettingsImageView: {
                Intent intent = new Intent(GroupDetailsActivity.this, GroupSettingsActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                intent.putExtra("memberType", memberType);
                startActivity(intent);
            }
            break;
            case R.id.addPostFAB:
                addPostContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.postContainer: {
                Intent intent = new Intent(GroupDetailsActivity.this, AddTextOrMediaGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent, 1111);
            }
            break;
            case R.id.pollContainer: {
                Intent intent = new Intent(GroupDetailsActivity.this, AddPollGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent, 1111);
            }
            break;
            case R.id.closeImageView:
                if (addPostContainer.getVisibility() == View.VISIBLE) {
                    addPostContainer.setVisibility(View.GONE);
                }
                break;
            case R.id.savePostTextView:
                Log.d("savePostTextView", "" + selectedPost.getId());
                if (savePostTextView.getText().toString().equals(getString(R.string.groups_save_post))) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                if (commentToggleTextView.getText().toString().equals(getString(R.string.groups_disable_comment))) {
                    updatePostCommentSettings(1);
                } else {
                    updatePostCommentSettings(0);
                }
                break;
            case R.id.notificationToggleTextView:
                Log.d("notifToggleTextView", "" + selectedPost.getId());
                if (notificationToggleTextView.getText().toString().equals("DISABLE NOTIFICATION")) {
                    updateUserPostPreferences("enableNotif");
                } else {
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.editPostTextView:
                Log.d("editPostTextView", "" + selectedPost.getId());
                openEditPostOption();
                break;
            case R.id.deletePostTextView:
                Log.d("deletePostTextView", "" + selectedPost.getId());
                updateAdminLevelPostPrefs("markInactive");
                break;
            case R.id.blockUserTextView:
                Log.d("blockUserTextView", "" + selectedPost.getId());
                updateAdminLevelPostPrefs("blockUser");
                break;
            case R.id.pinPostTextView:
                Log.d("pinPostTextView", "" + selectedPost.getId());
                if (pinPostTextView.getText().toString().equals(getString(R.string.groups_pin_post))) {
                    updateAdminLevelPostPrefs("pinPost");
                } else {
                    updateAdminLevelPostPrefs("unpinPost");
                }
                break;
            case R.id.reportPostTextView:
                Log.d("reportPostTextView", "" + selectedPost.getId());
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putInt("groupId", groupId);
                _args.putInt("postId", selectedPost.getId());
                _args.putString("type", AppConstants.GROUP_REPORT_TYPE_POST);
                groupPostReportDialogFragment.setArguments(_args);
                groupPostReportDialogFragment.setCancelable(true);
                groupPostReportDialogFragment.show(fm, "Choose video report option");
//                reportPostTextView.setText("UNREPORT");
                break;
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void openEditPostOption() {
        Intent intent = new Intent(this, GroupsEditPostActivity.class);
        intent.putExtra("postData", selectedPost);
        startActivityForResult(intent, EDIT_POST_REQUEST_CODE);
    }

    private void updateAdminLevelPostPrefs(String actionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdatePostSettingsRequest request = new UpdatePostSettingsRequest();
        if ("pinPost".equals(actionType)) {
            request.setIsPinned(1);
            request.setIsActive(1);
        } else if ("unpinPost".equals(actionType)) {
            request.setIsPinned(0);
            request.setIsActive(1);
        } else if ("blockUser".equals(actionType)) {
            getPostingUsersMembershipDetails(selectedPost.getGroupId(), selectedPost.getUserId());
            return;
        } else if ("markInactive".equals(actionType)) {
            request.setIsActive(0);
            request.setIsPinned(0);
        }

        Call<GroupPostResponse> call = groupsAPI.updatePost(selectedPost.getId(), request);
        call.enqueue(updateAdminLvlPostSettingResponseCallback);
    }

    private void getPostingUsersMembershipDetails(int groupId, String postsUserId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsAPI.getUsersMembershipDetailsForGroup(groupId, postsUserId);
        call.enqueue(getMembershipDetailsReponseCallback);
    }

    private Callback<GroupsMembershipResponse> getMembershipDetailsReponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                    GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                    UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
                    updateGroupMembershipRequest.setUserId(selectedPost.getUserId());
                    updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                    Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(selectedPost.getId(), updateGroupMembershipRequest);
                    call1.enqueue(updateGroupMembershipResponseCallback);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupsMembershipResponse> updateGroupMembershipResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsMembershipResponse groupsMembershipResponse = response.body();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupPostResponse> updateAdminLvlPostSettingResponseCallback = new Callback<GroupPostResponse>() {
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
                    GroupPostResponse updatePostResponse = response.body();
//                    if (updatePostResponse.getData().get(0).getResult().get(0).getIsPinned() == 1) {
//                        pinPostTextView.setText(getString(R.string.groups_unpin_post));
//                    } else {
//                        pinPostTextView.setText(getString(R.string.groups_pin_post));
//                    }
                    postSettingsContainerMain.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                    postSettingsContainer.setVisibility(View.GONE);
                    clearSearchImageView.setVisibility(View.GONE);
                    skip = 0;
                    limit = 10;
                    isRequestRunning = false;
                    isLastPageReached = false;
                    postList.clear();
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                        commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                        selectedPost.setDisableComments(1);
                    } else {
                        commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                        selectedPost.setDisableComments(0);
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
        request.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());

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
            Call<ResponseBody> call = groupsAPI.createNewPostSettingsForUser(request);
            call.enqueue(createPostSettingForUserResponseCallback);
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
            Call<UserPostSettingResponse> call = groupsAPI.updatePostSettingsForUser(currentPostPrefsForUser.getId(), request);
            call.enqueue(updatePostSettingForUserResponseCallback);
        }

    }

    private Callback<ResponseBody> createPostSettingForUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    currentPostPrefsForUser = new UserPostSettingResult();
                    currentPostPrefsForUser.setId(jObject.getJSONObject("data").getJSONObject("result").getInt("id"));
                    if (jObject.getJSONObject("data").getJSONObject("result").getBoolean("notificationOff")) {
                        notificationToggleTextView.setText("ENABLE NOTIFICATION");
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (jObject.getJSONObject("data").getJSONObject("result").getBoolean("isBookmarked")) {
                        savePostTextView.setText(getString(R.string.groups_remove_post));
                    } else {
                        savePostTextView.setText(getString(R.string.groups_save_post));
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
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

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
                    currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                        notificationToggleTextView.setText("ENABLE NOTIFICATION");
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (userPostSettingResponse.getData().get(0).getResult().get(0).getIsBookmarked() == 1) {
                        savePostTextView.setText(getString(R.string.groups_remove_post));
                    } else {
                        savePostTextView.setText(getString(R.string.groups_save_post));
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
            if ("questionnaire".equals(source)) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
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

        groupPostTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    getGroupPosts();
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    nextPageNumber = 1;
                    recyclerView.setAdapter(groupBlogsRecyclerAdapter);
                    if (StringUtils.isNullOrEmpty(commaSepCategoryList)) {
                        getCategoriesTaggedWithGroups();
                    } else {
                        hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                    }
                } else if (AppConstants.GROUP_SECTION_PHOTOS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_MEDIA_KEY;
                    getFilteredGroupPosts();
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    getGroupPosts();
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    nextPageNumber = 1;
                    recyclerView.setAdapter(groupBlogsRecyclerAdapter);
                    if (StringUtils.isNullOrEmpty(commaSepCategoryList)) {
                        getCategoriesTaggedWithGroups();
                    } else {
                        hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                    }
                } else if (AppConstants.GROUP_SECTION_PHOTOS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_MEDIA_KEY;
                    getFilteredGroupPosts();
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
                }
            }
        });
    }

    private void getCategoriesTaggedWithGroups() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsCategoryMappingResponse> call = groupsAPI.getGroupCategories(groupId);
        call.enqueue(groupsCategoryResponseCallback);
    }

    private Callback<GroupsCategoryMappingResponse> groupsCategoryResponseCallback = new Callback<GroupsCategoryMappingResponse>() {
        @Override
        public void onResponse(Call<GroupsCategoryMappingResponse> call, retrofit2.Response<GroupsCategoryMappingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsCategoryMappingResponse groupsCategoryMappingResponse = response.body();
                    groupMappedCategories = (ArrayList<GroupsCategoryMappingResult>) groupsCategoryMappingResponse.getData().getResult();
                    if (groupMappedCategories != null && !groupMappedCategories.isEmpty()) {
                        hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsCategoryMappingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void hitFilteredTopicsArticleListingApi(String categoryId) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(categoryId, 1, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
//        Call<ArticleListingResponse> filterCall = topicsAPI.getVernacularTrendingArticles(from, from + limit - 1, SharedPrefUtils.getLanguageFilters(this));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isRequestRunning = false;
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
            isRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
                if (categoryIndex < groupMappedCategories.size() - 1) {
                    nextPageNumber = 1;
                    categoryIndex++;
                } else {
                    isLastPageReached = true;
                }
            } else {
                if (categoryIndex < groupMappedCategories.size() - 1) {
                    nextPageNumber = 1;
                    categoryIndex++;
                    hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                } else {
                    articleDataModelsNew.addAll(dataList);
                    groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
                    groupBlogsRecyclerAdapter.notifyDataSetChanged();
                }
            }
        } else {
            articleDataModelsNew.addAll(dataList);
            groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
            groupBlogsRecyclerAdapter.notifyDataSetChanged();
            nextPageNumber = nextPageNumber + 1;
        }
    }


    @Override
    public void onRecyclerItemClick(View view, int position) {
        Intent intent = new Intent(this, ArticleDetailsContainerActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
        intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
        intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
        intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "GroupDetailsArticleListing");
        intent.putExtra(Constants.FROM_SCREEN, "GroupDetailActivity");
        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
        intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
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
                getCurrentUserPostSettingsStatus(selectedPost);
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                        || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
                    getAdminPostSettingsStatus(selectedPost);
                }
                if (selectedPost.getDisableComments() == 1) {
                    commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                } else {
                    commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                }
//                postSettingsContainer.startAnimation(slideAnim);
//                overlayView.startAnimation(fadeAnim);
//                postSettingsContainerMain.setVisibility(View.VISIBLE);
//                postSettingsContainer.setVisibility(View.VISIBLE);
//                overlayView.setVisibility(View.VISIBLE);
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
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;
            case R.id.shareTextView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppConstants.GROUPS_BASE_SHARE_URL + postList.get(position).getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
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
                    if (response.code() == 400) {
                        try {
                            int patchActionId = 0;
                            String patchActionType = null;

                            String errorBody = new String(response.errorBody().bytes());
                            JSONObject jObject = new JSONObject(errorBody);
                            JSONArray dataArray = jObject.optJSONArray("data");
                            if (dataArray.getJSONObject(0).get("type").equals(dataArray.getJSONObject(1).get("type"))) {
                                //Same Action Event
                                if ("0".equals(dataArray.getJSONObject(0).get("type"))) {
                                    showToast("already marked unhelpful");
                                } else {
                                    showToast("already marked helpful");
                                }
                            } else {
                                if (dataArray.getJSONObject(0).has("id") && !dataArray.getJSONObject(0).isNull("id")) {
                                    patchActionId = dataArray.getJSONObject(0).getInt("id");
                                    patchActionType = dataArray.getJSONObject(1).getString("type");
                                } else {
                                    patchActionType = dataArray.getJSONObject(0).getString("type");
                                    patchActionId = dataArray.getJSONObject(1).getInt("id");
                                }
                                sendUpvoteDownvotePatchRequest(patchActionId, patchActionType);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                }
                            }
                        }
                    }
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
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

    private void sendUpvoteDownvotePatchRequest(int patchActionId, String patchActionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        GroupActionsPatchRequest groupActionsRequest = new GroupActionsPatchRequest();
        groupActionsRequest.setType(patchActionType);

        Call<GroupsActionResponse> call = groupsAPI.patchAction(patchActionId, groupActionsRequest);
        call.enqueue(patchActionResponseCallback);
    }

    private Callback<GroupsActionResponse> patchActionResponseCallback = new Callback<GroupsActionResponse>() {
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
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0).getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() - 1);
                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() - 1);
                                }
                            }
                        }
                    }
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
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

        }
    };

    private void getCurrentUserPostSettingsStatus(GroupPostResult selectedPost) {
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

                    postSettingsContainer.startAnimation(slideAnim);
                    overlayView.startAnimation(fadeAnim);
                    postSettingsContainerMain.setVisibility(View.VISIBLE);
                    postSettingsContainer.setVisibility(View.VISIBLE);
                    overlayView.setVisibility(View.VISIBLE);
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

    private void getAdminPostSettingsStatus(GroupPostResult selectedPost) {
//        progressBar.setVisibility(View.VISIBLE);
        pinPostTextView.setVisibility(View.GONE);
        blockUserTextView.setVisibility(View.GONE);
        deletePostTextView.setVisibility(View.GONE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsAPI.getSinglePost(selectedPost.getId());
        call.enqueue(postDetailsResponseCallback);
    }

    private Callback<GroupPostResponse> postDetailsResponseCallback = new Callback<GroupPostResponse>() {
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
                    setAdminPostPreferences(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setPostCurrentPreferences(UserPostSettingResponse userPostSettingResponse) {

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupDetailsActivity.this).getDynamoId())
                || AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            commentToggleTextView.setVisibility(View.VISIBLE);
        } else {
            commentToggleTextView.setVisibility(View.GONE);
        }

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupDetailsActivity.this).getDynamoId())) {
            editPostTextView.setVisibility(View.VISIBLE);
        } else {
            editPostTextView.setVisibility(View.GONE);
        }

        //No existing settings for this post for this user
        if (userPostSettingResponse.getData().get(0).getResult() == null || userPostSettingResponse.getData().get(0).getResult().size() == 0) {
            savePostTextView.setText(getString(R.string.groups_save_post));
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
            currentPostPrefsForUser = null;
            return;
        }
        currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
        if (currentPostPrefsForUser.getIsBookmarked() == 1) {
            savePostTextView.setText(getString(R.string.groups_remove_post));
        } else {
            savePostTextView.setText(getString(R.string.groups_save_post));
        }

        if (currentPostPrefsForUser.getNotificationOff() == 1) {
            notificationToggleTextView.setText("ENABLE NOTIFICATION");
        } else {
            notificationToggleTextView.setText("DISABLE NOTIFICATION");
        }
    }

    private void setAdminPostPreferences(GroupPostResponse groupPostResponse) {
        pinPostTextView.setVisibility(View.VISIBLE);
        blockUserTextView.setVisibility(View.VISIBLE);
        deletePostTextView.setVisibility(View.VISIBLE);
        if (groupPostResponse.getData().get(0).getResult().get(0).getIsPinned() == 1) {
            pinPostTextView.setText(getString(R.string.groups_unpin_post));
        } else {
            pinPostTextView.setText(getString(R.string.groups_pin_post));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
                if (addPostContainer.getVisibility() == View.VISIBLE) {
                    addPostContainer.setVisibility(View.GONE);
                }
                isLastPageReached = false;
                skip = 0;
                limit = 10;
                postList.clear();
                groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                tab.select();
            } else if (requestCode == EDIT_POST_REQUEST_CODE) {
                if (postSettingsContainerMain.getVisibility() == View.VISIBLE) {
                    postSettingsContainerMain.setVisibility(View.GONE);
                }
                selectedPost.setContent(data.getStringExtra("updatedContent"));
                groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}