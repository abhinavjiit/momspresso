package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchTopicResult;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 4/5/16.
 */
public interface SearchArticlesAuthorsAPI {

    @GET("/v1/search/find")
    Call<SearchResponse> getAllSearchResult(@Query("q") String searchString,
                                                 @Query("type") String type,
                                                 @Query("start") int start,
                                                 @Query("end") int end);

    @GET("/v1/search/find")
    Call<SearchResponse> getSearchArticlesResult(@Query("q") String searchString,
                                                 @Query("type") String type,
                                                 @Query("start") int start,
                                                 @Query("end") int end);

    //    @GET("apiparentingstop/searchV1")
    @GET("/v1/search/find")
    Call<SearchResponse> getSearchAuthorsResult(@Query("q") String searchString,
                                                @Query("type") String type,
                                                @Query("start") int start,
                                                @Query("end") int end);

    @GET("/v1/search/find")
    Call<SearchResponse> getSearchBlogsResult(@Query("q") String searchString,
                                              @Query("type") String type,
                                              @Query("start") int start,
                                              @Query("end") int end);

    @GET("/v1/search/find")
    Call<SearchResponse> getSearchTopicsResult(@Query("q") String searchString,
                                               @Query("type") String type,
                                               @Query("start") int start,
                                               @Query("end") int end);

    @GET("/v1/search/find")
    Call<SearchResponse> getContextualSearchResult(@Query("q") String searchString,
                                                   @Query("type") String type,
                                                   @Query("start") int start,
                                                   @Query("end") int end,
                                                   @Query("category") String categoryId);

    @GET("/v1/search/find")
    Call<SearchResponse> getContextualSearchResultForCity(@Query("q") String searchString,
                                                          @Query("type") String type,
                                                          @Query("start") int start,
                                                          @Query("end") int end,
                                                          @Query("city") String cityId);

}
