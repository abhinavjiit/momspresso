package com.mycity4kids.ui.campaign.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.HttpException
import java.io.InputStreamReader
import java.util.regex.Pattern


class PanCardDetailsSubmissionFragment : BaseFragment(), View.OnClickListener {

    private var panNumber: String? = null
    private lateinit var submitTextView: TextView
    private lateinit var panCardDetailEditTextView: EditText
    private var isComingFromRewards: Boolean = false
    private lateinit var textLater: TextView
    private lateinit var submitOnClickListener: SubmitListener
    private lateinit var toolbar: android.support.v7.widget.Toolbar
    private lateinit var back: TextView
    override fun updateUi(response: Response?) {

    }

    companion object {
        @JvmStatic
        fun newInstance(isComingFromRewards: Boolean = false) =
                PanCardDetailsSubmissionFragment().apply {
                    arguments = Bundle().apply {
                        this.putBoolean("isComingFromRewards", isComingFromRewards)
                    }

                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pancard_detail_submission_fragment, container, false)
        back = view.findViewById(R.id.back)
        toolbar = view.findViewById(R.id.toolbar)

        if (arguments != null) {
            isComingFromRewards = if (arguments!!.containsKey("isComingFromRewards")) {
                arguments!!.getBoolean("isComingFromRewards")
            } else {
                false
            }
        }

        if (isComingFromRewards) {
            toolbar.visibility = View.GONE
        } else {
            toolbar.visibility = View.VISIBLE
        }

        textLater = view.findViewById(R.id.textLater)
        textLater.setOnClickListener {
            if (isComingFromRewards) {
                submitOnClickListener.onPanCardDone()
            } else {
                var campaignCongratulationFragment = CampaignCongratulationFragment.newInstance()
                (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, campaignCongratulationFragment,
                        CampaignCongratulationFragment::class.java.simpleName).addToBackStack("CampaignCongratulationFragment")
                        .commit()
            }
        }
        panCardDetailEditTextView = view.findViewById(R.id.panCardDetailEditTextView)
        submitTextView = view.findViewById(R.id.submitTextView)
        fetchPanNumber()
        submitTextView.setOnClickListener(this)
        back.setOnClickListener {
            if (isComingFromRewards) {
                (activity as RewardsContainerActivity).onBackPressed()
            } else {
                (activity as CampaignContainerActivity).onBackPressed()
            }
        }
        return view
    }

    private fun fetchPanNumber() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getPanNumber().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<ProofPostModel>) {
                if (response.data != null && response.data!!.result != null && !response.data!!.result.pan.isNullOrEmpty()) {
                    panNumber = response.data!!.result.pan
                    panCardDetailEditTextView.setText(panNumber)
                } else {
                    
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())

            }


        })
    }

    override fun onClick(p0: View?) {
        if (!panCardDetailEditTextView.text.toString().isNullOrEmpty()) {
            val panCardNumber = panCardDetailEditTextView.text.toString().trim()

            val pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
            val matcher = pattern.matcher(panCardNumber)


            if (matcher.matches()) {


                if (!panNumber.isNullOrEmpty()) {
                    val proofPostModel = ProofPostModel(pan = panCardDetailEditTextView.text.toString())
                    showProgressDialog(resources.getString(R.string.please_wait))
                    BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).updatePanNumber(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
                        override fun onComplete() {
                            removeProgressDialog()
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(response: BaseResponseGeneric<ProofPostModel>) {
                            if (response != null && response.code == 200 && response.data != null && response.data!!.result != null) {
                                if (isComingFromRewards) {
                                    submitOnClickListener.onPanCardDone()
                                } else {
                                    Utils.campaignEvent(activity, "Thank you screen", "Pan Card", "Submit", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Submission_Success")
                                    var campaignCongratulationFragment = CampaignCongratulationFragment.newInstance()
                                    (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, campaignCongratulationFragment,
                                            CampaignCongratulationFragment::class.java.simpleName).addToBackStack("CampaignCongratulationFragment")
                                            .commit()
                                }


                            }
                        }

                        override fun onError(e: Throwable) {
                            removeProgressDialog()
                            val code = (e as HttpException).code()
                            if (code == 400) {
                                var data = (e as HttpException).response().errorBody()!!.byteStream()
                                var jsonParser = JsonParser()
                                var jsonObject = jsonParser.parse(
                                        InputStreamReader(data, "UTF-8")) as JsonObject
                                var reason = jsonObject.get("reason")
                                Toast.makeText(context, reason.asString, Toast.LENGTH_SHORT).show()
                            }

                            Log.e("exception in error", e.message.toString())


                        }


                    })
                } else {
                    val proofPostModel = ProofPostModel(pan = panCardDetailEditTextView.text.toString())
                    showProgressDialog(resources.getString(R.string.please_wait))
                    BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).addPanNumber(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
                        override fun onComplete() {
                            removeProgressDialog()

                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(response: BaseResponseGeneric<ProofPostModel>) {
                            if (response != null && response.code == 200 && response.data != null && response.data!!.result != null) {
                                if (isComingFromRewards) {
                                    submitOnClickListener.onPanCardDone()
                                } else {
                                    Utils.campaignEvent(activity, "Thank you screen", "Pan Card", "Submit", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_Submission_Success")
                                    var campaignCongratulationFragment = CampaignCongratulationFragment.newInstance()
                                    (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, campaignCongratulationFragment,
                                            CampaignCongratulationFragment::class.java.simpleName).addToBackStack("CampaignCongratulationFragment")
                                            .commit()
                                }


                            }
                        }

                        override fun onError(e: Throwable) {
                            removeProgressDialog()
                            val code = (e as HttpException).code()
                            if (code == 400) {
                                var data = (e as HttpException).response().errorBody()!!.byteStream()
                                var jsonParser = JsonParser()
                                var jsonObject = jsonParser.parse(
                                        InputStreamReader(data, "UTF-8")) as JsonObject
                                var reason = jsonObject.get("reason")
                                Toast.makeText(context, reason.asString, Toast.LENGTH_SHORT).show()
                            }

                            Log.e("exception in error", e.message.toString())
                        }


                    })
                }


            } else {
                Toast.makeText(activity, panCardNumber + " is Not Matching the Correct Formate", Toast.LENGTH_SHORT).show()

            }


        } else {
            Toast.makeText(activity, "field cann't be empty", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitOnClickListener = context
        } else if (context is CampaignContainerActivity) {

        }
    }

    interface SubmitListener {
        fun onPanCardDone()
    }
}