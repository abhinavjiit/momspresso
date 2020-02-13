package com.mycity4kids.ui.campaign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_myearning.view.*

class EarningRecyclerAdapter(
        private val payoutList: List<AllCampaignTotalPayoutResponse.TotalPayoutResult>, private val context: Context)
    : RecyclerView.Adapter<EarningRecyclerAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener
    private var payoutsList: List<AllCampaignTotalPayoutResponse.TotalPayoutResult>? = null
    private var totalEarning: Double = 0.0

    init {
        payoutsList = payoutList
        mOnClickListener = View.OnClickListener { v ->
            //            val item = v.tag as AllCampaignTotalPayoutResponse.TotalPayoutResult
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
        if (payoutsList!!.isNotEmpty()) {
            val item = payoutsList!!.get(position)
            Picasso.get().load(item.campaignDetails.brandDetails.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.brandImageView)
            holder.settleAmount.text = ("\u20b9" + item.final_payout)
            holder.brandName.text = (item.campaignDetails.brandDetails.name)
            holder.campaignName.text = (item.campaignDetails.name)

            if (item.payment_meta.size == 1) {
                if (item.payment_meta[0].source == "rewards") {
                    holder.relativeFour.visibility = View.GONE
                    holder.totalAmount.text = ("\u20b9" + item.payment_meta[0].total_amount.toString())
                    holder.netAmount.text = ("\u20b9" + item.payment_meta[0].net_amount)
                    holder.tds.text = ("TDS (" + item.payment_meta[0].tax_percentage + "%)")
                    holder.paymentStatus.text = (setStatus(item.payment_status, holder))
                    holder.taxAmount.text = ("- \u20b9" + item.payment_meta[0].tax_amount)
                    holder.earningss.visibility = View.VISIBLE
                    holder.Tds.visibility = View.VISIBLE
                } else {
                    holder.reimbursementAmount.text = item.payment_meta[0].total_amount.toString()
                    holder.earningss.visibility = View.GONE
                    holder.Tds.visibility = View.GONE
                    holder.relativeFour.visibility = View.VISIBLE
                    holder.netAmount.text = ("\u20b9" + item.payment_meta[0].net_amount)
                    holder.paymentStatus.text = (setStatus(item.payment_status, holder))

                }
            } else {
                totalEarning = 0.0
                for (i in 0 until item.payment_meta.size) {
                    if (item.payment_meta[i].source == "rewards") {
                        holder.totalAmount.text = ("\u20b9" + item.payment_meta[i].total_amount.toString())

                        totalEarning += item.payment_meta[i].net_amount
                        holder.tds.text = ("TDS (" + item.payment_meta[i].tax_percentage + "%)")
                        holder.paymentStatus.text = (setStatus(item.payment_status, holder))
                        holder.taxAmount.text = ("- \u20b9" + item.payment_meta[i].tax_amount)
                    } else {
                        holder.reimbursementAmount.text = item.payment_meta[i].total_amount.toString()
                        holder.paymentStatus.text = (setStatus(item.payment_status, holder))
                        totalEarning += item.payment_meta[i].net_amount

                    }
                }
                holder.netAmount.text = ("\u20b9" + totalEarning)

            }


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
        var status = ""
        if (payment_status == 0 || payment_status == 2) {
            status = "In Process"
            holder.paymentStatus.setTextColor(ContextCompat.getColor(context, R.color.color_F5A623))
        } else if (payment_status == 1) {
            status = "Completed"
            holder.paymentStatus.setTextColor(ContextCompat.getColor(context, R.color.color_56CD6A))
        }
        return status
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
        var relativeFour: RelativeLayout = mView.relative_four
        var reimbursementAmount: TextView = mView.reimbursement_amount
        var reimbursementText: TextView = mView.reimbursement
        var earningss: RelativeLayout = mView.relative_one
        var Tds: RelativeLayout = mView.relative_two

    }
}
