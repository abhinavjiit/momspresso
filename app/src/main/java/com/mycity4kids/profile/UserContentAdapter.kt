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
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.widget.StoryShareCardWidget
import com.squareup.picasso.Picasso

/**
 * Created by hemant on 19/7/17.
 */
class UserContentAdapter(
    private val mListener: RecyclerViewClickListener,
    private val isPrivate: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mixFeedList: ArrayList<MixFeedResult>? = null

    fun setListData(mixFeedList: ArrayList<MixFeedResult>?) {
        this.mixFeedList = mixFeedList
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            AppConstants.CONTENT_TYPE_CREATE_SECTION == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_CREATE
            AppConstants.CONTENT_TYPE_SHORT_STORY == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_SHORT_STORY
            AppConstants.CONTENT_TYPE_VIDEO == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_VIDEO
            else -> CONTENT_TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CONTENT_TYPE_CREATE -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.profile_add_content_item, parent, false)
                CreateContentViewHolder(v0, mListener)
            }
            CONTENT_TYPE_ARTICLE -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.article_listing_item, parent, false)
                FeedViewHolder(v0, mListener)
            }
            CONTENT_TYPE_SHORT_STORY -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.short_story_listing_item, parent, false)
                ShortStoriesViewHolder(v0, mListener)
            }
            else -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.video_listing_item, parent, false)
                VideosViewHolder(v0, mListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CreateContentViewHolder -> {
            }
            is FeedViewHolder -> addArticleItem(
                holder.txvArticleTitle,
                holder.forYouInfoLL,
                holder.viewCountTextView,
                holder.commentCountTextView,
                holder.recommendCountTextView,
                holder.txvAuthorName,
                holder.articleImageView,
                holder.videoIndicatorImageView,
                holder.bookmarkArticleImageView,
                holder.watchLaterImageView,
                mixFeedList?.get(position),
                position,
                holder,
                isPrivate
            )
            is VideosViewHolder -> addVideoItem(
                holder.winnerLayout,
                holder.txvArticleTitle,
                holder.txvAuthorName,
                holder.articleImageView,
                holder.viewCountTextView,
                holder.commentCountTextView,
                holder.recommendCountTextView,
                holder.goldLogo,
                mixFeedList?.get(position),
                holder,
                isPrivate
            )
            is ShortStoriesViewHolder -> addShortStoryItem(
                holder.logoImageView,
                holder.storyAuthorTextView,
                holder.shareStoryImageView,
                holder.storyImage,
                holder.authorNameTextView,
                holder.storyCommentCountTextView,
                holder.storyRecommendationCountTextView,
                holder.likeImageView,
                mixFeedList?.get(position),
                holder,
                isPrivate
            )
        }
    }

    override fun getItemCount(): Int {
        return if (mixFeedList == null) 0 else mixFeedList!!.size
    }

    inner class CreateContentViewHolder internal constructor(
        view: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var draftContainer: RelativeLayout
        private var storyContainer: RelativeLayout
        private var articleContainer: RelativeLayout
        private var vlogContainer: RelativeLayout

        init {
            draftContainer = view.findViewById(R.id.draftContainer)
            storyContainer = view.findViewById(R.id.storyContainer)
            articleContainer = view.findViewById(R.id.articleContainer)
            vlogContainer = view.findViewById(R.id.vlogContainer)
            draftContainer.setOnClickListener(this)
            storyContainer.setOnClickListener(this)
            articleContainer.setOnClickListener(this)
            vlogContainer.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class FeedViewHolder internal constructor(
        view: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
        internal var videoIndicatorImageView: ImageView
        internal var forYouInfoLL: LinearLayout
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var bookmarkArticleImageView: ImageView
        internal var shareArticleImageView: ImageView
        internal var watchLaterImageView: ImageView
        internal var editArticleTextView: TextView
        internal var menuItemImageView: ImageView

        init {
            txvArticleTitle = view.findViewById<View>(R.id.txvArticleTitle) as TextView
            txvAuthorName = view.findViewById<View>(R.id.txvAuthorName) as TextView
            articleImageView = view.findViewById<View>(R.id.articleImageView) as ImageView
            videoIndicatorImageView =
                view.findViewById<View>(R.id.videoIndicatorImageView) as ImageView
            forYouInfoLL = view.findViewById<View>(R.id.forYouInfoLL) as LinearLayout
            viewCountTextView = view.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView = view.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView =
                view.findViewById<View>(R.id.recommendCountTextView) as TextView
            bookmarkArticleImageView =
                view.findViewById<View>(R.id.bookmarkArticleImageView) as ImageView
            shareArticleImageView = view.findViewById<View>(R.id.shareArticleImageView) as ImageView
            watchLaterImageView = view.findViewById<View>(R.id.watchLaterImageView) as ImageView
            editArticleTextView = view.findViewById<View>(R.id.editArticleTextView) as TextView
            menuItemImageView = view.findViewById<View>(R.id.menuItemImageView) as ImageView
            menuItemImageView.visibility = View.VISIBLE
            shareArticleImageView.setOnClickListener(this)
            bookmarkArticleImageView.setOnClickListener(this)
            editArticleTextView.setOnClickListener(this)
            menuItemImageView.setOnClickListener(this)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class ShortStoriesViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorNameTextView: TextView
        internal var storyCommentCountTextView: TextView
        internal var storyRecommendationContainer: LinearLayout
        internal var storyCommentContainer: LinearLayout
        internal var storyRecommendationCountTextView: TextView
        internal var storyImage: ImageView
        internal var likeImageView: ImageView
        internal var facebookShareImageView: ImageView
        internal var whatsappShareImageView: ImageView
        internal var instagramShareImageView: ImageView
        internal var addToCollectionImageView: ImageView
        internal var followAuthorTextView: TextView
        internal var menuItem: ImageView
        internal var storyShareCardWidget: StoryShareCardWidget
        internal var shareStoryImageView: ImageView
        internal var storyAuthorTextView: TextView
        internal var logoImageView: ImageView
        internal var editStoryTextView: TextView

        init {
            authorNameTextView = itemView.findViewById<View>(R.id.authorNameTextView) as TextView
            storyRecommendationContainer =
                itemView.findViewById<View>(R.id.storyRecommendationContainer) as LinearLayout
            storyCommentContainer =
                itemView.findViewById<View>(R.id.storyCommentContainer) as LinearLayout
            storyCommentCountTextView =
                itemView.findViewById<View>(R.id.storyCommentCountTextView) as TextView
            storyRecommendationCountTextView =
                itemView.findViewById<View>(R.id.storyRecommendationCountTextView) as TextView
            storyImage = itemView.findViewById<View>(R.id.storyImageView1) as ImageView
            likeImageView = itemView.findViewById<View>(R.id.likeImageView) as ImageView
            facebookShareImageView =
                itemView.findViewById<View>(R.id.facebookShareImageView) as ImageView
            whatsappShareImageView =
                itemView.findViewById<View>(R.id.whatsappShareImageView) as ImageView
            instagramShareImageView =
                itemView.findViewById<View>(R.id.instagramShareImageView) as ImageView
            addToCollectionImageView =
                itemView.findViewById<View>(R.id.genericShareImageView) as ImageView
            followAuthorTextView =
                itemView.findViewById<View>(R.id.followAuthorTextView) as TextView
            storyShareCardWidget =
                itemView.findViewById<View>(R.id.storyShareCardWidget) as StoryShareCardWidget
            editStoryTextView = itemView.findViewById(R.id.editStoryTextView) as TextView
            shareStoryImageView =
                storyShareCardWidget.findViewById(R.id.storyImageView) as ImageView
            logoImageView = storyShareCardWidget.findViewById(R.id.logoImageView) as ImageView
            storyAuthorTextView =
                storyShareCardWidget.findViewById(R.id.storyAuthorTextView) as TextView
            followAuthorTextView.visibility = View.INVISIBLE
            menuItem = itemView.findViewById<View>(R.id.menuItem) as ImageView
            whatsappShareImageView.tag = itemView

            storyRecommendationContainer.setOnClickListener(this)
            facebookShareImageView.setOnClickListener(this)
            whatsappShareImageView.setOnClickListener(this)
            instagramShareImageView.setOnClickListener(this)
            addToCollectionImageView.setOnClickListener(this)
            authorNameTextView.setOnClickListener(this)
            storyImage.setOnClickListener(this)
            followAuthorTextView.setOnClickListener(this)
            editStoryTextView.setOnClickListener(this)
            menuItem.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class VideosViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var winnerLayout: RelativeLayout
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
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
            viewCountTextView = itemView.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView =
                itemView.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView =
                itemView.findViewById<View>(R.id.recommendCountTextView) as TextView

            var drawable =
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_star_gold_videos)
            drawable?.let {
                drawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(
                    it,
                    ContextCompat.getColor(itemView.context, R.color.gold_color_video_listing)
                )
                DrawableCompat.setTintMode(it, PorterDuff.Mode.SRC_IN)
            }
            goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    private fun addArticleItem(
        articleTitleTV: TextView,
        forYouInfoLL: LinearLayout,
        viewCountTV: TextView,
        commentCountTV: TextView,
        recommendCountTV: TextView,
        authorNameTV: TextView,
        articleIV: ImageView,
        videoIndicatorIV: ImageView,
        bookmarkArticleIV: ImageView,
        watchLaterIV: ImageView,
        data: MixFeedResult?,
        position: Int,
        holder: FeedViewHolder,
        private: Boolean
    ) {
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
        if (data?.userName.isNullOrBlank()) {
            authorNameTV.text = "NA"
        } else {
            authorNameTV.text = data?.userName
        }
        try {
            if (!StringUtils.isNullOrEmpty(data?.videoUrl) && (data?.imageUrl?.thumbMax == null ||
                    data.imageUrl.thumbMax.contains("default.jp"))
            ) {
                Picasso.get().load(AppUtils.getYoutubeThumbnailURLMomspresso(data?.videoUrl))
                    .placeholder(R.drawable.default_article).into(articleIV)
            } else {
                if (!StringUtils.isNullOrEmpty(data?.imageUrl?.thumbMax)) {
                    Picasso.get().load(data?.imageUrl?.thumbMax)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .into(articleIV)
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

        if (private) {
            holder.shareArticleImageView.visibility = View.VISIBLE
            holder.bookmarkArticleImageView.visibility = View.GONE
            holder.editArticleTextView.visibility = View.VISIBLE
        } else {
            holder.editArticleTextView.visibility = View.GONE
            holder.shareArticleImageView.visibility = View.GONE
            holder.bookmarkArticleImageView.visibility = View.VISIBLE
            if (data?.isbookmark == 0) {
                holder.bookmarkArticleImageView.setImageResource(R.drawable.ic_bookmark)
            } else {
                holder.bookmarkArticleImageView.setImageResource(R.drawable.ic_bookmarked)
            }
        }
    }

    private fun addShortStoryItem(
        logoImageView: ImageView,
        storyAuthorTextView: TextView,
        shareStoryImageView: ImageView,
        storyImage: ImageView,
        authorNameTV: TextView,
        storyCommentCountTV: TextView,
        storyRecommendationCountTV: TextView,
        likeIV: ImageView,
        data: MixFeedResult?,
        holder: ShortStoriesViewHolder,
        private: Boolean
    ) {
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
        data?.isLiked?.let {
            if (it) {
                holder.likeImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_recommended
                    )
                )
            } else {
                likeIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_ss_like
                    )
                )
            }
        }

        try {
            Picasso.get().load(data?.storyImage?.trim { it <= ' ' })
                .placeholder(R.drawable.default_article).into(storyImage)
        } catch (e: Exception) {
            holder.storyImage.setImageResource(R.drawable.default_article)
        }
        try {
            Picasso.get().load(data?.storyImage?.trim { it <= ' ' }).into(shareStoryImageView)
            storyAuthorTextView.text = data?.userName
            AppUtils.populateLogoImageLanguageWise(
                holder.itemView.context,
                logoImageView,
                data?.lang.toString()
            )
        } catch (e: Exception) {
            holder.storyImage.setImageResource(R.drawable.default_article)
        }
        if (private) {
            holder.editStoryTextView.visibility = View.VISIBLE
        } else {
            holder.editStoryTextView.visibility = View.GONE
        }
    }

    private fun addVideoItem(
        winnerLayout: RelativeLayout,
        txvArticleTitle: TextView,
        txvAuthorName: TextView,
        articleImageView: ImageView,
        viewCountTextView: TextView,
        commentCountTextView: TextView,
        recommendCountTextView: TextView,
        goldLogo: TextView,
        data: MixFeedResult?,
        holder: RecyclerView.ViewHolder,
        private: Boolean
    ) {
        txvArticleTitle.text = data?.title
        viewCountTextView.text = "" + data?.view_count
        commentCountTextView.text = "" + data?.comment_count
        recommendCountTextView.text = "" + data?.like_count

        try {
            val userName = data?.userName
            if (userName.isNullOrBlank()) {
                txvAuthorName.text = ""
            } else {
                txvAuthorName.text = userName
            }
        } catch (e: Exception) {
            txvAuthorName.text = ""
        }

        try {
            Picasso.get().load(data?.thumbnail)
                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .into(articleImageView)
        } catch (e: Exception) {
            articleImageView.setImageResource(R.drawable.default_article)
        }

        if (data?.is_gold != null && data.is_gold) {
            goldLogo.visibility = View.VISIBLE
        } else {
            goldLogo.visibility = View.GONE
        }
        if (data?.winner != null && data.winner as Boolean) {
            winnerLayout.visibility = View.VISIBLE
        } else {
            winnerLayout.visibility = View.GONE
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }

    companion object {
        private val CONTENT_TYPE_CREATE = 0
        private val CONTENT_TYPE_SHORT_STORY = 1
        private val CONTENT_TYPE_ARTICLE = 2
        private val CONTENT_TYPE_VIDEO = 3
    }
}
