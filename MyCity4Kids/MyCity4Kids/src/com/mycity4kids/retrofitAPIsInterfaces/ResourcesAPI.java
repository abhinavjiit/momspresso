package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by anshul on 5/10/16.
 */
public interface ResourcesAPI {
    @FormUrlEncoded
    @POST("apilistings/add_or_remove_bookmark")
    Call<CommonResponse> addRemoveBookmark(@Field("user_id") String user_id,
                                           @Field("ar") String ar,
                                           @Field("res_id") String res_id);
    


}
