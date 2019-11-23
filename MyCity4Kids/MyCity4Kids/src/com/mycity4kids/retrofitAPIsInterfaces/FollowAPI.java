package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.FollowersFollowingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 1/8/16.
 */
public interface FollowAPI {

    @GET("/v1/users/followingList/{userId}")
    Call<FollowersFollowingResponse> getFollowingList(@Path("userId") String userId);

    @GET("/v1/users/followersList/{userId}")
    Call<FollowersFollowingResponse> getFollowersList(@Path("userId") String userId);

    @POST("/v1/users/followers/")
    Call<FollowUnfollowUserResponse> followUser(@Body FollowUnfollowUserRequest body);

    @POST("/v1/users/unfollow/")
    Call<FollowUnfollowUserResponse> unfollowUser(@Body FollowUnfollowUserRequest body);

    @GET("/v1/collections/followers/{collectionId}")
    Call<FollowersFollowingResponse> getCollectionFollowingList(@Path("collectionId") String collectionId,
                                                                @Query("start") int start,
                                                                @Query("offset") int offset);

}
