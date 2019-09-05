package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.activity.EditProfileNewActivity
import com.mycity4kids.ui.adapter.RewardCampaignAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.EndlessScrollListener
import retrofit2.Call
import retrofit2.Callback
import java.util.*

const val REWARDS_FILL_FORM = 1000

class CampaignListFragment : BaseFragment() {

    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RewardCampaignAdapter
    private lateinit var apiGetResponse: CampaignDataListResult
    private lateinit var backIcon: ImageView
    private lateinit var containerView: View
    private lateinit var recyclerView: RecyclerView
    private var endIndex: Int = 0
    private lateinit var ashimmerFrameLayout: ShimmerFrameLayout
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var profileIcon: ImageView
    private lateinit var isRewardAdded: String
    private lateinit var registerRewards: ConstraintLayout


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

        Log.i("Fragment", "onCreateView")

        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.reward_campaign, container, false)
        backIcon = containerView.findViewById(R.id.back)
        ashimmerFrameLayout = containerView.findViewById(R.id.shimmer1)
        profileIcon = containerView.findViewById(R.id.profile_icon)
        recyclerView = containerView.findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = RewardCampaignAdapter(campaignList, activity)
        registerRewards = containerView.findViewById(R.id.register_rewards)
        isRewardAdded = SharedPrefUtils.getIsRewardsAdded(context)
        recyclerView.adapter = adapter
        campaignList.clear()
//        if (campaignList.size == 0)
        fetchCampaignList(0)

        profileIcon.setOnClickListener {
            val intent = Intent(context, EditProfileNewActivity::class.java)
            intent.putExtra("isComingfromCampaign", true)
            intent.putExtra("isRewardAdded", isRewardAdded)
            startActivity(intent)
        }

        backIcon.setOnClickListener {
            activity!!.onBackPressed()
        }

        recyclerView.setOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                fetchCampaignList(totalItemsCount, true)
            }
        })
        if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
            registerRewards.visibility = View.VISIBLE
        } else {
            registerRewards.visibility = View.GONE
        }

        registerRewards.setOnClickListener {
            checkRewardForm()
        }
        /*  swipeRefresh.setOnRefreshListener {

              fetchCampaignList(0)
              swipeRefresh.isRefreshing = false

          }*/

        return containerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REWARDS_FILL_FORM -> {
                    isRewardAdded = SharedPrefUtils.getIsRewardsAdded(context)
                    if (isRewardAdded.isNotEmpty() || isRewardAdded.equals("1")) {
                        registerRewards.visibility = View.GONE
                    }
//                    checkRewardForm()
                }
            }
        }
    }

    fun checkRewardForm() {
        if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
            val intent = Intent(context, RewardsContainerActivity::class.java)
            intent.putExtra("isComingfromCampaign", true)
            intent.putExtra("pageLimit", 2)
            startActivityForResult(intent, REWARDS_FILL_FORM)
        } else {
            registerRewards.visibility = View.GONE
        }
    }

    private fun fetchCampaignList(startIndex: Int, shouldShowProgressbar: Boolean = false) {


        //endIndex = startIndex + 10

        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId

        val retro = BaseApplication.getInstance().retrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        if (startIndex == 0) {
            val call = campaignAPI.getCampaignList(userId, startIndex, startIndex + 10, 3.0)
            call.enqueue(getCampaignList)
        } else {
            val call = campaignAPI.getCampaignList(userId, startIndex + 1, startIndex + 10, 3.0)
            call.enqueue(getCampaignList)
        }


    }

    private val getCampaignList = object : Callback<AllCampaignDataResponse> {
        override fun onResponse(call: Call<AllCampaignDataResponse>, response: retrofit2.Response<AllCampaignDataResponse>) {
            //   removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    ashimmerFrameLayout.stopShimmerAnimation()
                    ashimmerFrameLayout.visibility = View.GONE

                    if (responseData.data!!.result!!.size > 0) {
                        campaignList.addAll(responseData.data!!.result as ArrayList<CampaignDataListResult>)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<AllCampaignDataResponse>, t: Throwable) {
            //  removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    override fun onStart() {
        super.onStart()
        ashimmerFrameLayout.startShimmerAnimation()
    }


    override fun onPause() {
        super.onPause()
        ashimmerFrameLayout.stopShimmerAnimation()
    }


}

