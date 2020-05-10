package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.MixFeedResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 26/12/16.
 */
public interface RecommendationAPI {

    @GET("v1/recommendations/v2/{userId}")
    Call<ArticleListingResponse> getRecommendedArticlesList(@Path("userId") String userId,
            @Query("size") int limit,
            @Query("chunks") String chunks,
            @Query("lang") String lang);

    @GET("v3/recommendations/v2/{userId}")
    Call<ArticleListingResponse> getFollowingArticlesList(@Path("userId") String userId,
            @Query("size") int limit,
            @Query("chunks") String chunks,
            @Query("lang") String lang);

    @GET("v1/recommendations/v2/{userId}")
    Call<MixFeedResponse> getRecommendedFeed(@Path("userId") String userId,
            @Query("size") int limit,
            @Query("chunks") String chunks,
            @Query("lang") String lang);

    @GET("/v1/recommendations/v3/{userId}")
    Call<MixFeedResponse> getFollowingFeed(@Path("userId") String userId,
            @Query("size") int limit,
            @Query("chunks") String chunks);
}
