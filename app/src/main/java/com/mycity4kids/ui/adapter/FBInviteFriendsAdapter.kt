package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.squareup.picasso.Picasso

class FBInviteFriendsAdapter(private val mListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<FBInviteFriendsAdapter.FBInviteFriendsViewHolder>() {
    private var list: List<FacebookInviteFriendsData>? = null

    fun setListData(list: List<FacebookInviteFriendsData>?) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FBInviteFriendsViewHolder {
        val v0 = LayoutInflater.from(parent.context).inflate(
            R.layout.fb_invite_friend_item,
            parent,
            false
        )
        return FBInviteFriendsViewHolder(v0, mListener)
    }

    inner class FBInviteFriendsViewHolder(itemView: View, listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorNameTextView: TextView
        internal var userImageView: ImageView
        internal var inviteTextView: TextView
        internal var invitedTextView: TextView

        init {
            authorNameTextView = itemView.findViewById<View>(R.id.authorNameTextView) as TextView
            userImageView = itemView.findViewById<View>(R.id.authorImageView) as ImageView
            inviteTextView = itemView.findViewById<View>(R.id.inviteTextView) as TextView
            invitedTextView = itemView.findViewById<View>(R.id.invitedTextView) as TextView

            userImageView.setOnClickListener(this)
            authorNameTextView.setOnClickListener(this)
            inviteTextView.setOnClickListener(this)
            invitedTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: FBInviteFriendsViewHolder, position: Int) {
        try {
            holder.authorNameTextView.text =
                list?.getOrElse(position) { list?.first() }?.firstName + " " + list?.getOrElse(
                    position
                ) { list?.first() }?.lastName
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        try {
            Picasso.get().load(list?.get(position)?.profilePicUrl?.clientApp).placeholder(
                R.drawable.default_article
            ).error(R.drawable.default_article).fit().into(holder.userImageView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        if (list?.get(position)?.isFollowing == "1") {
            holder.inviteTextView.visibility = View.GONE
            holder.invitedTextView.visibility = View.VISIBLE
        } else {
            holder.inviteTextView.visibility = View.VISIBLE
            holder.invitedTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}
