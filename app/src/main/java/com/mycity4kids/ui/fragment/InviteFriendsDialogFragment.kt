package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.ui.activity.PhoneContactsActivity
import com.mycity4kids.ui.activity.UserInviteFBSuggestionActivity
import com.mycity4kids.utils.AppUtils
import kotlinx.android.synthetic.main.invite_friends_dialog_fragment.*

class InviteFriendsDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var eventSuffix: String
    private lateinit var screenName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.invite_friends_dialog_fragment, container,
            false
        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facebookShareWidget.setOnClickListener(this)
        contactShareWidget.setOnClickListener(this)
        shareLinkWidget.setOnClickListener(this)
        whatsappShareLinkWidget.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)

        activity?.let {
            if (arguments != null && arguments?.getString("source") != null) {
                when {
                    arguments?.getString("source") == AppConstants.CONTENT_TYPE_ARTICLE -> {
                        Utils.shareEventTracking(
                            it, "Post creation",
                            "Invite_Android",
                            "Show_InvitePopup_PostBlogCreation"
                        )
                        eventSuffix = "_PBC"
                        screenName = "Post creation"
                    }
                    arguments?.getString("source") == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                        Utils.shareEventTracking(
                            it, "Post creation",
                            "Invite_Android",
                            "Show_InvitePopup_Post100WSCreation"
                        )
                        eventSuffix = "_PSC"
                        screenName = "Post creation"
                    }
                    arguments?.getString("source") == AppConstants.CONTENT_TYPE_VIDEO -> {
                        Utils.shareEventTracking(
                            it, "Post creation",
                            "Invite_Android",
                            "Show_InvitePopup_PostVlogCreation"
                        )
                        eventSuffix = "_PVC"
                        screenName = "Post creation"
                    }
                    arguments?.getString("source") == "profile" -> {
                        Utils.shareEventTracking(
                            it, "Self Profile",
                            "Invite_Android",
                            "Show_InvitePopup_FromProfile"
                        )
                        eventSuffix = "_FSP"
                        screenName = "Self Profile"
                    }
                    arguments?.getString("source") == "notification" -> {
                        Utils.shareEventTracking(
                            it, "Self Profile",
                            "Invite_Android",
                            "Show_InvitePopup_ViaNotif"
                        )
                        eventSuffix = "_VN"
                        screenName = "Self Profile"
                    }
                    arguments?.getString("source") == "deeplink" -> {
                        Utils.shareEventTracking(
                            it, "Self Profile",
                            "Invite_Android",
                            "Show_InvitePopup_ViaDeeplink"
                        )
                        eventSuffix = "_VD"
                        screenName = "Self Profile"
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        try {
            when {
                view?.id == R.id.facebookShareWidget -> {
                    activity?.let {
                        val intent = Intent(it, UserInviteFBSuggestionActivity::class.java)
                        intent.putExtra("eventScreen", screenName)
                        intent.putExtra("eventSuffix", eventSuffix)
                        startActivity(intent)
                        Utils.shareEventTracking(
                            it, screenName,
                            "Invite_Android",
                            "CTA_Invite_Facebook_Friends$eventSuffix"
                        )
                        dismiss()
                    }
                }
                view?.id == R.id.contactShareWidget -> {
                    activity?.let {
                        val contactIntent = Intent(it, PhoneContactsActivity::class.java)
                        contactIntent.putExtra("eventScreen", screenName)
                        contactIntent.putExtra("eventSuffix", eventSuffix)
                        startActivity(contactIntent)
                        Utils.shareEventTracking(
                            it, screenName,
                            "Invite_Android",
                            "CTA_Invite_Phone_Contacts$eventSuffix"
                        )
                        dismiss()
                    }
                }
                view?.id == R.id.shareLinkWidget -> {
                    if (activity is UserProfileActivity) {
                        (activity as UserProfileActivity).shareGenericProfile()
                        dismiss()
                    } else {
                        shareGenericProfileUrl()
                        dismiss()
                    }
                    Utils.shareEventTracking(
                        context, screenName,
                        "Invite_Android",
                        "CTA_Share_Link$eventSuffix"
                    )
                }
                view?.id == R.id.whatsappShareLinkWidget -> {
                    if (activity is UserProfileActivity) {
                        (activity as UserProfileActivity).shareProfile()
                        dismiss()
                    } else {
                        shareProfileUrl()
                        dismiss()
                    }
                    Utils.shareEventTracking(
                        context, screenName,
                        "Invite_Android",
                        "CTA_Invite_Whatsapp$eventSuffix"
                    )
                }
                view?.id == R.id.cancelTextView -> {
                    dismiss()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun shareProfileUrl() {
        activity?.let {
            val shareText = getString(
                R.string.profile_follow_author,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name + " " + SharedPrefUtils.getUserDetailModel(
                    BaseApplication.getAppContext()
                ).last_name,
                AppConstants.USER_PROFILE_SHARE_BASE_URL + SharedPrefUtils.getUserDetailModel(
                    BaseApplication.getAppContext()
                ).dynamoId
            )
            AppUtils.shareLinkWithSuccessStatusWhatsapp(it, shareText)
        }
    }

    private fun shareGenericProfileUrl() {
        activity?.let {
            val shareText = getString(
                R.string.profile_follow_author,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name + " " + SharedPrefUtils.getUserDetailModel(
                    BaseApplication.getAppContext()
                ).last_name,
                AppConstants.USER_PROFILE_SHARE_BASE_URL + SharedPrefUtils.getUserDetailModel(
                    BaseApplication.getAppContext()
                ).dynamoId
            )
            AppUtils.shareGenericLinkWithSuccessStatus(it, shareText)
        }
    }
}
