package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.ForYouArticleRemoveRequest;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ForYourArticleRemoveResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 26/12/16.
 */
public interface RecommendationAPI {

    @GET("v1/recommendations/v2/{userId}")
    Call<ArticleListingResponse> getRecommendedArticlesList(@Path("userId") String userId,
                                                            @Query("size") int limit,
                                                            @Query("chunks") String chunks,
                                                            @Query("lang") String lang);

    @POST("/v1/articles/removeForYouRelation/")
    Call<ForYourArticleRemoveResponse> removeFromForYouFeed(@Body ForYouArticleRemoveRequest body);
}
