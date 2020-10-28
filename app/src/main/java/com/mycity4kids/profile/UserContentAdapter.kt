package com.mycity4kids.profile

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.ui.adapter.SuggestedCreatorsRecyclerAdapter
import com.mycity4kids.ui.adapter.TopCreatorsRecyclerAdapter
import com.mycity4kids.ui.livestreaming.RecentOrUpcomingLiveStreamsHorizontalAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.ImageKitUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.StoryShareCardWidget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.short_story_listing_item.view.*
import org.apmem.tools.layouts.FlowLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

/**
 * Created by hemant on 19/7/17.
 */
class UserContentAdapter(
    private val mListener: RecyclerViewClickListener,
    private val horizontalListener: RecentOrUpcomingLiveStreamsHorizontalAdapter.HorizontalRecyclerViewClickListener,
    private val suggestedCreatorListener: SuggestedCreatorsRecyclerAdapter.SuggestedCreatorsClickListener,
    private val topCreatorListener: TopCreatorsRecyclerAdapter.TopCreatorsClickListener,
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
            AppConstants.CONTENT_TYPE_RECENT_LIVE_STREAM == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_RECENT_LIVE_STREAM
            AppConstants.CONTENT_TYPE_UPCOMING_LIVE_STREAM == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_UPCOMING_LIVE_STREAM
            AppConstants.CONTENT_TYPE_TORCAI_ADS == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_TORCAI_ADS
            AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS == mixFeedList?.get(
                position
            )?.contentType -> CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS
            AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS == mixFeedList?.get(
                position
            )?.contentType -> CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS
            AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY == mixFeedList?.get(
                position
            )?.contentType -> CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY
            AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS
            AppConstants.CONTENT_TYPE_SUGGESTED_TOPICS == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_SUGGESTED_TOPICS
            AppConstants.CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS
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
            CONTENT_TYPE_RECENT_LIVE_STREAM -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.live_stream_list_item, parent, false)
                RecentLiveStreamViewHolder(v0, mListener)
            }
            CONTENT_TYPE_UPCOMING_LIVE_STREAM -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.upcoming_live_streams_item, parent, false)
                UpcomingLiveStreamViewHolder(v0, mListener)
            }
            CONTENT_TYPE_TORCAI_ADS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.torcai_ads_item, parent, false)
                TorcaiAdsViewHolder(v0, mListener)
            }
            CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.suggested_creator_item, parent, false)
                SuggestedCreatorsMutualFriendsViewHolder(v0, mListener)
            }
            CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY, CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS, CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.suggested_creator_item, parent, false)
                SuggestedCreatorsViewHolder(v0, mListener, viewType)
            }
            CONTENT_TYPE_SUGGESTED_TOPICS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.suggested_topics_item, parent, false)
                SuggestedTopicsViewHolder(v0, mListener)
            }
            CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.follow_topic_creators_item, parent, false)
                FollowTopicCreatorViewHolder(v0, mListener)
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
                isPrivate,
                holder.trophyImageView
            )
            is VideosViewHolder -> addVideoItem(
                holder.winnerVlogImageView,
                holder.txvArticleTitle,
                holder.txvAuthorName,
                holder.articleImageView,
                holder.viewCountTextView,
                holder.commentCountTextView,
                holder.recommendCountTextView,
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
                isPrivate,
                holder.trophyImageView
            )
            is RecentLiveStreamViewHolder -> {
                if (BuildConfig.DEBUG) {
                    holder.eventId.visibility = View.VISIBLE
                    holder.eventId.text =
                        "" + mixFeedList?.get(position)?.recentLiveStreamsList?.get(0)?.id
                } else {
                    holder.eventId.visibility = View.GONE
                }
                addRecentLiveStreamItem(
                    holder.liveStreamImageView,
                    holder.upcomingLiveTimeWidget,
                    holder.liveOngoingLabel,
                    holder.liveEndedLabel,
                    holder.liveStartsLabel,
                    holder.remainingTimeTextView,
                    mixFeedList?.get(position)
                )
            }
            is UpcomingLiveStreamViewHolder -> addUpcomingLiveStreamItem(
                holder.upcomingLivesRecyclerView,
                mixFeedList?.get(position)
            )
            is TorcaiAdsViewHolder -> loadTorcaiAdData(
                holder.webView,
                mixFeedList?.get(position)
            )
            is SuggestedCreatorsMutualFriendsViewHolder -> addCreatorSuggestionsMutualFriends(
                position,
                holder.suggestedCreatorsRecyclerView,
                mixFeedList?.get(position)
            )
            is SuggestedCreatorsViewHolder -> addCreatorSuggestions(
                position,
                holder.viewType,
                holder.suggestedCreatorsRecyclerView,
                mixFeedList?.get(position)
            )
            is SuggestedTopicsViewHolder -> addTopicsSuggestions(
                holder.topicsContainerFlowLayout,
                mixFeedList?.get(position)
            )
            is FollowTopicCreatorViewHolder -> {
            }
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
        internal var trophyImageView: ImageView

        init {
            trophyImageView = view.findViewById(R.id.trophyImageView)
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
            if (isPrivate) {
                menuItemImageView.visibility = View.VISIBLE
            } else {
                menuItemImageView.visibility = View.GONE
            }
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
        internal var trophyImageView: ImageView
        internal var icSsComment: ImageView

        init {
            trophyImageView = itemView.trophyImageView
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
            menuItem = itemView.findViewById<View>(R.id.menuItem) as ImageView
            icSsComment = itemView.findViewById(R.id.icSsComment) as ImageView
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
            icSsComment.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class VideosViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var winnerVlogImageView: ImageView
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView

        init {
            winnerVlogImageView = itemView.findViewById(R.id.winnerVlogImageView)
            txvArticleTitle = itemView.findViewById(R.id.txvArticleTitle)
            txvAuthorName = itemView.findViewById(R.id.txvAuthorName)
            articleImageView = itemView.findViewById(R.id.articleImageView)
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView)
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView)
            recommendCountTextView = itemView.findViewById(R.id.recommendCountTextView)

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
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class RecentLiveStreamViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var liveStartsLabel: TextView = itemView.findViewById(R.id.liveStartsLabel)
        internal var liveOngoingLabel: CardView =
            itemView.findViewById(R.id.liveOngoingLabel)
        internal var liveEndedLabel: MomspressoButtonWidget =
            itemView.findViewById(R.id.liveEndedLabel)
        internal var liveStreamImageView: ImageView =
            itemView.findViewById(R.id.liveStreamImageView)
        internal var upcomingLiveTimeWidget: CardView =
            itemView.findViewById(R.id.upcomingLiveTimeWidget)
        internal var remainingTimeTextView: TextView =
            itemView.findViewById(R.id.remainingTimeTextView)
        internal var liveIndicatorImageView: ImageView =
            itemView.findViewById(R.id.liveIndicatorImageView)
        internal var eventId: TextView =
            itemView.findViewById(R.id.eventId)
        internal val anim = ObjectAnimator.ofFloat(liveIndicatorImageView, "alpha", 0.0f, 1f)

        init {
            itemView.setOnClickListener(this)
            anim.repeatMode = ValueAnimator.REVERSE
            anim.repeatCount = Animation.INFINITE
            anim.duration = 900
            anim.start()
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class UpcomingLiveStreamViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var viewAllLivesTextView: TextView =
            itemView.findViewById(R.id.viewAllLivesTextView)
        internal var upcomingLivesRecyclerView: RecyclerView =
            itemView.findViewById(R.id.upcomingLivesRecyclerView)

        init {
            viewAllLivesTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class SuggestedCreatorsMutualFriendsViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var sectionHeaderTextView: TextView =
            itemView.findViewById(R.id.sectionHeaderTextView)
        internal var suggestedCreatorsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.suggestedCreatorsRecyclerView)

        init {
            sectionHeaderTextView.text = "Creators your friends follow"
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class SuggestedCreatorsViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener,
        val viewType: Int
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var sectionHeaderTextView: TextView =
            itemView.findViewById(R.id.sectionHeaderTextView)
        internal var suggestedCreatorsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.suggestedCreatorsRecyclerView)

        init {
            if (viewType == CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS) {
                sectionHeaderTextView.text = "Bloggers based on topic followed"
            } else if (viewType == CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY) {
                sectionHeaderTextView.text = "Bloggers based on activity"
            } else if (viewType == CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS) {
                sectionHeaderTextView.text = "Top Bloggers"
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class SuggestedTopicsViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var topicsContainerFlowLayout: FlowLayout =
            itemView.findViewById(R.id.topicsContainerFlowLayout)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class FollowTopicCreatorViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var followTopicsWidget: MomspressoButtonWidget =
            itemView.findViewById(R.id.followTopicsWidget)
        internal var followCreatorsWidget: MomspressoButtonWidget =
            itemView.findViewById(R.id.followCreatorsWidget)

        init {
            followTopicsWidget.setOnClickListener(this)
            followCreatorsWidget.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class TorcaiAdsViewHolder(
        itemView: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        internal var webView: WebView =
            itemView.findViewById(R.id.webView)

        init {
            webView.settings.javaScriptEnabled = true
            webView.settings.layoutAlgorithm =
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    try {
                        if (request.url == null || request.url.toString().isEmpty()) {
                            return true
                        }
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            listener.onTorcaiAdClick(request)
                        }
                        //                        if (AppUtils.isMomspressoDomain(request.url.toString())) {
                        //                            if (getActivity() != null) {
                        //                                (getActivity() as BaseActivity).handleDeeplinks(request.url.toString())
                        //                            }
                        //                        } else {
                        //                            if (getActivity() != null) {
                        //                                val builder = CustomTabsIntent.Builder()
                        //                                val customTabsIntent = builder.build()
                        //                                customTabsIntent.launchUrl(getActivity(), request.url)
                        //                            }
                        //                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                    }
                    return true
                }
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
        private: Boolean,
        trophyImageView: ImageView
    ) {
        try {
            articleTitleTV.text = data?.title
            forYouInfoLL.visibility = View.GONE
            if (null == data?.articleCount || 0 == data.articleCount) {
                viewCountTV.visibility = View.GONE
            } else {
                viewCountTV.visibility = View.VISIBLE
                try {
                    viewCountTV.text = AppUtils.withSuffix(data.articleCount.toLong())
                } catch (e: Exception) {
                    viewCountTV.text = "" + data.articleCount
                }
            }
            if (null == data?.commentsCount || 0 == data?.commentsCount) {
                commentCountTV.visibility = View.GONE
            } else {
                commentCountTV.visibility = View.VISIBLE
                try {
                    commentCountTV.text = AppUtils.withSuffix(data.commentsCount.toLong())
                } catch (e: Exception) {
                    commentCountTV.text = "" + data.commentsCount
                }
            }
            if (null == data?.likesCount || 0 == data.likesCount) {
                recommendCountTV.visibility = View.GONE
            } else {
                recommendCountTV.visibility = View.VISIBLE
                try {
                    recommendCountTV.text = AppUtils.withSuffix(data.likesCount.toLong())
                } catch (e: Exception) {
                    recommendCountTV.text = "" + data.likesCount
                }
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
            showWinnerOrGoldMark(data, trophyImageView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
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
        private: Boolean,
        trophyImageView: ImageView
    ) {
        try {
            authorNameTV.text = data?.userName
            if (null == data?.commentsCount) {
                storyCommentCountTV.text = "0"
            } else {
                try {
                    storyCommentCountTV.text = AppUtils.withSuffix(data.commentsCount.toLong())
                } catch (e: Exception) {
                    storyCommentCountTV.text = "" + data.commentsCount
                }
            }
            if (null == data?.likesCount) {
                storyRecommendationCountTV.text = "0"
            } else {
                try {
                    storyRecommendationCountTV.text = AppUtils.withSuffix(data.likesCount.toLong())
                } catch (e: Exception) {
                    storyRecommendationCountTV.text = "" + data.likesCount
                }
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

            if (isPrivate) {
                holder.followAuthorTextView.visibility = View.INVISIBLE
            } else {
                holder.followAuthorTextView.visibility = View.VISIBLE
                if (data?.isfollowing == "1") {
                    holder.followAuthorTextView.text =
                        holder.followAuthorTextView.context.getString(R.string.all_following).toUpperCase(
                            Locale.getDefault()
                        )
                } else {
                    holder.followAuthorTextView.text =
                        holder.followAuthorTextView.context.getString(R.string.all_follow).toUpperCase(
                            Locale.getDefault()
                        )
                }
            }
            showWinnerOrGoldMark(data, trophyImageView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun addVideoItem(
        winnerVlogImageView: ImageView,
        txvArticleTitle: TextView,
        txvAuthorName: TextView,
        articleImageView: ImageView,
        viewCountTextView: TextView,
        commentCountTextView: TextView,
        recommendCountTextView: TextView,
        data: MixFeedResult?,
        holder: RecyclerView.ViewHolder,
        private: Boolean
    ) {
        try {
            txvArticleTitle.text = data?.title
            try {
                viewCountTextView.text = data?.view_count?.toLong()?.let { AppUtils.withSuffix(it) }
            } catch (e: Exception) {
                viewCountTextView.text = "" + data?.view_count
            }
            try {
                commentCountTextView.text =
                    data?.comment_count?.toLong()?.let { AppUtils.withSuffix(it) }
            } catch (e: Exception) {
                commentCountTextView.text = "" + data?.comment_count
            }
            try {
                recommendCountTextView.text =
                    data?.like_count?.toLong()?.let { AppUtils.withSuffix(it) }
            } catch (e: Exception) {
                recommendCountTextView.text = "" + data?.like_count
            }
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
                Picasso.get().load(data?.thumbnail?.let {
                    ImageKitUtils(
                        it,
                        0,
                        0
                    ).getVlogsCardImage()
                }).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(
                    articleImageView
                )
            } catch (e: Exception) {
                articleImageView.setImageResource(R.drawable.default_article)
            }
            showWinnerOrGoldMark(data, winnerVlogImageView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun addRecentLiveStreamItem(
        liveStreamImageView: ImageView,
        upcomingLiveTimeWidget: CardView,
        liveOngoingLabel: CardView,
        liveEndedLabel: MomspressoButtonWidget,
        liveStartsLabel: TextView,
        remainingTimeTextView: TextView,
        data: MixFeedResult?
    ) {
        try {
            try {
                Picasso.get().load(data?.recentLiveStreamsList?.get(0)?.image_url).placeholder(R.drawable.default_article).error(
                    R.drawable.default_article
                ).into(liveStreamImageView)
            } catch (e: Exception) {
                liveStreamImageView.setImageResource(R.drawable.default_article)
            }

            when (data?.recentLiveStreamsList?.get(0)?.status) {
                AppConstants.LIVE_STREAM_STATUS_UPCOMING -> {
                    upcomingLiveTimeWidget.visibility = View.VISIBLE
                    remainingTimeTextView.text =
                        DateTimeUtils.timeDiffInMinuteAndSeconds(data.recentLiveStreamsList?.get(0)?.live_datetime!!)
                    liveOngoingLabel.visibility = View.GONE
                    liveEndedLabel.visibility = View.GONE
                    liveStartsLabel.visibility = View.GONE
                }
                AppConstants.LIVE_STREAM_STATUS_ONGOING -> {
                    upcomingLiveTimeWidget.visibility = View.GONE
                    liveOngoingLabel.visibility = View.VISIBLE
                    liveEndedLabel.visibility = View.GONE
                    liveStartsLabel.visibility = View.VISIBLE
                    liveStartsLabel.text = liveStartsLabel.context.resources.getString(
                        R.string.live_started_ago,
                        DateTimeUtils.timeSince(data.recentLiveStreamsList?.get(0)?.live_datetime!!)
                    )
                }
                else -> {
                    upcomingLiveTimeWidget.visibility = View.GONE
                    liveOngoingLabel.visibility = View.GONE
                    liveEndedLabel.visibility = View.VISIBLE
                    liveEndedLabel.setText(
                        DateTimeUtils.timeSince(
                            data?.recentLiveStreamsList?.get(
                                0
                            )?.live_datetime!!
                        )
                    )
                    liveStartsLabel.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun addUpcomingLiveStreamItem(
        recyclerView: RecyclerView,
        data: MixFeedResult?
    ) {
        try {
            recyclerView.adapter = RecentOrUpcomingLiveStreamsHorizontalAdapter(
                data?.recentLiveStreamsList,
                horizontalListener
            )
            recyclerView.layoutManager = LinearLayoutManager(
                recyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.setHasFixedSize(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun loadTorcaiAdData(webView: WebView, mixFeedResult: MixFeedResult?) {
        webView.loadDataWithBaseURL("", mixFeedResult?.torcaiAdsData, "text/html", "utf-8", "")
    }

    private fun addCreatorSuggestionsMutualFriends(
        mixFeedPosition: Int,
        recyclerView: RecyclerView,
        data: MixFeedResult?
    ) {
        try {
            val adapter = SuggestedCreatorsRecyclerAdapter(
                mixFeedPosition,
                data?.suggestedCreatorList,
                suggestedCreatorListener
            )
            adapter.setHasMutualFriends(true)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                recyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.setHasFixedSize(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun addCreatorSuggestions(
        mixFeedPosition: Int,
        viewType: Int,
        recyclerView: RecyclerView,
        data: MixFeedResult?
    ) {
        try {
            if (viewType == CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS) {
                recyclerView.adapter = TopCreatorsRecyclerAdapter(
                    mixFeedPosition,
                    data?.topCreatorList,
                    topCreatorListener
                )
            } else {
                recyclerView.adapter = SuggestedCreatorsRecyclerAdapter(
                    mixFeedPosition,
                    data?.suggestedCreatorList,
                    suggestedCreatorListener
                )
            }
            recyclerView.layoutManager = LinearLayoutManager(
                recyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.setHasFixedSize(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun addTopicsSuggestions(
        flowLayout: FlowLayout,
        data: MixFeedResult?
    ) {
        flowLayout.removeAllViews()
        for (i in 0 until data?.suggestedTopicsList?.size!!) {
            val topicView = LayoutInflater.from(flowLayout.context)
                .inflate(R.layout.suggested_topic_custom_view, null, false) as FrameLayout
            topicView.isClickable = true
            (topicView.getChildAt(0) as RelativeLayout).getChildAt(0).tag =
                data.suggestedTopicsList?.get(i)?.categoryId
            (topicView.getChildAt(0) as RelativeLayout).getChildAt(2).tag =
                data.suggestedTopicsList?.get(i)?.categoryId
            ((topicView.getChildAt(0) as RelativeLayout).getChildAt(0) as TextView).text =
                data.suggestedTopicsList?.get(i)?.displayName?.toUpperCase()
            if (data.suggestedTopicsList?.get(i)?.isFollowing == "1") {
                ((topicView.getChildAt(0) as RelativeLayout).getChildAt(2) as ImageView)
                    .setImageDrawable(
                        ContextCompat
                            .getDrawable(BaseApplication.getAppContext(), R.drawable.ic_tick)
                    )
                (topicView.getChildAt(0) as RelativeLayout).getChildAt(2)
                    .setOnClickListener { v: View ->
                        data.suggestedTopicsList?.get(i)?.isFollowing = "0"
                        followUnfollowTopics(
                            v.tag as String,
                            v.parent as RelativeLayout, 0
                        )
                    }
            } else {
                ((topicView.getChildAt(0) as RelativeLayout).getChildAt(2) as ImageView)
                    .setImageDrawable(
                        ContextCompat
                            .getDrawable(BaseApplication.getAppContext(), R.drawable.ic_plus)
                    )
                (topicView.getChildAt(0) as RelativeLayout).getChildAt(2)
                    .setOnClickListener { v: View ->
                        data.suggestedTopicsList?.get(i)?.isFollowing = "1"
                        followUnfollowTopics(
                            v.tag as String,
                            v.parent as RelativeLayout, 1
                        )
                    }
            }

            (topicView.getChildAt(0) as RelativeLayout).getChildAt(0).setOnClickListener { v: View ->

            }
            flowLayout.addView(topicView)
        }
    }

    private fun showWinnerOrGoldMark(
        data: MixFeedResult?,
        winnerOrGoldImageView: ImageView
    ) {
        try {
            when {
                data?.winner == "1" || data?.winner == "true" -> {
                    winnerOrGoldImageView.visibility = View.VISIBLE
                    winnerOrGoldImageView.setImageResource(R.drawable.ic_trophy)
                }
                data?.is_gold == "1" || data?.is_gold == "true" -> {
                    winnerOrGoldImageView.visibility = View.VISIBLE
                    winnerOrGoldImageView.setImageResource(R.drawable.ic_star_yellow)
                }
                else -> {
                    winnerOrGoldImageView.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            winnerOrGoldImageView.visibility = View.GONE
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun followUnfollowTopics(
        selectedTopic: String,
        tagView: RelativeLayout,
        action: Int
    ) {
        val followUnfollowCategoriesRequest =
            FollowUnfollowCategoriesRequest()
        val topicIdLList = java.util.ArrayList<String>()
        topicIdLList.add(selectedTopic)
        followUnfollowCategoriesRequest.categories = topicIdLList
        if (action == 0) {
            tagView.getChildAt(0).tag = selectedTopic
            tagView.getChildAt(2).tag = selectedTopic
            (tagView.getChildAt(2) as ImageView).setImageDrawable(
                ContextCompat.getDrawable(
                    tagView.context,
                    R.drawable.ic_plus
                )
            )
            tagView.getChildAt(2).setOnClickListener { v: View ->
                //                data.suggestedTopicsList?.get(i)?.isFollowing = "1"
                followUnfollowTopics(
                    v.tag as String,
                    v.parent as RelativeLayout,
                    1
                )
            }
        } else {
            tagView.getChildAt(0).tag = selectedTopic
            tagView.getChildAt(2).tag = selectedTopic
            (tagView.getChildAt(2) as ImageView)
                .setImageDrawable(
                    ContextCompat.getDrawable(
                        tagView.context,
                        R.drawable.ic_tick
                    )
                )
            tagView.getChildAt(2).setOnClickListener { v: View ->
                //                data.suggestedTopicsList?.get(i)?.isFollowing = "0"
                followUnfollowTopics(
                    v.tag as String,
                    v.parent as RelativeLayout,
                    0
                )
            }
        }
        val retro = BaseApplication.getInstance().retrofit
        val topicsCategoryApi = retro.create(
            TopicsCategoryAPI::class.java
        )
        val call = topicsCategoryApi
            .followCategories(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                followUnfollowCategoriesRequest
            )
        call.enqueue(object : Callback<FollowUnfollowCategoriesResponse> {
            override fun onFailure(call: Call<FollowUnfollowCategoriesResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<FollowUnfollowCategoriesResponse>,
                response: Response<FollowUnfollowCategoriesResponse>
            ) {
            }
        })
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
        fun onFollowSuccess()
        fun onTorcaiAdClick(request: WebResourceRequest)
    }

    companion object {
        private val CONTENT_TYPE_CREATE = 0
        private val CONTENT_TYPE_SHORT_STORY = 1
        private val CONTENT_TYPE_ARTICLE = 2
        private val CONTENT_TYPE_VIDEO = 3
        private val CONTENT_TYPE_RECENT_LIVE_STREAM = 5
        private val CONTENT_TYPE_UPCOMING_LIVE_STREAM = 6
        private val CONTENT_TYPE_TORCAI_ADS = 7
        private val CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS = 8
        private val CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS = 9
        private val CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY = 10
        private val CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS = 11
        private val CONTENT_TYPE_SUGGESTED_TOPICS = 13
        private val CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS = 14
    }
}
