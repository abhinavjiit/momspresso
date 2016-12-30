package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.models.response.UserTypeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by anshul on 7/12/16.
 */
public interface ConfigAPIs {
    @GET("v1/utilities/config/")
    Call<ConfigResponse> getConfig();

    @GET("v1/utilities/config/userType/")
    Call<UserTypeResponse> getAllUserType();
}
