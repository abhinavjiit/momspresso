package com.mycity4kids.ui.campaign.fragment

import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.BaseResponseModel
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.campaignmodels.ParticipateCampaignResponse
import com.mycity4kids.models.request.CampaignParticipate
import com.mycity4kids.models.request.CampaignReferral
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.GroupsMembershipResponse
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.GroupMembershipStatus
import com.mycity4kids.ui.GroupMembershipStatus.IMembershipStatus
import com.mycity4kids.ui.activity.GroupDetailsActivity
import com.mycity4kids.ui.activity.GroupsSummaryActivity
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity
import com.mycity4kids.ui.adapter.CampaignDetailAdapter
import com.mycity4kids.ui.campaign.BasicResponse
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.campaign.activity.CampaignHowToVideoActivity
import com.mycity4kids.ui.mymoneytracker.activity.TrackerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

const val REWARDS_FILL_FORM_REQUEST = 1000
const val SURVEY_CAMPAIGN_REQUEST = 10000

class CampaignDetailFragment : BaseFragment(), IMembershipStatus {
    private lateinit var scrollView2: NestedScrollView
    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CampaignDetailAdapter
    private var apiGetResponse: CampaignDetailResult? = null
    private var apiFeedbackResponse: CampaignFeedBack? = null
    private var defaultapigetResponse: CampaignDetailResult? = null
    private var apiGetParticipationResponse: BaseResponseModel? = null
    private lateinit var containerView: View
    private var id: Int? = 0
    private var status: Int = 0
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var toolbar: Toolbar
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
    private lateinit var labelTextLayout: LinearLayout
    private lateinit var viewDetailsText: TextView
    private lateinit var appliedTag: TextView
    private lateinit var unapplyCampaign: ImageView
    private lateinit var descText: TextView
    private lateinit var applicationStatus: TextView
    private lateinit var bottomLayout: RelativeLayout
    private lateinit var isRewardAdded: String
    private lateinit var parentConstraint: ConstraintLayout
    private lateinit var referCode: EditText
    private lateinit var referCodeApply: TextView
    private lateinit var referCodeError: TextView
    private lateinit var viewLine: View
    private lateinit var getHelp: TextView
    private lateinit var referCodeHeader: TextView
    private lateinit var demoUpload: TextView
    private lateinit var readThisBox: LinearLayout
    private lateinit var detail_recyclerview: RecyclerView
    private lateinit var txtTrackerStatus: TextView
    private lateinit var demoUploadLayout: RelativeLayout
    private lateinit var playDemoIcon: ImageView
    private var position: Int = 0
    private var mediaController: MediaController? = null
    private var forYouStatus: Int = 0
    private var userId: String? = null
    private lateinit var defaultCampaignPopUp: View
    private var defaultCampaignShow: Boolean = false
    private val urlPattern = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
            "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
            "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )
    private var spannable: SpannableString? = null

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
    private var comingFrom: String? = null
    private lateinit var instaHandlePopUpView: View
    private var socialAccountsDetail: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private var showInstPopUpFlag: Boolean = false
    private var instaHandlePostFlag: Boolean = false
    val TIME_DELAY: Long = 1500
    // the default update interval for your text, this is in your hand , just run this sample
    // the default update interval for your text, this is in your hand , just run this sample
    var updateLoaderTextHandler = Handler()
    var count = 0
    private var loadWebView: Boolean = false
    private val updateLoaderTextArray = arrayOf(
        "Thanks for applying!",
        "We are generating the survey link.",
        "It should be visible in a few moments..."
    )

