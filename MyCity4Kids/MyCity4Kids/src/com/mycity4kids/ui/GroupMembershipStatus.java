package com.mycity4kids.ui;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 23/7/18.
 */

public class GroupMembershipStatus {

    IMembershipStatus iMembershipStatus;

    public GroupMembershipStatus(IMembershipStatus iMembershipStatus) {
        this.iMembershipStatus = iMembershipStatus;
    }

    public void checkMembershipStatus(int groupId, String userId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsAPI.getUsersMembershipDetailsForGroup(groupId, userId);
        call.enqueue(membershipDetailsResponseCallback);
    }

    private Callback<GroupsMembershipResponse> membershipDetailsResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, Response<GroupsMembershipResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                iMembershipStatus.onMembershipStatusFetchFail();
                return;
            }
            try {
                if (response.isSuccessful()) {
                    iMembershipStatus.onMembershipStatusFetchSuccess(response.body());
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                iMembershipStatus.onMembershipStatusFetchFail();
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            iMembershipStatus.onMembershipStatusFetchFail();
        }
    };

    public interface IMembershipStatus {
        void onMembershipStatusFetchSuccess(GroupsMembershipResponse body);

        void onMembershipStatusFetchFail();
    }
}
