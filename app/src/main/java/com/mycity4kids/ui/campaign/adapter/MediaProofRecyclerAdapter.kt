package com.mycity4kids.ui.campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignProofResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_image_proof_sreenshots.view.*

class MediaProofRecyclerAdapter(
    private val mediaLists: List<CampaignProofResponse>,
    private val context: Fragment
) :
    RecyclerView.Adapter<MediaProofRecyclerAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var campaignProofResponse: List<CampaignProofResponse> = mediaLists
    private var clickListener = context as ClickListener
    private var hasVideo: Boolean = false

    init {
        mOnClickListener = View.OnClickListener { v ->
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.campaign_image_proof_sreenshots, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (!campaignProofResponse.isNullOrEmpty()) {
            if (position < campaignProofResponse.size) {
                val item = campaignProofResponse.get(position)
                if (item.isTemplate) {
                    holder.relativeParent.setOnClickListener {
                        clickListener.onCellClick(hasVideo)
                    }

                    holder.imageScreenshot.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_proof))
                    holder.imageEdit.visibility = View.GONE
                } else {
                    if (item.url!!.contains("video")) {
                        hasVideo = true
                        Picasso.get().load(R.drawable.play)
                            .placeholder(R.drawable.ic_add_proof).error(R.drawable.ic_add_proof).into(
                                holder.imageScreenshot
                            )
                    } else {
                        Picasso.get().load(item.url)
                            .placeholder(R.drawable.ic_add_proof).error(R.drawable.ic_add_proof).into(
                                holder.imageScreenshot
                            )
                    }
                    if (item.proofStatus == 1 || item.proofStatus == 0) {
                        holder.imageEdit.visibility = View.VISIBLE
                    } else if (item.proofStatus == 2) {
                        holder.imageAcceptDeleteProof.setImageDrawable(
                            context.context!!.resources.getDrawable(
                                R.drawable.ic_delete_cross
                            )
                        )
                        holder.imageEdit.visibility = View.VISIBLE
                        holder.imageAcceptedRejected.setText("Rejected")
                        holder.imageAcceptedRejected.setTextColor(context.resources.getColor(R.color.campaign_rejected))
                    } else if (item.proofStatus == 3) {
                        holder.imageAcceptDeleteProof.setImageDrawable(
                            context.context!!.resources.getDrawable(
                                R.drawable.ic_accepted
                            )
                        )
                        holder.imageEdit.visibility = View.GONE
                        holder.imageAcceptedRejected.setText("Approved")
                        holder.imageAcceptedRejected.setTextColor(context.resources.getColor(R.color.campaign_approved_rejected))
                        holder.relativeParent.isClickable = false
                    }

                    holder.imageEdit.setOnClickListener {
                        if (campaignProofResponse.get(holder.adapterPosition).url!!.contains("video")){
                            hasVideo = false
                        }
                        clickListener.onProofDelete(holder.adapterPosition)
                    }

                    holder.relativeParent.setOnClickListener {
                    }

                    with(holder.mView) {
                        tag = item
                        setOnClickListener(mOnClickListener)
                    }
                }
            } else {
                holder.relativeParent.setOnClickListener {
                    clickListener.onCellClick(hasVideo)
                }
            }
        } else {
            holder.relativeParent.setOnClickListener {
                clickListener.onCellClick(hasVideo)
            }
        }
    }

    override fun getItemCount(): Int = campaignProofResponse.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val relativeParent = mView.relativeParent
        val imageScreenshot = mView.imageScreenshot
        val imageAcceptDeleteProof = mView.imageAcceptDeleteProof
        val imageEdit = mView.imageEdit
        val imageAcceptedRejected = mView.imageAcceptedRejected
    }

    interface ClickListener {
        fun onCellClick(hasVideo: Boolean)
        fun onProofDelete(cellIndex: Int)
    }
}
