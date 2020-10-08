package com.mycity4kids.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.AllLeaderboardDataResponse
import com.mycity4kids.retrofitAPIsInterfaces.BloggerGoldAPI
import com.mycity4kids.ui.adapter.AllLeaderboardPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAllLeaderboardActivity : BaseActivity() {

    private lateinit var toolbarTitleTextView: TextView
    private lateinit var back: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private lateinit var allLeaderboardPagerAdapter: AllLeaderboardPagerAdapter
    private lateinit var allBlogLeaderData: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>
    private lateinit var allVlogLeaderData: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_leaderboard_activity)
        back = findViewById(R.id.back)
        viewpager = findViewById(R.id.viewpager)
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView)
        tabs = findViewById(R.id.tabs)

        tabs.apply {
            addTab(tabs.newTab().setText(R.string.leaderboard_blogs))
            addTab(tabs.newTab().setText(R.string.leaderboard_vlogs))
        }

        back.setOnClickListener {
            onBackPressed()
        }
        showProgressDialog("please wait")
        getALLBlogLeaderboardData()
    }

    private fun getAllVlogLeaderboardData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
        val call = bloggerGoldAPI.getAllLeaderboardData(
            0,
            2
        )
        call.enqueue(allVlogLeaderboardData)
    }

    private fun getALLBlogLeaderboardData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
        val call = bloggerGoldAPI.getAllLeaderboardData(
            0,
            0
        )
        call.enqueue(allBlogLeaderboardData)
    }


    private var allBlogLeaderboardData = object : Callback<AllLeaderboardDataResponse> {
        override fun onFailure(call: Call<AllLeaderboardDataResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<AllLeaderboardDataResponse>,
            response: Response<AllLeaderboardDataResponse>
        ) {
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    allBlogLeaderData = responseData.data.result
                    getAllVlogLeaderboardData()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    private var allVlogLeaderboardData = object : Callback<AllLeaderboardDataResponse> {
        override fun onFailure(call: Call<AllLeaderboardDataResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<AllLeaderboardDataResponse>,
            response: Response<AllLeaderboardDataResponse>
        ) {
            removeProgressDialog()
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    allVlogLeaderData = responseData.data.result
                    setAdapter()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    private fun setAdapter() {
        allLeaderboardPagerAdapter = AllLeaderboardPagerAdapter(
            allBlogLeaderData,
            allVlogLeaderData,
            supportFragmentManager
        )
        viewpager.adapter = allLeaderboardPagerAdapter

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
    }
}
