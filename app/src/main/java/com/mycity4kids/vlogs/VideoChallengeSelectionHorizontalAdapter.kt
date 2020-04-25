package com.mycity4kids.vlogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.mycity4kids.vlogs.VideoChallengeSelectionHorizontalAdapter.CustomViewHolder
import com.squareup.picasso.Picasso
import java.util.ArrayList

class VideoChallengeSelectionHorizontalAdapter(
    private val listener: RecyclerViewClickListener,
    private val challengeList: ArrayList<Topics>,
    private val source: String
) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.horizontal_recycler_view_video_challenge,
            parent,
            false
        )
        return CustomViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val challenge = challengeList[position]
        holder.challengeNameText.text = challenge.display_name

        if (challenge.extraData[0].challenge.is_live == "1") {
            holder.liveChallengeTextView.visibility = View.VISIBLE
        } else {
            holder.liveChallengeTextView.visibility = View.GONE
        }
        try {
            Picasso.get().load(challenge.extraData[0].challenge.imageUrl).placeholder(
                R.drawable.default_article
            ).error(R.drawable.default_article)
                .fit().into(holder.challengeImageView)
        } catch (e: Exception) {
            holder.challengeImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.challengeImageView.context,
                    R.drawable.default_article
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return challengeList.size
    }

    inner class CustomViewHolder internal constructor(
        itemView: View,
        val recyclerViewClickListener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var challengeNameText: TextView = itemView.findViewById(R.id.challengeNameText)
        var challengeImageView: ImageView = itemView.findViewById(R.id.tagImageView)
        private var challengeInfoImageView: ImageView = itemView.findViewById(R.id.info)
        var liveChallengeTextView: TextView = itemView.findViewById(R.id.liveTextViewVideoChallenge)

        init {
            if (source == "vlogsListing") {
                challengeInfoImageView.visibility = View.GONE
            }
            itemView.setOnClickListener(this)
            challengeInfoImageView.setOnClickListener(this)
            challengeImageView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            recyclerViewClickListener.onChallengeItemClick(view, challengeList[adapterPosition])
        }
    }

    interface RecyclerViewClickListener {
        fun onChallengeItemClick(view: View, topics: Topics)
    }
}
