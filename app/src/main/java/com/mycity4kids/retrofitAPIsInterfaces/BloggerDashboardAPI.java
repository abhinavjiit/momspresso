package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.UserTaggableModel;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.MixFeedResponse;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anshul on 5/5/16.
 */
public interface BloggerDashboardAPI {

    @GET("v2/users/dashboard/{userId}")
    Call<UserDetailResponse> getBloggerData(@Path("userId") String userId);

    @GET("v1/articles/user/{userId}")
    Call<ArticleListingResponse> getAuthorsPublishedArticles(@Path("userId") String userId,
            @Query("sort") int sort,
            @Query("start") int start,
            @Query("end") int end);

    @GET("/user_history/v1/{userId}")
    Call<ArticleListingResponse> getAuthorsReadArticles(@Path("userId") String userId,
            @Query("size") int size,
            @Query("chunks") int chunks,
            @Query("filter") String filter);

    @GET("v1/reports/{userId}/{from}/{to}")
    Call<BloggerAnalyticsResponse> getAnalyticsReport(@Path("userId") String userId,
            @Path("from") String from,
            @Path("to") String to);

    @GET("v1/users/recommendationList/{userId}")
    Call<ArticleListingResponse> getUsersRecommendation(@Path("userId") String userId);

    @GET("v1/users/bookmark/")
    Call<ArticleListingResponse> getBookmarkedList(@Query("limit") int limit,
            @Query("pagination") String pagination);

    @GET("v1/users/bookmarkVideo/")
    Call<ArticleListingResponse> getUsersWatchLaterVideos(@Query("limit") int limit,
            @Query("pagination") String pagination);

    @GET("v3/comments/{userId}")
    Call<UserCommentsResponse> getUsersComments(@Path("userId") String userId);

    @GET("author/feed/{userId}")
    Call<MixFeedResponse> getUsersAllContent(@Path("userId") String userId,
            @Query("start") int start,
            @Query("size") int size,
            @Query("content_type") String contentType);

    @GET("bookmark/feed/")
    Call<MixFeedResponse> getUsersAllBookmark(@Query("start") int start,
            @Query("size") int size,
            @Query("collection_type") int collectionType);

    @PUT("/v2/users/{userId}")
    Call<ResponseBody> updateUserTaggableSetting(@Path("userId") String userId,
            @Body UserTaggableModel userTaggableModel);
}


