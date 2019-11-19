package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.*


class CollectionItemsListAdapter(var activity: Context) : RecyclerView.Adapter<CollectionItemsListAdapter.ViewHolder>() {


    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var context: Context = activity
    private var userCollectionsTopicList = ArrayList<UserCollectiosModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.user_collection_items_list_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userCollectionsTopicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            holder.articleTitleTextView.text = userCollectionsTopicList[position].item_info.title
          //  holder.viewCountTextView.text=userCollectionsTopicList
            if (userCollectionsTopicList[position].itemType.equals("2")) {
                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.thumbnail)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }


                if (userCollectionsTopicList[position].item_info.author != null)
                    holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.author.firstName + userCollectionsTopicList[position].item_info.author.lastName

            } else if (userCollectionsTopicList[position].itemType.equals("0")) {
                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.imageUrl.thumbMax)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }

                holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.userName


            } else {

                try {
                    Picasso.with(activity).load(userCollectionsTopicList[position].item_info.storyImage)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView)
                } catch (e: Exception) {
                    holder.articleImageView.setImageResource(R.drawable.default_article)
                }


                holder.articleAuthorName.text = userCollectionsTopicList[position].item_info.userName


            }
        } catch (e: Exception) {


        }


    }

    fun setListData(topicsData: UserCollectionsListModel) {
        userCollectionsTopicList = topicsData.collectionItems
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var articleTitleTextView: TextView = mView.articleTitleTextView
        var articleImageView = mView.articleImageView
        var articleAuthorName = mView.articleAuthorName
        var viewCountTextView = mView.viewCountTextView

    }


}