package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.squareup.picasso.Picasso
import java.util.ArrayList

/**
 * Created by hemant on 4/12/17.
 */
class GroupCollectionRecyclerAdapter(
    listener: RecyclerViewClickListener
) : RecyclerView.Adapter<GroupCollectionRecyclerAdapter.FeedViewHolder>() {

    private var userCollectionsTopicList = ArrayList<UserCollectionsModel>()
    private val recyclerViewClickListener: RecyclerViewClickListener = listener

    fun setData(userCollectionsTopicList: ArrayList<UserCollectionsModel>) {
        this.userCollectionsTopicList = userCollectionsTopicList
    }

    override fun getItemCount(): Int {
        return userCollectionsTopicList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(
            R.layout.user_collections_grid_view,
            parent,
            false
        )
        return FeedViewHolder(v0)
    }

    override fun onBindViewHolder(
        viewHolder: FeedViewHolder,
        position: Int
    ) {
        try {
            try {
                Picasso.get().load(userCollectionsTopicList[position].item_info.collectionImageUrl).placeholder(
                    R.drawable.default_article
                ).error(
                    R.drawable.default_article
                ).fit().into(viewHolder.tagImageView)
            } catch (e: Exception) {
                viewHolder.tagImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        BaseApplication.getAppContext(),
                        R.drawable.default_article
                    )
                )
            }
            viewHolder.topicsNameTextView.text = userCollectionsTopicList[position].item_info.name
            viewHolder.tagImageView.clipToOutline = true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4KException", Log.getStackTraceString(e))
        }
    }

    inner class FeedViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var tagImageView: ImageView
        var topicsNameTextView: TextView

        init {
            topicsNameTextView =
                view.findViewById<View>(R.id.topicsNameTextView) as TextView
            tagImageView =
                view.findViewById<View>(R.id.tagImageView) as ImageView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            recyclerViewClickListener.onCollectionClickListener(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onCollectionClickListener(view: View?, position: Int)
    }
}
