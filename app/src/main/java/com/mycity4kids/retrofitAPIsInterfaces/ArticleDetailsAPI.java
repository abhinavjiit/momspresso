package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.TopCommentData;
import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.ArticleReadTimeRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleDetailWebserviceResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.CrownDataResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.LikeReactionModel;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ViewCountResponse;
import io.reactivex.Observable;
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

public interface ArticleDetailsAPI {

    @GET("/v1/articles/article/{articleId}")
    Call<ArticleDetailResult> getArticleDetailsFromRedis(@Path("articleId") String articleId,
            @Query("type") String type);

    @GET("https://s3-ap-southeast-1.amazonaws.com/mycity4kids-phoenix/articles-data/{articleId}.json")
    Call<ArticleDetailResult> getArticleDetailsFromS3(@Path("articleId") String articleId);

    //    For Local JSON Testing
    //    @GET("http://10.0.0.27/test/article-fa3ae55a274040688a4370b055e6f8d6.json")
    //    Call<ArticleDetailResult> getArticleDetailsFromLocal();

    @GET("/v1/articles/doc/")
    Call<ArticleDetailWebserviceResponse> getArticleDetailsFromWebservice(@Query("articleId") String articleId);

    @GET("follow/v2/users/check_following_bookmark_status/")
    Call<ArticleDetailResponse> checkFollowingBookmarkStatus(@Query("article_id") String articleId,
            @Query("author_id") String authorId);

    @POST("/v1/users/isBookmarkVideo/")
    Call<ArticleDetailResponse> checkBookmarkVideoStatus(@Body ArticleDetailRequest body);

    @GET("v1/articles/user/{userId}")
    Call<ArticleListingResponse> getPublishedArticles(@Path("userId") String userId,
            @Query("sort") int authorId,
            @Query("start") int start,
            @Query("end") int end);

    @GET("v3/comments/")
    Call<CommentListResponse> getArticleComments(@Query("postId") String articleId,
            @Query("type") String type,
            @Query("commentId") String paginationCommentId);

    @GET("/v3/comments/")
    Call<CommentListResponse> getCommentAndReplyData(@Query("postId") String articleId,
            @Query("type") String type,
            @Query("commentId") String paginationCommentId,
            @Query("replyId") String replyId);

    @GET("v3/comments")
    Call<CommentListResponse> getArticleCommentReplies(@Query("postId") String articleId,
            @Query("type") String type,
            @Query("commentId") String parentCommentId,
            @Query("replyId") String paginationReplyId);

    @GET("v3/comments")
    Call<CommentListResponse> getArticleCommentRepliesNotification(@Query("postId") String articleId,
            @Query("type") String type,
            @Query("commentId") String parentCommentId);


    @PUT("/v1/reactions/comment/{comment_id}/")
    Call<ResponseBody> likeDislikeComment(@Path("comment_id") String comment_id,
            @Body LikeReactionModel commentListData);


    @POST("v3/comments/")
    Call<CommentListResponse> addCommentOrReply(@Body AddEditCommentOrReplyRequest body);

    @DELETE("v3/comments/{commentOrReplyId}")
    Call<CommentListResponse> deleteCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId);

    @PUT("v3/comments/{commentOrReplyId}")
    Call<CommentListResponse> editCommentOrReply(@Path("commentOrReplyId") String commentOrReplyId,
            @Body AddEditCommentOrReplyRequest body);

    @GET
    Call<ResponseBody> getComments(@Url String url);

    @GET("v1/comments/batch/{commentId}")
    Call<ResponseBody> getCommentsFromId(@Path("commentId") String commentId);

    @GET("v1/articles/views/{articleId}")
    Call<ViewCountResponse> getViewCount(@Path("articleId") String articleId);

    @GET("crowns/v1/user/crowns/{userId}/")
    Call<CrownDataResponse> getCrownData(@Path("userId") String userId);

    @GET("v1/comments/fb/{articleId}")
    Call<FBCommentResponse> getFBComments(@Path("articleId") String articleId,
            @Query("pagination") String pagination);

    @PUT("v1/articles/views/{articleId}")
    Call<ResponseBody> updateViewCount(@Path("articleId") String articleId, @Body UpdateViewCountRequest body);

    @POST("v1/comments/")
    Call<AddCommentResponse> addComment(@Body AddCommentRequest body);

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
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(
            @Body RecommendUnrecommendArticleRequest body);

    @POST("v1/articles/chronos/")
    Call<ResponseBody> updateArticleTimeSpent(@Body ArticleReadTimeRequest body);

    //https://api.momspresso.com/v1/comments/enable-top-comment/
    @POST("/v1/comments/enable-top-comment/")
    Observable<ResponseBody> markedTopComment(@Body TopCommentData commentListData);
}