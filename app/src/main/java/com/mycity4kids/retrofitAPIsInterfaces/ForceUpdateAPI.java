package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.newmodels.ForceUpdateModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 6/5/16.
 */
public interface ForceUpdateAPI {
    @GET("apiservices/forceUpdate")
    Call<ForceUpdateModel> checkForceUpdateRequired(@Query("appVersion") String appVersion,
                                                    @Query("deviceType") String deviceType);
}
