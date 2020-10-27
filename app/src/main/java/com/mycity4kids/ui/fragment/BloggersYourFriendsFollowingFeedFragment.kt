package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.BloggersYourFriendsFollowingResponseModel
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.DashboardActivity
import com.mycity4kids.ui.activity.FindFbFriendsActivity
import com.mycity4kids.ui.activity.phoneLogin.FBFriendsAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BloggersYourFriendsFollowingFeedFragment : BaseFragment(),
    FBFriendsAdapter.RecyclerViewClickListener, View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FBFriendsAdapter
    private val facebookFriendList = ArrayList<FacebookInviteFriendsData>()
    private lateinit var skip: MomspressoButtonWidget
    private lateinit var back: TextView
    private lateinit var nextTextView: MomspressoButtonWidget


    companion object {
        @JvmStatic
        fun instance() = BloggersYourFriendsFollowingFeedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.friends_following_bloggers_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        skip = view.findViewById(R.id.skip)
        back = view.findViewById(R.id.back)
        nextTextView = view.findViewById(R.id.nextTextView)
        adapter = FBFriendsAdapter(this, "followFbFriends")
        adapter.setListData(facebookFriendList)
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        getBloggersYourFriendsFollowing()
        skip.setOnClickListener(this)
        back.setOnClickListener(this)
        nextTextView.setOnClickListener(this)
        return view
    }

    private fun getBloggersYourFriendsFollowing() {
        showProgressDialog("please wait")
        BaseApplication.getInstance().retrofit.create(FollowAPI::class.java).getBloggersList(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            3,
            0,
            10,
            1
        ).enqueue(bloggersListCallBack)
    }

    private val bloggersListCallBack =
        object : Callback<BloggersYourFriendsFollowingResponseModel> {
            override fun onResponse(
                call: Call<BloggersYourFriendsFollowingResponseModel>,
                response: Response<BloggersYourFriendsFollowingResponseModel>
            ) {
                removeProgressDialog()
                if (null == response.body()) {
                    return
                }

                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        res.data.result?.suggestion?.let {
                            AppUtils.updateFollowingStatusBloggersYourFriendsFollowing(it.toMutableList() as ArrayList<FacebookInviteFriendsData>)
                            facebookFriendList.addAll(it)
                            adapter.notifyDataSetChanged()
                        } ?: run {
                            ToastUtils.showToast(activity, "something went wrong")
                        }
                    }
                } catch (t: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            }

            override fun onFailure(
                call: Call<BloggersYourFriendsFollowingResponseModel>,
                t: Throwable
            ) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

        }


    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.followTextView -> {
                followApi(position)
            }
            R.id.followingTextView -> {
                unFollowApi(position)
            }
            R.id.authorImageView, R.id.authorNameTextView -> {
                val userProfileIntent = Intent(activity, UserProfileActivity::class.java)
                userProfileIntent.putExtra(Constants.USER_ID, facebookFriendList[position].id)
                startActivity(userProfileIntent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.skip, R.id.back -> {
                activity?.onBackPressed()
            }
            R.id.nextTextView -> {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("comingFor", "followingFeed")
                startActivity(intent)
            }
        }

    }


    private fun followApi(position: Int) {
        val followUserBody = FollowUnfollowUserRequest()
        followUserBody.followee_id = facebookFriendList[position].id
        BaseApplication.getInstance().retrofit.create(FollowAPI::class.java).followUserV2(
            followUserBody
        ).enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
                if (null == response.body()) {
                    ToastUtils.showToast(activity, "something went wrong")
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        ToastUtils.showToast(activity, res.data.msg)
                        facebookFriendList[position].isFollowing = "1"
                        adapter.notifyDataSetChanged()
                        (activity as FindFbFriendsActivity).syncFollowingList()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FollowUnfollowUserResponse>, e: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun unFollowApi(position: Int) {
        val followUserBody = FollowUnfollowUserRequest()
        followUserBody.followee_id = facebookFriendList[position].id
        BaseApplication.getInstance().retrofit.create(FollowAPI::class.java).unfollowUserV2(
            followUserBody
        ).enqueue(object : Callback<FollowUnfollowUserResponse> {
            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
                if (null == response.body()) {
                    ToastUtils.showToast(activity, "something went wrong")
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        ToastUtils.showToast(activity, res.data.msg)
                        facebookFriendList[position].isFollowing = "0"
                        adapter.notifyDataSetChanged()
                        (activity as FindFbFriendsActivity).syncFollowingList()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FollowUnfollowUserResponse>, e: Throwable) {

                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        })
    }
}