package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
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
 * Created by hemant on 19/4/16.
 */
public class GroupsSearchActivity extends BaseActivity implements View.OnClickListener, GroupsRecyclerGridAdapter.RecyclerViewClickListener, GroupMembershipStatus.IMembershipStatus {

    private GroupsRecyclerGridAdapter adapter;

    private boolean isReuqestRunning = false;
    private ArrayList<GroupResult> groupList;
    private boolean isLastPageReached;
    private int skip = 0;
    private int limit = 10;
    private int totalGroupCount;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private Toolbar toolbar;
    private RecyclerView recyclerGridView;
    private TextView noGroupsTextView;
    private ProgressBar progressBar;
    private EditText toolbarTitle;
    private ImageView searchLogoImageView;
    private GroupResult selectedGroup;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(this, "GroupSearchScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        setContentView(R.layout.groups_search_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (EditText) findViewById(R.id.toolbarTitle);
        searchLogoImageView = (ImageView) findViewById(R.id.searchLogoImageView);
        recyclerGridView = (RecyclerView) findViewById(R.id.recyclerGridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noGroupsTextView = (TextView) findViewById(R.id.noGroupsTextView);

        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        searchLogoImageView.setOnClickListener(this);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerGridView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        recyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new GroupsRecyclerGridAdapter(this, this, true, true);
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
                            getSearchResults();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchLogoImageView:
                groupList.clear();
                skip = 0;
                limit = 10;
                adapter.notifyDataSetChanged();
                getSearchResults();
                break;
        }
    }

    private void getSearchResults() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsListingResponse> call = groupsAPI.searchGroups(toolbarTitle.getText().toString(), "group", 1, skip, limit);
        call.enqueue(getSearchResultReponseCallback);
    }

    private Callback<GroupsListingResponse> getSearchResultReponseCallback = new Callback<GroupsListingResponse>() {
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
            adapter.setNewListData(groupList);
            skip = skip + limit;
            if (skip >= totalGroupCount) {
                isLastPageReached = true;
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position, boolean isMember) {
        selectedGroup = groupList.get(position);
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupMembershipStatus.checkMembershipStatus(groupList.get(position).getId(), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
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
            Intent intent = new Intent(this, GroupDetailsActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
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

    @Override
    public void onMembershipStatusFetchFail() {

    }
}
