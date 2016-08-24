package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogDetailWithArticleModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by anshul on 8/23/16.
 */
public interface DeepLinkingAPI {

    @GET("v1/articles/urlLinking/")
    Call<DeepLinkingResposnse> getUrlDetails(@Query("url") String url);
}
