package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.ContributorListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by anshul on 8/7/16.
 */
public interface ContributorListAPI {
    @GET
    Call<ContributorListResponse> getContributorList(@Url String url);

    @GET("v1/users/")
    Call<ContributorListResponse> getContributorList(@Query("limit") int limit,
                                                     @Query("sortType") int sortType,
                                                     @Query("type") String type,
                                                     @Query("lang") String lang,
                                                     @Query("pagination") String pagination);

}
