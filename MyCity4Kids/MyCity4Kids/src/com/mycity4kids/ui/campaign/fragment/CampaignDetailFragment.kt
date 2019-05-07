package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ShareCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.BaseResponseModel
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.campaignmodels.ParticipateCampaignResponse
import com.mycity4kids.models.request.CampaignParticipate
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.CampaignDetailAdapter
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.campaign_detail_activity.*
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

const val REWARDS_FILL_FORM_REQUEST = 1000

class CampaignDetailFragment : BaseFragment() {

    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CampaignDetailAdapter
    private var apiGetResponse: CampaignDetailResult? = null
    private var apiGetParticipationResponse: BaseResponseModel? = null
    private lateinit var containerView: View
    private var id: Int? = 0
    private var status: Int? = 0
    private lateinit var bannerImg: ImageView
    private lateinit var brandImg: ImageView
    private lateinit var brandName: TextView
    private lateinit var campaignName: TextView
    private lateinit var amount: TextView
    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var readThisText: TextView
    private lateinit var showRewardText: TextView
    private lateinit var termText: TextView
    private lateinit var shareText: TextView
    private lateinit var submitBtn: TextView
    private lateinit var backIcon: ImageView
    private lateinit var labelText: TextView
    private lateinit var appliedTag: TextView
    private lateinit var descText: TextView
    private lateinit var applicationStatus: TextView
    private lateinit var bottomLayout: RelativeLayout
    private lateinit var isRewardAdded: String
    private lateinit var parentConstraint: ConstraintLayout

    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int) =
                CampaignDetailFragment().apply {
                    arguments = Bundle().apply {
                        this.putInt("id", id)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.campaign_detail_activity, container, false)
        id = arguments!!.getInt("id")
        isRewardAdded = SharedPrefUtils.getIsRewardsAdded(context)
        showProgressDialog(resources.getString(R.string.please_wait))
        fetchCampaignDetail();
        initializeXml()
        backIcon = containerView.findViewById(R.id.back)
        linearLayoutManager = LinearLayoutManager(activity as Context?, LinearLayoutManager.VERTICAL, false)
        backIcon.setOnClickListener {
            activity!!.onBackPressed()
        }
        shareText.setOnClickListener {
            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/" + apiGetResponse!!.nameSlug + "/" + id)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }
        }
        return containerView
    }

    private fun initializeXml() {
        bannerImg = containerView.findViewById(R.id.header_img)
        brandImg = containerView.findViewById(R.id.brand_img)
        brandName = containerView.findViewById(R.id.brand_name)
        campaignName = containerView.findViewById(R.id.campaign_name)
        amount = containerView.findViewById(R.id.amount)
        startDateText = containerView.findViewById(R.id.start_date_text)
        endDateText = containerView.findViewById(R.id.end_date_text)
        readThisText = containerView.findViewById(R.id.read_this_text)
        showRewardText = containerView.findViewById(R.id.show_reward_text)
        termText = containerView.findViewById(R.id.term_text)
        shareText = containerView.findViewById(R.id.share)
        submitBtn = containerView.findViewById(R.id.submit_btn)
        labelText = containerView.findViewById(R.id.label_text)
        descText = containerView.findViewById(R.id.desc_text)
        bottomLayout = containerView.findViewById(R.id.bottom_button)
        appliedTag = containerView.findViewById(R.id.applied_tag)
        applicationStatus = containerView.findViewById(R.id.application_status)
        parentConstraint = containerView.findViewById(R.id.parentConstraint)
    }

    private fun fetchCampaignDetail() {
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getCampaignDetail(this!!.id!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CampaignDetailResult>> {


            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                    parentConstraint.visibility = View.VISIBLE
                    apiGetResponse = response.data!!.result
                    setResponseData()
                } else {

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    private fun setResponseData() {
        Picasso.with(context).load(apiGetResponse!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(bannerImg)
        Picasso.with(context).load(apiGetResponse!!.brandDetails!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(brandImg)
        brandName.setText(apiGetResponse!!.brandDetails!!.name)
        campaignName.setText(apiGetResponse!!.name)
        amount.setText("Rs. " + apiGetResponse!!.totalPayout)
        startDateText.setText(getDate(apiGetResponse!!.startTime!!, "dd MMM yyyy"))
        endDateText.setText(getDate(apiGetResponse!!.endTime!!, "dd MMM yyyy"))

        val descBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.description?.instructions!!) {
            descBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        descText.setText(descBuilder.toString())

        val readBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.readThis?.instructions!!) {
            readBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        readThisText.setText(readBuilder.toString())

        val termBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.terms?.instructions!!) {
            termBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        termText.setText(termBuilder.toString())

        status = apiGetResponse!!.campaignStatus
        if (apiGetResponse!!.deliverables!!.size > 0) {
            detail_recyclerview.layoutManager = linearLayoutManager
            adapter = CampaignDetailAdapter(apiGetResponse!!.deliverables, activity)
            detail_recyclerview.adapter = adapter
            detail_recyclerview.isNestedScrollingEnabled = false
        }

        showRewardText.setOnClickListener {
            showDialog()
        }

        bottomLayout.setOnClickListener {
            setClickAction()

        }
        setLabels()
    }


    private fun setClickAction() {
        if (submitBtn.text == context!!.resources.getString(R.string.detail_bottom_apply_now)) {
            if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
                showRewardDialog()
            } else {
                var userId = SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
                var participateRequest = CampaignParticipate()
                participateRequest!!.user_id = userId
                participateRequest.campaign_id = this!!.id!!
                val retro = BaseApplication.getInstance().campaignRetrofit
                val campaignAPI = retro.create(CampaignAPI::class.java)
                val call = campaignAPI.postRegisterCampaign(participateRequest)
                call.enqueue(participateCampaign)
            }
        } else if (submitBtn.text == context!!.resources.getString(R.string.detail_bottom_share)) {

            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/" + apiGetResponse!!.nameSlug + "/" + id)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }
        } else if (submitBtn.text == context!!.resources.getString(R.string.detail_bottom_share_momspresso_reward)) {
            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }

        } else if (submitBtn.text == context!!.resources.getString(R.string.detail_bottom_view_other)) {
            (context as CampaignContainerActivity).onBackPressed()
        } else if (submitBtn.text == context!!.resources.getString(R.string.detail_bottom_submit_proof)) {
            (activity as CampaignContainerActivity).addAddProofFragment(apiGetResponse!!.id!!, (apiGetResponse!!.deliverableTypes as ArrayList<Int>?)!!)
            if (apiGetResponse != null && apiGetResponse!!.totalPayout != null && apiGetResponse!!.id != null && apiGetResponse!!.nameSlug != null) {
                (activity as CampaignContainerActivity).setTotalPayOut(apiGetResponse!!.totalPayout!!)
                (activity as CampaignContainerActivity).setIdCamp(apiGetResponse!!.id!!)
                (activity as CampaignContainerActivity).setNameSlug(apiGetResponse!!.nameSlug!!)
            }
        }
    }

    val participateCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(call: Call<ParticipateCampaignResponse>, response: retrofit2.Response<ParticipateCampaignResponse>) {
            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_applied))
                    labelText.setText(context!!.resources.getString(R.string.label_campaign_applied))
                } else {
                    Toast.makeText(context, responseData.reason, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ParticipateCampaignResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    fun setLabels() {
        labelText.visibility = View.VISIBLE
        appliedTag.visibility = View.GONE
        if (status == 0) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_expired))
            applicationStatus.setBackgroundResource(R.drawable.campaign_expired)
            labelText.setText(context!!.resources.getString(R.string.label_campaign_expired))
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share_momspresso_reward))
        } else if (status == 1) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_apply_now))
            applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
            labelText.setText(context!!.resources.getString(R.string.label_campaign_apply))
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_apply_now))
        } else if (status == 2) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_submission_open))
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            labelText.visibility = View.GONE
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_submit_proof))
            Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_started), Toast.LENGTH_SHORT).show()
        } else if (status == 21) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_submission_open))
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_not_started), Toast.LENGTH_SHORT).show()
            appliedTag.visibility = View.VISIBLE
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share))
            labelText.setText(context!!.resources.getString(R.string.label_campaign_not_started) + " " + getDate(apiGetResponse!!.startTime!!, "dd MMM yyyy"))
        } else if (status == 3) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_applied))
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscribed)
            Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_applied), Toast.LENGTH_SHORT).show()
            labelText.setText(context!!.resources.getString(R.string.label_campaign_applied))
            appliedTag.visibility = View.VISIBLE
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share))
        } else if (status == 4) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_application_full))
            applicationStatus.setBackgroundResource(R.drawable.campaign_submission_full)
            Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_full), Toast.LENGTH_SHORT).show()
            labelText.setText(context!!.resources.getString(R.string.label_campaign_full))
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share_momspresso_reward))
        } else if (status == 5) {
            if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
                val intent = Intent(context, RewardsContainerActivity::class.java)
                intent.putExtra("isComingfromCampaign", true)
                intent.putExtra("pageLimit", 2)
                startActivityForResult(intent, REWARDS_FILL_FORM_REQUEST)
            } else {
                applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_apply_now))
                applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                Toast.makeText(context, context!!.resources.getString(R.string.toast_not_elegible), Toast.LENGTH_SHORT).show()
                labelText.setText(context!!.resources.getString(R.string.label_campaign_not_eligible))
                submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share))
            }
        } else if (status == 6) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_rejected))
            applicationStatus.setBackgroundResource(R.drawable.campaign_rejected)
            Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_reject), Toast.LENGTH_SHORT).show()
            labelText.setText(context!!.resources.getString(R.string.label_campaign_reject))
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_view_other))
        } else if (status == 7) {
            applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_completed))
            applicationStatus.setBackgroundResource(R.drawable.campaign_completed)
            labelText.setText(context!!.resources.getString(R.string.label_campaign_completed))
            submitBtn.setText(context!!.resources.getString(R.string.detail_bottom_share_momspresso_reward))
        }
    }

    fun showDialog() {
        if (activity != null) {
            val dialog = Dialog(activity)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_show_rewards)
            dialog.setCancelable(true)
            val showAmount = dialog.findViewById<TextView>(R.id.show_amount)
            if (apiGetResponse!!.isFixedAmount == 1) {
                showAmount.setText("Rs. " + apiGetResponse!!.amount)
            } else {
                showAmount.setText("Rs. " + apiGetResponse!!.minAmount + "-" + "Rs. " + apiGetResponse!!.maxAmount)
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }


    fun showRewardDialog() {
        if (activity != null) {
            val dialog = Dialog(activity)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_detail_reward_redirect)
            dialog.setCancelable(true)
            val okBtn = dialog.findViewById<TextView>(R.id.click_ok)
            okBtn.setOnClickListener {
                val intent = Intent(context, RewardsContainerActivity::class.java)
                intent.putExtra("isComingfromCampaign", true)
                intent.putExtra("pageLimit", 2)
                startActivityForResult(intent, REWARDS_FILL_FORM_REQUEST)
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }


    fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds * 1000
        return formatter.format(calendar.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REWARDS_FILL_FORM_REQUEST -> {
                    fetchCampaignDetail()
                }
            }
        }
    }
}


