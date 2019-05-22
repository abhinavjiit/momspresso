package com.mycity4kids.ui.campaign.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.ShareCompat
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
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.utils.AppUtils


class CampaignCongratulationFragment : BaseFragment() {

    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var cancel: ImageView
    private lateinit var genricShareImageView: ImageView
    private lateinit var continueBrowsingCampaignsTextView: TextView
    private lateinit var submitListener: SubmitListener

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
        continueBrowsingCampaignsTextView = view.findViewById(R.id.continueBrowsingCampaignsTextView)
        cancel = view.findViewById(R.id.cancel)
        continueBrowsingCampaignsTextView.setOnClickListener {
            Utils.campaignEvent(activity, "Campaign Listing", "Thank you screen", "Close", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            submitListener.congratulateScreenDone()
        }
        cancel.setOnClickListener {
            Utils.campaignEvent(activity, "Campaign Listing", "Thank you screen", "Close", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Listing")
            submitListener.congratulateScreenDone()
//.setText("http://www.momspresso.com/mymoney/" + apiGetResponse!!.nameSlug + "/" + id + "?referrer=" + userId)
        }

        whatsappShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getTotalPayOut(), (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            AppUtils.shareCampaignWithWhatsApp(activity as CampaignContainerActivity, contentStr, "campaignCongo", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).name)
        }
        facebookShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getTotalPayOut(), (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            AppUtils.shareFacebook(activity as CampaignContainerActivity, "", contentStr)
        }
        genricShareImageView.setOnClickListener {
            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d?referrer=" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    (activity as CampaignContainerActivity).getTotalPayOut(), (activity as CampaignContainerActivity).getNameSlug(), (activity as CampaignContainerActivity).getIdCamp())
            val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(contentStr)
                    .intent
            startActivity(shareIntent)
        }


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