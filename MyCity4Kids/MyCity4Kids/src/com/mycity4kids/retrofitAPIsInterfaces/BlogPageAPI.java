package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BlogPageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by anshul on 4/26/16.
 */
public interface BlogPageAPI {
    @GET
    Call<BlogPageResponse> getBlogPage(@Url String url);

    @GET("/v1/users/blogPage/{userId}")
    Call<BlogPageResponse> getUserBlogPage(@Path("userId") String userId);
}
