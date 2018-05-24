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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupPostDetailsAndCommentsRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddViewGroupPostCommentsRepliesDialogFragment;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 19/4/18.
 */

public class GroupPostDetailActivity extends BaseActivity implements View.OnClickListener, GroupPostDetailsAndCommentsRecyclerAdapter.RecyclerViewClickListener {

    private String commentURL = "https://s3-ap-southeast-1.amazonaws.com/mycity4kids-phoenix/comments-data/article-e32f733cab7e4d9f8c9344b94a089c1d-1.json";

    private GroupPostDetailsAndCommentsRecyclerAdapter groupPostDetailsAndCommentsRecyclerAdapter;

    private int totalPostCount;
    private int skip = 0;
    private int limit = 10;
    private boolean isLoading;
    private ArrayList<GroupPostCommentResult> completeResponseList;
    private String postType;
    private GroupResult selectedGroup;
    private GroupPostResult selectedPost;
    private UserPostSettingResult currentPostPrefsForUser;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;

    private Animation slideAnim, fadeAnim;

    private RecyclerView recyclerView;
    private HashMap<String, String> mediaUrls, pollOptions;
    private GroupPostResult postData;
    private Toolbar toolbar;
    private AddViewGroupPostCommentsRepliesDialogFragment addViewGroupPostCommentsRepliesDialogFragment;
    private ImageView addCommentImageView;
    private EditText writeCommentEditText;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView, notificationToggleTextView, commentToggleTextView, reportPostTextView;
    private ProgressBar progressBar;
    private ImageView groupSettingsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_post_detail_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addCommentImageView = (ImageView) findViewById(R.id.addCommentImageView);
        writeCommentEditText = (EditText) findViewById(R.id.writeCommentEditText);
        groupSettingsImageView = (ImageView) findViewById(R.id.groupSettingsImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = (TextView) findViewById(R.id.savePostTextView);
        notificationToggleTextView = (TextView) findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = (TextView) findViewById(R.id.commentToggleTextView);
        reportPostTextView = (TextView) findViewById(R.id.reportPostTextView);

        selectedGroup = getIntent().getParcelableExtra("groupItem");
        postType = getIntent().getStringExtra("postType");
        postData = (GroupPostResult) getIntent().getParcelableExtra("postData");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (AppConstants.POST_TYPE_MEDIA.equals(postType)) {
            mediaUrls = (HashMap<String, String>) getIntent().getSerializableExtra("mediaUrls");
            postData.setMediaUrls(mediaUrls);
        } else if (AppConstants.POST_TYPE_IMAGE_POLL.equals(postType) || AppConstants.POST_TYPE_TEXT_POLL.equals(postType)) {
            pollOptions = (HashMap<String, String>) getIntent().getSerializableExtra("pollOptions");
            postData.setPollOptions(pollOptions);
        }

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        addCommentImageView.setOnClickListener(this);
        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);

