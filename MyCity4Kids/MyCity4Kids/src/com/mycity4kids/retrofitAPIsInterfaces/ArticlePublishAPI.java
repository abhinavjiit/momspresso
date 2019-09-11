package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.Topics;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleTagsImagesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anshul on 4/20/16.
 */
public interface ArticlePublishAPI {
    @FormUrlEncoded
    @POST("apiblogs/createUpdateArticle")
    Call<ParentingDetailResponse> publishArticle1(@Field("userId") String userId,
                                                  @Field("title") String title,
                                                  @Field("body") String body,
                                                  @Field("id") String id,
                                                  @Field("draftId") String draftId,
                                                  @Field("tag") String tag,
                                                  @Field("imageUrl") String imageUrl,
                                                  @Field("sourceId") String sourceId,
                                                  @Field("moderationStatus") String moderationStatus,
                                                  @Field("nodeId") String nodeId);

    @POST("v1/articles/")
    Call<ArticleDraftResponse> publishArticle(@Body ArticleDraftRequest body);

    @GET("v1/resources/images/")
    Call<ArticleTagsImagesResponse> getImagesForCategories(@Query("tags") String tags,
                                                           @Query("limit") int limit,
                                                           @Query("page") int page);

    @PUT("v1/articles/{articleId}")
    Call<ArticleDraftResponse> updateArticle(@Path("articleId") String articleId,
                                             @Body ArticleDraftRequest body);

    @POST("v2/categories/")
    Call<Topics> getArticleChallenges(@Query("id") String categoryId);
}
