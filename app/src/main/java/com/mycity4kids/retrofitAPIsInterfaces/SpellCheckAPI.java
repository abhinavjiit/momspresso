package com.mycity4kids.retrofitAPIsInterfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SpellCheckAPI {

    @FormUrlEncoded
    @POST("bing/v7.0/spellcheck/")
    Call<ResponseBody> getSpellCheck(@Query("mode") String mode,
                                     @Query("mkt") String mkt,
                                     @Field("Text") String text);
}
