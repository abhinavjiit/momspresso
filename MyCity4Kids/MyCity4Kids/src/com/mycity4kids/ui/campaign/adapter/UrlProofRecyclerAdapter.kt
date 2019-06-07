package com.mycity4kids.ui.campaign.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignProofResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_image_proof_sreenshots.view.*
import kotlinx.android.synthetic.main.campaign_url_proof_cell.view.*

class UrlProofRecyclerAdapter(private val mediaLists: List<CampaignProofResponse>, private val context: android.support.v4.app.Fragment)
    : RecyclerView.Adapter<UrlProofRecyclerAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var campaignProofResponse: List<CampaignProofResponse> = mediaLists
    private var clickListener = context as ClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            // val item = v.tag as QuestionAnswerResponse
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.campaign_url_proof_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("adapter position", position.toString())
        if (!campaignProofResponse.isNullOrEmpty()) {
            if (position < campaignProofResponse.size) {
                val item = campaignProofResponse.get(position)

                if (campaignProofResponse.size == 1) {
                    holder.imageDeleteComponent.visibility = View.GONE
                } else {
                    holder.imageDeleteComponent.visibility = View.VISIBLE
                }

                holder.imageDeleteComponent.setOnClickListener {
                    clickListener.onUrlComponentDelete(holder.adapterPosition)
                }
                holder.textUrl.setText(item.url)
                holder.textUrl.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        if (!text.toString().isNullOrEmpty()) {

                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                })

                if (item.proofStatus == 0 && item.proofStatus == 1) {
                    holder.imageApprovedRejected.visibility = View.GONE
                } else if (item.proofStatus == 2) {
                    holder.imageApprovedRejected.setImageDrawable(context.context!!.resources.getDrawable(R.drawable.ic_delete_cross))
                    holder.imageApprovedRejected.visibility = View.VISIBLE
                    holder.textAcceptedRejectedStatus.setText("Rejected")
                    holder.textAcceptedRejectedStatus.setTextColor(context.resources.getColor(R.color.campaign_rejected))
                    holder.imageDelete.visibility = View.VISIBLE
                    holder.textUrl.isEnabled = true
                } else if (item.proofStatus == 3) {
                    holder.textAcceptedRejectedStatus.setText("Approved")
                    holder.imageApprovedRejected.setImageDrawable(context.context!!.resources.getDrawable(R.drawable.ic_accepted))
                    holder.textAcceptedRejectedStatus.setTextColor(context.resources.getColor(R.color.campaign_approved_rejected))
                    holder.imageApprovedRejected.visibility = View.VISIBLE
                    holder.textUrl.isEnabled = false
                    holder.imageDelete.visibility = View.GONE
                }

                holder.imageDelete.setOnClickListener {
                    holder.textUrl.setText("")
                    //clickListener.onUrlProofDelete(holder.adapterPosition)
                }

                with(holder.mView) {
                    tag = item
                    setOnClickListener(mOnClickListener)
                }
            } else {
                holder.imageApprovedRejected.visibility = View.GONE
                holder.textUrl.isEnabled = true
                holder.textUrl.setText("")
                holder.imageDelete.visibility = View.GONE

                holder.textUrl.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        if (!text.toString().isNullOrEmpty()) {
                            holder.imageDelete.visibility = View.VISIBLE
                        } else {
                            holder.imageDelete.visibility = View.GONE
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                })

                holder.imageDelete.setOnClickListener {
                    holder.textUrl.setText("")
                }
            }
        } else {
            holder.imageApprovedRejected.visibility = View.GONE
            holder.textUrl.isEnabled = true
            holder.textUrl.setText("")
            holder.imageDelete.visibility = View.GONE

            holder.textUrl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    if (!text.toString().isNullOrEmpty()) {
                        holder.imageDelete.visibility = View.VISIBLE
                    } else {
                        holder.imageDelete.visibility = View.GONE
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

            holder.imageDelete.setOnClickListener {
                holder.textUrl.setText("")
            }
        }
    }

    override fun getItemCount(): Int = campaignProofResponse.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val textUrl = mView.textUrl
        val imageDelete = mView.imageDelete
        val imageApprovedRejected = mView.imageApprovedRejected
        val textAcceptedRejectedStatus = mView.textAcceptedRejectedStatus
        val imageDeleteComponent = mView.imageDeleteComponent
    }

    interface ClickListener {
        fun onCellClick()
        fun onUrlProofDelete(cellIndex: Int)
        fun onUrlComponentDelete(cellIndex: Int)
    }
}