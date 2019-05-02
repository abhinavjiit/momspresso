package com.mycity4kids.ui.campaign.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.utils.AppUtils
import android.R.id.shareText
import android.content.Intent.getIntent
import android.support.v4.app.ShareCompat
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity


class CampaignCongratulationFragment : BaseFragment() {

    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView

    private lateinit var genricShareImageView: ImageView

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

        whatsappShareImageView.setOnClickListener {


            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d", /*(activity as CampaignContainerActivity).getTotalPayOut()*/12, /*(activity as CampaignContainerActivity).getNameSlug()*/"abhinav", /*(activity as CampaignContainerActivity).getIdCamp()*/1)

            AppUtils.shareStoryWithWhatsApp(activity as CampaignContainerActivity, contentStr, "campaignCongo", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).name)


        }
        facebookShareImageView.setOnClickListener {

            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d", /*(activity as CampaignContainerActivity).getTotalPayOut()*/12, /*(activity as CampaignContainerActivity).getNameSlug()*/"abhinav", /*(activity as CampaignContainerActivity).getIdCamp()*/1)

            AppUtils.shareStoryWithFBForCampaign(this@CampaignCongratulationFragment, contentStr)

        }
        genricShareImageView.setOnClickListener {

            val contentStr = String.format("Participate in this campaign. Earn upto Rs.%d \n https://www.momspresso.com/mymoney/%s/%d", /*(activity as CampaignContainerActivity).getTotalPayOut()*/12, /*(activity as CampaignContainerActivity).getNameSlug()*/"abhinav", /*(activity as CampaignContainerActivity).getIdCamp()*/1)

            val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(contentStr)
                    .intent
            startActivity(shareIntent)

        }


        return view
    }

}