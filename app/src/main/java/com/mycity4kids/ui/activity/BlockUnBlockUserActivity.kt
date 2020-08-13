package com.mycity4kids.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.BlockUserModel
import com.mycity4kids.models.UnBlockUserModel
import com.mycity4kids.models.response.FollowersFollowingResponse
import com.mycity4kids.models.response.FollowersFollowingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.adapter.BlockUnblockUserAdapter
import com.mycity4kids.utils.EndlessScrollListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockUnBlockUserActivity : BaseActivity(), BlockUnblockUserAdapter.RecyclerViewClickListener {
    private var blockUserList: ArrayList<FollowersFollowingResult>? = null
    private lateinit var blockUserRecyclerView: RecyclerView
    private lateinit var backImageView: ImageView
    private val adapter: BlockUnblockUserAdapter by lazy {
        BlockUnblockUserAdapter(this)
    }
    private var start = 0
    private lateinit var noBlockedUserTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.block_unblock_user_activity)
        blockUserRecyclerView = findViewById(R.id.blockUserRecyclerView)
        backImageView = findViewById(R.id.backImageView)
        noBlockedUserTextView = findViewById(R.id.noBlockedUserTextView)
        blockUserList = ArrayList()
        getBlockUserList()
        val linearLayoutManager = LinearLayoutManager(this)
        blockUserRecyclerView.layoutManager = linearLayoutManager
        blockUserRecyclerView.adapter = adapter
        adapter.setListData(blockUserList)
        adapter.notifyDataSetChanged()
        blockUserRecyclerView.addOnScrollListener(object :
            EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getBlockUserList()
            }
        })
        backImageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getBlockUserList() {
        val ret = BaseApplication.getInstance().retrofit
        val followApi = ret.create(FollowAPI::class.java)
        val call = followApi.getBlockUserList(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            start,
            10
        )
        call.enqueue(getBlockUserListCallBack)
    }

    private val getBlockUserListCallBack = object : Callback<FollowersFollowingResponse> {
        override fun onFailure(call: Call<FollowersFollowingResponse>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<FollowersFollowingResponse>,
            response: Response<FollowersFollowingResponse>
        ) {
            if (response.body() == null) {
                return
            }
            try {
                val resData = response.body()
                if (resData?.code == 200 && resData.status == Constants.SUCCESS) {
                    processData(resData.data.result)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
    }

    private fun processData(blockedUserList: ArrayList<FollowersFollowingResult>) {
        if (blockedUserList.isNullOrEmpty()) {
            if (!blockUserList.isNullOrEmpty()) {
                // last page reached
            } else {
                noBlockedUserTextView.visibility = View.VISIBLE
            }
        } else {
            noBlockedUserTextView.visibility = View.GONE
            if (start == 0) {
                blockUserList = blockedUserList
            } else {
                blockUserList?.addAll(blockedUserList)
            }
            blockUserList?.let {
                adapter.setListData(it)
                start += 10
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRecyclerClick(position: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)

        if (blockUserList?.get(position)?.isBLocked!!) {
            val blockUnBlockRequest = UnBlockUserModel(blockUserList?.get(position)?.userId)
            val call = articleDetailsApi.unBlockUserApi(blockUnBlockRequest)
            blockUserList?.get(position)?.isBLocked = false
            call.enqueue(blockUnBlockCallback)
        } else {
            val blockUnBlockRequest = BlockUserModel(blockUserList?.get(position)?.userId)
            val call = articleDetailsApi.blockUserApi(blockUnBlockRequest)
            blockUserList?.get(position)?.isBLocked = true
            call.enqueue(blockUnBlockCallback)
        }
        adapter.setListData(blockUserList)
        adapter.notifyDataSetChanged()
    }

    private val blockUnBlockCallback = object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            if (response.body() == null) {
                return
            }
            try {
                Log.d("Tag", "success")
            } catch (t: Exception) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }
    }
}
