package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.ui.adapter.FollowerFollowingListAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserFollowingTabFragment extends BaseFragment {

    ListView followerFollowingListView;
    FollowerFollowingListAdapter followerFollowingListAdapter;
    ArrayList<FollowersFollowingResult> mDatalist;
    ProgressBar progressBar;
    TextView noResultTextView;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_following_tab_fragment, container, false);

        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        followerFollowingListView = view.findViewById(R.id.followerFollowingListView);
        progressBar = view.findViewById(R.id.progressBar);
        noResultTextView = view.findViewById(R.id.emptyList);

        mDatalist = new ArrayList<>();
        followerFollowingListView.setVisibility(View.VISIBLE);
        followerFollowingListAdapter = new FollowerFollowingListAdapter(getActivity(), AppConstants.FOLLOWING_LIST);
        followerFollowingListAdapter.setData(mDatalist);
        followerFollowingListView.setAdapter(followerFollowingListAdapter);

        followerFollowingListView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
            intent.putExtra(Constants.USER_ID, mDatalist.get(position).getUserId());
            intent.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getFirstName() + " " + mDatalist.get(position).getLastName());
            intent.putExtra(Constants.FROM_SCREEN, "Followers/Following List");
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatalist.clear();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI followListAPI = retrofit.create(FollowAPI.class);

        Call<FollowersFollowingResponse> callFollowingList = followListAPI.getFollowingListV2(userId);
        callFollowingList.enqueue(getFollowersListResponseCallback);
    }

    private Callback<FollowersFollowingResponse> getFollowersListResponseCallback = new Callback<FollowersFollowingResponse>() {
        @Override
        public void onResponse(Call<FollowersFollowingResponse> call, retrofit2.Response<FollowersFollowingResponse> response) {
            progressBar.setVisibility(View.INVISIBLE);
            if (response.body() == null) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                return;
            }
            try {
                FollowersFollowingResponse responseData = response.body();
                processFollowersListResponse(responseData);
            } catch (Exception e) {
                if (isAdded())
                    ToastUtils.showToast(getActivity(), getString(R.string.server_went_wrong));
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
                noResultTextView.setText(getResources().getString(R.string.profile_empty_following));
                noResultTextView.setVisibility(View.VISIBLE);
                followerFollowingListView.setVisibility(View.GONE);
            } else {
                followerFollowingListAdapter.setData(mDatalist);
                followerFollowingListAdapter.notifyDataSetChanged();
            }
        } else {
            if (null != responseData.getReason() && isAdded())
                ToastUtils.showToast(getActivity(), responseData.getReason());
        }
    }
}
