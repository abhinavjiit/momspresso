package com.mycity4kids.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso


class StickyRecyclerViewAdapter(private var mListener: RecyclerViewClickListener) : StickHeaderRecyclerView<ArticleListingResult, HeaderDataImpl>() {

    private val CONTENT_TYPE_SHORT_STORY = 3
    private val CONTENT_TYPE_ARTICLE = 4

    override fun getViewType(pos: Int): Int {
        return if ("1" == getDataInPosition(pos).contentType) {
            CONTENT_TYPE_SHORT_STORY
        } else {
            CONTENT_TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HeaderDataImpl.HEADER_TYPE_1 -> return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header1_item_recycler, parent, false))
            HeaderDataImpl.HEADER_TYPE_2 -> return Header2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header2_item_recycler, parent, false))
            else ->
                return if (viewType == CONTENT_TYPE_ARTICLE) {
                    val viewHolder: UserRecommendationsViewHolder
                    val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_recommendation_recycle_item, parent, false)
                    viewHolder = UserRecommendationsViewHolder(v0, mListener)
                    viewHolder
                } else {
                    val viewHolder: UserRecommendedSSViewHolder
                    val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_activity_short_stories_item, parent, false)
                    viewHolder = UserRecommendedSSViewHolder(v0, mListener)
                    viewHolder
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
        var tvHeader: TextView

        init {
            tvHeader = itemView.findViewById(R.id.usernameTextView)
        }

        fun bindData(position: Int) {
            tvHeader.text = "dwadawd  dwa daw d wad wad wa da wd awd wad ad wad " + (position / 5).toString()
        }
    }

    internal inner class Header2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var tvHeader: TextView

        init {
//            tvHeader = itemView.findViewById(R.id.title_header)
        }

        fun bindData(position: Int) {
//            tvHeader.text = "dwa fr rg " + (position / 5).toString()
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
            txvArticleTitle.setText(getDataInPosition(position).getTitle())

            if (StringUtils.isNullOrEmpty(getDataInPosition(position).getArticleCount()) || "0" == getDataInPosition(position).getArticleCount()) {
                viewCountTextView.setVisibility(View.GONE)
            } else {
                viewCountTextView.setVisibility(View.VISIBLE)
                viewCountTextView.setText(getDataInPosition(position).getArticleCount())
            }

            commentCountTextView.setText(getDataInPosition(position).getCommentCount())
            if (StringUtils.isNullOrEmpty(getDataInPosition(position).getCommentCount()) || "0" == getDataInPosition(position).getCommentCount()) {
                commentCountTextView.setVisibility(View.GONE)
                separatorView1.setVisibility(View.GONE)
            } else {
                commentCountTextView.setVisibility(View.VISIBLE)
                separatorView1.setVisibility(View.VISIBLE)
            }

            recommendCountTextView.setText(getDataInPosition(position).getLikesCount())
            if (StringUtils.isNullOrEmpty(getDataInPosition(position).getLikesCount()) || "0" == getDataInPosition(position).getLikesCount()) {
                recommendCountTextView.setVisibility(View.GONE)
                separatorView2.setVisibility(View.GONE)
            } else {
                recommendCountTextView.setVisibility(View.VISIBLE)
                separatorView2.setVisibility(View.VISIBLE)
            }

            try {
                if (!StringUtils.isNullOrEmpty(getDataInPosition(position).getVideoUrl()) && (getDataInPosition(position).getImageUrl().getThumbMax() == null || getDataInPosition(position).getImageUrl().getThumbMax().endsWith("default.jpg"))) {
                    Picasso.with(itemView.context).load(AppUtils.getYoutubeThumbnailURLMomspresso(getDataInPosition(position).getVideoUrl())).placeholder(R.drawable.default_article).into(articleImageView)
                } else {
                    if (!StringUtils.isNullOrEmpty(getDataInPosition(position).getImageUrl().getThumbMax())) {
                        Picasso.with(itemView.context).load(getDataInPosition(position).getImageUrl().getThumbMax())
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
            txvArticleTitle.setText(getDataInPosition(position).getTitle())

            commentCountTextView.setText(getDataInPosition(position).getCommentCount())
            if (StringUtils.isNullOrEmpty(getDataInPosition(position).getCommentCount()) || "0" == getDataInPosition(position).getCommentCount()) {
                commentCountTextView.setVisibility(View.GONE)
            } else {
                commentCountTextView.setVisibility(View.VISIBLE)
            }
            recommendCountTextView.setText(getDataInPosition(position).getLikesCount())
            if (StringUtils.isNullOrEmpty(getDataInPosition(position).getLikesCount()) || "0" == getDataInPosition(position).getLikesCount()) {
                recommendCountTextView.setVisibility(View.GONE)
                separatorView2.setVisibility(View.GONE)
            } else {
                recommendCountTextView.setVisibility(View.VISIBLE)
                separatorView2.setVisibility(View.VISIBLE)
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