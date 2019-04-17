package com.mycity4kids.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.RewardCampaignAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.reward_campaign.*
import java.util.*

class RewardCampaignActivity : BaseActivity() {

    private var campaignList: ArrayList<AllCampaignDataResponse> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RewardCampaignAdapter
    private var apiGetResponse: AllCampaignDataResponse = AllCampaignDataResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reward_campaign)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = RewardCampaignAdapter(campaignList, this)
        recyclerView.adapter = adapter
        fetchCampaignList();
    }

    private fun fetchCampaignList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getCampaignList(1, 10, 0).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AllCampaignDataResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<AllCampaignDataResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                    apiGetResponse = response.data!!.result
                    campaignList.add(apiGetResponse)
                    adapter.updateList(campaignList)
                    adapter.notifyDataSetChanged()
                    /*setting values to components*/
//                    setValuesToComponents()
                } else {

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
            }
        })
    }

    override fun updateUi(response: Response?) {

    }
}