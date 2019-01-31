package com.mycity4kids.ui.rewards.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatSpinner
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.ui.ThemeUIManager
import com.google.api.client.util.DateTime
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.DateTimeUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.activity.ActivityLogin.APP_REQUEST_CODE
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.observable.ObservableReplay.observeOn
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.aa_rate_app.view.*
import kotlinx.android.synthetic.main.activity_write_a_review.*
import kotlinx.android.synthetic.main.dynamic_child_view.*
import kotlinx.android.synthetic.main.fragment_rewards_personal_info.*
import kotlinx.android.synthetic.main.group_about_item.*
import org.jsoup.Connection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**editLanguage
 * A simple [Fragment] subclass.
 */

const val VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE = 1000

class RewardsPersonalInfoFragment : BaseFragment(), ChangePreferredLanguageDialogFragment.OnClickDoneListener {
    override fun onItemClick(language: String?) {
        editLanguage.setText(Constants.TypeOfLanguages.findById(language))
    }

    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var textSaveAndContinue: TextView
    private lateinit var saveAndContinueListener: SaveAndContinueListener
    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editLocation: EditText
    private lateinit var editLanguage: EditText
    //private lateinit var textDOB: TextView
    private lateinit var textVerify: TextView
    private lateinit var genderSpinner: AppCompatSpinner
    private lateinit var radioGroupWorkingStatus: RadioGroup
    private lateinit var apiGetResponse: RewardsDetailsResultResonse

    companion object {
        private lateinit var textDOB: TextView

        @JvmStatic
        fun newInstance() =
                RewardsPersonalInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_personal_info, container, false)

        /*initialize XML components with clicks*/
        initializeXMLComponents()

        /*fetch data from server*/
        fetchRewardsData()

