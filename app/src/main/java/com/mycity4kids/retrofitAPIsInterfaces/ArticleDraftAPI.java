package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.SaveDraftRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;

import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by anshul on 4/29/16.
 */
public interface ArticleDraftAPI {
    @FormUrlEncoded
    @POST("v1/articles/")
    Call<ArticleDraftResponse> saveDraft(
            @Field("title") String title,
            @Field("body") String body,
            @Field("articleType") String articleType,
            @Field("userAgent1") String userAgent1,
            @Field("tags") List<Map<String, String>> tags);

    @PUT
    Call<ArticleDraftResponse> updateDrafts(@Url String url,
                                            @Body SaveDraftRequest saveDraftRequest);

    @GET("v1/articles/")
    Call<ResponseBody> getDraftsList(@Query("aType") String aType);

    @DELETE
    Call<ArticleDraftResponse> deleteDraft(@Url String url);

    @GET("v1/articles/drafts/")
    Call<ResponseBody> getAllDrafts(@Query("aType") String aType);
}
