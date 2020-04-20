package com.mycity4kids.ui.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.UserDetailResult
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mom_vlog_follow_following_adaptr.view.*

class MomVLogFollowFollowingAdapter(
    var recyclerViewClickListner: FollowFollowingRecyclerViewClickListner,
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var vlogersDetailData = ArrayList<UserDetailResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mom_vlog_follow_following_adaptr, parent, false)
        return FollowFollowingCarousal(view, recyclerViewClickListner)
    }

    override fun getItemCount(): Int {
        return vlogersDetailData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowFollowingCarousal) {
            holder.authorName.text =
                (vlogersDetailData[position].firstName).toLowerCase().capitalize().trim()
                    .plus(" " + (vlogersDetailData[position].lastName).toLowerCase().capitalize().trim())

            Picasso.get().load(vlogersDetailData[position].profilePicUrl.clientApp)
                .error(R.drawable.default_article).into(holder.authorImageView, object : Callback {
                    override fun onSuccess() {
                        holder.homeProgress.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        holder.homeProgress.visibility = View.VISIBLE
                    }
                })
            if (vlogersDetailData[position].following) {

                holder.followTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.ad_author_name_text
                    )
                )
                val myGrad: GradientDrawable =
                    holder.followTextView.background as GradientDrawable
                myGrad.setStroke(
                    2,
                    ContextCompat.getColor(holder.itemView.context, R.color.ad_author_name_text)
                )
                holder.followTextView.text =
                    context.resources.getString(R.string.ad_following_author)
            } else {
                val myGrad: GradientDrawable =
                    holder.followTextView.background as GradientDrawable
                myGrad.setStroke(
                    2,
                    ContextCompat.getColor(holder.itemView.context, R.color.app_red)
                )
                holder.followTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.app_red
                    )
                )
                holder.followTextView.text = context.resources.getString(R.string.ad_follow_author)
            }
            holder.rank.text =
                context.resources.getString(R.string.myprofile_rank_label) + ": " + vlogersDetailData[position].rank

            holder.followTextView.setOnClickListener {
                recyclerViewClickListner.recyclerViewClick(position, it)
            }
        }
    }

    fun setVlogersData(list: ArrayList<UserDetailResult>) {
        vlogersDetailData = list
    }

    class FollowFollowingCarousal(
        view: View,
        recyclerViewClickListner: FollowFollowingRecyclerViewClickListner
    ) : RecyclerView.ViewHolder(view) {
        val authorName: TextView = view.authorName
        val authorImageView: ImageView = view.authorImageView
        val homeProgress: ProgressBar = view.homeProgress
        val followTextView: TextView = view.followTextView
        val rank: TextView = view.rank
    }

    interface FollowFollowingRecyclerViewClickListner {
        fun recyclerViewClick(position: Int, view: View)
    }
}
