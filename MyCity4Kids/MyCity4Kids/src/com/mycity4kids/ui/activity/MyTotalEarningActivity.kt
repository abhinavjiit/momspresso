package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.mycity4kids.models.campaignmodels.QuestionAnswerResponse
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.adapter.EarningRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList

class MyTotalEarningActivity : BaseActivity() {

    private lateinit var recyclerEarnings: RecyclerView
    private lateinit var earningRecyclerAdapter: EarningRecyclerAdapter
    private var totalPayout: Int = 0
    private lateinit var textTotalPayout: TextView
    private var totalPayoutResponse: ArrayList<AllCampaignTotalPayoutResponse.TotalPayoutResult> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_earning)

        totalPayout = intent.getIntExtra("totalPayout",0)

        textTotalPayout = findViewById(R.id.total_amount)
        recyclerEarnings = findViewById<RecyclerView>(R.id.recyclerEarnings)
        recyclerEarnings.layoutManager = LinearLayoutManager(this)

        textTotalPayout.text = "\u20b9 " + totalPayout
        earningRecyclerAdapter = EarningRecyclerAdapter(totalPayoutResponse,this)
        recyclerEarnings.adapter = earningRecyclerAdapter

        fetchAllCampaignPayout()

    }

    private fun fetchAllCampaignPayout() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(this)?.dynamoId
        val retro = BaseApplication.getInstance().campaignRetrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        val call = campaignAPI.getAllCampaignTotalPayout("0721da6e2e36482f813c2c9716fe8bdb")
        call.enqueue(getAllCampaignTotalPayout)
    }

    private val getAllCampaignTotalPayout = object : Callback<AllCampaignTotalPayoutResponse> {
        override fun onResponse(call: Call<AllCampaignTotalPayoutResponse>, response: retrofit2.Response<AllCampaignTotalPayoutResponse>) {
//            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data!!.size > 0) {
                        totalPayoutResponse.addAll(responseData.data.get(0).result)
                        earningRecyclerAdapter.notifyDataSetChanged()
                    }
                } else {
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<AllCampaignTotalPayoutResponse>, t: Throwable) {
//            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }


    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
