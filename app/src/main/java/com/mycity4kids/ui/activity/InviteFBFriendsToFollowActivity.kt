package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.response.FacebookInviteFriendsData
import com.mycity4kids.models.response.FacebookInviteFriendsResponse
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.phoneLogin.FBFriendsAdapter
import kotlinx.android.synthetic.main.invite_fb_friends_follow_activity.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteFBFriendsToFollowActivity : BaseActivity(), FBFriendsAdapter.RecyclerViewClickListener,
    IFacebookUser, View.OnClickListener {
    private lateinit var adapter: FBFriendsAdapter
    private val facebookFriendList = ArrayList<FacebookInviteFriendsData>()
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.invite_fb_friends_follow_activity)

        facebookShareWidget.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        adapter = FBFriendsAdapter(this)
        adapter.setListData(facebookFriendList)
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()

        val retrofit = BaseApplication.getInstance().retrofit
        val fbFriendsApi = retrofit.create(FollowAPI::class.java)
        val call = fbFriendsApi.facebookFriendsToInvite
        call.enqueue(getFacebookFriendsResponseCallback)
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun getFacebookUser(jsonObject: JSONObject?, token: String?) {
        Log.d("FB Data", "+++" + jsonObject.toString())
        val arr = jsonObject?.getJSONObject("friends")?.getJSONArray("data")

        jsonObject?.let { json ->
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
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    facebookFriendsResponse?.let { response ->
                        facebookFriendsResponse.data?.get(0)?.friendList?.let {
                            if (it.isNullOrEmpty() && !facebookFriendsResponse.data.get(
                                    0
                                ).hasExpired) {
                                showToast("No friends")
                            } else {
                                facebookShareWidget.visibility = View.GONE
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
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onClick(view: View?) {
        FacebookUtils.facebookLogin(this, this)
    }
}
