package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.activity.PhoneContactsActivity
import com.mycity4kids.utils.AppUtils
import kotlinx.android.synthetic.main.share_app_dialog_fragment.*

class ShareAppDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.share_app_dialog_fragment, container,
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
        whatsappShareWidget.setOnClickListener(this)
        facebookShareWidget.setOnClickListener(this)
        contactShareWidget.setOnClickListener(this)
        shareLinkWidget.setOnClickListener(this)
        cancelTextView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        try {
            when {
                view?.id == R.id.whatsappShareWidget -> {
                    activity?.let {
                        val shareText =
                            getString(R.string.share_app_msg, AppConstants.BRANCH_DEEPLINK)
                        AppUtils.shareLinkWithSuccessStatusWhatsapp(it, shareText)
                        Utils.pushGenericEvent(
                            context, "CTA_Shareapp_Whatsapp",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            "ShareAppDialogFragment"
                        )
                    }
                }
                view?.id == R.id.facebookShareWidget -> {
                    activity?.let {
                        val shareText =
                            getString(R.string.share_app_msg, AppConstants.BRANCH_DEEPLINK)
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent
                            .putExtra(
                                Intent.EXTRA_TEXT,
                                shareText
                            )
                        sendIntent.type = "text/plain"
                        sendIntent.setPackage("com.facebook.orca")
                        try {
                            startActivity(sendIntent)
                            Utils.pushGenericEvent(
                                context, "CTA_Shareapp_Facebook_Messenger",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                                "ShareAppDialogFragment"
                            )
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "Please Install Facebook Messenger",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                view?.id == R.id.contactShareWidget -> {
                    activity?.let {
                        val contactIntent = Intent(it, PhoneContactsActivity::class.java)
                        startActivity(contactIntent)
                        Utils.pushGenericEvent(
                            context, "CTA_Shareapp_Phonebook",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            "ShareAppDialogFragment"
                        )
                        dismiss()
                    }
                }
                view?.id == R.id.shareLinkWidget -> {
                    activity?.let {
                        AppUtils.shareGenericLinkWithSuccessStatus(
                            it,
                            getString(R.string.share_app_msg, AppConstants.BRANCH_DEEPLINK)
                        )
                        Utils.pushGenericEvent(
                            context, "CTA_Shareapp_Sharelink",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            "ShareAppDialogFragment"
                        )
                    }
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
}
