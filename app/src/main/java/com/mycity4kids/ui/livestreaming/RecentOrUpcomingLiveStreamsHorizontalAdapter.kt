package com.mycity4kids.ui.livestreaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso

class RecentOrUpcomingLiveStreamsHorizontalAdapter(
    private val upcomingLiveStreamList: List<LiveStreamResult>?,
    private val listener: HorizontalRecyclerViewClickListener
) :
    RecyclerView.Adapter<RecentOrUpcomingLiveStreamsHorizontalAdapter.LiveStreamViewHolder>() {

    private var cardSize: String = "small"

    override fun getItemViewType(position: Int): Int {
        return upcomingLiveStreamList?.get(position)?.liveStatus!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveStreamViewHolder {
        val v0 = LayoutInflater.from(parent.context)
            .inflate(R.layout.live_stream_horizontal_list_item, parent, false)
        return LiveStreamViewHolder(v0)
    }

    fun setCardSize(cardSize: String) {
        this.cardSize = cardSize
    }

    override fun getItemCount(): Int {
        return upcomingLiveStreamList?.size ?: 0
    }

    override fun onBindViewHolder(holder: LiveStreamViewHolder, position: Int) {
        try {
            Picasso.get().load(upcomingLiveStreamList?.get(position)?.cover_image).placeholder(R.drawable.default_article).error(
                R.drawable.default_article
            ).into(holder.liveStreamImageView)
        } catch (e: Exception) {
            holder.liveStreamImageView.setImageResource(R.drawable.default_article)
        }
        if (BuildConfig.DEBUG) {
            holder.eventId.visibility = View.VISIBLE
            holder.eventId.text = "" + upcomingLiveStreamList?.get(position)?.id
        } else {
            holder.eventId.visibility = View.GONE
        }

        when (upcomingLiveStreamList?.get(position)?.status) {
            AppConstants.LIVE_STREAM_STATUS_UPCOMING -> {
                holder.upcomingLiveTimeWidget.visibility = View.VISIBLE
                holder.remainingTimeTextView.text =
                    upcomingLiveStreamList[position].live_datetime?.let {
                        DateTimeUtils.timeDiffInMinuteAndSeconds(
                            it
                        )
                    }
                holder.liveOngoingLabel.visibility = View.GONE
                holder.liveEndedLabel.visibility = View.GONE
            }
            AppConstants.LIVE_STREAM_STATUS_ONGOING -> {
                holder.upcomingLiveTimeWidget.visibility = View.GONE
                holder.liveOngoingLabel.visibility = View.VISIBLE
                holder.liveEndedLabel.visibility = View.GONE
            }
            else -> {
                holder.upcomingLiveTimeWidget.visibility = View.GONE
                holder.liveOngoingLabel.visibility = View.GONE
                holder.liveEndedLabel.visibility = View.VISIBLE
                holder.liveEndedLabel.setText(
                    "Live ended " + DateTimeUtils.timeSince(
                        upcomingLiveStreamList?.get(position)?.updated_at!!
                    ) + " ago"
                )
            }
        }

        holder.liveDateLabel.text =
            upcomingLiveStreamList[position].live_datetime?.let {
                DateTimeUtils.getDateTimeFromTimestamp(
                    it
                )
            }
        holder.remainingTimeTextView.text =
            upcomingLiveStreamList[0].live_datetime?.let {
                DateTimeUtils.timeDiffInMinuteAndSeconds(
                    it
                )
            }
    }

    inner class LiveStreamViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var liveStreamImageView: ImageView =
            itemView.findViewById(R.id.liveStreamImageView)
        internal var remainingTimeTextView: TextView =
            itemView.findViewById(R.id.remainingTimeTextView)
        internal var eventId: TextView =
            itemView.findViewById(R.id.eventId)
        internal var liveDateLabel: TextView = itemView.findViewById(R.id.liveDateLabel)
        internal var liveOngoingLabel: CardView =
            itemView.findViewById(R.id.liveOngoingLabel)
        internal var liveEndedLabel: MomspressoButtonWidget =
            itemView.findViewById(R.id.liveEndedLabel)
        internal var upcomingLiveTimeWidget: CardView =
            itemView.findViewById(R.id.upcomingLiveTimeWidget)
        internal var liveIndicatorImageView: ImageView =
            itemView.findViewById(R.id.liveIndicatorImageView)

        init {
            val displayMetrics = itemView.context.resources.displayMetrics
            val widthIndp = displayMetrics.widthPixels / displayMetrics.density
            val wid = (widthIndp - 32.0f) * 0.85f
            if (cardSize == "large") {
                liveDateLabel.visibility = View.GONE
                liveStreamImageView.layoutParams.width = AppUtils.dpTopx(wid)
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onLiveStreamItemClick(v, upcomingLiveStreamList?.get(adapterPosition))
            }
        }
    }

    interface HorizontalRecyclerViewClickListener {
        fun onLiveStreamItemClick(view: View, liveStreamResult: LiveStreamResult?)
    }
}
