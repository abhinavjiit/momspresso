package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UpdateGroupMemberRoleRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsMembershipResult;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.GroupMembersPagerAdapter;
import com.mycity4kids.ui.fragment.GroupExistingMemberTabFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 9/7/18.
 */

public class GroupMembershipActivity extends BaseActivity implements View.OnClickListener {

    private GroupMembersPagerAdapter groupMembersPagerAdapter;

    private Animation slideAnim, fadeAnim;

    private Toolbar toolbar;
    private int groupId;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView moderatorInviteTextView;
    private TextView blockUnblockUserTextView;
    private LinearLayout postSettingsContainer;
    private GroupsMembershipResult memberDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_membership_request_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        postSettingsContainerMain = (RelativeLayout) findViewById(R.id.postSettingsContainerMain);
        postSettingsContainer = (LinearLayout) findViewById(R.id.postSettingsContainer);
        overlayView = findViewById(R.id.overlayView);
        moderatorInviteTextView = (TextView) findViewById(R.id.moderatorInviteTextView);
        blockUnblockUserTextView = (TextView) findViewById(R.id.blockUnblockUserTextView);

        overlayView.setOnClickListener(this);
        moderatorInviteTextView.setOnClickListener(this);
        blockUnblockUserTextView.setOnClickListener(this);

        groupId = getIntent().getIntExtra("groupId", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        tabLayout.addTab(tabLayout.newTab().setText("Existing Members"));
        tabLayout.addTab(tabLayout.newTab().setText("Requests"));

        groupMembersPagerAdapter = new GroupMembersPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), groupId);
        viewPager.setAdapter(groupMembersPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }

    public void showMembersOption(GroupsMembershipResult groupsMembershipResult, String memberType) {
        memberDetails = groupsMembershipResult;

        postSettingsContainer.startAnimation(slideAnim);
        overlayView.startAnimation(fadeAnim);
        postSettingsContainerMain.setVisibility(View.VISIBLE);
        postSettingsContainer.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
        if (AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            moderatorInviteTextView.setVisibility(View.GONE);
        } else if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)) {
            moderatorInviteTextView.setVisibility(View.VISIBLE);
        } else {
            moderatorInviteTextView.setVisibility(View.GONE);
            blockUnblockUserTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
            case R.id.moderatorInviteTextView: {
                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                UpdateGroupMemberRoleRequest updateGroupMemberRoleRequest = new UpdateGroupMemberRoleRequest();
                updateGroupMemberRoleRequest.setUserId(memberDetails.getUserId());
                updateGroupMemberRoleRequest.setIsModerator(1);
                updateGroupMemberRoleRequest.setIsAdmin(0);
                Call<GroupsMembershipResponse> call1 = groupsAPI.updateMemberRole(memberDetails.getId(), updateGroupMemberRoleRequest);
                call1.enqueue(updateGroupMemberRoleResponseCallback);
            }
            break;
            case R.id.blockUnblockUserTextView: {
                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                UpdateGroupMembershipRequest updateGroupMembershipRequest = new UpdateGroupMembershipRequest();
//                updateGroupMembershipRequest.setUserId(memberDetails.getUserId());
                updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                Call<GroupsMembershipResponse> call1 = groupsAPI.updateMember(memberDetails.getId(), updateGroupMembershipRequest);
                call1.enqueue(updateGroupMemberRoleResponseCallback);
            }
            break;
        }
    }

    private Callback<GroupsMembershipResponse> updateGroupMemberRoleResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, retrofit2.Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                showToast("Failed to update membership");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsMembershipResponse groupsMembershipResponse = response.body();
                    showToast("Successfully updated membership");
                } else {
                    showToast("Failed to update membership");
                }
            } catch (Exception e) {
                showToast("Failed to update membership");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            showToast("Failed to update membership");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void updateExistingMemberList() {
        ((GroupExistingMemberTabFragment) groupMembersPagerAdapter.getItem(1)).refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
