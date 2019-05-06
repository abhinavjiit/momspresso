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
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.question_popup_layout_groups_detail_activity.*

class PanCardDetailsSubmissionFragment : BaseFragment(), View.OnClickListener {

    private var panNumber: String? = null
    private lateinit var submitTextView: TextView
    private lateinit var panCardDetailEditTextView: EditText
    private var isComingFromRewards : Boolean =false
    private lateinit var textLater : TextView
    private lateinit var submitOnClickListener : SubmitListener
    override fun updateUi(response: Response?) {

    }

    companion object {
        @JvmStatic
        fun newInstance(isComingFromRewards : Boolean = false) =
                PanCardDetailsSubmissionFragment().apply {
                    arguments = Bundle().apply {
                        this.putBoolean("isComingFromRewards",isComingFromRewards)
                    }

                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pancard_detail_submission_fragment, container, false)


        if (arguments != null) {
            isComingFromRewards = if (arguments.containsKey("isComingFromRewards")) {
                arguments.getBoolean("isComingFromRewards")
            } else{
                false
            }
        }

        textLater = view.findViewById(R.id.textLater)
        textLater.setOnClickListener {
            if(isComingFromRewards){
                submitOnClickListener.onPanCardDone()
            }else{
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
        return view
    }

    private fun fetchPanNumber() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getPanNumber().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<ProofPostModel>) {
                if (response.data != null && response.data!!.result != null && !response.data!!.result.pan.isNullOrEmpty()) {
                    panNumber = response.data!!.result.pan
                    panCardDetailEditTextView.setText(panNumber)
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

            if (!panNumber.isNullOrEmpty()) {
                val proofPostModel = ProofPostModel(pan = panCardDetailEditTextView.text.toString())
                showProgressDialog(resources.getString(R.string.please_wait))
                BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).updatePanNumber(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
                    override fun onComplete() {
                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseResponseGeneric<ProofPostModel>) {
                        if(isComingFromRewards){
                            submitOnClickListener.onPanCardDone()
                        }else{
                            var campaignCongratulationFragment = CampaignCongratulationFragment.newInstance()
                            (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, campaignCongratulationFragment,
                                    CampaignCongratulationFragment::class.java.simpleName).addToBackStack("CampaignCongratulationFragment")
                                    .commit()
                        }


                    }

                    override fun onError(e: Throwable) {
                        removeProgressDialog()
                        Log.e("exception in error", e.message.toString())

                    }


                })
            } else {
                val proofPostModel = ProofPostModel(pan = panCardDetailEditTextView.text.toString())
                showProgressDialog(resources.getString(R.string.please_wait))
                BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).addPanNumber(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
                    override fun onComplete() {
                        removeProgressDialog()

                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseResponseGeneric<ProofPostModel>) {
                    }

                    override fun onError(e: Throwable) {
                        removeProgressDialog()
                        Log.e("exception in error", e.message.toString())
                    }


                })
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