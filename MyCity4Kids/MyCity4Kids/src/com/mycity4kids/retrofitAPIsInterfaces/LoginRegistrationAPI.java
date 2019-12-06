package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddEditKidsInformationRequest;
import com.mycity4kids.models.request.ChangePasswordRequest;
import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.PhoneLoginRequest;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.ChangePasswordResponse;
import com.mycity4kids.models.response.FBPhoneLoginResponse;
import com.mycity4kids.models.response.ForgotPasswordResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.ui.activity.CustomSignUpActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @PUT("v1/users/kid/")
    Call<UserDetailResponse> addEditKidsInformation(@Body AddEditKidsInformationRequest body);

    @PUT("v1/users/socialTokens/")
    Call<BaseResponse> socialConnect(@Body SocialConnectRequest body);

    @POST("v1/users/loginfbnumber")
    Call<FBPhoneLoginResponse> loginWithPhone(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("v1/smsapi/")
    Call<ResponseBody> triggerSMS(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("v1/smsapi/verify/")
    Call<ResponseBody> verifySMS(@Body PhoneLoginRequest phoneLoginRequest);

    @POST("v1/users/loginmobilenumber/")
    Call<FBPhoneLoginResponse> loginWithPhoneToken(@Body PhoneLoginRequest phoneLoginRequest);
}