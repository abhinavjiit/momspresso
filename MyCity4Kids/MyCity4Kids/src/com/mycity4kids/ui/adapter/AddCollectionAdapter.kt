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
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.CollectionsModels.UserCollectiosModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_collection_adapter.view.*
import kotlinx.android.synthetic.main.add_collection_adapter.view.close
import kotlinx.android.synthetic.main.add_collection_adapter.view.collectionDiscription
import kotlinx.android.synthetic.main.add_collection_adapter.view.collectionTitle
import kotlinx.android.synthetic.main.add_collection_adapter.view.root
import kotlinx.android.synthetic.main.edit_collection_item_adapter.view.*

class AddCollectionAdapter(val activity: Context, var recyclerViewClickListner: RecyclerViewClickListener, var adapterViewType: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var collectionList = ArrayList<UserCollectiosModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 2) {
            val view = mInflater.inflate(R.layout.add_collection_adapter, parent, false)
            return ViewHolderCollection(view, recyclerViewClickListner)
        } else {
            val view = mInflater.inflate(R.layout.edit_collection_item_adapter, parent, false)
            return ViewHolderCollectionItem(view, recyclerViewClickListner)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolderCollection) {
            try {
                Picasso.with(activity).load(collectionList[position].imageUrl).placeholder(R.drawable.default_article).into(holder.collectionImageVIEW)
            } catch (e: Exception) {
                holder.collectionImageVIEW.setBackgroundResource(R.drawable.default_article);
            }

            holder.collectionTitle.text = collectionList[position].name

            holder.root.setOnClickListener {
                recyclerViewClickListner.onClick(position)
            }
        } else if (holder is ViewHolderCollectionItem) {
            holder.collectionTitle.text = collectionList[position].item_info.title

            if (AppConstants.ARTICLE_COLLECTION_TYPE.equals(collectionList[position].itemType)) {
                try {
                    Picasso.with(activity).load(collectionList[position].item_info.imageUrl.thumbMax).placeholder(R.drawable.default_article).into(holder.collectionItemImageVIEW)
                } catch (e: Exception) {
                    holder.collectionItemImageVIEW.setBackgroundResource(R.drawable.default_article);
                }

            } else if (AppConstants.SHORT_STORY_COLLECTION_TYPE.equals(collectionList[position].itemType)) {
                try {
                    Picasso.with(activity).load(collectionList[position].item_info.storyImage)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.collectionItemImageVIEW)
                } catch (e: Exception) {
                    holder.collectionItemImageVIEW.setImageResource(R.drawable.default_article)
                }
            } else {

                try {
                    Picasso.with(activity).load(collectionList[position].item_info.thumbnail)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.collectionItemImageVIEW)
                } catch (e: Exception) {
                    holder.collectionItemImageVIEW.setImageResource(R.drawable.default_article)
                }
            }

            holder.close.setOnClickListener {
                recyclerViewClickListner.onClick(position)
            }

        }

    }


    override fun getItemCount(): Int {
        return collectionList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (adapterViewType)
            1
        else
            2
    }


    fun setListData(listData: UserCollectionsListModel) {
        collectionList = listData.collections_list

    }

    fun setItemListData(listData: UserCollectionsListModel) {
        collectionList = listData.collectionItems

    }


    class ViewHolderCollection(mView: View, recyclerViewClickListner: RecyclerViewClickListener) : RecyclerView.ViewHolder(mView) {
        var collectionImageVIEW: ImageView = mView.collectionImageVIEW
        var collectionTitle: TextView = mView.collectionTitle
        var collectionDiscription: TextView = mView.collectionDiscription
        var root: RelativeLayout = mView.root


    }

    class ViewHolderCollectionItem(mView: View, recyclerViewClickListner: RecyclerViewClickListener) : RecyclerView.ViewHolder(mView) {
        var collectionItemImageVIEW: ImageView = mView.collectionItemImageVIEW
        var collectionTitle: TextView = mView.collectionTitle
        var collectionDiscription: TextView = mView.collectionDiscription
        var root: RelativeLayout = mView.root
        var close: ImageView = mView.close


    }

    interface RecyclerViewClickListener {
        fun onClick(position: Int)
    }

}