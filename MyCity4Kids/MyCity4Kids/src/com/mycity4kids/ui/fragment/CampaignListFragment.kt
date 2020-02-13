package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.activity.MyTotalEarningActivity
import com.mycity4kids.ui.adapter.RewardCampaignAdapter
import com.mycity4kids.ui.campaign.BasicResponse
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsShareReferralCodeActivity
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.widget.RoundedHorizontalProgressBar
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
    private lateinit var editIcon: ImageView
    private lateinit var isRewardAdded: String
    private lateinit var registerRewards: TextView
    private var forYouStatus: Int = 0
    private lateinit var defaultCampaignPopUp: View
    private var defaultapigetResponse: CampaignDetailResult? = null
    private lateinit var default_campaign_header: ImageView
    private lateinit var default_brand_img: ImageView
    private lateinit var default_brand_name: TextView
    private lateinit var default_campaign_name: TextView
    private lateinit var default_submission_status: TextView
    private lateinit var cancel: ImageView
    private lateinit var default_participateTextView: TextView
    private lateinit var mainLinearLayout: LinearLayout
    private lateinit var upperTextHeader: TextView
    private lateinit var lowerTextHeader: TextView
    private lateinit var profilePercentageTextView: TextView
    private lateinit var userName: TextView
    private lateinit var totalEarning: TextView
    private lateinit var progressBar: RoundedHorizontalProgressBar
    private var apiResponse: UserDetailResult = UserDetailResult()
    private lateinit var profileImageView: ImageView
    private lateinit var loader: ProgressBar
    private var totalPayout = 0
    private var totalCampaignCount = 0
    private lateinit var dashboardLayout: ConstraintLayout
    private lateinit var campaignNos: TextView
    private lateinit var referText: TextView

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
        containerView = inflater.inflate(R.layout.reward_campaign, container, false)
        backIcon = containerView.findViewById(R.id.back)
        ashimmerFrameLayout = containerView.findViewById(R.id.shimmer1)
        editIcon = containerView.findViewById(R.id.edit_profile)
        recyclerView = containerView.findViewById(R.id.recyclerView)
        defaultCampaignPopUp = containerView.findViewById(R.id.include)
        default_campaign_header = containerView.findViewById(R.id.default_campaign_header)
        default_brand_img = containerView.findViewById(R.id.default_brand_img)
        default_brand_name = containerView.findViewById(R.id.default_brand_name)
        default_campaign_name = containerView.findViewById(R.id.default_campaign_name)
        default_submission_status = containerView.findViewById(R.id.default_submission_status)
        default_participateTextView = containerView.findViewById(R.id.default_participateTextView)
        mainLinearLayout = containerView.findViewById(R.id.mainLinearLayout)
        cancel = containerView.findViewById(R.id.cancel)
        upperTextHeader = containerView.findViewById(R.id.upperTextHeader)
        lowerTextHeader = containerView.findViewById(R.id.lowerTextHeader)
        profilePercentageTextView = containerView.findViewById(R.id.profilePercentageTextView)
        progressBar = containerView.findViewById(R.id.progress_bar_1)
        linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = RewardCampaignAdapter(campaignList, activity)
        registerRewards = containerView.findViewById(R.id.bottomText)
        profileImageView = containerView.findViewById(R.id.profileImageView)
        userName = containerView.findViewById(R.id.user_name)
        totalEarning = containerView.findViewById(R.id.total_earning)
        isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
        loader = containerView.findViewById(R.id.loader)
        referText = containerView.findViewById(R.id.refer_text)
        campaignNos = containerView.findViewById(R.id.campaign_nos)
        dashboardLayout = containerView.findViewById(R.id.dashboard_layout)
        recyclerView.adapter = adapter
        campaignList.clear()
        userName.text = SharedPrefUtils.getUserDetailModel(activity)?.first_name + " " + SharedPrefUtils.getUserDetailModel(activity)?.last_name
        try {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext())).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).into(profileImageView)
        } catch (e: Exception) {
            profileImageView.setImageResource(R.drawable.family_xxhdpi)
        }
        fetchForYou()
        if (SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext()) == "1") {
            dashboardLayout.visibility = View.VISIBLE
            referText.visibility = View.VISIBLE
            fetchTotalEarning()
            fetchRewardsData()
        }
        editIcon.setOnClickListener {
            val personalIntent = Intent(context, RewardsContainerActivity::class.java)
            personalIntent.putExtra("showProfileInfo", true)
            startActivity(personalIntent)
        }

        backIcon.setOnClickListener {
            activity!!.onBackPressed()
        }

        referText.setOnClickListener {
            val intent = Intent(context, RewardsShareReferralCodeActivity::class.java)
            startActivity(intent)
        }

        recyclerView.addOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
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
        mainLinearLayout.setOnClickListener {
            (activity as CampaignContainerActivity).addCampaginDetailFragment(defaultapigetResponse?.id!!, "defaultCampaign")
        }

        totalEarning.setOnClickListener {
            var intent = Intent(activity, MyTotalEarningActivity::class.java)
            intent.putExtra("totalPayout", totalPayout)
            startActivity(intent)
        }

        cancel.setOnClickListener {
            defaultCampaignPopUp.visibility = View.GONE
        }
        return containerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REWARDS_FILL_FORM -> {
                    fetchDefaultCampaign()
                    isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
                    if (isRewardAdded.isNotEmpty() || isRewardAdded.equals("1")) {
                        //  fetchForYou()
                        registerRewards.visibility = View.GONE
                    }
//                    checkRewardForm()
                }
            }
        }
    }

    fun checkRewardForm() {
        if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
            activity?.let {
                Utils.pushGenericEvent(it, "CTA_CampaignListing_Register",
                        SharedPrefUtils.getUserDetailModel(it).dynamoId, "CampaignListFragment")
            }
            val intent = Intent(context, RewardsContainerActivity::class.java)
            intent.putExtra("isComingfromCampaign", true)
            intent.putExtra("pageLimit", 2)
            startActivityForResult(intent, REWARDS_FILL_FORM)
        } else {
            registerRewards.visibility = View.GONE
        }
    }

    private fun fetchCampaignList(startIndex: Int, shouldShowProgressbar: Boolean = false) {
        var userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())?.dynamoId

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

    private fun fetchDefaultCampaign() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getDefaultCampaignDetail().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CampaignDetailResult>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                if (response == null) {
                    val nee = NetworkErrorException(response.toString())
                    Crashlytics.logException(nee)
                    return
                }
                if (response != null && response.code == 200 && response.status == Constants.SUCCESS && response.data?.result != null) {
                    defaultapigetResponse = response.data!!.result
                    defaultCampaignPopUp.visibility = View.VISIBLE
                    setDefaultCampaignValues()
                } else if (response != null && response.code == 200 && response.status == Constants.SUCCESS && response.data?.result == null) {
                    defaultCampaignPopUp.visibility = View.GONE
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private val getCampaignList = object : Callback<AllCampaignDataResponse> {
        override fun onResponse(call: Call<AllCampaignDataResponse>, response: retrofit2.Response<AllCampaignDataResponse>) {
            //   removeProgressDialog()
            if (null == response.body()) {
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


    fun setDefaultCampaignValues() {
        upperTextHeader.text = resources.getString(R.string.campaign_list_sorry_not_eligible)
        lowerTextHeader.text = resources.getString(R.string.campaign_list_try_following_campaign)
        Picasso.get().load(defaultapigetResponse!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(default_campaign_header)
        Picasso.get().load(defaultapigetResponse!!.brandDetails!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(default_brand_img)
        default_brand_name.setText(defaultapigetResponse!!.brandDetails!!.name)
        default_campaign_name.setText(defaultapigetResponse!!.name)
        default_submission_status.text = resources.getString(R.string.campaign_details_apply_now)
    }

    private fun fetchForYou() {
        // showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getForYouStatus(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BasicResponse> {
            override fun onNext(response: BasicResponse) {
                if (response.code == 200 && response.data != null && response.status == "success") {
                    if (response.data.result != null && response.data.result.recm_status != null) {
                        forYouStatus = response.data.result.recm_status
                        adapter.updateForYouStatus(forYouStatus)
                        fetchCampaignList(0)
                    }
                }
            }

            override fun onComplete() {
                //  removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                // removeProgressDialog()
            }
        })
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        val userId = SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getUserDetails(userId, "yes").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserDetailResult>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<UserDetailResult>) {
                    removeProgressDialog()
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        progressBar.isIndeterminate = false
                        apiResponse = response.data!!.result
                        progressBar.progress = getProfileCompletionPecentage(apiResponse)
                        profilePercentageTextView.text = "" + getProfileCompletionPecentage(apiResponse) + "%"
                    } else {
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    Log.d("exception in error", e.message.toString())
                }
            })
        }
    }


    private fun getProfileCompletionPecentage(result: UserDetailResult): Int {
        val totalProgress = 100
        var progress = 0
        if (result.profilePicUrl == null || StringUtils.isNullOrEmpty(result.profilePicUrl.clientApp)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.firstName)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.lastName)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.email)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.phone.mobile)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.blogTitle)) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(result.userBio)) {
            progress += 10
        }
        if (result.kids == null || result.kids.isEmpty()) {
            progress += 10
        }
        if (StringUtils.isNullOrEmpty(SharedPrefUtils.getCurrentCityModel(BaseApplication.getAppContext()).name)) {
            progress += 10
        }
        return totalProgress - progress
    }

    private fun fetchTotalEarning() {
        val userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        val retrofit = BaseApplication.getInstance().retrofit
        val campaignAPI = retrofit.create(CampaignAPI::class.java)
        val call = campaignAPI.getTotalPayout(userId)
        call.enqueue(getTotalPayout)
    }


    internal var getTotalPayout: Callback<TotalPayoutResponse> = object : Callback<TotalPayoutResponse> {
        override fun onResponse(call: Call<TotalPayoutResponse>, response: retrofit2.Response<TotalPayoutResponse>) {
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data.size > 0) {
                        loader.visibility = View.GONE
                        totalPayout = responseData.data[0].result[0].total_payout
                        totalCampaignCount = responseData.data[0].result[0].total_payout_campaign_count
                        totalEarning.text = "\u20b9 " + totalPayout
                        campaignNos.text = "" + totalCampaignCount + " Campaigns"
                    }
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<TotalPayoutResponse>, t: Throwable) {
            loader.visibility = View.GONE
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }
}

