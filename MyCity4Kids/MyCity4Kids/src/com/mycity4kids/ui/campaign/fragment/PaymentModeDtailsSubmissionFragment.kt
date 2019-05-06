package com.mycity4kids.ui.campaign.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.googlecode.mp4parser.authoring.Edit
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.AddAccountDetailModal
import com.mycity4kids.ui.campaign.BankNameModal
import com.mycity4kids.ui.campaign.DefaultData
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class PaymentModeDtailsSubmissionFragment : BaseFragment(), View.OnClickListener {


    private var paymantModeId: Int = 0
    private lateinit var comingFrom: String
    private lateinit var paytmContainer: RelativeLayout
    private lateinit var upiContainer: RelativeLayout
    private lateinit var bankTransferContainer: RelativeLayout
    private lateinit var selectBankAccountspinner: Spinner
    private var allBankNames: List<BankNameModal>? = null
    private var bankNames = mutableListOf<String>()
    private lateinit var selectedBankName: String
    private lateinit var submitTextViewCampaign: TextView
    private lateinit var addAccountHolderNameEditTextView: EditText
    private lateinit var accountNumberEditTextView: EditText
    private lateinit var confirmAccountNumberEditTextView: EditText
    private lateinit var ifscEditTextView: EditText
    private lateinit var addUpiEditTextView: EditText
    private lateinit var addMobileNumberEditText: EditText
    private lateinit var back: TextView
    private lateinit var toolbar: Toolbar
    private var source: String? = null

    private var isComingFromRewards: Boolean = false


    override fun updateUi(response: Response?) {

    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int, comingFrom: String, isComingFromRewards: Boolean = false) =
                PaymentModeDtailsSubmissionFragment().apply {
                    arguments = Bundle().apply {
                        this.putInt("id", id)
                        this.putString("comingFrom", comingFrom)
                        this.putBoolean("isComingFromRewards", isComingFromRewards)
                    }

                }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.campaign_enter_registered_paytm_mobile_number_screen, container, false)
        paytmContainer = view.findViewById(R.id.paytmContainer)
        upiContainer = view.findViewById(R.id.upiContainer)
        bankTransferContainer = view.findViewById(R.id.bankTransferContainer)
        selectBankAccountspinner = view.findViewById(R.id.selectBankAccountspinner)
        submitTextViewCampaign = view.findViewById(R.id.submitTextViewCampaign)
        addAccountHolderNameEditTextView = view.findViewById(R.id.addAccountHolderNameEditTextView)
        accountNumberEditTextView = view.findViewById(R.id.accountNumberEditTextView)
        confirmAccountNumberEditTextView = view.findViewById(R.id.confirmAccountNumberEditTextView)
        ifscEditTextView = view.findViewById(R.id.ifscEditTextView)
        addUpiEditTextView = view.findViewById(R.id.addUpiEditTextView)
        addMobileNumberEditText = view.findViewById(R.id.addMobileNumberEditText)
        back = view.findViewById(R.id.back)
        toolbar = view.findViewById(R.id.toolbar)
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        paytmContainer.visibility = View.GONE
        upiContainer.visibility = View.GONE
        bankTransferContainer.visibility = View.GONE

        if (arguments != null && arguments!!.containsKey("id")) {
            paymantModeId = arguments!!.getInt("id")
            comingFrom = arguments!!.getString("comingFrom")
        }


        when (paymantModeId) {
            1 -> {
                paytmContainer.visibility = View.VISIBLE
                source = "Paytm detail"
            }
            2 -> {
                upiContainer.visibility = View.VISIBLE
                source = "UPI Detail"
            }
            else -> {
                bankTransferContainer.visibility = View.VISIBLE
                source = "Bank Detail"
            }

        }

        if (bankTransferContainer.visibility == View.VISIBLE) {
            fetchAllBankName()
        }
        submitTextViewCampaign.setOnClickListener(this)
        back.setOnClickListener {

            if (comingFrom.equals("comingForEdit")) {
                Utils.campaignEvent(activity, "Bank Detail", "Bank Detail", "Cancel", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Change_bank_account_detail_no")

            } else {
                Utils.campaignEvent(activity, "Payment Option", source, "Back", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Show_payment_option_detail")
            }

            if (isComingFromRewards) {
                (activity as RewardsContainerActivity).onBackPressed()
            } else {
                (activity as CampaignContainerActivity).onBackPressed()
            }


        }
        return view
    }

    private fun fetchAllBankName() {


        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).getAllBankName().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<List<BankNameModal>>> {


            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<List<BankNameModal>>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status
                        && response.data != null && response.data!!.result != null && response.data!!.result.isNotEmpty()) {
                    if (response.data!!.result != null) {
                        allBankNames = response.data!!.result
                        response.data!!.result.forEach {
                            bankNames.add(it.name!!)
                        }
                        if (selectBankAccountspinner != null) {
                            val arrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, bankNames)
                            selectBankAccountspinner.adapter = arrayAdapter

                            selectBankAccountspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                                    val item = parent.getItemAtPosition(position).toString()
                                    (parent.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.greytxt_color))
                                    selectedBankName = item
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {


                                }
                            }
                        }


                    } else {
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


    override fun onClick(p0: View?) {
        if (isValid()) {
            if (comingFrom.equals("comingForEdit")) {
                Utils.campaignEvent(activity, "Bank Detail", "Bank Detail", "Continue", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Change_bank_account_detail_yes")

            } else {
                Utils.campaignEvent(activity, "PayTM Detail", source, "Submit", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Submit_paytm_mobile_no")
            }
            val addAcoountDetailModal: AddAccountDetailModal

            when (paymantModeId) {
                1 -> {

                    addAcoountDetailModal = AddAccountDetailModal(account_type_id = paymantModeId.toString(), account_number = addMobileNumberEditText.text.toString())

                }
                2 -> {
                    addAcoountDetailModal = AddAccountDetailModal(account_type_id = paymantModeId.toString(), account_number = addUpiEditTextView.text.toString())
                }
                3 -> {
                    addAcoountDetailModal = AddAccountDetailModal(account_type_id = paymantModeId.toString(), account_number = accountNumberEditTextView.text.toString(), account_ifsc_code = ifscEditTextView.text.toString(), account_name = addAccountHolderNameEditTextView.text.toString())
                }
                else -> {
                    addAcoountDetailModal = AddAccountDetailModal(account_type_id = paymantModeId.toString(), account_number = addMobileNumberEditText.text.toString())
                }

            }

            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().campaignRetrofit.create(CampaignAPI::class.java).addAccountDetail(addAcoountDetailModal).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<DefaultData>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseResponseGeneric<DefaultData>) {
                    if (comingFrom.equals("firstTime")) {
                        var panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = false)
                        (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, panCardDetailsSubmissionFragment,
                                CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PanCardDetailsSubmissionFragment")
                                .commit()
                    } else {
                        activity!!.supportFragmentManager.popBackStack()
                    }

                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

    private fun isValid(): Boolean {

        if (bankTransferContainer.visibility == View.VISIBLE) {

            if (!addAccountHolderNameEditTextView.text.isNullOrEmpty() && !accountNumberEditTextView.text.isNullOrEmpty() && !confirmAccountNumberEditTextView.text.isNullOrEmpty() && !ifscEditTextView.text.isNullOrEmpty()) {

                if (accountNumberEditTextView.text.toString().equals(confirmAccountNumberEditTextView.text.toString())) {
                    return true
                }
                Toast.makeText(activity, "account number is not matching", Toast.LENGTH_SHORT).show()
                return false


            } else {

                Toast.makeText(activity, "fill the all fields", Toast.LENGTH_SHORT).show()
                return false
            }


        } else if (paytmContainer.visibility == View.VISIBLE) {
            if (!addMobileNumberEditText.text.isNullOrEmpty() && addMobileNumberEditText.text.toString().length == 10) {
                return true
            }
            Toast.makeText(activity, "enter valid phone number", Toast.LENGTH_SHORT).show()
            return false

        } else {
            if (!addUpiEditTextView.text.isNullOrEmpty()) {
                return true
            }
            Toast.makeText(activity, "field cann't be empty", Toast.LENGTH_SHORT).show()

            return false

        }
    }

}