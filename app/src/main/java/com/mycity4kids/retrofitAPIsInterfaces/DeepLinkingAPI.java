package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.DeepLinkingResposnse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by anshul on 8/23/16.
 */
public interface DeepLinkingAPI {

    @GET("v1/articles/urlLinking/")
    Call<DeepLinkingResposnse> getUrlDetails(@Query("url") String url);
}
