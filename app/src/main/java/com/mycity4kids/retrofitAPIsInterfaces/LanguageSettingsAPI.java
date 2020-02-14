package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.LanguageSettingsResponse;
import com.mycity4kids.models.response.PreferredLanguageUpdateRequest;
import com.mycity4kids.models.response.UpdateLanguageSettingsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hemant on 9/3/17.
 */
public interface LanguageSettingsAPI {

    @GET("/v2/users/subscribeLanguage/")
    Call<LanguageSettingsResponse> getLanguagesList();

    @POST("/v1/users/subscribeLanguage/")
    Call<UpdateLanguageSettingsResponse> updatePreferredLanguages(@Body PreferredLanguageUpdateRequest body);
}
