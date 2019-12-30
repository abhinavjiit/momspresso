package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.kelltontech.utils.DateTimeUtils
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.ImageURL
import com.mycity4kids.profile.MilestonesResult
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
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
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        try {
            if (!milestonesList?.get(position)?.milestone_image_url.isNullOrBlank()) {
                Picasso.with(holder.itemView.context).load(milestonesList?.get(position)?.milestone_image_url).into(holder.milestoneImageView)
            } else {
                holder.milestoneImageView?.setImageResource(R.drawable.default_article)
            }
        } catch (e: Exception) {
            holder.milestoneImageView?.setImageResource(R.drawable.default_article)
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        when (milestonesList?.get(position)?.item_type) {
            AppConstants.CONTENT_TYPE_ARTICLE -> {
                holder.contentTypeImageView?.visibility = View.VISIBLE
                holder.contentTypeImageView?.setImageResource(R.drawable.draft_red)
                try {
                    val jsonObject = Gson().toJsonTree(milestonesList?.get(position)?.meta_data?.content_info?.imageUrl).asJsonObject
                    val imageUrl = Gson().fromJson<ImageURL>(jsonObject, ImageURL::class.java)
                    Picasso.with(holder.itemView.context).load(imageUrl?.thumbMin).into(holder.contentImageView)
                } catch (e: Exception) {
                    holder.contentImageView?.setImageResource(R.drawable.default_article)
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                holder.contentTypeImageView?.visibility = View.VISIBLE
                holder.contentTypeImageView?.setImageResource(R.drawable.shortstory_red)
                try {
                    val imageUrl: String? = milestonesList?.get(position)?.meta_data?.content_info?.imageUrl as String
                    Picasso.with(holder.itemView.context).load(imageUrl).into(holder.contentImageView)
                } catch (e: Exception) {
                    holder.contentImageView?.setImageResource(R.drawable.default_article)
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            AppConstants.CONTENT_TYPE_VIDEO -> {
                holder.contentTypeImageView?.visibility = View.VISIBLE
                holder.contentTypeImageView?.setImageResource(R.drawable.ic_video)
                try {
                    Picasso.with(holder.itemView.context).load(
                            milestonesList?.get(position)?.meta_data?.content_info?.thumbnail).into(holder.contentImageView)
                } catch (e: Exception) {
                    holder.contentImageView?.setImageResource(R.drawable.default_article)
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            AppConstants.CONTENT_TYPE_MYMONEY -> {
                holder.contentTypeImageView?.visibility = View.VISIBLE
                holder.contentTypeImageView?.setImageResource(R.drawable.ic_rupee)
                holder.milestoneImageView?.setImageDrawable(null)
                try {
                    val imageUrl: String? = milestonesList?.get(position)?.meta_data?.content_info?.imageUrl as String
                    Picasso.with(holder.itemView.context).load(imageUrl).into(holder.contentImageView)
                } catch (e: Exception) {
                    holder.contentImageView?.setImageResource(R.drawable.default_article)
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        }
        holder.contentTextView?.text = milestonesList?.get(position)?.meta_data?.content_info?.title

        if (milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_VIDEO) {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
                val formatter = SimpleDateFormat("dd MMM yyyy")
                holder.dateTextView?.text = formatter.format((parser.parse(milestonesList?.get(position)?.meta_data?.content_info?.created_at)))
                holder.dateTextView?.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.dateTextView?.visibility = View.GONE
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        } else {
            try {
                if (!milestonesList?.get(position)?.meta_data?.content_info?.created_at.isNullOrBlank()) {
                    holder.dateTextView?.text = milestonesList?.get(position)?.meta_data?.content_info?.created_at?.toLong()?.let {
                        DateTimeUtils.getDateFromTimestamp(it)
                    }
                }
                holder.dateTextView?.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.dateTextView?.visibility = View.GONE
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
    }

    inner class MilestonesViewHolder internal constructor(
            view: View,
            val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var milestoneBgImageView: ImageView? = null
        var milestoneImageView: ImageView? = null
        var contentImageView: ImageView? = null
        var contentTypeImageView: ImageView? = null
        var contentTextView: TextView? = null
        var dateTextView: TextView? = null

        init {
            milestoneBgImageView = view.findViewById(R.id.milestoneBgImageView)
            milestoneImageView = view.findViewById(R.id.milestoneImageView)
            contentImageView = view.findViewById(R.id.contentImageView)
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