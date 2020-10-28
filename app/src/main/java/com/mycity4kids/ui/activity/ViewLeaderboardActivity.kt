package com.mycity4kids.ui.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.LeaderboardDataResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerGoldAPI
import com.mycity4kids.ui.adapter.LeaderboardPagerAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.CustomTabsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewLeaderboardActivity : BaseActivity() {

    private lateinit var toolbarTitleTextView: TextView
    private lateinit var back: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private lateinit var leaderboardPagerAdapter: LeaderboardPagerAdapter
    private lateinit var checkout_growth_btn: TextView
    private var tabName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_leaderboard_activity)
        back = findViewById(R.id.back)
        viewpager = findViewById(R.id.viewpager)
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView)
        checkout_growth_btn = findViewById(R.id.checkout_growth_btn)
        tabs = findViewById(R.id.tabs)

        tabName = intent.getStringExtra("tab")

        checkout_growth_btn.visibility = View.VISIBLE
        tabs.apply {
            addTab(tabs.newTab().setText(R.string.all_blogs))
            addTab(tabs.newTab().setText(R.string.all_vlogs))
        }

        back.setOnClickListener {
            onBackPressed()
        }
        checkout_growth_btn.setOnClickListener {
            Utils.shareEventTracking(
                this,
                "BB Program Page",
                "BirthdayBonanza_Android",
                "LB_Hacks_BB"
            )
            launchChromeTabs(
                "https://" + AppUtils.getLanguage(
                    SharedPrefUtils.getAppLocale(
                        BaseApplication.getAppContext()
                    )
                ) + ".momspresso.com/birthdaybonanza/hack_to_get_more_page_views"
            )
        }
        showProgressDialog("please wait")
        getLeaderboardData()
    }

    private fun getLeaderboardData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
        val call = bloggerGoldAPI.getLeaderboardData(
            SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
            0,
            10,
            0
        )
        call.enqueue(leaderboardData)
    }

    private var leaderboardData = object : Callback<LeaderboardDataResponse> {
        override fun onFailure(call: Call<LeaderboardDataResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<LeaderboardDataResponse>,
            response: Response<LeaderboardDataResponse>
        ) {
            removeProgressDialog()
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data.result != null && responseData.data.result.isNotEmpty()) {
                        processLeaderboardDataResponse(responseData)
                    } else {
                        viewpager.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    private fun processLeaderboardDataResponse(response: LeaderboardDataResponse) {
        leaderboardPagerAdapter = LeaderboardPagerAdapter(
            response.data.result,
            supportFragmentManager
        )
        viewpager.adapter = leaderboardPagerAdapter

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }
        })
        if ("vlogs" == tabName) {
            viewpager.currentItem = 1
        }
    }

    private fun launchChromeTabs(deepLinkUrl: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            val packageName = CustomTabsHelper.getPackageNameToUse(this)
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName)
            }
            customTabsIntent.launchUrl(this, Uri.parse(deepLinkUrl))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
