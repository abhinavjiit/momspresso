package com.mycity4kids.ui.activity.phoneLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import org.json.JSONObject

class FacebookFriends : BaseActivity(), FBFriendsAdapter.RecyclerViewClickListener, View.OnClickListener, IFacebookUser {

    private var recyclerView: RecyclerView? = null
    private var adapter: FBFriendsAdapter? = null
    private var getFBFriendsTextView: TextView? = null
    private var callbackManager: CallbackManager? = null
    private val list = mutableListOf<FBObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facebook_friends)
        getFBFriendsTextView = findViewById(R.id.getFBFriendsTextView)
        recyclerView = findViewById(R.id.recyclerView)

        getFBFriendsTextView?.setOnClickListener(this)

        callbackManager = CallbackManager.Factory.create()

        adapter = FBFriendsAdapter(this)
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter

    }

    override fun onClick(v: View?) {
        FacebookUtils.facebookLogin(this, this)
    }

    override fun onClick(view: View, position: Int) {

    }

    override fun getFacebookUser(jObject: JSONObject?, token: String?) {
        Log.d("FB Data", "+++" + jObject.toString())
        val arr = jObject?.getJSONObject("friends")?.getJSONArray("data")
        arr?.let {
            for (i in 0 until arr.length()) {
                list.add(FBObject(arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getString("name")))
            }
        }
//        adapter?.setListData(list)
        adapter?.notifyDataSetChanged()
    }

    data class FBObject(var id: String, var name: String)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }
}