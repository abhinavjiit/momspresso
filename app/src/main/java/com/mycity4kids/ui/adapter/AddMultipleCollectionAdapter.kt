package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.models.response.MixFeedResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_multiple_collection_item.view.*
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleAuthorName
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleImageView
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleTitleTextView
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleVideoShortStoryIcon
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.root
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.viewCountTextView

class AddMultipleCollectionAdapter(var recyclerViewClickListner: RecyclerViewClick, val viewType: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: ArrayList<ArticleListingResult>? = null
    private var mixFeedList: ArrayList<MixFeedResult>? = null

    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = mInflater.inflate(R.layout.add_multiple_collection_item, parent, false)
            return ViewHolderCollection(view)
        } else {
            val view = mInflater.inflate(R.layout.add_multiple_collection_item, parent, false)
            return ViewHolderCreatedCollection(view)
        }
    }

    override fun getItemCount(): Int {
        return if ("READ" == viewType)
            dataList?.size!!
        else
            mixFeedList?.size!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if ("READ" == viewType)
            1
        else
            2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderCollection) {
            if (null != dataList?.get(position)?.imageUrl) {
                Picasso.get().load(dataList?.get(position)?.imageUrl?.thumbMin).placeholder(R.drawable.default_article).error(R.drawable.default_article).into((holder).articleImageView)
            } else {
                (holder).articleImageView.setBackgroundResource(R.drawable.article_default)
            }
            holder.articleTitleTextView.text = dataList?.get(position)?.title
            holder.viewCountTextView.text = dataList?.get(position)?.articleCount
            holder.articleAuthorName.text = dataList?.get(position)?.userName

            if (dataList?.get(position)?.isCollectionItemSelected!!) {
                holder.checkBoxImageView.setImageResource(R.drawable.ic_done)
            } else {
                holder.checkBoxImageView.setImageResource(R.drawable.ic_rectangle)
            }
            holder.root.setOnClickListener {
                recyclerViewClickListner.onclick(position)
            }
        } else if (holder is ViewHolderCreatedCollection) {

            when (mixFeedList?.get(position)?.contentType) {

                AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                    try {
                        Picasso.get().load(mixFeedList?.get(position)?.storyImage).placeholder(R.drawable.default_article).error(R.drawable.default_article).into((holder).articleImageView)
                    } catch (e: Exception) {
                        (holder).articleImageView.setBackgroundResource(R.drawable.article_default)
                    }
                    holder.articleTitleTextView.text = mixFeedList?.get(position)?.title
                    holder.articleAuthorName.text = mixFeedList?.get(position)?.userName
                    holder.viewCountTextView.text = mixFeedList?.get(position)?.articleCount.toString()
                    holder.articleVideoShortStoryIcon.setImageResource(R.drawable.shortstory_red)
                    if (mixFeedList?.get(position)?.isCollectionItemSelected!!) {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_done)
                    } else {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_rectangle)

                    }
                }
                AppConstants.CONTENT_TYPE_VIDEO -> {
                    try {
                        Picasso.get().load(mixFeedList?.get(position)?.thumbnail).placeholder(R.drawable.default_article).error(R.drawable.default_article).into((holder).articleImageView)
                    } catch (e: Exception) {
                        (holder).articleImageView.setBackgroundResource(R.drawable.article_default)
                    }
                    holder.articleTitleTextView.text = mixFeedList?.get(position)?.title
                    holder.articleAuthorName.text = mixFeedList?.get(position)?.userName
                    holder.viewCountTextView.text = mixFeedList?.get(position)?.view_count.toString()
                    holder.articleVideoShortStoryIcon.setImageResource(R.drawable.ic_video)
                    if (mixFeedList?.get(position)?.isCollectionItemSelected!!) {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_done)
                    } else {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_rectangle)
                    }
                }
                else -> {
                    try {
                        Picasso.get().load(mixFeedList?.get(position)?.imageUrl?.thumbMax).placeholder(R.drawable.default_article).error(R.drawable.default_article).into((holder).articleImageView)
                    } catch (e: Exception) {
                        (holder).articleImageView.setBackgroundResource(R.drawable.article_default)
                    }
                    holder.articleTitleTextView.text = mixFeedList?.get(position)?.title
                    holder.articleAuthorName.text = mixFeedList?.get(position)?.userName
                    holder.viewCountTextView.text = mixFeedList?.get(position)?.articleCount.toString()
                    holder.articleVideoShortStoryIcon.setImageResource(R.drawable.draft_red)
                    if (mixFeedList?.get(position)?.isCollectionItemSelected!!) {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_done)
                    } else {
                        holder.checkBoxImageView.setImageResource(R.drawable.ic_rectangle)

                    }
                }
            }
            holder.root.setOnClickListener {
                recyclerViewClickListner.onclick(position)
            }
        }
    }

    fun setUserReadListData(dataList: ArrayList<ArticleListingResult>?) {
        this.dataList = dataList
    }

    fun setUserCreatedListData(mixFeedList: ArrayList<MixFeedResult>?) {
        this.mixFeedList = mixFeedList
    }

    class ViewHolderCollection(mView: View) : RecyclerView.ViewHolder(mView) {
        var articleTitleTextView: TextView = mView.articleTitleTextView
        var articleImageView: ImageView = mView.articleImageView
        var articleAuthorName: TextView = mView.articleAuthorName
        var viewCountTextView: TextView = mView.viewCountTextView
        var articleVideoShortStoryIcon: ImageView = mView.articleVideoShortStoryIcon
        var root: RelativeLayout = mView.root
        var checkBoxImageView: ImageView = mView.checkBoxImageView

    }

    class ViewHolderCreatedCollection(mView: View) : RecyclerView.ViewHolder(mView) {
        var articleTitleTextView: TextView = mView.articleTitleTextView
        var articleImageView: ImageView = mView.articleImageView
        var articleAuthorName: TextView = mView.articleAuthorName
        var viewCountTextView: TextView = mView.viewCountTextView
        var articleVideoShortStoryIcon: ImageView = mView.articleVideoShortStoryIcon
        var root: RelativeLayout = mView.root
        var checkBoxImageView: ImageView = mView.checkBoxImageView

    }

    interface RecyclerViewClick {
        fun onclick(position: Int)
    }

}
