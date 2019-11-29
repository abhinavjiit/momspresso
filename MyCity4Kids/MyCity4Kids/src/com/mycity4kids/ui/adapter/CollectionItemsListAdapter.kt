package com.mycity4kids.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.*


class CollectionItemsListAdapter(var activity: Context, var recyclerViewClick: RecyclerViewClick) : RecyclerView.Adapter<CollectionItemsListAdapter.ViewHolder>() {


    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var context: Context = activity
    private var userCollectionsTopicList = ArrayList<UserCollectionsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.user_collection_items_list_adapter, parent, false)
        return ViewHolder(view, recyclerViewClick)
    }

    override fun getItemCount(): Int {

        return userCollectionsTopicList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.articleTitleTextView.text = userCollectionsTopicList[position].item_info.title
            if (userCollectionsTopicList[position].item_info.viewCount > 0)
                holder.viewCountTextView.text = userCollectionsTopicList[position].item_info.viewCount.toString()
            else holder.viewCountTextView.visibility = View.GONE
            if (userCollectionsTopicList[position].itemType.equals(AppConstants.CONTENT_TYPE_VIDEO)) {
                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.thumbnail)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }

                holder.articleVideoShortStoryIcon.setImageResource(R.drawable.ic_video)
                if (userCollectionsTopicList[position].item_info.author != null)
                    holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.author.firstName + userCollectionsTopicList[position].item_info.author.lastName

            } else if (userCollectionsTopicList[position].itemType.equals(AppConstants.CONTENT_TYPE_ARTICLE)) {
                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.imageUrl.thumbMax)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }
                holder.articleVideoShortStoryIcon.setImageResource(R.drawable.draft_red)

                holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.userName
            } else {
                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.storyImage)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }
                holder.articleVideoShortStoryIcon.setImageResource(R.drawable.shortstory_red)

                holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.userName


            }
            holder.root.setOnClickListener {
                recyclerViewClick.onRecyclerViewclick(position)
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4KException", Log.getStackTraceString(e))

        }


    }

    fun setListData(topicsData: ArrayList<UserCollectionsModel>) {
        userCollectionsTopicList = topicsData
    }


    class ViewHolder(mView: View, recyclerViewClick: RecyclerViewClick) : RecyclerView.ViewHolder(mView) {
        var articleTitleTextView: TextView = mView.articleTitleTextView
        var articleImageView: ImageView = mView.articleImageView
        var articleAuthorName: TextView = mView.articleAuthorName
        var viewCountTextView: TextView = mView.viewCountTextView
        var articleVideoShortStoryIcon: ImageView = mView.articleVideoShortStoryIcon
        var root: RelativeLayout = mView.root

    }

    interface RecyclerViewClick {
        fun onRecyclerViewclick(position: Int)
    }


}
