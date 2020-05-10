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
import com.mycity4kids.gtmutils.Utils
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
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mom_vlog_follow_following_carousal.view.*
import kotlinx.android.synthetic.main.mom_vlog_listing_adapter.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val CATEGORY_VIDEOS = 0
const val CAROUSAL = 1

class MomVlogListingAdapter(val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var start: Int = 0
    var end: Int = 0
    var start_gold = 1
    var end_gold = 0
    var momVlogVideosOrCarousalList = ArrayList<VlogsListingAndDetailResult>()
    var alternateCarousal = 0
    private var vlogersListData = ArrayList<UserDetailResult>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIDEOS) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.mom_vlog_listing_adapter, parent, false)
            ListingViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mom_vlog_follow_following_carousal, parent, false)
            FollowFollowingCarousal(view)
        }
    }

    override fun getItemCount(): Int {
        return momVlogVideosOrCarousalList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListingViewHolder) {
            Picasso.get().load(momVlogVideosOrCarousalList[position].thumbnail)
                .error(R.drawable.default_article)
                .into(holder.articleImageView)
            holder.articleTitleTextView.text = momVlogVideosOrCarousalList[position].title
            holder.author_name.text =
                momVlogVideosOrCarousalList[position].author.firstName.plus(
                    " " +
                        momVlogVideosOrCarousalList[position].author.lastName
                )
            try {
                holder.recommendCountTextView1.text =
                    AppUtils.withSuffix(momVlogVideosOrCarousalList[position].like_count.toLong())
            } catch (e: Exception) {
                holder.recommendCountTextView1.text =
                    momVlogVideosOrCarousalList[position].like_count
            }
            try {
                holder.viewCountTextView1.text =
                    AppUtils.withSuffix(momVlogVideosOrCarousalList[position].view_count.toLong())
            } catch (e: Exception) {
                holder.viewCountTextView1.text = momVlogVideosOrCarousalList[position].view_count
            }
            when {

                momVlogVideosOrCarousalList[position].winner == 1 -> {
                    holder.imageWinner.setImageResource(R.drawable.ic_trophy)
                }
                momVlogVideosOrCarousalList[position].isIs_gold -> {
                    holder.imageWinner.setImageResource(R.drawable.ic_star_yellow)
                }
                else -> {
                    holder.imageWinner.visibility = View.GONE
                }
            }
            holder.container.setOnClickListener {
                val intent = Intent(mContext, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.VIDEO_ID, momVlogVideosOrCarousalList[position].id)
                intent.putExtra(Constants.STREAM_URL, momVlogVideosOrCarousalList[position].url)
                intent.putExtra(
                    Constants.AUTHOR_ID,
                    momVlogVideosOrCarousalList[position].author.id
                )
                intent.putExtra(Constants.FROM_SCREEN, "Funny Videos Listing")
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos")
                intent.putExtra(
                    Constants.AUTHOR,
                    momVlogVideosOrCarousalList.get(position).getAuthor().getId() + "~" + momVlogVideosOrCarousalList.get(
                        position
                    ).getAuthor().getFirstName() + " " + momVlogVideosOrCarousalList.get(
                        position
                    ).getAuthor().getLastName()
                )
                mContext.startActivity(intent)
            }
        } else if (holder is FollowFollowingCarousal) {
            Log.e(
                "TTTTT",
                "position = " + position + " careouselRunning --- " + momVlogVideosOrCarousalList[position].isCarouselRequestRunning + "  ----  list[position].isResponseReceived = " + momVlogVideosOrCarousalList[position].isResponseReceived
            )
            holder.scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT)
            if (!momVlogVideosOrCarousalList[position].isCarouselRequestRunning && !momVlogVideosOrCarousalList[position].isResponseReceived) {
                holder.shimmerLayout.startShimmerAnimation()
                holder.shimmerLayout.visibility = View.VISIBLE
                momVlogVideosOrCarousalList[position].isCarouselRequestRunning = true
                val pos = position
                val retrofit = BaseApplication.getInstance().retrofit
                val vlogsListingAndDetailsAPI =
                    retrofit.create(VlogsListingAndDetailsAPI::class.java)
                // if (alternateCarousal % 2 == 0) {
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
                            //  alternateCarousal++
                            if (response.isSuccessful && response.body() != null) {
                                val responseVlogersData = response.body()?.data?.result
                                processVlogersData(
                                    holder,
                                    responseVlogersData as ArrayList<UserDetailResult>?,
                                    pos
                                )
                                momVlogVideosOrCarousalList[pos].isCarouselRequestRunning = false
                                momVlogVideosOrCarousalList[pos].isResponseReceived = true
                            } else {
                                momVlogVideosOrCarousalList[pos].isCarouselRequestRunning = false
                                momVlogVideosOrCarousalList[pos].isResponseReceived = true
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
                // }
                /*  else if (alternateCarousal % 2 == 1) {
                      holder.shimmerLayout.startShimmerAnimation()
                      holder.shimmerLayout.visibility = View.VISIBLE
                      end_gold = start_gold + 5
                      val call = vlogsListingAndDetailsAPI.getGoldVlogersData(start_gold, end_gold, 1)
                      start_gold += end_gold
                      call.enqueue(object : Callback<MomVlogersDetailResponse> {
                          override fun onFailure(call: Call<MomVlogersDetailResponse>, t: Throwable) {
                          }

                          override fun onResponse(
                              call: Call<MomVlogersDetailResponse>,
                              response: Response<MomVlogersDetailResponse>
                          ) {
                              holder.shimmerLayout.stopShimmerAnimation()
                              holder.shimmerLayout.visibility = View.GONE
                              holder.scroll.visibility = View.VISIBLE
                              alternateCarousal++
                              if (response.isSuccessful && response.body() != null) {
                                  val responseVlogersData = response.body()?.data?.result
                                  processVlogersData(
                                      holder,
                                      responseVlogersData as ArrayList<UserDetailResult>?,
                                      pos
                                  )
                                  momVlogVideosOrCarousalList[pos].isCarouselRequestRunning = false
                                  momVlogVideosOrCarousalList[pos].isResponseReceived = true
                              } else {
                                  momVlogVideosOrCarousalList[pos].isCarouselRequestRunning = false
                                  momVlogVideosOrCarousalList[pos].isResponseReceived = true
                              }
                          }
                      })
                  }*/
            } else if (momVlogVideosOrCarousalList[position].isCarouselRequestRunning && !momVlogVideosOrCarousalList[position].isResponseReceived) {
            } else {
                Log.d(
                    "runningRequest",
                    momVlogVideosOrCarousalList[position].isCarouselRequestRunning.toString()
                )
                Log.d(
                    "runningRequest",
                    momVlogVideosOrCarousalList[position].isResponseReceived.toString()
                )
                if (!momVlogVideosOrCarousalList[position].carouselVideoList.isNullOrEmpty())
                    populateCarouselFollowFollowing(
                        holder,
                        momVlogVideosOrCarousalList[position].carouselVideoList
                    )
            }

            holder.carosalContainer1.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[0].dynamoId
                )
                mContext.startActivity(intent1)
            }

            holder.carosalContainer2.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[1].dynamoId
                )
                mContext.startActivity(intent1)
            }

            holder.carosalContainer3.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[2].dynamoId
                )
                mContext.startActivity(intent1)
            }
            holder.carosalContainer4.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[3].dynamoId
                )
                mContext.startActivity(intent1)
            }

            holder.carosalContainer5.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[4].dynamoId
                )
                mContext.startActivity(intent1)
            }

            holder.carosalContainer6.setOnClickListener {
                val intent1 = Intent(mContext, UserProfileActivity::class.java)
                intent1.putExtra(
                    Constants.USER_ID,
                    momVlogVideosOrCarousalList[position].carouselVideoList[5].dynamoId
                )
                mContext.startActivity(intent1)
            }

            holder.authorFollowTextView1.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[0].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[0].dynamoId,
                        position,
                        0,
                        holder.authorFollowTextView1
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[0].dynamoId,
                        position,
                        0,
                        holder.authorFollowTextView1
                    )
                }
            }
            holder.authorFollowTextView2.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[1].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[1].dynamoId,
                        position,
                        1,
                        holder.authorFollowTextView2
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[1].dynamoId,
                        position,
                        1,
                        holder.authorFollowTextView2
                    )
                }
            }
            holder.authorFollowTextView3.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[2].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[2].dynamoId,
                        position,
                        2,
                        holder.authorFollowTextView3
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[2].dynamoId,
                        position,
                        2,
                        holder.authorFollowTextView3
                    )
                }
            }
            holder.authorFollowTextView4.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[3].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[3].dynamoId,
                        position,
                        3,
                        holder.authorFollowTextView4
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[3].dynamoId,
                        position,
                        3,
                        holder.authorFollowTextView4
                    )
                }
            }
            holder.authorFollowTextView5.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[4].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[4].dynamoId,
                        position,
                        4,
                        holder.authorFollowTextView5
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[4].dynamoId,
                        position,
                        4,
                        holder.authorFollowTextView5
                    )
                }
            }
            holder.authorFollowTextView6.setOnClickListener {
                if (momVlogVideosOrCarousalList[position].carouselVideoList[5].following) {

                    unFollowApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[5].dynamoId,
                        position,
                        5,
                        holder.authorFollowTextView6
                    )
                } else {
                    followApiCall(
                        momVlogVideosOrCarousalList[position].carouselVideoList[5].dynamoId,
                        position,
                        5,
                        holder.authorFollowTextView6
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (momVlogVideosOrCarousalList[position].itemType == 0) {
            CATEGORY_VIDEOS
        } else {
            CAROUSAL
        }
    }

    fun setNewListData(
        newList: ArrayList<VlogsListingAndDetailResult>
    ) {
        this.momVlogVideosOrCarousalList = newList
    }

    private fun processVlogersData(
        holder: FollowFollowingCarousal,
        responseVlogersData: ArrayList<UserDetailResult>?,
        position: Int
    ) {
        if (!responseVlogersData.isNullOrEmpty()) {
            momVlogVideosOrCarousalList[position].carouselVideoList = responseVlogersData
            populateCarouselFollowFollowing(holder, responseVlogersData)
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
            followTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_BABABA))
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(2, ContextCompat.getColor(mContext, R.color.color_BABABA))
            myGrad.setColor(ContextCompat.getColor(mContext, R.color.white))
            followTextView.text =
                mContext.getString(R.string.ad_following_author).toLowerCase().capitalize()
        } else {
            followTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            val myGrad: GradientDrawable =
                followTextView.background as GradientDrawable
            myGrad.setStroke(2, ContextCompat.getColor(mContext, R.color.app_red))
            myGrad.setColor(ContextCompat.getColor(mContext, R.color.app_red))
            followTextView.text =
                mContext.getString(R.string.ad_follow_author).toLowerCase().capitalize()
        }
        authorNameTextView.text = carosalList.firstName.trim().toLowerCase().capitalize()
            .plus(" " + carosalList.lastName.trim().toLowerCase().capitalize())

        authorRanktextView.text =
            mContext.getString(R.string.myprofile_rank_label).toLowerCase().capitalize() + ": " + carosalList.rank
    }

    private fun unFollowApiCall(
        authorId: String,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        momVlogVideosOrCarousalList[position].carouselVideoList[index].following = false
        followFollowingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        val myGrad: GradientDrawable =
            followFollowingTextView.background as GradientDrawable
        myGrad.setStroke(2, ContextCompat.getColor(mContext, R.color.app_red))
        myGrad.setColor(ContextCompat.getColor(mContext, R.color.app_red))
        followFollowingTextView.text =
            mContext.getString(R.string.ad_follow_author).toLowerCase().capitalize()
        followFollowingTextView.text =
            mContext.getString(R.string.ad_follow_author).toLowerCase().capitalize()
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

                if (response.isSuccessful) {
                }
            }
        })
    }

    private fun followApiCall(
        authorId: String,
        position: Int,
        index: Int,
        followFollowingTextView: TextView
    ) {
        Utils.momVlogEvent(
            followFollowingTextView.context,
            "Video Listing",
            "Follow",
            "",
            "android",
            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            System.currentTimeMillis().toString(),
            "Following",
            "",
            ""
        )
        momVlogVideosOrCarousalList[position].carouselVideoList[index].following = true
        followFollowingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_BABABA))
        val myGrad: GradientDrawable =
            followFollowingTextView.background as GradientDrawable
        myGrad.setStroke(2, ContextCompat.getColor(mContext, R.color.color_BABABA))
        myGrad.setColor(ContextCompat.getColor(mContext, R.color.white))
        followFollowingTextView.text =
            mContext.getString(R.string.ad_following_author).toLowerCase().capitalize()
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
                if (response.isSuccessful) {
                }
            }
        })
    }

    class ListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val articleTitleTextView: TextView = view.articleTitleTextView
        val articleImageView: ImageView = view.articleImageView
        val author_name: TextView = view.author_name
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
