package com.mycity4kids.utils;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.GroupIdCategoryIdMappingResponse;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/9/18.
 */

public class GroupIdCategoryMap {

    private String categoryId;
    private List<String> categoriesList;
    private int groupId;
    private String gpHeading, gpSubHeading, gpImageUrl;
    GroupCategoryInterface groupCategoryInterface;

    public GroupIdCategoryMap(String categoryId, GroupCategoryInterface groupCategoryInterface) {
        this.categoryId = categoryId;
        this.groupCategoryInterface = groupCategoryInterface;
    }

    public GroupIdCategoryMap(ArrayList<String> categoriesList, GroupCategoryInterface groupCategoryInterface) {
        this.categoriesList = categoriesList;
        this.groupCategoryInterface = groupCategoryInterface;
    }

    public void getGroupIdForCurrentCategory() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupIdCategoryIdMappingResponse> mappingCall = groupsAPI.getGroupIdForSingleCategory("android", categoryId);
        mappingCall.enqueue(groupIdResponseCallback);
    }

    public void getGroupIdForMultipleCategories() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        Call<GroupIdCategoryIdMappingResponse> mappingCall = groupsAPI.getGroupIdForMultipleCategories("android", categoriesList);
        mappingCall.enqueue(groupIdResponseCallback);
    }

    private Callback<GroupIdCategoryIdMappingResponse> groupIdResponseCallback = new Callback<GroupIdCategoryIdMappingResponse>() {

        @Override
        public void onResponse(Call<GroupIdCategoryIdMappingResponse> call, retrofit2.Response<GroupIdCategoryIdMappingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                groupCategoryInterface.onGroupMappingResult(0, "", "", "");
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupIdCategoryIdMappingResponse responseModel = response.body();
                    groupId = responseModel.getData().get(0).getResult().get(0).getGroupId();
                    gpHeading = responseModel.getData().get(0).getResult().get(0).getHeading();
                    gpSubHeading = responseModel.getData().get(0).getResult().get(0).getSubHeading();
                    gpImageUrl = responseModel.getData().get(0).getResult().get(0).getMedia();

                    groupCategoryInterface.onGroupMappingResult(groupId, gpHeading, gpSubHeading, gpImageUrl);
                } else {
                    groupCategoryInterface.onGroupMappingResult(0, "", "", "");
                }
            } catch (Exception e) {
                groupCategoryInterface.onGroupMappingResult(0, "", "", "");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupIdCategoryIdMappingResponse> call, Throwable t) {
            groupCategoryInterface.onGroupMappingResult(0, "", "", "");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public interface GroupCategoryInterface {
        void onGroupMappingResult(int groupId, String gpHeading, String gpSubHeading, String gpImageUrl);
    }
}
