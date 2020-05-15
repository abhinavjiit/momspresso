package com.mycity4kids.ui;

import android.accounts.NetworkErrorException;
import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
    private int groupId;

    public GroupMembershipStatus(IMembershipStatus iMembershipStatus) {
        this.iMembershipStatus = iMembershipStatus;
    }

    public void checkMembershipStatus(int groupId, String userId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        this.groupId = groupId;
        Call<GroupsMembershipResponse> call = groupsAPI.getUsersMembershipDetailsForGroup(groupId, userId);
        call.enqueue(membershipDetailsResponseCallback);
    }

    private Callback<GroupsMembershipResponse> membershipDetailsResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, Response<GroupsMembershipResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                iMembershipStatus.onMembershipStatusFetchFail();
                return;
            }
            try {
                if (response.isSuccessful()) {
                    iMembershipStatus.onMembershipStatusFetchSuccess(response.body(), groupId);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                iMembershipStatus.onMembershipStatusFetchFail();
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            iMembershipStatus.onMembershipStatusFetchFail();
        }
    };

    public interface IMembershipStatus {

        void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId);

        void onMembershipStatusFetchFail();
    }
}
