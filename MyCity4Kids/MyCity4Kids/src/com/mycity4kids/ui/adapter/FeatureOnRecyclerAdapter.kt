package com.mycity4kids.ui.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.mycity4kids.R
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.ui.activity.FeaturedOnActivity
import com.squareup.picasso.Picasso

import java.util.ArrayList

import androidx.recyclerview.widget.RecyclerView


class FeatureOnRecyclerAdapter(private val mContext: Context, private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<FeatureOnRecyclerAdapter.FeatureOnViewHolder>() {
    private val mInflator: LayoutInflater
    private var featuredList: ArrayList<UserCollectionsModel>? = null
    private val userId: String? = null
    internal var recyclerView: RecyclerView? = null
    private val mHolder: FeatureOnViewHolder? = null

    init {
        mInflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setData(featuredList: ArrayList<UserCollectionsModel>) {
        this.featuredList = featuredList
    }

    fun setListUpdate(updatePos: Int, featuredList: ArrayList<UserCollectionsModel>) {
        this.featuredList = featuredList
        notifyItemChanged(updatePos, mHolder!!.follow_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureOnViewHolder {
        val v0 = mInflator.inflate(R.layout.featured_on_item_cell, parent, false)
        return FeatureOnViewHolder(v0)
    }

    override fun onBindViewHolder(holder: FeatureOnViewHolder, position: Int) {
        Picasso.with(mContext).load(featuredList!![position].imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .fit().into(holder.contentImageView)
        holder.featured_name.text = featuredList!![position].name
        holder.author_name.text = featuredList!![position].name
        if (featuredList!![position].isFollowing) {
            holder.follow_text.text = "Following"
        } else {
            holder.follow_text.text = mContext.resources.getString(R.string.ad_follow_author)
        }
        holder.follow_text.setOnClickListener { (mContext as FeaturedOnActivity).followAPICall(featuredList!![position].userId, featuredList!![position].userCollectionId, featuredList!![position].sortOrder, position) }
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
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}