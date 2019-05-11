package com.mycity4kids.ui.campaign.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.*
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.campaign.adapter.FaqRecyclerAdapter
import com.mycity4kids.ui.campaign.adapter.MediaProofRecyclerAdapter
import com.mycity4kids.ui.campaign.adapter.UrlProofRecyclerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val SELECT_IMAGE = 1005

class CampaignAddProofFragment : BaseFragment(), UrlProofRecyclerAdapter.ClickListener, MediaProofRecyclerAdapter.ClickListener {
    override fun onUrlProofDelete(cellIndex: Int) {
        if (campaignUrlProofList != null && !campaignUrlProofList!!.isNullOrEmpty()) {
            if (campaignUrlProofList.get(cellIndex).id != 0) {
                deleteProof(campaignUrlProofList.get(cellIndex).id!!, urlType = 1)
            } else {
                campaignUrlProofList.removeAt(cellIndex)
                notifyUrlAdapter()
            }
        }

    }

    fun onProofSubmitClick() {
        for (i in 0..2) {
            if (campaignUrlProofList != null && campaignUrlProofList.size > i) {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<TextView>(R.id.textUrl)
                var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                if (i == 2) {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignUrlProofList.get(i).id!!, proceedToPayment = true)
                } else {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignUrlProofList.get(i).id!!, proceedToPayment = false)
                }
            } else {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<TextView>(R.id.textUrl)
                if (!textview.text.isNullOrEmpty()) {
                    var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                    if (i == 2) {
                        postProofToServer(proofPostModel, true, urlType = 1)
                    } else {
                        postProofToServer(proofPostModel, false, urlType = 1)
                    }
                } else {
                    if (i == 2) {
                        submitListener.proofSubmitDone()
                        // showRewardDialog()
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
        showEditDeleteProofDialog(cellIndex = cellIndex)
    }

    fun showEditDeleteProofDialog(cellIndex: Int) {
        val dialog = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)
        dialog.setMessage("Are you sure? you want to delete this image.").setNegativeButton("Delete") { dialog, which ->
            dialog.cancel()
            if (!campaignImageProofList.isNullOrEmpty() && cellIndex < campaignImageProofList.size && !campaignImageProofList.get(cellIndex).url.isNullOrEmpty()) {
                deleteProof(campaignImageProofList.get(cellIndex).id!!, urlType = 0)
            }

        }.setPositiveButton(R.string.new_cancel) { dialog, which ->
            dialog.cancel()
        }.setIcon(android.R.drawable.ic_dialog_alert)
        val alert11 = dialog.create()
        alert11.show()
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
    private var campaignImageProofList: ArrayList<CampaignProofResponse> = arrayListOf()
    private var campaignUrlProofList: ArrayList<CampaignProofResponse> = arrayListOf()
    private var campaignId: Int = 60
    private lateinit var textSubmit: TextView
    private lateinit var deliverableTypeList: ArrayList<Int>
    private lateinit var submitListener: SubmitListener
    private lateinit var relativeMediaProof: RelativeLayout
    //private lateinit var relativeTextProof: RelativeLayout
    private lateinit var urlTypes: String
    private lateinit var toolbarTitle: TextView
    private lateinit var back: TextView

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

        back = view.findViewById(R.id.back)

        back.setOnClickListener {
                (activity as CampaignContainerActivity).onBackPressed()
            Utils.campaignEvent(activity, "Campaign Detail", "Proof Submission", "Back", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Campaign_Detail")
        }

        relativeMediaProof = view.findViewById(R.id.relativeMediaProof)
        relativeMediaProof.setOnClickListener {
        }

        faqRecyclerAdapter = FaqRecyclerAdapter(faqs, activity as Context)
        recyclerFaqs.adapter = faqRecyclerAdapter

        recyclerMediaProof = view.findViewById<RecyclerView>(R.id.recyclerMediaProof)
        recyclerMediaProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mediaProofRecyclerAdapter = MediaProofRecyclerAdapter(campaignImageProofList, this)
        recyclerMediaProof.adapter = mediaProofRecyclerAdapter

        recyclerUrlProof = view.findViewById(R.id.recyclerUrlProof)
        recyclerUrlProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        urlProofRecyclerAdapter = UrlProofRecyclerAdapter(campaignUrlProofList, this)
        recyclerUrlProof.adapter = urlProofRecyclerAdapter

        textSubmit = view.findViewById<TextView>(R.id.textSubmit)
        textSubmit.setOnClickListener {
            if (!validateUrlProofs() || !validateMediaProofs()) {
                onProofSubmitClick()
            } else {
                Toast.makeText(activity, "Please submit a proof", Toast.LENGTH_SHORT).show()
            }
            Utils.campaignEvent(activity, "Payment Option", "Proof Submission", "Share_payment_details", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_payment_option_detail")
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


    private fun validateMediaProofs(): Boolean {
        return campaignImageProofList.isNullOrEmpty()
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
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getSubmissionDetail(campaignId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null && !response.data!!.result.campaignProofResponse.isNullOrEmpty()) {

                    var campaignImageProofList = response.data!!.result.campaignProofResponse!!.filter {
                        !it.url.isNullOrEmpty() && it.urlType == 0
                    }


                    var campaignUrlProofList = response.data!!.result.campaignProofResponse!!.filter {
                        !it.url.isNullOrEmpty() && it.urlType == 1
                    }

                    this@CampaignAddProofFragment.campaignImageProofList.clear()
                    this@CampaignAddProofFragment.campaignUrlProofList.clear()

                    this@CampaignAddProofFragment.campaignImageProofList.addAll(campaignImageProofList)
                    this@CampaignAddProofFragment.campaignUrlProofList.addAll(campaignUrlProofList)

                    if (!campaignImageProofList.isNullOrEmpty()) {
                        notifyMediaAdapter()
                    }

                    if (!campaignUrlProofList.isNullOrEmpty()) {
                        notifyUrlAdapter()
                    } else

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
    private fun postProofToServer(proofPostModel: ProofPostModel, proceedToPayment: Boolean = false, urlType: Int = -1) {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).postProofToServer(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                    if (urlType != 1) {
                        if (urlType == 0) {
                            var campaignProofResponse = CampaignProofResponse()
                            campaignProofResponse.id = response!!.data!!.result.id
                            campaignProofResponse.url = proofPostModel.url
                            campaignProofResponse.urlType = 0
                            this@CampaignAddProofFragment.campaignImageProofList.add(campaignProofResponse)
                            notifyMediaAdapter()
                        } else if (urlType == 1) {
                            if (proceedToPayment) {
                                // showRewardDialog()
                                submitListener.proofSubmitDone()
                            }
                        }
                    }
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
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).updateProofToServer(proofId, proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
    private fun deleteProof(proofId: Int, urlType: Int = -1) {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).deleteProofById(proofId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                        response.data != null && response.data!!.result != null) {
                    if (urlType != -1) {
                        if (urlType == 0) {
                            var campaignProofListLocal = this@CampaignAddProofFragment.campaignImageProofList.filter { it.id != proofId }
                            campaignImageProofList.clear()
                            campaignImageProofList.addAll(campaignProofListLocal)
                            notifyMediaAdapter()
                        } else if (urlType == 1) {
                            var campaignProofListLocal = this@CampaignAddProofFragment.campaignUrlProofList.filter { it.id != proofId }
                            campaignUrlProofList.clear()
                            campaignUrlProofList.addAll(campaignProofListLocal)
                            notifyUrlAdapter()
                        }
                    }

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

    private fun notifyMediaAdapter() {
        mediaProofRecyclerAdapter.notifyDataSetChanged()
    }

    private fun notifyUrlAdapter() {
        urlProofRecyclerAdapter.notifyDataSetChanged()
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
                                postProofToServer(proofPostModel, urlType = 0)
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

