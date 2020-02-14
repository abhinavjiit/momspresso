package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hemant on 9/3/17.
 */
public interface SubscriptionsAPI {

    @GET("/v1/users/subscribe/")
    Call<SubscriptionSettingsResponse> getSubscriptionList(@Query("email") String email);

    @POST("/v1/users/subscribe/")
    Call<SubscriptionSettingsResponse> updateSubscriptions(@Body SubscriptionUpdateRequest body);
}
