package com.mycity4kids.ui.momspressotv

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.StringUtils
import com.squareup.picasso.Picasso
import java.util.ArrayList

class MomspressoLibraryAdapter(
    private val recyclerViewClickListener: RecyclerViewClickListener,
    private val libraryListing: ArrayList<ArticleListingResult>
) : RecyclerView.Adapter<MomspressoLibraryAdapter.MtvFeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtvFeedViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(
            R.layout.article_listing_item,
            parent,
            false
        )
        return MtvFeedViewHolder(v0, recyclerViewClickListener)
    }

    override fun getItemCount(): Int {
        return libraryListing.size
    }

    override fun onBindViewHolder(holder: MtvFeedViewHolder, position: Int) {
        if (libraryListing[position].eventId != null && libraryListing[position].eventId.isNotBlank()) {
            holder.txvArticleTitle.visibility = View.GONE
        } else {
            holder.txvArticleTitle.visibility = View.VISIBLE
            holder.txvArticleTitle.text = libraryListing[position].title
        }

        if (null == libraryListing[position].articleCount || "0" == libraryListing[position].articleCount) {
            holder.viewCountTextView.visibility = View.GONE
        } else {
            holder.viewCountTextView.visibility = View.VISIBLE
            try {
                holder.viewCountTextView.text =
                    AppUtils.withSuffix(libraryListing[position].articleCount.toLong())
            } catch (e: Exception) {
                holder.viewCountTextView.text = libraryListing[position].articleCount
            }
        }
        if (null == libraryListing[position].commentsCount || "0" == libraryListing[position].commentsCount) {
            holder.commentCountTextView.visibility = View.GONE
        } else {
            holder.commentCountTextView.visibility = View.VISIBLE
            try {
                holder.commentCountTextView.text =
                    AppUtils.withSuffix(libraryListing[position].commentsCount.toLong())
            } catch (e: Exception) {
                holder.commentCountTextView.text = libraryListing[position].commentsCount
            }
        }
        if (null == libraryListing[position].likesCount || "0" == libraryListing[position].likesCount) {
            holder.recommendCountTextView.visibility = View.GONE
        } else {
            holder.recommendCountTextView.visibility = View.VISIBLE
            try {
                holder.recommendCountTextView.text =
                    AppUtils.withSuffix(libraryListing[position].likesCount.toLong())
            } catch (e: Exception) {
                holder.recommendCountTextView.text = libraryListing[position].likesCount
            }
        }
        if (StringUtils.isNullOrEmpty(libraryListing[position].userName) || libraryListing[position].userName.trim { it <= ' ' }.equals(
                "",
                ignoreCase = true
            )) {
            holder.txvAuthorName.setText("NA")
        } else {
            holder.txvAuthorName.setText(libraryListing[position].userName)
        }
        try {
            if (!StringUtils.isNullOrEmpty(libraryListing[position].getVideoUrl()) &&
                (libraryListing[position].imageUrl.thumbMax == null || libraryListing[position].imageUrl.thumbMax
                    .contains("default.jp"))) {
                Picasso.get().load(AppUtils.getYoutubeThumbnailURLMomspresso(libraryListing[position].videoUrl))
                    .placeholder(R.drawable.default_article).into(holder.articleImageView)
            } else {
                if (!StringUtils.isNullOrEmpty(libraryListing[position].imageUrl.thumbMax)) {
                    Picasso.get().load(libraryListing[position].imageUrl.thumbMax)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(
                            holder.articleImageView
                        )
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article)
                }
            }
        } catch (e: Exception) {
            holder.articleImageView.setBackgroundResource(R.drawable.default_article)
        }
        if (!StringUtils.isNullOrEmpty(libraryListing[position].videoUrl)) {
            holder.videoIndicatorImageView.visibility = View.VISIBLE
        } else {
            holder.videoIndicatorImageView.visibility = View.INVISIBLE
        }
        holder.bookmarkArticleImageView.visibility = View.VISIBLE

        if ("0" == libraryListing[position].is_bookmark) {
            holder.bookmarkArticleImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.bookmarkArticleImageView.context,
                    R.drawable.ic_bookmark
                )
            )
        } else {
            holder.bookmarkArticleImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.bookmarkArticleImageView.context,
                    R.drawable.ic_bookmarked
                )
            )
        }
        setWinnerOrGoldFlag(holder.trophyImageView, libraryListing[position])
    }

    private fun setWinnerOrGoldFlag(
        winnerGoldImageView: ImageView,
        articleListingResult: ArticleListingResult
    ) {
        try {
            if ("1" == articleListingResult.winner || "true" == articleListingResult.winner) {
                winnerGoldImageView.setImageResource(R.drawable.ic_trophy)
                winnerGoldImageView.visibility = View.VISIBLE
            } else if ("1" == articleListingResult.isGold || ("true"
                    == articleListingResult.isGold)) {
                winnerGoldImageView.setImageResource(R.drawable.ic_star_yellow)
                winnerGoldImageView.visibility = View.VISIBLE
            } else {
                winnerGoldImageView.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {
            winnerGoldImageView.visibility = View.GONE
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    interface RecyclerViewClickListener {
        fun onRecyclerItemClick(view: View?, position: Int)
    }

    class MtvFeedViewHolder internal constructor(
        view: View,
        val listener: RecyclerViewClickListener
    ) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var txvArticleTitle: TextView = view.findViewById(R.id.txvArticleTitle)
        var txvAuthorName: TextView = view.findViewById(R.id.txvAuthorName)
        var articleImageView: ImageView = view.findViewById(R.id.articleImageView)
        var videoIndicatorImageView: ImageView = view.findViewById(R.id.videoIndicatorImageView)
        var viewCountTextView: TextView = view.findViewById(R.id.viewCountTextView)
        var commentCountTextView: TextView =
            view.findViewById<View>(R.id.commentCountTextView) as TextView
        var recommendCountTextView: TextView =
            view.findViewById<View>(R.id.recommendCountTextView) as TextView
        var bookmarkArticleImageView: ImageView =
            view.findViewById<View>(R.id.bookmarkArticleImageView) as ImageView
        var watchLaterImageView: ImageView =
            view.findViewById<View>(R.id.watchLaterImageView) as ImageView
        var trophyImageView: ImageView = view.findViewById<View>(R.id.trophyImageView) as ImageView
        var forYouInfoLL: LinearLayout = view.findViewById(R.id.forYouInfoLL)
        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onRecyclerItemClick(v, adapterPosition)
            }
        }

        init {
            forYouInfoLL.visibility = View.GONE
            watchLaterImageView.visibility = View.GONE
            bookmarkArticleImageView.setOnClickListener(this)
            view.setOnClickListener(this)
        }
    }
}
