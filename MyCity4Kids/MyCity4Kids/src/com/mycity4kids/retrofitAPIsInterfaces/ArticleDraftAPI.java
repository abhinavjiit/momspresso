package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by anshul on 4/29/16.
 */
public interface ArticleDraftAPI {
    @FormUrlEncoded
    @POST("apiblogs/createUpdateDraft")
    Call<ParentingDetailResponse> draftArticle(@Field("userId") String userId,
                                                 @Field("title") String title,
                                                 @Field("body") String body,
                                                 @Field("id") String id,
                                                 @Field("status") String status,
                                                 @Field("sourceId") String sourceId);

    @FormUrlEncoded
    @POST("apiblogs/getDraftLists")
    Call<ResponseBody> getDraftsList(@Field("userId") String userId);
}
