package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.ReportStoryOrCommentRequest;
import com.mycity4kids.models.request.ShortStoryConfigRequest;
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ReportStoryOrCommentResponse;
import com.mycity4kids.models.response.ShortStoryConfigData;
import com.mycity4kids.models.response.ShortStoryDetailResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
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

    @GET("/v1/articles/story/{storyId}")
    Call<ShortStoryDetailResult> getShortStoryDetails(@Path("storyId") String storyId,
                                                      @Query("type") String type);

    @GET("/v1/articles/doc/")
    Call<ShortStoryDetailResponse> getShortStoryDetailsFallback(@Query("articleId") String articleId);

    @GET("v3/comments")
    Call<CommentListResponse> getStoryComments(@Query("postId") String articleId,
                                               @Query("type") String type,
                                               @Query("commentId") String paginationCommentId);

    @GET("v3/comments")
    Call<CommentListResponse> getStoryCommentReplies(@Query("postId") String articleId,
                                                     @Query("type") String type,
                                                     @Query("commentId") String parentCommentId,
                                                     @Query("replyId") String paginationReplyId);

    @POST("v3/comments/")
    Call<CommentListResponse> addCommentOrReply(@Body AddEditCommentOrReplyRequest body);

    @DELETE("v3/comments/{commentOrReplyId}")
    Call<CommentListResponse> deleteCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId);

    @PUT("v3/comments/{commentOrReplyId}")
    Call<CommentListResponse> editCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId,
                                                 @Body AddEditCommentOrReplyRequest body);

    @POST("v1/articles/report/")
    Call<ReportStoryOrCommentResponse> reportStoryOrComment(@Body ReportStoryOrCommentRequest body);

    @GET("v1/articles/views/{articleId}")
    Call<ViewCountResponse> getViewCount(@Path("articleId") String articleId);

    @PUT("v1/articles/views/{articleId}")
    Call<ResponseBody> updateViewCount(@Path("articleId") String articleId, @Body UpdateViewCountRequest body);

    @PUT("v1/comments/{commentId}")
    Call<AddCommentResponse> editComment(@Path("commentId") String commentId,
                                         @Body AddCommentRequest body);

    @PUT("v1/users/likes/")
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(@Body RecommendUnrecommendArticleRequest body);

    @POST("/article-category-images/article-config/")
    Call<ResponseBody> shortStoryConfig(@Body ShortStoryConfigRequest body);

    @GET("/article-category-images/article-config/{ss_id}/")
    Call<ShortStoryConfigData> getConfig(@Path("ss_id") String shortStoryId);


    @PUT("/article-category-images/article-config/{ss_id}/")
    Call<ResponseBody> updateConfig(@Path("ss_id") String shortStoryId, @Body ShortStoryConfigRequest body);
}