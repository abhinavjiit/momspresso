package com.mycity4kids.ui.campaign.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.DefaultData
import com.mycity4kids.ui.campaign.PaymentModeListModal
import com.mycity4kids.ui.campaign.PaymentModesModal
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CampaignPaymentModesFragment : BaseFragment(), PaymentModesAdapter.ClickListener, View.OnClickListener {

    private var mContext: Context? = null
    private var selectedPaymantIdPosition: Int = 0
    private lateinit var saveContinueTextView: TextView
    private lateinit var submitOnClickListener: CampaignPaymentModesFragment.SubmitListener
    private lateinit var toolbar: Toolbar
    private var columnCount = 1
    private lateinit var actionbar: ActionBar
    private var availableList = mutableListOf<PaymentModesModal>()
    private var allPaymantModes = mutableListOf<PaymentModesModal>()
    private lateinit var paymentModesAdapter: PaymentModesAdapter
    private lateinit var recyclerPaymentModesOption: RecyclerView
    private var dataDefaultPaymentMode: DefaultData? = null
    private lateinit var allPaymentData: PaymentModeListModal
    private lateinit var textLater: TextView
    private var isComingFromRewards: Boolean = false
    private lateinit var back: TextView
    private var source: String? = null
    private var eventName: String? = null


    override fun onRadioButton(position: Int) {
        selectedPaymantIdPosition = position
        for (i in 0..allPaymantModes!!.size - 1) {
            if (i == position) {
                allPaymantModes[position].isDefault = true
            } else {
                allPaymantModes[i].isDefault = false
            }
        }
        paymentModesAdapter.notifyDataSetChanged()
    }


    override fun onClick(p0: View?) {
        val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].type_id
        when (paymentModeId) {
            1 -> {

                source = "Select_paytm"
                eventName = "Show_paytm_detail"
            }
            2 -> {

                source = "Select_upi"
                eventName = "Show_upi_detail"
            }
            else -> {

                source = "Select_bank_detail"
                eventName = "Show_bank_detail"
            }

        }
        Utils.campaignEvent(activity, "PayTM Detail", source, "Back", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), eventName)


        if (isComingFromRewards) {
            val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].type_id
            if (allPaymantModes[selectedPaymantIdPosition].accountNumber.isNullOrEmpty()) {
                var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "firstTime", isComingFromRewards = isComingFromRewards)
                (activity).supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
                        CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PaymentModeDtailsSubmissionFragment")
                        .commit()
            } else {
                submitOnClickListener.onPaymentModeDone()
            }
        } else {
            val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].type_id
            if (allPaymantModes[selectedPaymantIdPosition].accountNumber.isNullOrEmpty()) {
                var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "firstTime", isComingFromRewards = isComingFromRewards)
                (activity).supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
                        CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PaymentModeDtailsSubmissionFragment")
                        .commit()
            } else {
                val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].id
                postApiForDefaultPaymantMode(paymentModeId)
                var panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = isComingFromRewards)
                (activity).supportFragmentManager.beginTransaction().add(R.id.container, panCardDetailsSubmissionFragment,
                        CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PanCardDetailsSubmissionFragment")
                        .commit()
            }
        }
    }

    private fun postApiForDefaultPaymantMode(paymentModeId: Int) {
        val proofPostModel = ProofPostModel(id = paymentModeId.toString())
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).postForDefaultAccount(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
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

    override fun onCellClick(paymentModeId: Int, position: Int) {
        if (paymentModeId == 3) {
            Utils.campaignEvent(activity, "Bank Detail", "Bank Detail", "Change_Account_Detail", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Change_bank_account_detail")

        }
        var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "comingForEdit")
        (activity).supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
                PanCardDetailsSubmissionFragment::class.java.simpleName).addToBackStack("PaymentModeDtailsSubmissionFragment")
                .commit()
    }


    override fun updateUi(response: Response?) {
    }


    companion object {
        @JvmStatic
        fun newInstance(isComingFromRewards: Boolean = false) =
                CampaignPaymentModesFragment().apply {
                    arguments = Bundle().apply {
                        this.putBoolean("isComingFromRewards", isComingFromRewards)
                    }

                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.campaign_payment_modes_fragment, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        back = view.findViewById(R.id.back)

        if (arguments != null) {
            isComingFromRewards = if (arguments.containsKey("isComingFromRewards")) {
                arguments.getBoolean("isComingFromRewards")
            } else {
                false
            }
        }
        back.setOnClickListener {
            if (isComingFromRewards) {
                (activity as RewardsContainerActivity).onBackPressed()
            } else {
                (activity as CampaignContainerActivity).onBackPressed()

            }
        }
        textLater = view.findViewById(R.id.textLater)

        if (isComingFromRewards) {
            textLater.visibility = View.VISIBLE
        } else {
            textLater.visibility = View.GONE
        }

        textLater.setOnClickListener {
            submitOnClickListener.onPaymentModeDone()
        }

        // Set the adapter
        recyclerPaymentModesOption = view.findViewById<RecyclerView>(R.id.recyclerPaymentModesOption)
        saveContinueTextView = view.findViewById(R.id.saveContinueTextView)
        recyclerPaymentModesOption.layoutManager = LinearLayoutManager(context)

        /*fetch faq data from server*/
        fetchPaymentModes()
        saveContinueTextView.setOnClickListener(this)


        return view
    }


    /*fetch data from server*/
    private fun fetchPaymentModes() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getPaymentModes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<PaymentModeListModal>> {


            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<PaymentModeListModal>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status
                        && response.data != null && response.data!!.result != null && response.data!!.result.available!!.isNotEmpty()) {
                    allPaymentData = response.data!!.result
                    if (response.data!!.result.default != null && response.data!!.result!!.default!!.account_type != null) {
                        allPaymantModes.add(0, response.data!!.result!!.default!!.account_type!!)
                        allPaymantModes[0].isDefault = true
                        allPaymantModes[0].isChecked = true
                        allPaymantModes[0].accountNumber = allPaymentData.default!!.account_number
                        allPaymantModes[0].id = response.data!!.result!!.default!!.id

                        availableList.addAll(response.data!!.result.available as List<PaymentModesModal>)
                        for (i in 0..availableList!!.size - 1) {
                            var paymentMOdesModal: PaymentModesModal? = null
                            if (availableList!!.get(i).exists != null && availableList!!.get(i).exists!!.account_number.isNotEmpty()) {
                                paymentMOdesModal = PaymentModesModal(icon = availableList[i].icon, type_id = availableList!!.get(i).type_id, isDefault = false, accountNumber = availableList!!.get(i).exists!!.account_number, id = availableList[i].exists!!.id)
                            } else {
                                paymentMOdesModal = PaymentModesModal(icon = availableList[i].icon, type_id = availableList!!.get(i).type_id, isDefault = false, accountNumber = null, id = -1)
                            }

                            allPaymantModes.add(paymentMOdesModal)
                        }
                    } else {
                        availableList.addAll(response.data!!.result.available as List<PaymentModesModal>)
                        for (i in 0..availableList!!.size - 1) {
                            var paymentMOdesModal: PaymentModesModal? = null
                            if (availableList!!.get(i).exists != null && availableList!!.get(i).exists!!.account_number.isNotEmpty()) {
                                paymentMOdesModal = PaymentModesModal(icon = availableList[i].icon, type_id = availableList!!.get(i).type_id, isDefault = false, accountNumber = availableList!!.get(i).exists!!.account_number, id = availableList[i].exists!!.id)
                            } else {
                                paymentMOdesModal = PaymentModesModal(icon = availableList[i].icon, type_id = availableList!!.get(i).type_id, isDefault = false, accountNumber = null, id = -1)
                            }
                            allPaymantModes.add(paymentMOdesModal)
                        }
                    }
                    paymentModesAdapter = PaymentModesAdapter(allPaymantModes, this@CampaignPaymentModesFragment)
                    recyclerPaymentModesOption.adapter = paymentModesAdapter
                    //faqRecyclerAdapter.notifyDataSetChanged()
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    override fun onAttach(context: Context?) {
        mContext = context
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitOnClickListener = context
        } else if (context is CampaignContainerActivity) {


        }
    }

    interface SubmitListener {
        fun onPaymentModeDone()
    }


}
