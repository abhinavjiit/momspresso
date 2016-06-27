package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.VerifyEmailRequest;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by hemant on 3/5/16.
 */
public interface LoginRegistrationAPI {

    @POST("v1/users/")
    Call<UserDetailResponse> login(@Body LoginRegistrationRequest body);

    @PUT("v1/users/email/")
    Call<UserDetailResponse> verifyEmail(@Body VerifyEmailRequest body);

    @GET("v1/users/{userId}")
    Call<UserDetailResponse> getUserDetails(@Path("userId") String userId);
}