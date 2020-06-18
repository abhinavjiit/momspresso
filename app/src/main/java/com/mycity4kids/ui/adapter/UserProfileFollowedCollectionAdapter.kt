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

class UserProfileFollowedCollectionAdapter(private val collectionRecyclerViewClickListener: CollectionRecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var followedCollectionsList: ArrayList<UserCollectionsModel>? = null

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
            followedCollectionsList == null -> 0
            followedCollectionsList?.size!! >= 6 -> 6
            else -> followedCollectionsList?.size!!
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            try {
                Picasso.get().load(followedCollectionsList?.get(position)?.imageUrl).error(R.drawable.default_article).into(
                    holder.tagImageView
                )
            } catch (e: Exception) {
                holder.tagImageView.setImageResource(R.drawable.default_article)
            }

            holder.topicsNameTextView.text = followedCollectionsList?.get(position)?.name
            holder.container.setOnClickListener {

                collectionRecyclerViewClickListener.onFollowedCollectionsClick(
                    position,
                    followedCollectionsList?.get(position)?.userCollectionId
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
        fun onFollowedCollectionsClick(position: Int, id: String?)
    }

    fun followedCollectionsListData(createdCollectionData: ArrayList<UserCollectionsModel>) {
        followedCollectionsList = createdCollectionData
    }
}
