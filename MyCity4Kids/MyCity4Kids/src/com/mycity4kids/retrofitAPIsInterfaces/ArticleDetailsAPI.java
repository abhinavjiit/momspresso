package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailData;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.BaseResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<ArticleDetailData> getDemoArticle(@Path("article_id") String article_id);

    @POST("v1/users/isbookmark/")
    Call<ArticleDetailData> checkBookmarkStatus(@Body ArticleDetailRequest body);

    @GET
    Call<ResponseBody> getComments(@Url String url);

    @POST("v1/comments/{articleId}")
    Call<AddCommentResponse> addComment(@Path("articleId") String articleId);
}