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
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.BaseResponseModel
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.campaignmodels.ParticipateCampaignResponse
import com.mycity4kids.models.request.CampaignParticipate
import com.mycity4kids.models.request.CampaignReferral
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.CampaignDetailAdapter
import com.mycity4kids.ui.campaign.BasicResponse
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.mymoneytracker.activity.TrackerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

const val REWARDS_FILL_FORM_REQUEST = 1000

class CampaignDetailFragment : BaseFragment() {
    private lateinit var scrollView2: NestedScrollView
    private var campaignList = mutableListOf<CampaignDataListResult>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CampaignDetailAdapter
    private var apiGetResponse: CampaignDetailResult? = null
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
    private lateinit var crossDemo: ImageView
    private lateinit var demoVideoLayout: FrameLayout
    private lateinit var videoView: VideoView
    private lateinit var demoUploadLayout: RelativeLayout
    private lateinit var playDemoIcon: ImageView
    private var position: Int = 0;
    private var mediaController: MediaController? = null
    private var forYouStatus: Int = 0
    private var userId: String? = null
    private lateinit var defaultCampaignPopUp: View
    private var defaultCampaignShow: Boolean = false
    private val urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
    private var spannable: SpannableString? = null

    private lateinit var default_campaign_header: ImageView
    private lateinit var default_brand_img: ImageView
    private lateinit var default_brand_name: TextView
    private lateinit var default_campaign_name: TextView
    private lateinit var default_submission_status: TextView
    private lateinit var cancel: ImageView
    private lateinit var default_participateTextView: TextView
    private lateinit var upperTextHeader: TextView
    private lateinit var lowerTextHeader: TextView
    private var comingFrom: String? = null


    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.campaign_detail_activity, container, false)
        id = arguments!!.getInt("id")
        comingFrom = arguments!!.getString("comingFrom")
        userId = SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
        defaultCampaignPopUp = containerView.findViewById(R.id.include)

        if (isRewardAdded.equals("1", true)) {
            fetchForYou()
        } else {
            fetchCampaignDetail()
        }
        initializeXml()
        backIcon = containerView.findViewById(R.id.back)
        linearLayoutManager = LinearLayoutManager(activity as Context?, RecyclerView.VERTICAL, false)
        if (SharedPrefUtils.getDemoVideoSeen(BaseApplication.getAppContext())) {
            demoUploadLayout.visibility = View.GONE
            playDemoIcon.visibility = View.VISIBLE
        }
        backIcon.setOnClickListener {
            Utils.campaignEvent(activity, "Campaign Listing", "Campaign Detail", "Back", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            activity!!.onBackPressed()
        }
        shareText.setOnClickListener {
            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }
        }

        default_participateTextView.setOnClickListener {

            //  setResponseData(defaultapigetResponse)

            (activity as CampaignContainerActivity).addCampaginDetailFragment(defaultapigetResponse?.id!!, "defaultCampaign")


        }

        getHelp.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@momspresso-mymoney.com", null))
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }

        txtTrackerStatus.setOnClickListener {
            var intent = Intent(activity, TrackerActivity::class.java)
            intent.putExtra("campaign_id", id!!)
            intent.putExtra("brand_name", apiGetResponse!!.brandDetails!!.name)
            intent.putExtra("campaign_name", apiGetResponse!!.name)
            intent.putExtra("total_payout", apiGetResponse!!.totalPayout!!.toInt())
            intent.putExtra("image_url", apiGetResponse!!.brandDetails!!.imageUrl)
            startActivity(intent)
        }

        referCodeApply.setOnClickListener {
            applyCode()
        }


        cancel.setOnClickListener {
            defaultCampaignPopUp.visibility = View.GONE
        }

        crossDemo.setOnClickListener {
            //            if (videoView.isPlaying)
            videoView.stopPlayback()
            videoView.setMediaController(null)
            mediaController = null
            demoVideoLayout.visibility = View.GONE
        }

        demoUpload.setOnClickListener {
            demoVideoLayout.visibility = View.VISIBLE
//            demoUploadLayout.visibility = View.GONE
//            playDemoIcon.visibility = View.VISIBLE
            SharedPrefUtils.setDemoVideoSeen(BaseApplication.getAppContext(), true)
            playVideo()
//            (activity as CampaignContainerActivity).addCampaignDemoUploadFragment()
        }

        playDemoIcon.setOnClickListener {
            demoVideoLayout.visibility = View.VISIBLE
            playVideo()
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

        return containerView
    }

    private fun setController() {
        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = MediaController(activity);

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController!!.setAnchorView(videoView);


            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }
    }

    private fun playVideo() {
        try {
            // ID of video file.
            var videoUrl: String = "https://static.momspresso.com/mymoney/assets/how-to.mp4"
            var video: Uri
            video = Uri.parse(videoUrl);
            videoView.setVideoURI(video);
            setController()

        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

/*
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }*/
        videoView.start()

        videoView.setOnCompletionListener { mp ->
            mp.isLooping = false
            demoVideoLayout.visibility = View.GONE
        }
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
        txtTrackerStatus = containerView.findViewById(R.id.txtTrackerStatus);
        toolbar = containerView.findViewById(R.id.toolbar)
        scrollView2 = containerView.findViewById(R.id.scrollView2)
        shimmer1 = containerView.findViewById(R.id.shimmer1)
        demoUpload = containerView.findViewById(R.id.demo_upload)
        crossDemo = containerView.findViewById(R.id.cross_demo)
        demoVideoLayout = containerView.findViewById(R.id.demo_video_layout)
        videoView = containerView.findViewById(R.id.videoView)
        demoUploadLayout = containerView.findViewById(R.id.demo_upload_layout)
        playDemoIcon = containerView.findViewById(R.id.play_demo_icon)
    }

    private fun fetchCampaignDetail() {
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getCampaignDetail(this!!.id!!, 2.0).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CampaignDetailResult>> {


            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<CampaignDetailResult>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response.data!!.result != null) {
                    parentConstraint.visibility = View.VISIBLE
                    apiGetResponse = response.data!!.result
                    setResponseData(apiGetResponse)
                    shimmer1.visibility = View.GONE
                    shimmer1.stopShimmerAnimation()
                    toolbar.visibility = View.VISIBLE
                    scrollView2.visibility = View.VISIBLE
                    labelText.visibility = View.VISIBLE
                    bottomLayout.visibility = View.VISIBLE
                } else {
                }
            }

            override fun onError(e: Throwable) {
                ToastUtils.showToast(activity, "something went wrong")
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    private fun setResponseData(apiGetResponsee: CampaignDetailResult?) {
        apiGetResponse = apiGetResponsee
        Picasso.with(context).load(apiGetResponse!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(bannerImg)
        Picasso.with(context).load(apiGetResponse!!.brandDetails!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(brandImg)
        brandName.setText(apiGetResponse!!.brandDetails!!.name)
        campaignName.setText(apiGetResponse!!.name)
        amount.setText("" + (apiGetResponse!!.maxSlots?.minus(apiGetResponse!!.totalUsedSlots!!)))
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

        showRewardText.setOnClickListener {
            showDialog()
        }

        unapplyCampaign.setOnClickListener {
            val popupwindow_obj = popupDisplay()
            popupwindow_obj.showAsDropDown(unapplyCampaign, -140, -140)
        }

        bottomLayout.setOnClickListener {
            setClickAction()

        }
        setLabels()
    }

    fun popupDisplay(): PopupWindow {

        val popupWindow = PopupWindow(context)

        val inflater = (context as CampaignContainerActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
            override fun onClick(p0: View?) {
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
            spannable!!.setSpan(clickableSpan, matchStart, matchEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        };
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
//        textView.highlightColor = Color.TRANSPARENT
    }

    fun demoVideoLayout(): FrameLayout {
//        if (videoView.isPlaying)
        videoView.stopPlayback()
        videoView.setMediaController(null)
        mediaController = null
        return demoVideoLayout
    }

    private fun setClickAction() {
        if (submitBtn.text == resources.getString(R.string.check_ypur_eligibility)) {
            showRewardDialog()
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_apply_now)) {
            Utils.campaignEvent(activity, "Campaign Listing", "Campaign Detail", "applyNow", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            if ("defaultCampaign".equals(comingFrom))
                SharedPrefUtils.setDefaultCampaignShownFlag(BaseApplication.getAppContext(), true)

            var participateRequest = CampaignParticipate()
            participateRequest!!.user_id = userId
            participateRequest.campaign_id = this!!.id!!
            val retro = BaseApplication.getInstance().retrofit
            val campaignAPI = retro.create(CampaignAPI::class.java)
            val call = campaignAPI.postRegisterCampaign(participateRequest)
            call.enqueue(participateCampaign)

        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_share)) {
            Utils.campaignEvent(activity, "Campaign Listing", "Campaign Detail", "Share", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")

            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }
        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_share_momspresso_reward)) {
            Utils.campaignEvent(activity, "Campaign Listing", "Campaign Detail", "Share_Rewards_Sticky_Bottom", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "CTA_Momspresso_Rewards_Share")

            val shareIntent = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("https://www.momspresso.com/mymoney?referrer=" + userId)
                    .intent

            if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                context!!.startActivity(shareIntent)
            }

        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_view_other)) {
            (context as CampaignContainerActivity).onBackPressed()
            Utils.campaignEvent(activity, "Campaign Listing", "Campaign Detail", "View other campaigns", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")

        } else if (submitBtn.text == resources.getString(R.string.detail_bottom_submit_proof)) {
            Utils.campaignEvent(activity, "Proof Submission", "Campaign Detail", "Submit Proof", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Submission")

            (activity as CampaignContainerActivity).addAddProofFragment(apiGetResponse!!.id!!, (apiGetResponse!!.deliverableTypes as ArrayList<Int>?)!!, status)
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
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    txtTrackerStatus.visibility = View.VISIBLE
                    submitBtn.setText(resources.getString(R.string.detail_bottom_applied))
                    Toast.makeText(context, resources.getString(R.string.toast_campaign_applied), Toast.LENGTH_SHORT).show()
                    labelText.setText(resources.getString(R.string.label_campaign_applied))
                    unapplyCampaign.visibility = View.VISIBLE
                } else {
                    if (!SharedPrefUtils.isDefaultCampaignShown(BaseApplication.getAppContext()))
                        fetchDefaultCampaign()
                    submitBtn.text = resources.getString(R.string.detail_bottom_share)
                    Toast.makeText(context, responseData.reason, Toast.LENGTH_SHORT).show()////////////////////////////////////////////////
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


    fun setDefaultCampaignValues() {
        upperTextHeader.text = resources.getString(R.string.sorry_not_eligible)
        lowerTextHeader.text = resources.getString(R.string.try_following_campaign)
        Picasso.with(context).load(defaultapigetResponse!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(default_campaign_header)
        Picasso.with(context).load(defaultapigetResponse!!.brandDetails!!.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(default_brand_img)
        default_brand_name.setText(defaultapigetResponse!!.brandDetails!!.name)
        default_campaign_name.setText(defaultapigetResponse!!.name)
        default_submission_status.text = resources.getString(R.string.campaign_details_apply_now)
    }


    val withdrawParticipateCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(call: Call<ParticipateCampaignResponse>, response: retrofit2.Response<ParticipateCampaignResponse>) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
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

    val referCampaign = object : Callback<ParticipateCampaignResponse> {
        override fun onResponse(call: Call<ParticipateCampaignResponse>, response: retrofit2.Response<ParticipateCampaignResponse>) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200) {
                    if (Constants.SUCCESS == responseData.status) {
                        Toast.makeText(context, responseData.data.get(0).msg, Toast.LENGTH_SHORT).show()
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
        unapplyCampaign.visibility = View.GONE
        if (status == 0) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_expired)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_expired)
                labelText.text = it.resources.getString(R.string.label_campaign_expired)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
            }
        } else if (status == 1 || status == 18) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_apply_now)
                labelText.text = AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                submitBtn.text = it.resources.getString(R.string.detail_bottom_apply_now)
            }
            labelText.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (status == 2) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_submission_open)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_started), Toast.LENGTH_SHORT).show()
            }
            labelText.visibility = View.GONE
        } else if (status == 21) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_submission_open)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_not_started), Toast.LENGTH_SHORT).show()
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
                labelText.text = it.resources.getString(R.string.label_campaign_not_started) + " " + getDate(apiGetResponse!!.startTime!!, "dd MMM yyyy") + ". Please wait for campaign to start to submit proofs."
            }
            appliedTag.visibility = View.VISIBLE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 22 || status == 16 || status == 17) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.setText(context!!.resources.getString(R.string.campaign_details_submission_open))
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_started), Toast.LENGTH_SHORT).show()
            }
            labelText.visibility = View.GONE
        } else if (status == 3) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscribed)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_applied)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_applied), Toast.LENGTH_SHORT).show()
                labelText.text = it.resources.getString(R.string.label_campaign_applied)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
            }
            appliedTag.visibility = View.VISIBLE
            unapplyCampaign.visibility = View.VISIBLE
        } else if (status == 4) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_submission_full)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_application_full)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_full), Toast.LENGTH_SHORT).show()
                labelText.text = it.resources.getString(R.string.label_campaign_full)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
            }
        } else if (status == 5) {


            hideShowReferral(status)
            if (isRewardAdded.isEmpty() || isRewardAdded.equals("0")) {
                applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                context?.let {
                    applicationStatus.text = it.resources.getString(R.string.campaign_details_apply_now)
                    labelText.text = AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                    submitBtn.text = it.resources.getString(R.string.check_ypur_eligibility)
                    bottomLayout.setBackgroundColor(it.resources.getColor(R.color.campaign_listing_light_green_bottom))
                }
                labelText.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                if (isRewardAdded.equals("1", true)) {
                    if (forYouStatus == 0) {
                        applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                        context?.let {
                            applicationStatus.text = it.resources.getString(R.string.campaign_details_apply_now)
                            Toast.makeText(it, it.resources.getString(R.string.checking_elegiblity), Toast.LENGTH_SHORT).show()
                            labelText.text = AppUtils.fromHtml(it.resources.getString(R.string.label_campaign_apply))
                            submitBtn.text = it.resources.getString(R.string.detail_bottom_apply_now)
                        }
                    } else {
                        applicationStatus.setBackgroundResource(R.drawable.subscribe_now)
                        context?.let {
                            applicationStatus.text = it.resources.getString(R.string.campaign_details_apply_now)
                            Toast.makeText(it, it.resources.getString(R.string.toast_not_elegible), Toast.LENGTH_SHORT).show()
                            labelText.text = it.resources.getString(R.string.label_campaign_not_eligible)
                            submitBtn.text = it.resources.getString(R.string.detail_bottom_share)
                        }
                    }
                }
            }
        } else if (status == 6) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_rejected)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_rejected)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_reject), Toast.LENGTH_SHORT).show()
                labelText.text = it.resources.getString(R.string.label_campaign_reject)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_view_other)
            }
        } else if (status == 7) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_completed)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_details_completed)
                labelText.text = it.resources.getString(R.string.label_campaign_completed)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_share_momspresso_reward)
            }
        } else if (status == 9) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_subscription_open)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_list_proof_moderation)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_proof_moderation), Toast.LENGTH_SHORT).show()
                labelText.text = it.resources.getString(R.string.label_campaign_proof_moderation)
                submitBtn.text = it.resources.getString(R.string.detail_bottom_submit_proof)
            }
        } else if (status == 10) {
            hideShowReferral(status)
            applicationStatus.setBackgroundResource(R.drawable.campaign_proof_rejected_bg)
            context?.let {
                applicationStatus.text = it.resources.getString(R.string.campaign_list_proof_reject)
                Toast.makeText(it, it.resources.getString(R.string.toast_campaign_proof_reject), Toast.LENGTH_SHORT).show()
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

    fun showDialog() {
        Utils.campaignEvent(activity, "Campaign Detail", "Campaign Detail", "Show_Earnings", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "CTA_Show_Earnings")

        if (activity != null) {
            val dialog = Dialog(activity)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_show_rewards)
            dialog.setCancelable(true)
            val showAmount = dialog.findViewById<TextView>(R.id.show_amount)
            if (apiGetResponse!!.isFixedAmount == 1) {
                var amount: Double? = (apiGetResponse!!.amount)
                showAmount.setText("Rs." + amount?.toInt())
            } else {
                showAmount.setText("Rs." + (apiGetResponse!!.minAmount) + "-" + "Rs." + (apiGetResponse!!.maxAmount))
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }

    fun unapplyCampaignDialog() {
        Utils.campaignEvent(activity, "Campaign Detail", "Campaign Detail", "Show_Earnings", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "CTA_Show_Earnings")

        if (activity != null) {
            val dialog = Dialog(activity)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
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
                dialog.dismiss()
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }


    fun showRewardDialog() {
        Utils.campaignEvent(activity, "Rewards 1st screen", "Campaign Detail", "Rewards_popup_ok", apiGetResponse!!.name, "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Rewards_Detail")
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
                    isRewardAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext())
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
            }
        }
    }


    private fun fetchForYou() {
        // showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getForYouStatus(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BasicResponse> {
            override fun onNext(response: BasicResponse) {
                if (response.code == 200 && response.data != null && response.status == "success") {
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

    override fun onStart() {
        super.onStart()
        shimmer1.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }


}





