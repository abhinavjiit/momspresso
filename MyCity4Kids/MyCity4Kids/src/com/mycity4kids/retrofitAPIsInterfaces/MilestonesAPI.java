package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BadgeListResponse;
import com.mycity4kids.profile.MilestonesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MilestonesAPI {
    @GET("milestones/all")
    Call<MilestonesResponse> getMilestoneList(@Query("user_id") String user_id);

    @GET("milestones/detail")
    Call<MilestonesResponse> getMilestoneDetail(@Query("user_id") String user_id,
                                               @Query("id") String id);
}
