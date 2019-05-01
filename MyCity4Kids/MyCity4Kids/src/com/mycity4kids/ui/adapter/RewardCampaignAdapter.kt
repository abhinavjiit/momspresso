package com.mycity4kids.ui.adapter

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.fragment.CampaignListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_list_recycler_adapter.view.*
import java.text.SimpleDateFormat
import java.util.*

class RewardCampaignAdapter(private var campaignList: List<CampaignDataListResult>, val context: FragmentActivity?) : RecyclerView.Adapter<RewardCampaignAdapter.RewardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardCampaignAdapter.RewardHolder {
        return RewardHolder(LayoutInflater.from(context).inflate(R.layout.campaign_list_recycler_adapter, parent, false))
    }

    override fun getItemCount(): Int = campaignList.size

    override fun onBindViewHolder(holder: RewardCampaignAdapter.RewardHolder, position: Int) {
        val itemPhoto = campaignList[position]
        holder.bindPhoto(itemPhoto)
    }

    //1
    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        //2
        private var campaignList: CampaignDataListResult? = null

        //3
        init {

            view.setOnClickListener(this)
        }

        fun bindPhoto(campaignList: CampaignDataListResult) {
            this.campaignList = campaignList
            Picasso.with(view.context).load(campaignList.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(view.campaign_header)
            Picasso.with(view.context).load(campaignList.brandDetails.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(view.brand_img)
            (view.brand_name).setText(campaignList.brandDetails.name)
            (view.campaign_name).setText(campaignList.name)
            (view.amount).setText("" + campaignList.totalPayout)
            setTextAndColor(campaignList.campaignStatus)
        }

        //4
        override fun onClick(v: View) {
            //val context = itemView.context
            (context as CampaignContainerActivity).addCampaginDetailFragment(campaignList!!.id)
        }

        fun setTextAndColor(status: Int) {
            if (status == 0) {
                (view.submission_status).setText("Expired")
                (view.submission_status).setBackgroundResource(R.drawable.campaign_expired)
                (view.end_date).setText("End Date")
                (view.end_date_text).setText(getDate(campaignList!!.endTime,"dd MMM YYYY"))
            } else if (status == 1) {
                (view.submission_status).setText("Subscribe now")
                (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                (view.end_date).setText("End Date")
                (view.end_date_text).setText(getDate(campaignList!!.startTime,"dd MMM YYYY"))
            } else if (status == 2) {
                (view.submission_status).setText("Submission open")
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.end_date).setText("Start Date")
                (view.end_date_text).setText(getDate(campaignList!!.startTime,"dd MMM YYYY"))
            } else if (status == 3) {
                (view.submission_status).setText("Subscribed")
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscribed)
                (view.end_date).setText("End Date")
                (view.end_date_text).setText(getDate(campaignList!!.startTime,"dd MMM YYYY"))
            } else if (status == 4) {
                (view.submission_status).setText("Subscription full")
                (view.submission_status).setBackgroundResource(R.drawable.campaign_submission_full)
                (view.end_date).setText("End Date")
                (view.end_date_text).setText(getDate(campaignList!!.startTime,"dd MMM YYYY"))
            } else if (status == 5) {
                (view.submission_status).setText("Not eligible")
                (view.submission_status).setBackgroundResource(R.drawable.campaign_expired)
                (view.end_date).setText("End Date")
                (view.end_date_text).setText(getDate(campaignList!!.startTime,"dd MMM YYYY"))
            }
        }


        fun getDate(milliSeconds: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }


    }
}