package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 4/5/16.
 */
public interface SearchArticlesAuthorsAPI {

    @GET("apiparentingstop/searchV1")
    Call<CommonParentingResponse> getSearchArticlesResult(@Query("q") String searchString,
                                                          @Query("type") String type,
                                                          @Query("page") String page);

    @GET("apiparentingstop/searchV1")
    Call<ParentingBlogResponse> getSearchAuthorsResult(@Query("q") String searchString,
                                                          @Query("type") String type,
                                                          @Query("page") String page);
}
