package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.forgot.CommonResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by anshul on 4/22/16.
 */
public interface ImageUploadAPI {
    @Multipart
    @POST("apiblogs/uploadImage")
    Call<CommonResponse> uploadImage(
            @Part("user_id") RequestBody user_id,
            @Part("imageType") RequestBody imageType,
            @Part("file") RequestBody image);
}
