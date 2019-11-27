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
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_collection_adapter.view.*
import kotlinx.android.synthetic.main.add_collection_adapter.view.close
import kotlinx.android.synthetic.main.add_collection_adapter.view.collectionDiscription
import kotlinx.android.synthetic.main.add_collection_adapter.view.collectionTitle
import kotlinx.android.synthetic.main.add_collection_adapter.view.root
import kotlinx.android.synthetic.main.edit_collection_item_adapter.view.*

const val ADD_COLLECTION_TYPE = 2
const val EDIT_COLLECTION_ITEM_TYPE = 1

class AddCollectionAdapter(val activity: Context, var recyclerViewClickListner: RecyclerViewClickListener, var adapterViewType: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var collectionList = ArrayList<UserCollectionsModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ADD_COLLECTION_TYPE) {
            val view = mInflater.inflate(R.layout.add_collection_adapter, parent, false)
            ViewHolderCollection(view, recyclerViewClickListner)
        } else {
            val view = mInflater.inflate(R.layout.edit_collection_item_adapter, parent, false)
            ViewHolderCollectionItem(view, recyclerViewClickListner)
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
            EDIT_COLLECTION_ITEM_TYPE
        else
            ADD_COLLECTION_TYPE
    }


    fun setListData(listData: ArrayList<UserCollectionsModel>) {
        collectionList = listData

    }

    fun setItemListData(listData: ArrayList<UserCollectionsModel>) {
        collectionList = listData

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
