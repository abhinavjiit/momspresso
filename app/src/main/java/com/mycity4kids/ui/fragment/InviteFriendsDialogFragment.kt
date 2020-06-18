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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.invite_friends_dialog_fragment, container,
            false
        )
        return rootView
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
    }

    override fun onClick(view: View?) {
        try {
            when {
                view?.id == R.id.facebookShareWidget -> {
                    Utils.pushGenericEvent(
                        context, "CTA_Invite_Facebook_Friends",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                        "InviteFriendsDialogFragment"
                    )
                    activity?.let {
                        val intent = Intent(it, UserInviteFBSuggestionActivity::class.java)
                        startActivity(intent)
                        dismiss()
                    }
                }
                view?.id == R.id.contactShareWidget -> {
                    activity?.let {
                        val contactIntent = Intent(it, PhoneContactsActivity::class.java)
                        startActivity(contactIntent)
                        Utils.pushGenericEvent(
                            it, "CTA_Invite_Phone_Contacts",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            "InviteFriendsDialogFragment"
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
                    Utils.pushGenericEvent(
                        context, "CTA_Share_Link",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                        "InviteFriendsDialogFragment"
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
                    Utils.pushGenericEvent(
                        context, "CTA_Invite_Whatsapp",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                        "InviteFriendsDialogFragment"
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
