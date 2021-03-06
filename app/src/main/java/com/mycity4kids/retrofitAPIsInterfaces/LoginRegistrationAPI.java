package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.ChangePasswordRequest;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.PhoneLoginRequest;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.ChangePasswordResponse;
import com.mycity4kids.models.response.FBPhoneLoginResponse;
import com.mycity4kids.models.response.ForgotPasswordResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.response.UserHandleResult;
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse;
import com.mycity4kids.ui.activity.CustomSignUpActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 3/5/16.
 */
public interface LoginRegistrationAPI {

    @POST("v1/users/")
    Call<UserDetailResponse> login(@Body LoginRegistrationRequest body);

    @POST("v1/users/")
    Call<UserDetailResponse> customRegistration(@Body CustomSignUpActivity.RegistrationRequest body);

    @PUT("v1/users/email/")
    Call<UserDetailResponse> addFacebookEmail(@Body LoginRegistrationRequest body);

    @GET("v1/users/{userId}")
    Call<UserDetailResponse> getUserDetails(@Path("userId") String userId);

    @PUT("v2/users/updatePassword/")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest body);

    @POST("v1/users/link/password/")
    Call<ForgotPasswordResponse> resetPassword(@Body LoginRegistrationRequest body);

    @POST("v1/users/link/email/")
    Call<UserDetailResponse> resendVerificationLink(@Body LoginRegistrationRequest body);

    @PUT("v1/users/socialTokens/")
    Call<BaseResponse> socialConnect(@Body SocialConnectRequest body);

    @POST("v1/smsapi/")
    Call<ResponseBody> triggerSMS(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("v1/smsapi/verify/")
    Call<ResponseBody> verifySMS(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("v1/users/loginmobilenumber/")
    Call<FBPhoneLoginResponse> loginWithPhoneToken(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("apiusers/logoutV1/")
    Call<ResponseBody> logout();

    @GET("apiusers/updatePushTokenV1")
    Call<ResponseBody> updatePushToken(@Query("userId") String userId,
            @Query("dynamoId") String dynamoId,
            @Query("app_version") String app_version,
            @Query("deviceType") String deviceType,
            @Query("cityId") int cityId,
            @Query("pushToken") String pushToken,
            @Query("fcmToken") String fcmToken);

    @PUT("/v2/users/{userId}")
    Call<RewardsPersonalResponse> updateUserDetails(@Path("userId") String userId,
            @Body UserDetailResult userDetailResult);

    @GET("https://api.ipify.org")
    Call<ResponseBody> getPublicIpAddress();
}