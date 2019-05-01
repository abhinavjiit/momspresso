package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.RewardCampaignAdapter
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import kotlinx.android.synthetic.main.reward_campaign.*
import retrofit2.Call
import retrofit2.Callback

class CampaignListFragment : BaseFragment() {

    private lateinit var saveAndContinueListener: SaveAndContinueListener
    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RewardCampaignAdapter
    private lateinit var apiGetResponse: CampaignDataListResult
    private lateinit var containerView: View

    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                CampaignListFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.reward_campaign, container, false)
        fetchCampaignList();
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        return containerView
    }

    fun initiatelistner(id:Int){
        saveAndContinueListener.pushCampaignDetail(id)
    }


    interface SaveAndContinueListener {
        fun pushCampaignDetail(id: Int)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CampaignContainerActivity) {
            //saveAndContinueListener = context
        }
    }

    private fun fetchCampaignList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        val retro = BaseApplication.getInstance().campaignRetrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        val call = campaignAPI.getCampaignList(userId, 1, 20)
        call.enqueue(getCampaignList)


        /*BaseApplication.getInstance().getCampaignRetrofit().create(CampaignAPI::class.java).getCampaignList(userId,1, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AllCampaignDataResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<AllCampaignDataResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                    apiGetResponse = response.data!!.result
                    campaignList.add(apiGetResponse)
                    recyclerView.layoutManager = linearLayoutManager
                    adapter = RewardCampaignAdapter(campaignList, activity)
                    recyclerView.adapter = adapter
//                    adapter.updateList(campaignList)
//                    adapter.notifyDataSetChanged()
                    *//*setting values to components*//*
//                    setValuesToComponents()
                } else {

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
            }
        })*/
    }

    private val getCampaignList = object : Callback<AllCampaignDataResponse> {
        override fun onResponse(call: Call<AllCampaignDataResponse>, response: retrofit2.Response<AllCampaignDataResponse>) {
            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
//                if (isAdded)
//                    (activity as CampaignContainerActivity).showToast(getString(R.string.server_went_wrong))
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    campaignList.addAll(responseData.data!!.result as ArrayList<CampaignDataListResult>)
                    recyclerView.layoutManager = linearLayoutManager
                    //adapter = RewardCampaignAdapter(campaignList, activity)
                    recyclerView.adapter = adapter
//                    if (isAdded)
//                        (activity as ArticleDetailsContainerActivity).showToast(responseData.reason)
                } else {
//                    if (isAdded)
//                        (activity as ArticleDetailsContainerActivity).showToast(responseData.reason)
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
//                if (isAdded)
//                    (activity as ArticleDetailsContainerActivity).showToast(getString(R.string.went_wrong))
            }
        }

        override fun onFailure(call: Call<AllCampaignDataResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
//            if (isAdded)
//                (activity as ArticleDetailsContainerActivity).showToast(getString(R.string.went_wrong))
        }
    }

}