        return containerView
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData("8ffb68f436724516850cdfdb5d064d69", 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

                        /*setting values to components*/
                        setValuesToComponents()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {

                }
            })
        }
    }

    /*setting values to components*/
    private fun setValuesToComponents() {
        if (!apiGetResponse.firstName.isNullOrBlank()) editFirstName.setText(apiGetResponse.firstName)
        if (!apiGetResponse.lastName.isNullOrBlank()) editLastName.setText(apiGetResponse.lastName)
        if (!apiGetResponse.contact.isNullOrBlank()) editPhone.setText(apiGetResponse.contact)
        if (!apiGetResponse.email.isNullOrBlank()) editEmail.setText(apiGetResponse.email)
        if (apiGetResponse.dob != null && apiGetResponse.dob!! > 0) textDOB.setText(DateTimeUtils.getDateFromTimestamp(apiGetResponse.dob!!.toLong()))
        if (!apiGetResponse.location.isNullOrBlank()) editLocation.setText(apiGetResponse.location)
        if (apiGetResponse.motherTongue.isNullOrBlank()) editLanguage.setText(apiGetResponse.motherTongue)
        if (apiGetResponse.workStatus != null) {
            if (apiGetResponse.workStatus == 0) {
                radioGroupWorkingStatus.check(R.id.radioNotWorking)
            } else if (apiGetResponse.workStatus == 1) {
                radioGroupWorkingStatus.check(R.id.radiokWorking)
            }
        }
    }

    /*initialize XML components with clicks*/
    private fun initializeXMLComponents() {
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editLastName = containerView.findViewById(R.id.editLastName)
        editPhone = containerView.findViewById(R.id.editPhone)
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editEmail = containerView.findViewById(R.id.editEmail)
        editLocation = containerView.findViewById(R.id.editLocation)
        editLanguage = containerView.findViewById(R.id.editLanguage)
        genderSpinner = containerView.findViewById(R.id.genderSpinner)
        RewardsPersonalInfoFragment.textDOB = containerView.findViewById(R.id.textDOB)
        RewardsPersonalInfoFragment.textDOB.setOnClickListener {
            showDatePickerDialog()
        }
        radioGroupWorkingStatus = containerView.findViewById(R.id.radioGroupWorkingStatus)
        textVerify = containerView.findViewById(R.id.textVerify)
        textVerify.setOnClickListener {
            varifyNumberWithFacebookAccountKit()
        }
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            if(prepareDataForPosting()){
                postDataofRewardsToServer()
            }

            //postDataofRewardsToServer()
        }

        editLanguage.setOnClickListener {
            val changePreferredLanguageDialogFragment = ChangePreferredLanguageDialogFragment.newInstance(this, true)
            val fm = fragmentManager
            changePreferredLanguageDialogFragment.isCancelable = true
            changePreferredLanguageDialogFragment.show(fm, "Choose video option")
        }

        val genderList = ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        genderSpinner.adapter = spinAdapter
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                genderSpinner.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    fun prepareDataForPosting(): Boolean {
        if (editFirstName.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.firstName = editFirstName.text.toString()
        }

        if (editLastName.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.lastName = editLastName.text.toString()
        }

        if (!apiGetResponse.contact.isNullOrEmpty() || !apiGetResponse.mobile_token.isNullOrEmpty()) {
            apiGetResponse.contact = ""
        } else {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            return false
        }

        if (editEmail.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            return false
        } else if (isMailValid()) {
            apiGetResponse.email = editEmail.text.toString()
        } else {
            Toast.makeText(activity, resources.getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerGender.selectedItem.toString().isNullOrEmpty()) {
            if (spinnerGender.selectedItem.toString().trim().toLowerCase().equals("Male")) {
                apiGetResponse.gender = 0
            } else if (spinnerGender.selectedItem.toString().trim().toLowerCase().equals("Female")) {
                apiGetResponse.gender = 1
            }
        }

        if (RewardsPersonalInfoFragment.textDOB.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.dob = convertStringToTimestamp()
        }

        apiGetResponse.location = editLocation.text.toString()

        if(radioGroupWorkingStatus.checkedRadioButtonId == R.id.radiokWorking){
            apiGetResponse.workStatus =1
        }else{
            apiGetResponse.workStatus = 0
        }

        if(!editLanguage.text.isNullOrEmpty()){
            apiGetResponse.motherTongue = Constants.TypeOfLanguages.findByName(editLanguage.text.toString())
        }

        return true
    }

    private fun varifyNumberWithFacebookAccountKit() {
        val intent = Intent(activity, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE, AccountKitActivity.ResponseType.CODE)
        val themeId = R.style.AppLoginTheme
        val themeManager = ThemeUIManager(themeId)
        configurationBuilder.setUIManager(themeManager)
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build())
        startActivityForResult(intent, VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            var authCode = (data!!.getParcelableExtra(AccountKitLoginResult.RESULT_KEY) as AccountKitLoginResult).authorizationCode!!
            varifyNumberWithFacebookAccountKit()
            Log.e("authcode ", authCode)
        }
    }

    /*fetch data from server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData("8ffb68f436724516850cdfdb5d064d69", apiGetResponse, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

                    } else {

                    }
                }

                override fun onError(e: Throwable) {

                }
            })
        }
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(activity.supportFragmentManager, "datePicker")
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        internal var cancel: Boolean = false

        internal val c = Calendar.getInstance()
        internal var curent_year = c.get(Calendar.YEAR)
        internal var current_month = c.get(Calendar.MONTH)
        internal var current_day = c.get(Calendar.DAY_OF_MONTH)

        @SuppressLint("NewApi")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val dlg = DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day)
            dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.datePicker.maxDate = c.timeInMillis
            return dlg
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            if (RewardsPersonalInfoFragment.textDOB != null) {
                val sel_date = "" + day + "-" + (month + 1) + "-" + year
                if (chkTime(sel_date)) {
                    RewardsPersonalInfoFragment.textDOB.setText("" + day + "-" + (month + 1) + "-" + year)
                } else {
                    RewardsPersonalInfoFragment.textDOB.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year)
                }
            }
        }

        fun chkTime(time: String): Boolean {
            var result = true

            val currentime = "" + System.currentTimeMillis() / 1000
            if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
                result = false

            return result
        }

        fun convertDate(convertdate: String): String {
            var timestamp = ""
            try {
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val dateobj = formatter.parse(convertdate)
                timestamp = "" + dateobj.time / 1000
                return timestamp
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return timestamp
        }
    }

    private fun getLanguageCode() {
        if (!editLanguage.text.isNullOrEmpty()) {
            var languageCode = Constants.TypeOfLanguages.findByName(editLanguage.text.toString())
            if (!languageCode.equals("")) {
                Constants.TypeOfLanguagesCodes.findByName(languageCode)
            } else {
                editLanguage.setText("")
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            saveAndContinueListener = context
        }
    }


    interface SaveAndContinueListener {
        fun profileOnSaveAndContinue()
    }

    fun convertStringToTimestamp(): Long {
        return DateTimeUtils.convertStringToTimestamp(RewardsPersonalInfoFragment.textDOB.getText().toString())
    }


    fun isMailValid(): Boolean {
        return !editEmail.text.isNullOrBlank() && StringUtils.isValidEmail(editEmail.text.toString())
    }
}

