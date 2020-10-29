package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.CampaignTypeSelectionData
import com.mycity4kids.models.campaignmodels.CampaignListTypeResult
import com.mycity4kids.models.campaignmodels.DeliverableType
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.adapter.CampaignTypeListAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CampaignTourList : BaseActivity() {
    private var campaignTypeList = mutableListOf<DeliverableType>()
    private lateinit var bottomSheet: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CampaignTypeListAdapter
    private lateinit var next: TextView
    private lateinit var skip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_tour_list)
        bottomSheet = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        next = findViewById(R.id.next)
        skip = findViewById(R.id.skip)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        recyclerView = findViewById(R.id.recyclerview)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = CampaignTypeListAdapter(campaignTypeList, this)
        recyclerView.adapter = adapter
        fetchCampaignTypeList()

        skip.setOnClickListener {
            finish()
        }

        next.setOnClickListener {
            if (!adapter.campaignTypeList.isNullOrEmpty() && adapter.campaignTypeList!!.size > 0) {
                postCampaignPreference()
            } else {
                Toast.makeText(
                    this@CampaignTourList,
                    "Please select atleast 1 campaign",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun postCampaignPreference() {
        val retrofit = BaseApplication.getInstance().retrofit
        val campaignAPI = retrofit.create(CampaignAPI::class.java)
        val campaignpreferenceData = CampaignTypeSelectionData()
        campaignpreferenceData.userDeliverablesTypes = adapter.campaignTypeList
        val call =
            campaignAPI.postCampaignType(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                4,
                campaignpreferenceData
            )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (null == response.body()) {
                    val nee = NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }

                try {
                    val response = response.body()
                    val jsonObject = JSONObject(String(response?.bytes()!!))
                    val code = jsonObject.getInt("code")
                    val status = jsonObject.getString("status")
                    if (code == 200 && status == "success") {
                        val data = jsonObject.getJSONObject("data")
                        Toast.makeText(
                            this@CampaignTourList,
                            data.getString("msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                } catch (e: Exception) {
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MC4kException", Log.getStackTraceString(t))
                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(this@CampaignTourList, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCampaignTypeList() {
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getCampaignTypeList().subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<CampaignListTypeResult>> {

            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<CampaignListTypeResult>) {
                try {
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        val responseData = response.data!!.result.deliverable_types
                        if (responseData!!.size > 0) {
                            for (i in 0 until responseData.size) {
                                if (responseData.get(i).status.equals("1")) {
                                    campaignTypeList.add(responseData.get(i))
                                }
                            }
                            adapter.notifyDataSetChanged()
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }
}