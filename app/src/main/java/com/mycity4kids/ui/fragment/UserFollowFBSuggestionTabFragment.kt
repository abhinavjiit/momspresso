package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FacebookInviteFriendsResponse
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.phoneLogin.FBFriendsAdapter
import com.mycity4kids.utils.ToastUtils.showToast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by hemant.parmar on 21-04-2016.
 */
class UserFollowFBSuggestionTabFragment : BaseFragment(), View.OnClickListener,
    FBFriendsAdapter.RecyclerViewClickListener, IFacebookUser {
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var emptyList: TextView? = null
    var fbFriendsContainer: RelativeLayout? = null
    private var userDynamoId: String? = null
    private var adapter: FBFriendsAdapter? = null
    private var callbackManager: CallbackManager? = null
    private val facebookFriendList = mutableListOf<FacebookInviteFriendsData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.user_follow_fb_suggestion_tab_fragment, container, false)
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyList = view.findViewById(R.id.emptyList)
        progressBar = view.findViewById(R.id.progressBar)
        fbFriendsContainer = view.findViewById(R.id.fbFriendsContainer)

        fbFriendsContainer?.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        adapter = FBFriendsAdapter(this)
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter

        fbFriendsContainer?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE

        val retrofit = BaseApplication.getInstance().retrofit
        val fbFriendsApi = retrofit.create(FollowAPI::class.java)
        val call = fbFriendsApi.getFacebookFriendsToInvite()
        call.enqueue(getFacebookFriendsResponseCallback)

        return view
    }

    private var getFacebookFriendsResponseCallback: Callback<FacebookInviteFriendsResponse> =
        object : Callback<FacebookInviteFriendsResponse> {
            override fun onResponse(
                call: Call<FacebookInviteFriendsResponse>,
                response: Response<FacebookInviteFriendsResponse>
            ) {
                if (response.body() == null) {
                    activity?.let {
                        showToast(it, getString(R.string.went_wrong))
                    }
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    facebookFriendsResponse?.let { response ->
                        if (response.data?.get(0)?.hasExpired!!) {
                            fbFriendsContainer?.visibility = View.VISIBLE
                            progressBar?.visibility = View.GONE
                        } else {
                            progressBar?.visibility = View.GONE
                            response.data[0].friendList?.let {
                                facebookFriendList.addAll(it)
                                if (facebookFriendList.isNotEmpty()) {
                                    recyclerView?.visibility = View.VISIBLE
                                    emptyList?.visibility = View.GONE
                                } else {
                                    recyclerView?.visibility = View.GONE
                                    fbFriendsContainer?.visibility = View.GONE
                                    emptyList?.text =
                                        recyclerView?.context?.getString(R.string.no_facebook_friends)
                                }
                                adapter?.setListData(it)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FacebookInviteFriendsResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(activity, requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        FacebookUtils.facebookLogin(activity, this)
    }

    override fun getFacebookUser(jObject: JSONObject?, token: String?) {
        jObject?.let { json ->
            token?.let {
                val retrofit = BaseApplication.getInstance().retrofit
                val facebookFriendsRequest = FacebookFriendsRequest(it, json.getString("id"))
                val fbFriendsApi = retrofit.create(FollowAPI::class.java)
                val call = fbFriendsApi.getFacebookFriendsToInvite(facebookFriendsRequest)
                call.enqueue(getFacebookFriendsResponseCallback)
            }
        }
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.followTextView -> {
                facebookFriendList.get(position).isFollowing = "1"
                adapter?.notifyDataSetChanged()
                facebookFriendList.get(position).id?.let { hitFollowUnfollowAPI(it, "1") }
            }
            view.id == R.id.followingTextView -> {
                facebookFriendList.get(position).isFollowing = "0"
                adapter?.notifyDataSetChanged()
                facebookFriendList.get(position).id?.let { hitFollowUnfollowAPI(it, "0") }
            }
            view.id == R.id.authorNameTextView || view.id == R.id.authorImageView -> {
                activity?.let {
                    val profileIntent = Intent(activity, UserProfileActivity::class.java)
                    profileIntent.putExtra(Constants.USER_ID, facebookFriendList.get(position).id)
                    startActivity(profileIntent)
                }
            }
        }
    }

    private fun hitFollowUnfollowAPI(authorId: String, action: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val followAPI = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        if ("0" == action) {
            val followUnfollowUserResponseCall = followAPI.unfollowUserV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
            activity?.let {
                Utils.pushProfileEvents(
                    it, "CTA_Unfollow_Profile", "UserFollowFBSuggestionTabFragment", "Unfollow", "-"
                )
            }
        } else {
            val followUnfollowUserResponseCall = followAPI.followUserV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
            activity?.let {
                Utils.shareEventTracking(
                    it,
                    "Self Profile",
                    "Follow_Android",
                    "SelfProfile_FollowingSuggestions_Follow"
                )
            }
        }
    }

    object followUnfollowUserResponseCallback : Callback<FollowUnfollowUserResponse> {
        override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
        }

        override fun onResponse(
            call: Call<FollowUnfollowUserResponse>,
            response: Response<FollowUnfollowUserResponse>
        ) {
        }
    }
}
