package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.BlogDataResponse;
import com.mycity4kids.models.forgot.CommonResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by anshul on 4/26/16.
 */
public interface BlogPageAPI {
    @FormUrlEncoded
    @POST("apiblogs/getBlogPage")
    Call<BlogDataResponse> getBlogPage(@Field("userId") String userId,
                                        @Field("sourceId") String sourceId);

    @FormUrlEncoded
    @POST("apiblogs/createUpdateBlogPage")
    Call<CommonResponse> createBlogPage(@Field("userId") String userId,
                                          @Field("title") String title,
                                          @Field("userBio") String userBio,
                                          @Field("imageName") String imageName,
                                          @Field("sourceId") String sourceId);
}
