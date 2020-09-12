package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.models.response.ArticleListingResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.article_challenge_listing_adapter_layout.view.*

class ArticleChallengeListingAdapter(
    var clickListener: RecyclerViewItemClickListener,
    private var type: String? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var articleListingData: ArrayList<ArticleListingResult>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.article_challenge_listing_adapter_layout,
            parent,
            false
        )
        return ViewHolder(view, clickListener)
    }

    fun setListData(listData: ArrayList<ArticleListingResult>) {
        articleListingData = listData
    }

    override fun getItemCount(): Int {
        return articleListingData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            holder.apply {
                txvArticleTitle.text = articleListingData[position].title
                txvAuthorName.text = articleListingData[position].userName
                viewCountTextView.text = articleListingData[position].articleCount
                commentCountTextView.text = articleListingData[position].commentsCount
                recommendCountTextView.text = articleListingData[position].likesCount
                if (articleListingData[position].is_bookmark == "1") {
                    this.bookmarkArticleImageView.setImageResource(R.drawable.ic_bookmarked)
                } else {
                    this.bookmarkArticleImageView.setImageResource(R.drawable.ic_bookmark)
                }
                try {
                    Picasso.get()
                        .load(articleListingData[position].imageUrl.thumbMax).into(this.articleImageView)
                } catch (e: Exception) {
                    this.articleImageView.setImageResource(R.drawable.default_article)
                }
                type?.let {
                    trophyImageView.visibility = View.VISIBLE
                } ?: run {
                    showWinnerOrGoldFlag(trophyImageView, articleListingData[position])
                }
            }
        }
    }

    private fun showWinnerOrGoldFlag(
        winnerOrGoldImageView: ImageView,
        data: ArticleListingResult?
    ) {
        try {
            when {
                data?.winner == "1" || data?.winner == "true" -> {
                    winnerOrGoldImageView.visibility = View.VISIBLE
                    winnerOrGoldImageView.setImageResource(R.drawable.ic_trophy)
                }
                data?.isGold == "1" || data?.isGold == "true" -> {
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

    inner class ViewHolder internal constructor(
        view: View,
        clickListener: RecyclerViewItemClickListener
    ) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        val txvArticleTitle: TextView = view.txvArticleTitle
        val articleImageView: ImageView = view.articleImageView
        val txvAuthorName: TextView = view.txvAuthorName
        val viewCountTextView: TextView = view.viewCountTextView
        val commentCountTextView: TextView = view.commentCountTextView
        val recommendCountTextView: TextView = view.recommendCountTextView
        val bookmarkArticleImageView: ImageView = view.bookmarkArticleImageView
        val articleItemView: FrameLayout = view.articleItemView
        val trophyImageView: ImageView = view.trophyImageView

        init {
            txvAuthorName.setOnClickListener(this)
            articleItemView.setOnClickListener(this)
            bookmarkArticleImageView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                clickListener.onClick(it, adapterPosition)
            }
        }
    }

    interface RecyclerViewItemClickListener {
        fun onClick(view: View, position: Int)
    }
}
