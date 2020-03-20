package com.mycity4kids.sync

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
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.mycity4kids.R
import com.mycity4kids.preference.SharedPrefUtils

/**
 * Created by kapil.vij on 17-07-2015.
 */
class FetchAdvertisementInfoService :
    IntentService("FetchAdvertisementInfoService") {
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
        determineAdvertisingInfo()
    }

    private fun determineAdvertisingInfo() {
        try {
            val advertisingIdInfo =
                AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
            SharedPrefUtils.setAdvertisementId(applicationContext, advertisingIdInfo.id)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
