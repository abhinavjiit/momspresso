package com.mycity4kids.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.kelltontech.utils.DateTimeUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.MixFeedResult
import com.squareup.picasso.Picasso
import org.apmem.tools.layouts.FlowLayout
import java.text.SimpleDateFormat

class UsersFeaturedContentAdapter(private val mListener: RecyclerViewClickListener) :
        RecyclerView.Adapter<UsersFeaturedContentAdapter.UserFeaturedContentViewHolder>() {

    private val CONTENT_TYPE_ARTICLE = 0
    private val CONTENT_TYPE_SHORT_STORY = 1
    private val CONTENT_TYPE_VIDEO = 2

    private var userFeaturedOnList: ArrayList<MixFeedResult>? = null

    override fun getItemViewType(position: Int): Int {
        return if (AppConstants.CONTENT_TYPE_ARTICLE == userFeaturedOnList?.get(position)?.itemType) {
            CONTENT_TYPE_ARTICLE
        } else if (AppConstants.CONTENT_TYPE_SHORT_STORY == userFeaturedOnList?.get(position)?.itemType) {
            CONTENT_TYPE_SHORT_STORY
        } else {
            CONTENT_TYPE_VIDEO
        }
    }

    fun setListData(featuredItemsOn: ArrayList<MixFeedResult>?) {
        this.userFeaturedOnList = featuredItemsOn
    }

    override fun getItemCount(): Int {
        return userFeaturedOnList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFeaturedContentViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.featured_item, parent, false)
        return UserFeaturedContentViewHolder(v0, mListener)
    }

    inner class UserFeaturedContentViewHolder internal constructor(itemView: View, val listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var contentImageView: ImageView
        internal var itemTypeImageView: ImageView
        internal var dateTextView: TextView
        internal var titleTextView: TextView
        internal var collectionItem1TextView: TextView
        internal var collectionItem2TextView: TextView
        internal var collectionItem3TextView: TextView
        internal var collectionItem4TextView: TextView
        internal var moreItemsTextView: TextView
        internal var collectionsFLContainer: FlowLayout

        init {
            titleTextView = itemView.findViewById<View>(R.id.titleTextView) as TextView
            contentImageView = itemView.findViewById<View>(R.id.itemImageView) as ImageView
            itemTypeImageView = itemView.findViewById<View>(R.id.itemTypeImageView) as ImageView
            dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView
            collectionItem1TextView = itemView.findViewById<View>(R.id.collectionItem1TextView) as TextView
            collectionItem2TextView = itemView.findViewById<View>(R.id.collectionItem2TextView) as TextView
            collectionItem3TextView = itemView.findViewById<View>(R.id.collectionItem3TextView) as TextView
            collectionItem4TextView = itemView.findViewById<View>(R.id.collectionItem4TextView) as TextView
            moreItemsTextView = itemView.findViewById<View>(R.id.moreItemsTextView) as TextView
            collectionsFLContainer = itemView.findViewById<View>(R.id.collectionsFLContainer) as FlowLayout
            collectionItem1TextView.setOnClickListener(this)
            collectionItem2TextView.setOnClickListener(this)
            collectionItem3TextView.setOnClickListener(this)
            collectionItem4TextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
            moreItemsTextView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onFeaturedItemClick(v, adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: UserFeaturedContentViewHolder, position: Int) {
        holder.titleTextView.text = userFeaturedOnList?.get(position)?.title
        if (userFeaturedOnList?.get(position)?.itemType == AppConstants.CONTENT_TYPE_ARTICLE) {
            setArticleContentImage(position, holder)
            holder.itemTypeImageView.setImageResource(R.drawable.draft_red)
        } else if (userFeaturedOnList?.get(position)?.itemType == AppConstants.CONTENT_TYPE_SHORT_STORY) {
            setArticleContentImage(position, holder)
            holder.itemTypeImageView.setImageResource(R.drawable.shortstory_red)
        } else {
            setVideoContentImage(position, holder)
            holder.itemTypeImageView.setImageResource(R.drawable.ic_video)
        }
        setContentDate(userFeaturedOnList?.get(position)?.itemType, position, holder)
        try {
            populateCollectionsForEachItem(position, holder)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun populateCollectionsForEachItem(position: Int, holder: UserFeaturedContentViewHolder) {
        try {
            when {
                (userFeaturedOnList?.get(position)?.collectionListTotal == null)
                        || (userFeaturedOnList?.get(position)?.collectionListTotal == 0) -> {
                    holder.collectionsFLContainer.visibility = GONE
                }
                (userFeaturedOnList?.get(position)?.collectionListTotal ?: 0) == 1 -> {
                    holder.collectionItem2TextView.visibility = GONE
                    holder.collectionItem3TextView.visibility = GONE
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(0)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (userFeaturedOnList?.get(position)?.collectionListTotal ?: 0) == 2 -> {
                    holder.collectionItem3TextView.visibility = GONE
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(1)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (userFeaturedOnList?.get(position)?.collectionListTotal ?: 0) == 3 -> {
                    holder.collectionItem4TextView.visibility = GONE
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(1)?.name
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(2)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (userFeaturedOnList?.get(position)?.collectionListTotal ?: 0) == 4 -> {
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(1)?.name
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(2)?.name
                    holder.collectionItem4TextView.visibility = VISIBLE
                    holder.collectionItem4TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(3)?.name
                    holder.moreItemsTextView.visibility = GONE
                }
                (userFeaturedOnList?.get(position)?.collectionListTotal ?: 0) > 4 -> {
                    val moreItemCount = userFeaturedOnList?.get(position)?.collectionListTotal!! - 4
                    holder.collectionItem1TextView.visibility = VISIBLE
                    holder.collectionItem1TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(0)?.name
                    holder.collectionItem2TextView.visibility = VISIBLE
                    holder.collectionItem2TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(1)?.name
                    holder.collectionItem3TextView.visibility = VISIBLE
                    holder.collectionItem3TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(2)?.name
                    holder.collectionItem4TextView.visibility = VISIBLE
                    holder.collectionItem4TextView.text = userFeaturedOnList?.get(position)?.collectionList?.get(3)?.name
                    holder.moreItemsTextView.visibility = VISIBLE
                    holder.moreItemsTextView.text = holder.itemView.context.getString(R.string.profile_featured_more, moreItemCount)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun setContentDate(itemType: String?, position: Int, holder: UserFeaturedContentViewHolder) {
        try {
            if (AppConstants.CONTENT_TYPE_VIDEO == itemType) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
                val formatter = SimpleDateFormat("dd MMM yyyy")
                holder.dateTextView.text = formatter.format((parser.parse(userFeaturedOnList?.get(position)?.created_at)))
            } else {
                userFeaturedOnList?.get(position)?.createdTime?.let {
                    holder.dateTextView.text =
                            DateTimeUtils.getDateFromTimestamp(it.toLong())
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun setVideoContentImage(position: Int, holder: UserFeaturedContentViewHolder) {
        try {
            if (!StringUtils.isNullOrEmpty(userFeaturedOnList?.get(position)?.thumbnail)) {
                Picasso.get().load(userFeaturedOnList?.get(position)?.thumbnail).placeholder(
                        R.drawable.default_article).into(holder.contentImageView)
            } else {
                holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            }
        } catch (e: Exception) {
            holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun setArticleContentImage(position: Int, holder: UserFeaturedContentViewHolder) {
        try {
            if (!StringUtils.isNullOrEmpty(userFeaturedOnList?.get(position)?.imageUrl?.clientApp) &&
                    (userFeaturedOnList?.get(position)?.imageUrl?.thumbMax == null ||
                            userFeaturedOnList?.get(position)?.imageUrl?.thumbMax?.endsWith("default.jpg")!!)) {
                Picasso.get().load(userFeaturedOnList?.get(position)?.imageUrl?.clientApp).placeholder(
                        R.drawable.default_article).into(holder.contentImageView)
            } else {
                if (!StringUtils.isNullOrEmpty(userFeaturedOnList?.get(position)?.imageUrl?.thumbMax)) {
                    Picasso.get().load(userFeaturedOnList?.get(position)?.imageUrl?.thumbMax).placeholder(
                            R.drawable.default_article).error(R.drawable.default_article).into(holder.contentImageView)
                } else {
                    holder.contentImageView.setBackgroundResource(R.drawable.default_article)
                }
            }
        } catch (e: Exception) {
            holder.contentImageView.setBackgroundResource(R.drawable.default_article)
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    interface RecyclerViewClickListener {
        fun onFeaturedItemClick(view: View, position: Int)
    }
}
