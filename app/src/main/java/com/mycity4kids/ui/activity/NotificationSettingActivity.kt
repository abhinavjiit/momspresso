package com.mycity4kids.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.NotificationEnabledOrDisabledModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.NotificationCenterListResponse
import com.mycity4kids.models.response.NotificationCenterResult
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI
import com.mycity4kids.ui.adapter.NotificationCategoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationSettingActivity : BaseActivity(),
    NotificationCategoryAdapter.RecyclerViewClick {

    lateinit var backImageView: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var notificationCategoryAdapter: NotificationCategoryAdapter

    private var notificationCategoryListData = ArrayList<NotificationCenterResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_setting_activity)
        backImageView = findViewById(R.id.backImageView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notificationCategoryAdapter = NotificationCategoryAdapter(this)
        recyclerView.adapter = notificationCategoryAdapter
        notificationCategoryAdapter.setNotificationCategoryListData(notificationCategoryListData)
        notificationCategoryAdapter.notifyDataSetChanged()
        getAllNotificationCategories()
        backImageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getAllNotificationCategories() {
        showProgressDialog("please wait")
        val retrofit = BaseApplication.getInstance().retrofit
        val notificationApi = retrofit.create(NotificationsAPI::class.java)
        val call = notificationApi.allNotificationCategories
        call.enqueue(allNotificationCategoriesCallback)
    }

    private val allNotificationCategoriesCallback =
        object : Callback<NotificationCenterListResponse> {
            override fun onFailure(call: Call<NotificationCenterListResponse>, t: Throwable) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<NotificationCenterListResponse>,
                response: Response<NotificationCenterListResponse>
            ) {
                if (response.body() == null) {
                    return
                }
                try {
                    removeProgressDialog()
                    val res = response.body()
                    if (res?.code == 200 && res.status == Constants.SUCCESS) {
                        notificationCategoryListData = res.data.result
                        notificationCategoryAdapter.setNotificationCategoryListData(
                            notificationCategoryListData
                        )
                        notificationCategoryAdapter.notifyDataSetChanged()
                    }
                } catch (t: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            }
        }

    override fun onRecyclerClick(position: Int, id: String, notificationOn: Boolean) {
        val retrofit = BaseApplication.getInstance().retrofit
        val notificationApi = retrofit.create(NotificationsAPI::class.java)
        val notificationEnableRequestModel: NotificationEnabledOrDisabledModel
        if (notificationOn) {
            if(id =="10"){
                Utils.shareEventTracking(
                    this,
                    "Notification Settings",
                    "WhatsappSubscription_Android",
                    "NotifSettings_On_WS"
                )
            }
            notificationEnableRequestModel =
                NotificationEnabledOrDisabledModel(id.toInt(), enabled = false)
            notificationCategoryListData[position].disabled = false
        } else {
            notificationEnableRequestModel =
                NotificationEnabledOrDisabledModel(id.toInt(), enabled = true)
            notificationCategoryListData[position].disabled = true
        }
        notificationCategoryAdapter.setNotificationCategoryListData(notificationCategoryListData)
        notificationCategoryAdapter.notifyDataSetChanged()
        val call = notificationApi.enableOrDisableNotification(notificationEnableRequestModel)
        call.enqueue(object : Callback<BaseResponseGeneric<NotificationEnabledOrDisabledModel>> {
            override fun onFailure(
                call: Call<BaseResponseGeneric<NotificationEnabledOrDisabledModel>>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<BaseResponseGeneric<NotificationEnabledOrDisabledModel>>,
                response: Response<BaseResponseGeneric<NotificationEnabledOrDisabledModel>>
            ) {
                if (response.body() == null) {
                    return
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success updated") {
                        Log.d("TAG", res.status.toString())
                    }
                } catch (t: Exception) {

                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            }
        })
    }
}
