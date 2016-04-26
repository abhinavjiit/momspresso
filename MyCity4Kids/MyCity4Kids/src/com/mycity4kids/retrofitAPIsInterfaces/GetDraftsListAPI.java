package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftListResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by anshul on 4/22/16.
 */
public interface GetDraftsListAPI {
    @FormUrlEncoded
    @POST("apiblogs/getDraftLists")
    Call<ArticleDraftListResponse> getDraftsList(@Field("userId") String userId);
                                              /*   @Field("title") String title,
                                                 @Field("body") String body,
                                                 @Field("id") String id,
                                                 @Field("draftId") String draftId,
                                                 @Field("imageUrl") String imageUrl,
                                                 @Field("sourceId") String sourceId,
                                                 @Field("moderationStatus") String moderationStatus,
                                                 @Field("nodeId") String nodeId);*/
}
