package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.FollowersFollowingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hemant on 1/8/16.
 */
public interface FollowListAPI {

    @GET("/v1/users/followingList/{userId}")
    Call<FollowersFollowingResponse> getFollowingList(@Path("userId") String userId);

    @GET("/v1/users/followersList/{userId}")
    Call<FollowersFollowingResponse> getFollowersList(@Path("userId") String userId);
}
