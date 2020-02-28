package com.mycity4kids.profile

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.crashlytics.android.Crashlytics
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.MilestonesAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsShareReferralCodeActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.PermissionUtil
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class MilestonesDialogFragment : DialogFragment(), View.OnClickListener {

    val REQUEST_GALLERY_PERMISSION = 1
    val PERMISSIONS_INIT = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val sharableMilestoneImageName = "milestone"

    private var milestoneData: MilestonesResult? = null

    private lateinit var rootLayout: RelativeLayout
    private lateinit var milestoneImageView: ImageView
    private lateinit var milestoneBgImageView: ImageView
    private lateinit var milestoneTitleTextView: TextView
    private lateinit var milestoneDescTextView: TextView
    private lateinit var viewContentTextView: TextView
    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var instagramShareImageView: ImageView
    private lateinit var genericShareImageView: ImageView
    private lateinit var shareJoyContainer: RelativeLayout
    private lateinit var shareContainer: ConstraintLayout
    private lateinit var milestonesSharableCard: BadgeShareCardWidget
    private lateinit var milestonesShimmerContainer: ShimmerFrameLayout

    var userId: String? = null
    var milestoneId: String? = null
    var shareMedium: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.milestones_dialog_fragment, container,
                false)

        rootLayout = rootView.findViewById(R.id.rootLayout)
        milestoneImageView = rootView.findViewById(R.id.milestoneImageView)
        milestonesSharableCard = rootView.findViewById(R.id.milestonesSharableCard)
        milestoneBgImageView = rootView.findViewById(R.id.milestoneBgImageView)
        milestoneTitleTextView = rootView.findViewById(R.id.milestoneTitleTextView)
        milestoneDescTextView = rootView.findViewById(R.id.milestoneDescTextView)
        viewContentTextView = rootView.findViewById(R.id.viewContentTextView)
        shareJoyContainer = rootView.findViewById(R.id.shareJoyContainer)
        shareContainer = rootView.findViewById(R.id.shareContainer)
        whatsappShareImageView = rootView.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = rootView.findViewById(R.id.facebookShareImageView)
        instagramShareImageView = rootView.findViewById(R.id.instagramShareImageView)
        genericShareImageView = rootView.findViewById(R.id.genericShareImageView)
        milestonesShimmerContainer = rootView.findViewById(R.id.milestonesShimmerContainer)

        whatsappShareImageView.setOnClickListener(this)
        facebookShareImageView.setOnClickListener(this)
        instagramShareImageView.setOnClickListener(this)
        genericShareImageView.setOnClickListener(this)
        viewContentTextView.setOnClickListener(this)

        val bundle = arguments
        userId = bundle?.getString(Constants.USER_ID)
        milestoneId = bundle?.getString("id")

        if (userId.isNullOrBlank() || milestoneId.isNullOrBlank()) {
            activity?.let {
                ToastUtils.showToast(it, it.getString(R.string.empty_screen), Toast.LENGTH_SHORT)
            }
            dismiss()
        }
        if (AppUtils.isPrivateProfile(userId)) {
            shareContainer.visibility = View.VISIBLE
            shareJoyContainer.visibility = View.VISIBLE
            viewContentTextView.visibility = View.GONE
        } else {
            if (BuildConfig.DEBUG) {
                shareContainer.visibility = View.VISIBLE
                shareJoyContainer.visibility = View.VISIBLE
            } else {
                shareContainer.visibility = View.GONE
                shareJoyContainer.visibility = View.GONE
            }
        }

        milestonesShimmerContainer.startShimmerAnimation()
        fetchMilestoneDetail(userId!!, milestoneId!!)

        return rootView
    }

    private fun fetchMilestoneDetail(userId: String, milestoneId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val milestoneAPI = retrofit.create(MilestonesAPI::class.java)
        val milestoneListResponseCall = milestoneAPI.getMilestoneDetail(userId, milestoneId)
        milestoneListResponseCall.enqueue(object : Callback<MilestonesResponse> {
            override fun onResponse(call: Call<MilestonesResponse>, response: retrofit2.Response<MilestonesResponse>) {
                try {
                    milestonesShimmerContainer.visibility = View.GONE
                    if (response.body() == null) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                        return
                    }
                    val responseModel = response.body() as MilestonesResponse
                    if (responseModel.code == 200 && Constants.SUCCESS == responseModel.status) {
                        if (responseModel.data != null) {
                            populateMilestoneDetails(userId, responseModel.data.result)
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<MilestonesResponse>, t: Throwable) {
                milestonesShimmerContainer.visibility = View.GONE
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }

    private fun populateMilestoneDetails(userId: String, result: List<MilestonesResult>?) {
        activity?.let {
            milestoneData = result?.get(0)
            Picasso.get().load(milestoneData?.milestone_bg_url).error(R.drawable.default_article)
                    .fit().into(milestoneBgImageView)
            if (milestoneData?.item_type == AppConstants.CONTENT_TYPE_MYMONEY) {
                milestoneTitleTextView.text = getString(R.string.milestones_dialog_title,
                        milestoneData?.meta_data?.content_info?.payment_value?.toInt())
                val terms: String = getString(R.string.all_terms_conditions)
                val fullDesc: String = getString(R.string.milestones_share_joy, getString(R.string.all_terms_conditions))
                val spannableStringBuilder = SpannableStringBuilder(fullDesc)
                val click = TermsConditionTextClick()
                spannableStringBuilder.setSpan(click, fullDesc.indexOf(terms),
                        fullDesc.indexOf(terms) + terms.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                milestoneDescTextView.text = spannableStringBuilder
                milestoneDescTextView.movementMethod = LinkMovementMethod.getInstance()
            } else {
                Picasso.get().load(milestoneData?.milestone_image_url).error(R.drawable.default_article)
                        .fit().into(milestoneImageView)
                milestoneTitleTextView.text = milestoneData?.milestone_title
                milestoneDescTextView.text = milestoneData?.milestone_desc
            }
            if (AppUtils.isPrivateProfile(userId)) {
                Utils.pushProfileEvents(it, "Show_Private_Milestone_Detail", "MilestonesDialogFragment",
                        "-", milestoneData?.milestone_name)
            } else {
                Utils.pushProfileEvents(it, "Show_Public_Milestone_Detail", "MilestonesDialogFragment",
                        "-", milestoneData?.milestone_name)
                when {
                    result?.get(0)?.item_type == AppConstants.CONTENT_TYPE_ARTICLE -> {
                        viewContentTextView.visibility = View.VISIBLE
                    }
                    result?.get(0)?.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                        viewContentTextView.visibility = View.VISIBLE
                    }
                    result?.get(0)?.item_type == AppConstants.CONTENT_TYPE_VIDEO -> {
                        viewContentTextView.visibility = View.VISIBLE
                    }
                    else -> viewContentTextView.visibility = View.GONE
                }
            }
            milestonesSharableCard.populateMilestonesDetails(milestoneData)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.whatsappShareImageView -> {
                shareMedium = AppConstants.MEDIUM_WHATSAPP
                shareWithWhatsApp()
            }
            view?.id == R.id.facebookShareImageView -> {
                shareMedium = AppConstants.MEDIUM_FACEBOOK
                shareWithFB()
            }
            view?.id == R.id.instagramShareImageView -> {
                shareMedium = AppConstants.MEDIUM_INSTAGRAM
                shareWithInstagram()
            }
            view?.id == R.id.genericShareImageView -> {
                shareMedium = AppConstants.MEDIUM_GENERIC
                shareWithGeneric()
            }
            view?.id == R.id.viewContentTextView -> {
                when {
                    milestoneData?.item_type == AppConstants.CONTENT_TYPE_ARTICLE -> {
                        activity?.let {
                            Utils.pushProfileEvents(it, "CTA_View_Article_Public_Milestone_Detail",
                                    "MilestonesDialogFragment", "View article", milestoneData?.milestone_name)
                            val intent = Intent(it, ArticleDetailsContainerActivity::class.java)
                            intent.putExtra(Constants.ARTICLE_ID, milestoneData?.content_id)
                            startActivity(intent)
                        }
                    }
                    milestoneData?.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                        activity?.let {
                            Utils.pushProfileEvents(it, "CTA_View_Story_Public_Milestone_Detail",
                                    "MilestonesDialogFragment", "View Story", milestoneData?.milestone_name)
                            val intent = Intent(it, ShortStoryContainerActivity::class.java)
                            intent.putExtra(Constants.ARTICLE_ID, milestoneData?.content_id)
                            startActivity(intent)
                        }
                    }
                    milestoneData?.item_type == AppConstants.CONTENT_TYPE_VIDEO -> {
                        activity?.let {
                            Utils.pushProfileEvents(it, "CTA_View_Video_Public_Milestone_Detail",
                                    "MilestonesDialogFragment", "View Video", milestoneData?.milestone_name)
                            val intent = Intent(activity, ParallelFeedActivity::class.java)
                            intent.putExtra(Constants.VIDEO_ID, milestoneData?.content_id)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun shareWithGeneric() {
        activity?.let {
            if (AppConstants.CONTENT_TYPE_MYMONEY == milestoneData?.item_type) {
                if (AppUtils.shareGenericLinkWithSuccessStatus(activity, getString(R.string.all_refer_url,
                                milestoneData?.meta_data?.content_info?.referral_code))) {
                    Utils.pushProfileEvents(it, "CTA_Generic_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "Generic Share", milestoneData?.milestone_name)
                }
            } else {
                if (AppUtils.shareGenericLinkWithSuccessStatus(activity, milestoneData?.milestone_sharing_url)) {
                    Utils.pushProfileEvents(it, "CTA_Generic_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "Generic Share", milestoneData?.milestone_name)
                }
            }
        }
    }

    private fun shareWithInstagram() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/milestone.jpg")
            if (AppUtils.shareImageWithInstagram(it, uri)) {
                Utils.pushProfileEvents(it, "CTA_IG_Share_Private_Milestone_Detail",
                        "MilestonesDialogFragment", "IG Share", milestoneData?.milestone_name)
            }
        }
    }

    private fun shareWithFB() {
        if (AppConstants.CONTENT_TYPE_MYMONEY == milestoneData?.item_type) {
            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                val content = ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(getString(R.string.all_refer_url, milestoneData?.meta_data?.content_info?.referral_code)))
                        .build()
                activity?.let {
                    ShareDialog(it).show(content)
                    Utils.pushProfileEvents(it, "CTA_FB_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "FB Share", milestoneData?.milestone_name)
                }
            }
        } else {
            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                val content = ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(milestoneData?.milestone_sharing_url))
                        .build()
                activity?.let {
                    ShareDialog(it).show(content)
                    Utils.pushProfileEvents(it, "CTA_FB_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "FB Share", milestoneData?.milestone_name)
                }
            }
        }
    }

    private fun shareWithWhatsApp() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val username = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name + " " +
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).last_name
            var contentType: String? = ""
            when (milestoneData?.item_type) {
                AppConstants.CONTENT_TYPE_ARTICLE -> {
                    contentType = "Article"
                }
                AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                    contentType = "Story"
                }
                AppConstants.CONTENT_TYPE_VIDEO -> {
                    contentType = "Video"
                }
                else -> viewContentTextView.visibility = View.GONE
            }
            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/milestone.jpg")
            if (AppConstants.CONTENT_TYPE_MYMONEY == milestoneData?.item_type) {
                if (AppUtils.shareImageWithWhatsApp(it, uri, getString(R.string.milestone_mm_share,
                                milestoneData?.meta_data?.content_info?.payment_value?.toInt(),
                                milestoneData?.meta_data?.content_info?.referral_code, getString(R.string.all_refer_url,
                                milestoneData?.meta_data?.content_info?.referral_code)))) {
                    Utils.pushProfileEvents(it, "CTA_Whatsapp_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "Whatsapp Share", milestoneData?.milestone_name)
                }
            } else {
                if (AppUtils.shareImageWithWhatsApp(it, uri, getString(R.string.milestones_share_text, username, contentType,
                                milestoneData?.meta_data?.content_info?.title, milestoneData?.milestone_name,
                                milestoneData?.milestone_sharing_url))) {
                    Utils.pushProfileEvents(it, "CTA_Whatsapp_Share_Private_Milestone_Detail",
                            "MilestonesDialogFragment", "Whatsapp Share", milestoneData?.milestone_name)
                }
            }
        }
    }

    private fun createSharableImageWhileCheckingPermissions(): Boolean {
        context?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(it,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(it,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                    return true
                } else {
                    if (createSharableCardWithMilestoneName()) return true
                }
            } else {
                if (createSharableCardWithMilestoneName()) return true
            }
        }
        return false
    }

    private fun createSharableCardWithMilestoneName(): Boolean {
        try {
            AppUtils.getBitmapFromView(milestonesSharableCard, sharableMilestoneImageName)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
            return true
        }
        return false
    }

    private fun requestPermissions() {
        activity?.let {
            if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) { requestUngrantedPermissions() }.show()
            } else {
                requestUngrantedPermissions()
            }
        }
    }

    private fun requestUngrantedPermissions() {
        val permissionList = ArrayList<String>()
        context?.let {
            for (s in PERMISSIONS_INIT) {
                if (checkSelfPermission(it, s) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(s)
                }
            }
            val requiredPermission = permissionList.toTypedArray()
            requestPermissions(requiredPermission, REQUEST_GALLERY_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show()
                try {
                    when (shareMedium) {
                        AppConstants.MEDIUM_FACEBOOK -> {
                            shareWithFB()
                        }
                        AppConstants.MEDIUM_WHATSAPP -> {
                            shareWithWhatsApp()
                        }
                        AppConstants.MEDIUM_INSTAGRAM -> {
                            shareWithInstagram()
                        }
                        AppConstants.MEDIUM_GENERIC -> {
                            shareWithGeneric()
                        }
                        else -> {
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    inner class TermsConditionTextClick : ClickableSpan() {
        override fun onClick(widget: View) {
            activity?.let {
                val intent = Intent(it, RewardsShareReferralCodeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
