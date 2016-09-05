package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.adapter.FollowerFollowingListAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersAndFollowingListActivity extends BaseActivity {

    ListView followerFollowingListView;
    ProgressBar progressBar;
    TextView noResultTextView;
    Toolbar toolbar;

    FollowerFollowingListAdapter followerFollowingListAdapter;

    ArrayList<FollowersFollowingResult> mDatalist;
    private String userId;
    private String followListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_following_list_activity);
        Utils.pushOpenScreenEvent(FollowersAndFollowingListActivity.this, "Followers/Following List", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        followListType = getIntent().getStringExtra(AppConstants.FOLLOW_LIST_TYPE);
        userId = getIntent().getStringExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS);

        if (null == userId) {
            userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        }
        followerFollowingListView = (ListView) findViewById(R.id.followerFollowingListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noResultTextView = (TextView) findViewById(R.id.emptyList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDatalist = new ArrayList<>();


        followerFollowingListAdapter = new FollowerFollowingListAdapter(this);
        followerFollowingListAdapter.setData(mDatalist);
        followerFollowingListView.setAdapter(followerFollowingListAdapter);

        followerFollowingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FollowersAndFollowingListActivity.this, BloggerDashboardActivity.class);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, mDatalist.get(position).getUserId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatalist.clear();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followListAPI = retrofit.create(FollowAPI.class);
        if (AppConstants.FOLLOWER_LIST.equals(followListType)) {
            Call<FollowersFollowingResponse> callFollowerList = followListAPI.getFollowersList(userId);
            callFollowerList.enqueue(getFollowersListResponseCallback);
            progressBar.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Followers");
        } else {
            Call<FollowersFollowingResponse> callFollowingList = followListAPI.getFollowingList(userId);
            callFollowingList.enqueue(getFollowersListResponseCallback);
            progressBar.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Following");
        }
    }

    private Callback<FollowersFollowingResponse> getFollowersListResponseCallback = new Callback<FollowersFollowingResponse>() {
        @Override
        public void onResponse(Call<FollowersFollowingResponse> call, retrofit2.Response<FollowersFollowingResponse> response) {
            progressBar.setVisibility(View.INVISIBLE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowersFollowingResponse responseData = (FollowersFollowingResponse) response.body();
                processFollowersListResponse(responseData);
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<FollowersFollowingResponse> call, Throwable t) {
            progressBar.setVisibility(View.INVISIBLE);
            noResultTextView.setVisibility(View.VISIBLE);
//            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processFollowersListResponse(FollowersFollowingResponse responseData) {
        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
            noResultTextView.setVisibility(View.GONE);
            mDatalist = responseData.getData().getResult();

            if (mDatalist.size() == 0) {
                noResultTextView.setVisibility(View.VISIBLE);
                followerFollowingListView.setVisibility(View.GONE);
            } else {
                followerFollowingListAdapter.setData(mDatalist);
                followerFollowingListAdapter.notifyDataSetChanged();
            }
        } else {
            if (null != responseData.getReason())
                showToast(responseData.getReason());
        }
    }

    @Override
    protected void updateUi(Response response) {

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


}
