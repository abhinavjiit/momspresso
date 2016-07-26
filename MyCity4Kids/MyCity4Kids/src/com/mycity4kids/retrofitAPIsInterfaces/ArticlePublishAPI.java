package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.response.ArticleDraftResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by anshul on 4/20/16.
 */
public interface ArticlePublishAPI  {
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
    @FormUrlEncoded
    @POST("192.168.1.14/v1/articles/")
    Call<ArticleDraftResponse> publishArticle(
                                                 @Field("title") String title,
                                                 @Field("body") String body,
                                                 @Field("tags") String tag,
                                                 @Field("imageUrl") String imageUrl,
                                                 @Field("articleType") String articleType);


    @FormUrlEncoded
    @PUT
    Call<ArticleDraftResponse> updateArticle(@Url String url,
                                           @Field("title") String title,
                                           @Field("body") String body,
                                           @Field("tags") String tag,
                                           @Field("imageUrl") String imageUrl,
                                           @Field("articleType") String articleType
    );

}
