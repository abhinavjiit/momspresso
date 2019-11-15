package com.mycity4kids.ui.campaign.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.ShareCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.activity.PrivateProfileActivity
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.utils.AppUtils


class CampaignCongratulationFragment : BaseFragment() {

    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var cancel: ImageView
    private lateinit var genricShareImageView: ImageView
    private lateinit var continueBrowsingCampaignsTextView: TextView
    private lateinit var submitListener: SubmitListener
    private lateinit var pendingTextView: TextView
    private lateinit var spannable: SpannableStringBuilder
    override fun updateUi(response: Response?) {
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                CampaignCongratulationFragment().apply {
                    arguments = Bundle().apply {

                    }

                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.campaign_congratulation_layout, container, false)
        whatsappShareImageView = view.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = view.findViewById(R.id.facebookShareImageView)
        genricShareImageView = view.findViewById(R.id.genricShareImageView)
        pendingTextView = view.findViewById(R.id.pendingTextView)
        continueBrowsingCampaignsTextView = view.findViewById(R.id.continueBrowsingCampaignsTextView)
        cancel = view.findViewById(R.id.cancel)
        continueBrowsingCampaignsTextView.setOnClickListener {
            Utils.campaignEvent(activity, "Campaign Listing", "Thank you screen", "Close", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            submitListener.congratulateScreenDone()
        }
        cancel.setOnClickListener {
            Utils.campaignEvent(activity, "Campaign Listing", "Thank you screen", "Close", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            submitListener.congratulateScreenDone()
//.setText("http://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
        }

        whatsappShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign and earn Momspresso MyMoney now! \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            AppUtils.shareCampaignWithWhatsApp(activity as CampaignContainerActivity, contentStr, "campaignCongo", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).name)
        }
        facebookShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign and earn Momspresso MyMoney now! \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            AppUtils.shareFacebook(activity as CampaignContainerActivity, "", contentStr)
        }
        genricShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign and earn Momspresso MyMoney now! \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(contentStr)
                    .intent
            startActivity(shareIntent)
        }

        /*spannable = SpannableStringBuilder()
        var newColorString = " 7 working days "
        var str = pendingTextView.text.toString()
        var iStart = str.indexOf(" 7 working days ")
        var iEnd = iStart + 16
        var preString = str.subSequence(0, iStart)
        val ssText1 = SpannableString(preString)
        ssText1.setSpan(ForegroundColorSpan(resources.getColor(R.color.greytxt_color)), 0, preString.length, 0)
        spannable.append(ssText1)

        val ssText = SpannableString(newColorString)
        ssText.setSpan(ForegroundColorSpan(resources.getColor(R.color.app_red)), 0, newColorString.length, 0)
        spannable.append(ssText)


        var postString = str.subSequence(iEnd, str.length)
        val ssText2 = SpannableString(postString)
        ssText2.setSpan(ForegroundColorSpan(resources.getColor(R.color.greytxt_color)), 0, postString.length, 0)
        spannable.append(ssText2)


        pendingTextView.setText(spannable, TextView.BufferType.SPANNABLE)*/
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CampaignContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun congratulateScreenDone()
    }


}