    val handler = CoroutineExceptionHandler { _, exception ->
        FirebaseCrashlytics.getInstance().recordException(exception)
        Log.d("MC4kException", Log.getStackTraceString(exception))
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int, fromNotification: Boolean = false, comingFrom: String) =
            CampaignDetailFragment().apply {
                arguments = Bundle().apply {
                    this.putInt("id", id)
                    this.putString("comingFrom", comingFrom)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            containerView = inflater.inflate(R.layout.campaign_detail_activity, container, false)
            id = arguments!!.getInt("id")
            comingFrom = arguments!!.getString("comingFrom")
            userId = SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
            isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
            defaultCampaignPopUp = containerView.findViewById(R.id.include)
            instaHandlePopUpView = containerView.findViewById(R.id.includeInstaPopUp)

            if (isRewardAdded.equals("1", true)) {
                fetchForYou()
            } else {
                fetchCampaignDetail()
            }
            initializeXml()
            backIcon = containerView.findViewById(R.id.back)
            linearLayoutManager =
                LinearLayoutManager(activity as Context?, RecyclerView.VERTICAL, false)
            if (SharedPrefUtils.getDemoVideoSeen(BaseApplication.getAppContext())) {
                demoUploadLayout.visibility = View.GONE
                playDemoIcon.visibility = View.VISIBLE
            }
            backIcon.setOnClickListener {
                try {
                    Utils.campaignEvent(
                        activity,
                        "Campaign Listing",
                        "Campaign Detail",
                        "Back",
                        "",
                        "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                        System.currentTimeMillis().toString(),
                        "Show_Campaign_Listing"
                    )
                    activity!!.onBackPressed()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            shareText.setOnClickListener {
                try {
                    activity?.let {
                        val shareIntent = ShareCompat.IntentBuilder
                            .from(it)
                            .setType("text/plain")
                            .setChooserTitle("Share URL")
                            .setText("https://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
                            .intent

                        if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                            it.startActivity(shareIntent)
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            mainLinearLayout.setOnClickListener {
                try {
                    (activity as CampaignContainerActivity).addCampaginDetailFragment(
                        defaultapigetResponse?.id!!,
                        "defaultCampaign"
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            getHelp.setOnClickListener {
                getHelpDialog()
            }

            txtTrackerStatus.setOnClickListener {
                try {
                    val intent = Intent(activity, TrackerActivity::class.java)
                    intent.putExtra("campaign_id", id!!)
                    intent.putExtra("brand_name", apiGetResponse?.brandDetails?.name)
                    intent.putExtra("campaign_name", apiGetResponse?.name)
                    intent.putExtra("total_payout", apiGetResponse?.totalPayout?.toInt())
                    intent.putExtra("image_url", apiGetResponse?.brandDetails?.imageUrl)
                    startActivity(intent)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            referCodeApply.setOnClickListener {
                try {
                    applyCode()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            cancel.setOnClickListener {
                try {
                    defaultCampaignPopUp.visibility = View.GONE
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            demoUpload.setOnClickListener {
                try {
                    SharedPrefUtils.setDemoVideoSeen(BaseApplication.getAppContext(), true)
                    playVideo()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            playDemoIcon.setOnClickListener {
                try {
                    playVideo()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            referCode.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    referCodeError.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        return containerView
    }

    private fun playVideo() {
        val intent = Intent(activity, CampaignHowToVideoActivity::class.java)
        startActivity(intent)
    }

    private fun applyCode() {
        var referralRequest = CampaignReferral()
        referralRequest!!.user_id = userId
        referralRequest.campaign_id = this!!.id!!
        referralRequest.referral_code = referCode.text.toString()

        val retro = BaseApplication.getInstance().retrofit
        val campaignAPI = retro.create(CampaignAPI::class.java)
        val call = campaignAPI.postReferralCampaign(referralRequest)
        call.enqueue(referCampaign)
    }

    private fun initializeXml() {
        upperTextHeader = containerView.findViewById(R.id.upperTextHeader)
        lowerTextHeader = containerView.findViewById(R.id.lowerTextHeader)
        default_campaign_header = containerView.findViewById(R.id.default_campaign_header)
        default_brand_img = containerView.findViewById(R.id.default_brand_img)
        default_brand_name = containerView.findViewById(R.id.default_brand_name)
        default_campaign_name = containerView.findViewById(R.id.default_campaign_name)
        default_submission_status = containerView.findViewById(R.id.default_submission_status)
        default_participateTextView = containerView.findViewById(R.id.default_participateTextView)
        mainLinearLayout = containerView.findViewById(R.id.mainLinearLayout)
        cancel = containerView.findViewById(R.id.cancel)

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
        labelTextLayout = containerView.findViewById(R.id.label_text_layout)
        viewDetailsText = containerView.findViewById(R.id.view_details)
        descText = containerView.findViewById(R.id.desc_text)
        bottomLayout = containerView.findViewById(R.id.bottom_button)
        appliedTag = containerView.findViewById(R.id.applied_tag)
        unapplyCampaign = containerView.findViewById(R.id.unapply_campaign)
        applicationStatus = containerView.findViewById(R.id.application_status)
        parentConstraint = containerView.findViewById(R.id.parentConstraint)
        referCode = containerView.findViewById(R.id.refer_code_text)
        referCodeApply = containerView.findViewById(R.id.refer_code_apply)
        referCodeError = containerView.findViewById(R.id.refer_code_error)
        referCodeHeader = containerView.findViewById(R.id.refer_header)
        viewLine = containerView.findViewById(R.id.view_7)
        readThisBox = containerView.findViewById(R.id.read_this_box)
        getHelp = containerView.findViewById(R.id.get_help)
        detail_recyclerview = containerView.findViewById(R.id.detail_recyclerview)
        txtTrackerStatus = containerView.findViewById(R.id.txtTrackerStatus)
        toolbar = containerView.findViewById(R.id.toolbar)
        scrollView2 = containerView.findViewById(R.id.scrollView2)
        shimmer1 = containerView.findViewById(R.id.shimmer1)
        demoUpload = containerView.findViewById(R.id.demo_upload)
        demoUploadLayout = containerView.findViewById(R.id.demo_upload_layout)
        playDemoIcon = containerView.findViewById(R.id.play_demo_icon)
    }

    private fun fetchCampaignDetail() {
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getCampaignDetail(
            this!!.id!!,
            2.0
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<CampaignDetailResult>> {

            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                try {
                    if (response != null && response.code == 200 &&
                        Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                        parentConstraint.visibility = View.VISIBLE
                        apiGetResponse = response.data!!.result
                        setResponseData(apiGetResponse)
                        shimmer1.visibility = View.GONE
                        shimmer1.stopShimmerAnimation()
                        toolbar.visibility = View.VISIBLE
                        scrollView2.visibility = View.VISIBLE
                        //                        labelTextLayout.visibility = View.VISIBLE
                        //                        labelText.visibility = View.VISIBLE
                        bottomLayout.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                activity?.let {
                    ToastUtils.showToast(it, "something went wrong")
                }
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    private fun setResponseData(apiGetResponsee: CampaignDetailResult?) {
        apiGetResponse = apiGetResponsee
        Picasso.get().load(apiGetResponse!!.imageUrl).placeholder(R.drawable.default_article).error(
            R.drawable.default_article
        ).into(bannerImg)
        Picasso.get().load(apiGetResponse!!.brandDetails!!.imageUrl)
            .placeholder(R.drawable.default_article).error(
                R.drawable.default_article
            ).into(brandImg)
        brandName.setText(apiGetResponse!!.brandDetails!!.name)
        campaignName.setText(apiGetResponse!!.name)
        amount.setText("" + apiGetResponse!!.slotAvailable)
        startDateText.setText(getDate(apiGetResponse!!.startTime!!, "dd MMM yyyy"))
        endDateText.setText(getDate(apiGetResponse!!.endTime!!, "dd MMM yyyy"))

        val descBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.description?.instructions!!) {
            if (!instructions.isNullOrEmpty() && !instructions.equals(""))
                descBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        if (!descBuilder.isEmpty()) {
            getOffset(descBuilder.toString(), descText)
        }
        val readBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.readThis?.instructions!!) {
            if (!instructions.isNullOrEmpty() && !instructions.equals(""))
                readBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        if (!readBuilder.isEmpty()) {
            getOffset(readBuilder.toString(), readThisText)
        }
        val termBuilder = StringBuilder()
        for (instructions in apiGetResponse!!.terms?.instructions!!) {
            if (!instructions.isNullOrEmpty() && !instructions.equals(""))
                termBuilder.append("\u2022" + "  " + instructions + "\n")
        }
        if (!termBuilder.isEmpty()) {
            getOffset(termBuilder.toString(), termText)
        }
        status = apiGetResponse!!.campaignStatus!!

        if (apiGetResponse!!.deliverables!!.size > 0) {
            detail_recyclerview.layoutManager = linearLayoutManager
            adapter = CampaignDetailAdapter(apiGetResponse!!.deliverables, activity)
            detail_recyclerview.adapter = adapter
            detail_recyclerview.isNestedScrollingEnabled = false
        }

        if (apiGetResponsee?.amount != null) {
            activity?.let {
                showRewardText.text =
                    resources.getString(R.string.dialog_you_can_earn) + apiGetResponsee?.amount?.toInt()
            }
        } else {
            showRewardText.visibility = View.GONE
        }

        viewDetailsText.setOnClickListener {
            getFeedback()
        }
        unapplyCampaign.setOnClickListener {
            try {
                val popupwindow_obj = popupDisplay()
                popupwindow_obj.showAsDropDown(unapplyCampaign, -140, -140)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        bottomLayout.setOnClickListener {
            try {
                setClickAction()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
        setLabels()
    }

    fun popupDisplay(): PopupWindow {
        val popupWindow = PopupWindow(context)
        val inflater =
            (context as CampaignContainerActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.unapply_campaign_popup, null)
        val textView = view.findViewById<TextView>(R.id.unapply_text)
        textView.setOnClickListener {
            unapplyCampaignDialog()
            popupWindow.dismiss()
        }

        popupWindow.setFocusable(true)
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
        popupWindow.setContentView(view)
        return popupWindow
    }

    private fun getOffset(instruction: String, textView: TextView) {
        val matcher = urlPattern.matcher(instruction)
        var matchStart: Int? = null
        var matchEnd: Int? = null
        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()
        }
        spannable = SpannableString(instruction)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                var url: String
                url = instruction.substring(matchStart!!, matchEnd!!)
                if (!url.startsWith("http") || !url.startsWith("https")) {
                    url = "http://" + url
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context!!.startActivity(intent)
            }
        }
        if (matchStart != null && matchEnd != null) {
            spannable!!.setSpan(
                clickableSpan,
                matchStart,
                matchEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = Html.fromHtml(spannable.toString())
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickAction() {
        if (submitBtn.text == resources.getString(R.string.check_ypur_eligibility)) {
            showRewardDialog()
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_apply_now)) {
            Utils.campaignEvent(
                activity,
                "Campaign Listing",
                "Campaign Detail",
                "applyNow",
                apiGetResponse!!.name,
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "Show_Campaign_Listing"
            )
            if ("defaultCampaign".equals(comingFrom))
                SharedPrefUtils.setDefaultCampaignShownFlag(BaseApplication.getAppContext(), true)
            Log.d("Taggggggg", "ho gya")
            val participateRequest = CampaignParticipate()
            participateRequest.user_id = userId
            participateRequest.campaign_id = this.id!!
            val retro = BaseApplication.getInstance().retrofit
            val campaignAPI = retro.create(CampaignAPI::class.java)
            val call = campaignAPI.postRegisterCampaign(participateRequest)
            call.enqueue(participateCampaign)
        } else if (submitBtn.text == resources.getString(
                R.string.detail_take_survey
            )) {
            val url =
                apiGetResponse!!.deliverables?.get(0)?.get(0)?.instructions?.get(0)
            val intent = Intent(context, NewsLetterWebviewActivity::class.java)
            //            intent.putExtra("fromNotification", true)
            intent.putExtra(Constants.URL, url)
            startActivityForResult(intent, SURVEY_CAMPAIGN_REQUEST)
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_share)) {
            activity?.let {
                Utils.campaignEvent(
                    it,
                    "Campaign Listing",
                    "Campaign Detail",
                    "Share",
                    apiGetResponse!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(activity),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_Campaign_Listing"
                )
                val shareIntent = ShareCompat.IntentBuilder
                    .from(it)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
                    .intent
                if (shareIntent.resolveActivity(it.packageManager) != null) {
                    it.startActivity(shareIntent)
                }
            }
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_share_momspresso_reward)) {
            activity?.let {
                Utils.campaignEvent(
                    it,
                    "Campaign Listing",
                    "Campaign Detail",
                    "Share_Rewards_Sticky_Bottom",
                    apiGetResponse!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(activity),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "CTA_Momspresso_Rewards_Share"
                )
                val shareIntent = ShareCompat.IntentBuilder
                    .from(it)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney?referrer=" + userId)
                    .intent
                if (shareIntent.resolveActivity(it.packageManager) != null) {
                    it.startActivity(shareIntent)
                }
            }
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_view_other)) {
            (context as CampaignContainerActivity).onBackPressed()
            Utils.campaignEvent(
                activity,
                "Campaign Listing",
                "Campaign Detail",
                "View other campaigns",
                "",
                "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "Show_Campaign_Listing"
            )
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_submit_proof)) {
            Utils.campaignEvent(
                activity,
                "Proof Submission",
                "Campaign Detail",
                "Submit Proof",
                "",
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "Show_Campaign_Submission"
            )
            val submissionStatus = if (apiGetResponse?.submissionStatus == null) {
                -1
            } else {
                apiGetResponse!!.submissionStatus!!
            }
            (activity as CampaignContainerActivity).addAddProofFragment(
                apiGetResponse!!.id!!,
                (apiGetResponse!!.deliverableTypes as ArrayList<Int>?)!!,
                (apiGetResponse!!.proofAllowed as ArrayList<Int>?)!!,
                status, submissionStatus
            )
            if (apiGetResponse != null && apiGetResponse!!.totalPayout != null && apiGetResponse!!.id != null && apiGetResponse!!.nameSlug != null) {
                (activity as CampaignContainerActivity).setTotalPayOut(apiGetResponse!!.totalPayout!!)
                (activity as CampaignContainerActivity).setIdCamp(apiGetResponse!!.id!!)
                (activity as CampaignContainerActivity).setNameSlug(apiGetResponse!!.nameSlug!!)
            }
        }
    }

    val participateCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(
            call: Call<ParticipateCampaignResponse>,
            response: retrofit2.Response<ParticipateCampaignResponse>
        ) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (apiGetResponse?.deliverableTypes?.get(0) == 0) {
                        CoroutineScope(Dispatchers.Main).launch {
                            checkInstaHandle()
                        }
                    } else {
                        txtTrackerStatus.visibility = View.VISIBLE
                        if (apiGetResponse!!.deliverableTypes!!.get(0) == 5) {
                            loadWebView = true
                            updateLoaderTextHandler.post(updateLoaderTextRunnable)
                            val handler = Handler()
                            handler.postDelayed({
                                try {
                                    updateLoaderTextHandler.removeCallbacks(
                                        updateLoaderTextRunnable
                                    )
                                    removeProgressDialog()
                                    fetchCampaignDetail()
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                    Log.d("MC4kException", Log.getStackTraceString(e))
                                }
                            }, 5000)
                        } else {
                            submitBtn.setText(resources.getString(R.string.detail_bottom_applied))
                            Toast.makeText(
                                context,
                                resources.getString(R.string.toast_campaign_applied),
                                Toast.LENGTH_SHORT
                            ).show()
                            labelText.setText(resources.getString(R.string.label_campaign_applied))
                        }
                        unapplyCampaign.visibility = View.VISIBLE
                    }
                } else {
                    if (!SharedPrefUtils.isDefaultCampaignShown(BaseApplication.getAppContext()))
                        fetchDefaultCampaign()
                    submitBtn.text = resources.getString(R.string.detail_bottom_share)
                    Toast.makeText(
                        context,
                        responseData.reason,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ParticipateCampaignResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    suspend fun checkInstaHandle() {
        CoroutineScope(Dispatchers.Main).launch {
            if (checkInstagramHandle()) {
                showInstaHandlePopUp()
            }
        }
    }

    private fun fetchDefaultCampaign() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java)
            .getDefaultCampaignDetail().subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
                Observer<BaseResponseGeneric<CampaignDetailResult>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                    if (response == null) {
                        val nee = NetworkErrorException(response.toString())
                        FirebaseCrashlytics.getInstance().recordException(nee)
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
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            })
    }

    fun setDefaultCampaignValues() {
        upperTextHeader.text = resources.getString(R.string.sorry_not_eligible)
        lowerTextHeader.text = resources.getString(R.string.try_following_campaign)
        Picasso.get().load(defaultapigetResponse!!.imageUrl).placeholder(R.drawable.default_article)
            .error(
                R.drawable.default_article
            ).into(default_campaign_header)
        Picasso.get().load(defaultapigetResponse!!.brandDetails!!.imageUrl)
            .placeholder(R.drawable.default_article).error(
                R.drawable.default_article
            ).into(default_brand_img)
        default_brand_name.setText(defaultapigetResponse!!.brandDetails!!.name)
        default_campaign_name.setText(defaultapigetResponse!!.name)
        default_submission_status.text = resources.getString(R.string.campaign_details_apply_now)
    }

    val withdrawParticipateCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(
            call: Call<ParticipateCampaignResponse>,
            response: retrofit2.Response<ParticipateCampaignResponse>
        ) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    fetchCampaignDetail()
                } else {
                    Toast.makeText(context, responseData.reason, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ParticipateCampaignResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    val referCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(
            call: Call<ParticipateCampaignResponse>,
            response: retrofit2.Response<ParticipateCampaignResponse>
        ) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200) {
                    if (Constants.SUCCESS == responseData.status) {
                        Toast.makeText(
                            context,
                            responseData.data.get(0).msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewLine.visibility = View.GONE
                        referCode.visibility = View.GONE
                        referCodeApply.visibility = View.GONE
                        referCodeHeader.visibility = View.GONE
                    } else {
                        referCodeError.visibility = View.VISIBLE
                        referCodeError.setText("" + responseData.reason)
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ParticipateCampaignResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    fun setLabels() {
        labelTextLayout.visibility = View.VISIBLE
        labelText.visibility = View.VISIBLE
        appliedTag.visibility = View.GONE
        unapplyCampaign.visibility = View.GONE
        if (status == 0) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_expired)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_expired)
                labelText.text = it.resources.getString(R.string.label_campaign_expired)
                submitBtn.text =
                    it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
            }
        } else if (status == 1 || status == 18) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_apply_now)
                labelText.text =
                    AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                submitBtn.text = it.resources.getString(R.string.detail_bottom_apply_now)
            }
            labelText.setMovementMethod(LinkMovementMethod.getInstance())
        } else if (status == 2) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text =
                    it.resources.getString(R.string.campaign_details_submission_open)
                if (apiGetResponse!!.deliverableTypes!!.get(0) == 5) {
                    submitBtn.text = resources.getString(R.string.detail_take_survey)
                    if (loadWebView) {
                        val url =
                            apiGetResponse!!.deliverables?.get(0)?.get(0)?.instructions?.get(0)
                        val intent = Intent(context, NewsLetterWebviewActivity::class.java)
                        //                        intent.putExtra("fromNotification", true)
                        intent.putExtra(Constants.URL, url)
                        startActivityForResult(intent, SURVEY_CAMPAIGN_REQUEST)
                    }
                } else {
                    submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
                    Toast.makeText(
                        it,
                        it.resources.getString(R.string.toast_campaign_started),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            labelTextLayout.visibility = View.GONE
            labelText.visibility = View.GONE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 21) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text =
                    it.resources.getString(R.string.campaign_details_submission_open)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_not_started),
                    Toast.LENGTH_SHORT
                ).show()
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
                labelText.text =
                    it.resources.getString(R.string.label_campaign_not_started) + " " + getDate(
                        apiGetResponse!!.startTime!!,
                        "dd MMM yyyy"
                    ) + ". Please wait for campaign to start to submit proofs."
            }
            appliedTag.visibility = View.VISIBLE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 22 || status == 16) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_submission_open))
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_started),
                    Toast.LENGTH_SHORT
                ).show()
            }
            labelTextLayout.visibility = View.GONE
            labelText.visibility = View.GONE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 3) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscribed)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_applied)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_applied),
                    Toast.LENGTH_SHORT
                ).show()
                labelText.text = it.resources.getString(R.string.label_campaign_applied)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
            }
            appliedTag.visibility = View.VISIBLE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 4) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_submission_full)
            context?.let {
                applicationStatus.text =
                    it.resources.getString(R.string.campaign_details_application_full)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_full),
                    Toast.LENGTH_SHORT
                ).show()
                labelText.text = it.resources.getString(R.string.label_campaign_full)
                submitBtn.text =
                    it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
            }
        } else if (status == 5) {
            hideShowReferral(status)
            if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
                applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                context?.let {
                    applicationStatus.text =
                        it.resources.getString(R.string.campaign_details_apply_now)
                    labelText.text =
                        AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                    submitBtn.text = it.resources.getString(R.string.check_ypur_eligibility)
                    bottomLayout.setBackgroundColor(it.resources.getColor(R.color.campaign_listing_light_green_bottom))
                }
                labelText.setMovementMethod(LinkMovementMethod.getInstance())
            } else {
                if (isRewardAdded.equals("1", true)) {
                    if (forYouStatus == 0) {
                        applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                        context?.let {
                            applicationStatus.text =
                                it.resources.getString(R.string.campaign_details_apply_now)
                            Toast.makeText(
                                it,
                                it.resources.getString(R.string.checking_elegiblity),
                                Toast.LENGTH_SHORT
                            ).show()
                            labelText.text =
                                AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                            submitBtn.text =
                                it.resources.getString(R.string.detail_bottom_apply_now)
                        }
                    } else {
                        applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                        context?.let {
                            applicationStatus.text =
                                it.resources.getString(R.string.campaign_details_apply_now)
                            Toast.makeText(
                                it,
                                it.resources.getString(R.string.toast_not_elegible),
                                Toast.LENGTH_SHORT
                            ).show()
                            labelText.text =
                                it.resources.getString(R.string.label_campaign_not_eligible)
                            submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
                        }
                    }
                }
            }
        } else if (status == 6) {
            hideShowReferral(status)
            viewDetailsText.visibility = View.VISIBLE
            //            getFeedback()
            applicationStatus.setBackgroundResource(R.drawable.campaign_rejected)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_rejected)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_reject),
                    Toast.LENGTH_SHORT
                ).show()
                if (apiGetResponse!!.deliverableTypes!!.get(0) == 5) {
                    labelText.text = it.resources.getString(R.string.label_survey_reject)
                    submitBtn.text = it.resources.getString(R.string.detail_bottom_survey_rejected)
                } else {
                    labelText.text = it.resources.getString(R.string.label_campaign_reject)
                    submitBtn.text = it.resources.getString(R.string.detail_bottom_view_other)
                }
            }
        } else if (status == 7) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_completed)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_completed)
                labelText.text = it.resources.getString(R.string.label_campaign_completed)
                if (apiGetResponse!!.deliverableTypes!!.get(0) == 5) {
                    submitBtn.text =
                        it.resources.getString(R.string.detail_bottom_survey_completed)
                } else {
                    submitBtn.text =
                        it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
                }
            }
        } else if (status == 9) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text =
                    it.resources.getString(R.string.campaign_list_proof_moderation)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_proof_moderation),
                    Toast.LENGTH_SHORT
                ).show()
                labelText.text = it.resources.getString(R.string.label_campaign_proof_moderation)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
            }
        } else if (status == 10 || status == 17) {
            hideShowReferral(status)
            viewDetailsText.visibility = View.VISIBLE
            //            getFeedback()
            applicationStatus.setBackgroundResource(R.drawable.campaign_proof_rejected_bg)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_list_proof_reject)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.toast_campaign_proof_reject),
                    Toast.LENGTH_SHORT
                ).show()
                labelText.text = it.resources.getString(R.string.label_campaign_proof_reject)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
            }
        }
        /*making visible invisible tracker button*/
        if (status == 0 || status == 4 || status == 5 || status == 6 || status == 1) {
            if (status == 5) {
                if (isRewardAdded.equals("1", true)) {
                    txtTrackerStatus.visibility = View.GONE
                }
            } else {
                txtTrackerStatus.visibility = View.GONE
            }
        } else {
            txtTrackerStatus.visibility = View.VISIBLE
        }
    }

    private fun getFeedback() {
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getFeedback(
            this.id!!
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<CampaignFeedBack>> {

            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<CampaignFeedBack>) {
                try {
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiFeedbackResponse = response.data!!.result
                        var feedback = apiFeedbackResponse!!.feedback.get(0)
                        feedback = "<font color=#D03F56>Feedback- </font>" + feedback
                        showFeedbackDialog(feedback)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                activity?.let {
                    ToastUtils.showToast(it, "something went wrong")
                }
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    private fun showFeedbackDialog(feedback: String) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.campaign_rejection_feedback)
        dialog.setCancelable(false)
        val rejectionText = dialog.findViewById<TextView>(R.id.rejection_text)
        rejectionText.setText(Html.fromHtml(feedback))
        val okBtn = dialog.findViewById<TextView>(R.id.cross)
        okBtn.setOnClickListener {
            dialog.cancel()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getHelpDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.campaign_gethelp_dialog)
        dialog.setCancelable(false)
        val writeEmail = dialog.findViewById<TextView>(R.id.email)
        writeEmail.setOnClickListener {
            try {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@momspresso-mymoney.com", null
                )
                )
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
            dialog.cancel()
        }
        val helpdesk = dialog.findViewById<TextView>(R.id.mymoney_helpdesk)
        helpdesk.setOnClickListener {
            val groupMembershipStatus = GroupMembershipStatus(this)
            groupMembershipStatus.checkMembershipStatus(
                102,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            )
            dialog.cancel()
        }
        val okBtn = dialog.findViewById<TextView>(R.id.cross)
        okBtn.setOnClickListener {
            dialog.cancel()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun hideShowReferral(status: Int) {
        if (status == 1 && apiGetResponse!!.showRefferField == true) {
            viewLine.visibility = View.VISIBLE
            referCode.visibility = View.VISIBLE
            referCodeApply.visibility = View.VISIBLE
            referCodeHeader.visibility = View.VISIBLE
            if (!apiGetResponse!!.referralCode.isNullOrEmpty()) {
                referCode.setText(apiGetResponse!!.referralCode)
                referCodeApply.setText(resources.getString(R.string.campaign_details_applied))
                referCodeApply.isEnabled = false
                referCode.isEnabled = false
            }
        } else {
            viewLine.visibility = View.GONE
            referCode.visibility = View.GONE
            referCodeApply.visibility = View.GONE
            referCodeHeader.visibility = View.GONE
        }
    }

    fun unapplyCampaignDialog() {
        activity.let {
            Utils.campaignEvent(
                it,
                "Campaign Detail",
                "Campaign Detail",
                "Show_Earnings",
                apiGetResponse!!.name,
                "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "CTA_Show_Earnings"
            )
            val dialog = it?.let { it1 -> Dialog(it1) }
            dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_unapply_campaign)
            dialog.setCancelable(true)
            val noBtn = dialog.findViewById<TextView>(R.id.btn_no)
            val yesBtn = dialog.findViewById<TextView>(R.id.btn_yes)
            noBtn.setOnClickListener {
                dialog.dismiss()
            }

            yesBtn.setOnClickListener {
                var participateRequest = CampaignParticipate()
                participateRequest!!.user_id = userId
                participateRequest.campaign_id = this!!.id!!
                val retro = BaseApplication.getInstance().retrofit
                val campaignAPI = retro.create(CampaignAPI::class.java)
                val call = campaignAPI.unapplyCampaign(participateRequest)
                call.enqueue(withdrawParticipateCampaign)
                count = 0
                dialog.dismiss()
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    fun showRewardDialog() {
        Utils.pushGenericEvent(
            activity,
            "CTA_CampaignDetail_Register",
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            "CampaignDetailFragment"
        )
        Utils.campaignEvent(
            activity, "Rewards 1st screen", "Campaign Detail", "Rewards_popup_ok",
            apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity),
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            System.currentTimeMillis().toString(), "Show_Rewards_Detail"
        )
        val intent = Intent(context, RewardsContainerActivity::class.java)
        intent.putExtra("isComingfromCampaign", true)
        intent.putExtra("pageLimit", 2)
        startActivityForResult(intent, REWARDS_FILL_FORM_REQUEST)
    }

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds * 1000
        return formatter.format(calendar.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REWARDS_FILL_FORM_REQUEST -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    bottomLayout.setBackgroundColor(resources.getColor(R.color.app_red))
                    isRewardAdded =
                        SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
                    if (isRewardAdded.equals("1", true)) {
                        val participateRequest = CampaignParticipate()
                        participateRequest.user_id = userId
                        participateRequest.campaign_id = this!!.id!!
                        val retro = BaseApplication.getInstance().retrofit
                        val campaignAPI = retro.create(CampaignAPI::class.java)
                        val call = campaignAPI.postRegisterCampaign(participateRequest)
                        call.enqueue(participateCampaign)
                        //   fetchForYou()
                        defaultCampaignShow = true
                    }
                }
                SURVEY_CAMPAIGN_REQUEST -> {
                    fetchCampaignDetail()
                }
            }
        }
    }

    private fun fetchForYou() {
        // showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getForYouStatus(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BasicResponse> {
            override fun onNext(response: BasicResponse) {
                if (response.code == 200 && response.data != null && response.status == Constants.SUCCESS) {
                    if (response.data.result != null && response.data.result.recm_status != null) {
                        forYouStatus = response.data.result.recm_status
                    }
                }
                fetchCampaignDetail()
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

    private suspend fun checkInstagramHandle(): Boolean {
        val job = CoroutineScope(Dispatchers.Main + handler).launch {
            userId?.let {
                val socialAccountsDetailData =
                    BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java)
                        .getInstagramHandle(
                            it,
                            3
                        )
                socialAccountsDetailData.data?.result?.let { it1 ->
                    socialAccountsDetail = it1
                    if (socialAccountsDetailData.code == 200 && socialAccountsDetailData.status == Constants.SUCCESS) {
                        if (socialAccountsDetailData.data?.result?.socialAccounts.isNullOrEmpty()) {
                            showInstPopUpFlag = true
                        } else {
                            socialAccountsDetailData.data?.result?.socialAccounts?.forEach {
                                if (it.platform_name == AppConstants.MEDIUM_INSTAGRAM && it.acc_link.isNullOrBlank()) {
                                    showInstPopUpFlag = true
                                } else if (it.platform_name == AppConstants.MEDIUM_INSTAGRAM && !it.acc_link.isNullOrBlank()) {
                                    txtTrackerStatus.visibility = View.VISIBLE
                                    if (apiGetResponse!!.deliverableTypes!!.get(0) == 5) {
                                        //                                    submitBtn.text = resources.getString(R.string.detail_scroll_survey
                                        updateLoaderTextHandler.post(updateLoaderTextRunnable)
                                        //                                        showProgressDialog(resources.getString(R.string.please_wait))
                                        val handler = Handler()
                                        handler.postDelayed({
                                            updateLoaderTextHandler.removeCallbacks(
                                                updateLoaderTextRunnable
                                            )
                                            removeProgressDialog()
                                            fetchCampaignDetail()
                                        }, 5000)
                                    } else {
                                        submitBtn.setText(resources.getString(R.string.detail_bottom_applied))
                                        Toast.makeText(
                                            context,
                                            resources.getString(R.string.toast_campaign_applied),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        labelText.setText(resources.getString(R.string.label_campaign_applied))
                                    }
                                    unapplyCampaign.visibility = View.VISIBLE
                                    showInstPopUpFlag = false
                                } else {
                                    return@forEach
                                }
                            }
                        }
                    }
                }
            }
        }
        job.join()
        return showInstPopUpFlag
    }

    var updateLoaderTextRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                showProgressDialog(updateLoaderTextArray.get(count))
                System.out.println("count bahar-------- " + count)
                if (count < 2)
                    count++
                updateLoaderTextHandler.postDelayed(this, TIME_DELAY)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
    }

    private suspend fun showInstaHandlePopUp() {
        instaHandlePopUpView.visibility = View.VISIBLE
        Utils.campaignEvent(
            activity, "Campaign_Detail_Fragment", "Campaign Detail", "Instagram_popup",
            apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity),
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            System.currentTimeMillis().toString(), "Show_IG_popup"
        )
        val confimTextView = containerView.findViewById<TextView>(R.id.confirmTextView)
        confimTextView.setOnClickListener {
            Utils.campaignEvent(
                activity,
                "Campaign_Detail_Fragment",
                "Campaign Detail",
                "Instagram_popup_confirm_text",
                apiGetResponse!!.name,
                "android",
                SharedPrefUtils.getAppLocale(activity),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "CTA_IG_popup"
            )
            if (isValid().isNotBlank()) {
                socialAccountsDetail.socialAccounts?.forEach {
                    if (it.platform_name == AppConstants.MEDIUM_INSTAGRAM) {
                        it.acc_link =
                            containerView.findViewById<EditText>(R.id.instaHandleEditTextView)
                                .text.toString()
                    }
                }
                instaHandlePopUpView.visibility = View.GONE
                CoroutineScope(Dispatchers.Main + handler).launch {
                    userId?.let {
                        val response =
                            BaseApplication.getInstance()
                                .retrofit.create(RewardsAPI::class.java)
                                .sendInstageamHandle(
                                    it,
                                    socialAccountsDetail,
                                    3
                                )
                        if (response.code == 200 && response.status == Constants.SUCCESS) {
                            txtTrackerStatus.visibility = View.VISIBLE
                            submitBtn.setText(resources.getString(R.string.detail_bottom_applied))
                            Toast.makeText(
                                context,
                                resources.getString(R.string.toast_campaign_applied),
                                Toast.LENGTH_SHORT
                            ).show()
                            labelText.setText(resources.getString(R.string.label_campaign_applied))
                            unapplyCampaign.visibility = View.VISIBLE
                        } else {
                            ToastUtils.showToast(activity, response.reason)
                            instaHandlePopUpView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun isValid(): String {
        val instaHandleEditTextView =
            containerView.findViewById<EditText>(R.id.instaHandleEditTextView) // ^([A-Za-z0-9._](?:(?:[A-Za-z0-9._]|(?:\.(?!\.))){2,28}(?:[A-Za-z0-9._]))?)$
        val instaHandle = instaHandleEditTextView.text.toString()
        val pattern =
            Pattern.compile("^([A-Za-z0-9._](?:(?:[A-Za-z0-9._]|(?:\\.(?!\\.))){2,28}(?:[A-Za-z0-9._]))?)\$")
        val matcher = pattern.matcher(instaHandle)
        if (matcher.matches()) {
            return instaHandle
        } else {
            ToastUtils.showToast(
                activity,
                getString(R.string.enter_valid_instagram_campaign_detail_fragment_popup)
            )
        }
        return ""
    }

    override fun onStart() {
        super.onStart()
        shimmer1.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }

    override fun onMembershipStatusFetchFail() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onMembershipStatusFetchSuccess(body: GroupsMembershipResponse?, groupId: Int) {
        var userType: String? = null
        if (body!!.data.result != null && !body.data.result.isEmpty()) {
            if (body.data.result[0].isAdmin == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN
            } else if (body.data.result[0].isModerator == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR
            }
        }

        if (AppConstants.GROUP_MEMBER_TYPE_MODERATOR != userType && AppConstants.GROUP_MEMBER_TYPE_ADMIN != userType) {
            if ("male".equals(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).gender,
                    ignoreCase = true
                ) ||
                "m".equals(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).gender,
                    ignoreCase = true
                )) {
                if (isAdded) {
                    Toast.makeText(
                        activity,
                        getString(R.string.women_only),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID
                        .contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)) {
                    return
                }
            }
        }

        if (body.data.result == null || body.data.result.isEmpty()) {
            val intent = Intent(
                activity,
                GroupsSummaryActivity::class.java
            )
            intent.putExtra("groupId", 102)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED == body.data.result[0].status) {
            if (isAdded) {
                Toast.makeText(
                    activity,
                    getString(R.string.groups_user_blocked_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER == body.data.result[0].status) {
            val intent = Intent(
                activity,
                GroupDetailsActivity::class.java
            )
            intent.putExtra("groupId", 102)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
            Utils.groupsEvent(
                activity,
                "Home Screen",
                "Groups you are member of_group card",
                "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "Discussion Page",
                "",
                ""
            )
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
            == body.data.result[0].status) {
            val intent = Intent(
                activity,
                GroupsSummaryActivity::class.java
            )
            intent.putExtra("groupId", 102)
            intent.putExtra("pendingMembershipFlag", true)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        } else {
            Utils.groupsEvent(
                activity,
                "Home Screen",
                "other groups_group card",
                "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                System.currentTimeMillis().toString(),
                "About Page",
                "",
                ""
            )
            val intent = Intent(
                activity,
                GroupsSummaryActivity::class.java
            )
            intent.putExtra("groupId", 102)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        }
    }
}
