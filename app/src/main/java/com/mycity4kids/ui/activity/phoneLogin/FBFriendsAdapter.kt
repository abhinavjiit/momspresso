package com.mycity4kids.ui.activity.phoneLogin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.models.response.FacebookFriendsData
import com.squareup.picasso.Picasso

/**
 * Created by hemant on 19/7/17.
 */
class FBFriendsAdapter(private val mListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<FBFriendsAdapter.FBFriendsViewHolder>() {
    private var list: List<FacebookFriendsData>? = null

    fun setListData(list: List<FacebookFriendsData>?) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FBFriendsViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.fb_friend_item, parent, false)
        return FBFriendsViewHolder(v0, mListener)
    }

    inner class FBFriendsViewHolder(itemView: View, listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorNameTextView: TextView
        internal var userImageView: ImageView
        internal var followTextView: TextView
        internal var followingTextView: TextView

        init {
            authorNameTextView = itemView.findViewById<View>(R.id.authorNameTextView) as TextView
            userImageView = itemView.findViewById<View>(R.id.authorImageView) as ImageView
            followTextView = itemView.findViewById<View>(R.id.followTextView) as TextView
            followingTextView = itemView.findViewById<View>(R.id.followingTextView) as TextView

            userImageView.setOnClickListener(this)
            authorNameTextView.setOnClickListener(this)
            followTextView.setOnClickListener(this)
            followingTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: FBFriendsViewHolder, position: Int) {
        try {
            holder.authorNameTextView.text =
                list?.getOrElse(position) { list?.first() }?.firstName + " " + list?.getOrElse(
                    position
                ) { list?.first() }?.lastName
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        try {
            Picasso.get().load(list?.get(position)?.profilePicUrl?.clientApp).placeholder(
                R.drawable.default_article
            ).error(R.drawable.default_article).fit().into(holder.userImageView)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        if (list?.get(position)?.isFollowing == "1") {
            holder.followTextView.visibility = View.GONE
            holder.followingTextView.visibility = View.VISIBLE
        } else {
            holder.followTextView.visibility = View.VISIBLE
            holder.followingTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}
