package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.ReportSpamRequest;
import com.mycity4kids.models.request.VlogsEventRequest;
import com.mycity4kids.ui.activity.ReportSpamActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportSpamAPI {

    @POST("http://eventsapi.momspresso.com/datapipeline/")
    Call<ResponseBody> reportSpam(@Body ReportSpamRequest reportSpamRequest);


}
