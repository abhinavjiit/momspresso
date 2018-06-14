package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.AddEditShortStoryCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.ArticleReadTimeRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.ReportStoryOrCommentRequest;
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ReportStoryOrCommentResponse;
import com.mycity4kids.models.response.ShortStoryCommentListResponse;
import com.mycity4kids.models.response.ShortStoryDetailResponse;
import com.mycity4kids.models.response.ViewCountResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hemant on 3/5/16.
 */
public interface ShortStoryAPI {

    @POST("v1/articles/stories/")
    Call<ArticleDraftResponse> saveOrPublishShortStory(@Body ShortStoryDraftOrPublishRequest body);

    @PUT("v1/articles/stories/{draftId}")
    Call<ArticleDraftResponse> updateOrPublishShortStory(@Path("draftId") String draftId,
                                                         @Body ShortStoryDraftOrPublishRequest body);


    @GET("v1/articles/story/user/{userId}")
    Call<ArticleListingResponse> getAuthorsPublishedStories(@Path("userId") String userId,
                                                            @Query("sort") int sort,
                                                            @Query("start") int start,
                                                            @Query("end") int end);

    @GET("v1/articles/stories/")
    Call<ResponseBody> getDraftsList(@Query("aType") String aType);

    @DELETE
    Call<ArticleDraftResponse> deleteDraft(@Url String url);


    @GET("/v1/articles/story/recent/{start}/{end}")
    Call<ArticleListingResponse> getRecentSortStories(@Path("start") int start,
                                                      @Path("end") int end,
                                                      @Query("lang") String lang);

    @GET("/v1/articles/story/popular/{start}/{end}")
    Call<ArticleListingResponse> getPopularSortStories(@Path("start") int start,
                                                       @Path("end") int end,
                                                       @Query("lang") String lang);

    @GET("/v1/articles/story/{storyId}")
    Call<ShortStoryDetailResponse> getShortStoryDetails(@Path("storyId") String storyId,
                                                        @Query("type") String type);

    @GET("/v1/articles/doc/")
    Call<ShortStoryDetailResponse> getShortStoryDetailsFallback(@Query("articleId") String articleId);

    @GET("v1/users/checkFollowingBookmarkStatus/")
    Call<ArticleDetailResponse> checkFollowingBookmarkStatus(@Query("articleId") String articleId,
                                                             @Query("authorId") String authorId);

    @POST("/v1/users/isBookmarkVideo/")
    Call<ArticleDetailResponse> checkBookmarkVideoStatus(@Body ArticleDetailRequest body);

    @GET("v1/articles/user/{userId}")
    Call<ArticleListingResponse> getPublishedArticles(@Path("userId") String userId,
                                                      @Query("sort") int authorId,
                                                      @Query("start") int start,
                                                      @Query("end") int end);

    @GET("v3/comments")
    Call<ShortStoryCommentListResponse> getStoryComments(@Query("postId") String articleId,
                                                         @Query("type") String type,
                                                         @Query("commentId") String paginationCommentId);

    @GET("v3/comments")
    Call<ShortStoryCommentListResponse> getStoryCommentReplies(@Query("postId") String articleId,
                                                               @Query("type") String type,
                                                               @Query("commentId") String parentCommentId,
                                                               @Query("replyId") String paginationReplyId);

    @POST("v3/comments/")
    Call<ShortStoryCommentListResponse> addCommentOrReply(@Body AddEditShortStoryCommentOrReplyRequest body);

    @DELETE("v3/comments/{commentOrReplyId}")
    Call<ShortStoryCommentListResponse> deleteCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId);

    @PUT("v3/comments/{commentOrReplyId}")
    Call<ShortStoryCommentListResponse> editCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId,
                                                           @Body AddEditShortStoryCommentOrReplyRequest body);

    @POST("v1/articles/report/")
    Call<ReportStoryOrCommentResponse> reportStoryOrComment(@Body ReportStoryOrCommentRequest body);


    @GET("v1/comments/batch/{commentId}")
    Call<ResponseBody> getCommentsFromId(@Path("commentId") String commentId);

    @GET("v1/articles/views/{articleId}")
    Call<ViewCountResponse> getViewCount(@Path("articleId") String articleId);

    @GET("v1/comments/fb/{articleId}")
    Call<FBCommentResponse> getFBComments(@Path("articleId") String articleId,
                                          @Query("pagination") String pagination);

    @PUT("v1/articles/views/{articleId}")
    Call<ResponseBody> updateViewCount(@Path("articleId") String articleId, @Body UpdateViewCountRequest body);

    @PUT("v1/comments/{commentId}")
    Call<AddCommentResponse> editComment(@Path("commentId") String commentId,
                                         @Body AddCommentRequest body);

    @POST("v1/users/bookmark/")
    Call<AddBookmarkResponse> addBookmark(@Body ArticleDetailRequest body);

    @POST("v1/users/bookmarkVideo/")
    Call<AddBookmarkResponse> addVideoWatchLater(@Body ArticleDetailRequest body);

    @POST("v1/users/deleteBookmark/")
    Call<AddBookmarkResponse> deleteBookmark(@Body DeleteBookmarkRequest body);

    @POST("v1/users/deleteBookmarkVideo/")
    Call<AddBookmarkResponse> deleteVideoWatchLater(@Body DeleteBookmarkRequest body);

    @GET("v1/recommend/related/{articleId}")
    Call<ArticleListingResponse> getCategoryRelatedArticles(@Path("articleId") String articleId,
                                                            @Query("start") int start,
                                                            @Query("end") int end,
                                                            @Query("lang") String lang);

    @GET("v1/users/likes/{articleId}")
    Call<ArticleRecommendationStatusResponse> getArticleRecommendedStatus(@Path("articleId") String articleId);

    @PUT("v1/users/likes/")
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(@Body RecommendUnrecommendArticleRequest body);

    @POST("v1/articles/chronos/")
    Call<ResponseBody> updateArticleTimeSpent(@Body ArticleReadTimeRequest body);
}