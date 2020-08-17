package com.mycity4kids.profile

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.ContributorListResponse
import com.mycity4kids.models.response.ContributorListResult
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ImageKitUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.widget.StoryShareCardWidget
import com.squareup.picasso.Picasso
import java.util.Locale
import kotlinx.android.synthetic.main.mom_vlog_follow_following_carousal.view.*
import kotlinx.android.synthetic.main.short_story_listing_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            AppConstants.CONTENT_TYPE_SUGGESTED_BLOGGERS == mixFeedList?.get(position)?.contentType -> CONTENT_TYPE_SUGGESTED_BLOGGERS
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
            CONTENT_TYPE_SUGGESTED_BLOGGERS -> {
                val v0 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.mom_vlog_follow_following_carousal, parent, false)
                FollowFollowingCarousel(v0, mListener)
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
                holder.goldVlogImageView,
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
            is FollowFollowingCarousel -> {
                try {
                    holder.scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                    if (!mixFeedList?.get(position)?.isCarouselRequestRunning!! && !mixFeedList?.get(
                            position
                        )?.responseReceived!!) {
                        holder.shimmerLayout.startShimmerAnimation()
                        holder.shimmerLayout.visibility = View.VISIBLE
                        mixFeedList?.get(position)?.isCarouselRequestRunning = true
                        val pos = position
                        val retrofit = BaseApplication.getInstance().retrofit
                        val contributorListApi =
                            retrofit.create(
                                ContributorListAPI::class.java
                            )
                        val call = contributorListApi
                            .getContributorList(
                                10,
                                2,
                                AppConstants.USER_TYPE_BLOGGER,
                                "" + AppUtils.getLangKey(),
                                ""
                            )
                        call.enqueue(object : Callback<ContributorListResponse> {
                            override fun onFailure(
                                call: Call<ContributorListResponse>,
                                t: Throwable
                            ) {
                            }

                            override fun onResponse(
                                call: Call<ContributorListResponse>,
                                response: Response<ContributorListResponse>
                            ) {
                                try {
                                    holder.shimmerLayout.stopShimmerAnimation()
                                    holder.shimmerLayout.visibility = View.GONE
                                    holder.scroll.visibility = View.VISIBLE
                                    if (response.isSuccessful && response.body() != null) {
                                        val bloggersList = response.body()?.data?.result
                                        processBloggersData(
                                            holder,
                                            bloggersList as ArrayList<ContributorListResult>,
                                            pos
                                        )
                                        mixFeedList?.get(pos)?.isCarouselRequestRunning =
                                            false
                                        mixFeedList?.get(pos)?.responseReceived = true
                                    } else {
                                        mixFeedList?.get(pos)?.isCarouselRequestRunning =
                                            false
                                        mixFeedList?.get(pos)?.responseReceived = true
                                    }
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                    Log.d(
                                        "MC4kException",
                                        Log.getStackTraceString(e)
                                    )
                                }
                            }
                        })
                    } else {
                        mixFeedList?.get(position)?.carouselBloggerList?.let {
                            populateCarouselFollowFollowing(
                                holder,
                                it
                            )
                        }
                    }

                    holder.carosalContainer1.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(0)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.carosalContainer2.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(1)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.carosalContainer3.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(2)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.carosalContainer4.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(3)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.carosalContainer5.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(4)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.carosalContainer6.setOnClickListener {
                        val intent1 = Intent(it.context, UserProfileActivity::class.java)
                        intent1.putExtra(
                            Constants.USER_ID,
                            mixFeedList?.get(position)?.carouselBloggerList?.get(5)?.id
                        )
                        it.context.startActivity(intent1)
                    }
                    holder.authorFollowTextView1.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(0)?.isFollowed == 1) {
                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(0)?.id,
                                position,
                                0,
                                holder.authorFollowTextView1
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(0)?.id,
                                position,
                                0,
                                holder.authorFollowTextView1
                            )
                        }
                    }
                    holder.authorFollowTextView2.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(1)?.isFollowed == 1) {

                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(1)?.id,
                                position,
                                1,
                                holder.authorFollowTextView2
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(1)?.id,
                                position,
                                1,
                                holder.authorFollowTextView2
                            )
                        }
                    }
                    holder.authorFollowTextView3.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(2)?.isFollowed == 1) {

                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(2)?.id,
                                position,
                                2,
                                holder.authorFollowTextView3
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(2)?.id,
                                position,
                                2,
                                holder.authorFollowTextView3
                            )
                        }
                    }
                    holder.authorFollowTextView4.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(3)?.isFollowed == 1) {

                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(3)?.id,
                                position,
                                3,
                                holder.authorFollowTextView4
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(3)?.id,
                                position,
                                3,
                                holder.authorFollowTextView4
                            )
                        }
                    }
                    holder.authorFollowTextView5.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(4)?.isFollowed == 1) {

                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(4)?.id,
                                position,
                                4,
                                holder.authorFollowTextView5
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(4)?.id,
                                position,
                                4,
                                holder.authorFollowTextView5
                            )
                        }
                    }
                    holder.authorFollowTextView6.setOnClickListener {
                        if (mixFeedList?.get(position)?.carouselBloggerList?.get(5)?.isFollowed == 1) {

                            unFollowApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(5)?.id,
                                position,
                                5,
                                holder.authorFollowTextView6
                            )
                        } else {
                            followApiCall(
                                mixFeedList?.get(position)?.carouselBloggerList?.get(5)?.id,
                                position,
                                5,
                                holder.authorFollowTextView6
                            )
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
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
        internal var winnerVlogImageView: ImageView
        internal var txvArticleTitle: TextView
        internal var txvAuthorName: TextView
        internal var articleImageView: ImageView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var goldVlogImageView: ImageView

        init {
            winnerVlogImageView = itemView.findViewById(R.id.winnerVlogImageView)
            goldVlogImageView = itemView.findViewById(R.id.goldVlogImageView)
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
            //            goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onClick(v, adapterPosition)
            }
        }
    }

    inner class FollowFollowingCarousel(view: View, val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(view) {

        val headerTextView: TextView = view.headerTextView
        val shimmerLayout: ShimmerFrameLayout = view.shimmerLayout
        val carosalContainer1: LinearLayout = view.carosalContainer1
        val carosalContainer2: LinearLayout = view.carosalContainer2
        val carosalContainer3: LinearLayout = view.carosalContainer3
        val carosalContainer4: LinearLayout = view.carosalContainer4
        val carosalContainer5: LinearLayout = view.carosalContainer5
        val carosalContainer6: LinearLayout = view.carosalContainer6

        val scroll: HorizontalScrollView = view.scroll
        val authorImageView1: ImageView = view.authorImageView1
        val authorNameTextView1: TextView = view.authorNameTextView1
        val authorRankTextView1: TextView = view.authorRankTextView1
        val authorFollowTextView1: TextView = view.authorFollowTextView1

        val authorImageView2: ImageView = view.authorImageView2
        val authorNameTextView2: TextView = view.authorNameTextView2
        val authorRankTextView2: TextView = view.authorRankTextView2
        val authorFollowTextView2: TextView = view.authorFollowTextView2

        val authorImageView3: ImageView = view.authorImageView3
        val authorNameTextView3: TextView = view.authorNameTextView3
        val authorRankTextView3: TextView = view.authorRankTextView3
        val authorFollowTextView3: TextView = view.authorFollowTextView3

        val authorImageView4: ImageView = view.authorImageView4
        val authorNameTextView4: TextView = view.authorNameTextView4
        val authorRankTextView4: TextView = view.authorRankTextView4
        val authorFollowTextView4: TextView = view.authorFollowTextView4

        val authorImageView5: ImageView = view.authorImageView5
        val authorNameTextView5: TextView = view.authorNameTextView5
        val authorRankTextView5: TextView = view.authorRankTextView5
        val authorFollowTextView5: TextView = view.authorFollowTextView5

        val authorImageView6: ImageView = view.authorImageView6
        val authorNameTextView6: TextView = view.authorNameTextView6
        val authorRankTextView6: TextView = view.authorRankTextView6
        val authorFollowTextView6: TextView = view.authorFollowTextView6

        init {
            headerTextView.text = "Follow Top Ranking Authors"
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
            when {
                data?.winner == 1 -> {
                    trophyImageView.visibility = View.VISIBLE
                    trophyImageView.setImageResource(R.drawable.ic_trophy)
                }
                data?.is_gold!! -> {
                    trophyImageView.visibility = View.VISIBLE
                    trophyImageView.setImageResource(R.drawable.ic_star_yellow)
                }
                else -> {
                    trophyImageView.visibility = View.GONE
                }
            }
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
            when {
                data?.winner == 1 -> {
                    trophyImageView.visibility = View.VISIBLE
                    trophyImageView.setImageResource(R.drawable.ic_trophy)
                }
                data?.is_gold!! -> {
                    trophyImageView.visibility = View.VISIBLE
                    trophyImageView.setImageResource(R.drawable.ic_star_yellow)
                }
                else -> {
                    trophyImageView.visibility = View.GONE
                }
            }
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
        goldVlogImageView: ImageView,
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

            if (data?.is_gold != null && data.is_gold) {
                goldVlogImageView.visibility = View.VISIBLE
            } else {
                goldVlogImageView.visibility = View.GONE
            }
            if (data?.winner != null && data.winner as Boolean) {
                winnerVlogImageView.visibility = View.VISIBLE
            } else {
                winnerVlogImageView.visibility = View.GONE
            }
        } catch (e: Exception) {
            winnerVlogImageView.visibility = View.GONE
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun setTorcaiAdSlotData(showAds: Boolean, adSlotHtml: String) {
        //        this.showAds = showAds
        //        this.htmlContent = adSlotHtml
    }

    fun setCampaignOrAdSlotData(
        dataType: String,
        campaignList: ArrayList<CampaignDataListResult>,
        adSlotHtml: String
    ) {
        //        this.dataType = dataType
        //        campaignListDataModels = campaignList
        //        this.htmlContent = adSlotHtml
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }

    private fun processBloggersData(
        holder: FollowFollowingCarousel,
        bloggersList: ArrayList<ContributorListResult>,
        position: Int
    ) {
        try {
            if (bloggersList.isNotEmpty()) {
                mixFeedList?.get(position)?.carouselBloggerList = bloggersList
                mixFeedList?.get(position)?.carouselBloggerList?.let {
                    populateCarouselFollowFollowing(
                        holder,
                        it
                    )
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun populateCarouselFollowFollowing(
        holder: FollowFollowingCarousel,
        carouselList: ArrayList<ContributorListResult>
    ) {
        if (carouselList.isEmpty()) {
            holder.scroll.visibility = View.GONE
            return
        } else {
            holder.scroll.visibility = View.VISIBLE
        }
        if (carouselList.size >= 1) {
            holder.carosalContainer1.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView1,
                holder.authorImageView1,
                holder.authorNameTextView1,
                holder.authorRankTextView1,
                carouselList[0]
            )
        }
        if (carouselList.size >= 2) {
            holder.carosalContainer2.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView2,
                holder.authorImageView2,
                holder.authorNameTextView2,
                holder.authorRankTextView2,
                carouselList[1]
            )
        }
        if (carouselList.size >= 3) {
            holder.carosalContainer3.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView3,
                holder.authorImageView3,
                holder.authorNameTextView3,
                holder.authorRankTextView3,
                carouselList[2]
            )
        }
        if (carouselList.size >= 4) {
            holder.carosalContainer4.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView4,
                holder.authorImageView4,
                holder.authorNameTextView4,
                holder.authorRankTextView4,
                carouselList[3]
            )
        }
        if (carouselList.size >= 5) {
            holder.carosalContainer5.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView5,
                holder.authorImageView5,
                holder.authorNameTextView5,
                holder.authorRankTextView5,
                carouselList[4]
            )
        }
        if (carouselList.size >= 6) {
            holder.carosalContainer6.visibility = View.VISIBLE
            updateCarousel(
                holder.authorFollowTextView6,
                holder.authorImageView6,
                holder.authorNameTextView6,
                holder.authorRankTextView6,
                carouselList[5]
            )
        }
        holder.scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT)
    }

    private fun updateCarousel(
        followTextView: TextView,
        authorImageView: ImageView,
        authorNameTextView: TextView,
        authorRanktextView: TextView,
        contributorItem: ContributorListResult
    ) {
        Picasso.get().load(contributorItem.profilePic.clientApp).error(R.drawable.default_article)
            .into(authorImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                }
            })
        if (contributorItem.isFollowed == 1) {
            updateTextViewForFollowUnfollow(followTextView, 1)
        } else {
            updateTextViewForFollowUnfollow(followTextView, 0)
        }
        authorNameTextView.text = contributorItem.firstName.trim().toLowerCase().capitalize()
            .plus(" " + contributorItem.lastName.trim().toLowerCase().capitalize())

        authorRanktextView.text =
            authorRanktextView.context.resources.getString(R.string.myprofile_rank_label).toLowerCase().capitalize() +
                ":" + contributorItem.rank
    }

    private fun updateTextViewForFollowUnfollow(followTextView: TextView, status: Int) {
        if (status == 1) {
            followTextView.setTextColor(
                ContextCompat.getColor(
                    followTextView.context,
                    R.color.color_BABABA
                )
            )
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(
                2,
                ContextCompat.getColor(followTextView.context, R.color.color_BABABA)
            )
            myGrad.setColor(ContextCompat.getColor(followTextView.context, R.color.white))
            followTextView.text =
                followTextView.context.getString(R.string.ad_following_author).toLowerCase().capitalize()
        } else {
            followTextView.setTextColor(
                ContextCompat.getColor(
                    followTextView.context,
                    R.color.white
                )
            )
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(2, ContextCompat.getColor(followTextView.context, R.color.app_red))
            myGrad.setColor(ContextCompat.getColor(followTextView.context, R.color.app_red))
            followTextView.text =
                followTextView.context.getString(R.string.ad_follow_author).toLowerCase().capitalize()
        }
    }

    private fun unFollowApiCall(
        authorId: String?,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        mixFeedList?.get(position)?.carouselBloggerList?.get(index)?.isFollowed = 0
        updateTextViewForFollowUnfollow(followFollowingTextView, 0)
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.unfollowUserV2(request)
        followUnfollowUserResponseCall.enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    private fun followApiCall(
        authorId: String?,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        Utils.shareEventTracking(
            followFollowingTextView.context,
            "Main Follow Feed",
            "Follow_Android",
            "MainFollowFeed_C_Follow"
        )
        mixFeedList?.get(position)?.carouselBloggerList?.get(index)?.isFollowed = 1
        updateTextViewForFollowUnfollow(followFollowingTextView, 1)
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.followUserV2(request)
        followUnfollowUserResponseCall.enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    companion object {
        private val CONTENT_TYPE_CREATE = 0
        private val CONTENT_TYPE_SHORT_STORY = 1
        private val CONTENT_TYPE_ARTICLE = 2
        private val CONTENT_TYPE_VIDEO = 3
        private val CONTENT_TYPE_SUGGESTED_BLOGGERS = 4
    }
}
