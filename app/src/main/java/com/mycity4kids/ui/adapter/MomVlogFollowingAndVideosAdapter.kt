package com.mycity4kids.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.MomVlogersDetailResponse
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.models.response.VlogsListingAndDetailResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.articleImageView
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.articleTitleTextView
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.author_name
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.imageWinner
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.recommendCountTextView1
import kotlinx.android.synthetic.main.follow_following_tab_vlog_adapter.view.viewCountTextView1
import kotlinx.android.synthetic.main.mom_vlog_follow_following_carousal.view.*
import kotlinx.android.synthetic.main.mom_vlog_listing_adapter.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val VIDEOS = 0
const val FOLLOWING_CAROUSAL = 1

class MomVlogFollowingAndVideosAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var momVlogVideosOrFollowingList: ArrayList<VlogsListingAndDetailResult>? = null
    private var vlogersListData = ArrayList<UserDetailResult>()
    var start: Int = 0
    var end: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIDEOS) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mom_vlog_listing_adapter, parent, false)
            MomVlogViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mom_vlog_follow_following_carousal, parent, false)
            FollowFollowingCarousal(view)
        }
    }

    override fun getItemCount(): Int {
        return if (momVlogVideosOrFollowingList == null) 0 else momVlogVideosOrFollowingList!!.size
    }

    fun setListData(res: ArrayList<VlogsListingAndDetailResult>?) {
        momVlogVideosOrFollowingList = res
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowFollowingCarousal) {
            Log.e(
                "TTTTT",
                "position = " + position +
                    " careouselRunnin --- " + momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning + "  ----  list[position].isResponseReceived = " + momVlogVideosOrFollowingList?.get(
                    position
                )?.isResponseReceived
            )
            holder.scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT)
            if (!momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning!! && !momVlogVideosOrFollowingList?.get(
                    position
                )?.isResponseReceived!!) {
                holder.shimmerLayout.startShimmerAnimation()
                holder.shimmerLayout.visibility = View.VISIBLE
                momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning = true
                val pos = position
                val retrofit = BaseApplication.getInstance().retrofit
                val vlogsListingAndDetailsAPI =
                    retrofit.create(VlogsListingAndDetailsAPI::class.java)
                end = start + 6
                val call = vlogsListingAndDetailsAPI.getVlogersData(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    start,
                    end,
                    1
                )
                start = end + 1
                call.enqueue(object : Callback<MomVlogersDetailResponse> {
                    override fun onFailure(call: Call<MomVlogersDetailResponse>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<MomVlogersDetailResponse>,
                        response: Response<MomVlogersDetailResponse>
                    ) {
                        try {
                            holder.shimmerLayout.stopShimmerAnimation()
                            holder.shimmerLayout.visibility = View.GONE
                            holder.scroll.visibility = View.VISIBLE
                            if (response.isSuccessful && response.body() != null) {
                                val responseVlogersData = response.body()?.data?.result
                                processVlogersData(
                                    holder,
                                    responseVlogersData as ArrayList<UserDetailResult>,
                                    pos
                                )
                                momVlogVideosOrFollowingList?.get(pos)?.isCarouselRequestRunning =
                                    false
                                momVlogVideosOrFollowingList?.get(pos)?.isResponseReceived = true
                            } else {
                                momVlogVideosOrFollowingList?.get(pos)?.isCarouselRequestRunning =
                                    false
                                momVlogVideosOrFollowingList?.get(pos)?.isResponseReceived = true
                            }
                        } catch (e: Exception) {
                            Crashlytics.logException(e)
                            Log.d(
                                "MC4kException",
                                Log.getStackTraceString(e)
                            )
                        }
                    }
                })
            } else if (momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning!! && !momVlogVideosOrFollowingList?.get(
                    position
                )?.isResponseReceived!!) {
                Log.d(
                    "Tag",
                    momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning.toString() + momVlogVideosOrFollowingList?.get(
                        position
                    )?.isResponseReceived.toString()
                )
            } else {
                Log.d(
                    "runningRequest",
                    momVlogVideosOrFollowingList?.get(position)?.isCarouselRequestRunning.toString()
                )
                Log.d(
                    "runningRequest",
                    momVlogVideosOrFollowingList?.get(position)?.isResponseReceived.toString()
                )

                momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.let {
                    populateCarouselFollowFollowing(
                        holder,
                        it
                    )
                }
            }

            holder.carosalContainer1.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(0)?.dynamoId
                )
                context.startActivity(intent1)
            }

            holder.carosalContainer2.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(1)?.dynamoId
                )
                context.startActivity(intent1)
            }

            holder.carosalContainer3.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(2)?.dynamoId
                )
                context.startActivity(intent1)
            }
            holder.carosalContainer4.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(3)?.dynamoId
                )
                context.startActivity(intent1)
            }

            holder.carosalContainer5.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(4)?.dynamoId
                )
                context.startActivity(intent1)
            }

            holder.carosalContainer6.setOnClickListener {
                val intent1 = Intent(context, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(5)?.dynamoId
                )
                context.startActivity(intent1)
            }
            holder.authorFollowTextView1.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(0)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(0)?.dynamoId,
                        position,
                        0,
                        holder.authorFollowTextView1
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(0)?.dynamoId,
                        position,
                        0,
                        holder.authorFollowTextView1
                    )
                }
            }
            holder.authorFollowTextView2.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(1)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(1)?.dynamoId,
                        position,
                        1,
                        holder.authorFollowTextView2
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(1)?.dynamoId,
                        position,
                        1,
                        holder.authorFollowTextView2
                    )
                }
            }
            holder.authorFollowTextView3.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(2)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(2)?.dynamoId,
                        position,
                        2,
                        holder.authorFollowTextView3
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(2)?.dynamoId,
                        position,
                        2,
                        holder.authorFollowTextView3
                    )
                }
            }
            holder.authorFollowTextView4.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(3)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(3)?.dynamoId,
                        position,
                        3,
                        holder.authorFollowTextView4
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(3)?.dynamoId,
                        position,
                        3,
                        holder.authorFollowTextView4
                    )
                }
            }
            holder.authorFollowTextView5.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(4)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(4)?.dynamoId,
                        position,
                        4,
                        holder.authorFollowTextView5
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(4)?.dynamoId,
                        position,
                        4,
                        holder.authorFollowTextView5
                    )
                }
            }
            holder.authorFollowTextView6.setOnClickListener {
                if (momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(5)?.following!!) {

                    unFollowApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(5)?.dynamoId,
                        position,
                        5,
                        holder.authorFollowTextView6
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(5)?.dynamoId,
                        position,
                        5,
                        holder.authorFollowTextView6
                    )
                }
            }
        } else if (holder is MomVlogViewHolder) {
            Picasso.get().load(momVlogVideosOrFollowingList?.get(position)?.thumbnail)
                .error(R.drawable.default_article)
                .into(holder.articleImageView)
            holder.articleTitleTextView.text = momVlogVideosOrFollowingList?.get(position)?.title
            holder.authorName.text =
                momVlogVideosOrFollowingList?.get(position)?.author?.firstName.plus(
                    " " +
                        momVlogVideosOrFollowingList?.get(position)?.author?.lastName
                )

            holder.recommendCountTextView1.text =
                momVlogVideosOrFollowingList?.get(position)?.like_count
            holder.viewCountTextView1.text = momVlogVideosOrFollowingList?.get(position)?.view_count
            when {
                momVlogVideosOrFollowingList?.get(position)?.winner == 1 -> {
                    holder.imageWinner.setImageResource(R.drawable.ic_trophy)
                }
                momVlogVideosOrFollowingList?.get(position)?.isIs_gold!! -> {
                    holder.imageWinner.setImageResource(R.drawable.ic_star_yellow)
                }
                else -> {
                    holder.imageWinner.visibility = View.GONE
                }
            }

            holder.container.setOnClickListener {
                val intent = Intent(context, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.VIDEO_ID, momVlogVideosOrFollowingList?.get(position)?.id)
                intent.putExtra(
                    Constants.STREAM_URL,
                    momVlogVideosOrFollowingList?.get(position)?.url
                )
                intent.putExtra(
                    Constants.AUTHOR_ID,
                    momVlogVideosOrFollowingList?.get(position)?.author?.id
                )
                intent.putExtra(Constants.FROM_SCREEN, "Funny Videos Listing")
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos")
                intent.putExtra(
                    Constants.AUTHOR,
                    momVlogVideosOrFollowingList?.get(position)?.author?.id + "~" + momVlogVideosOrFollowingList?.get(
                        position
                    )?.author?.firstName + " " + momVlogVideosOrFollowingList?.get(
                        position
                    )?.author?.lastName
                )
                context.startActivity(intent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (momVlogVideosOrFollowingList?.get(position)?.itemType == 0) {
            VIDEOS
        } else {

            FOLLOWING_CAROUSAL
        }
    }

    private fun processVlogersData(
        holder: FollowFollowingCarousal,
        responseVlogersData: ArrayList<UserDetailResult>,
        position: Int
    ) {
        try {
            if (!responseVlogersData.isEmpty()) {
                momVlogVideosOrFollowingList?.get(position)?.carouselVideoList = responseVlogersData
                momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.let {
                    populateCarouselFollowFollowing(
                        holder,
                        it
                    )
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun populateCarouselFollowFollowing(
        holder: FollowFollowingCarousal,
        carosalList: ArrayList<UserDetailResult>
    ) {
        if (carosalList.isEmpty()) {
            holder.scroll.visibility = View.GONE
            return
        } else {
            holder.scroll.visibility = View.VISIBLE
        }
        if (carosalList.size >= 1) {
            holder.carosalContainer1.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView1,
                holder.authorImageView1,
                holder.authorNameTextView1,
                holder.authorRankTextView1,
                carosalList[0]
            )
        }
        if (carosalList.size >= 2) {
            holder.carosalContainer2.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView2,
                holder.authorImageView2,
                holder.authorNameTextView2,
                holder.authorRankTextView2,
                carosalList[1]
            )
        }
        if (carosalList.size >= 3) {
            holder.carosalContainer3.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView3,
                holder.authorImageView3,
                holder.authorNameTextView3,
                holder.authorRankTextView3,
                carosalList[2]
            )
        }
        if (carosalList.size >= 4) {
            holder.carosalContainer4.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView4,
                holder.authorImageView4,
                holder.authorNameTextView4,
                holder.authorRankTextView4,
                carosalList[3]
            )
        }
        if (carosalList.size >= 5) {
            holder.carosalContainer5.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView5,
                holder.authorImageView5,
                holder.authorNameTextView5,
                holder.authorRankTextView5,
                carosalList[4]
            )
        }
        if (carosalList.size >= 6) {
            holder.carosalContainer6.visibility = View.VISIBLE
            updateCarosal(
                holder.authorFollowTextView6,
                holder.authorImageView6,
                holder.authorNameTextView6,
                holder.authorRankTextView6,
                carosalList[5]
            )
        }
        holder.scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT)
    }

    private fun updateCarosal(
        followTextView: TextView,
        authorImageView: ImageView,
        authorNameTextView: TextView,
        authorRanktextView: TextView,
        carosalList: UserDetailResult
    ) {

        Picasso.get().load(carosalList.profilePicUrl.clientApp).error(R.drawable.default_article)
            .into(authorImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                }
            })
        if (carosalList.following) {
            followTextView.setTextColor(ContextCompat.getColor(context, R.color.color_BABABA))
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(2, ContextCompat.getColor(context, R.color.color_BABABA))
            myGrad.setColor(ContextCompat.getColor(context, R.color.white))

            followTextView.text =
                context.getString(R.string.ad_following_author).toLowerCase().capitalize()
        } else {
            followTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(2, ContextCompat.getColor(context, R.color.app_red))
            myGrad.setColor(ContextCompat.getColor(context, R.color.app_red))

            followTextView.text =
                context.getString(R.string.ad_follow_author).toLowerCase().capitalize()
        }
        authorNameTextView.text = carosalList.firstName.trim().toLowerCase().capitalize()
            .plus(" " + carosalList.lastName.trim().toLowerCase().capitalize())

        authorRanktextView.text =
            context.resources.getString(R.string.myprofile_rank_label).toLowerCase().capitalize() + ":" + carosalList.rank
    }

    private fun unFollowApiCall(
        authorId: String?,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(index)?.following =
            false
        followFollowingTextView.text = context.getString(R.string.ad_follow_author)
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.unfollowUserV2(request)
        followUnfollowUserResponseCall.enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    private fun followApiCall(
        authorId: String?,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        momVlogVideosOrFollowingList?.get(position)?.carouselVideoList?.get(index)?.following = true
        followFollowingTextView.text = context.getString(R.string.ad_following_author)
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.followUserV2(request)
        followUnfollowUserResponseCall.enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    class MomVlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val articleTitleTextView: TextView = view.articleTitleTextView
        val articleImageView: ImageView = view.articleImageView
        val authorName: TextView = view.author_name
        val recommendCountTextView1: TextView = view.recommendCountTextView1
        val viewCountTextView1: TextView = view.viewCountTextView1
        val imageWinner: ImageView = view.imageWinner
        val container: RelativeLayout = view.container
    }

    class FollowFollowingCarousal(view: View) : RecyclerView.ViewHolder(view) {

        val shimmerLayout: ShimmerFrameLayout = view.shimmerLayout
        val carosalContainer1: LinearLayout = view.carosalContainer1
        val carosalContainer2: LinearLayout = view.carosalContainer2
        val carosalContainer3: LinearLayout = view.carosalContainer3
        val carosalContainer4: LinearLayout = view.carosalContainer4
        val carosalContainer5: LinearLayout = view.carosalContainer5
        val carosalContainer6: LinearLayout = view.carosalContainer6

        val scroll: HorizontalScrollView = view.scroll
        val authorImageView1: ImageView = view.authorImageView1
        val authorNameTextView1: TextView = view.authorNameTextView1
        val authorRankTextView1: TextView = view.authorRankTextView1
        val authorFollowTextView1: TextView = view.authorFollowTextView1

        val authorImageView2: ImageView = view.authorImageView2
        val authorNameTextView2: TextView = view.authorNameTextView2
        val authorRankTextView2: TextView = view.authorRankTextView2
        val authorFollowTextView2: TextView = view.authorFollowTextView2

        val authorImageView3: ImageView = view.authorImageView3
        val authorNameTextView3: TextView = view.authorNameTextView3
        val authorRankTextView3: TextView = view.authorRankTextView3
        val authorFollowTextView3: TextView = view.authorFollowTextView3

        val authorImageView4: ImageView = view.authorImageView4
        val authorNameTextView4: TextView = view.authorNameTextView4
        val authorRankTextView4: TextView = view.authorRankTextView4
        val authorFollowTextView4: TextView = view.authorFollowTextView4

        val authorImageView5: ImageView = view.authorImageView5
        val authorNameTextView5: TextView = view.authorNameTextView5
        val authorRankTextView5: TextView = view.authorRankTextView5
        val authorFollowTextView5: TextView = view.authorFollowTextView5

        val authorImageView6: ImageView = view.authorImageView6
        val authorNameTextView6: TextView = view.authorNameTextView6
        val authorRankTextView6: TextView = view.authorRankTextView6
        val authorFollowTextView6: TextView = view.authorFollowTextView6
    }
}
