package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.Share;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.ReportedContentModerationRequest;
import com.mycity4kids.models.response.GroupReportedContentResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsReportedContentResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupsReportedContentRecyclerAdapter;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 9/7/18.
 */

public class GroupsReportedContentActivity extends BaseActivity implements View.OnClickListener, GroupsReportedContentRecyclerAdapter.RecyclerViewClickListener {

    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int groupId;

    private Animation slideAnim, fadeAnim;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private ArrayList<GroupReportedContentResult> postList;
    private GroupsReportedContentRecyclerAdapter groupsReportedContentRecyclerAdapter;
    private GroupReportedContentResult selectedPost;
    private TextView noActionTextView, hideContentTextView, blockUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_reported_content_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        noActionTextView = (TextView) findViewById(R.id.noActionTextView);
        hideContentTextView = (TextView) findViewById(R.id.hideContentTextView);
        blockUserTextView = (TextView) findViewById(R.id.blockUserTextView);
        overlayView = findViewById(R.id.overlayView);

        groupId = getIntent().getIntExtra("groupId", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        overlayView.setOnClickListener(this);
        noActionTextView.setOnClickListener(this);
        hideContentTextView.setOnClickListener(this);
        blockUserTextView.setOnClickListener(this);

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        postList = new ArrayList<>();
        groupsReportedContentRecyclerAdapter = new GroupsReportedContentRecyclerAdapter(this, this);
        groupsReportedContentRecyclerAdapter.setData(postList);
        recyclerView.setAdapter(groupsReportedContentRecyclerAdapter);

        getReportedCotent();

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
                            getReportedCotent();
                        }
                    }
                }
            }
        });
    }

    private void getReportedCotent() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsReportedContentResponse> call = groupsAPI.getReportedContent(groupId, skip, limit);
        call.enqueue(reportedContentResponseCallback);
    }

    Callback<GroupsReportedContentResponse> reportedContentResponseCallback = new Callback<GroupsReportedContentResponse>() {
        @Override
        public void onResponse(Call<GroupsReportedContentResponse> call, retrofit2.Response<GroupsReportedContentResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsReportedContentResponse groupPostResponse = response.body();
                    processReportedContentList(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsReportedContentResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processReportedContentList(GroupsReportedContentResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupReportedContentResult> dataList = response.getData().getResult();
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
            formatPostData(dataList);
            postList.addAll(dataList);

            groupsReportedContentRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsReportedContentRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void formatPostData(ArrayList<GroupReportedContentResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            for (int i = 0; i < dataList.get(j).getCounts().size(); i++) {
                switch (dataList.get(j).getCounts().get(i).getName()) {
                    case "abusiveContent":
                        dataList.get(j).setAbusiveContentCount(dataList.get(j).getCounts().get(i).getCount());
                        break;
                    case "itsASpam":
                        dataList.get(j).setItsASpamCount(dataList.get(j).getCounts().get(i).getCount());
                        break;
                    case "notInteresting":
                        dataList.get(j).setNotInterestingCount(dataList.get(j).getCounts().get(i).getCount());
                        break;
                    case "other":
                        dataList.get(j).setOtherCount(dataList.get(j).getCounts().get(i).getCount());
                        break;
                }
            }
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
            case R.id.noActionTextView:
                updateReportedContent(selectedPost.getId(), AppConstants.CONTENT_REPORT_ACTION_NONE);
                break;
            case R.id.hideContentTextView:
                updateReportedContent(selectedPost.getId(), AppConstants.CONTENT_REPORT_ACTION_HIDE_CONTENT);
                break;
            case R.id.blockUserTextView:
                updateReportedContent(selectedPost.getId(), AppConstants.CONTENT_REPORT_ACTION_BLOCK_USER);
                break;
        }
    }

    private void updateReportedContent(int contentId, String contentReportAction) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        ReportedContentModerationRequest reportedContentModerationRequest = new ReportedContentModerationRequest();
        reportedContentModerationRequest.setActionBy(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        reportedContentModerationRequest.setActionResponse(contentReportAction);
        reportedContentModerationRequest.setIsModerated(1);
        Call<ResponseBody> call = groupsAPI.moderateReportedContent(contentId, reportedContentModerationRequest);
        call.enqueue(reportedContentModerationReponseCallback);
    }

    Callback<ResponseBody> reportedContentModerationReponseCallback = new Callback<ResponseBody>() {
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
                    ResponseBody reportedContentResponse = response.body();
                    postSettingsContainerMain.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                    postSettingsContainer.setVisibility(View.GONE);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.postSettingImageView:
                selectedPost = postList.get(position);
                postSettingsContainer.startAnimation(slideAnim);
                overlayView.startAnimation(fadeAnim);
                postSettingsContainerMain.setVisibility(View.VISIBLE);
                postSettingsContainer.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.VISIBLE);
                break;
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
