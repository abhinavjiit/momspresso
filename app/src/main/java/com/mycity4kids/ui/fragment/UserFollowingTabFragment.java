package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.adapter.FollowerFollowingListAdapter;
import com.mycity4kids.utils.ToastUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserFollowingTabFragment extends BaseFragment {

    private static final int LIMIT = 15;
    private int offset = 0;
    private boolean isLastPageReached = false;
    private boolean isRequestRunning = false;
    private ListView followerFollowingListView;
    private FollowerFollowingListAdapter followerFollowingListAdapter;
    private ArrayList<FollowersFollowingResult> datalist;
    private ProgressBar progressBar;
    private TextView noResultTextView;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_following_tab_fragment, container, false);

        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        followerFollowingListView = view.findViewById(R.id.followerFollowingListView);
        progressBar = view.findViewById(R.id.progressBar);
        noResultTextView = view.findViewById(R.id.emptyList);

        datalist = new ArrayList<>();
        followerFollowingListView.setVisibility(View.VISIBLE);
        followerFollowingListAdapter = new FollowerFollowingListAdapter(getActivity(), "SelfProfile_Following_Follow");
        followerFollowingListAdapter.setData(datalist);
        followerFollowingListView.setAdapter(followerFollowingListAdapter);
        fetchFollowing();

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
                    fetchFollowing();
                }
            }
        });

        followerFollowingListView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
            intent.putExtra(Constants.USER_ID, datalist.get(position).getUserId());
            intent.putExtra(AppConstants.AUTHOR_NAME,
                    datalist.get(position).getFirstName() + " " + datalist.get(position).getLastName());
            intent.putExtra(Constants.FROM_SCREEN, "Followers/Following List");
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchFollowing() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followListApi = retrofit.create(FollowAPI.class);
        Call<FollowersFollowingResponse> callFollowingList = followListApi.getFollowingListV2(userId, LIMIT, offset);
        callFollowingList.enqueue(getFollowersListResponseCallback);
    }

    private Callback<FollowersFollowingResponse> getFollowersListResponseCallback =
            new Callback<FollowersFollowingResponse>() {
                @Override
                public void onResponse(Call<FollowersFollowingResponse> call,
                        retrofit2.Response<FollowersFollowingResponse> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRequestRunning = false;
                    if (response.body() == null) {
                        if (isAdded()) {
                            ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                        }
                        return;
                    }
                    try {
                        FollowersFollowingResponse responseData = response.body();
                        processFollowersListResponse(responseData);
                    } catch (Exception e) {
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
                    noResultTextView.setText(getResources().getString(R.string.profile_empty_following));
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
            if (null != responseData.getReason() && isAdded()) {
                ToastUtils.showToast(getActivity(), responseData.getReason());
            }
        }
    }
}
