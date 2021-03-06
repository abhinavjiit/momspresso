package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleTagsImagesResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anshul on 4/20/16.
 */
public interface ArticlePublishAPI {
    @POST("v1/articles/")
    Call<ArticleDraftResponse> publishArticle(@Body ArticleDraftRequest body);

    @GET("v1/resources/images/")
    Call<ArticleTagsImagesResponse> getImagesForCategories(@Query("tags") String tags,
                                                           @Query("limit") int limit,
                                                           @Query("page") int page);

    @PUT("v1/articles/{articleId}")
    Call<ArticleDraftResponse> updateArticle(@Path("articleId") String articleId,
                                             @Body ArticleDraftRequest body);

    @GET("v2/categories/")
    Call<ResponseBody> getArticleChallenges(@Query("id") String categoryId);
}
