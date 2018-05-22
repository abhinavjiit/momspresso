package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.GroupAboutRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupBlogsRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupsGenericPostRecyclerAdapter;
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

    private int nextPageNumber = 1;
    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<GroupPostResult> postList;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private final static String[] sectionsKey = {"ABOUT", "DISCUSSION", "BLOGS", "PHOTOS", "VIDEOS", "TOP POSTS", "POLLS"};
    private GroupResult selectedGroup;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details_activity);
        addPostContainer = (RelativeLayout) findViewById(R.id.addPostContainer);
        addPostFAB = (FloatingActionButton) findViewById(R.id.addPostFAB);
        postContainer = (LinearLayout) findViewById(R.id.postContainer);
        pollContainer = (LinearLayout) findViewById(R.id.pollContainer);
        closeImageView = (ImageView) findViewById(R.id.closeImageView);
        noPostsTextView = (TextView) findViewById(R.id.noPostsTextView);
        groupNameTextView = (TextView) findViewById(R.id.groupNameTextView);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");

        toolbarTitle.setText(getString(R.string.groups_search_in) + " " + selectedGroup.getTitle());
        groupNameTextView.setText(selectedGroup.getTitle());

        addPostFAB.setOnClickListener(this);
        pollContainer.setOnClickListener(this);
        postContainer.setOnClickListener(this);
        closeImageView.setOnClickListener(this);

        String[] sections = {
                getString(R.string.groups_sections_about), getString(R.string.groups_sections_discussions), getString(R.string.groups_sections_blogs),
                getString(R.string.groups_sections_photos), getString(R.string.groups_sections_videos), getString(R.string.groups_sections_top_posts), getString(R.string.groups_sections_polls)
        };

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        groupPostTabLayout = (TabLayout) findViewById(R.id.groupPostTabLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUpTabLayout(sections);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("one");
        arrayList.add("two");
        arrayList.add("three");
        arrayList.add("four");
        arrayList.add("five");
        arrayList.add("six");

        groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(this, this);
        groupAboutRecyclerAdapter.setData(selectedGroup);
        recyclerView.setAdapter(groupAboutRecyclerAdapter);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        groupBlogsRecyclerAdapter = new GroupBlogsRecyclerAdapter(this, this);
        groupBlogsRecyclerAdapter.setData(articleDataModelsNew);

        postList = new ArrayList<>();
        groupsGenericPostRecyclerAdapter = new GroupsGenericPostRecyclerAdapter(this, this);
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

        getGroupPosts();

    }

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupPostResponse> call = groupsAPI.getAllPostsForAGroup(selectedGroup.getId(), skip, limit);
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
        ArrayList<GroupPostResult> dataList = response.getData().get(0).getResult();
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
            case R.id.addPostFAB:
                addPostContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.postContainer: {
                Intent intent = new Intent(GroupDetailsActivity.this, AddTextOrMediaGroupPostActivity.class);
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
        }
    }

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
                    Intent intent = new Intent(GroupDetailsActivity.this, GroupPostDetailActivity.class);
                    startActivity(intent);
                } else if (AppConstants.GROUP_SECTION_VIDEOS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_TOP_POSTS.equalsIgnoreCase(tab.getTag().toString())) {
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    Intent intent = new Intent(GroupDetailsActivity.this, AddPollGroupPostActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    startActivity(intent);
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
}
