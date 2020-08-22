package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.blog_challenge_adapter.view.*
import java.lang.Exception

class BlogChallengeAdapter(
    private val priviousWeekChallenges: ArrayList<Topics>,
    private val liveChallenges: ArrayList<Topics>,
    private val listener: ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener
    , private val priviousWeekChallengesListner: BlogsPriviousWeekChallengesClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 2) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.blog_challenge_adapter,
                parent,
                false
            )
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.video_challenge_selection_vertical_item,
                parent,
                false
            )
            return HorizontalViewHolder(view)
        }
    }

    override fun getItemCount(): Int {

        return if (priviousWeekChallenges.isNullOrEmpty() && liveChallenges.isNullOrEmpty()) {
            0
        } else {
            if (liveChallenges.isNullOrEmpty() && priviousWeekChallenges.isNotEmpty()) {
                priviousWeekChallenges.size
            } else if (liveChallenges.isNotEmpty() && priviousWeekChallenges.isNullOrEmpty()) {
                1
            } else {
                priviousWeekChallenges.size + 1
            }

        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && liveChallenges.isNotEmpty()) {
            1
        } else 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is HorizontalViewHolder) {
                holder.challengeRecyclerView.adapter = ContentChallengeSelectionHorizontalAdapter(
                    listener,
                    liveChallenges,
                    ""
                )
                holder.challengeRecyclerView.layoutManager = LinearLayoutManager(
                    holder.challengeRecyclerView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                holder.challengeRecyclerView.setHasFixedSize(true)
                holder.categoryTextView.text = "Live Challenges"
            } else if (holder is ViewHolder) {
                if (liveChallenges.isEmpty()) {
                    holder.apply {
                        if (position == 0) {
                            categoryTextView.visibility = View.VISIBLE
                        } else {
                            categoryTextView.visibility = View.GONE
                        }
                        Picasso.get().load(priviousWeekChallenges[position].extraData[0].challenge.imageUrl).error(
                            R.drawable.default_article
                        ).into(tagImageView)
                        challengeNameText.text = priviousWeekChallenges[position].display_name

                    }
                } else {
                    holder.apply {
                        if (position - 1 == 0) {
                            categoryTextView.visibility = View.VISIBLE
                        } else {
                            categoryTextView.visibility = View.GONE
                        }

                        Picasso.get().load(priviousWeekChallenges[position - 1].extraData[0].challenge.imageUrl).error(
                            R.drawable.default_article
                        ).into(tagImageView)
                        challengeNameText.text = priviousWeekChallenges[position - 1].display_name

                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal val tagImageView: ImageView = view.tagImageView
        internal val info: ImageView = view.info
        internal val challengeNameText: TextView = view.challengeNameText
        internal val categoryTextView: TextView = view.categoryTextView

        init {
            tagImageView.setOnClickListener(this)
            info.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.tagImageView -> {
                    if (liveChallenges.isEmpty()) {
                        priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                            v,
                            priviousWeekChallenges[adapterPosition]
                        )
                    } else {
                        priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                            v,
                            priviousWeekChallenges[adapterPosition - 1]
                        )
                    }
                }
                R.id.info -> {
                    if (liveChallenges.isEmpty()) {
                        priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                            v,
                            priviousWeekChallenges[adapterPosition]
                        )
                    } else {
                        priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                            v,
                            priviousWeekChallenges[adapterPosition - 1]
                        )
                    }
                }
            }
        }
    }

    inner class HorizontalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var challengeRecyclerView: RecyclerView =
            itemView.findViewById<View>(R.id.challengeRecyclerView) as RecyclerView
        var categoryTextView: TextView =
            itemView.findViewById<View>(R.id.categoryTextView) as TextView
    }

    interface BlogsPriviousWeekChallengesClickListener {
        fun onPriviousWeekChallengeClick(
            v: View?,
            topics: Topics
        )
    }

}