package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 3/5/16.
 */
public interface ArticleDetailsAPI {

    @GET("apiparentingstop/get_blog_detail")
    Call<ParentingDetailResponse> getArticleBody(@Query("article_id") String article_id,
                                                 @Query("user_id") String user_id,
                                                 @Query("app_version") String app_version);

    @GET("apiparentingstop/get_comment")
    Call<ResponseBody> getArticleComments(@Query("article_id") String article_id,
                                          @Query("limit") String limit,
                                          @Query("offset") String offset,
                                          @Query("comment_type") String comment_type,
                                          @Query("user_id") String user_id,
                                          @Query("app_version") String app_version);

}