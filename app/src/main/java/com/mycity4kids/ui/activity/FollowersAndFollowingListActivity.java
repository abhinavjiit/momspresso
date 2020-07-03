package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.adapter.CollectionFollowFollowersListAdapter;
import com.mycity4kids.ui.adapter.FollowerFollowingListAdapter;
import com.mycity4kids.utils.EndlessScrollListener;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowersAndFollowingListActivity extends BaseActivity {

    public static final int LIMIT = 15;

    private int offset = 0;
    ListView followerFollowingListView;
    ProgressBar progressBar;
    TextView noResultTextView;
    Toolbar toolbar;
    private RelativeLayout root;
    private boolean isLastPageReached = false;
    private boolean isRequestRunning = false;

    FollowerFollowingListAdapter followerFollowingListAdapter;

    ArrayList<FollowersFollowingResult> datalist;
    ArrayList<FollowersFollowingResult> collectionDatalist;
    private String userId;
    RecyclerView collectionFollowFollowingListView;
    private CollectionFollowFollowersListAdapter collectionFollowFollowersListAdapter;
    private String followListType;
    private TextView toolbarTitle;
    private String collectionId = "";
    LinearLayoutManager linearLayoutManager;


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
        collectionFollowFollowingListView = findViewById(R.id.collectionFollowFollowingListView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        datalist = new ArrayList<>();
        collectionDatalist = new ArrayList<>();

        if (!StringUtils.isNullOrEmpty(collectionId)) {
            getCollectionFollowers(0);
            linearLayoutManager = new LinearLayoutManager(this);
            followerFollowingListView.setVisibility(View.GONE);
            collectionFollowFollowingListView.setVisibility(View.VISIBLE);
            collectionFollowFollowingListView.setLayoutManager(linearLayoutManager);
            collectionFollowFollowersListAdapter = new CollectionFollowFollowersListAdapter(this, followListType);
            collectionFollowFollowersListAdapter.setData(collectionDatalist);
            collectionFollowFollowingListView.setAdapter(collectionFollowFollowersListAdapter);
        } else {
            if (AppConstants.FOLLOWER_LIST.equals(followListType)) {
                Utils.pushGenericEvent(this, "Show_Followers_Listing",
                        userId, "FollowersAndFollowingListActivity");
            } else {
                Utils.pushGenericEvent(this, "Show_Following_Listing",
                        userId, "FollowersAndFollowingListActivity");
            }
            followerFollowingListView.setVisibility(View.VISIBLE);
            collectionFollowFollowingListView.setVisibility(View.GONE);
            followerFollowingListAdapter = new FollowerFollowingListAdapter(this, "SelfProfile_Followers_Follow");
            followerFollowingListAdapter.setData(datalist);
            followerFollowingListView.setAdapter(followerFollowingListAdapter);
            getFollowerFollowingList();
        }

        collectionFollowFollowingListView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getCollectionFollowers(totalItemsCount);
            }
        });

        followerFollowingListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isRequestRunning
                        && !isLastPageReached) {
                    isRequestRunning = true;
                    getFollowerFollowingList();
                }
            }
        });

        followerFollowingListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FollowersAndFollowingListActivity.this, UserProfileActivity.class);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
            intent.putExtra(Constants.USER_ID, datalist.get(position).getUserId());
            intent.putExtra(AppConstants.AUTHOR_NAME,
                    datalist.get(position).getFirstName() + " " + datalist.get(position).getLastName());
            intent.putExtra(Constants.FROM_SCREEN, "Followers/Following List");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getFollowerFollowingList() {
        if (StringUtils.isNullOrEmpty(collectionId)) {
            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            FollowAPI followListApi = retrofit.create(FollowAPI.class);
            if (AppConstants.FOLLOWER_LIST.equals(followListType)) {
                Call<FollowersFollowingResponse> callFollowerList = followListApi
                        .getFollowersListV2(userId, LIMIT, offset);
                callFollowerList.enqueue(getFollowersListResponseCallback);
                progressBar.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.myprofile_followers_label));
            } else {
                Call<FollowersFollowingResponse> callFollowingList = followListApi
                        .getFollowingListV2(userId, LIMIT, offset);
                callFollowingList.enqueue(getFollowersListResponseCallback);
                progressBar.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.myprofile_following_label));
            }
        }
    }

    private Callback<FollowersFollowingResponse> getCollectionFollowersList =
            new Callback<FollowersFollowingResponse>() {
                @Override
                public void onResponse(Call<FollowersFollowingResponse> call,
                        retrofit2.Response<FollowersFollowingResponse> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.body() == null) {
                        showToast(getString(R.string.went_wrong));
                        return;
                    }
                    try {
                        FollowersFollowingResponse responseData = response.body();
                        processCollectionFollowersListResponse(responseData);
                    } catch (Exception e) {
                        showToast(getString(R.string.server_went_wrong));
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowersFollowingResponse> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    noResultTextView.setVisibility(View.VISIBLE);
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private Callback<FollowersFollowingResponse> getFollowersListResponseCallback =
            new Callback<FollowersFollowingResponse>() {
                @Override
                public void onResponse(Call<FollowersFollowingResponse> call,
                        retrofit2.Response<FollowersFollowingResponse> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRequestRunning = false;
                    if (response.body() == null) {
                        showToast(getString(R.string.went_wrong));
                        return;
                    }
                    try {
                        FollowersFollowingResponse responseData = response.body();
                        processFollowersListResponse(responseData);
                    } catch (Exception e) {
                        showToast(getString(R.string.server_went_wrong));
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<FollowersFollowingResponse> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRequestRunning = false;
                    noResultTextView.setVisibility(View.VISIBLE);
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void processFollowersListResponse(FollowersFollowingResponse responseData) {
        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
            noResultTextView.setVisibility(View.GONE);
            ArrayList<FollowersFollowingResult> datalist = responseData.getData().getResult();
            if (datalist.size() == 0) {
                isLastPageReached = false;
                if (null != this.datalist && !this.datalist.isEmpty()) {
                    //No more next results for search from pagination
                    isLastPageReached = true;
                } else {
                    // No results for search
                    if (AppConstants.FOLLOWER_LIST.equals(followListType)) {
                        noResultTextView.setText(getResources().getString(R.string.empty_followers_in_profile));
                    } else {
                        noResultTextView.setText(getResources().getString(R.string.profile_empty_following));
                    }
                    noResultTextView.setVisibility(View.VISIBLE);
                    followerFollowingListView.setVisibility(View.GONE);
                }
            } else {
                noResultTextView.setVisibility(View.GONE);
                followerFollowingListView.setVisibility(View.VISIBLE);
                this.datalist.addAll(datalist);
                followerFollowingListAdapter.setData(this.datalist);
                offset = offset + LIMIT;
                followerFollowingListAdapter.notifyDataSetChanged();
            }
        } else {
            if (null != responseData.getReason()) {
                showToast(responseData.getReason());
            }
        }
    }

    private void processCollectionFollowersListResponse(FollowersFollowingResponse responseData) {
        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
            noResultTextView.setVisibility(View.GONE);
            datalist = responseData.getData().getResult();
            collectionDatalist.addAll(datalist);
            if (collectionDatalist.size() == 0) {
                noResultTextView.setText(getResources().getString(R.string.empty_followers_in_collection));
                noResultTextView.setVisibility(View.VISIBLE);
                collectionFollowFollowingListView.setVisibility(View.GONE);
            } else {
                collectionFollowFollowersListAdapter.setData(collectionDatalist);
                collectionFollowFollowersListAdapter.notifyDataSetChanged();
            }
        } else {
            if (null != responseData.getReason()) {
                showToast(responseData.getReason());
            }
        }
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

    private void getCollectionFollowers(int start) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followListApi = retrofit.create(FollowAPI.class);
        Call<FollowersFollowingResponse> callCollectionFollowersList = followListApi
                .getCollectionFollowingList(collectionId, start, 10);
        callCollectionFollowersList.enqueue(getCollectionFollowersList);
        progressBar.setVisibility(View.VISIBLE);
    }


}
