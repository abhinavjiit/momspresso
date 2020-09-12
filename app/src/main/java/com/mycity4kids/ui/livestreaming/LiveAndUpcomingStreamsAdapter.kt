package com.mycity4kids.ui.livestreaming

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.ui.momspressotv.MomspressoTelevisionLiveAndUpcomingTabFragment
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso

class LiveAndUpcomingStreamsAdapter(
    private val recyclerViewClickListener: RecyclerViewClickListener,
    private val listener: RecentOrUpcomingLiveStreamsHorizontalAdapter.HorizontalRecyclerViewClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var liveStreamList: ArrayList<MomspressoTelevisionLiveAndUpcomingTabFragment.LiveAndUpcomingData>? =
        null

    fun setListData(liveStreamList: ArrayList<MomspressoTelevisionLiveAndUpcomingTabFragment.LiveAndUpcomingData>?) {
        this.liveStreamList = liveStreamList
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            AppConstants.CONTENT_TYPE_RECENT_LIVE_STREAM == liveStreamList?.get(position)?.contentType -> CONTENT_TYPE_RECENT_LIVE_STREAM
            AppConstants.CONTENT_TYPE_UPCOMING_LIVE_STREAM == liveStreamList?.get(position)?.contentType -> CONTENT_TYPE_UPCOMING_LIVE_STREAM
            else -> CONTENT_TYPE_SECTION_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CONTENT_TYPE_SECTION_HEADER -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.live_stream_section_header, parent, false)
                SectionViewHolder(v0)
            }
            CONTENT_TYPE_RECENT_LIVE_STREAM -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recent_live_streams_horizontal_item, parent, false)
                RecentLiveStreamViewHolder(v0)
            }
            else -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.live_stream_list_item, parent, false)
                UpcomingLiveStreamViewHolder(v0, recyclerViewClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (liveStreamList == null) 0 else liveStreamList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SectionViewHolder -> {
            }
            is UpcomingLiveStreamViewHolder -> {
                try {
                    Picasso.get().load(liveStreamList?.get(position)?.liveStreamResult?.image_url).placeholder(
                        R.drawable.default_article
                    ).error(
                        R.drawable.default_article
                    ).into(holder.liveStreamImageView)
                } catch (e: Exception) {
                    holder.liveStreamImageView.setImageResource(R.drawable.default_article)
                }
                if (BuildConfig.DEBUG) {
                    holder.eventId.visibility = View.VISIBLE
                    holder.eventId.text = "" + liveStreamList?.get(position)?.liveStreamResult?.id
                } else {
                    holder.eventId.visibility = View.GONE
                }
                holder.remainingTimeTextView.text =
                    DateTimeUtils.timeDiffInMinuteAndSeconds(liveStreamList?.get(position)?.liveStreamResult?.live_datetime!!)
            }
            is RecentLiveStreamViewHolder -> {
                try {
                    val recyclerViewAdapter =
                        RecentOrUpcomingLiveStreamsHorizontalAdapter(
                            liveStreamList?.get(position)?.recentLivesList,
                            listener
                        )
                    recyclerViewAdapter.setCardSize("large")
                    holder.recentLivesRecyclerView.adapter = recyclerViewAdapter
                    holder.recentLivesRecyclerView.layoutManager = LinearLayoutManager(
                        holder.recentLivesRecyclerView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    holder.recentLivesRecyclerView.setHasFixedSize(true)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        }
    }

    inner class SectionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        internal var sectionHeaderTextView: TextView = itemView as TextView

        init {
            sectionHeaderTextView.text = "Upcoming Live Sessions"
        }
    }

    inner class RecentLiveStreamViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        internal var recentLivesRecyclerView: RecyclerView =
            itemView as RecyclerView
    }

    inner class UpcomingLiveStreamViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var liveStartsLabel: TextView = itemView.findViewById(R.id.liveStartsLabel)
        internal var liveOngoingLabel: CardView =
            itemView.findViewById(R.id.liveOngoingLabel)
        internal var liveEndedLabel: MomspressoButtonWidget =
            itemView.findViewById(R.id.liveEndedLabel)
        internal var upcomingLiveTimeWidget: CardView =
            itemView.findViewById(R.id.upcomingLiveTimeWidget)
        internal var remainingTimeTextView: TextView =
            itemView.findViewById(R.id.remainingTimeTextView)
        internal var liveStreamImageView: ImageView =
            itemView.findViewById(R.id.liveStreamImageView)
        internal var eventId: TextView =
            itemView.findViewById(R.id.eventId)

        init {
            liveOngoingLabel.visibility = View.GONE
            liveEndedLabel.visibility = View.GONE
            liveStartsLabel.visibility = View.GONE
            upcomingLiveTimeWidget.visibility = View.VISIBLE
            itemView.setOnClickListener(this)
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

    companion object {
        private val CONTENT_TYPE_SECTION_HEADER = 0
        private val CONTENT_TYPE_RECENT_LIVE_STREAM = 1
        private val CONTENT_TYPE_UPCOMING_LIVE_STREAM = 2
    }
}
