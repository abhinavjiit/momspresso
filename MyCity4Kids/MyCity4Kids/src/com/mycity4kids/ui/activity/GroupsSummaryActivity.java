package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.JoinGroupRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsJoinResponse;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupSummaryPostRecyclerAdapter;
import com.mycity4kids.ui.fragment.GroupJoinConfirmationFragment;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsSummaryActivity extends BaseActivity implements View.OnClickListener, GroupSummaryPostRecyclerAdapter.RecyclerViewClickListener {

    private GroupSummaryPostRecyclerAdapter groupSummaryPostRecyclerAdapter;
    private GroupResult selectedGroup;
    private ArrayList<GroupPostResult> postList;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 10;
    private int totalPostCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String groupId;
    private GroupPostResult selectedPost;

    private Animation slideAnim, fadeAnim;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView joinGroupTextView;
    private ProgressBar progressBar;
    private TextView noPostsTextView;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView;
    private UserPostSettingResult currentPostPrefsForUser;
    private HashMap<String, String> questionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_summary_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        joinGroupTextView = (TextView) findViewById(R.id.joinGroupTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noPostsTextView = (TextView) findViewById(R.id.noPostsTextView);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);

        joinGroupTextView.setOnClickListener(this);
        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);

        selectedGroup = getIntent().getParcelableExtra("groupItem");
        groupId = getIntent().getStringExtra("groupId");
        questionMap = (HashMap<String, String>) getIntent().getSerializableExtra("questionnaire");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postList.add(new GroupPostResult());

        groupSummaryPostRecyclerAdapter = new GroupSummaryPostRecyclerAdapter(this, this);
        groupSummaryPostRecyclerAdapter.setData(postList);
        groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
        recyclerView.setAdapter(groupSummaryPostRecyclerAdapter);

//        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);
        if (selectedGroup == null) {
            getGroupDetails();
        }
        getGroupPosts();
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

        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {

        }
    };

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupPostResponse> call = groupsAPI.getAllPostsForAGroup(selectedGroup.getId(), 0, 10);
        call.enqueue(groupPostResponseCallback);
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
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
            groupSummaryPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupSummaryPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupSummaryPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savePostTextView:
                Log.d("savePostTextView", selectedPost.getId());
                if (savePostTextView.getText().toString().equals("SAVE POST")) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                Log.d("commentToggleTextView", selectedPost.getId());
                commentToggleTextView.setText("Disable Comment");
                break;
            case R.id.notificationToggleTextView:
                Log.d("notifToggleTextView", selectedPost.getId());
                if (notificationToggleTextView.getText().toString().equals("Disable Notification")) {
                    updateUserPostPreferences("enableNotif");
                } else {
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.reportPostTextView:
                Log.d("reportPostTextView", selectedPost.getId());
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                groupPostReportDialogFragment.setArguments(_args);
                groupPostReportDialogFragment.setCancelable(false);
                groupPostReportDialogFragment.show(fm, "Choose video report option");
                reportPostTextView.setText("Unreport");
                break;
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
            case R.id.joinGroupTextView:
                if (questionMap == null || questionMap.isEmpty()) {
                    Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                    GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                    JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
                    joinGroupRequest.setGroupId(selectedGroup.getId());
                    joinGroupRequest.setUserId(SharedPrefUtils.getUserDetailModel(GroupsSummaryActivity.this).getDynamoId());
                    Call<GroupsJoinResponse> call = groupsAPI.createMember(joinGroupRequest);
                    call.enqueue(groupJoinResponseCallback);
                } else {
                    Intent intent = new Intent(GroupsSummaryActivity.this, GroupsQuestionnaireActivity.class);
                    intent.putExtra("groupItem", selectedGroup);
                    intent.putExtra("questionnaire", questionMap);
                    startActivity(intent);
                }
//                if (selectedGroup.getType().equals("1")) {
//
//                } else {
//
//                }
                break;
        }

    }

    private void updateUserPostPreferences(String action) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        UpdateUserPostSettingsRequest request = new UpdateUserPostSettingsRequest();
        request.setPostId(Integer.parseInt(selectedPost.getId()));
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
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.userImageView:
            case R.id.usernameTextView: {
                Intent intent = new Intent(GroupsSummaryActivity.this, GroupDetailsActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivity(intent);
                break;
            }
            case R.id.postSettingImageView:
                selectedPost = postList.get(position);
                getCurrentPostSettingsStatus(selectedPost);
                postSettingsContainer.startAnimation(slideAnim);
                overlayView.startAnimation(fadeAnim);
                postSettingsContainerMain.setVisibility(View.VISIBLE);
                postSettingsContainer.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.VISIBLE);
                break;
            case R.id.postDataTextView:
            case R.id.postDateTextView: {
                Intent intent = new Intent(GroupsSummaryActivity.this, GroupPostDetailActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivity(intent);
                break;
            }
        }
    }

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

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupsSummaryActivity.this).getDynamoId())) {
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

    private Callback<GroupsJoinResponse> groupJoinResponseCallback = new Callback<GroupsJoinResponse>() {
        @Override
        public void onResponse(Call<GroupsJoinResponse> call, retrofit2.Response<GroupsJoinResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    showSuccessDialog();
//                    GroupsJoinResponse responseModel = response.body();
//                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsJoinResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showSuccessDialog() {
        GroupJoinConfirmationFragment groupJoinConfirmationFragment = new GroupJoinConfirmationFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("groupItem", selectedGroup);
        groupJoinConfirmationFragment.setArguments(_args);
        groupJoinConfirmationFragment.setCancelable(true);
        groupJoinConfirmationFragment.show(fm, "Choose video option");
    }
}
