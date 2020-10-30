package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FacebookInviteFriendsResponse
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.FindFbFriendsActivity
import com.mycity4kids.ui.activity.phoneLogin.FBFriendsAdapter
import com.mycity4kids.utils.ToastUtils.showToast
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.ShareButtonWidget
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindFbFriendsFragment : BaseFragment(), IFacebookUser,
    FBFriendsAdapter.RecyclerViewClickListener, View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var back: TextView
    private lateinit var nextTextView: MomspressoButtonWidget
    private val facebookFriendList = ArrayList<FacebookInviteFriendsData>()
    private var callbackManager: CallbackManager? = null
    private lateinit var facebookShareWidget: ShareButtonWidget
    private lateinit var fbFriendListContainer: ConstraintLayout
    private lateinit var adapter: FBFriendsAdapter
    private lateinit var connectFbContainer: ConstraintLayout
    private lateinit var skipForNowTextView: TextView
    private lateinit var skip: MomspressoButtonWidget

    companion object {
        @JvmStatic
        fun instance() = FindFbFriendsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.find_fb_friends_fragment, container, false)
        nextTextView = view.findViewById(R.id.nextTextView)
        facebookShareWidget = view.findViewById(R.id.facebookShareWidget)
        fbFriendListContainer = view.findViewById(R.id.fbFriendListContainer)
        recyclerView = view.findViewById(R.id.recyclerView)
        connectFbContainer = view.findViewById(R.id.connectFbContainer)
        skipForNowTextView = view.findViewById(R.id.skipForNowTextView)
        skip = view.findViewById(R.id.skip)
        back = view.findViewById(R.id.back)
        callbackManager = CallbackManager.Factory.create()
        adapter = FBFriendsAdapter(this, "followFbFriends")
        adapter.setListData(facebookFriendList)
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        nextTextView.setOnClickListener {
            activity?.let {
                val onNextButtonClick = it as OnNextButtonClick
                onNextButtonClick.onClick()
            }
        }
        facebookShareWidget.setOnClickListener {
            activity?.let {
                FacebookUtils.facebookLogin(it as FindFbFriendsActivity, this)
            }
        }
        getFbFriends()
        back.setOnClickListener(this)
        skip.setOnClickListener(this)
        skipForNowTextView.setOnClickListener(this)
        return view
    }

    private fun getFbFriends() {
        showProgressDialog("getting Facebook Friends")
        val retrofit = BaseApplication.getInstance().retrofit
        val fbFriendsApi = retrofit.create(FollowAPI::class.java)
        val call = fbFriendsApi.facebookFriendsToInvite
        call.enqueue(getFacebookFriendsResponseCallback)
    }


    override fun getFacebookUser(jsonObject: JSONObject?, token: String?) {
        Log.d("FB Data", "+++" + jsonObject.toString())
        val arr = jsonObject?.getJSONObject("friends")?.getJSONArray("data")
        jsonObject?.let { json ->
            token?.let {
                showProgressDialog("getting Facebook Friends")
                val retrofit = BaseApplication.getInstance().retrofit
                val facebookFriendsRequest = FacebookFriendsRequest(it, json.getString("id"))
                val fbFriendsApi = retrofit.create(FollowAPI::class.java)
                val call = fbFriendsApi.getFacebookFriendsToInvite(facebookFriendsRequest)
                call.enqueue(getFacebookFriendsResponseCallback)
            }
        }
    }


    private var getFacebookFriendsResponseCallback: Callback<FacebookInviteFriendsResponse> =
        object : Callback<FacebookInviteFriendsResponse> {
            override fun onResponse(
                call: Call<FacebookInviteFriendsResponse>,
                response: Response<FacebookInviteFriendsResponse>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    showToast(activity, getString(R.string.went_wrong))
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    facebookFriendsResponse?.let { response ->
                        facebookFriendsResponse.data?.get(0)?.friendList?.let {
                            if (it.isNullOrEmpty() && !facebookFriendsResponse.data[0].hasExpired) {
                                activity?.let {
                                    showToast(activity, "no friends")
                                }
                            } else if (facebookFriendsResponse.data[0].hasExpired) {
                                connectFbContainer.visibility = View.VISIBLE
                            } else {
                                connectFbContainer.visibility = View.GONE
                                fbFriendListContainer.visibility = View.VISIBLE
                                facebookFriendList.addAll(it)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FacebookInviteFriendsResponse>, t: Throwable) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity?.let {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
            FacebookUtils.onActivityResult(it, requestCode, resultCode, data)
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
                    showToast(activity, "something went wrong")
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        showToast(activity, res.data.msg)
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
                    showToast(activity, "something went wrong")
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        showToast(activity, res.data.msg)
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

    interface OnNextButtonClick {
        fun onClick()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.skipForNowTextView, R.id.skip, R.id.back -> {
                activity?.onBackPressed()
            }
        }
    }

}