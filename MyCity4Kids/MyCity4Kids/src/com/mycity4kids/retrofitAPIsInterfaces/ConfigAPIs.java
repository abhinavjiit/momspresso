package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.UserTypeResponse;
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by anshul on 7/12/16.
 */
public interface ConfigAPIs {
    @GET("v1/utilities/config/")
    Call<ConfigResponse> getConfig();

    @GET("v1/utilities/config/userType/")
    Call<UserTypeResponse> getAllUserType();

    @GET("v1/utilities/config/cityType/")
    Call<CityConfigResponse> getCityConfig();

    @GET("v1/utilities/config/cityType/")
    Observable<BaseResponseGeneric<CityConfigResultResponse>> getCityConfigRx();
}
