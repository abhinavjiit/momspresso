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
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_collection_adapter.view.*

class AddCollectionAdapter(val activity: Context, var recyclerViewClickListner: RecyclerViewClickListener) : RecyclerView.Adapter<AddCollectionAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var collectionList = ArrayList<UserCollectiosModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.add_collection_adapter, parent, false)
        return ViewHolder(view, recyclerViewClickListner)


    }

    override fun getItemCount(): Int {
        return collectionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            Picasso.with(activity).load(collectionList[position].imageUrl).placeholder(R.drawable.default_article).into(holder.collectionImageVIEW)
        } catch (e: Exception) {
            holder.collectionImageVIEW.setBackgroundResource(R.drawable.default_article);
        }

        holder.collectionTitle.text = collectionList[position].name

        holder.root.setOnClickListener {
            recyclerViewClickListner.onClick(position)
        }
    }


    fun setListData(listData: UserCollectionsListModel) {
        collectionList = listData.collections_list

    }


    class ViewHolder(mView: View, recyclerViewClickListner: RecyclerViewClickListener) : RecyclerView.ViewHolder(mView) {
        var collectionImageVIEW: ImageView = mView.collectionImageVIEW
        var collectionTitle: TextView = mView.collectionTitle
        var collectionDiscription: TextView = mView.collectionDiscription
        var root: RelativeLayout = mView.root


    }

    interface RecyclerViewClickListener {
        fun onClick(position: Int)
    }

}