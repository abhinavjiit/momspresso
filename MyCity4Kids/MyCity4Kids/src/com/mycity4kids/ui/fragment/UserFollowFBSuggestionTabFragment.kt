package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.activity.phoneLogin.FBFriendsAdapter
import org.json.JSONObject

/**
 * Created by hemant.parmar on 21-04-2016.
 */
class UserFollowFBSuggestionTabFragment : BaseFragment(), View.OnClickListener, FBFriendsAdapter.RecyclerViewClickListener, IFacebookUser {
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var emptyList: TextView? = null
    var getFBFriendsTextView: TextView? = null
    private var userDynamoId: String? = null
    private var adapter: FBFriendsAdapter? = null
    private var callbackManager: CallbackManager? = null
    private val list = mutableListOf<FBObject>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_follow_fb_suggestion_tab_fragment, container, false)
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyList = view.findViewById(R.id.emptyList)
        progressBar = view.findViewById(R.id.progressBar)
        getFBFriendsTextView = view.findViewById(R.id.getFBFriendsTextView)

        getFBFriendsTextView?.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        adapter = FBFriendsAdapter(this)
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter
        return view
    }

    override fun getFacebookUser(jObject: JSONObject?, token: String?) {
        Log.d("FB Data", "+++" + jObject.toString())
        val arr = jObject?.getJSONObject("friends")?.getJSONArray("data")
        arr?.let {
            for (i in 0 until arr.length()) {
                list.add(FBObject(arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getString("name"), "0"))
            }
        }
        if (list.size > 0) {
            recyclerView?.visibility = View.VISIBLE
            emptyList?.visibility = View.GONE
        } else {
            recyclerView?.visibility = View.GONE
            emptyList?.visibility = View.VISIBLE
            emptyList?.text = "None of your friends are logged into momspresso using facebook"
        }
        adapter?.setListData(list)
        adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(activity, requestCode, resultCode, data)
    }

    data class FBObject(var id: String, var name: String, var followStatus: String)

    override fun updateUi(response: Response) {}
    override fun onClick(v: View) {
        FacebookUtils.facebookLogin(activity, this)
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.followTextView -> {
                list.get(position).followStatus = "1"
                adapter?.notifyDataSetChanged()
            }
            view.id == R.id.followingTextView -> {
                list.get(position).followStatus = "0"
                adapter?.notifyDataSetChanged()
            }

        }

    }
}
