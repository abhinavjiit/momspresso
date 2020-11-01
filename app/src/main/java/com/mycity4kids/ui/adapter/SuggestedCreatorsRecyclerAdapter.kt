package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.Rank
import com.mycity4kids.models.response.SuggestedCreators
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso
import org.apache.commons.lang3.StringUtils

class SuggestedCreatorsRecyclerAdapter(
    private val mixFeedPosition: Int,
    private val suggestedCreatorsList: List<SuggestedCreators>?,
    private val listener: SuggestedCreatorsClickListener
) :
    RecyclerView.Adapter<SuggestedCreatorsRecyclerAdapter.SuggestedCreatorViewHolder>() {

    private var mutualFriendsFlag: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedCreatorViewHolder {
        val v0 = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggested_creator_carousel_item, parent, false)
        return SuggestedCreatorViewHolder(v0)
    }

    override fun getItemCount(): Int {
        return suggestedCreatorsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: SuggestedCreatorViewHolder, position: Int) {
        loadImage(
            suggestedCreatorsList?.get(position)?.profilePicUrl?.clientApp,
            holder.authorImageView
        )
        holder.authorNameTextView.text =
            suggestedCreatorsList?.get(position)?.firstName + " " + suggestedCreatorsList?.get(
                position
            )?.lastName
        if (mutualFriendsFlag) {
            holder.mutualFriendsContainer.visibility = View.VISIBLE
            holder.authorRankTextView.visibility = View.GONE
            holder.authorPostsTextView.visibility = View.GONE
            try {
                suggestedCreatorsList?.get(position)?.friendsuserList?.let {
                    if (it.size > 2) {
                        holder.friend1ImageView.visibility = View.VISIBLE
                        holder.friend2ImageView.visibility = View.VISIBLE
                        holder.friend3ImageView.visibility = View.VISIBLE
                        loadImage(it[0].profilePic.clientApp, holder.friend1ImageView)
                        loadImage(it[1].profilePic.clientApp, holder.friend2ImageView)
                        loadImage(it[2].profilePic.clientApp, holder.friend3ImageView)
                        if (suggestedCreatorsList[position].followersCount.toLong() - 3 > 0) {
                            holder.followersCountTextView.text =
                                "+" + AppUtils.withSuffix(suggestedCreatorsList.get(position).followersCount.toLong() - 3)
                        }
                    } else if (it.size > 1) {
                        holder.friend1ImageView.visibility = View.VISIBLE
                        holder.friend2ImageView.visibility = View.VISIBLE
                        holder.friend3ImageView.visibility = View.GONE
                        loadImage(it[0].profilePic.clientApp, holder.friend1ImageView)
                        loadImage(it[1].profilePic.clientApp, holder.friend2ImageView)
                        if (suggestedCreatorsList[position].followersCount.toLong() - 2 > 0) {
                            holder.followersCountTextView.text =
                                "+" + AppUtils.withSuffix(suggestedCreatorsList.get(position).followersCount.toLong() - 2)
                        }
                    } else if (it.isNotEmpty()) {
                        holder.friend1ImageView.visibility = View.VISIBLE
                        holder.friend2ImageView.visibility = View.GONE
                        holder.friend3ImageView.visibility = View.GONE
                        loadImage(it[0].profilePic.clientApp, holder.friend1ImageView)
                        if (suggestedCreatorsList.get(position).followersCount.toLong() - 1 > 0) {
                            holder.followersCountTextView.text =
                                "+" + AppUtils.withSuffix(suggestedCreatorsList.get(position).followersCount.toLong() - 1)
                        }
                    }
                }
            } catch (e: Exception) {

            }
        } else {
            holder.mutualFriendsContainer.visibility = View.GONE
            holder.authorRankTextView.visibility = View.VISIBLE
            holder.authorPostsTextView.visibility = View.VISIBLE
            try {
                holder.authorPostsTextView.text =
                    StringUtils.capitalize(holder.authorRankTextView.context.getString(R.string.blogger_profile_article_count_label).toLowerCase()) + " " + suggestedCreatorsList?.get(
                        position
                    )?.totalArticles
                holder.authorRankTextView.text =
                    StringUtils.capitalize(holder.authorRankTextView.context.getString(R.string.blogger_profile_rank_label).toLowerCase()) + " " + getCurrentLanguageRank(
                        suggestedCreatorsList?.get(position)?.ranks
                    )
            } catch (e: Exception) {
                holder.authorRankTextView.visibility = View.GONE
            }
        }

        if (suggestedCreatorsList?.get(position)?.isfollowing == "1") {
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

    fun setHasMutualFriends(flag: Boolean) {
        this.mutualFriendsFlag = flag
    }

    inner class SuggestedCreatorViewHolder(
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
        internal var friend1ImageView: ImageView =
            itemView.findViewById(R.id.friend1ImageView)
        internal var friend2ImageView: ImageView =
            itemView.findViewById(R.id.friend2ImageView)
        internal var friend3ImageView: ImageView =
            itemView.findViewById(R.id.friend3ImageView)
        internal var followersCountTextView: TextView =
            itemView.findViewById(R.id.followersCountTextView)

        init {
            authorImageView.setOnClickListener(this)
            authorFollowTextView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onSuggestedCreatorClick(
                    mixFeedPosition,
                    adapterPosition,
                    v,
                    suggestedCreatorsList?.get(adapterPosition)
                )
            }
        }
    }

    interface SuggestedCreatorsClickListener {
        fun onSuggestedCreatorClick(
            mixFeedPosition: Int,
            position: Int,
            view: View,
            suggestedCreator: SuggestedCreators?
        )
    }

    private fun getCurrentLanguageRank(rankArray: List<Rank>?): String? {
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
