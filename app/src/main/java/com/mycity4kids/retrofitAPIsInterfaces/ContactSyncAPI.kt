package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.request.PhoneContactRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ContactSyncAPI {

    @PUT("/v1/users/phonebook/")
    fun syncContacts(@Body phoneContactRequest: PhoneContactRequest): Call<ResponseBody>

    @POST("/v1/articles/sms/")
    fun sendInvite(@Body phoneContactRequest: PhoneContactRequest): Call<ResponseBody>
}