        completeResponseList = new ArrayList<>();
        completeResponseList.add(new GroupPostCommentResult()); // Empty element for Header position

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        groupPostDetailsAndCommentsRecyclerAdapter = new GroupPostDetailsAndCommentsRecyclerAdapter(this, this, postType);
        groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, completeResponseList);
        recyclerView.setAdapter(groupPostDetailsAndCommentsRecyclerAdapter);

        getPostComments();

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
                            getPostComments();
                        }
                    }
                }
            }
        });
    }

    private void getPostComments() {
        Retrofit retro = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retro.create(GroupsAPI.class);
        Call<GroupPostCommentResponse> call = groupsAPI.getPostComments(postData.getGroupId(), postData.getId(), skip, limit);
        call.enqueue(postCommentCallback);
    }

    private Callback<GroupPostCommentResponse> postCommentCallback = new Callback<GroupPostCommentResponse>() {
        @Override
        public void onResponse(Call<GroupPostCommentResponse> call, retrofit2.Response<GroupPostCommentResponse> response) {
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
                    GroupPostCommentResponse groupPostResponse = response.body();
                    processRepliesListingResponse(groupPostResponse);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostCommentResponse> call, Throwable t) {
            isReuqestRunning = false;
        }
    };

    private void processRepliesListingResponse(GroupPostCommentResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupPostCommentResult> dataList = (ArrayList<GroupPostCommentResult>) response.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != completeResponseList && !completeResponseList.isEmpty()) {
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
//            noPostsTextView.setVisibility(View.GONE);
            completeResponseList.addAll(dataList);
//            groupsGenericPostRecyclerAdapter.setHeaderData(selectedGroup);
            groupPostDetailsAndCommentsRecyclerAdapter.setData(postData, completeResponseList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.replyCommentTextView:
            case R.id.replyCountTextView:
//                GroupPostCommentReplyFragment
                addViewGroupPostCommentsRepliesDialogFragment = new AddViewGroupPostCommentsRepliesDialogFragment();
                Bundle _args = new Bundle();
                _args.putParcelable("commentReplies", completeResponseList.get(position));
                _args.putInt("childCount", completeResponseList.get(position).getChildCount());
                addViewGroupPostCommentsRepliesDialogFragment.setArguments(_args);
                FragmentManager fm = getSupportFragmentManager();
                addViewGroupPostCommentsRepliesDialogFragment.show(fm, "Replies");
                break;
            case R.id.postSettingImageView:
                getCurrentPostSettingsStatus(postData);
                postSettingsContainer.startAnimation(slideAnim);
                overlayView.startAnimation(fadeAnim);
                postSettingsContainerMain.setVisibility(View.VISIBLE);
                postSettingsContainer.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.VISIBLE);
                break;
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

        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupPostDetailActivity.this).getDynamoId())) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupSettingsImageView: {
                Intent intent = new Intent(GroupPostDetailActivity.this, GroupSettingsActivity.class);
                intent.putExtra("groupItem", postData);
                startActivity(intent);
            }
            break;
            case R.id.addCommentImageView:
                if (validateCommentText()) {
                    addComment();
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
                Log.d("commentToggleTextView", "" + selectedPost.getId());
                commentToggleTextView.setText("Disable Comment");
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
                groupPostReportDialogFragment.setCancelable(false);
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

    private void addComment() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(postData.getGroupId());
        addGpPostCommentOrReplyRequest.setPostId(postData.getId());
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(writeCommentEditText.getText().toString());
        Call<AddGpPostCommentReplyResponse> call = groupsAPI.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addCommentReplyResponseListener);
//        Call ca
    }

    private Callback<AddGpPostCommentReplyResponse> addCommentReplyResponseListener = new Callback<AddGpPostCommentReplyResponse>() {
        @Override
        public void onResponse(Call<AddGpPostCommentReplyResponse> call, retrofit2.Response<AddGpPostCommentReplyResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to add comment. Please try again");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGpPostCommentReplyResponse groupPostResponse = response.body();
                    GroupPostCommentResult groupPostCommentResult = new GroupPostCommentResult();
                    groupPostCommentResult.setId(groupPostResponse.getData().getResult().getId());
                    groupPostCommentResult.setContent(groupPostResponse.getData().getResult().getContent());
                    groupPostCommentResult.setSentiment(groupPostResponse.getData().getResult().getSentiment());
                    groupPostCommentResult.setParentId(groupPostResponse.getData().getResult().getParentId());
                    groupPostCommentResult.setGroupId(groupPostResponse.getData().getResult().getGroupId());
                    groupPostCommentResult.setPostId(groupPostResponse.getData().getResult().getPostId());
                    groupPostCommentResult.setUserId(groupPostResponse.getData().getResult().getUserId());
                    groupPostCommentResult.setIsActive(groupPostResponse.getData().getResult().isActive() ? 1 : 0);
                    groupPostCommentResult.setIsAnnon(groupPostResponse.getData().getResult().isAnnon() ? 1 : 0);
                    groupPostCommentResult.setModerationStatus(groupPostResponse.getData().getResult().getModerationStatus());
                    groupPostCommentResult.setModeratedBy(groupPostResponse.getData().getResult().getModeratedBy());
                    groupPostCommentResult.setModeratedOn(groupPostResponse.getData().getResult().getModeratedon());
                    groupPostCommentResult.setLang(groupPostResponse.getData().getResult().getLang());
                    groupPostCommentResult.setCreatedAt(groupPostResponse.getData().getResult().getCreatedAt());
                    groupPostCommentResult.setUpdatedAt(groupPostResponse.getData().getResult().getUpdatedAt());

                    completeResponseList.add(groupPostCommentResult);
                    groupPostDetailsAndCommentsRecyclerAdapter.notifyDataSetChanged();
                } else {
                    showToast("Failed to add comment. Please try again");
                }
            } catch (Exception e) {
                showToast("Failed to add comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
            showToast("Failed to add comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private boolean validateCommentText() {
        if (StringUtils.isNullOrEmpty(writeCommentEditText.getText().toString())) {
            Toast.makeText(this, "Please add a comment", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
