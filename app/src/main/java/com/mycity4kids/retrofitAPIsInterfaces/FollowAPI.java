package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.FacebookFriendsRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FacebookFriendsResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import okhttp3.ResponseBody;
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

    @POST("/v1/users/followers/")
    Call<ResponseBody> followUserInShortStoryListing(@Body FollowUnfollowUserRequest body);

    @POST("/v1/users/unfollow/")
    Call<FollowUnfollowUserResponse> unfollowUser(@Body FollowUnfollowUserRequest body);

    @POST("/v1/users/unfollow/")
    Call<ResponseBody> unfollowUserInShortStoryListing(@Body FollowUnfollowUserRequest body);

    @GET("/v1/collections/followers/{collectionId}")
    Call<FollowersFollowingResponse> getCollectionFollowingList(@Path("collectionId") String collectionId,
            @Query("start") int start,
            @Query("offset") int offset);

    @POST("/v1/users/userfrdspoc/")
    Call<FacebookFriendsResponse> getFacebookFriends(@Body FacebookFriendsRequest body);

    @POST("/follow/v2/users/follow/")
    Call<FollowUnfollowUserResponse> followUserV2(@Body FollowUnfollowUserRequest body);

    @POST("/follow/v2/users/unfollow/")
    Call<FollowUnfollowUserResponse> unfollowUserV2(@Body FollowUnfollowUserRequest body);

    @GET("/follow/v2/users/followers/{userId}")
    Call<FollowersFollowingResponse> getFollowersListV2(@Path("userId") String userId,
            @Query("limit") int limit,
            @Query("offset") int offset);

    @GET("/follow/v2/users/following/{userId}")
    Call<FollowersFollowingResponse> getFollowingListV2(@Path("userId") String userId,
            @Query("limit") int limit,
            @Query("offset") int offset);

    @POST("/follow/v2/users/follow/")
    Call<ResponseBody> followUserInShortStoryListingV2(@Body FollowUnfollowUserRequest body);

    @POST("/follow/v2/users/unfollow/")
    Call<ResponseBody> unfollowUserInShortStoryListingV2(@Body FollowUnfollowUserRequest body);
}
