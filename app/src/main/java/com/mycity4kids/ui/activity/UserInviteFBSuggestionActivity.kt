package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.request.FacebookInviteFriendsRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FacebookInviteFriendsResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.adapter.FBInviteFriendsAdapter
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInviteFBSuggestionActivity : BaseActivity(),
    FBInviteFriendsAdapter.RecyclerViewClickListener, IFacebookUser {

    private var recyclerView: RecyclerView? = null
    private lateinit var progressBar: ProgressBar
    private var emptyList: TextView? = null
    var fbFriendsContainer: RelativeLayout? = null
    private var userDynamoId: String? = null
    private var adapter: FBInviteFriendsAdapter? = null
    private var callbackManager: CallbackManager? = null
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private val facebookFriendList = mutableListOf<FacebookInviteFriendsData>()
    private var eventSuffix: String? = ""
    private var eventScreen: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_invite_fb_suggestion_activity)
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        recyclerView = findViewById(R.id.recyclerView)
        emptyList = findViewById(R.id.emptyList)
        progressBar = findViewById(R.id.progressBar)
        fbFriendsContainer = findViewById(R.id.fbFriendsContainer)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        eventScreen = intent.getStringExtra("eventScreen")
        eventSuffix = intent.getStringExtra("eventSuffix")

        callbackManager = CallbackManager.Factory.create()
        adapter = FBInviteFriendsAdapter(this)
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter

        progressBar.visibility = View.VISIBLE

        val retrofit = BaseApplication.getInstance().retrofit
        val fbFriendsApi = retrofit.create(FollowAPI::class.java)
        val call = fbFriendsApi.facebookFriendsToInvite
        call.enqueue(getFacebookFriendsResponseCallback)
    }

    private var getFacebookFriendsResponseCallback: Callback<FacebookInviteFriendsResponse> =
        object : Callback<FacebookInviteFriendsResponse> {
            override fun onResponse(
                call: Call<FacebookInviteFriendsResponse>,
                response: Response<FacebookInviteFriendsResponse>
            ) {
                if (response.body() == null) {
                    progressBar.visibility = View.GONE
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    facebookFriendsResponse?.let { response ->
                        if (response.data?.get(0)?.hasExpired!!) {
                            refreshFbToken()
                        } else {
                            progressBar.visibility = View.GONE
                            response.data[0].friendList?.let {
                                val map =
                                    SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext())
                                for (i in it.indices) {
                                    if (map.containsKey(it[i].id)) {
                                        it[i].isFollowing = "1"
                                    }
                                }
                                facebookFriendList.addAll(it)
                                if (facebookFriendList.isNotEmpty()) {
                                    recyclerView?.visibility = View.VISIBLE
                                    emptyList?.visibility = View.GONE
                                } else {
                                    recyclerView?.visibility = View.GONE
                                    emptyList?.text =
                                        getString(R.string.no_facebook_friends)
                                }
                                adapter?.setListData(it)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: Exception) {
                    progressBar.visibility = View.GONE
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FacebookInviteFriendsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    private fun refreshFbToken() {
        FacebookUtils.facebookLogin(this, this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.inviteButton -> {
                if (facebookFriendList[position].isInvited != "1") {
                    facebookFriendList[position].isInvited = "1"
                    adapter?.notifyDataSetChanged()
                    facebookFriendList[position].id?.let {
                        hitInviteAPI(it)

                        Utils.shareEventTracking(
                            this, eventScreen, "Invite_Android",
                            "CTA_Final_InviteFBFriend$eventSuffix"
                        )
                    }
                }
            }
            view.id == R.id.authorNameTextView || view.id == R.id.authorImageView -> {
                this.let {
                    val profileIntent = Intent(this, UserProfileActivity::class.java)
                    profileIntent.putExtra(Constants.USER_ID, facebookFriendList.get(position).id)
                    startActivity(profileIntent)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun hitInviteAPI(authorId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val followAPI = retrofit.create(FollowAPI::class.java)
        val friendInviteList = ArrayList<String>()
        friendInviteList.add(authorId)
        val request = FacebookInviteFriendsRequest(friendInviteList)
        val inviteResponseCall = followAPI.inviteFBFriends(request)
        inviteResponseCall.enqueue(followUnfollowUserResponseCallback)
    }

    private var followUnfollowUserResponseCallback: Callback<ResponseBody> =
        object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                try {
                    if (response.code() == 200 && response.isSuccessful) {
                        showToast(getString(R.string.follow_invite))
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        }
}
