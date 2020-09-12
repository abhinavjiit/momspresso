package com.mycity4kids.sync

import android.accounts.NetworkErrorException
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by kapil.vij on 17-07-2015.
 */
class FetchPublicIpAddressService :
    IntentService("FetchPublicIpAddressService") {
    override fun onCreate() {
        super.onCreate()
        var channelId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service")
        }
        val builder =
            NotificationCompat.Builder(this, channelId)
        val notification = builder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_notify)
            .setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE).build()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        return channelId
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.e("determineAdvertising", "determineAdvertisingInfo")
        determinePublicIpAdress()
    }

    private fun determinePublicIpAdress() {
        try {
            val retrofit = BaseApplication.getInstance().retrofit
            val articleDetailsApi = retrofit.create(LoginRegistrationAPI::class.java)
            val call: Call<ResponseBody> = articleDetailsApi.publicIpAddress
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.body() == null) {
                        val nee =
                            NetworkErrorException(response.raw().toString())
                        FirebaseCrashlytics.getInstance().recordException(nee)
                        return
                    }
                    try {
                        if (response.isSuccessful) {
                            val resData = String(response.body()!!.bytes())
                            SharedPrefUtils.setPublicIpAddress(applicationContext, resData)
                        }
                    } catch (e: java.lang.Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
