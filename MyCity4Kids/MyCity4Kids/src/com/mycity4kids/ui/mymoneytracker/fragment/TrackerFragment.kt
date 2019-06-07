package com.mycity4kids.ui.mymoneytracker.fragment

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs
import com.mycity4kids.ui.activity.EditProfileNewActivity
import com.mycity4kids.ui.adapter.RewardCampaignAdapter
import com.mycity4kids.ui.mymoneytracker.adapter.TrackerListAdapter
import com.mycity4kids.ui.mymoneytracker.model.TrackerDataModel
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.utils.EndlessScrollListener
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cropimage.*
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class TrackerFragment : BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: TrackerListAdapter
    private lateinit var apiGetResponse: CampaignDataListResult
    private lateinit var backIcon: ImageView
    private lateinit var containerView: View
    private lateinit var recyclerView: RecyclerView
    private var trackerDataModel = arrayListOf<TrackerDataModel>()
    private var endIndex: Int = 0
    private lateinit var profileIcon: ImageView
    private lateinit var isRewardAdded: String
    private lateinit var registerRewards: ConstraintLayout
    private var campaignId = 0
    private var totalPayout = 0
    private lateinit var brandName: String
    private lateinit var campaignName: String
    private lateinit var imageUrl: String

    override fun updateUi(response: Response?) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_tracker, container, false)

        if (arguments != null) {
            campaignId = if (arguments!!.containsKey("campaign_id")) {
                arguments!!.getInt("campaign_id")
            } else {
                0
            }
            brandName = if (arguments!!.containsKey("brand_name")) {
                arguments!!.getString("brand_name")
            } else {
                ""
            }

            campaignName = if (arguments!!.containsKey("campaign_name")) {
                arguments!!.getString("campaign_name")
            } else {
                ""
            }

            totalPayout = if (arguments!!.containsKey("total_payout")) {
                arguments!!.getInt("total_payout")
            } else {
                0
            }

            imageUrl = if (arguments!!.containsKey("image_url")) {
                arguments!!.getString("image_url")
            } else {
                ""
            }
        }

        (containerView.findViewById<TextView>(R.id.textBrand)).text = brandName
        (containerView.findViewById<TextView>(R.id.textCampaign)).text = campaignName
        (containerView.findViewById<TextView>(R.id.textTotalPayout)).text = totalPayout.toString()
        var imageBrandLogo = containerView.findViewById<ImageView>(R.id.imageBrandLogo)

        Picasso.with(context).load(imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageBrandLogo)

        recyclerView = containerView.findViewById(R.id.trackerListIndex)
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = TrackerListAdapter(activity as Context, trackerDataModel)
        registerRewards = containerView.findViewById(R.id.register_rewards)
        isRewardAdded = SharedPrefUtils.getIsRewardsAdded(context)
        recyclerView.adapter = adapter
        getTrackerData(campaignId)

        return containerView
    }

    companion object {

        fun newInstance(campaignId: Int = 0, brandName: String = "", campaignName: String = "", totalPayout: Int = 0, imageUrl: String = "") =
                TrackerFragment().apply {
                    arguments = Bundle().apply {
                        this.putInt("campaign_id", campaignId)
                        this.putString("brand_name", brandName)
                        this.putString("campaign_name", campaignName)
                        this.putInt("total_payout", totalPayout)
                        this.putString("image_url", imageUrl)
                    }
                }
    }

    private fun getTrackerData(campaignId: Int) {
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getTrackerData(campaignId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ArrayList<TrackerDataModel>>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<ArrayList<TrackerDataModel>>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && !response.data!!.result.isNullOrEmpty()) {
                    trackerDataModel.addAll(response.data!!.result)
                    adapter.notifyDataSetChanged()
                } else {

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
            }
        })
    }
}
