package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.NotificationEnabledOrDisabledModel;
import com.mycity4kids.models.request.NotificationReadRequest;
import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 7/12/16.
 */
public interface NotificationsAPI {

    @GET("/v1/users/settings/notifications/")
    Call<NotificationSettingsResponse> getNotificationsStatus();

    @PUT("/v1/users/settings/notifications/")
    Call<NotificationSettingsResponse> updateNotificationSettings(@Body HashMap<String, String> body);

    @GET("/v2/notifications/{userId}")
    Call<NotificationCenterListResponse> getNotificationCenterList(@Path("userId") String userId,
            @Query("limit") int limit,
            @Query("pagination") String pagination);

    @GET("/v1/notifications/centre/{userId}")
    Call<NotificationCenterListResponse> getUnreadNotificationCount(@Path("userId") String userId);

    @PUT("/v1/notifications/centre/")
    Call<NotificationCenterListResponse> markNotificationAsRead(@Body NotificationReadRequest body);

    @GET("/subscriptions/notification/subscription_types")
    Call<NotificationCenterListResponse> getAllNotificationCategories();


    @POST("/subscriptions/opt-out")
    Call<BaseResponseGeneric<NotificationEnabledOrDisabledModel>> enableOrDisableNotification(
            @Body NotificationEnabledOrDisabledModel body);
}
