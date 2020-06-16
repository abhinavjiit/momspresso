package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_created_collections_adapter.view.*

class UserProfileCreatedCollectionsAdapter(private val collectionRecyclerViewClickListener: CollectionRecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var createdCollectionsList: ArrayList<UserCollectionsModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_created_collections_adapter,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return when {
            createdCollectionsList == null -> 0
            createdCollectionsList?.size!! >= 6 -> 6
            else -> createdCollectionsList?.size!!
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            Picasso.get().load(createdCollectionsList?.get(position)?.imageUrl).error(R.drawable.default_article).into(
                holder.tagImageView
            )
            holder.topicsNameTextView.text = createdCollectionsList?.get(position)?.name
            holder.container.setOnClickListener {

                collectionRecyclerViewClickListener.onCollectionsClick(
                    position,
                    createdCollectionsList?.get(position)?.userCollectionId
                )
            }


        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tagImageView: ImageView = itemView.tagImageView
        val topicsNameTextView: TextView = itemView.topicsNameTextView
        val container: RelativeLayout = itemView.container
    }

    interface CollectionRecyclerViewClickListener {
        fun onCollectionsClick(position: Int, id: String?)
    }

    fun createdCollectionsListData(createdCollectionData: ArrayList<UserCollectionsModel>) {
        createdCollectionsList = createdCollectionData
    }

}