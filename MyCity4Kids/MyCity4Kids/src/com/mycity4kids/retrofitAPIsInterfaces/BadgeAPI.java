package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BadgeListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BadgeAPI {
    @GET("badges/")
    Call<BadgeListResponse> getBadgeList(@Query("user_id") String user_id);
}
