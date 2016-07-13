package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by anshul on 4/29/16.
 */
public interface ArticleDraftAPI {
    @FormUrlEncoded
    @POST("v1/drafts/")
    Call<BaseResponse> draftArticle(
            @Field("title") String title,
            @Field("body") String body,
            @Field("id") String id,
            @Field("status") String status,
            @Field("sourceId") String sourceId);

    @FormUrlEncoded
    @POST("v1/articles/")
    Call<DraftResponse> saveDraft(
            @Field("title") String title,
            @Field("body") String body,
            @Field("articleType") String articleType);

    @FormUrlEncoded
    @PUT
    Call<DraftResponse> updateDraft(@Url String url,
                                    @Field("title") String title,
                                    @Field("body") String body,
                                    @Field("articleType") String articleType
    );


    @GET
    Call<DraftListResponse> getDraftsList(@Url String url);
    @DELETE
    Call<DraftResponse> deleteDraft(@Url String url);
}
