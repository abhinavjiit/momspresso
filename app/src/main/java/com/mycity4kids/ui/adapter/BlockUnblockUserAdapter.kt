package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.FollowersFollowingResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.block_unblock_user_adapter.view.*
import java.util.ArrayList

class BlockUnblockUserAdapter(val recyclerViewClick: RecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var blockUserList: ArrayList<FollowersFollowingResult>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.block_unblock_user_adapter,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (blockUserList == null) 0
        else blockUserList?.size!!
    }

    fun setListData(list: ArrayList<FollowersFollowingResult>?) {
        blockUserList = list
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {
                try {
                    Picasso.get().load(blockUserList?.get(position)?.profilePicUrl?.clientApp).error(
                        R.drawable.default_commentor_img
                    ).into(authorImageView)
                } catch (e: Exception) {
                    authorImageView.setImageResource(R.drawable.default_commentor_img)
                }
                if (blockUserList?.get(position)?.isBLocked!!) {
                    followingTextView.text = "Unblock"
                } else {
                    followingTextView.text = "Block"
                }
                authorNameTextView.text =
                    blockUserList?.get(position)?.firstName.plus(" " + blockUserList?.get(position)?.lastName)
            }
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal val authorImageView: ImageView
        internal val followingTextView: TextView
        internal val authorNameTextView: TextView

        init {
            authorImageView = view.authorImageView
            followingTextView = view.followingTextView
            authorNameTextView = view.authorNameTextView
            followingTextView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (view?.id == R.id.followingTextView) {
                recyclerViewClick.onRecyclerClick(adapterPosition)
            }
        }


    }


    interface RecyclerViewClickListener {
        fun onRecyclerClick(position: Int)
    }
}