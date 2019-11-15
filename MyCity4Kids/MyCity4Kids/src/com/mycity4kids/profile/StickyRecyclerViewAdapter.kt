package com.mycity4kids.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.animation.MyCityAnimationsUtil
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.models.response.LanguageRanksModel
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import java.util.*


class StickyRecyclerViewAdapter(private var mListener: RecyclerViewClickListener) : StickHeaderRecyclerView<StickyMainData, HeaderDataImpl>() {

    private val CONTENT_TYPE_HEADER = 3
    private val CONTENT_TYPE_SHORT_STORY = 4
    private val CONTENT_TYPE_ARTICLE = 5

    override fun getViewType(pos: Int): Int {
        return when {
            pos == 1 -> CONTENT_TYPE_HEADER
            "1" == (getDataInPosition(pos) as ArticleListingResult).contentType -> CONTENT_TYPE_SHORT_STORY
            else -> CONTENT_TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HeaderDataImpl.HEADER_TYPE_1 -> return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_view, parent, false))
            HeaderDataImpl.HEADER_TYPE_2 -> return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header2_item_recycler, parent, false))
            else ->
                return when (viewType) {
                    CONTENT_TYPE_HEADER -> {
                        val viewHolder: HeaderViewHolder
                        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.header1_item_recycler, parent, false)
                        viewHolder = HeaderViewHolder(v0)
                        viewHolder
                    }
                    CONTENT_TYPE_ARTICLE -> {
                        val viewHolder: UserRecommendationsViewHolder
                        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_recommendation_recycle_item, parent, false)
                        viewHolder = UserRecommendationsViewHolder(v0, mListener)
                        viewHolder
                    }
                    else -> {
                        val viewHolder: UserRecommendedSSViewHolder
                        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_activity_short_stories_item, parent, false)
                        viewHolder = UserRecommendedSSViewHolder(v0, mListener)
                        viewHolder
                    }
                }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserRecommendationsViewHolder -> holder.bindData(position)
            is UserRecommendedSSViewHolder -> holder.bindData(position)
            is HeaderViewHolder -> holder.bindData(position)
            is Header2ViewHolder -> holder.bindData(position)
        }
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
    }

    internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var profileImageView: ImageView
        internal var usernameTextView: TextView
        internal var cityTextView: TextView
        internal var badgesContainer: LinearLayout
        internal var userBioTextView: TextView
        internal var contentLangTextView: TextView
        internal var rankCountTextView: TextView
        internal var rankLanguageTextView: TextView
        internal var rankContainer: TextView
        internal var followerCountTextView: TextView
        internal var followingCountTextView: TextView
        val multipleRankList = ArrayList<LanguageRanksModel>()

        init {
            profileImageView = itemView.findViewById(R.id.profileImageView)
            usernameTextView = itemView.findViewById(R.id.usernameTextView)
            cityTextView = itemView.findViewById(R.id.cityTextView)
            badgesContainer = itemView.findViewById(R.id.badgesContainer)
            userBioTextView = itemView.findViewById(R.id.userBioTextView)
            contentLangTextView = itemView.findViewById(R.id.contentLangTextView)
            rankCountTextView = itemView.findViewById(R.id.rankCountTextView)
            rankLanguageTextView = itemView.findViewById(R.id.rankLanguageTextView)
            rankContainer = itemView.findViewById(R.id.rankContainer)
            followerCountTextView = itemView.findViewById(R.id.followerCountTextView)
            followingCountTextView = itemView.findViewById(R.id.followingCountTextView)

        }

        fun bindData(position: Int) {
            val userData = (getDataInPosition(position) as UserDetailResponse)
            Picasso.with(itemView.context).load(userData.data[0].result.profilePicUrl.clientApp).placeholder(R.drawable.default_article).into(profileImageView)

            if (userData.getData().get(0).getResult().getRanks() == null || userData.getData().get(0).getResult().getRanks().size == 0) {
                rankCountTextView.setText("--")
                rankLanguageTextView.setText(itemView.context.getString(R.string.myprofile_rank_label))
            } else if (userData.getData().get(0).getResult().getRanks().size < 2) {
                rankCountTextView.setText("" + userData.getData().get(0).getResult().getRanks().get(0).getRank())
                if (AppConstants.LANG_KEY_ENGLISH == userData.getData().get(0).getResult().getRanks().get(0).getLangKey()) {
                    rankLanguageTextView.setText(itemView.context.getString(R.string.blogger_profile_rank_in) + " ENGLISH")
                } else {
                    rankLanguageTextView.setText(itemView.context.getString(R.string.blogger_profile_rank_in)
                            + " " + userData.getData().get(0).getResult().getRanks().get(0).getLangValue().toUpperCase())
                }
            } else {
                for (i in 0 until userData.getData().get(0).getResult().getRanks().size) {
                    if (AppConstants.LANG_KEY_ENGLISH == userData.getData().get(0).getResult().getRanks().get(i).getLangKey()) {
                        multipleRankList.add(userData.getData().get(0).getResult().getRanks().get(i))
                        break
                    }
                }
                Collections.sort<LanguageRanksModel>(userData.getData().get(0).getResult().getRanks())
                for (i in 0 until userData.getData().get(0).getResult().getRanks().size) {
                    if (AppConstants.LANG_KEY_ENGLISH != userData.getData().get(0).getResult().getRanks().get(i).getLangKey()) {
                        multipleRankList.add(userData.getData().get(0).getResult().getRanks().get(i))
                    }
                }
                MyCityAnimationsUtil.animate(itemView.context, rankContainer, multipleRankList, 0, true)
            }

            val followerCount = Integer.parseInt(userData.getData().get(0).getResult().getFollowersCount())
            if (followerCount > 999) {
                val singleFollowerCount = followerCount.toFloat() / 1000
                followerCountTextView.setText("" + singleFollowerCount + "k")
            } else {
                followerCountTextView.setText("" + followerCount)
            }

            val followingCount = Integer.parseInt(userData.getData().get(0).getResult().getFollowingCount())
            if (followingCount > 999) {
                val singleFollowingCount = followingCount.toFloat() / 1000
                followingCountTextView.setText("" + singleFollowingCount + "k")
            } else {
                followingCountTextView.setText("" + followingCount)
            }
            usernameTextView.setText(userData.getData().get(0).getResult().getFirstName() + " " + userData.getData().get(0).getResult().getLastName())

        }
    }

    internal inner class Header2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
        }

        fun bindData(position: Int) {
        }
    }

    inner class UserRecommendationsViewHolder internal constructor(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var articleImageView: ImageView
        internal var shareImageView: ImageView
        internal var txvArticleTitle: TextView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var separatorView1: View
        internal var separatorView2: View

        init {
            txvArticleTitle = itemView.findViewById(R.id.articleTitleTextView)
            articleImageView = itemView.findViewById(R.id.articleImageView)
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView)
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView)
            recommendCountTextView = itemView.findViewById(R.id.recommendCountTextView)
            shareImageView = itemView.findViewById(R.id.shareImageView)
            separatorView1 = itemView.findViewById(R.id.separatorView1)
            separatorView2 = itemView.findViewById(R.id.separatorView2)
            shareImageView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bindData(position: Int) {
            val articleData = (getDataInPosition(position) as ArticleListingResult)
            txvArticleTitle.text = articleData.title

            if (StringUtils.isNullOrEmpty(articleData.articleCount) || "0" == articleData.articleCount) {
                viewCountTextView.visibility = View.GONE
            } else {
                viewCountTextView.visibility = View.VISIBLE
                viewCountTextView.text = articleData.articleCount
            }

            commentCountTextView.text = articleData.commentCount
            if (StringUtils.isNullOrEmpty(articleData.commentCount) || "0" == articleData.commentCount) {
                commentCountTextView.visibility = View.GONE
                separatorView1.visibility = View.GONE
            } else {
                commentCountTextView.visibility = View.VISIBLE
                separatorView1.visibility = View.VISIBLE
            }

            recommendCountTextView.setText(articleData.likesCount)
            if (StringUtils.isNullOrEmpty(articleData.likesCount) || "0" == articleData.likesCount) {
                recommendCountTextView.visibility = View.GONE
                separatorView2.visibility = View.GONE
            } else {
                recommendCountTextView.visibility = View.VISIBLE
                separatorView2.visibility = View.VISIBLE
            }

            try {
                if (!StringUtils.isNullOrEmpty(articleData.getVideoUrl()) && (articleData.getImageUrl().getThumbMax() == null || articleData.getImageUrl().getThumbMax().endsWith("default.jpg"))) {
                    Picasso.with(itemView.context).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleData.getVideoUrl())).placeholder(R.drawable.default_article).into(articleImageView)
                } else {
                    if (!StringUtils.isNullOrEmpty(articleData.getImageUrl().getThumbMax())) {
                        Picasso.with(itemView.context).load(articleData.getImageUrl().getThumbMax())
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(articleImageView)
                    } else {
                        articleImageView.setBackgroundResource(R.drawable.default_article)
                    }
                }
            } catch (e: Exception) {
                articleImageView.setBackgroundResource(R.drawable.default_article)
            }

        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    inner class UserRecommendedSSViewHolder internal constructor(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var txvArticleTitle: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var shareArticleImageView: ImageView
        internal var separatorView2: View

        init {
            txvArticleTitle = itemView.findViewById(R.id.articleTitleTextView)
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView)
            recommendCountTextView = itemView.findViewById(R.id.recommendCountTextView)
            shareArticleImageView = itemView.findViewById(R.id.shareImageView)
            separatorView2 = itemView.findViewById(R.id.separatorView2)
            shareArticleImageView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bindData(position: Int) {
            val storyData = (getDataInPosition(position) as ArticleListingResult)
            txvArticleTitle.text = storyData.title

            commentCountTextView.text = storyData.commentCount
            if (StringUtils.isNullOrEmpty(storyData.commentCount) || "0" == storyData.commentCount) {
                commentCountTextView.visibility = View.GONE
            } else {
                commentCountTextView.visibility = View.VISIBLE
            }
            recommendCountTextView.text = storyData.likesCount
            if (StringUtils.isNullOrEmpty(storyData.likesCount) || "0" == storyData.likesCount) {
                recommendCountTextView.visibility = View.GONE
                separatorView2.visibility = View.GONE
            } else {
                recommendCountTextView.visibility = View.VISIBLE
                separatorView2.visibility = View.VISIBLE
            }
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}