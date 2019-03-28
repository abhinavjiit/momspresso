package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.ArticleReadTimeRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hemant on 3/5/16.
 */
public interface VlogsListingAndDetailsAPI {

    @GET("/v2/videos/{videoId}")
    Call<VlogsDetailResponse> getVlogDetail(@Path("videoId") String videoId);

    @GET("v1/videos/{videoId}/views")
    Call<ViewCountResponse> getViewCount(@Path("videoId") String videoId);

    @PUT("v2/videos/{videoId}/views/")
    Call<ResponseBody> updateViewCount(@Path("videoId") String videoId);

    @GET("v2/videos")
    Call<VlogsListingResponse> getPublishedVlogs(@Query("user_id") String userId,
                                                 @Query("start") int start,
                                                 @Query("end") int end,
                                                 @Query("sort") int sort);

    @GET("v2/videos/")
    Call<VlogsListingResponse> getVlogsList(@Query("start") int start,
                                            @Query("end") int end,
                                            @Query("sort") int sort,
                                            @Query("type") int type,
                                            @Query("category_id") String categoryId);


    @POST("v2/videos/")
    Call<ResponseBody> publishHomeVideo(@Body UploadVideoRequest uploadVideoRequest/*@Query("start") int start,
                                                  @Query("end") int end,
                                                  @Query("sort") int sort,
                                                  @Query("type") int type*/);

    @GET("/v1/articles/doc/")
    Call<ArticleDetailResponse> getArticleDetailsFromWebservice(@Query("articleId") String articleId);

    @GET("v1/users/checkFollowingBookmarkStatus/")
    Call<ArticleDetailResponse> checkFollowingBookmarkStatus(@Query("articleId") String articleId,
                                                             @Query("authorId") String authorId);


    @GET
    Call<ResponseBody> getComments(@Url String url);


    @GET("v1/comments/fb/{articleId}")
    Call<FBCommentResponse> getFBComments(@Path("articleId") String articleId,
                                          @Query("pagination") String pagination);

    @POST("v1/comments/")
    Call<AddCommentResponse> addComment(@Body AddCommentRequest body);

    @PUT("v1/comments/{commentId}")
    Call<AddCommentResponse> editComment(@Path("commentId") String commentId,
                                         @Body AddCommentRequest body);

    @POST("v1/users/bookmark/")
    Call<AddBookmarkResponse> addBookmark(@Body ArticleDetailRequest body);

    @POST("v1/users/deleteBookmark/")
    Call<AddBookmarkResponse> deleteBookmark(@Body DeleteBookmarkRequest body);

    @POST("/v1/users/isBookmarkVideo/")
    Call<ArticleDetailResponse> checkBookmarkVideoStatus(@Body ArticleDetailRequest body);

    @GET("v1/recommend/related/{articleId}")
    Call<ArticleListingResponse> getCategoryRelatedArticles(@Path("articleId") String articleId);

    @GET("v1/users/likes/{articleId}")
    Call<ArticleRecommendationStatusResponse> getArticleRecommendedStatus(@Path("articleId") String articleId);

    @PUT("v1/users/likes/")
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(@Body RecommendUnrecommendArticleRequest body);

    @POST("v1/articles/chronos/")
    Call<ResponseBody> updateArticleTimeSpent(@Body ArticleReadTimeRequest body);

}