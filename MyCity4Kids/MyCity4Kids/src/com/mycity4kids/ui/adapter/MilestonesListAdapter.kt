package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.profile.MilestonesResult
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import java.util.*

class MilestonesListAdapter(
        private val mListener: RecyclerViewClickListener
) : RecyclerView.Adapter<MilestonesListAdapter.MilestonesViewHolder>() {

    private var milestonesList: ArrayList<MilestonesResult>? = null

    fun setListData(milestonesList: ArrayList<MilestonesResult>?) {
        this.milestonesList = milestonesList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestonesViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.milestones_item, parent, false)
        return MilestonesViewHolder(v0, mListener)
    }

    override fun getItemCount(): Int {
        return if (milestonesList == null) 0 else milestonesList!!.size
    }

    override fun onBindViewHolder(holder: MilestonesViewHolder, position: Int) {
        try {
            if (!milestonesList?.get(position)?.milestone_bg_url.isNullOrBlank()) {
                Picasso.with(holder.itemView.context).load(milestonesList?.get(position)?.milestone_bg_url).into(holder.milestoneBgImageView)
            } else {
                holder.milestoneBgImageView?.setBackgroundResource(R.drawable.default_article)
            }
        } catch (e: Exception) {
            holder.milestoneBgImageView?.setBackgroundResource(R.drawable.default_article)
        }

        try {
            if (!milestonesList?.get(position)?.milestone_image_url.isNullOrBlank()) {
                Picasso.with(holder.itemView.context).load(milestonesList?.get(position)?.milestone_image_url).into(holder.milestoneImageView)
            } else {
                holder.milestoneImageView?.setBackgroundResource(R.drawable.default_article)
            }
        } catch (e: Exception) {
            holder.milestoneImageView?.setBackgroundResource(R.drawable.default_article)
        }

        if (milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_ARTICLE) {
            holder.contentTypeImageView?.setImageResource(R.drawable.draft_red)
        } else if (milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY) {
            holder.contentTypeImageView?.setImageResource(R.drawable.shortstory_red)
        } else if (milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_VIDEO) {
            holder.contentTypeImageView?.setImageResource(R.drawable.ic_video)
        } else {
            // Need Icon for my money type
            //holder.contentTypeImageView?.setImageResource(R.drawable.ic_video)
        }
        holder.contentTextView?.text = milestonesList?.get(position)?.milestone_title
    }

    inner class MilestonesViewHolder internal constructor(
            view: View,
            val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var milestoneBgImageView: ImageView? = null
        var milestoneImageView: ImageView? = null
        var contentTypeImageView: ImageView? = null
        var contentTextView: TextView? = null
        var dateTextView: TextView? = null

        init {
            milestoneBgImageView = view.findViewById(R.id.milestoneBgImageView)
            milestoneImageView = view.findViewById(R.id.milestoneImageView)
            contentTypeImageView = view.findViewById(R.id.contentTypeImageView)
            contentTextView = view.findViewById(R.id.contentTextView)
            dateTextView = view.findViewById(R.id.dateTextView)

            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}