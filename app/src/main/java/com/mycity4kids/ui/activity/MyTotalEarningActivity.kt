package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.campaign.adapter.EarningRecyclerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsShareReferralCodeActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class MyTotalEarningActivity : BaseActivity() {

    private lateinit var recyclerEarnings: RecyclerView
    private lateinit var earningRecyclerAdapter: EarningRecyclerAdapter
    private var totalPayout: Int = 0
    private lateinit var textTotalPayout: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var startCampaign: TextView
    private lateinit var startCampaignText: TextView
    private lateinit var backIcon: ImageView
    private lateinit var paymentHistory: TextView
    private lateinit var referEarnContainerCL: ConstraintLayout
    private var totalPayoutResponse: ArrayList<AllCampaignTotalPayoutResponse.TotalPayoutResult> = arrayListOf()
    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_earning)
        root = findViewById(R.id.root)
        (application as BaseApplication).view = root
        (application as BaseApplication).activity = this

        totalPayout = intent.getIntExtra("totalPayout", 0)

        textTotalPayout = findViewById(R.id.total_amount)
        recyclerEarnings = findViewById(R.id.recyclerEarnings)
        profileImageView = findViewById(R.id.profileImageView)
        startCampaign = findViewById(R.id.start_campaign_cta)
        startCampaignText = findViewById(R.id.start_campaign_text)
        referEarnContainerCL = findViewById(R.id.referEarnContainerCL)
        recyclerEarnings.layoutManager = LinearLayoutManager(this)

        paymentHistory = findViewById(R.id.payment_history)
        backIcon = findViewById(R.id.back)
        textTotalPayout.text = "\u20b9" + totalPayout
        earningRecyclerAdapter = EarningRecyclerAdapter(totalPayoutResponse, this)
        recyclerEarnings.adapter = earningRecyclerAdapter

        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))) {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext())).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).into(profileImageView)
        }

        backIcon.setOnClickListener {
            finish()
        }

        startCampaign.setOnClickListener {
            val intent = Intent(this, CampaignContainerActivity::class.java)
            startActivity(intent)
        }

        referEarnContainerCL.setOnClickListener {
            Utils.pushGenericEvent(this, "CTA_MyMoney_Earning_Screen_Refer",
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, "MyTotalEarningActivity")
            val intent = Intent(this, RewardsShareReferralCodeActivity::class.java)
            startActivity(intent)
        }

        fetchAllCampaignPayout()

    }

    private fun fetchAllCampaignPayout() {
        showProgressDialog(resources.getString(R.string.please_wait))
        var userId = SharedPrefUtils.getUserDetailModel(this)?.dynamoId
        val retro = BaseApplication.getInstance().retrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        val call = campaignAPI.getAllCampaignTotalPayout(userId)
        call.enqueue(getAllCampaignTotalPayout)
    }

    private val getAllCampaignTotalPayout = object : Callback<AllCampaignTotalPayoutResponse> {
        override fun onResponse(call: Call<AllCampaignTotalPayoutResponse>, response: retrofit2.Response<AllCampaignTotalPayoutResponse>) {
            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data.get(0).result.size > 0) {
                        totalPayoutResponse.addAll(responseData.data.get(0).result)
                        earningRecyclerAdapter.notifyDataSetChanged()
                        startCampaign.visibility = View.GONE
                        startCampaignText.visibility = View.GONE
                        recyclerEarnings.visibility = View.VISIBLE
                        paymentHistory.visibility = View.VISIBLE
                    } else {
                        recyclerEarnings.visibility = View.GONE
                        paymentHistory.visibility = View.GONE
                        startCampaign.visibility = View.VISIBLE
                        startCampaignText.visibility = View.VISIBLE
                    }
                } else {
                    recyclerEarnings.visibility = View.GONE
                    paymentHistory.visibility = View.GONE
                    startCampaign.visibility = View.VISIBLE
                    startCampaignText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<AllCampaignTotalPayoutResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }
}
