package com.mycity4kids.ui.campaign.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_myearning.view.*

class EarningRecyclerAdapter(
        private val payoutList: List<AllCampaignTotalPayoutResponse.TotalPayoutResult>, private val context: Context)
    : RecyclerView.Adapter<EarningRecyclerAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener
    private var payoutsList: List<AllCampaignTotalPayoutResponse.TotalPayoutResult>? = null

    init {
        payoutsList = payoutList
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as AllCampaignTotalPayoutResponse.TotalPayoutResult
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_myearning, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (payoutsList!!.size > 0) {
            val item = payoutsList!!.get(position)
            Picasso.with(context).load(item.campaignDetails.brandDetails.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.brandImageView)
            holder.settleAmount.setText("\u20b9" + item.final_payout)
            holder.brandName.setText(item.campaignDetails.brandDetails.name)
            holder.campaignName.setText(item.campaignDetails.name)
            holder.totalAmount.setText("\u20b9" + item.final_payout)
            holder.netAmount.setText("\u20b9" + item.payment_meta.net_amount)
            holder.tds.setText("TDS(" + item.payment_meta.tax_percentage + "%)")
            holder.paymentStatus.setText(setStatus(item.payment_status, holder))
            holder.taxAmount.setText("-\u20b9" + item.payment_meta.tax_amount)
            holder.relativeOne.setOnClickListener {
                if (holder.relativeTwo.visibility == View.GONE) {
                    holder.relativeTwo.visibility = View.VISIBLE
                } else {
                    holder.relativeTwo.visibility = View.GONE
                }
            }

            with(holder.mView) {
                setOnClickListener(mOnClickListener)
            }
        }
    }

    private fun setStatus(payment_status: Int, holder: ViewHolder): String {
        var status: String = ""
        if (payment_status == 0 || payment_status == 2) {
            status = "In Process"
            holder.paymentStatus.setTextColor(context.resources.getColor(R.color.color_F5A623))
        } else if (payment_status == 1) {
            status = "Completed"
            holder.paymentStatus.setTextColor(context.resources.getColor(R.color.color_56CD6A))
        }
        return status;
    }

    override fun getItemCount(): Int = payoutsList!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var brandImageView: ImageView = mView.brandImageView
        var settleAmount: TextView = mView.settle_amount
        var totalAmount: TextView = mView.total_amount
        var netAmount: TextView = mView.net_amount
        var paymentStatus: TextView = mView.payment_status
        var brandName: TextView = mView.brand_name
        var campaignName: TextView = mView.campaign_name
        var tds: TextView = mView.tds
        var taxAmount: TextView = mView.tds_amount
        var relativeOne: RelativeLayout = mView.first
        var relativeTwo: RelativeLayout = mView.second

    }
}
