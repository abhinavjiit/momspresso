package com.mycity4kids.ui.campaign.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.*
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.adapter.FaqRecyclerAdapter
import com.mycity4kids.ui.campaign.adapter.MediaProofRecyclerAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val SELECT_IMAGE = 1005

class CampaignAddProofFragment : BaseFragment(), MediaProofRecyclerAdapter.ClickListener {
    fun onProofSubmitClick() {
        if(!(addLinkOrUrlEditTextView1.text.isNullOrEmpty() && addLinkOrUrlEditTextView2.text.isNullOrEmpty() && addLinkOrUrlEditTextView3.text.isNullOrEmpty())){
            var urlList = mutableListOf<String>()
            if(!addLinkOrUrlEditTextView1.text.isNullOrEmpty()){
                urlList.add(addLinkOrUrlEditTextView1.text.toString())
            }
            if(!addLinkOrUrlEditTextView2.text.isNullOrEmpty()){
                urlList.add(addLinkOrUrlEditTextView2.text.toString())
            }
            if(!addLinkOrUrlEditTextView3.text.isNullOrEmpty()){
                urlList.add(addLinkOrUrlEditTextView3.text.toString())
            }

            if(!urlList.isNullOrEmpty()){
                (urlList).forEach {
                    var proofPostModel = ProofPostModel(url = it, campaign_id = campaignId)
                    postProofToServer(proofPostModel)
                }

            }
        }
    }


    override fun onCellClick() {
        val intent = Intent()
        intent.setType("image/*")
//        intent.setType("file/*") //this is used for open file manager
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE)
    }

    override fun onProofDelete(cellIndex: Int) {
        if (!campaignProofList.get(cellIndex).url.isNullOrEmpty()) {
            deleteProof(campaignProofList.get(cellIndex).id!!)
        }
    }

    override fun updateUi(response: Response?) {
    }

    private var columnCount = 1
    private var faqs: ArrayList<QuestionAnswerResponse> = arrayListOf()
    private lateinit var faqRecyclerAdapter: FaqRecyclerAdapter
    private lateinit var mediaProofRecyclerAdapter: MediaProofRecyclerAdapter
    private lateinit var recyclerFaqs: RecyclerView
    private lateinit var recyclerMediaProof: RecyclerView
    private var campaignProofList: ArrayList<CampaignProofResponse> = arrayListOf()
    private var campaignId: Int = 15
    private lateinit var textSubmit : TextView
    private lateinit var addLinkOrUrlEditTextView1 : TextView
    private lateinit var addLinkOrUrlEditTextView2 : TextView
    private lateinit var addLinkOrUrlEditTextView3 : TextView

    companion object {
        @JvmStatic
        fun newInstance(id: Int, deliverableTypeList: ArrayList<Int>) =
                CampaignAddProofFragment().apply {
                    arguments = Bundle().apply {
                        this.putInt("id", id)
                        this.putIntegerArrayList("deliverableTypeList", deliverableTypeList)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        recyclerFaqs = view.findViewById<RecyclerView>(R.id.recyclerFaqs)

        recyclerFaqs.layoutManager = LinearLayoutManager(context)

        textSubmit = view.findViewById<TextView>(R.id.textSubmit)
        textSubmit.setOnClickListener {
            onProofSubmitClick()
        }

        faqRecyclerAdapter = FaqRecyclerAdapter(faqs, activity as Context)
        recyclerFaqs.adapter = faqRecyclerAdapter

        addLinkOrUrlEditTextView1 = view.findViewById<TextView>(R.id.addLinkOrUrlEditTextView1)
        addLinkOrUrlEditTextView2 = view.findViewById<TextView>(R.id.addLinkOrUrlEditTextView2)
        addLinkOrUrlEditTextView3 = view.findViewById<TextView>(R.id.addLinkOrUrlEditTextView3)

        recyclerMediaProof = view.findViewById<RecyclerView>(R.id.recyclerMediaProof)
        recyclerMediaProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mediaProofRecyclerAdapter = MediaProofRecyclerAdapter(campaignProofList, this)
        recyclerMediaProof.adapter = mediaProofRecyclerAdapter

        /*fetch faq data from server*/
        fetchFaq()

        /*fetch submission data from server*/
        fetSubmissionDetail()

        return view
    }

    /*fetch data from server*/
    private fun fetchFaq() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getFaqsList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<FaqResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<FaqResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null && response.data!!.result.faqs!!.isNotEmpty()) {
                    faqs.clear()
                    faqs.addAll(response.data!!.result.faqs as ArrayList)
                    faqRecyclerAdapter.notifyDataSetChanged()
                    Log.e("adapter size is ", faqRecyclerAdapter.itemCount.toString())
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    /*fetch data from server for submission*/
    private fun fetSubmissionDetail() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getSubmissionDetail(campaignId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null && !response.data!!.result.campaignProofResponse.isNullOrEmpty()) {
                    campaignProofList.addAll(response.data!!.result!!.campaignProofResponse!!)
                    mediaProofRecyclerAdapter.notifyDataSetChanged()
                    Log.e("response", Gson().toJson(response.data!!.result.campaignProofResponse))
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    /*Post proof on server*/
    private fun postProofToServer(proofPostModel: ProofPostModel) {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).postProofToServer(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    /*Delete proof on server*/
    private fun deleteProof(proofId: Int) {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).deleteProofById(proofId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                    var campaignProofListLocal = campaignProofList.filter { it.id!=proofId }
                    campaignProofList.clear()
                    campaignProofList.addAll(campaignProofListLocal)
                    mediaProofRecyclerAdapter.notifyDataSetChanged()
                    Log.e("response", Gson().toJson(response.data!!))
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        val storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com")
                        var file = File(data.getData().getPath());//create path from uri
                        var split = file.getPath().split(":");//split the path.
                        var path = split[1];
                        val storageRef = storage.reference
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                                + "/media/" + file + "_" + timeStamp)
                        val uploadTask = riversRef.putFile(data.data)
                        Log.e("file path ", riversRef.path)
                        uploadTask.addOnFailureListener {
                            Log.e("fcm ", it.message)
                        }.addOnSuccessListener {
                            riversRef.downloadUrl.addOnSuccessListener {
                                Log.e("uploaded path ", it.toString())
                            }
                            Log.e("fcm ", "file uploaded succesfully")
                        }.addOnProgressListener {
                            Log.e("fcm ", "file uploaded succesfully")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
