package com.mycity4kids.profile

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by hemant on 19/7/17.
 */
class UserContentAdapter(private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var mixFeedList: ArrayList<MixFeedResult>? = null

    fun setListData(mixFeedList: ArrayList<MixFeedResult>?) {
        this.mixFeedList = mixFeedList
    }

    override fun getItemViewType(position: Int): Int {
        return if ("1" == mixFeedList!![position].contentType) {
            CONTENT_TYPE_SHORT_STORY
        } else if ("2" == mixFeedList!![position].contentType) {
            CONTENT_TYPE_VIDEO
        } else {
            CONTENT_TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CONTENT_TYPE_ARTICLE) {
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.article_listing_item, parent, false)
            FeedViewHolder(v0, mListener)
        } else if (viewType == CONTENT_TYPE_SHORT_STORY) {
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.short_story_listing_item, parent, false)
            ShortStoriesViewHolder(v0, mListener)
        } else {
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.video_listing_item, parent, false)
            VideosViewHolder(v0, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FeedViewHolder) {
            addArticleItem(holder.txvArticleTitle, holder.forYouInfoLL, holder.viewCountTextView, holder.commentCountTextView,
                    holder.recommendCountTextView, holder.txvAuthorName, holder.articleImageView, holder.videoIndicatorImageView,
                    holder.bookmarkArticleImageView, holder.watchLaterImageView, mixFeedList?.get(position), position, holder)
        } else if (holder is VideosViewHolder) {
            addVideoItem(holder.winnerLayout, holder.txvArticleTitle, holder.txvAuthorName, holder.articleImageView,
                    holder.authorImageView, holder.viewCountTextView, holder.commentCountTextView, holder.recommendCountTextView,
                    holder.goldLogo, mixFeedList?.get(position), holder)
        } else if (holder is ShortStoriesViewHolder) {
            addShortStoryItem(holder.mainView, holder.storyTitleTextView, holder.storyBodyTextView, holder.authorNameTextView,
                    holder.storyCommentCountTextView, holder.storyRecommendationCountTextView, holder.likeImageView,
                    mixFeedList?.get(position), holder)
        }
    }

    override fun getItemCount(): Int {
        return if (mixFeedList == null) 0 else mixFeedList!!.size
    }

    inner class FeedViewHolder internal constructor(view: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
        internal var videoIndicatorImageView: ImageView
        internal var forYouInfoLL: LinearLayout
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var bookmarkArticleImageView: ImageView
        internal var watchLaterImageView: ImageView

        init {
            txvArticleTitle = view.findViewById<View>(R.id.txvArticleTitle) as TextView
            txvAuthorName = view.findViewById<View>(R.id.txvAuthorName) as TextView
            articleImageView = view.findViewById<View>(R.id.articleImageView) as ImageView
            videoIndicatorImageView = view.findViewById<View>(R.id.videoIndicatorImageView) as ImageView
            forYouInfoLL = view.findViewById<View>(R.id.forYouInfoLL) as LinearLayout
            viewCountTextView = view.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView = view.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView = view.findViewById<View>(R.id.recommendCountTextView) as TextView
            bookmarkArticleImageView = view.findViewById<View>(R.id.bookmarkArticleImageView) as ImageView
            watchLaterImageView = view.findViewById<View>(R.id.watchLaterImageView) as ImageView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class ShortStoriesViewHolder(itemView: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var storyTitleTextView: TextView
        internal var storyBodyTextView: TextView
        internal var authorNameTextView: TextView
        internal var storyCommentCountTextView: TextView
        internal var storyRecommendationContainer: LinearLayout
        internal var storyCommentContainer: LinearLayout
        internal var storyRecommendationCountTextView: TextView
        internal var storyOptionImageView: ImageView
        internal var likeImageView: ImageView
        internal var facebookShareImageView: ImageView
        internal var whatsappShareImageView: ImageView
        internal var instagramShareImageView: ImageView
        internal var genericShareImageView: ImageView
        internal var mainView: RelativeLayout

        init {
            mainView = itemView.findViewById<View>(R.id.mainView) as RelativeLayout
            storyTitleTextView = itemView.findViewById<View>(R.id.storyTitleTextView) as TextView
            storyBodyTextView = itemView.findViewById<View>(R.id.storyBodyTextView) as TextView
            authorNameTextView = itemView.findViewById<View>(R.id.authorNameTextView) as TextView
            storyRecommendationContainer = itemView.findViewById<View>(R.id.storyRecommendationContainer) as LinearLayout
            storyCommentContainer = itemView.findViewById<View>(R.id.storyCommentContainer) as LinearLayout
            storyCommentCountTextView = itemView.findViewById<View>(R.id.storyCommentCountTextView) as TextView
            storyRecommendationCountTextView = itemView.findViewById<View>(R.id.storyRecommendationCountTextView) as TextView
            storyOptionImageView = itemView.findViewById<View>(R.id.storyOptionImageView) as ImageView
            likeImageView = itemView.findViewById<View>(R.id.likeImageView) as ImageView
            facebookShareImageView = itemView.findViewById<View>(R.id.facebookShareImageView) as ImageView
            whatsappShareImageView = itemView.findViewById<View>(R.id.whatsappShareImageView) as ImageView
            instagramShareImageView = itemView.findViewById<View>(R.id.instagramShareImageView) as ImageView
            genericShareImageView = itemView.findViewById<View>(R.id.genericShareImageView) as ImageView

            whatsappShareImageView.tag = itemView

            storyRecommendationContainer.setOnClickListener(this)
            facebookShareImageView.setOnClickListener(this)
            whatsappShareImageView.setOnClickListener(this)
            instagramShareImageView.setOnClickListener(this)
            genericShareImageView.setOnClickListener(this)
            authorNameTextView.setOnClickListener(this)
            storyOptionImageView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class VideosViewHolder(itemView: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var winnerLayout: RelativeLayout
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
        internal var authorImageView: ImageView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var goldLogo: TextView

        init {
            winnerLayout = itemView.findViewById<View>(R.id.winnerLayout) as RelativeLayout
            goldLogo = itemView.findViewById<View>(R.id.goldLogo) as TextView
            txvArticleTitle = itemView.findViewById<View>(R.id.txvArticleTitle) as TextView
            txvAuthorName = itemView.findViewById<View>(R.id.txvAuthorName) as TextView
            articleImageView = itemView.findViewById<View>(R.id.articleImageView) as ImageView
            authorImageView = itemView.findViewById<View>(R.id.authorImageView) as ImageView
            viewCountTextView = itemView.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView = itemView.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView = itemView.findViewById<View>(R.id.recommendCountTextView) as TextView
            var drawable = itemView.context.resources.getDrawable(R.drawable.ic_star_gold_videos)
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable, itemView.context.resources.getColor(R.color.gold_color_video_listing))
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
            goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    private fun addArticleItem(articleTitleTV: TextView, forYouInfoLL: LinearLayout, viewCountTV: TextView,
                               commentCountTV: TextView, recommendCountTV: TextView, authorNameTV: TextView,
                               articleIV: ImageView, videoIndicatorIV: ImageView, bookmarkArticleIV: ImageView, watchLaterIV: ImageView,
                               data: MixFeedResult?, position: Int, holder: RecyclerView.ViewHolder) {
        articleTitleTV.text = data?.title
        forYouInfoLL.visibility = View.GONE
        if (null == data?.articleCount || 0 == data.articleCount) {
            viewCountTV.visibility = View.GONE
        } else {
            viewCountTV.visibility = View.VISIBLE
            viewCountTV.text = "" + data.articleCount
        }
        if (null == data?.commentsCount || 0 == data?.commentsCount) {
            commentCountTV.visibility = View.GONE
        } else {
            commentCountTV.visibility = View.VISIBLE
            commentCountTV.text = "" + data?.commentsCount
        }
        if (null == data?.likesCount || 0 == data?.likesCount) {
            recommendCountTV.visibility = View.GONE
        } else {
            recommendCountTV.visibility = View.VISIBLE
            recommendCountTV.text = "" + data?.likesCount
        }
        if (StringUtils.isNullOrEmpty(data?.userName) || data?.userName?.trim { it <= ' ' }.equals("", ignoreCase = true)) {
            authorNameTV.text = "NA"
        } else {
            authorNameTV.text = data?.userName
        }
        try {
            if (!StringUtils.isNullOrEmpty(data?.videoUrl) && (data?.imageUrl?.thumbMax == null
                            || data?.imageUrl.thumbMax.contains("default.jp"))) {
                Picasso.with(holder.itemView.context).load(AppUtils.getYoutubeThumbnailURLMomspresso(data?.videoUrl))
                        .placeholder(R.drawable.default_article).into(articleIV)
            } else {
                if (!StringUtils.isNullOrEmpty(data?.imageUrl?.thumbMax)) {
                    Picasso.with(holder.itemView.context).load(data?.imageUrl?.thumbMax)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(articleIV)
                } else {
                    articleIV.setBackgroundResource(R.drawable.default_article)
                }
            }
        } catch (e: Exception) {
            articleIV.setBackgroundResource(R.drawable.default_article)
        }

        if (!StringUtils.isNullOrEmpty(data?.videoUrl)) {
            videoIndicatorIV.visibility = View.VISIBLE
        } else {
            videoIndicatorIV.visibility = View.INVISIBLE
        }
//        if ("1" == data?.isMomspresso) {
//            bookmarkArticleIV.visibility = View.VISIBLE
//            watchLaterIV.visibility = View.GONE
//            if ("0" == data.is_bookmark) {
//                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_bookmark))
//            } else {
//                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_bookmarked))
//            }
//        } else {
//            bookmarkArticleIV.visibility = View.VISIBLE
//            watchLaterIV.visibility = View.GONE
//            if ("0" == data.is_bookmark) {
//                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_bookmark))
//            } else {
//                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_bookmarked))
//            }
//        }
//        watchLaterIV.setOnClickListener {
//            addRemoveWatchLater(position, holder)
//            Utils.pushWatchLaterArticleEvent(holder.itemView.context, "ArticleListing",
//                    SharedPrefUtils.getUserDetailModel(holder.itemView.context).dynamoId + "",
//                    data?.id, data?.userId + "~" + data?.userName)
//        }
//        bookmarkArticleIV.setOnClickListener {
//            if (!isRequestRunning) {
//                addRemoveBookmark(position, holder)
//            }
//            Utils.pushBookmarkArticleEvent(holder.itemView.context, "ArticleListing",
//                    SharedPrefUtils.getUserDetailModel(holder.itemView.context).dynamoId + "",
//                    data?.id, data?.userId + "~" + data?.userName)
//        }
    }

    private fun addShortStoryItem(mainViewRL: RelativeLayout, storyTitleTV: TextView, storyBodyTV: TextView, authorNameTV: TextView,
                                  storyCommentCountTV: TextView, storyRecommendationCountTV: TextView, likeIV: ImageView,
                                  data: MixFeedResult?, holder: ShortStoriesViewHolder) {
        mainViewRL.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.short_story_card_bg_6))
        storyTitleTV.text = data?.title?.trim { it <= ' ' }
        storyBodyTV.text = data?.body?.trim { it <= ' ' }
        authorNameTV.text = data?.userName
        if (null == data?.commentsCount) {
            storyCommentCountTV.text = "0"
        } else {
            storyCommentCountTV.text = "" + data.commentsCount
        }
        if (null == data?.likesCount) {
            storyRecommendationCountTV.text = "0"
        } else {
            storyRecommendationCountTV.text = "" + data.likesCount
        }
//        if (data.isLiked) {
        likeIV.setImageDrawable(null)
//        } else {
//            likeIV.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_ss_like))
//        }
    }

    private fun addVideoItem(winnerLayout: RelativeLayout, txvArticleTitle: TextView, txvAuthorName: TextView, articleImageView: ImageView,
                             authorImageView: ImageView, viewCountTextView: TextView, commentCountTextView: TextView,
                             recommendCountTextView: TextView, goldLogo: TextView, data: MixFeedResult?, holder: RecyclerView.ViewHolder) {
        txvArticleTitle.text = data?.title
        viewCountTextView.text = "" + data?.view_count
        commentCountTextView.text = "" + data?.comment_count
        recommendCountTextView.text = "" + data?.like_count

        try {
            val userName = (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).first_name +
                    " " + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).last_name)
            if (StringUtils.isNullOrEmpty(userName) || userName.trim({ it <= ' ' }).equals("", ignoreCase = true)) {
                txvAuthorName.setText("NA")
            } else {
                txvAuthorName.setText(userName)
            }
        } catch (e: Exception) {
            txvAuthorName.setText("NA")
        }

        try {
            Picasso.with(holder.itemView.context).load(data?.thumbnail)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(articleImageView)
        } catch (e: Exception) {
            articleImageView.setImageResource(R.drawable.default_article)
        }

        if (data?.is_gold != null && data.is_gold) {
            goldLogo.visibility = View.VISIBLE
        } else {
            goldLogo.visibility = View.GONE
        }
        if (data?.winner != null && data.winner) {
            winnerLayout.visibility = View.VISIBLE
        } else {
            winnerLayout.visibility = View.GONE
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }

    companion object {

        private val CONTENT_TYPE_SHORT_STORY = 0
        private val CONTENT_TYPE_ARTICLE = 1
        private val CONTENT_TYPE_VIDEO = 2
    }

}