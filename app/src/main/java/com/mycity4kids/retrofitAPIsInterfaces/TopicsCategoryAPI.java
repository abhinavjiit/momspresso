package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.SelectContentTopicsModel;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.MixFeedResponse;
import com.mycity4kids.models.response.SuggestedTopicsResponse;
import com.mycity4kids.models.response.TopicsFollowingStatusResponse;
import com.mycity4kids.models.response.TrendingListingResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hemant on 3/5/16.
 */
public interface TopicsCategoryAPI {

    @GET("/v1/utilities/config/")
    Call<ResponseBody> downloadCategoriesJSON();

    @GET("/v1/categories/")
    Call<ResponseBody> downloadTopicsJSON();

    @GET("http://192.168.29.66/momVlogData/abc.json")
    Call<Topics> TopicsJSON();

    @GET("/v2/categories/")
    Call<Topics> momVlogTopics(@Query("id") String id);

    @GET("/v1/articles/topics/{categoryId}")
    Call<ArticleListingResponse> getArticlesForCategory(@Path("categoryId") String categoryId,
            @Query("sort") int sort,
            @Query("start") int start,
            @Query("end") int end,
            @Query("lang") String lang);

    @GET("/v1/articles/handpicked/")
    Call<ArticleListingResponse> getTodaysBestArticles(@Query("publicationDate") String categoryId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("lang") String lang);

    @GET("/v1/articles/trending/{start}/{end}")
    Call<ArticleListingResponse> getTrendingArticles(@Path("start") int start,
            @Path("end") int end,
            @Query("lang") String lang);

    @GET("/v1/articles/recent/{start}/{end}")
    Call<ArticleListingResponse> getRecentArticles(@Path("start") int start,
            @Path("end") int end,
            @Query("lang") String lang);

    @GET("/v1/articles/handpicked/")
    Call<MixFeedResponse> getTodaysBestFeed(@Query("publicationDate") String categoryId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("lang") String lang);

    @GET("/v1/articles/handpicked/mixed/")
    Call<MixFeedResponse> getTodaysBestMixedFeed(
            @Query("publicationDate") String categoryId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("lang") String lang,
            @Query("itemTypes") String itemType
    );


    @GET("/v1/trending/mixfeed/{start}/{size}")
    Call<MixFeedResponse> getTrendingFeed(@Path("start") int start,
            @Path("size") int size,
            @Query("lang") String lang,
            @Query("itemTypes") String itemType);

    @GET("/v1/articles/recent/mixed/{start}/{end}")
    Call<MixFeedResponse> getRecentFeed(@Path("start") int start,
            @Path("end") int end,
            @Query("lang") String lang,
            @Query("itemTypes") String itemType);

    @PUT("/v1/users/{userId}/followTopics")
    Call<FollowUnfollowCategoriesResponse> followCategories(@Path("userId") String userId,
            @Body SelectContentTopicsModel body);

    @GET("/v1/users/{userId}/topics")
    Call<FollowUnfollowCategoriesResponse> getFollowedCategories(@Path("userId") String userId);

    @GET
    Call<ResponseBody> downloadTopicsListForFollowUnfollow(@Url String fileUrl);

    @GET("/v1/users/{userId}/topics/{topicId}")
    Call<TopicsFollowingStatusResponse> checkTopicsFollowingStatus(@Path("userId") String userId,
            @Path("topicId") String topicId);

    @GET("v1/categories/trending/{lowerLimit}/{upperLimit}/{articleCount}")
    Call<TrendingListingResponse> getTrendingTopicAndArticles(@Path("lowerLimit") String lowerLimit,
            @Path("upperLimit") String upperLimit,
            @Path("articleCount") String articleCount,
            @Query("lang") String lang);

    @GET("v1/categories/topic/")
    Call<SuggestedTopicsResponse> getSuggestedTopics(@Query("langCode") String lang);

    @GET("v2/categories/parent")
    Call<Topics> getCategorySiblings(@Query("id") String categoryId);

    @GET("/winner/content/")
    Call<ArticleListingResponse> getWinnerArticleChallenge(
            @Query("start") int start,
            @Query("size") int size,
            @Query("category_id") String category_id,
            @Query("content_type") String content_type
    );

/*
    GET /winner/content/?start=0&size=10&content_type=0&category_id=category-66b6883fd0434683b053f18aa4d52b59 HTTP/1.1
    Host: api.momspresso.com
    Accept-Language: en
    Cookie: session=5a5f8423-5ef1-4f5a-a9f2-d8c966b919c6
            Collapse*/
}
