package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.ui.activity.FeaturedOnActivity
import com.squareup.picasso.Picasso
import java.util.*


class FeatureOnRecyclerAdapter(private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<FeatureOnRecyclerAdapter.FeatureOnViewHolder>() {
    private var featuredList: ArrayList<UserCollectionsModel>? = null
    internal var recyclerView: RecyclerView? = null
    private val mHolder: FeatureOnViewHolder? = null

    fun setData(featuredList: ArrayList<UserCollectionsModel>) {
        this.featuredList = featuredList
    }

    fun setListUpdate(updatePos: Int, featuredList: ArrayList<UserCollectionsModel>) {
        this.featuredList = featuredList
        notifyItemChanged(updatePos, mHolder!!.follow_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureOnViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.featured_on_item_cell, parent, false)
        return FeatureOnViewHolder(v0)
    }

    override fun onBindViewHolder(holder: FeatureOnViewHolder, position: Int) {
        try {
            if (!featuredList?.get(position)?.imageUrl.isNullOrBlank()) {
                Picasso.with(holder.itemView.context).load(featuredList?.get(position)?.imageUrl).placeholder(
                        R.drawable.default_article).error(R.drawable.default_article).into(holder.contentImageView)
            } else {
                holder.contentImageView.setImageResource(R.drawable.default_article)
            }
        } catch (e: Exception) {
            holder.contentImageView.setImageResource(R.drawable.default_article)
        }
        holder.contentImageView.clipToOutline = true
        holder.featured_name.text = featuredList?.get(position)?.name
        holder.author_name.text = featuredList?.get(position)?.user_info?.firstName + " " +
                featuredList?.get(position)?.user_info?.lastName
        if (featuredList?.get(position)?.isFollowing!!) {
            holder.follow_text.text = holder.itemView.context.resources.getString(R.string.ad_following_author)
        } else {
            holder.follow_text.text = holder.itemView.context.resources.getString(R.string.ad_follow_author)
        }
        holder.follow_text.setOnClickListener {
            (holder.itemView.context as FeaturedOnActivity).followAPICall(featuredList?.get(position)?.userId,
                    featuredList?.get(position)?.userCollectionId, featuredList?.get(position)?.sortOrder!!, position)
        }
    }

    override fun getItemCount(): Int {
        return if (featuredList == null) 0 else featuredList!!.size
    }

    inner class FeatureOnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var contentImageView: ImageView
        internal var featured_name: TextView
        internal var author_name: TextView
        internal var follow_text: TextView

        init {
            contentImageView = itemView.findViewById(R.id.contentImageView)
            featured_name = itemView.findViewById(R.id.featured_name)
            author_name = itemView.findViewById(R.id.author_name)
            follow_text = itemView.findViewById(R.id.follow)
            contentImageView.clipToOutline = true
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}