package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.BloggersYourFriendsFollowingResponseModel;
import com.mycity4kids.models.request.FacebookFriendsRequest;
import com.mycity4kids.models.request.FacebookInviteFriendsRequest;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.FacebookFriendsResponse;
import com.mycity4kids.models.response.FacebookInviteFriendsResponse;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.FollowersFollowingResponse;
import com.mycity4kids.models.response.SuggestedCreatorsResponse;
import com.mycity4kids.models.response.SuggestedTopics;
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

    @GET("/follow/v2/users/followingList/{userId}")
    Call<ResponseBody> getAllFollowingList(@Path("userId") String userId);

    @POST("/follow/v2/users/follow/")
    Call<ResponseBody> followUserInShortStoryListingV2(@Body FollowUnfollowUserRequest body);

    @POST("/follow/v2/users/unfollow/")
    Call<ResponseBody> unfollowUserInShortStoryListingV2(@Body FollowUnfollowUserRequest body);

    @POST("/v2/users/userfrdspoc/")
    Call<FacebookInviteFriendsResponse> getFacebookFriendsToInvite(@Body FacebookFriendsRequest body);

    @POST("/v2/users/userfrdspoc/")
    Call<FacebookInviteFriendsResponse> getFacebookFriendsToInvite();

    @POST("/v1/users/userfrdspoc/notification")
    Call<ResponseBody> inviteFBFriends(@Body FacebookInviteFriendsRequest body);

    @GET("/v1/blocked-user/user/{userId}")
    Call<FollowersFollowingResponse> getBlockUserList(@Path("userId") String userId,
            @Query("start") int start,
            @Query("limit") int limit);

    @GET("/v1/followers-suggestion/{userId}/")
    Call<BloggersYourFriendsFollowingResponseModel> getBloggersList(@Path("userId") String userId,
            @Query("suggestion_type") int suggestion_type,
            @Query("start") int start,
            @Query("limit") int limit,
            @Query("sort") int sort);

    @GET("/v1/followers-suggestion/{userId}")
    Call<SuggestedCreatorsResponse> getSuggestedCreators(@Path("userId") String userId,
            @Query("suggestion_type") int suggestionType,
            @Query("start") int start,
            @Query("limit") int limit,
            @Query("sort") int sort);

    @GET("/v1/followers-suggestion/{userId}")
    Call<SuggestedCreatorsResponse> getSuggestedCreators(@Path("userId") String userId,
            @Query("suggestion_type") int suggestionType,
            @Query("start") int start,
            @Query("limit") int limit);

    @GET("/v1/follow-topic-suggestion/{userId}")
    Call<SuggestedTopics> getSuggestedTopics(@Path("userId") String userId,
            @Query("start") int start,
            @Query("limit") int limit,
            @Query("sort") int sort);

    @GET("v1/users/")
    Call<ContributorListResponse> getContributorList(@Query("limit") int limit,
            @Query("sortType") int sortType,
            @Query("type") String type,
            @Query("lang") String lang,
            @Query("pagination") String pagination);
}
