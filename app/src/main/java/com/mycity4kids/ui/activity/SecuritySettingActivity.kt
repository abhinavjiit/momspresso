package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.models.UserTaggableModel
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.utils.ToastUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecuritySettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var backImageView: ImageView
    private lateinit var switchTextView: SwitchCompat
    private lateinit var blockUserRightArrowImageView: ImageView
    private lateinit var blockUserTextView: TextView
    private var isTaggable = false
    val a = emptyArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.security_setting_activity)

        backImageView = findViewById(R.id.backImageView)
        switchTextView = findViewById(R.id.switchTextView)
        blockUserRightArrowImageView = findViewById(R.id.blockUserRightArrowImageView)
        blockUserTextView = findViewById(R.id.blockUserTextView)
        getBloggerData()
        backImageView.setOnClickListener {
            onBackPressed()
        }
        blockUserRightArrowImageView.setOnClickListener(this)
        switchTextView.setOnClickListener(this)
    }

    private fun getBloggerData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerDashBoardApi = retrofit.create(BloggerDashboardAPI::class.java)
        val call =
            bloggerDashBoardApi.getBloggerData(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
        call.enqueue(bloggerDataCallBack)
    }

    private val bloggerDataCallBack = object : Callback<UserDetailResponse> {
        override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {

            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<UserDetailResponse>,
            response: Response<UserDetailResponse>
        ) {
            if (response.body() == null) {
                return
            }

            try {
                val resData = response.body()
                if (resData?.data?.get(0)?.result?.isTaggable == "1") {
                    switchTextView.isChecked = true
                    isTaggable = true
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.switchTextView -> {
                val ret = BaseApplication.getInstance().retrofit
                val bloggerDashboardAPI = ret.create(BloggerDashboardAPI::class.java)
                val userTaggableModel: UserTaggableModel
                if (isTaggable) {
                    userTaggableModel = UserTaggableModel(isTaggable = "0")
                    isTaggable = false
                } else {
                    userTaggableModel = UserTaggableModel(isTaggable = "1")
                    isTaggable = true
                }
                val call = bloggerDashboardAPI.updateUserTaggableSetting(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    userTaggableModel
                )
                call.enqueue(updateUserTaggableCallBack)
            }
            R.id.blockUserRightArrowImageView -> {
                val intent =
                    Intent(this@SecuritySettingActivity, BlockUnBlockUserActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private val updateUserTaggableCallBack = object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
            ToastUtils.showToast(this@SecuritySettingActivity, "something went wrong")
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        }
    }
}
