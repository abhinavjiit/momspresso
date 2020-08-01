package com.mycity4kids.ui.campaign.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.GetAllPaymentDetails
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.AddAccountDetailModal
import com.mycity4kids.ui.campaign.BankNameModal
import com.mycity4kids.ui.campaign.DefaultData
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import retrofit2.HttpException

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
    private var addAccountHolderNameEditTextView: EditText? = null
    private var accountNumberEditTextView: EditText? = null
    private var selectBankAccountEditText: EditText? = null
    private var confirmAccountNumberEditTextView: EditText? = null

    private var ifscEditTextView: EditText? = null
    private lateinit var addUpiEditTextView: EditText
    private lateinit var addMobileNumberEditText: EditText
    private lateinit var back: TextView
    private lateinit var toolbar: Toolbar
    private var source: String? = null
    private var ID: Int = -1
    private var isComingFromRewards: Boolean = false
    private var spannable: SpannableString? = null
    private lateinit var paytmDisclaimerTwo: TextView
    private lateinit var upiDisclaimerTwo: TextView
    private lateinit var accountDisclaimerTwo: TextView

    companion object {
        @JvmStatic
        fun newInstance(
            id: Int,
            comingFrom: String,
            isComingFromRewards: Boolean = false,
            Id: Int
        ) =
            PaymentModeDtailsSubmissionFragment().apply {
                arguments = Bundle().apply {
                    this.putInt("id", id)
                    this.putString("comingFrom", comingFrom)
                    this.putBoolean("isComingFromRewards", isComingFromRewards)
                    this.putInt("Id", Id)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.campaign_enter_registered_paytm_mobile_number_screen,
            container,
            false
        )
        paytmContainer = view.findViewById(R.id.paytmContainer)
        upiContainer = view.findViewById(R.id.upiContainer)
        bankTransferContainer = view.findViewById(R.id.bankTransferContainer)
        selectBankAccountspinner = view.findViewById(R.id.selectBankAccountspinner)
        submitTextViewCampaign = view.findViewById(R.id.submitTextViewCampaign)
        selectBankAccountEditText = view.findViewById(R.id.selectBankAccountEditText)
        addAccountHolderNameEditTextView = view.findViewById(R.id.addAccountHolderNameEditTextView)
        accountNumberEditTextView = view.findViewById(R.id.accountNumberEditTextView)
        confirmAccountNumberEditTextView = view.findViewById(R.id.confirmAccountNumberEditTextView)
        ifscEditTextView = view.findViewById(R.id.ifscEditTextView)
        addUpiEditTextView = view.findViewById(R.id.addUpiEditTextView)
        addMobileNumberEditText = view.findViewById(R.id.addMobileNumberEditText)
        paytmDisclaimerTwo = view.findViewById(R.id.paytm_disclaimer_two)
        upiDisclaimerTwo = view.findViewById(R.id.upi_disclaimer_two)
        accountDisclaimerTwo = view.findViewById(R.id.account_disclaimer_two)
        back = view.findViewById(R.id.back)
        toolbar = view.findViewById(R.id.toolbar)
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        paytmContainer.visibility = View.GONE
        upiContainer.visibility = View.GONE
        bankTransferContainer.visibility = View.GONE

        if (arguments != null && arguments!!.containsKey("id")) {
            paymantModeId = arguments!!.getInt("id")
            comingFrom = arguments!!.getString("comingFrom").toString()
            ID = arguments!!.getInt("Id")

            isComingFromRewards = if (arguments!!.containsKey("isComingFromRewards")) {
                arguments!!.getBoolean("isComingFromRewards")
            } else {
                false
            }
        }

        when (paymantModeId) {
            1 -> paytmContainer.visibility = View.VISIBLE
            2 -> upiContainer.visibility = View.VISIBLE
            else -> bankTransferContainer.visibility = View.VISIBLE
        }

        if (bankTransferContainer.visibility == View.VISIBLE) {
            fetchAllBankName()
        }
        if (ID != -1 && comingFrom.equals("comingForEdit")) {

            fetchLastUpdatedDetails(ID)
        }

        submitTextViewCampaign.setOnClickListener(this)

        if (isComingFromRewards) {
            toolbar.visibility = View.GONE
        } else {
            toolbar.visibility = View.VISIBLE
        }
        back.setOnClickListener {
            if (isComingFromRewards) {
                (activity as RewardsContainerActivity).onBackPressed()
                Utils.campaignEvent(
                    activity,
                    "Payment Option",
                    source,
                    "Back",
                    "",
                    "android",
                    SharedPrefUtils.getAppLocale(activity),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_payment_option_detail"
                )
            } else {
                Utils.campaignEvent(
                    activity,
                    "Bank Detail",
                    "Bank Detail",
                    "Cancel",
                    "",
                    "android",
                    SharedPrefUtils.getAppLocale(activity),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Change_bank_account_detail_no"
                )
                (activity as CampaignContainerActivity).onBackPressed()
            }
        }

        spannable = SpannableString(resources.getString(R.string.rewards_payment_disclaimer_note_two))

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@momspresso-mymoney.com", null
                )
                )
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setUnderlineText(false)
            }
        }
        spannable!!.setSpan(clickableSpan, 80, 110, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (paymantModeId == 1) {
            paytmDisclaimerTwo.movementMethod = LinkMovementMethod.getInstance()
            paytmDisclaimerTwo.highlightColor = Color.BLUE
            paytmDisclaimerTwo.setText(spannable)
        } else if (paymantModeId == 2) {
            upiDisclaimerTwo.movementMethod = LinkMovementMethod.getInstance()
            upiDisclaimerTwo.highlightColor = Color.BLUE
            upiDisclaimerTwo.setText(spannable)
        } else {
            accountDisclaimerTwo.movementMethod = LinkMovementMethod.getInstance()
            accountDisclaimerTwo.highlightColor = Color.BLUE
            accountDisclaimerTwo.setText(spannable)
        }

        return view
    }

    private fun fetchAllBankName() {

        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getAllBankName().subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<List<BankNameModal>>> {

            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<List<BankNameModal>>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status &&
                    response.data != null && response.data!!.result != null && response.data!!.result.isNotEmpty()) {
                    if (response.data!!.result != null) {
                        allBankNames = response.data!!.result
                        response.data!!.result.forEach {
                            bankNames.add(it.name!!)
                        }
                        if (selectBankAccountspinner != null) {
                            val arrayAdapter = context?.let {
                                ArrayAdapter(
                                    it,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    bankNames
                                )
                            }
                            selectBankAccountspinner.adapter = arrayAdapter

                            selectBankAccountspinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {

                                        val item = parent.getItemAtPosition(position).toString()
                                        (parent.getChildAt(0) as TextView).setTextColor(
                                            resources.getColor(
                                                R.color.greytxt_color
                                            )
                                        )
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
            //            if (comingFrom.equals("comingForEdit")) {
            //                Utils.campaignEvent(activity, "Bank Detail", "Bank Detail", "Continue", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Change_bank_account_detail_yes")
            //
            //            } else {
            //                Utils.campaignEvent(activity, "PayTM Detail", source, "Submit", "", "android", SharedPrefUtils.getAppLocale(activity), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, System.currentTimeMillis().toString(), "Submit_paytm_mobile_no")
            //            }
            val addAcoountDetailModal: AddAccountDetailModal

            when (paymantModeId) {
                1 -> {
                    addAcoountDetailModal = AddAccountDetailModal(
                        account_type_id = paymantModeId.toString().trim(),
                        account_number = addMobileNumberEditText.text.toString().trim()
                    )
                }
                2 -> {
                    addAcoountDetailModal = AddAccountDetailModal(
                        account_type_id = paymantModeId.toString().trim(),
                        account_number = addUpiEditTextView.text.toString().trim()
                    )
                }
                3 -> {
                    addAcoountDetailModal = AddAccountDetailModal(
                        account_type_id = paymantModeId.toString().trim(),
                        account_number = accountNumberEditTextView?.text.toString().trim(),
                        account_ifsc_code = ifscEditTextView?.text.toString().trim().toUpperCase(),
                        account_name = addAccountHolderNameEditTextView?.text.toString().trim()
                    )
                }
                else -> {
                    addAcoountDetailModal = AddAccountDetailModal(
                        account_type_id = paymantModeId.toString().trim(),
                        account_number = addMobileNumberEditText.text.toString().trim()
                    )
                }
            }

            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).addAccountDetail(
                addAcoountDetailModal
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                object : Observer<BaseResponseGeneric<DefaultData>> {
                    override fun onComplete() {
                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseResponseGeneric<DefaultData>) {
                        /*if (comingFrom.equals("firstTime") && !isComingFromRewards) {
                            var panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = false)
                            (context as CampaignContainerActivity).supportFragmentManager.beginTransaction().add(R.id.container, panCardDetailsSubmissionFragment,
                                    CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("PanCardDetailsSubmissionFragment")
                                    .commit()
                        } else {*/
                        when (paymantModeId) {
                            1 -> {
                                ToastUtils.showToast(context, "PaytmNumber is Updated Successfully")
                            }
                            else -> {
                                ToastUtils.showToast(
                                    context,
                                    "BankDetails are Updated Successfully"
                                )
                            }
                        }
                        activity!!.supportFragmentManager.popBackStack()
                        var fragment = targetFragment
                        if (fragment != null && fragment is CampaignPaymentModesFragment) {
                            fragment.onActivityResult(2019, Activity.RESULT_OK, null)
                        }

                        //                    }
                    }

                    override fun onError(e: Throwable) {
                        removeProgressDialog()
                        val code = (e as HttpException).code()
                        if (code == 400) {
                            var data = e.response()?.errorBody()!!.byteStream()
                            var jsonParser = JsonParser()
                            var jsonObject = jsonParser.parse(
                                InputStreamReader(data, "UTF-8")
                            ) as JsonObject
                            var reason = jsonObject.get("reason")
                            Toast.makeText(context, reason.asString, Toast.LENGTH_SHORT).show()
                        }

                        Log.e("exception in error", e.message.toString())
                    }
                })
        }
    }

    private fun isValid(): Boolean {

        if (bankTransferContainer.visibility == View.VISIBLE) {

            if (!addAccountHolderNameEditTextView?.text.isNullOrEmpty() && !accountNumberEditTextView?.text.isNullOrEmpty() && !confirmAccountNumberEditTextView?.text.isNullOrEmpty() && !ifscEditTextView?.text.toString().trim().isNullOrEmpty() && !selectBankAccountEditText?.text.toString().trim().isNullOrEmpty()) {

                if (ifscEditTextView?.text.toString().trim().contains(" ")) {
                    Toast.makeText(activity, "Space is not allowed", Toast.LENGTH_SHORT).show()
                    return false
                }

                if (accountNumberEditTextView?.text.toString().equals(
                        confirmAccountNumberEditTextView?.text.toString()
                    )) {
                    return true
                }

                Toast.makeText(
                    activity,
                    "account number is not matching",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {

                Toast.makeText(activity, "fill the all fields", Toast.LENGTH_SHORT).show()
                return false
            }
        } else if (paytmContainer.visibility == View.VISIBLE) {
            if (!addMobileNumberEditText.text.isNullOrEmpty() && addMobileNumberEditText.text.toString().trim().length == 10) {
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

    private fun fetchLastUpdatedDetails(ID: Int) {
        showProgressDialog(resources.getString(R.string.please_wait))

        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getAllPaymentModeDetails(
            ID
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<GetAllPaymentDetails>> {
            override fun onComplete() {
                removeProgressDialog(); }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<GetAllPaymentDetails>) {

                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response.data!!.result != null) {

                    addAccountHolderNameEditTextView?.setText(response.data!!.result.account_name)
                    accountNumberEditTextView?.setText(response.data!!.result.account_number)
                    confirmAccountNumberEditTextView?.setText(response.data!!.result.account_number)
                    ifscEditTextView?.setText(response.data!!.result.account_ifsc_code)
                    selectBankAccountEditText?.setText(response.data!!.result.bank_name)
                    if (paytmContainer.visibility == View.VISIBLE && response.data!!.result.account_ifsc_code.isNullOrEmpty()) {
                        addMobileNumberEditText.setText(response.data!!.result.account_number)
                    }
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }
}
