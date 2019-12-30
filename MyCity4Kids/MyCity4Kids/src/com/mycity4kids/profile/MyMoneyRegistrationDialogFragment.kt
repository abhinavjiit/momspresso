package com.mycity4kids.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity

class MyMoneyRegistrationDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var getStartedTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.my_money_registration_dialog_fragment, container,
                false)

        getStartedTextView = rootView.findViewById(R.id.getStartedTextView)
        getStartedTextView.setOnClickListener(this)

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

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.getStartedTextView -> {
                activity?.let {
                    Utils.campaignEvent(it, "MyMoneyRegistrationDialogFragment", "MyMoneyRegistrationDialogFragment",
                            "Get_Started_Button", "", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            System.currentTimeMillis().toString(), "Show_Campaign_Listing")
                    val cityIntent = Intent(it, CampaignContainerActivity::class.java)
                    startActivity(cityIntent)
                }
            }
        }
    }
}
