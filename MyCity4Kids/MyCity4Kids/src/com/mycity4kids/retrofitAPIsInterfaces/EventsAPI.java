package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.businesseventdetails.DetailsResponse;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 29/9/16.
 */
public interface EventsAPI {
    @GET("apilistings/lists")
    Call<BusinessListResponse> getEventList(@Query("city_id") String cityId,
                                            @Query("category_id") String category_id,
                                            @Query("latitude") String latitude,
                                            @Query("longitude") String longitude,
                                            @Query("pincode") String pincode,
                                            @Query("user_id") String user_id,
                                            @Query("page") int pageNum);

    @GET("apiservices/detail")
    Call<DetailsResponse> getEventDetails(@Query("id") String id,
                                          @Query("categoryId") String categoryId,
                                          @Query("user_id") String user_id,
                                          @Query("type") String type,
                                          @Query("latitude") String latitude,
                                          @Query("longitude") String longitude,
                                          @Query("pincode") String pincode);
}
