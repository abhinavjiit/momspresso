package com.mycity4kids.ui.adapter

import android.support.v4.app.FragmentActivity
import android.support.v4.app.ShareCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_list_recycler_adapter.view.*
import java.text.SimpleDateFormat
import java.util.*


class RewardCampaignAdapter(private var campaignList: List<CampaignDataListResult>, val context: FragmentActivity?) : RecyclerView.Adapter<RewardCampaignAdapter.RewardHolder>() {

    private var campaignNewList: List<CampaignDataListResult>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardCampaignAdapter.RewardHolder {
        return RewardHolder(LayoutInflater.from(context).inflate(R.layout.campaign_list_recycler_adapter, parent, false))
    }

    fun updateList(campaignList: List<CampaignDataListResult>) {
        campaignNewList = campaignList
    }

    override fun getItemCount(): Int = campaignList.size

    override fun onBindViewHolder(holder: RewardCampaignAdapter.RewardHolder, position: Int) {
        val itemPhoto = campaignList[position]
        holder.bindPhoto(itemPhoto)
    }

    //1
    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var campaignList: CampaignDataListResult? = null

        init {
            view.setOnClickListener(this)
            (view.share).setOnClickListener(this)
        }

        fun bindPhoto(campaignList: CampaignDataListResult) {
            this.campaignList = campaignList
            Picasso.with(view.context).load(campaignList.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(view.campaign_header)
            Picasso.with(view.context).load(campaignList.brandDetails.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(view.brand_img)
            (view.brand_name).setText(campaignList.brandDetails.name)
            (view.campaign_name).setText(campaignList.name)
            (view.amount).setText("Rs. " + campaignList.totalPayout)
            setTextAndColor(campaignList.campaignStatus)
            compareDate()
        }

        //4
        override fun onClick(v: View) {
            if (v == (view.share)) {
                var userId = SharedPrefUtils.getUserDetailModel(context)?.dynamoId
                val shareIntent = ShareCompat.IntentBuilder
                        .from(context)
                        .setType("text/plain")
                        .setChooserTitle("Share URL")
                        .setText("http://603236c3.ngrok.io/mymoney/" + campaignList!!.nameSlug + "/" + campaignList!!.id + "?referrer=" + userId)
                        .intent

                if (shareIntent.resolveActivity(context!!.packageManager) != null) {
                    context!!.startActivity(shareIntent)
                }
            } else {
                if (campaignList!!.campaignStatus == 8)
                    Toast.makeText(context, context!!.resources.getString(R.string.toast_campaign_invite), Toast.LENGTH_SHORT).show()
                else
                    (context as CampaignContainerActivity).addCampaginDetailFragment(campaignList!!.id)

            }
        }

        fun setTextAndColor(status: Int) {
            if (status == 0) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_expired))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_expired)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_expired_background))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_expired_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_expired_bg)
            } else if (status == 1) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_apply_now))
                (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 2) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_submission_open))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 3) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_applied))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscribed)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 4) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_application_full))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_submission_full)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 5) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_apply_now))
                (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 6) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_rejected))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_rejected)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 7) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_completed))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_completed)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 8) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_invite_only))
                (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            }
        }

        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String {
            val formatter = SimpleDateFormat(dateFormat)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds * 1000
            return formatter.format(calendar.time)
        }

        fun compareDate() {
            if ((getCurrentDateTime().toString("yyyy-MM-dd")).compareTo(getDate(campaignList!!.startTime, "yyyy-MM-dd")) > 0) {
                (view.end_date).setText(context!!.resources.getString(R.string.end_date))
                (view.end_date_text).setText(getDate(campaignList!!.endTime, "dd MMM yyyy"))
            } else {
                (view.end_date).setText(context!!.resources.getString(R.string.start_date))
                (view.end_date_text).setText(getDate(campaignList!!.startTime, "dd MMM yyyy"))
            }


        }
    }
}