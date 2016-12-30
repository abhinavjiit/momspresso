package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.ArticleReadTimeRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateViewCountRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.ViewCountResponse;

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

    @GET("https://s3-ap-southeast-1.amazonaws.com/mycity4kids-phoenix/articles-data/{articleId}.json")
    Call<ArticleDetailResult> getArticleDetailsFromS3(@Path("articleId") String articleId);

//    For Local JSON Testing
//    @GET("http://10.0.0.27/test/article-fa3ae55a274040688a4370b055e6f8d6.json")
//    Call<ArticleDetailResult> getArticleDetailsFromLocal();

    @GET("/v1/articles/doc/")
    Call<ArticleDetailResponse> getArticleDetailsFromWebservice(@Query("articleId") String articleId);

    @GET("v1/users/checkFollowingBookmarkStatus/")
    Call<ArticleDetailResponse> checkFollowingBookmarkStatus(@Query("articleId") String articleId,
                                                             @Query("authorId") String authorId);

    @GET("v1/articles/user/{userId}")
    Call<ArticleListingResponse> getPublishedArticles(@Path("userId") String userId,
                                                      @Query("sort") int authorId,
                                                      @Query("start") int start,
                                                      @Query("end") int end);

    @GET
    Call<ResponseBody> getComments(@Url String url);

    @GET("v1/articles/views/{articleId}")
    Call<ViewCountResponse> getViewCount(@Path("articleId") String articleId);

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

    @POST("v1/users/deleteBookmark/")
    Call<AddBookmarkResponse> deleteBookmark(@Body DeleteBookmarkRequest body);

    @GET("v1/recommend/related/{articleId}")
    Call<ArticleListingResponse> getCategoryRelatedArticles(@Path("articleId") String articleId);

    @GET("v1/users/likes/{articleId}")
    Call<ArticleRecommendationStatusResponse> getArticleRecommendedStatus(@Path("articleId") String articleId);

    @PUT("v1/users/likes/")
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(@Body RecommendUnrecommendArticleRequest body);

    @POST("v1/articles/chronos/")
    Call<ResponseBody> updateArticleTimeSpent(@Body ArticleReadTimeRequest body);
}