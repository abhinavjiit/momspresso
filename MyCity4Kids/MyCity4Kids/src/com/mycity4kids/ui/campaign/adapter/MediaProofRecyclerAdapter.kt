package com.mycity4kids.ui.campaign.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignProofResponse
import com.mycity4kids.models.campaignmodels.QuestionAnswerResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_image_proof_sreenshots.view.*

class MediaProofRecyclerAdapter(
        private val mediaLists: List<CampaignProofResponse>, private val context: android.support.v4.app.Fragment)
    : RecyclerView.Adapter<MediaProofRecyclerAdapter.ViewHolder>() {

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
                .inflate(R.layout.campaign_image_proof_sreenshots, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(!campaignProofResponse.isNullOrEmpty()){
            if(position < campaignProofResponse.size){
                val item = campaignProofResponse.get(position)
                Picasso.with(context.context).load(item.thumbnail)
                        .placeholder(R.drawable.ic_add_proof).error(R.drawable.ic_add_proof).into(holder.imageScreenshot)

                if (item.proofStatus == 1) {

                } else if (item.proofStatus == 2) {
                    holder.imageAcceptDeleteProof.setImageDrawable(context.context!!.resources.getDrawable(R.drawable.ic_delete_cross))
                } else if (item.proofStatus == 3) {
                    holder.imageAcceptDeleteProof.setImageDrawable(context.context!!.resources.getDrawable(R.drawable.ic_accepted))
                }

                holder.relativeParent.setOnClickListener {
                    clickListener.onCellClick()
                }

                holder.imageAcceptDeleteProof.setOnClickListener {
                    //if(item.proofStatus!=3){
                    clickListener.onProofDelete(holder.adapterPosition)
                    //}
                }

                with(holder.mView) {
                    tag = item
                    setOnClickListener(mOnClickListener)
                }
            }else{
//                Picasso.with(context.context).load(R.drawable.ic_add_proof)
//                        .placeholder(R.drawable.ic_add_proof).error(R.drawable.ic_add_proof).into(holder.imageScreenshot)
            }
        }else{
//            Picasso.with(context.context).load(R.drawable.ic_add_proof)
//                    .placeholder(R.drawable.ic_add_proof).error(R.drawable.ic_add_proof).into(holder.imageScreenshot)
        }

    }

    override fun getItemCount(): Int = 6

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val relativeParent = mView.relativeParent
        val imageScreenshot = mView.imageScreenshot
        val imageAcceptDeleteProof = mView.imageAcceptDeleteProof
    }

    interface ClickListener {
        fun onCellClick()
        fun onProofDelete(cellIndex: Int)
    }
}