package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.kelltontech.ui.BaseActivity;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.adapter.GroupsRecyclerGridAdapter;
import com.mycity4kids.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 17/5/18.
 */

public class GroupsListingActivity extends BaseActivity implements GroupsRecyclerGridAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus, View.OnClickListener {

    private GroupsRecyclerGridAdapter adapter;
    private boolean isReuqestRunning = false;
    private ArrayList<GroupResult> groupList;
    ArrayList<GroupResult> listOutput1 = new ArrayList<>();
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 20;
    private int totalGroupCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    Toolbar toolbar;
    private RecyclerView recyclerGridView;
    private TextView noGroupsTextView;
    private ProgressBar progressBar;
    private GroupResult selectedGroup;
    private RelativeLayout addPostContainer;
    LinkedTreeMap<String, String> selectedQuestionnaire;
    TextView toolbarTitle;
    MixpanelAPI mixpanel;
    ArrayList<GroupResult> joinList = null;
    private String comingFrom = "";
    ImageView audioImageView, closeImageView, suggestedTopicImageView, writeArticleImageView;
    private RelativeLayout root;
    private View hideBottomDrawer;
    LinearLayout bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout announcementContainerR, pollContainerR, postContainerR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_listing_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        postContainerR = (RelativeLayout) findViewById(R.id.postContainerR);
        pollContainerR = (RelativeLayout) findViewById(R.id.pollContainerR);
        announcementContainerR = (RelativeLayout) findViewById(R.id.announcementContainerR);
        hideBottomDrawer = (View) findViewById(R.id.hideBottomDrawer);
        bottom_sheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        addPostContainer = (RelativeLayout) findViewById(R.id.addPostContainer);
        audioImageView = (ImageView) findViewById(R.id.audioImageView);
        closeImageView = (ImageView) findViewById(R.id.closeImageView);
        suggestedTopicImageView = (ImageView) findViewById(R.id.suggestedTopicImageView);
        writeArticleImageView = (ImageView) findViewById(R.id.writeArticleImageView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerGridView = (RecyclerView) findViewById(R.id.recyclerGridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noGroupsTextView = (TextView) findViewById(R.id.noGroupsTextView);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        audioImageView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        suggestedTopicImageView.setOnClickListener(this);
        writeArticleImageView.setOnClickListener(this);
        postContainerR.setOnClickListener(this);
        pollContainerR.setOnClickListener(this);
        announcementContainerR.setOnClickListener(this);
        hideBottomDrawer.setOnClickListener(this);
        bottomSheetStateChange();
        final boolean isMember = getIntent().getBooleanExtra("isMember", false);
        if (!isMember) {
            joinList = getIntent().getParcelableArrayListExtra("joinedList");
        }

        comingFrom = getIntent().getStringExtra("comingFrom");
        if (comingFrom == null) {
            comingFrom = "notFromFeed";
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerGridView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        recyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new GroupsRecyclerGridAdapter(this, this, isMember, true);
        groupList = new ArrayList<>();
        adapter.setNewListData(groupList);
        recyclerGridView.setAdapter(adapter);

        recyclerGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            if (isMember) {
                                getJoinedGroupListApi(skip, limit);
                            } else {
                                getAllGroupListApi(skip, limit);
                            }
                        }
                    }
                }
            }
        });

        if (isMember) {
            getJoinedGroupListApi(skip, limit);
            toolbarTitle.setText(getString(R.string.groups_join_label));
            Utils.pushOpenScreenEvent(this, "JoinedGroupsListingScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        } else {
            getAllGroupListApi(skip, limit);
            toolbarTitle.setText(getString(R.string.groups_all_groups));
            Utils.pushOpenScreenEvent(this, "AllGroupsListingScreen", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    private void getJoinedGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsAPI.getJoinedGroupList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER, skip, limit);
        call.enqueue(joinedGroupListResponseCallback);
    }

    private Callback<GroupsMembershipResponse> joinedGroupListResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            progressBar.setVisibility(View.GONE);
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
                    GroupsMembershipResponse responseModel = response.body();
                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processGroupListingResponse(GroupsMembershipResponse responseModel) {
        totalGroupCount = responseModel.getTotal();
        List<GroupsMembershipResult> membershipList = responseModel.getData().getResult();
        List<GroupResult> dataList = new ArrayList<>();
        for (int i = 0; i < membershipList.size(); i++) {
            dataList.add(membershipList.get(i).getGroupInfo());
        }
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != groupList && !groupList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noGroupsTextView.setVisibility(View.VISIBLE);
                groupList = (ArrayList<GroupResult>) dataList;
                adapter.setNewListData(groupList);
                adapter.notifyDataSetChanged();
                recyclerGridView.setVisibility(View.GONE);
            }
        } else {
            noGroupsTextView.setVisibility(View.GONE);
            if (skip == 0) {
                groupList = (ArrayList<GroupResult>) dataList;
            } else {
                groupList.addAll(dataList);
            }
            adapter.setNewListData(groupList);
            skip = skip + limit;
            if (skip >= totalGroupCount) {
                isLastPageReached = true;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getAllGroupListApi(int skip, int limit) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.getGroupList(skip, limit);
        call.enqueue(groupListResponseCallback);
    }

    private Callback<GroupsListingResponse> groupListResponseCallback = new Callback<GroupsListingResponse>() {
        @Override
        public void onResponse(Call<GroupsListingResponse> call, retrofit2.Response<GroupsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
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
                    GroupsListingResponse responseModel = response.body();
                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsListingResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processGroupListingResponse(GroupsListingResponse responseModel) {
        totalGroupCount = responseModel.getTotal();
        List<GroupResult> dataList = responseModel.getData().get(0).getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != groupList && !groupList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noGroupsTextView.setVisibility(View.VISIBLE);
                groupList = (ArrayList<GroupResult>) dataList;
                adapter.setNewListData(groupList);
                adapter.notifyDataSetChanged();
                recyclerGridView.setVisibility(View.GONE);
            }
        } else {
            noGroupsTextView.setVisibility(View.GONE);
            if (skip == 0) {
                groupList = (ArrayList<GroupResult>) dataList;
            } else {
                groupList.addAll(dataList);
            }

            for (int i = 0; i < groupList.size(); i++) {
                int count = 0;
                for (int j = 0; j < joinList.size(); j++) {
                    if (groupList.get(i).getId() == joinList.get(j).getId()) {
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    listOutput1.add(groupList.get(i));
                }

            }


         /*   List<GroupResult> listOutput =
                    groupList.stream()
                            .filter(e -> joinList.stream().map(GroupResult::getId).noneMatch(id -> id.equals(e.getId())))
                            .collect(Collectors.toList());*/


            adapter.setNewListData((ArrayList<GroupResult>) listOutput1);
            //  Observable.merge(Observable.just(groupList), Observable.just(joinList)).flatMap(Observable::fromIterable).toList();
            skip = skip + limit;
            if (skip >= totalGroupCount) {
                isLastPageReached = true;
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position, boolean isMember) {
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        if (isMember) {
            selectedGroup = groupList.get(position);
        } else {
            selectedGroup = listOutput1.get(position);
            selectedQuestionnaire = (LinkedTreeMap<String, String>) listOutput1.get(position).getQuestionnaire();
        }
        groupMembershipStatus.checkMembershipStatus(selectedGroup.getId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }
        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(this, getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            if (comingFrom.equals("myFeed")) {
                hideBottomDrawer.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                if (addPostContainer.getVisibility() == View.GONE) {
//                    addPostContainer.setVisibility(View.VISIBLE);
//                }
            } else {
                Intent intent = new Intent(this, GroupDetailsActivity.class);
                intent.putExtra("groupId", selectedGroup.getId());
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
                Utils.groupsEvent(GroupsListingActivity.this, "Groups you are member of_listing", "group card", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Groups_Discussion", "", "");
            }
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        hideBottomDrawer.setVisibility(View.GONE);
    }

    @Override
    public void onMembershipStatusFetchFail() {

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hideBottomDrawer:
                hideBottomSheet();
                break;
            case R.id.announcementContainerR:
            case R.id.audioImageView:
                SharedPrefUtils.setSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId(), "");
                Intent intent = new Intent(GroupsListingActivity.this, AddAudioGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent, 1111);
                hideBottomSheet();
                break;
            case R.id.closeImageView:
                if (addPostContainer.getVisibility() == View.VISIBLE) {
                    addPostContainer.setVisibility(View.GONE);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.postContainerR:
            case R.id.suggestedTopicImageView:
                SharedPrefUtils.setSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId(), "");
                Intent intent2 = new Intent(GroupsListingActivity.this, AddTextOrMediaGroupPostActivity.class);
                intent2.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent2, 1111);
                hideBottomSheet();
                break;
            case R.id.pollContainerR:
            case R.id.writeArticleImageView:
                SharedPrefUtils.setSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId(), "");
                Intent intent1 = new Intent(GroupsListingActivity.this, AddPollGroupPostActivity.class);
                intent1.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent1, 1111);
                hideBottomSheet();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
                setResult(RESULT_OK);
                addPostContainer.setVisibility(View.GONE);
                finish();
            }
        }
    }


    private void bottomSheetStateChange() {

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideBottomDrawer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {


            }
        });

    }
}