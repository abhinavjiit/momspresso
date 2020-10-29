package com.mycity4kids.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.DeliverableType
import kotlinx.android.synthetic.main.campaign_type_list_recycler_adapter.view.*

class CampaignTypeListAdapter(
    private var campaignList: List<DeliverableType>,
    val context: Activity?
) : RecyclerView.Adapter<CampaignTypeListAdapter.RewardHolder>() {

    var campaignTypeList: ArrayList<String>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardHolder {
        return RewardHolder(
            LayoutInflater.from(context).inflate(
                R.layout.campaign_type_list_recycler_adapter,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = campaignList.size

    override fun onBindViewHolder(holder: RewardHolder, position: Int) {
        val itemPhoto = campaignList[position]
        holder.bindPhoto(itemPhoto)
    }

    // 1
    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var campaignList: DeliverableType? = null

        init {
            view.setOnClickListener(this)
            //            (view.share).setOnClickListener(this)
        }

        fun bindPhoto(campaignList: DeliverableType) {
            this.campaignList = campaignList
            (view.campaign_type).setText("" + campaignList.display_name)
            (view.campaign_desc).setText("" + campaignList.description)
            (view.campaignlistCheckBox).setOnCheckedChangeListener(
                object : CompoundButton.OnCheckedChangeListener {
                    override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                        if (isChecked) {
                            (view.layout).background =
                                context!!.getDrawable(R.drawable.campaign_type_selected_bg)
                            campaignTypeList!!.add(campaignList.id!!)
                            Toast.makeText(context, "checked", Toast.LENGTH_LONG)
                        } else {
                            (view.layout).background =
                                context!!.getDrawable(R.drawable.campaign_type_unselected_bg)
                            campaignTypeList!!.remove(campaignList.id!!)
                            Toast.makeText(context, "unchecked", Toast.LENGTH_LONG)
                        }
                    }
                })
        }

        override fun onClick(p0: View?) {

        }

        // 4
        /*override fun onClick(v: View) {
            if (v == (view.share)) {
                val userId =
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                Utils.campaignEvent(
                    context,
                    "Campaign Detail",
                    "Campaign Listing",
                    "share",
                    campaignList!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_Campaign_Listing"
                )

                context?.let {
                    val shareIntent = ShareCompat.IntentBuilder
                        .from(it)
                        .setType("text/plain")
                        .setChooserTitle("Share URL")
                        .setText("https://www.momspresso.com/mymoney/" + campaignList!!.nameSlug + "/" + campaignList!!.id + "?referrer=" + userId)
                        .intent

                    if (shareIntent.resolveActivity(context!!.packageManager) != null) {
                        it.startActivity(shareIntent)
                    }
                }
            } else {
                Utils.campaignEvent(
                    context,
                    "Campaign Detail",
                    "Campaign Listing",
                    "Click_listing_card",
                    campaignList!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_Campaign_Listing"
                )
                if (campaignList!!.campaignStatus == 8)
                    showInviteDialog()
                else
                    (context as CampaignContainerActivity).addCampaginDetailFragment(
                        campaignList!!.id,
                        ""
                    )
            }
        }*/
    }
}
