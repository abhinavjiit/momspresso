package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.BlogDataResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by anshul on 4/26/16.
 */
public interface GetBlogPageAPI {
    @FormUrlEncoded
    @POST("apiblogs/getBlogPage")
    Call<BlogDataResponse> getBlogPage(@Field("userId") String userId,
                                        @Field("sourceId") String sourceId);
}
