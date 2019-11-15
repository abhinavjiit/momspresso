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

        holder.text.text = userCollectionsTopicList[position].itemId

    }

    fun setListData(topicsData: UserCollectionsListModel) {
        userCollectionsTopicList = topicsData.collectionItems
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var text: TextView = mView.text

    }


}