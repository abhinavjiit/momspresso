package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlinx.android.synthetic.main.blog_challenge_adapter.view.*

class BlogChallengeAdapter(
    private var challengeList: ArrayList<Topics>,
    private val listener: ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener,
    private val priviousWeekChallengesListner: BlogsPriviousWeekChallengesClickListener,
    private val context: Context?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.blog_challenge_adapter,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return challengeList.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is ViewHolder) {
                holder.apply {
                    when (position) {
                        0 -> {
                            categoryTextView.text =
                                context?.resources?.getString(R.string.this_week_challenge)
                        }
                        1 -> {
                            categoryTextView.text =
                                context?.resources?.getString(R.string.previous_week_challenge)
                        }
                        else -> {
                            categoryTextView.visibility = View.GONE
                        }
                    }
                    Picasso.get().load(challengeList[position].extraData[0].challenge.imageUrl).error(
                        R.drawable.default_article
                    ).into(tagImageView)
                    challengeNameText.text = challengeList[position].display_name
                    if (challengeList[position].extraData[0].challenge.is_live == "1") {
                        liveTextViewVideoChallenge.visibility = View.VISIBLE
                    } else {
                        liveTextViewVideoChallenge.visibility = View.GONE
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
        internal val liveTextViewVideoChallenge: TextView = view.liveTextViewVideoChallenge

        init {
            tagImageView.setOnClickListener(this)
            info.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.tagImageView -> {
                    priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                        v,
                        challengeList[adapterPosition]
                    )
                }
                R.id.info -> {
                    priviousWeekChallengesListner.onPriviousWeekChallengeClick(
                        v,
                        challengeList[adapterPosition]
                    )
                }
            }
        }
    }

    interface BlogsPriviousWeekChallengesClickListener {
        fun onPriviousWeekChallengeClick(
            v: View?,
            topics: Topics
        )
    }
}
