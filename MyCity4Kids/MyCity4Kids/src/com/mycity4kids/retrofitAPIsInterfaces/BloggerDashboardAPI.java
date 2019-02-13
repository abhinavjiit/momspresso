package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.SuggestBlogTitle;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.ReviewResponse;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by anshul on 5/5/16.
 */
public interface BloggerDashboardAPI {
    /*@GET("apiblogs/bloggerDashboardData?")
    Call<BloggerDashboardModel> getBloggerData(@Query("userId") String userId);*/

    @GET("apiblogs/publishedArticle?")
    Call<ResponseBody> getPublishedArticleList(@Query("userId") String userId,
                                               @Query("page") String page);

    @GET("v1/users/dashboard/{userId}")
    Call<UserDetailResponse> getBloggerData(@Path("userId") String userId);

    @GET
    Call<ReviewResponse> getUserReview(@Url String url);

    @GET
    Call<UserCommentsResponse> getUserComments(@Url String url);

    @GET
    Call<ArticleListingResponse> getPublishedArticles(@Url String url);

    @GET("v1/articles/user/{userId}")
    Call<ArticleListingResponse> getAuthorsPublishedArticles(@Path("userId") String userId,
                                                             @Query("sort") int sort,
                                                             @Query("start") int start,
                                                             @Query("end") int end);

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

    @GET("v1/comments/{userId}")
    Call<UserCommentsResponse> getUsersComments(@Path("userId") String userId,
                                                @Query("limit") int limit,
                                                @Query("pagination") String pagination);


    @GET("v1/users/suggestBlogTitle/")
    Call<SuggestBlogTitle> getUserhandle();


}
