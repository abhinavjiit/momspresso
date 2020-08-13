package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.SaveSearchQueryRequest;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchTrendsAndHistoryResponse;
import com.mycity4kids.tagging.MentionsResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hemant on 4/5/16.
 */
public interface SearchArticlesAuthorsAPI {

    @GET("/v2/search/")
    Call<SearchResponse> getAllSearchResult(@Query("q") String searchString,
            @Query("type") String type,
            @Query("start") int start,
            @Query("end") int end);

    @GET("/v2/search/")
    Call<SearchResponse> getSearchAuthorsResult(@Query("q") String searchString,
            @Query("type") String type,
            @Query("start") int start,
            @Query("end") int end);

    @GET("/v2/search/")
    Call<SearchResponse> getSearchTopicsResult(@Query("q") String searchString,
            @Query("type") String type,
            @Query("start") int start,
            @Query("end") int end,
            @Query("gc") int gc);

    @GET("/v1/search/usersearch")
    Call<SearchTrendsAndHistoryResponse> getSearchTrendAndHistory();

    @POST("/v1/search/insert")
    Call<ResponseBody> saveSearchQuery(@Body SaveSearchQueryRequest saveSearchQueryRequest);

    @GET("v2/search/users/mapping/")
    Call<MentionsResponse> searchUserHandles(@Query("q") String query);
}
