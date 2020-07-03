package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.GroupsMembershipResponse
import com.mycity4kids.models.response.NotificationCenterResult
import com.mycity4kids.ui.GroupMembershipStatus.IMembershipStatus
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso
import java.util.ArrayList

/**
 * Created by hemant on 21/12/16.
 */

const val NOTIFICATION_ITEM = 0
const val RECENT_SECTION_HEADER = 1
const val EARLIER_SECTION_HEADER = 2

class NotificationCenterRecyclerAdapter(
    private val recyclerViewClickListener: RecyclerViewClickListener,
    private val notificationList: ArrayList<NotificationCenterResult>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IMembershipStatus {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {
            RECENT_SECTION_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.notification_center_section_item,
                    parent, false
                )
                return RecentHeaderViewHolder(
                    view
                )
            }
            EARLIER_SECTION_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.notification_center_section_item,
                    parent, false
                )
                return EarlierHeaderViewHolder(
                    view
                )
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.notification_center_recycler_item,
                    parent, false
                )
                return NotificationViewHolder(
                    view
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is RecentHeaderViewHolder -> {
            }
            is EarlierHeaderViewHolder -> {
            }
            is NotificationViewHolder -> {
                if (notificationList[position].title.isNullOrBlank()) {
                    holder.notificationBodyTextView.text =
                        AppUtils.fromHtml(notificationList[position].htmlBody)
                } else {
                    holder.notificationBodyTextView.text =
                        AppUtils.fromHtml("<b>" + notificationList[position].title + "</b> " + notificationList[position].htmlBody)
                }

                holder.notificationDateTextView.text = "" + DateTimeUtils.getMMMDDFormatDate(
                    notificationList[position].createdTime
                )

                try {
                    if (!StringUtils.isNullOrEmpty(notificationList[position].thumbNail)) {
                        holder.contentImageView.visibility = View.VISIBLE
                        Picasso.get().load(notificationList[position].thumbNail)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(holder.contentImageView)
                    } else {
                        holder.contentImageView.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    holder.contentImageView.visibility = View.GONE
                }

                val notifType = notificationList[position].notifType
                holder.actionButtonWidget.visibility = View.VISIBLE
                holder.multipleUserImageContainer.visibility = View.VISIBLE
                if (notificationList[position].notifiedBy != null &&
                    notificationList[position].notifiedBy.size > 1) {
                    holder.singleUserImageView.visibility = View.GONE
                    holder.userImageView1.visibility = View.VISIBLE
                    holder.userImageView2.visibility = View.VISIBLE
                    try {
                        Picasso.get().load(
                            notificationList[position].notifiedBy[0].userProfilePicUrl
                                .clientAppMin
                        )
                            .placeholder(R.drawable.default_blogger_profile_img)
                            .error(R.drawable.default_blogger_profile_img)
                            .into(holder.userImageView1)
                    } catch (e: Exception) {
                        holder.userImageView1.setImageResource(R.drawable.default_blogger_profile_img)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                    try {
                        Picasso.get().load(
                            notificationList[position].notifiedBy[1].userProfilePicUrl
                                .clientAppMin
                        )
                            .placeholder(R.drawable.default_blogger_profile_img)
                            .error(R.drawable.default_blogger_profile_img)
                            .into(holder.userImageView2)
                    } catch (e: Exception) {
                        holder.userImageView2.setImageResource(R.drawable.default_blogger_profile_img)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                } else if (notificationList[position].notifiedBy != null &&
                    notificationList[position].notifiedBy.size == 1) {
                    holder.singleUserImageView.visibility = View.VISIBLE
                    holder.userImageView1.visibility = View.GONE
                    holder.userImageView2.visibility = View.GONE
                    try {
                        Picasso.get().load(
                            notificationList[position].notifiedBy[0].userProfilePicUrl
                                .clientAppMin
                        )
                            .placeholder(R.drawable.default_blogger_profile_img)
                            .error(R.drawable.default_blogger_profile_img)
                            .into(holder.singleUserImageView)
                    } catch (e: Exception) {
                        holder.singleUserImageView.setImageResource(R.drawable.default_blogger_profile_img)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                } else {
                    holder.singleUserImageView.visibility = View.GONE
                    holder.userImageView1.visibility = View.GONE
                    holder.userImageView2.visibility = View.GONE
                }
                if (AppConstants.NOTIFICATION_CENTER_PROFILE == notifType) {
                    if (!notificationList[position].notifiedBy.isNullOrEmpty() &&
                        notificationList[position].followBack == "1") {
                        holder.actionButtonWidget.tag =
                            AppConstants.NOTIFICATION_ACTION_FOLLOW_AUTHOR
                        if (notificationList[position].serviceType == "followers_mapping") {
                            if (notificationList[position].isFollowing) {
                                holder.actionButtonWidget.isSelected = false
                                holder.actionButtonWidget.setText(
                                    holder.actionButtonWidget.context.getString(
                                        R.string.all_following
                                    )
                                )
                            } else {
                                holder.actionButtonWidget.isSelected = true
                                holder.actionButtonWidget.setText("Follow Back")
                            }
                        } else {
                            if (notificationList[position].isFollowing) {
                                holder.actionButtonWidget.isSelected = false
                                holder.actionButtonWidget.setText(
                                    holder.actionButtonWidget.context.getString(
                                        R.string.all_following
                                    )
                                )
                            } else {
                                holder.actionButtonWidget.isSelected = true
                                holder.actionButtonWidget.setText(
                                    holder.actionButtonWidget.context.getString(
                                        R.string.all_follow
                                    )
                                )
                            }
                        }
                        holder.actionButtonWidget.visibility = View.VISIBLE
                    }
                    // else if (!notificationList[position].badgeId.isNullOrBlank()) {
                    //     holder.actionButtonWidget.tag = AppConstants.NOTIFICATION_ACTION_SHARE_BADGE
                    //     holder.actionButtonWidget.setText(holder.actionButtonWidget.context.getString(R.string.ad_bottom_bar_generic_share))
                    //     holder.actionButtonWidget.visibility = View.VISIBLE
                    // } else if (!notificationList[position].milestoneId.isNullOrBlank()) {
                    //     holder.actionButtonWidget.tag = AppConstants.NOTIFICATION_ACTION_SHARE_MILESTONE
                    //     holder.actionButtonWidget.setText(holder.actionButtonWidget.context.getString(R.string.ad_bottom_bar_generic_share))
                    //     holder.actionButtonWidget.visibility = View.VISIBLE
                    // }
                    else {
                        holder.actionButtonWidget.tag = null
                        holder.actionButtonWidget.visibility = View.GONE
                    }
                }
                // else if (AppConstants.NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL == notifType) {
                //     holder.actionButtonWidget.tag = AppConstants.NOTIFICATION_ACTION_VLOG_CHALLENGE
                //     holder.actionButtonWidget.setText(holder.actionButtonWidget.context.getString(R.string.participate))
                //     holder.actionButtonWidget.visibility = View.VISIBLE
                // } else if (AppConstants.NOTIFICATION_CENTER_STORY_CHALLENGE_DETAIL == notifType) {
                //     holder.actionButtonWidget.tag = AppConstants.NOTIFICATION_ACTION_STORY_CHALLENGE
                //     holder.actionButtonWidget.setText(holder.actionButtonWidget.context.getString(R.string.participate))
                //     holder.actionButtonWidget.visibility = View.VISIBLE
                // }
                else {
                    holder.actionButtonWidget.tag = null
                    holder.actionButtonWidget.visibility = View.GONE
                }
                if (AppConstants.NOTIFICATION_STATUS_UNREAD == notificationList[position].isRead) {
                    holder.rootView
                        .setBackgroundColor(
                            ContextCompat
                                .getColor(
                                    holder.rootView.context,
                                    R.color.notification_center_unread_bg
                                )
                        )
                } else {
                    holder.rootView
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                holder.rootView.context,
                                R.color.notification_center_read_bg
                            )
                        )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            AppConstants.NOTIFICATION_CENTER_RECENT_HEADER == notificationList[position].recyclerItemType -> RECENT_SECTION_HEADER
            AppConstants.NOTIFICATION_CENTER_EARLIER_HEADER == notificationList[position].recyclerItemType -> EARLIER_SECTION_HEADER
            else -> NOTIFICATION_ITEM
        }
    }

    inner class RecentHeaderViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        var sectionTextView: TextView = view.findViewById(R.id.sectionTextView)

        init {
            sectionTextView.text =
                sectionTextView.context.getString(R.string.article_listing_type_recent_label).toLowerCase().capitalize()
        }
    }

    inner class EarlierHeaderViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        var sectionTextView: TextView = view.findViewById(R.id.sectionTextView)

        init {
            sectionTextView.text = sectionTextView.context.getString(R.string.all_earlier)
        }
    }

    inner class NotificationViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var rootView: RelativeLayout
        var notificationBodyTextView: TextView
        var notificationDateTextView: TextView
        var contentImageView: ImageView
        var userImageView1: ImageView
        var userImageView2: ImageView
        var singleUserImageView: ImageView
        var multipleUserImageContainer: FrameLayout
        var actionButtonWidget: MomspressoButtonWidget
        override fun onClick(v: View) {
            recyclerViewClickListener.onNotificationItemClick(v, adapterPosition)
        }

        init {
            rootView =
                view.findViewById(R.id.rootView)
            notificationBodyTextView =
                view.findViewById(R.id.notificationBodyTextView)
            notificationDateTextView =
                view.findViewById(R.id.notificationDateTextView)
            contentImageView =
                view.findViewById(R.id.contentImageView)
            userImageView1 =
                view.findViewById(R.id.userImageView1)
            userImageView2 =
                view.findViewById(R.id.userImageView2)
            singleUserImageView =
                view.findViewById(R.id.singleUserImageView)
            multipleUserImageContainer =
                view.findViewById(R.id.multipleUserImageContainer)
            actionButtonWidget = view.findViewById(R.id.actionButtonWidget)
            rootView.setOnClickListener(this)
            actionButtonWidget.setOnClickListener(this)
        }
    }

    override fun onMembershipStatusFetchSuccess(
        body: GroupsMembershipResponse,
        groupId: Int
    ) {
    }

    override fun onMembershipStatusFetchFail() {}
    interface RecyclerViewClickListener {
        fun onNotificationItemClick(view: View, adapterPosition: Int)
    }
}
