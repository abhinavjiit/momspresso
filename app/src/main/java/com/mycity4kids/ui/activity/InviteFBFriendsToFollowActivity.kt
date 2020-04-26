package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.FacebookFriendsRequest
import com.mycity4kids.models.response.FacebookFriendsData
import com.mycity4kids.models.response.FacebookFriendsResponse
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
    private val facebookFriendList = ArrayList<FacebookFriendsData>()
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
                val call = fbFriendsApi.getFacebookFriends(facebookFriendsRequest)
                call.enqueue(getFacebookFriendsResponseCallback)
            }
        }
    }

    private var getFacebookFriendsResponseCallback: Callback<FacebookFriendsResponse> =
        object : Callback<FacebookFriendsResponse> {
            override fun onResponse(
                call: Call<FacebookFriendsResponse>,
                response: Response<FacebookFriendsResponse>
            ) {
                if (response.body() == null) {
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val facebookFriendsResponse = response.body()
                    facebookFriendsResponse?.let { response ->
                        response.data?.let {
                            if (it.isNullOrEmpty()) {
                                showToast("No friends")
                            } else {
                                facebookShareWidget.visibility = View.GONE
                                facebookFriendList.addAll(it)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<FacebookFriendsResponse>, t: Throwable) {
                Crashlytics.logException(t)
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
