package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.adapter.FollowerFollowingListAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
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
    private RelativeLayout root;

    FollowerFollowingListAdapter followerFollowingListAdapter;

    ArrayList<FollowersFollowingResult> mDatalist;
    private String userId;
    private String followListType;
    private TextView toolbarTitle;
    private String collectionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_following_list_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        followListType = getIntent().getStringExtra(AppConstants.FOLLOW_LIST_TYPE);
        userId = getIntent().getStringExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS);
        if (null == userId) {
            userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        }
        if (getIntent().hasExtra("collectionId")) {
            collectionId = getIntent().getStringExtra("collectionId");
        }
        followerFollowingListView = (ListView) findViewById(R.id.followerFollowingListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noResultTextView = (TextView) findViewById(R.id.emptyList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatalist = new ArrayList<>();


        followerFollowingListAdapter = new FollowerFollowingListAdapter(this, followListType);
        followerFollowingListAdapter.setData(mDatalist);
        followerFollowingListView.setAdapter(followerFollowingListAdapter);

        followerFollowingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FollowersAndFollowingListActivity.this, PublicProfileActivity.class);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, mDatalist.get(position).getUserId());
                intent.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getFirstName() + " " + mDatalist.get(position).getLastName());
                intent.putExtra(Constants.FROM_SCREEN, "Followers/Following List");
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
            toolbarTitle.setText(getString(R.string.myprofile_followers_label));
        } else if (AppConstants.COLLECTION_FOLLOWING_LIST.equals(followListType)) {
            Retrofit retrofit1 = BaseApplication.getInstance().getCampaignRetrofit();
            FollowAPI followListAPIi = retrofit1.create(FollowAPI.class);
            Call<FollowersFollowingResponse> callCollectionFollowersList = followListAPIi.getCollectionFollowingList(collectionId, 0, 10);
            callCollectionFollowersList.enqueue(getCollectionFollowersList);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Call<FollowersFollowingResponse> callFollowingList = followListAPI.getFollowingList(userId);
            callFollowingList.enqueue(getFollowersListResponseCallback);
            progressBar.setVisibility(View.VISIBLE);
            toolbarTitle.setText(getString(R.string.myprofile_following_label));
        }
    }

    private Callback<FollowersFollowingResponse> getCollectionFollowersList = new Callback<FollowersFollowingResponse>() {
        @Override
        public void onResponse(Call<FollowersFollowingResponse> call, retrofit2.Response<FollowersFollowingResponse> response) {
            progressBar.setVisibility(View.INVISIBLE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowersFollowingResponse responseData = response.body();
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
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private Callback<FollowersFollowingResponse> getFollowersListResponseCallback = new Callback<FollowersFollowingResponse>() {
        @Override
        public void onResponse(Call<FollowersFollowingResponse> call, retrofit2.Response<FollowersFollowingResponse> response) {
            progressBar.setVisibility(View.INVISIBLE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                FollowersFollowingResponse responseData = response.body();
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
