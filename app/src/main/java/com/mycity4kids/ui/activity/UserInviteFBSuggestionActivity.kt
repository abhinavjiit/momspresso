package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.request.FacebookInviteFriendsRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FacebookInviteFriendsResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.adapter.FBInviteFriendsAdapter
import com.mycity4kids.utils.ToastUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInviteFBSuggestionActivity : BaseActivity(), View.OnClickListener,
    FBInviteFriendsAdapter.RecyclerViewClickListener, IFacebookUser {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var emptyList: TextView? = null
    var fbFriendsContainer: RelativeLayout? = null
    private var userDynamoId: String? = null
    private var adapter: FBInviteFriendsAdapter? = null
    private var callbackManager: CallbackManager? = null
    private val facebookFriendList = mutableListOf<FacebookInviteFriendsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_invite_fb_suggestion_activity)
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        recyclerView = findViewById(R.id.recyclerView)
        emptyList = findViewById(R.id.emptyList)
        progressBar = findViewById(R.id.progressBar)
        fbFriendsContainer = findViewById(R.id.fbFriendsContainer)

        fbFriendsContainer?.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        adapter = FBInviteFriendsAdapter(this)
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter

        val retrofit = BaseApplication.getInstance().retrofit
        val fbFriendsApi = retrofit.create(FollowAPI::class.java)
        val call = fbFriendsApi.getFacebookFriendsToInvite()
        call.enqueue(getFacebookFriendsResponseCallback)
    }

    override fun getFacebookUser(jObject: JSONObject?, token: String?) {
        Log.d("FB Data", "+++" + jObject.toString())
        val arr = jObject?.getJSONObject("friends")?.getJSONArray("data")

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

    private var getFacebookFriendsResponseCallback: Callback<FacebookInviteFriendsResponse> =
        object : Callback<FacebookInviteFriendsResponse> {
            override fun onResponse(
                call: Call<FacebookInviteFriendsResponse>,
                response: Response<FacebookInviteFriendsResponse>
            ) {
                if (response.body() == null) {
                    this@UserInviteFBSuggestionActivity.let {
                        ToastUtils.showToast(it, getString(R.string.went_wrong))
                    }
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    /*facebookFriendsResponse?.data?.get(0)?.friendList?.let {
                        facebookFriendList.addAll(
                            it
                        )
                    }*/
                    facebookFriendsResponse?.let { response ->
                        facebookFriendsResponse.data?.get(0)?.friendList?.let {
                            facebookFriendList.addAll(it)
                            if (facebookFriendList.isNotEmpty() && !facebookFriendsResponse.data.get(
                                    0
                                ).hasExpired) {
                                recyclerView?.visibility = View.VISIBLE
                                fbFriendsContainer?.visibility = View.GONE
                                emptyList?.visibility = View.GONE
                            } else {
                                recyclerView?.visibility = View.GONE
                                fbFriendsContainer?.visibility = View.VISIBLE
                                emptyList?.text =
                                    "None of your friends are logged into momspresso using facebook"
                            }
                            adapter?.setListData(it)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                } catch (e: Exception) {
                    //                    showToast(getString(R.string.server_went_wrong))
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FacebookInviteFriendsResponse>, t: Throwable) {
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        FacebookUtils.facebookLogin(this, this)
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.inviteTextView -> {
                facebookFriendList.get(position).isFollowing = "1"
                adapter?.notifyDataSetChanged()
                facebookFriendList.get(position).id?.let { hitInviteAPI(it) }
                //                facebookFriendList.get(position).id?.let { hitInviteAPI(it, "1") }
            }
            /*view.id == R.id.invitedTextView -> {
                facebookFriendList.get(position).isFollowing = "0"
                adapter?.notifyDataSetChanged()
                facebookFriendList.get(position).id?.let { hitFollowUnfollowAPI(it, "0") }
            }*/
            view.id == R.id.authorNameTextView || view.id == R.id.authorImageView -> {
                this.let {
                    val profileIntent = Intent(this, UserProfileActivity::class.java)
                    profileIntent.putExtra(Constants.USER_ID, facebookFriendList.get(position).id)
                    startActivity(profileIntent)
                }
            }
        }
    }

    private fun hitInviteAPI(authorId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val followAPI = retrofit.create(FollowAPI::class.java)
        val request = FacebookInviteFriendsRequest(authorId)
        //        request.notifiedUsers = authorId
        val inviteResponseCall = followAPI.inviteFBFriends(request)
        inviteResponseCall.enqueue(followUnfollowUserResponseCallback)
        /*this.let {
            Utils.pushProfileEvents(
                it, "CTA_Unfollow_Profile", "UserProfileActivity", "Unfollow", "-"
            )
        }*/
        /*val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        if ("0" == action) {
            val followUnfollowUserResponseCall = followAPI.inviteFBFriends(authorId)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
            this.let {
                Utils.pushProfileEvents(
                    it, "CTA_Unfollow_Profile", "UserProfileActivity", "Unfollow", "-"
                )
            }
        } else {
            val followUnfollowUserResponseCall = followAPI.followUserV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
            this.let {
                Utils.pushProfileEvents(
                    it, "CTA_Follow_Profile", "UserProfileActivity", "Follow", "-"
                )
            }
        }*/
    }

    object followUnfollowUserResponseCallback : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        }

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
        }
    }
}