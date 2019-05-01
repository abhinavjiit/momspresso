package com.mycity4kids.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.CampaignDetailAdapter
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.campaign_detail_activity.*
import org.wordpress.android.util.MediaUtils.getDate
import java.text.SimpleDateFormat
import java.util.*

class CampaignDetailFragment : BaseFragment() {

    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CampaignDetailAdapter
    private var apiGetResponse: CampaignDetailResult? = null
    private lateinit var containerView: View
    private var id: Int? = 0
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
    private lateinit var checkbox: CheckBox
    private lateinit var submitBtn: TextView

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
        id = arguments!!.getInt("id", 0)
        initializeXml()
        fetchCampaignDetail();
        linearLayoutManager = LinearLayoutManager(activity as Context?, LinearLayoutManager.VERTICAL, false)
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
        checkbox = containerView.findViewById(R.id.checkbox)
        submitBtn = containerView.findViewById(R.id.submit_btn)
    }

    private fun fetchCampaignDetail() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getCampaignDetail(18).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CampaignDetailResult>> {


            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
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
        amount.setText("" + apiGetResponse!!.amount)
        startDateText.setText(getDate(apiGetResponse!!.startTime!!, "dd MMM YYYY"))
        endDateText.setText(getDate(apiGetResponse!!.endTime!!, "dd MMM YYYY"))
        val readBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.readThis!!.instructions!!) {
            readBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        readThisText.setText(readBuilder.toString())
        val termBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.terms!!.instructions!!) {
            termBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        termText.setText(termBuilder.toString())

        detail_recyclerview.layoutManager = linearLayoutManager
        adapter = CampaignDetailAdapter(apiGetResponse!!.deliverables, activity)
        detail_recyclerview.adapter = adapter

//        submitBtn.setOnClickListener{
//            (activity as CampaignContainerActivity).addAddProofFragment()
//        }
    }

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}