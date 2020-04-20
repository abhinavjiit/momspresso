package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.SuggestedTopicsResponse;
import com.mycity4kids.models.response.TopicsFollowingStatusResponse;
import com.mycity4kids.models.response.TrendingListingResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;

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
    Call<Topics> MomVlogTopics(@Query("id") String id);

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

    @GET("/v1/articles/cities/{cityId}")
    Call<ArticleListingResponse> getBestArticlesForCity(@Path("cityId") String cityId,
                                                        @Query("sort") int sort,
                                                        @Query("start") int start,
                                                        @Query("end") int end,
                                                        @Query("lang") String lang);

    @PUT("/v1/users/{userId}/topics")
    Call<FollowUnfollowCategoriesResponse> followCategories(@Path("userId") String userId,
                                                            @Body FollowUnfollowCategoriesRequest body);

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
}