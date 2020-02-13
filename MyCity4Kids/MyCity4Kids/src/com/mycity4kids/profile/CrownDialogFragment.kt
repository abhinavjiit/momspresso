package com.mycity4kids.profile

import android.Manifest
import android.app.Dialog
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.crashlytics.android.Crashlytics
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.material.snackbar.Snackbar
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.PermissionUtil
import com.squareup.picasso.Picasso
import java.util.*

class CrownDialogFragment : DialogFragment(), View.OnClickListener {

    val REQUEST_GALLERY_PERMISSION = 1
    val PERMISSIONS_INIT = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val sharableCrownImageName = "crown"

    private lateinit var rootLayout: RelativeLayout
    private lateinit var crownImageView: ImageView
    private lateinit var crownBgImageView: ImageView
    private lateinit var crownTitleTextView: TextView
    private lateinit var crownDescTextView: TextView
    private lateinit var shareJoyContainer: RelativeLayout
    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var instagramShareImageView: ImageView
    private lateinit var genericShareImageView: ImageView
    private lateinit var crownSharableCard: CrownShareCardWidget
    private lateinit var shareContainer: ConstraintLayout

    var userId: String? = null
    var crownData: Crown? = null
    var shareMedium: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.crown_dialog_fragment, container,
                false)

        rootLayout = rootView.findViewById(R.id.rootLayout)
        crownImageView = rootView.findViewById(R.id.crownImageView)
        crownBgImageView = rootView.findViewById(R.id.crownBgImageView)
        crownTitleTextView = rootView.findViewById(R.id.crownTitleTextView)
        crownDescTextView = rootView.findViewById(R.id.crownDescTextView)
        shareContainer = rootView.findViewById(R.id.shareContainer)
        shareJoyContainer = rootView.findViewById(R.id.shareJoyContainer)
        whatsappShareImageView = rootView.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = rootView.findViewById(R.id.facebookShareImageView)
        instagramShareImageView = rootView.findViewById(R.id.instagramShareImageView)
        crownSharableCard = rootView.findViewById(R.id.crownSharableCard)
        genericShareImageView = rootView.findViewById(R.id.genericShareImageView)

        whatsappShareImageView.setOnClickListener(this)
        facebookShareImageView.setOnClickListener(this)
        instagramShareImageView.setOnClickListener(this)
        genericShareImageView.setOnClickListener(this)

        val bundle = arguments
        userId = bundle?.getString(Constants.USER_ID)
        crownData = bundle?.getParcelable<Crown>("crown")

        if (userId.isNullOrBlank() || crownData == null) {
            activity?.let {
                ToastUtils.showToast(it, it.getString(R.string.empty_screen), Toast.LENGTH_SHORT)
            }
            dismiss()
        }

        if (AppUtils.isPrivateProfile(userId)) {
            shareContainer.visibility = View.VISIBLE
            shareJoyContainer.visibility = View.VISIBLE
            activity?.let {
                Utils.pushGenericEvent(it, "Show_Private_Rank_Detail", userId, "CrownDialogFragment")
            }
        } else {
            if (BuildConfig.DEBUG) {
                shareContainer.visibility = View.GONE
                shareJoyContainer.visibility = View.GONE
            } else {
                shareContainer.visibility = View.GONE
                shareJoyContainer.visibility = View.GONE
            }
            activity?.let {
                Utils.pushGenericEvent(it, "Show_Public_Rank_Detail", userId, "CrownDialogFragment")
            }
        }

        populateCrownDetails(userId!!, crownData)

        return rootView
    }

    private fun populateCrownDetails(userId: String, result: Crown?) {
        activity?.let {
            Picasso.get().load(result?.bg_url).error(R.drawable.default_article)
                    .fit().into(crownBgImageView)
            Picasso.get().load(result?.image_url).error(R.drawable.default_article)
                    .fit().into(crownImageView)
            crownTitleTextView.text = result?.title
            crownDescTextView.text = result?.desc
            if (AppUtils.isPrivateProfile(userId)) {
                shareContainer.visibility = View.VISIBLE
            } else {
                shareContainer.visibility = View.GONE
            }
            crownSharableCard.populateCrownDetails(result)
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
        }
    }

    private fun shareWithGeneric() {
        activity?.let {
            if (AppUtils.shareGenericLinkWithSuccessStatus(activity, crownData?.sharing_url)) {
                Utils.pushProfileEvents(it, "CTA_Generic_Share_Private_Rank_Detail",
                        "CrownDialogFragment", "Generic Share", "-")
            }
        }
    }

    private fun shareWithInstagram() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                    "/MyCity4Kids/videos/" + sharableCrownImageName + ".jpg")
            if (AppUtils.shareImageWithInstagram(it, uri)) {
                Utils.pushProfileEvents(it, "CTA_IG_Share_Private_Rank_Detail",
                        "CrownDialogFragment", "IG Share", "-")
            }
        }
    }

    private fun shareWithFB() {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val content = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(crownData?.sharing_url))
                    .build()
            activity?.let {
                ShareDialog(it).show(content)
                Utils.pushProfileEvents(it, "CTA_FB_Share_Private_Rank_Detail",
                        "CrownDialogFragment", "FB Share", "-")
            }
        }
    }

    private fun shareWithWhatsApp() {
        if (createSharableImageWhileCheckingPermissions()) {
            return
        }
        activity?.let {
            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                    "/MyCity4Kids/videos/" + sharableCrownImageName + ".jpg")
            if (AppUtils.shareImageWithWhatsApp(it, uri, getString(R.string.badges_winner_share_text,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).last_name,
                            crownData?.crown?.name, crownData?.sharing_url))) {
                Utils.pushProfileEvents(it, "CTA_Whatsapp_Share_Private_Rank_Detail",
                        "CrownDialogFragment", "Whatsapp Share", "-")
            }
        }
    }

    private fun createSharableImageWhileCheckingPermissions(): Boolean {
        context?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(it,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(it,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                    return true
                } else {
                    try {
                        AppUtils.getBitmapFromView(crownSharableCard, sharableCrownImageName)
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                        return true
                    }
                }
            } else {
                try {
                    AppUtils.getBitmapFromView(crownSharableCard, sharableCrownImageName)
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    return true
                }
            }
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
                if (ContextCompat.checkSelfPermission(it, s) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(s)
                }
            }
            val requiredPermission = permissionList.toTypedArray()
            requestPermissions(requiredPermission, REQUEST_GALLERY_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
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
}