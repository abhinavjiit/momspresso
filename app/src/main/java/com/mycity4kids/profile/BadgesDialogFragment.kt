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
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.BadgeListResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BadgeAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback

class BadgesDialogFragment : DialogFragment(), View.OnClickListener {

    val REQUEST_GALLERY_PERMISSION = 1
    val PERMISSIONS_INIT = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val sharableBadgeImageName = "badge"

    private var badgeData: BadgeListResponse.BadgeListData.BadgeListResult? = null

    private lateinit var rootLayout: RelativeLayout
    private lateinit var badgeImageView: ImageView
    private lateinit var badgeBgImageView: ImageView
    private lateinit var badgeTitleTextView: TextView
    private lateinit var badgeDescTextView: TextView
    private lateinit var viewContentTextView: TextView
    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var instagramShareImageView: ImageView
    private lateinit var genericShareImageView: ImageView
    private lateinit var shareJoyContainer: RelativeLayout
    private lateinit var shareContainer: ConstraintLayout
    private lateinit var badgesSharableCard: BadgeShareCardWidget
    private lateinit var badgesShimmerContainer: ShimmerFrameLayout

    var userId: String? = null
    var badgeId: String? = null
    var shareMedium: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.badge_dialog_fragment, container,
            false
        )

        rootLayout = rootView.findViewById(R.id.rootLayout)
        badgeImageView = rootView.findViewById(R.id.badgeImageView)
        badgesSharableCard = rootView.findViewById(R.id.badgesSharableCard)
        badgeBgImageView = rootView.findViewById(R.id.badgeBgImageView)
        badgeTitleTextView = rootView.findViewById(R.id.badgeTitleTextView)
        badgeDescTextView = rootView.findViewById(R.id.badgeDescTextView)
        viewContentTextView = rootView.findViewById(R.id.viewContentTextView)
        shareJoyContainer = rootView.findViewById(R.id.shareJoyContainer)
        shareContainer = rootView.findViewById(R.id.shareContainer)
        whatsappShareImageView = rootView.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = rootView.findViewById(R.id.facebookShareImageView)
        instagramShareImageView = rootView.findViewById(R.id.instagramShareImageView)
        genericShareImageView = rootView.findViewById(R.id.genericShareImageView)
        badgesShimmerContainer = rootView.findViewById(R.id.badgesShimmerContainer)

        whatsappShareImageView.setOnClickListener(this)
        facebookShareImageView.setOnClickListener(this)
        instagramShareImageView.setOnClickListener(this)
        genericShareImageView.setOnClickListener(this)
        viewContentTextView.setOnClickListener(this)

        val bundle = arguments
        userId = bundle?.getString(Constants.USER_ID)
        badgeId = bundle?.getString("id")

        if (userId.isNullOrBlank() || badgeId.isNullOrBlank()) {
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

        badgesShimmerContainer.startShimmerAnimation()
        fetchBadgeDetail(userId!!, badgeId!!)

        return rootView
    }

    private fun fetchBadgeDetail(userId: String, badgeId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val badgeAPI = retrofit.create(BadgeAPI::class.java)
        val badgeListResponseCall = badgeAPI.getBadgeDetail(userId, badgeId)
        badgeListResponseCall.enqueue(object : Callback<BadgeListResponse> {
            override fun onResponse(
                call: Call<BadgeListResponse>,
                response: retrofit2.Response<BadgeListResponse>
            ) {
                try {
                    badgesShimmerContainer.visibility = View.GONE
                    if (response.body() == null) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                        return
                    }
                    val responseModel = response.body() as BadgeListResponse
                    if (responseModel.code == 200 && Constants.SUCCESS == responseModel.status) {
                        if (responseModel.data != null && !responseModel.data.isEmpty() && responseModel.data[0] != null) {
                            populateBadgeDetails(userId, responseModel.data[0].result)
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<BadgeListResponse>, t: Throwable) {
                badgesShimmerContainer.visibility = View.GONE
                Crashlytics.logException(t)
                activity?.let {
                    (it as BaseActivity).apiExceptions(t)
                }
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }

    private fun populateBadgeDetails(
        userId: String,
        result: ArrayList<BadgeListResponse.BadgeListData.BadgeListResult>?
    ) {
        activity?.let {
            badgeData = result?.get(0)
            Picasso.get().load(result?.get(0)?.badge_image_url).error(R.drawable.default_article)
                .fit().into(badgeImageView)
            Picasso.get().load(result?.get(0)?.badge_bg_url).error(R.drawable.default_article)
                .fit().into(badgeBgImageView)
            badgeTitleTextView.text = result?.get(0)?.badge_title
            badgeDescTextView.text = result?.get(0)?.badge_desc
            if (AppUtils.isPrivateProfile(userId)) {
                Utils.pushProfileEvents(
                    it,
                    "Show_Private_Badge_Detail",
                    "BadgesDialogFragment",
                    "-",
                    badgeData?.badge_name
                )
            } else {
                Utils.pushProfileEvents(
                    it,
                    "Show_Public_Badge_Detail",
                    "BadgesDialogFragment",
                    "-",
                    badgeData?.badge_name
                )
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
            badgesSharableCard.populateBadgesDetails(badgeData)
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
                    badgeData?.item_type == AppConstants.CONTENT_TYPE_ARTICLE -> {
                        activity?.let {
                            Utils.pushProfileEvents(
                                it, "CTA_View_Article_Public_Badge_Detail",
                                "BadgesDialogFragment", "View article", badgeData?.badge_name
                            )
                            val intent = Intent(it, ArticleDetailsContainerActivity::class.java)
                            intent.putExtra(Constants.ARTICLE_ID, badgeData?.content_id)
                            startActivity(intent)
                        }
                    }
                    badgeData?.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                        activity?.let {
                            Utils.pushProfileEvents(
                                it, "CTA_View_Story_Public_Badge_Detail",
                                "BadgesDialogFragment", "View Story", badgeData?.badge_name
                            )
                            val intent = Intent(it, ShortStoryContainerActivity::class.java)
                            intent.putExtra(Constants.ARTICLE_ID, badgeData?.content_id)
                            startActivity(intent)
                        }
                    }
                    badgeData?.item_type == AppConstants.CONTENT_TYPE_VIDEO -> {
                        activity?.let {
                            Utils.pushProfileEvents(
                                it, "CTA_View_Video_Public_Badge_Detail",
                                "BadgesDialogFragment", "View Video", badgeData?.badge_name
                            )
                            val intent = Intent(activity, ParallelFeedActivity::class.java)
                            intent.putExtra(Constants.VIDEO_ID, badgeData?.content_id)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun shareWithGeneric() {
        activity?.let {
            if (AppUtils.shareGenericLinkWithSuccessStatus(
                    activity,
                    badgeData?.badge_sharing_url
                )
            ) {
                Utils.pushProfileEvents(
                    it, "CTA_Generic_Share_Private_Badge_Detail",
                    "BadgesDialogFragment", "Generic Share", badgeData?.badge_name
                )
            }
        }
    }

    private fun shareWithInstagram() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val uri =
                Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/badge.jpg")
            if (AppUtils.shareImageWithInstagram(it, uri)) {
                Utils.pushProfileEvents(
                    it, "CTA_IG_Share_Private_Badge_Detail",
                    "BadgesDialogFragment", "IG Share", badgeData?.badge_name
                )
            }
        }
    }

    private fun shareWithFB() {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val content = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(badgeData?.badge_sharing_url))
                .build()
            activity?.let {
                ShareDialog(it).show(content)
                Utils.pushProfileEvents(
                    it, "CTA_FB_Share_Private_Badge_Detail",
                    "BadgesDialogFragment", "FB Share", badgeData?.badge_name
                )
            }
        }
    }

    private fun shareWithWhatsApp() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val uri =
                Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/badge.jpg")
            if (AppUtils.shareImageWithWhatsApp(
                    it, uri, getString(
                        R.string.badges_winner_share_text,
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name,
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).last_name,
                        badgeData?.badge_name, badgeData?.badge_sharing_url
                    )
                )
            ) {
                Utils.pushProfileEvents(
                    it, "CTA_Whatsapp_Share_Private_Badge_Detail",
                    "BadgesDialogFragment", "Whatsapp Share", badgeData?.badge_name
                )
            }
        }
    }

    private fun createSharableImageWhileCheckingPermissions(): Boolean {
        context?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return true
                } else {
                    if (createSharableCardWithBadgeName()) return true
                }
            } else {
                if (createSharableCardWithBadgeName()) return true
            }
        }
        return false
    }

    private fun createSharableCardWithBadgeName(): Boolean {
        try {
            AppUtils.getBitmapFromView(badgesSharableCard, sharableBadgeImageName)
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
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) || shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(
                    rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
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
                Snackbar.make(
                    rootLayout, R.string.permision_available_init,
                    Snackbar.LENGTH_SHORT
                )
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
                Snackbar.make(
                    rootLayout, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
