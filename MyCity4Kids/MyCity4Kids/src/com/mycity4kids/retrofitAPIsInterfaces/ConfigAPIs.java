package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.UserTypeResponse;
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @GET("badges/")
    Call<ResponseBody> getBadges(@Query("user_id") String userId);


    @GET("http://testingapi.momspresso.com/v1/collections/user/{userId}?start=0&offset=20")
    Call<ResponseBody> getCollections(@Path("userId") String userId,
                                      @Query("start") int start,
                                      @Query("offset") int offset);

    @GET("v1/utilities/config/cityType/")
    Observable<BaseResponseGeneric<CityConfigResultResponse>> getCityConfigRx();
}
