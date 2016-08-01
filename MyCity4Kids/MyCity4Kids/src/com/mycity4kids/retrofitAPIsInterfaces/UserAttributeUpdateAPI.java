package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.request.UpdateUserDetail;
import com.mycity4kids.models.response.UserDetailResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by anshul on 7/1/16.
 */
public interface UserAttributeUpdateAPI  {
    @PUT("v1/users/profilePic/")
   // Call<UserDetailResponse> updateProfilePic(@Part("photo") RequestBody photo, @Part("description") RequestBody description);
    Call<UserDetailResponse> updateProfilePic(@Body UpdateUserDetail body);



    @PUT("v1/users/")
    Call<UserDetailResponse> updateProfile( @Body UpdateUserDetail body);
}
