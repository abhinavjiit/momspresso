package com.mycity4kids.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.models.response.MixFeedResult
import com.squareup.picasso.Picasso

/**
 * Created by hemant on 19/7/17.
 */
class UsersBookmarksAdapter(private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mixFeedResult: ArrayList<MixFeedResult>? = null

    fun setListData(mixFeedResult: ArrayList<MixFeedResult>?) {
        this.mixFeedResult = mixFeedResult
    }

    override fun getItemViewType(position: Int): Int {
        return if ("2" == mixFeedResult?.get(position)?.contentType) {
            CONTENT_TYPE_VIDEO
        } else {
            CONTENT_TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CONTENT_TYPE_ARTICLE) {
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_bookmark_generic_item, parent, false)
            return UserArticleBookmarksViewHolder(v0, mListener)
        } else {
            val v0 = LayoutInflater.from(parent.context).inflate(R.layout.users_bookmark_generic_item, parent, false)
            return UserVideoBookmarksViewHolder(v0, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserArticleBookmarksViewHolder) {
            holder.contentTitleTextView.text = mixFeedResult?.get(position)?.title
            holder.authorTextView.text = holder.itemView.context.getString(R.string.user_activities_bookmarks_by) +
                    " " + mixFeedResult?.get(position)?.userName

            if (0 == mixFeedResult?.get(position)?.articleCount) {
                holder.viewCountTextView.visibility = View.GONE
            } else {
                holder.viewCountTextView.visibility = View.VISIBLE
                holder.viewCountTextView.text = "" + mixFeedResult?.get(position)?.articleCount
            }

            if (0 == mixFeedResult?.get(position)?.commentsCount) {
                holder.commentCountTextView.visibility = View.GONE
            } else {
                holder.commentCountTextView.visibility = View.VISIBLE
                holder.commentCountTextView.text = "" + mixFeedResult?.get(position)?.commentsCount
            }

            if (0 == mixFeedResult?.get(position)?.likesCount) {
                holder.recommendCountTextView.visibility = View.GONE
            } else {
                holder.recommendCountTextView.visibility = View.VISIBLE
                holder.recommendCountTextView.text = "" + mixFeedResult?.get(position)?.likesCount
            }
            try {
                if (!StringUtils.isNullOrEmpty(mixFeedResult?.get(position)?.imageUrl?.clientApp) &&
                        (mixFeedResult?.get(position)?.imageUrl?.thumbMax == null ||
                                mixFeedResult?.get(position)?.imageUrl?.thumbMax?.endsWith("default.jpg")!!)) {
                    Picasso.get().load(mixFeedResult?.get(position)?.imageUrl?.clientApp).placeholder(
                            R.drawable.default_article).into(holder.contentImageView)
                } else {
                    if (!StringUtils.isNullOrEmpty(mixFeedResult?.get(position)?.imageUrl?.thumbMax)) {
                        Picasso.get().load(mixFeedResult?.get(position)?.imageUrl?.thumbMax).placeholder(
                                R.drawable.default_article).error(R.drawable.default_article).into(holder.contentImageView)
                    } else {
                        holder.contentImageView.setBackgroundResource(R.drawable.default_article)
                    }
                }
            } catch (e: Exception) {
                holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            }
        } else if (holder is UserVideoBookmarksViewHolder) {
            holder.contentTitleTextView.text = mixFeedResult?.get(position)?.title
            holder.authorTextView.text = "" // holder.itemView.context.getString(R.string.user_activities_bookmarks_by) +
            // " " + mixFeedResult?.get(position)?.userName

            if (0 == mixFeedResult?.get(position)?.view_count) {
                holder.viewCountTextView.visibility = View.GONE
            } else {
                holder.viewCountTextView.visibility = View.VISIBLE
                holder.viewCountTextView.text = "" + mixFeedResult?.get(position)?.view_count
            }

            if (0 == mixFeedResult?.get(position)?.comment_count) {
                holder.commentCountTextView.visibility = View.GONE
            } else {
                holder.commentCountTextView.visibility = View.VISIBLE
                holder.commentCountTextView.text = "" + mixFeedResult?.get(position)?.comment_count
            }

            if (0 == mixFeedResult?.get(position)?.like_count) {
                holder.recommendCountTextView.visibility = View.GONE
            } else {
                holder.recommendCountTextView.visibility = View.VISIBLE
                holder.recommendCountTextView.text = "" + mixFeedResult?.get(position)?.like_count
            }
            try {
                if (!StringUtils.isNullOrEmpty(mixFeedResult?.get(position)?.thumbnail)) {
                    Picasso.get().load(mixFeedResult?.get(position)?.thumbnail).placeholder(
                            R.drawable.default_article).into(holder.contentImageView)
                } else {
                    holder.contentImageView.setBackgroundResource(R.drawable.default_article)
                }
            } catch (e: Exception) {
                holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mixFeedResult == null) 0 else mixFeedResult!!.size
    }

    inner class UserArticleBookmarksViewHolder(itemView: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorTextView: TextView
        internal var contentImageView: ImageView
        internal var itemTypeImageView: ImageView
        internal var contentTitleTextView: TextView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var removeBookmarkTextView: TextView
        internal var shareImageView: ImageView

        init {
            contentTitleTextView = itemView.findViewById<View>(R.id.contentTitleTextView) as TextView
            contentImageView = itemView.findViewById<View>(R.id.contentImageView) as ImageView
            itemTypeImageView = itemView.findViewById<View>(R.id.itemTypeImageView) as ImageView
            authorTextView = itemView.findViewById<View>(R.id.authorTextView) as TextView
            viewCountTextView = itemView.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView = itemView.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView = itemView.findViewById<View>(R.id.recommendCountTextView) as TextView
            removeBookmarkTextView = itemView.findViewById<View>(R.id.removeBookmarkTextView) as TextView
            shareImageView = itemView.findViewById<View>(R.id.shareImageView) as ImageView
            removeBookmarkTextView.visibility = View.GONE
            itemTypeImageView.setImageResource(R.drawable.draft_red)
            shareImageView.setOnClickListener(this)
            removeBookmarkTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onBookmarkItemInteraction(v, adapterPosition)
        }
    }

    inner class UserVideoBookmarksViewHolder(itemView: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorTextView: TextView
        internal var contentImageView: ImageView
        internal var itemTypeImageView: ImageView
        internal var contentTitleTextView: TextView
        internal var viewCountTextView: TextView
        internal var commentCountTextView: TextView
        internal var recommendCountTextView: TextView
        internal var removeBookmarkTextView: TextView
        internal var shareImageView: ImageView

        init {
            contentTitleTextView = itemView.findViewById<View>(R.id.contentTitleTextView) as TextView
            contentImageView = itemView.findViewById<View>(R.id.contentImageView) as ImageView
            itemTypeImageView = itemView.findViewById<View>(R.id.itemTypeImageView) as ImageView
            authorTextView = itemView.findViewById<View>(R.id.authorTextView) as TextView
            viewCountTextView = itemView.findViewById<View>(R.id.viewCountTextView) as TextView
            commentCountTextView = itemView.findViewById<View>(R.id.commentCountTextView) as TextView
            recommendCountTextView = itemView.findViewById<View>(R.id.recommendCountTextView) as TextView
            removeBookmarkTextView = itemView.findViewById<View>(R.id.removeBookmarkTextView) as TextView
            shareImageView = itemView.findViewById<View>(R.id.shareImageView) as ImageView
            itemTypeImageView.setImageResource(R.drawable.ic_video)
            removeBookmarkTextView.visibility = View.GONE
            shareImageView.setOnClickListener(this)
            removeBookmarkTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onBookmarkItemInteraction(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onBookmarkItemInteraction(view: View, position: Int)
    }

    companion object {
        private val CONTENT_TYPE_ARTICLE = 0
        private val CONTENT_TYPE_VIDEO = 1
    }
}
