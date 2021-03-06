package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.models.request.CreateUpdateGroupRequest;
import com.mycity4kids.models.request.GroupsCategoryUpdateRequest;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsCategoryMappingResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.ui.adapter.EditGroupPagerAdapter;
import com.mycity4kids.utils.AppUtils;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 5/7/18.
 */

public class EditGroupActivity extends BaseActivity implements View.OnClickListener {

    private int groupId;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private EditGroupPagerAdapter adapter;
    private TextView saveGroupDetailsTextView;
    private GroupResult groupItem;
    private String userDynamoId;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        saveGroupDetailsTextView = (TextView) findViewById(R.id.saveGroupDetailsTextView);

        saveGroupDetailsTextView.setOnClickListener(this);
        groupId = getIntent().getIntExtra("groupId", 0);

        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getGroupDetails(groupId);
    }

    private void getGroupDetails(int groupId) {
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
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupDetailResponse = response.body();
                    processGroupResponse(groupDetailResponse);
                } else {

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processGroupResponse(GroupDetailResponse groupDetailResponse) {
        groupItem = groupDetailResponse.getData().getResult();
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_sections_about)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_join_form)));
        adapter = new EditGroupPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                groupDetailResponse.getData().getResult());

        AppUtils.changeTabsFont(tabLayout);
        viewPager.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveGroupDetailsTextView:
                ArrayList<GroupsCategoryMappingResult> catList = adapter.getUpdatedCategories();
                if (catList == null || catList.isEmpty()) {
                    showToast("Please chose atleast one category");
                    return;
                }
                removeExistingCategories();
                break;
        }
    }

    private void removeExistingCategories() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        ArrayList<GroupsCategoryUpdateRequest> groupsCategoryUpdateRequestList = new ArrayList<>();
        Call<ResponseBody> call = groupsAPI.removeGroupCategory(groupId);
        call.enqueue(removeCategoriesResponseCallback);
    }

    private Callback<ResponseBody> removeCategoriesResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
//                    String resData = new String(response.body().bytes());
                    ArrayList<GroupsCategoryMappingResult> catList = adapter.getUpdatedCategories();
                    Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                    GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

                    ArrayList<GroupsCategoryUpdateRequest> groupsCategoryUpdateRequestList = new ArrayList<>();
                    for (int i = 0; i < catList.size(); i++) {
                        if (catList.get(i).isSelected()) {
                            GroupsCategoryUpdateRequest groupsCategoryUpdateRequest = new GroupsCategoryUpdateRequest();
                            groupsCategoryUpdateRequest.setCategoryId(catList.get(i).getCategoryId());
                            groupsCategoryUpdateRequest.setCategoryName(catList.get(i).getCategoryName());
                            groupsCategoryUpdateRequest.setGroupId(groupItem.getId());
                            groupsCategoryUpdateRequest.setCreatedBy(userDynamoId);
                            groupsCategoryUpdateRequestList.add(groupsCategoryUpdateRequest);
                        }
                    }

                    Call<GroupsCategoryMappingResponse> call1 = groupsAPI
                            .addGroupCategory(groupsCategoryUpdateRequestList);
                    call1.enqueue(categoryUpdateResponseCallback);

                    GroupResult groupResult = adapter.getAllUpdatedFields();
                    if (groupResult != null) {

                        CreateUpdateGroupRequest createUpdateGroupRequest = new CreateUpdateGroupRequest();

                        createUpdateGroupRequest.setType(groupItem.getType());
                        createUpdateGroupRequest.setTitle(groupItem.getTitle());
                        createUpdateGroupRequest.setCreatedBy(groupItem.getCreatedBy());
                        createUpdateGroupRequest.setLang(groupItem.getLang());
                        createUpdateGroupRequest.setLogoImage(groupItem.getLogoImage());
                        createUpdateGroupRequest.setUrl(groupItem.getUrl());
                        createUpdateGroupRequest.setUserId(userDynamoId);
                        createUpdateGroupRequest.setDescription(groupResult.getDescription());
                        createUpdateGroupRequest.setHeaderImage(groupResult.getHeaderImage());
                        createUpdateGroupRequest.setQuestionnaire(groupResult.getQuestionnaire());

                        Call<GroupDetailResponse> call2 = groupsAPI
                                .updateGroup(groupItem.getId(), createUpdateGroupRequest);
                        call2.enqueue(updateGroupResponseCallback);
                    }
                } else {

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupsCategoryMappingResponse> categoryUpdateResponseCallback = new Callback<GroupsCategoryMappingResponse>() {
        @Override
        public void onResponse(Call<GroupsCategoryMappingResponse> call,
                retrofit2.Response<GroupsCategoryMappingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsCategoryMappingResponse groupDetailResponse = response.body();
                } else {

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsCategoryMappingResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<GroupDetailResponse> updateGroupResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupDetailResponse = response.body();
                    showToast("Updated successfully");
                } else {

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
