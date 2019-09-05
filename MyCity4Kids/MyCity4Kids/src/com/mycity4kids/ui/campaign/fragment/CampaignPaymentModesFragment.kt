package com.mycity4kids.ui.campaign.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
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
    private lateinit var submitOnClickListener: SubmitListener
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
    private var defaultId: Int = -1
    private var str: String? = null

    override fun onRadioButton(position: Int) {
        selectedPaymantIdPosition = position
        for (i in 0..allPaymantModes!!.size - 1) {
            if (i == position) {
                allPaymantModes[position].isDefault = true
                defaultId = allPaymantModes[position].id
            } else {
                allPaymantModes[i].isDefault = false
            }
        }
        paymentModesAdapter.notifyDataSetChanged()
    }


    override fun onClick(p0: View?) {
        for (i in 0..allPaymantModes!!.size - 1) {
            if (allPaymantModes[i].isDefault) {
                str = "selected"
                break
            }

        }
        if (str.equals("selected")) {
            if (isComingFromRewards) {
                val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].type_id
                if (allPaymantModes[selectedPaymantIdPosition].accountNumber.isNullOrEmpty()) {
                    var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "firstTime", isComingFromRewards = isComingFromRewards, Id = -1)
                    paymentModeDtailsSubmissionFragment.setTargetFragment(this@CampaignPaymentModesFragment, 2019)
                    (activity)!!.supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
                            CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PaymentModeDtailsSubmissionFragment")
                            .commit()
                } else {
                    val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].id
                    //   postApiForDefaultPaymantMode(paymentModeId)
                    submitOnClickListener.onPaymentModeDone(paymentModeId)
                }
            } else {
                val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].type_id
                if (allPaymantModes[selectedPaymantIdPosition].accountNumber.isNullOrEmpty()) {
                    var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "firstTime", isComingFromRewards = isComingFromRewards, Id = -1)
                    paymentModeDtailsSubmissionFragment.setTargetFragment(this@CampaignPaymentModesFragment, 2019)
                    (activity)!!.supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
                            CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PaymentModeDtailsSubmissionFragment")
                            .commit()
                } else {
                    val paymentModeId: Int = allPaymantModes[selectedPaymantIdPosition].id
                    postApiForDefaultPaymantMode(paymentModeId)
                    var panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = isComingFromRewards)
                    (activity)!!.supportFragmentManager.beginTransaction().add(R.id.container, panCardDetailsSubmissionFragment,
                            CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PanCardDetailsSubmissionFragment")
                            .commit()
                }
            }
        } else {
            Toast.makeText(mContext, "choose atleast one option", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postApiForDefaultPaymantMode(paymentModeId: Int) {
        val proofPostModel = ProofPostModel(id = paymentModeId.toString())
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).postForDefaultAccount(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
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

    override fun onCellClick(paymentModeId: Int, position: Int, ID: Int) {
        var paymentModeDtailsSubmissionFragment = PaymentModeDtailsSubmissionFragment.newInstance(paymentModeId, comingFrom = "comingForEdit", isComingFromRewards = isComingFromRewards, Id = ID)
        paymentModeDtailsSubmissionFragment.setTargetFragment(this@CampaignPaymentModesFragment, 2019)
        (activity)!!.supportFragmentManager.beginTransaction().add(R.id.container, paymentModeDtailsSubmissionFragment,
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
            submitOnClickListener.onPaymentModeDone(-1)
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
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getPaymentModes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<PaymentModeListModal>> {


            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<PaymentModeListModal>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status
                        && response.data != null && response.data!!.result != null && response.data!!.result.available!!.isNotEmpty()) {
                    allPaymentData = response.data!!.result
                    allPaymantModes.clear()
                    availableList.clear()
                    if (response.data!!.result.default != null && response.data!!.result!!.default!!.account_type != null) {
                        allPaymantModes.add(0, response.data!!.result!!.default!!.account_type!!)
                        allPaymantModes[0].isDefault = true
                        allPaymantModes[0].isChecked = true
                        allPaymantModes[0].accountNumber = allPaymentData.default!!.account_number
                        allPaymantModes[0].id = response.data!!.result!!.default!!.id
                        defaultId = response.data!!.result!!.default!!.id
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

    fun refreshPaymentInfo() {
        /*fetch faq data from server*/
        fetchPaymentModes()
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
        fun onPaymentModeDone(paymentModeId: Int)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2019 && resultCode == Activity.RESULT_OK) {
            /*fetch faq data from server*/
            fetchPaymentModes()
        }

    }

}
