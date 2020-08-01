package com.mycity4kids.ui.adapter

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.CommentListData
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.DateTimeUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notification_replies_item.view.*

class ContentCommentReplyNotificationAdapter(val recyclerViewClickListner: RecyclerViewRepliesClickListner) :
    RecyclerView.Adapter<ContentCommentReplyNotificationAdapter.RepliesViewHolder>() {
    private var repliesList: ArrayList<CommentListData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepliesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_replies_item, null)
        return RepliesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (repliesList == null) 0 else repliesList!!.size
    }

    override fun onBindViewHolder(holder: RepliesViewHolder, position: Int) {
        try {
            Picasso.get().load(repliesList?.get(position)?.userPic?.clientApp).error(R.drawable.default_commentor_img).into(
                holder.replierImageView
            )
        } catch (e: Exception) {
            holder.replierImageView.setImageResource(R.drawable.default_commentor_img)
        }
        holder.commentDataTextView.text = AppUtils.createSpannableForMentionHandling(
            repliesList?.get(position)?.userId,
            repliesList?.get(position)?.userName,
            repliesList?.get(position)?.message,
            repliesList?.get(position)?.mentions,
            ContextCompat.getColor(holder.commentDataTextView.context, R.color.app_red)
        )
        holder.commentDataTextView.movementMethod = LinkMovementMethod.getInstance()
        holder.DateTextView.text =
            DateTimeUtils
                .getDateFromNanoMilliTimestamp(repliesList?.get(position)?.createdTime?.toLong()!!)
        if (repliesList?.get(position)?.liked!!) {
            val drawable =
                ContextCompat.getDrawable(holder.likeTextView.context, R.drawable.ic_like)
            holder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } else {
            val drawable =
                ContextCompat.getDrawable(holder.likeTextView.context, R.drawable.ic_like_grey)
            holder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }

        if (repliesList?.get(position)?.likeCount!! > 0) {
            holder.likeTextView.text = repliesList?.get(position)?.likeCount.toString()
        } else {
            holder.likeTextView.text = ""
        }
        holder.likeTextView.setOnClickListener {
            recyclerViewClickListner.onclick(it, position)
        }
        holder.moreOptionImageView.setOnClickListener {
            recyclerViewClickListner.onclick(it, position)
        }
        holder.replierImageView.setOnClickListener {
            recyclerViewClickListner.onclick(it, position)
        }
    }

    fun setRepliesList(repliesList: ArrayList<CommentListData>) {
        this.repliesList = repliesList
    }

    class RepliesViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val replierImageView = mView.replierImageView
        val commentDataTextView = mView.commentDataTextView
        val likeTextView = mView.likeTextView
        val DateTextView = mView.DateTextView
        val moreOptionImageView = mView.moreOptionImageView
    }

    interface RecyclerViewRepliesClickListner {
        fun onclick(v: View?, position: Int)
    }
}
