package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.LoginRegistrationRequest;
import com.mycity4kids.models.response.UserHandleSuggestionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserHandleSuggestionAPI {

    @GET("v1/users/handle/suggestions/")
    Call<UserHandleSuggestionResponse> getSuggestion(@Query("firstName") String firstName,
            @Query("lastName") String lastName);
}
