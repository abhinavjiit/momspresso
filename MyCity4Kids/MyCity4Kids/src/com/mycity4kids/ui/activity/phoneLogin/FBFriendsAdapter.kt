package com.mycity4kids.ui.activity.phoneLogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import java.util.*

/**
 * Created by hemant on 19/7/17.
 */
class FBFriendsAdapter(private val mListener: RecyclerViewClickListener) : RecyclerView.Adapter<FBFriendsAdapter.FBFriendsViewHolder>() {
    private var list: MutableList<FacebookFriends.FBObject>? = null

    fun setListData(list: MutableList<FacebookFriends.FBObject>?) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FBFriendsViewHolder {
        var viewHolder: FBFriendsViewHolder? = null
//        LayoutInflater.from(parent.context).inflate(R.layout.fb_friend_item, parent, false)
        val v0 = LayoutInflater.from(parent.context).inflate(R.layout.fb_friend_item, parent, false)
        viewHolder = FBFriendsViewHolder(v0, mListener)
        return viewHolder
    }

    inner class FBFriendsViewHolder(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var nameTextView: TextView

        init {
            nameTextView = itemView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: FBFriendsViewHolder, position: Int) {
        holder.nameTextView.text = list?.getOrElse(position, {list?.first()})?.name
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }

}
