package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.activity.MyTotalEarningActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsShareReferralCodeActivity
import retrofit2.Call
import retrofit2.Callback

class RewardsTabFragment : BaseFragment() {
    private lateinit var textStartReward: TextView
    private lateinit var containerView: View
    private lateinit var textPersonalInfo: View
    private lateinit var textSocial: View
    private lateinit var textPaymentModes: View
    private lateinit var textPanDetails: View
    private lateinit var textMyEarning: View
    private lateinit var textTotalPayout: TextView
    private lateinit var linearConnectivity: LinearLayout
    private lateinit var relativeParticipate: RelativeLayout
    private lateinit var myEarningLayout: RelativeLayout
    private lateinit var relativeShareReferralCode: RelativeLayout
    private var totalPayout: Int = 0
    private var isRewardsAdded = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerView = inflater.inflate(R.layout.fragment_rewards, container, false)
        textStartReward = containerView.findViewById(R.id.textStartReward)
        textPersonalInfo = containerView.findViewById(R.id.textPersonalInfo)
        textSocial = containerView.findViewById(R.id.textSocial)
        textPaymentModes = containerView.findViewById(R.id.textPaymentModes)
        textPanDetails = containerView.findViewById(R.id.textPanDetails)
        textMyEarning = containerView.findViewById(R.id.textMyEarning)
        textTotalPayout = containerView.findViewById(R.id.totalearning)
        linearConnectivity = containerView.findViewById(R.id.linearConnectivity)
        myEarningLayout = containerView.findViewById(R.id.myearning_layout)
        relativeParticipate = containerView.findViewById(R.id.relativeParticipate)
        relativeShareReferralCode = containerView.findViewById(R.id.relativeShareReferralCode)

        fetchTotalEarning()
        // val isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())

        textPersonalInfo.setOnClickListener {
            Utils.campaignEvent(
                activity,
                "personalInfo",
                "reward_tab",
                "personalInfoText",
                "",
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "personal_info_detail"
            )
            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageLimit", 1)
            intent.putExtra("pageNumber", 1)
            startActivity(intent)
        }

        relativeShareReferralCode.setOnClickListener {
            activity?.let {
                Utils.pushGenericEvent(
                    activity,
                    "CTA_MyMoney_Profile_Refer",
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    "Home Screen"
                )
            }
            var intent = Intent(activity, RewardsShareReferralCodeActivity::class.java)
            startActivity(intent)
        }

        textSocial.setOnClickListener {
            Utils.campaignEvent(
                activity,
                "socialInfo",
                "reward_tab",
                "socialText",
                "",
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "social_info_detail"
            )

            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 3)
            intent.putExtra("pageLimit", 3)
            startActivity(intent)
        }

        textPaymentModes.setOnClickListener {
            Utils.campaignEvent(
                activity,
                "paymentModeOptions",
                "rewards_tab",
                "paymentText",
                "",
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "payment_option_selection"
            )

            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 4)
            intent.putExtra("pageLimit", 4)
            startActivity(intent)
        }

        textPanDetails.setOnClickListener {
            Utils.campaignEvent(
                activity,
                "panDetailAdd",
                "rewards_tab",
                "panDetailText",
                "",
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "show_pan_detail"
            )

            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 5)
            intent.putExtra("pageLimit", 5)
            startActivity(intent)
        }

        myEarningLayout.setOnClickListener {

            var intent = Intent(activity, MyTotalEarningActivity::class.java)
            intent.putExtra("totalPayout", totalPayout)
            startActivity(intent)
        }

        if (arguments != null) {
            isRewardsAdded = arguments!!.getString("isRewardsAdded", "0")
            if (isRewardsAdded == "1") {
                textStartReward.setText(resources.getString(R.string.update))
                relativeParticipate.visibility = View.GONE
                linearConnectivity.visibility = View.VISIBLE
            } else {
                textStartReward.setText(resources.getString(R.string.rewards_start_now))
                relativeParticipate.visibility = View.VISIBLE
                linearConnectivity.visibility = View.GONE
            }
        }

        textStartReward.setOnClickListener {
            startActivity(Intent(activity, RewardsContainerActivity::class.java))
        }

        return containerView
    }

    private fun fetchTotalEarning() {
        showProgressDialog(resources.getString(R.string.please_wait))
        var userId =
            com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        val retro = BaseApplication.getInstance().retrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        val call = campaignAPI.getTotalPayout(userId)
        call.enqueue(getTotalPayout)
    }

    private val getTotalPayout = object : Callback<TotalPayoutResponse> {
        override fun onResponse(
            call: Call<TotalPayoutResponse>,
            response: retrofit2.Response<TotalPayoutResponse>
        ) {
            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data!!.size > 0) {
                        totalPayout = responseData.data.get(0).result.get(0).total_payout
                        if (totalPayout > 0)
                            textTotalPayout.setText("\u20b9" + totalPayout)
                        else
                            textTotalPayout.visibility = View.GONE
                    }
                } else {
                    textTotalPayout.visibility = View.GONE
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<TotalPayoutResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }
}
