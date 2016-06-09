package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 3/5/16.
 */
public interface TopicsCategoryAPI {

    @GET("apiparentingstop/getCategoryList")
    Call<TopicsResponse> getTopicsCategory(@Query("user_id") String user_id);

    @GET("apiparentingstop/searchV1")
    Call<CommonParentingResponse> filterCategories(@Query("q") String searchParam,
                                          @Query("type") String type,
                                          @Query("page") int pageNum);
}