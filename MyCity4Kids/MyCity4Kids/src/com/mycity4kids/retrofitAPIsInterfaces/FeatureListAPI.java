package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.FeaturedOnListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface FeatureListAPI {

    @GET("/v1/collections/featured/{userId}")
    Call<FeaturedOnListResponse> getFeatureList(@Path("userId") String articleId,
                                                @Query("start") int start,
                                                @Query("offset") int end);
}