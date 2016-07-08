package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    @GET("https://s3-ap-northeast-1.amazonaws.com/microservices-sync-test/articles-data/article-2535488b90f248eab3e6cabcbf9b4468.json")
    Call<ArticleDetailResponse> getDemoArticle(@Query("article_id") String article_id,
                                               @Query("user_id") String user_id,
                                               @Query("app_version") String app_version);

}