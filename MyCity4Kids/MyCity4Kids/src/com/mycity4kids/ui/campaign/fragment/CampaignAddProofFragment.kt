package com.mycity4kids.ui.campaign.fragment

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.crashlytics.android.Crashlytics
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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val SELECT_IMAGE = 1005

class CampaignAddProofFragment : BaseFragment(), UrlProofRecyclerAdapter.ClickListener, MediaProofRecyclerAdapter.ClickListener {
    override fun onUrlComponentDelete(cellIndex: Int) {
        for (i in 0..campaignUrlProofList.size - 1) {
            var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
            var textview = view.findViewById<EditText>(R.id.textUrl)
            if (textview != null && !textview.text.isNullOrEmpty()) {
                this@CampaignAddProofFragment.campaignUrlProofList.get(i).url = textview.text.toString()
            } else {
                this@CampaignAddProofFragment.campaignUrlProofList.get(i).url = ""
            }
        }

        if (campaignUrlProofList != null && !campaignUrlProofList!!.isNullOrEmpty()) {
            if (campaignUrlProofList.get(cellIndex).id != 0) {
                deleteProof(campaignUrlProofList.get(cellIndex).id!!, urlType = 1)
            } else {
                campaignUrlProofList.removeAt(cellIndex)
                if (campaignUrlProofList.size < 3) {
                    textAddUrlProof.visibility = View.VISIBLE
                }
                notifyUrlAdapter()
            }


        }
    }

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
        for (i in 0..campaignUrlProofList.size - 1) {
            if (campaignUrlProofList != null && campaignUrlProofList.size > i) {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<EditText>(R.id.textUrl)
                var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                if (i == campaignUrlProofList.size - 1) {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignUrlProofList.get(i).id!!, proceedToPayment = true)
                } else {
                    updateProofToServer(proofPostModel = proofPostModel, proofId = campaignUrlProofList.get(i).id!!, proceedToPayment = false)
                }
            } else {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<EditText>(R.id.textUrl)
                if (!textview.text.isNullOrEmpty()) {
                    var proofPostModel = ProofPostModel(url = textview.text.toString(), campaign_id = campaignId, url_type = 1)
                    if (i == campaignUrlProofList.size - 1) {
                        postProofToServer(proofPostModel, true, urlType = 1)
                    } else {
                        postProofToServer(proofPostModel, false, urlType = 1)
                    }
                } else {
                    if (i == campaignUrlProofList.size - 1) {
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
        intent.setAction(Intent.ACTION_PICK)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE)
    }

    override fun onProofDelete(cellIndex: Int) {
        showEditDeleteProofDialog(cellIndex = cellIndex)
    }

    fun showEditDeleteProofDialog(cellIndex: Int) {
        val dialog = AlertDialog.Builder(activity!!, R.style.MyAlertDialogStyle)
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
    private var status: Int = 0
    private lateinit var textSubmit: TextView
    private lateinit var textInstruction: TextView
    private lateinit var deliverableTypeList: ArrayList<Int>
    private lateinit var submitListener: SubmitListener
    private lateinit var relativeMediaProof: RelativeLayout
    //private lateinit var relativeTextProof: RelativeLayout
    private lateinit var urlTypes: String
    private lateinit var toolbarTitle: TextView
    private lateinit var back: TextView
    private lateinit var textAddUrlProof: TextView
    private lateinit var linearInstruction: LinearLayout
    private lateinit var addScreenShotTextView: TextView
    private lateinit var addScreenShotTextView1: TextView
    private lateinit var addlinkTextView: TextView
    private lateinit var addlinkTextView1: TextView
    private lateinit var headerTextViewContainer: RelativeLayout
    private lateinit var headerTextViewContainer1: RelativeLayout

    companion object {
        @JvmStatic
        fun newInstance(id: Int, deliverableTypeList: ArrayList<Int>, status: Int) =
                CampaignAddProofFragment().apply {
                    arguments = Bundle().apply {
                        this.putInt("id", id)
                        this.putIntegerArrayList("deliverableTypeList", deliverableTypeList)
                        this.putInt("status", status)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_proof, container, false)
        showProgressDialog(resources.getString(R.string.please_wait))

        recyclerFaqs = view.findViewById<RecyclerView>(R.id.recyclerFaqs)
        recyclerFaqs.layoutManager = LinearLayoutManager(context)

        if (arguments != null && arguments!!.containsKey("id") && arguments!!.containsKey("deliverableTypeList")) {

            campaignId = if (arguments!!.containsKey("id")) {
                arguments!!.getInt("id")
            } else {
                0
            }

            deliverableTypeList = if (arguments!!.containsKey("deliverableTypeList")) {
                arguments!!.getIntegerArrayList("deliverableTypeList")
            } else {
                emptyList<Int>() as ArrayList<Int>
            }

            status = if (arguments!!.containsKey("status")) {
                arguments!!.getInt("status")
            } else {
                0
            }
        }
        addScreenShotTextView = view.findViewById(R.id.addScreenShotTextView)
        addlinkTextView = view.findViewById(R.id.addlinkTextView)
        addScreenShotTextView1 = view.findViewById(R.id.addScreenShotTextView1)
        addlinkTextView1 = view.findViewById(R.id.addlinkTextView1)
        headerTextViewContainer = view.findViewById(R.id.headerTextViewContainer)
        headerTextViewContainer1 = view.findViewById(R.id.headerTextViewContainer1)
        linearInstruction = view.findViewById(R.id.linearInstruction)
        textAddUrlProof = view.findViewById(R.id.textAddUrlProof)
        textAddUrlProof.setOnClickListener {
            var isEmpty = false
            for (i in 0..campaignUrlProofList.size - 1) {
                var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
                var textview = view.findViewById<EditText>(R.id.textUrl)
                if (textview != null && !textview.text.isNullOrEmpty()) {
                    this@CampaignAddProofFragment.campaignUrlProofList.get(i).url = textview.text.toString()
                } else {
                    this@CampaignAddProofFragment.campaignUrlProofList.get(i).url = ""
                    isEmpty = true
                }
            }

            if (!isEmpty) {
                var campaignProofResponse = CampaignProofResponse()
                campaignProofResponse.id = 0
                this@CampaignAddProofFragment.campaignUrlProofList.add(campaignProofResponse)

                if (campaignUrlProofList.size == 3) {
                    textAddUrlProof.visibility = View.GONE
                }
                notifyUrlAdapter()
            } else {
                Toast.makeText(activity, "Please add link in the box above before adding more", Toast.LENGTH_SHORT).show()
            }

        }

        back = view.findViewById(R.id.back)

        back.setOnClickListener {
            (activity as CampaignContainerActivity).onBackPressed()
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

        var campaignProofResponse = CampaignProofResponse()
        campaignProofResponse.id = 0
        this@CampaignAddProofFragment.campaignUrlProofList.add(campaignProofResponse)
        notifyUrlAdapter()

        textInstruction = view.findViewById(R.id.textInstruction)

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
        if (status == 22) {
            val retro = BaseApplication.getInstance().retrofit
            val campaignAPI = retro.create(CampaignAPI::class.java)
            val call = campaignAPI.getPreProof(campaignId)
            call.enqueue(preProof)
        } else {
            removeProgressDialog()
        }
        return view
    }


    val preProof = object : Callback<PreProofResponse> {
        override fun onResponse(call: Call<PreProofResponse>, response: retrofit2.Response<PreProofResponse>) {
            removeProgressDialog()
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data.get(0).result.get(0).isIs_image_required == 1) {
                        addScreenShotTextView.setText(responseData.data.get(0).result.get(0).image_name)
                        addScreenShotTextView1.visibility = View.VISIBLE
                    } else {
                        headerTextViewContainer1.visibility = View.GONE
                        relativeMediaProof.visibility = View.GONE
                    }

                    if (responseData.data.get(0).result.get(0).isIs_text_required == 1) {
                        addlinkTextView.setText(responseData.data.get(0).result.get(0).text_name)
                        addlinkTextView1.visibility = View.VISIBLE
                        textAddUrlProof.visibility = View.GONE
                        var view = recyclerUrlProof.layoutManager.findViewByPosition(0)
                        var textview = view!!.findViewById<EditText>(R.id.textUrl)
                        textview.setHint("Enter description")
                    } else {
                        headerTextViewContainer.visibility = View.GONE
                        recyclerUrlProof.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(context, responseData.reason, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<PreProofResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }


    private fun validateUrlProofs(): Boolean {
        var isAllEmpty: Boolean = true
        for (i in 0..campaignUrlProofList.size - 1) {
            var view = recyclerUrlProof.layoutManager.findViewByPosition(i);
            var textview: EditText = view.findViewById<EditText>(R.id.textUrl)
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


                    /*this will fetch proof instruction from server*/
                    fetchProofInstruction()
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
                        response.data != null && response.data!!.result != null) {

                    if (!response.data!!.result.campaignProofResponse.isNullOrEmpty()) {
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

                        if (this@CampaignAddProofFragment.campaignImageProofList.size < 6) {
                            var campaignProofResponse = CampaignProofResponse()
                            campaignProofResponse.isTemplate = true
                            this@CampaignAddProofFragment.campaignImageProofList.add(campaignProofResponse)
                            notifyMediaAdapter()
                        }

                        if (this@CampaignAddProofFragment.campaignUrlProofList.isNullOrEmpty()) {
                            var campaignProofResponse = CampaignProofResponse()
                            campaignProofResponse.id = 0
                            this@CampaignAddProofFragment.campaignUrlProofList.add(campaignProofResponse)
                            notifyUrlAdapter()
                            textAddUrlProof.visibility = View.VISIBLE
                        } else {
                            if (this@CampaignAddProofFragment.campaignUrlProofList.size >= 3) {
                                textAddUrlProof.visibility = View.GONE
                            }
                        }

                        if (!campaignImageProofList.isNullOrEmpty()) {
                            notifyMediaAdapter()
                        }

                        if (!campaignUrlProofList.isNullOrEmpty()) {
                            notifyUrlAdapter()
                        }
                    } else {
                        if (this@CampaignAddProofFragment.campaignImageProofList.isNullOrEmpty()) {
                            var campaignProofResponse = CampaignProofResponse()
                            campaignProofResponse.isTemplate = true
                            this@CampaignAddProofFragment.campaignImageProofList.add(campaignProofResponse)
                            notifyMediaAdapter()
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

    /*this will fetch proof instruction from server*/
    private fun fetchProofInstruction() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getProofInstruction(campaignId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofInstructionResult>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<ProofInstructionResult>) =
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                            response.data != null && response.data!!.result != null && response.data!!.result.readThis != null
                            && !response.data!!.result.readThis!!.instructions.isNullOrEmpty()) {
                        val readBuilder = StringBuilder()
                        for (instructions in response.data!!.result.readThis!!.instructions!!) {
                            if (!instructions.isNullOrEmpty() && !instructions.equals(""))
                                readBuilder.append("\u2022" + "  " + instructions + "\n")
                        }
                        if (!readBuilder.isEmpty()) {
                            getOffset(readBuilder.toString(), textInstruction)
                        }
                        linearInstruction.visibility = View.VISIBLE
                    } else {
                        linearInstruction.visibility = View.GONE

                    }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    private fun getOffset(instruction: String, textView: TextView) {
        textView.setText(instruction)
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
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
                    if (urlType != -1) {
                        if (urlType == 0) {
                            var campaignProofResponse = CampaignProofResponse()
                            campaignProofResponse.id = response!!.data!!.result.id
                            campaignProofResponse.url = proofPostModel.url
                            campaignProofResponse.urlType = 0
                            campaignProofResponse.isTemplate = false
                            this@CampaignAddProofFragment.campaignImageProofList.add(0, campaignProofResponse)

                            if (this@CampaignAddProofFragment.campaignImageProofList.size >= 7) {
                                this@CampaignAddProofFragment.campaignImageProofList.removeAt(campaignImageProofList.size - 1)
                            }
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

                            if (campaignImageProofList.size > 0) {
                                var campaignImageProof = campaignImageProofList.get(campaignImageProofList.size - 1)
                                if (!campaignImageProof.isTemplate) {
                                    var campaignProofResponse = CampaignProofResponse()
                                    campaignProofResponse.url = ""
                                    campaignProofResponse.urlType = 0
                                    campaignProofResponse.isTemplate = true
                                    this@CampaignAddProofFragment.campaignImageProofList.add(campaignImageProofList.size, campaignProofResponse)
                                }
                            }

                            notifyMediaAdapter()
                        } else if (urlType == 1) {
                            var campaignProofListLocal = this@CampaignAddProofFragment.campaignUrlProofList.filter { it.id != proofId }
                            campaignUrlProofList.clear()
                            campaignUrlProofList.addAll(campaignProofListLocal)
                            if (campaignUrlProofList.size < 3) {
                                textAddUrlProof.visibility = View.VISIBLE
                            }
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
                        var file = File(data.data.path);//create path from uri
                        /*  var split = file.getPath().split(":");//split the path.
                          var path = split[1];*/
                        val storageRef = storage.reference
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                                + "/media/" + file + "_" + timeStamp)
                        val uploadTask = riversRef.putFile(data.data)
                        Log.e("file path ", riversRef.path)
                        showProgressDialog("")
                        uploadTask.addOnFailureListener {
                            Log.e("fcm ", it.message)
                            removeProgressDialog()
                        }.addOnSuccessListener {
                            riversRef.downloadUrl.addOnSuccessListener {
                                Log.e("uploaded path ", it.toString())
                                var proofPostModel = ProofPostModel(url = it.toString(), campaign_id = campaignId, url_type = 0)
                                postProofToServer(proofPostModel, urlType = 0)
                            }
                            Log.e("fcm ", "file uploaded succesfully")
                            removeProgressDialog()
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



