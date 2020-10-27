package com.mycity4kids.ui.activity.phoneLogin

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fb_friend_item.view.*

/**
 * Created by hemant on 19/7/17.
 */
class FBFriendsAdapter(
    private val mListener: RecyclerViewClickListener,
    var comingFor: String = ""
) :
    RecyclerView.Adapter<FBFriendsAdapter.FBFriendsViewHolder>() {
    private var list: List<FacebookInviteFriendsData>? = null

    fun setListData(list: List<FacebookInviteFriendsData>?) {
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
        internal var followTextView: MomspressoButtonWidget
        internal var followingTextView: TextView
        internal var user1: ImageView
        internal var user2: ImageView
        internal var user3: ImageView
        internal var mutualFriendsContainer: ConstraintLayout
        internal var remainingCountTextView: TextView


        init {
            remainingCountTextView = itemView.remainingCountTextView
            mutualFriendsContainer = itemView.mutualFriendsContainer
            user1 = itemView.user1
            user2 = itemView.user2
            user3 = itemView.user3
            authorNameTextView = itemView.findViewById<View>(R.id.authorNameTextView) as TextView
            userImageView = itemView.findViewById<View>(R.id.authorImageView) as ImageView
            followTextView =
                itemView.findViewById<View>(R.id.followTextView) as MomspressoButtonWidget
            followingTextView =
                itemView.findViewById<View>(R.id.followingTextView) as TextView

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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FBFriendsViewHolder, position: Int) {
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
            list?.get(position)?.userFriendsList?.let {
                holder.mutualFriendsContainer.visibility = View.VISIBLE
                if (it.size >= 1) {
                    holder.user1.visibility = View.VISIBLE
                    Picasso.get().load(it[0].profilePicUrl?.clientApp).placeholder(
                        R.drawable.default_article
                    ).error(R.drawable.default_article).fit().into(holder.user1)
                }
                if (it.size >= 2) {
                    holder.user2.visibility = View.VISIBLE
                    Picasso.get().load(it[1].profilePicUrl?.clientApp).placeholder(
                        R.drawable.default_article
                    ).error(R.drawable.default_article).fit().into(holder.user2)
                }
                if (it.size >= 3) {
                    holder.user3.visibility = View.VISIBLE
                    Picasso.get().load(it[2].profilePicUrl?.clientApp).placeholder(
                        R.drawable.default_article
                    ).error(R.drawable.default_article).fit().into(holder.user3)
                }
                if (it.size > 3) {
                    holder.remainingCountTextView.visibility = View.VISIBLE
                    holder.remainingCountTextView.text = "+ ${it.size - 3}"
                }
            }
            Picasso.get().load(list?.get(position)?.profilePicUrl?.clientApp).placeholder(
                R.drawable.default_article
            ).error(R.drawable.default_article).fit().into(holder.userImageView)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
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
