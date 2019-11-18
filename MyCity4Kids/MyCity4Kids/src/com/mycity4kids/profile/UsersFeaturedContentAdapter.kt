package com.mycity4kids.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import org.apmem.tools.layouts.FlowLayout
import java.util.*

class UsersFeaturedContentAdapter(private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val CONTENT_TYPE_SHORT_STORY = 0
    private val CONTENT_TYPE_ARTICLE = 1
    private val mContext: Context? = null
    private val mInflator: LayoutInflater? = null
    internal var featuredItemsList: ArrayList<FeaturedItem>? = null

    override fun getItemViewType(position: Int): Int {
        return if ("1" == featuredItemsList?.get(position)?.itemType) {
            CONTENT_TYPE_SHORT_STORY
        } else {
            CONTENT_TYPE_ARTICLE
        }
    }

    fun setListData(featuredItemsList: ArrayList<FeaturedItem>) {
        this.featuredItemsList = featuredItemsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CONTENT_TYPE_ARTICLE) {
            var viewHolder: UserFeaturedContentViewHolder? = null
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.featured_item, parent, false)
            viewHolder = UserFeaturedContentViewHolder(v0, mListener)
            return viewHolder
        } else {
            var viewHolder: UserRecommendedSSViewHolder? = null
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_activity_short_stories_item, parent, false)
            viewHolder = UserRecommendedSSViewHolder(v0, mListener)
            return viewHolder
        }
    }

    override fun getItemCount(): Int {
        return featuredItemsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserFeaturedContentViewHolder) {
            holder.titleTextView.text = featuredItemsList?.get(position)?.title
            try {
                if (!StringUtils.isNullOrEmpty(featuredItemsList?.get(position)?.thumbnail)) {
//                    Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(featuredItemsList?.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.contentImageView)
                } else {
//                    if (!StringUtils.isNullOrEmpty(featuredItemsList?.get(position).getImageUrl().getThumbMax())) {
//                        Picasso.with(mContext).load(featuredItemsList?.get(position).getImageUrl().getThumbMax())
//                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.contentImageView)
//                    } else {
                    holder.contentImageView.setBackgroundResource(R.drawable.default_article)
//                    }
                }
            } catch (e: Exception) {
                holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            }
            when {
                (featuredItemsList?.get(position)?.collectionList == null) || (featuredItemsList?.get(position)?.collectionList?.size == 0) -> {
                    holder.collectionsFLContainer.visibility = GONE
                }
                (featuredItemsList?.get(position)?.collectionList?.size ?: 0) == 1 -> {
                    holder.collectionItem2TextView.visibility = GONE
                    holder.collectionItem3TextView.visibility = GONE
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = featuredItemsList?.get(position)?.collectionList?.get(0)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (featuredItemsList?.get(position)?.collectionList?.size ?: 0) == 2 -> {
                    holder.collectionItem3TextView.visibility = GONE
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = featuredItemsList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = featuredItemsList?.get(position)?.collectionList?.get(1)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (featuredItemsList?.get(position)?.collectionList?.size ?: 0) == 3 -> {
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = featuredItemsList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = featuredItemsList?.get(position)?.collectionList?.get(1)?.name
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.text = featuredItemsList?.get(position)?.collectionList?.get(2)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (featuredItemsList?.get(position)?.collectionList?.size ?: 0) == 4 -> {
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = featuredItemsList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = featuredItemsList?.get(position)?.collectionList?.get(1)?.name
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.text = featuredItemsList?.get(position)?.collectionList?.get(2)?.name
                    holder.collectionItem4TextView.visibility = VISIBLE
                    holder.collectionItem4TextView.text = featuredItemsList?.get(position)?.collectionList?.get(3)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (featuredItemsList?.get(position)?.collectionList?.size ?: 0) >= 5 -> {
                    val moreItemCount = featuredItemsList?.get(position)?.collectionList?.size!! - 5
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem4TextView.visibility = VISIBLE
                    holder.moreItemsTextView.visibility = VISIBLE
                    holder.moreItemsTextView.text = "" + moreItemCount + " More Items"
                }
            }
        } else {
//            val shortStoryViewHolder = holder as UserRecommendedSSViewHolder
//            shortStoryViewHolder.txvArticleTitle.setText(featuredItemsList?.get(position).title)
//
//            shortStoryViewHolder.commentCountTextView.setText(featuredItemsList?.get(position).getCommentCount())
//            if (StringUtils.isNullOrEmpty(featuredItemsList?.get(position).getCommentCount()) || "0" == featuredItemsList?.get(position).getCommentCount()) {
//                shortStoryViewHolder.commentCountTextView.setVisibility(View.GONE)
//            } else {
//                shortStoryViewHolder.commentCountTextView.setVisibility(View.VISIBLE)
//            }
//            shortStoryViewHolder.recommendCountTextView.setText(featuredItemsList?.get(position).getLikesCount())
//            if (StringUtils.isNullOrEmpty(featuredItemsList?.get(position).getLikesCount()) || "0" == featuredItemsList?.get(position).getLikesCount()) {
//                shortStoryViewHolder.recommendCountTextView.setVisibility(View.GONE)
//                shortStoryViewHolder.separatorView2.setVisibility(View.GONE)
//            } else {
//                shortStoryViewHolder.recommendCountTextView.setVisibility(View.VISIBLE)
//                shortStoryViewHolder.separatorView2.setVisibility(View.VISIBLE)
//            }
        }
    }

    inner class UserFeaturedContentViewHolder internal constructor(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var contentImageView: ImageView
        internal var dateTextView: TextView
        internal var titleTextView: TextView
        internal var collectionItem1TextView: TextView
        internal var collectionItem2TextView: TextView
        internal var collectionItem3TextView: TextView
        internal var collectionItem4TextView: TextView
        internal var collectionItem5TextView: TextView
        internal var moreItemsTextView: TextView
        internal var collectionsFLContainer: FlowLayout


        init {
            titleTextView = itemView.findViewById<View>(R.id.titleTextView) as TextView
            contentImageView = itemView.findViewById<View>(R.id.itemImageView) as ImageView
            dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView
            collectionItem1TextView = itemView.findViewById<View>(R.id.collectionItem1TextView) as TextView
            collectionItem2TextView = itemView.findViewById<View>(R.id.collectionItem2TextView) as TextView
            collectionItem3TextView = itemView.findViewById<View>(R.id.collectionItem3TextView) as TextView
            collectionItem4TextView = itemView.findViewById<View>(R.id.collectionItem4TextView) as TextView
            collectionItem5TextView = itemView.findViewById<View>(R.id.collectionItem5TextView) as TextView
            moreItemsTextView = itemView.findViewById<View>(R.id.moreItemsTextView) as TextView
            collectionsFLContainer = itemView.findViewById<View>(R.id.collectionsFLContainer) as FlowLayout
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onFeaturedItemClick(v, adapterPosition)
        }
    }

    inner class UserRecommendedSSViewHolder internal constructor(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var txvArticleTitle: TextView
        internal var txvPublishDate: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var sharecontentImageView: ImageView
        internal var separatorView2: View

        init {
            txvArticleTitle = itemView.findViewById<View>(R.id.articleTitleTextView) as TextView
            txvPublishDate = itemView.findViewById<View>(R.id.txvPublishDate) as TextView
            commentCountTextView = itemView.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView = itemView.findViewById<View>(R.id.recommendCountTextView) as TextView
            sharecontentImageView = itemView.findViewById<View>(R.id.shareImageView) as ImageView
            separatorView2 = itemView.findViewById(R.id.separatorView2)
            sharecontentImageView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onFeaturedItemClick(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onFeaturedItemClick(view: View, position: Int)
    }
}
