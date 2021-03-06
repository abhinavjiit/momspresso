package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.UserDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

/**
 * Created by anshul on 7/1/16.
 */
public interface UserAttributeUpdateAPI {
    @PUT("v1/users/profilePic/")
    Call<UserDetailResponse> updateProfilePic(@Body UpdateUserDetailsRequest body);

    @PUT("v1/users/")
    Call<UserDetailResponse> updateProfile(@Body UpdateUserDetailsRequest body);

    @PUT("v1/users/")
    Call<ResponseBody> updateBlogProfile(@Body UpdateUserDetailsRequest body);

    @PUT("v1/users/cityId/")
    Call<UserDetailResponse> updateCity(@Body UpdateUserDetailsRequest body);

    @PUT("v1/users/")
    Call<UserDetailResponse> updateCityAndKids(@Body UpdateUserDetailsRequest body);

    @PUT("v1/users/")
    Call<UserDetailResponse> updateSubscriptionEmail(@Body UpdateUserDetailsRequest body);
}
