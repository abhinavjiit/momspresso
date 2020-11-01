package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.ContributorListResult
import com.mycity4kids.models.response.LanguageRanksModel
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso
import org.apache.commons.lang3.StringUtils
import java.util.ArrayList

class TopCreatorsRecyclerAdapter(
    private val mixFeedPosition: Int,
    private val topCreatorsList: List<ContributorListResult>?,
    private val listener: TopCreatorsClickListener
) :
    RecyclerView.Adapter<TopCreatorsRecyclerAdapter.TopCreatorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopCreatorViewHolder {
        val v0 = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggested_creator_carousel_item, parent, false)
        return TopCreatorViewHolder(v0)
    }

    override fun getItemCount(): Int {
        return topCreatorsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: TopCreatorViewHolder, position: Int) {
        loadImage(
            topCreatorsList?.get(position)?.profilePic?.clientApp,
            holder.authorImageView
        )
        holder.authorNameTextView.text =
            topCreatorsList?.get(position)?.firstName + " " + topCreatorsList?.get(
                position
            )?.lastName

        holder.mutualFriendsContainer.visibility = View.GONE
        holder.authorRankTextView.visibility = View.VISIBLE
        holder.authorPostsTextView.visibility = View.GONE
        try {
            holder.authorRankTextView.text =
                StringUtils.capitalize(holder.authorRankTextView.context.getString(R.string.blogger_profile_rank_label).toLowerCase()) + " " + getCurrentLanguageRank(
                    topCreatorsList?.get(position)?.ranks
                )
        } catch (e: Exception) {

        }
        if (topCreatorsList?.get(position)?.isFollowed == 1) {
            holder.authorFollowTextView.isSelected = true
            holder.authorFollowTextView.setText(holder.authorFollowTextView.context.getString(R.string.all_following))
        } else {
            holder.authorFollowTextView.isSelected = false
            holder.authorFollowTextView.setText(holder.authorFollowTextView.context.getString(R.string.all_follow))
        }
    }

    private fun loadImage(imageUrl: String?, imageView: ImageView) {
        try {
            Picasso.get().load(imageUrl).placeholder(
                R.drawable.default_article
            ).error(
                R.drawable.default_article
            ).into(imageView)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.default_article)
        }
    }

    inner class TopCreatorViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorImageView: ImageView =
            itemView.findViewById(R.id.authorImageView)
        internal var authorNameTextView: TextView =
            itemView.findViewById(R.id.authorNameTextView)
        internal var authorRankTextView: TextView =
            itemView.findViewById(R.id.authorRankTextView)
        internal var authorPostsTextView: TextView =
            itemView.findViewById(R.id.authorPostsTextView)
        internal var authorFollowTextView: MomspressoButtonWidget =
            itemView.findViewById(R.id.authorFollowTextView)
        internal var mutualFriendsContainer: RelativeLayout =
            itemView.findViewById(R.id.mutualFriendsContainer)

        init {
            authorImageView.setOnClickListener(this)
            authorFollowTextView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onTopCreatorClick(
                    mixFeedPosition,
                    adapterPosition,
                    v,
                    topCreatorsList?.get(adapterPosition)
                )
            }
        }
    }

    interface TopCreatorsClickListener {
        fun onTopCreatorClick(
            mixFeedPosition: Int,
            position: Int,
            view: View,
            topCreator: ContributorListResult?
        )
    }

    private fun getCurrentLanguageRank(rankArray: ArrayList<LanguageRanksModel>?): String? {
        val langKey = AppUtils.getLangKey().toString()
        rankArray?.let {
            for (i in it.indices) {
                if (it[i].langKey == langKey) {
                    return "" + it[i].rank
                }
            }
        }
        return "--"
    }
}
