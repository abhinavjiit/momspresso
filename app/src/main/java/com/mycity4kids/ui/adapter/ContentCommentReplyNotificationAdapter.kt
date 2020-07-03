package com.mycity4kids.ui.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.CommentListData
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.commentDataTextView.text = (
                (Html
                    .fromHtml(
                        "<b>" + "<font color=\"#D54058\">" + repliesList?.get(position)?.userName + "</font>" +
                            "</b>" +
                            " " +
                            "<font color=\"#4A4A4A\">" + repliesList?.get(position)?.message + "</font>", Html.FROM_HTML_MODE_LEGACY
                    ))
                )
        } else {
            holder.commentDataTextView.text = (
                (Html
                    .fromHtml(
                        "<b>" + "<font color=\"#D54058\">" + repliesList?.get(position)?.userName + "</font>" +
                            "</b>" +
                            " " +
                            "<font color=\"#4A4A4A\">" + repliesList?.get(position)?.message + "</font>"
                    ))
                )
        }
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
        holder.replierImageView.setOnClickListener{
            recyclerViewClickListner.onclick(it,position)
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
