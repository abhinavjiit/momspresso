package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UpdateGroupMemberRoleRequest;
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
    private RelativeLayout root;
    private Animation slideAnim;
    private Animation fadeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_membership_request_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

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

        groupMembersPagerAdapter = new GroupMembersPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                groupId);
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
                GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
                UpdateGroupMemberRoleRequest updateGroupMemberRoleRequest = new UpdateGroupMemberRoleRequest();
                updateGroupMemberRoleRequest.setUserId(memberDetails.getUserId());
                updateGroupMemberRoleRequest.setIsModerator(1);
                updateGroupMemberRoleRequest.setIsAdmin(0);
                Call<GroupsMembershipResponse> call1 = groupsApi
                        .updateMemberRole(memberDetails.getId(), updateGroupMemberRoleRequest);
                call1.enqueue(updateGroupMemberRoleResponseCallback);
            }
            break;
            case R.id.blockUnblockUserTextView: {
                Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
                Call<GroupsMembershipResponse> call1 = groupsApi.blockUserWithMembershipId(memberDetails.getId());
                call1.enqueue(updateGroupMemberRoleResponseCallback);
            }
            break;
            default:
                break;
        }
    }

    private Callback<GroupsMembershipResponse> updateGroupMemberRoleResponseCallback =
            new Callback<GroupsMembershipResponse>() {
                @Override
                public void onResponse(Call<GroupsMembershipResponse> call,
                        retrofit2.Response<GroupsMembershipResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
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
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
                    showToast("Failed to update membership");
                    FirebaseCrashlytics.getInstance().recordException(t);
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
