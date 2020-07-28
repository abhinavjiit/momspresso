package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.horizontal_recycler_view_video_challenge.view.*

class ShortStoryChallengeTopicsAdapter(private val clickListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var shortStoryChallengesList = ArrayList<Topics>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.horizontal_recycler_view_video_challenge,
            parent,
            false
        )
        return ViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return shortStoryChallengesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.info.visibility = View.GONE
            holder.challengeNameText.text =
                shortStoryChallengesList[position].display_name.toUpperCase()
            try {
                Picasso.get().load(shortStoryChallengesList[position].extraData[0].challenge.imageUrl).placeholder(
                    R.drawable.default_article
                ).error(R.drawable.default_article).fit().into(holder.challengesImageView)
            } catch (e: Exception) {
                holder.challengesImageView.setImageResource(R.drawable.default_article)
            }
            if ("1" == shortStoryChallengesList[0].is_live) {
                holder.liveTextView.visibility = View.VISIBLE
            } else {
                holder.liveTextView.visibility = View.GONE
            }
        }
    }

    fun setShortStoryChallengesData(data: ArrayList<Topics>) {
        shortStoryChallengesList = data
    }

    class ViewHolder(itemView: View, private val clickListener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View) {
            clickListener.onClick(v, adapterPosition)
        }

        val challengesImageView: ImageView = itemView.tagImageView
        val liveTextView: TextView = itemView.liveTextViewVideoChallenge
        val challengeNameText: TextView = itemView.challengeNameText
        val info: ImageView = itemView.info

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(v: View, position: Int)
    }
}
