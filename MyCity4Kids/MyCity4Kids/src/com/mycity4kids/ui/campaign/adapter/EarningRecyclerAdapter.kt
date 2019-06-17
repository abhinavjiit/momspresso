package com.mycity4kids.ui.campaign.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.mycity4kids.models.campaignmodels.QuestionAnswerResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item.view.*
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
        if (payoutsList!!.size>0) {
            val item = payoutsList!!.get(position)
            Picasso.with(context).load(item.campaignDetails.brandDetails.imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.brandImageView)
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

    override fun getItemCount(): Int = payoutsList!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var brandImageView: ImageView = mView.brandImageView
        var relativeOne: RelativeLayout = mView.first
        var relativeTwo: RelativeLayout = mView.second

    }
}
