package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;

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
public interface ArticleDetailsAPI {

    @GET("apiparentingstop/get_blog_detail")
    Call<ArticleDetailResponse> getArticleBody(@Query("article_id") String article_id,
                                               @Query("user_id") String user_id,
                                               @Query("app_version") String app_version);

    @GET("apiparentingstop/get_comment")
    Call<ResponseBody> getArticleComments(@Query("article_id") String article_id,
                                          @Query("limit") String limit,
                                          @Query("offset") String offset,
                                          @Query("comment_type") String comment_type,
                                          @Query("user_id") String user_id,
                                          @Query("app_version") String app_version);

    @GET("https://s3-ap-northeast-1.amazonaws.com/microservices-sync-test/articles-data/{article_id}.json")
    Call<ArticleDetailResult> getArticleDetails(@Path("article_id") String article_id);

    @GET("v1/users/checkFollowingBookmarkStatus/")
    Call<ArticleDetailResponse> checkFollowingBookmarkStatus(@Query("articleId") String articleId,
                                                             @Query("authorId") String authorId);

    @GET
    Call<ResponseBody> getComments(@Url String url);

    @PUT("v1/articles/views/{articleId}")
    Call<ResponseBody> updateViewCount(@Path("articleId") String articleId, @Body UpdateViewCountRequest body);

    @POST("v1/comments/")
    Call<AddCommentResponse> addComment(@Body AddCommentRequest body);

    @PUT("v1/comments/{commentId}")
    Call<AddCommentResponse> editComment(@Path("commentId") String commentId,
                                         @Body AddCommentRequest body);

    @POST("v1/users/bookmark/")
    Call<AddBookmarkResponse> addBookmark(@Body ArticleDetailRequest body);

    @POST("v1/users/deleteBookmark/")
    Call<AddBookmarkResponse> deleteBookmark(@Body DeleteBookmarkRequest body);
}