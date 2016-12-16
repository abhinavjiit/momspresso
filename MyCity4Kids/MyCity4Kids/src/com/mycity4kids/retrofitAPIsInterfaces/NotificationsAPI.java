package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.NotificationSettingsRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleDetailResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by hemant on 7/12/16.
 */
public interface NotificationsAPI {

    @GET("/v1/users/settings/notifications/")
    Call<NotificationSettingsResponse> getNotificationsStatus();

    @PUT("/v1/users/settings/notifications/")
    Call<NotificationSettingsResponse> updateNotificationSettings(@Body HashMap<String, String> body);
}
