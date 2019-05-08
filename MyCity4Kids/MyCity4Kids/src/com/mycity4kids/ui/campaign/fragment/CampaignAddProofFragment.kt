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
import android.widget.RelativeLayout
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
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.campaign.adapter.FaqRecyclerAdapter
import com.mycity4kids.ui.campaign.adapter.MediaProofRecyclerAdapter
import com.mycity4kids.ui.campaign.adapter.UrlProofRecyclerAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.picker_dialog_cell.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val SELECT_IMAGE = 1005

class CampaignAddProofFragment : BaseFragment(), UrlProofRecyclerAdapter.ClickListener, MediaProofRecyclerAdapter.ClickListener {
    override fun onUrlProofDelete(cellIndex: Int) {
        if (campaignProofList != null && !campaignProofList!!.isNullOrEmpty()) {
            if (campaignProofList.get(cellIndex).id != 0) {
                deleteProof(campaignProofList.get(cellIndex).id!!)
            } else {
                campaignProofList.removeAt(cellIndex)
                notifyAdapters()
            }
        }

    }

    fun onProofSubmitClick() {
        for (i in 0..2) {
            if (campaignProofList != null && campaignProofList.size > i) {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<TextView>(R.id.textUrl)
                var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                if (i == 2) {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignProofList.get(i).id!!, proceedToPayment = true)
                } else {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignProofList.get(i).id!!, proceedToPayment = false)
                }
            } else {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<TextView>(R.id.textUrl)
                if (!textview.text.isNullOrEmpty()) {
                    var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                    if (i == 2) {
                        postProofToServer(proofPostModel, true)
                    } else {
                        postProofToServer(proofPostModel, false)
                    }
                } else {
                    if (i == 2) {
                        submitListener.proofSubmitDone()
                    }
                }
            }
        }
    }

    override fun onCellClick() {
        val intent = Intent()
        intent.setType("image/*")
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
    private lateinit var urlProofRecyclerAdapter: UrlProofRecyclerAdapter
    private lateinit var recyclerFaqs: RecyclerView
    private lateinit var recyclerUrlProof: RecyclerView
    private lateinit var recyclerMediaProof: RecyclerView
    private var campaignProofList: ArrayList<CampaignProofResponse> = arrayListOf()
    private var campaignId: Int = 60
    private lateinit var textSubmit: TextView
    private lateinit var deliverableTypeList: ArrayList<Int>
    private lateinit var submitListener: SubmitListener
    private lateinit var relativeMediaProof: RelativeLayout
    //private lateinit var relativeTextProof: RelativeLayout
    private lateinit var urlTypes: String

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
        val view = inflater.inflate(R.layout.fragment_add_proof, container, false)

        recyclerFaqs = view.findViewById<RecyclerView>(R.id.recyclerFaqs)

        recyclerFaqs.layoutManager = LinearLayoutManager(context)

        if (arguments != null && arguments.containsKey("id") && arguments.containsKey("deliverableTypeList")) {

            campaignId = if (arguments.containsKey("id")) {
                arguments.getInt("id")
            } else {
                0
            }

            deliverableTypeList = if (arguments.containsKey("deliverableTypeList")) {
                arguments.getIntegerArrayList("deliverableTypeList")
            } else {
                emptyList<Int>() as ArrayList<Int>
            }
        }

        relativeMediaProof = view.findViewById(R.id.relativeMediaProof)
        relativeMediaProof.setOnClickListener {
        }

        faqRecyclerAdapter = FaqRecyclerAdapter(faqs, activity as Context)
        recyclerFaqs.adapter = faqRecyclerAdapter

        recyclerMediaProof = view.findViewById<RecyclerView>(R.id.recyclerMediaProof)
        recyclerMediaProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mediaProofRecyclerAdapter = MediaProofRecyclerAdapter(campaignProofList, this)
        recyclerMediaProof.adapter = mediaProofRecyclerAdapter

        recyclerUrlProof = view.findViewById(R.id.recyclerUrlProof)
        recyclerUrlProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        urlProofRecyclerAdapter = UrlProofRecyclerAdapter(campaignProofList, this)
        recyclerUrlProof.adapter = urlProofRecyclerAdapter

        if (!deliverableTypeList.isNullOrEmpty()) {
            urlTypes = Constants.DeliverableTypes.findUrlTypeByDeliverableTypes(deliverableTypeList.get(0).toString())
            if (!urlTypes.isNullOrEmpty()) {
                if (urlTypes.equals("image_link", true)) {
                    relativeMediaProof.visibility = View.VISIBLE
                    recyclerUrlProof.visibility = View.GONE
                } else if (urlTypes.equals("website_link", true)) {
                    recyclerUrlProof.visibility = View.VISIBLE
                    relativeMediaProof.visibility = View.GONE
                } else if (urlTypes.equals("video_link", true)) {
                }
            }
        }

        textSubmit = view.findViewById<TextView>(R.id.textSubmit)
        textSubmit.setOnClickListener {
            if (urlTypes.equals("image_link", true)) {
                submitListener.proofSubmitDone()
            } else if (urlTypes.equals("website_link", true)) {
                if (!validateUrlProofs()) {
                    onProofSubmitClick()
                } else {
                    Toast.makeText(activity, "Please submit a proof", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /*fetch faq data from server*/
        fetchFaq()

        /*fetch submission data from server*/
        fetSubmissionDetail()

        return view
    }

    private fun validateUrlProofs(): Boolean {
        var isAllEmpty: Boolean = true
        for (i in 0..2) {
            var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
            var textview = view.findViewById<TextView>(R.id.textUrl)
            if (textview != null && !textview.text.isNullOrEmpty()) {
                isAllEmpty = false
                break
            }
        }
        return isAllEmpty
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
                var websiteUrlCount = 0
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null && !response.data!!.result.campaignProofResponse.isNullOrEmpty()) {
                    if (!urlTypes.isNullOrEmpty()) {
                        if (urlTypes.equals("image_link", true)) {
                            relativeMediaProof.visibility = View.VISIBLE
                            campaignProofList.addAll(response.data!!.result!!.campaignProofResponse!!)
                            mediaProofRecyclerAdapter.notifyDataSetChanged()
                        } else if (urlTypes.equals("website_link", true)) {
                            relativeMediaProof.visibility = View.GONE
                            if (response.data!!.result!!.campaignProofResponse!!.size >= 3) {
                                campaignProofList.addAll(response.data!!.result.campaignProofResponse!!.subList(0, 3))
                            }
                            campaignProofList.addAll(response.data!!.result!!.campaignProofResponse!!)
                            urlProofRecyclerAdapter.notifyDataSetChanged()
                        } else if (urlTypes.equals("video_link", true)) {

                        }
                    }

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
    private fun postProofToServer(proofPostModel: ProofPostModel, proceedToPayment: Boolean = false) {
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
                    if (!urlTypes.isNullOrEmpty()) {
                        if (urlTypes.equals("image_link", true)) {
                            if (response!!.data!!.result.id != 0) {
                                var campaignProofResponse = CampaignProofResponse()
                                campaignProofResponse.id = response!!.data!!.result.id
                                campaignProofResponse.url = proofPostModel.url
                                campaignProofResponse.urlType = 0
                                campaignProofList.add(campaignProofResponse)
                            }
                        } else if (urlTypes.equals("website_link", true)) {
                            if (proceedToPayment) {
                                submitListener.proofSubmitDone()
                            }
                        }
                    }
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
    private fun updateProofToServer(proofPostModel: ProofPostModel, proceedToPayment: Boolean = false, proofId: Int) {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).updateProofToServer(proofId, proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                    if (proceedToPayment) {
                        submitListener.proofSubmitDone()
                    }
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
                    var campaignProofListLocal = campaignProofList.filter { it.id != proofId }
                    campaignProofList.clear()
                    campaignProofList.addAll(campaignProofListLocal)
                    notifyAdapters()
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

    private fun notifyAdapters() {
        if (!urlTypes.isNullOrEmpty()) {
            if (urlTypes.equals("image_link", true)) {
                mediaProofRecyclerAdapter.notifyDataSetChanged()
            } else if (urlTypes.equals("website_link", true)) {
                urlProofRecyclerAdapter.notifyDataSetChanged()
            }
        }
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
                                var proofPostModel = ProofPostModel(url = it.toString(), campaign_id = campaignId, url_type = 0)
                                postProofToServer(proofPostModel)
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CampaignContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun proofSubmitDone()
    }
}
