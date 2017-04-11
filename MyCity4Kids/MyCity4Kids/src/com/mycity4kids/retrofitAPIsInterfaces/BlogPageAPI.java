package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.BlogDataResponse;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.SetupBlogResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by anshul on 4/26/16.
 */
public interface BlogPageAPI {
    @GET
    Call<BlogPageResponse> getBlogPage(@Url String url);

    @FormUrlEncoded
    @POST("/v1/users/blogPage/")
    Call<SetupBlogResponse> createBlogPage(
            @Field("blogTitle") String title,
            @Field("userBio") String userBio
    );
}
