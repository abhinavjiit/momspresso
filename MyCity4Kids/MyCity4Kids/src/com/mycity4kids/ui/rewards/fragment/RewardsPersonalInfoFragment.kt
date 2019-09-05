package com.mycity4kids.ui.rewards.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatSpinner
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
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.gson.Gson
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.DateTimeUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.ReferralCodeResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.CityInfoItem
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse
import com.mycity4kids.models.rewardsmodels.KidsInfoResponse
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.fragment.CityListingDialogFragment
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.dialog.PickerDialogFragment
import com.mycity4kids.utils.AppUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.apmem.tools.layouts.FlowLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**editLanguage
 * A simple [Fragment] subclass.
 */

const val VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE = 1000
const val REQUEST_SELECT_PLACE = 2000

class RewardsPersonalInfoFragment : BaseFragment(), ChangePreferredLanguageDialogFragment.OnClickDoneListener, CityListingDialogFragment.IChangeCity, PickerDialogFragment.OnClickDoneListener {

    var address: String? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    private lateinit var editReferralCode1: EditText


    override fun onItemClick(selectedValueName: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            //preSelectedInterest = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        } else if (popupType == Constants.PopListRequestType.LANGUAGE.name) {
            //preSelectedDurables = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        }
    }

    override fun onCitySelect(cityItem: CityInfoItem?) {
        editLocation.setText(cityItem!!.getCityName())
        currentCityName = cityItem!!.getCityName()
        selectedCityId = Integer.parseInt(cityItem!!.getId().replace("city-", ""))
        newSelectedCityId = cityItem!!.getId()
    }

    override fun onOtherCitySelect(pos: Int, cityName: String?) {
        cityList.get(pos).setCityName("Others($cityName)")
        editLocation.setText(cityList.get(pos).getCityName())
        currentCityName = cityName
        selectedCityId = Integer.parseInt(cityList.get(pos).getId().replace("city-", ""))
        newSelectedCityId = cityList.get(pos).getId()
    }

//    fun setOtherCityName(pos: Int, cityName: String) {
//        otherCityName = cityName
//        cityList.get(pos).setCityName("Others($cityName)")
//        editLocation.setText(cityList.get(pos).getCityName())
//    }

    override fun onItemClick(language: String?) {
        //editLanguage.setText(Constants.TypeOfLanguages.findById(language))
    }

    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var textSaveAndContinue: TextView
    private lateinit var saveAndContinueListener: SaveAndContinueListener
    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPhone: TextView
    private lateinit var editAddNumber: TextView
    private lateinit var editEmail: EditText
    private lateinit var referralMainLayout: RelativeLayout
    private lateinit var editReferralCode: EditText
    private lateinit var textReferCodeError: TextView
    private lateinit var spinnernumberOfKids: AppCompatSpinner
    private lateinit var checkAreYouExpecting: AppCompatCheckBox
    private lateinit var editLocation: EditText
    private lateinit var textVerify: TextView
    private lateinit var textApplyReferral: TextView
    private var apiGetResponse: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private var cityList = ArrayList<CityInfoItem>()
    private var selectedCityId: Int = 0
    private var newSelectedCityId: String? = null
    private var currentCityName: String? = null
    private var cityName: String? = null
    private var accountKitAuthCode = ""
    private lateinit var layoutNumberOfKids: RelativeLayout
    private lateinit var layoutMotherExptectedDate: RelativeLayout
    private lateinit var editExpectedDate: EditText
    private lateinit var checkNuclear: AppCompatRadioButton
    private lateinit var checkJoint: AppCompatRadioButton
    private lateinit var genderSpinner: AppCompatSpinner
    private lateinit var spinnerGender: AppCompatSpinner
    private lateinit var radioYes: AppCompatRadioButton
    private lateinit var radioNo: AppCompatRadioButton
    private lateinit var radioExpecting: AppCompatRadioButton
    private lateinit var linearKidsDetail: LinearLayout
    private lateinit var radioGroupWorkingStatus: RadioGroup
    private var preSelectedInterest = ArrayList<String>()
    private var preSelectedInterestForPosting = ArrayList<Int>()
    private var preSelectedLanguage = ArrayList<String>()
    private lateinit var editInterest: EditText
    private lateinit var linearInterest: LinearLayout
    private lateinit var floatingInterest: FlowLayout
    private lateinit var editLanguage: EditText
    private lateinit var linearLanguage: LinearLayout
    private lateinit var floatingLanguage: FlowLayout
    private lateinit var textEditInterest: TextView
    private lateinit var textEditLanguage: TextView
    private lateinit var textAddChild: TextView
    private lateinit var radioGroupAreMother: RadioGroup
    private lateinit var layoutDynamicNumberOfKids: LinearLayout
    private lateinit var textDeleteChild: TextView
    private lateinit var linearKidsEmptyView: LinearLayout
    private lateinit var editKidsName: EditText
    private var isComingFromCampaign = false
    private var isComingFromRewards = false
    private var referralCode: String = ""


    private var endIndex: Int = 0

    companion object {
        lateinit var textView: TextView
        private lateinit var textDOB: TextView
        private lateinit var textKidsDOB: TextView

        @JvmStatic
        fun newInstance(isComingFromRewards: Boolean = false, isComingfromCampaign: Boolean = false, referralCode: String = "") =
                RewardsPersonalInfoFragment().apply {
                    arguments = Bundle().apply {
                        this.putBoolean("isComingFromRewards", isComingFromRewards)
                        this.putBoolean("isComingfromCampaign", isComingfromCampaign)
                        this.putString("referralCode", referralCode)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_personal_info, container, false)

        textReferCodeError = containerView.findViewById(R.id.textReferCodeError)
        editReferralCode = containerView.findViewById(R.id.editReferralCode)
        referralMainLayout = containerView.findViewById(R.id.referralMainLayout)
        spinnernumberOfKids = containerView.findViewById(R.id.spinnernumberOfKids)
        checkAreYouExpecting = containerView.findViewById(R.id.checkAreYouExpecting)

        if (arguments != null) {
            isComingFromRewards = if (arguments!!.containsKey("isComingFromRewards")) {
                arguments!!.getBoolean("isComingFromRewards")
            } else {
                false
            }

            isComingFromCampaign = if (arguments!!.containsKey("isComingfromCampaign")) {
                arguments!!.getBoolean("isComingfromCampaign")
            } else {
                false
            }

            referralCode = if (arguments!!.containsKey("referralCode")) {
                arguments!!.getString("referralCode")
            } else {
                ""
            }
        }

        /*initialize XML components with clicks*/
        initializeXMLComponents()

        /*fetch data from server*/
        fetchRewardsData()



        return containerView
    }

    /*setting values to components*/
    private fun setValuesToComponents() {
        if (apiGetResponse.is_rewards_added == 1) {
            referralMainLayout.visibility = View.GONE
        } else {
            referralMainLayout.visibility = View.VISIBLE

        }
        if (!apiGetResponse.referred_by.isNullOrEmpty()) {
            editReferralCode.setText(apiGetResponse.referred_by)
            editReferralCode.isEnabled = true
        } else {
            if (!referralCode.trim().isNullOrEmpty()) {
                editReferralCode.setText(referralCode)
                editReferralCode.isEnabled = true
                apiGetResponse.referred_by = referralCode
            } else {
                apiGetResponse.referred_by = null

            }
        }
        if (!apiGetResponse.firstName.isNullOrBlank()) editFirstName.setText(apiGetResponse.firstName)
        if (!apiGetResponse.lastName.isNullOrBlank()) editLastName.setText(apiGetResponse.lastName)
        if (!apiGetResponse.contact.isNullOrBlank()) {
            editPhone.visibility = View.VISIBLE
            textVerify.visibility = View.VISIBLE
            editAddNumber.visibility = View.GONE
        } else {
            editPhone.visibility = View.GONE
            textVerify.visibility = View.GONE
            editAddNumber.visibility = View.VISIBLE
        }
        if (!apiGetResponse.email.isNullOrBlank()) editEmail.setText(apiGetResponse.email)
        if (!apiGetResponse.location.isNullOrBlank()) editLocation.setText(apiGetResponse.location)
        if (apiGetResponse.latitude != null)   lat = apiGetResponse.latitude!!

        if (apiGetResponse.longitude != null)   lng = apiGetResponse.longitude!!

        if (apiGetResponse.familyType != null) {
            if (apiGetResponse.familyType == 1) {
                checkNuclear.isChecked = true
            } else if (apiGetResponse.familyType == 2) {
                checkJoint.isChecked = true
            }
        }
        if (apiGetResponse.preferred_languages != null && apiGetResponse.preferred_languages!!.size > 0) {
            floatingLanguage.removeAllViews()
            textEditLanguage.visibility = View.VISIBLE
            editLanguage.visibility = View.GONE
            linearLanguage.visibility = View.VISIBLE
            apiGetResponse.preferred_languages!!.forEach {
                var interestName = Constants.TypeOfLanguages.findById(it)
                preSelectedLanguage.add(it)
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.shape_rewards_border_rectangular, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(interestName)
                catTextView.isSelected = true
                floatingLanguage.addView(subsubLL)
            }
        } else {
            editLanguage.visibility = View.VISIBLE
            linearLanguage.visibility = View.GONE
            textEditLanguage.visibility = View.GONE

        }

        if (apiGetResponse.interest != null && apiGetResponse.interest!!.isNotEmpty()) {
            floatingInterest.removeAllViews()
            textEditInterest.visibility = View.VISIBLE
            editInterest.visibility = View.GONE
            linearInterest.visibility = View.VISIBLE
            apiGetResponse.interest!!.forEach {
                var interestName = Constants.TypeOfInterest.findById(it.toInt())
                preSelectedInterest.add(it.toString())
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.shape_rewards_border_rectangular, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(interestName)
                catTextView.isSelected = true
                floatingInterest.addView(subsubLL)
            }
        } else {
            editInterest.visibility = View.VISIBLE
            linearInterest.visibility = View.GONE
            textEditInterest.visibility = View.GONE

        }

        if (apiGetResponse.isMother != null && apiGetResponse.kidsInfo != null && apiGetResponse.kidsInfo!!.isNotEmpty()) {
            radioYes.isChecked = true
        } else {
            radioNo.isChecked = true
        }

        if (apiGetResponse.workStatus != null) {
            if (apiGetResponse.workStatus == 0) {
                radioGroupWorkingStatus.check(R.id.radioNotWorking)
            } else if (apiGetResponse.workStatus == 1) {
                radioGroupWorkingStatus.check(R.id.radiokWorking)
            }
        }

        if (apiGetResponse.gender != null) {
            genderSpinner.setSelection(apiGetResponse.gender!!)
        }
        if (apiGetResponse.dob != null) {
            RewardsPersonalInfoFragment.textDOB.setText(AppUtils.convertTimestampToDate(apiGetResponse.dob))
        }

        if (apiGetResponse.isExpecting != null && apiGetResponse.isExpecting == 1 && apiGetResponse.expectedDate != null) {
            editExpectedDate.setText(AppUtils.convertTimestampToDate(apiGetResponse.expectedDate))
            checkAreYouExpecting.isChecked = true
            layoutMotherExptectedDate.visibility = View.VISIBLE
        } else {
            layoutMotherExptectedDate.visibility = View.GONE
        }

        /*this is not a master piece so handle with care*/
        if (apiGetResponse.kidsInfo != null && apiGetResponse.kidsInfo!!.isNotEmpty()) {
            for (i in 0..apiGetResponse.kidsInfo!!.size - 1) {
                if (i == 0 && apiGetResponse.kidsInfo!!.size == 1) {
                    createKidsDetailDynamicView(apiGetResponse.kidsInfo!!.get(i).gender!!, AppUtils.convertTimestampToDate(apiGetResponse.kidsInfo!!.get(i).dob), apiGetResponse.kidsInfo!!.get(i).name, false)
                } else {
                    createKidsDetailDynamicView(apiGetResponse.kidsInfo!!.get(i).gender!!, AppUtils.convertTimestampToDate(apiGetResponse.kidsInfo!!.get(i).dob), apiGetResponse.kidsInfo!!.get(i).name)
                }
            }
            linearKidsEmptyView.visibility = View.GONE
        } else {
            textDeleteChild.visibility = View.GONE
            linearKidsEmptyView.visibility = View.VISIBLE
        }
    }

    /*initialize XML components with clicks*/
    private fun initializeXMLComponents() {
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editLastName = containerView.findViewById(R.id.editLastName)
        editPhone = containerView.findViewById(R.id.editPhone)
        editAddNumber = containerView.findViewById(R.id.editAddNumber)
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editEmail = containerView.findViewById(R.id.editEmail)
        editLocation = containerView.findViewById(R.id.editLocation)
        textApplyReferral = containerView.findViewById(R.id.textApplyReferral)

        editAddNumber.setOnClickListener {
            varifyNumberWithFacebookAccountKit()
        }

        textApplyReferral.setOnClickListener {
            validateReferralCode()
        }

        editLocation.setOnClickListener {

            val typeFilter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(activity)
            startActivityForResult(intent, REQUEST_SELECT_PLACE)

        }

        textVerify = containerView.findViewById(R.id.textVerify)
        textVerify.setOnClickListener {
            varifyNumberWithFacebookAccountKit()
        }
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            if (prepareDataForPosting()) {
//                showProgressDialog(resources.getString(R.string.please_wait))
//                var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//                val retro = BaseApplication.getInstance().retrofit
//                val campaignAPI = retro.create(RewardsAPI::class.java)
//                val call = campaignAPI.sendRewardsapiDataTest(userId!!,apiGetResponse, pageValue = 4 )
//                call.enqueue(postDataofRewardsToServer)


                postDataofRewardsToServer()
            }
        }

        editExpectedDate = containerView.findViewById(R.id.editExpectedDate)
        radioGroupWorkingStatus = containerView.findViewById(R.id.radioGroupWorkingStatus)
        genderSpinner = containerView.findViewById(R.id.genderSpinner)
        spinnerGender = containerView.findViewById(R.id.spinnerGender)
        editKidsName = containerView.findViewById(R.id.editKidsName)
        layoutNumberOfKids = containerView.findViewById(R.id.layoutNumberOfKids)
        layoutMotherExptectedDate = containerView.findViewById(R.id.layoutExptectedDateOfDelivery)
        linearKidsDetail = containerView.findViewById(R.id.linearKidsDetail)
        textAddChild = containerView.findViewById(R.id.textAddChild)
        radioYes = containerView.findViewById(R.id.radioYes)
        radioNo = containerView.findViewById(R.id.radioNo)
        checkNuclear = containerView.findViewById(R.id.checkNuclear)
        checkJoint = containerView.findViewById(R.id.checkJoint)
        textEditInterest = containerView.findViewById(R.id.textEditInterest)
        floatingInterest = containerView.findViewById(R.id.floatingInterest)
        linearInterest = containerView.findViewById(R.id.linearInterest)
        editInterest = containerView.findViewById(R.id.editInterest)
        textEditInterest = containerView.findViewById(R.id.textEditInterest)
        floatingLanguage = containerView.findViewById(R.id.floatingLanguage)
        linearLanguage = containerView.findViewById(R.id.linearLanguage)
        editLanguage = containerView.findViewById(R.id.editLanguage)
        textEditLanguage = containerView.findViewById(R.id.textEditLanguage)
        radioGroupAreMother = containerView.findViewById<RadioGroup>(R.id.radioGroupAreMother)
        layoutDynamicNumberOfKids = containerView.findViewById(R.id.layoutDynamicNumberOfKids)
        RewardsPersonalInfoFragment.textKidsDOB = containerView.findViewById(R.id.textKidsDOB)
        RewardsPersonalInfoFragment.textDOB = containerView.findViewById(R.id.textDOB)
        textDeleteChild = containerView.findViewById(R.id.textDeleteChild)
        linearKidsEmptyView = containerView.findViewById(R.id.linearKidsEmptyView)

        textDeleteChild.setOnClickListener {
            if (linearKidsDetail.childCount > 0) {
                if (linearKidsDetail.childCount == 1) {
                    linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild).visibility = View.GONE
                    linearKidsEmptyView.visibility = View.GONE
                } else {
                    linearKidsEmptyView.visibility = View.GONE
                }

            }
        }








        (containerView.findViewById<CheckBox>(R.id.checkAreYouExpecting)).setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    layoutMotherExptectedDate.visibility = View.VISIBLE
                } else {
                    layoutMotherExptectedDate.visibility = View.GONE
                    editExpectedDate.setText("")
                }
            }
        })

        textAddChild.setOnClickListener {
            if (validateChildData()) {
                createKidsDetailDynamicView()
            } else {

            }
        }

        textEditInterest.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsPersonalInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        editInterest.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsPersonalInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        RewardsPersonalInfoFragment.textDOB.setOnClickListener {
            RewardsPersonalInfoFragment.textView = RewardsPersonalInfoFragment.textDOB
            showDatePickerDialog(true, isForParent = true)
        }

        RewardsPersonalInfoFragment.textKidsDOB.setOnClickListener {
            RewardsPersonalInfoFragment.textView = RewardsPersonalInfoFragment.textKidsDOB
            showDatePickerDialog(true)
        }

        textEditLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.LANGUAGE.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedLanguage, context = this@RewardsPersonalInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        editLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.LANGUAGE.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedLanguage, context = this@RewardsPersonalInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        val genderList = ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
        genderSpinner.adapter = spinAdapter
        genderSpinner.setSelection(1)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                genderSpinner.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        editExpectedDate.setOnClickListener {
            RewardsPersonalInfoFragment.textView = editExpectedDate
            showDatePickerDialog(false, true)
        }

        containerView.findViewById<TextView>(R.id.textSaveAndContinue).setOnClickListener {
            if (prepareDataForPosting()) {
//                showProgressDialog(resources.getString(R.string.please_wait))
//                var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//                val retro = BaseApplication.getInstance().retrofit
//                val campaignAPI = retro.create(RewardsAPI::class.java)
//                val call = campaignAPI.sendRewardsapiDataTest(userId!!,apiGetResponse, pageValue = 4 )
//                call.enqueue(postDataofRewardsToServer)
                postDataofRewardsToServer()
            }
        }

        containerView.findViewById<RadioGroup>(R.id.radioGroupFamilyType)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        0 -> {

                        }

                        1 -> {

                        }
                    }
                }

        containerView.findViewById<RadioGroup>(R.id.radioGroupAreMother)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        R.id.radioNo -> {
                            linearKidsDetail.removeAllViews()
                            layoutNumberOfKids.visibility = View.GONE
                            linearKidsDetail.visibility = View.GONE
                            linearKidsEmptyView.visibility = View.GONE
                            textAddChild.visibility = View.GONE
                            layoutDynamicNumberOfKids.visibility = View.GONE
                        }

                        R.id.radioYes -> {
                            textAddChild.visibility = View.VISIBLE
                            spinnernumberOfKids.setSelection(0)
                            layoutNumberOfKids.visibility = View.VISIBLE
                            linearKidsDetail.visibility = View.VISIBLE
                            linearKidsEmptyView.visibility = View.VISIBLE
                            layoutDynamicNumberOfKids.visibility = View.VISIBLE
                        }

                    }
                }
    }

    fun prepareDataForPosting(): Boolean {
        if (editFirstName.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_first_name)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.firstName = editFirstName.text.toString()
        }

        if (editLastName.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_last_name)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.lastName = editLastName.text.toString()
        }

        if (accountKitAuthCode.isNullOrEmpty() && apiGetResponse.contact.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_phone)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            if (!apiGetResponse.contact.isNullOrEmpty()) {
                apiGetResponse.contact = apiGetResponse.contact
                apiGetResponse.mobile_token = ""
            } else if (!accountKitAuthCode.isNullOrEmpty()) {
                apiGetResponse.mobile_token = accountKitAuthCode
                apiGetResponse.contact = ""
            }
        }

        if (editEmail.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_email)), Toast.LENGTH_SHORT).show()
            return false
        } else if (isMailValid()) {
            apiGetResponse.email = editEmail.text.toString().trim()
        } else {
            Toast.makeText(activity, resources.getString(R.string.please_enter_a_valid, resources.getString(R.string.rewards_email)), Toast.LENGTH_SHORT).show()
            return false
        }

        if (editLocation.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_location)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.location = editLocation.text.toString()
            address = editLocation.text.toString()
        }


        apiGetResponse.latitude = lat
        apiGetResponse.longitude = lng




        if (radioGroupWorkingStatus.checkedRadioButtonId == R.id.radiokWorking) {
            apiGetResponse.workStatus = 1
        } else {
            apiGetResponse.workStatus = 0
        }

        apiGetResponse.gender = if (genderSpinner.selectedItemPosition == 0) {
            0
        } else {
            1
        }

        if (RewardsPersonalInfoFragment.textDOB.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_dob)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsPersonalInfoFragment.textDOB.text.toString())
        }


        preSelectedLanguage.removeAll(Collections.singleton(""))


        if (preSelectedLanguage.isEmpty()) {


            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_language)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.preferred_languages = preSelectedLanguage
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            apiGetResponse.isMother = 1
        } else if (radioGroupAreMother.checkedRadioButtonId == R.id.radioNo) {
            apiGetResponse.isMother = 0
        }

        if (!preSelectedInterest.isEmpty()) {
            preSelectedInterestForPosting.clear()
            (preSelectedInterest).forEach {
                try {
                    preSelectedInterestForPosting.add(it.toInt())
                } catch (ex: Exception) {

                }
            }
            apiGetResponse.interest = preSelectedInterestForPosting
        }

        if (checkAreYouExpecting.isChecked) {
            if (editExpectedDate.text.isNullOrEmpty()) {
                Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_expected_date)), Toast.LENGTH_SHORT).show()
                return false
            } else {
                apiGetResponse.isExpecting = 1
                apiGetResponse.expectedDate = DateTimeUtils.convertStringToTimestamp(editExpectedDate.text.toString())
            }
        } else {
            apiGetResponse.isExpecting = 0
            apiGetResponse.expectedDate = 0
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            if (linearKidsDetail.childCount > 0) {
                var kidsList = ArrayList<KidsInfoResponse>()
                for (i in 0..linearKidsDetail.childCount) {
                    var kidsInfoResponse = KidsInfoResponse()
                    if (linearKidsDetail.getChildAt(i) != null) {
                        kidsInfoResponse.gender = if (linearKidsDetail.getChildAt(i).findViewById<Spinner>(R.id.spinnerGender).selectedItemPosition == 0) {
                            0
                        } else {
                            1
                        }
                        kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textKidsDOB).text.toString())
                        kidsInfoResponse.name = linearKidsDetail.getChildAt(i).findViewById<EditText>(R.id.editKidsName).text.toString()
                        kidsList.add(kidsInfoResponse)
                    }
                }
                apiGetResponse.kidsInfo = kidsList
                Log.e("dob text is ", RewardsPersonalInfoFragment.textKidsDOB.text.toString())
                if (linearKidsEmptyView.visibility == View.VISIBLE) {
                    if (!RewardsPersonalInfoFragment.textKidsDOB.text.isNullOrEmpty()) {
                        //if (apiGetResponse.kidsInfo.isNullOrEmpty()) {
                        var kidsInfoResponse = KidsInfoResponse()
                        kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                            0
                        } else {
                            1
                        }
                        kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsPersonalInfoFragment.textKidsDOB.text.toString())
                        kidsInfoResponse.name = editKidsName.text.toString()
                        apiGetResponse.kidsInfo!!.add(kidsInfoResponse)
                        //}
                    } else {
                        Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_number_of_kids)), Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            } else {
                if (!RewardsPersonalInfoFragment.textKidsDOB.text.isNullOrEmpty()) {
                    var kidsInfoLocal = ArrayList<KidsInfoResponse>()
                    var kidsInfoResponse = KidsInfoResponse()
                    kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                        0
                    } else {
                        1
                    }
                    kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsPersonalInfoFragment.textKidsDOB.text.toString())
                    kidsInfoResponse.name = editKidsName.text.toString()
                    kidsInfoLocal.add(kidsInfoResponse)
                    apiGetResponse.kidsInfo = kidsInfoLocal
                } else {
                    Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_number_of_kids)), Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        } else {
            apiGetResponse.kidsInfo = null
        }

/*        apiGetResponse.latitude = 28.7041
        apiGetResponse.longitude = 77.1025*/

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
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_SELECT_PLACE -> {
                    val place = PlaceAutocomplete.getPlace(activity, data)
                    if (!place.name.toString().isNullOrEmpty()) {
                        cityName = place.name.toString()
                        editLocation.setText(cityName)
                        lat = place.latLng.latitude
                        lng = place.latLng.longitude
                        address = cityName

                    }
                }
                VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE -> {
                    if (data != null && resultCode == Activity.RESULT_OK) {
                        accountKitAuthCode = (data!!.getParcelableExtra(AccountKitLoginResult.RESULT_KEY) as AccountKitLoginResult).authorizationCode!!
                        Log.e("account code ", accountKitAuthCode)
                        apiGetResponse.contact = null
                        editPhone.visibility = View.VISIBLE
                        textVerify.visibility = View.VISIBLE
                        editAddNumber.visibility = View.GONE
                    }
                }
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


    fun isMailValid(): Boolean {
        return !editEmail.text.isNullOrBlank() && StringUtils.isValidEmail(editEmail.text.toString().trim())
    }

    /*post data to server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "218f7fd8fe914c3887f508486fc9cf8e"
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            Log.e("sending json", Gson().toJson(apiGetResponse))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiDataForAny(userId!!, apiGetResponse, pageValue = 4).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<RewardsPersonalResponse> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: RewardsPersonalResponse) {
//                    Log.e("response is ", Gson().toJson(response.data))
                    if (response != null && response.code == 200) {
                        if (Constants.SUCCESS == response.status) {
                            if (isComingFromCampaign) {
                                SharedPrefUtils.setIsRewardsAdded(activity, "1")
                            }
                            saveAndContinueListener.profileOnSaveAndContinue()
                        } else if (Constants.FAILURE == response.status) {
                            Toast.makeText(context, response?.reason, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
//                    var data = (e as IllegalStateException).response().errorBody()!!.byteStream()
//
//                    if (code == 400) {
//                        var data = (e as HttpException).response().errorBody()!!.byteStream()
//                        var jsonParser = JsonParser()
//                        var jsonObject = jsonParser.parse(
//                                InputStreamReader(data, "UTF-8")) as JsonObject
//                        var reason = jsonObject.get("reason")
//                        Toast.makeText(context, reason.asString, Toast.LENGTH_SHORT).show()
//                    }
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

//    private val postDataofRewardsToServer = object : Callback<RewardsPersonalResponse> {
//
//        override fun onResponse(call: Call<RewardsPersonalResponse>, response: retrofit2.Response<RewardsPersonalResponse>) {
//            removeProgressDialog()
//            if (response == null || null == response.body()) {
//                val nee = NetworkErrorException(response.raw().toString())
//                Crashlytics.logException(nee)
//                return
//            }
//            try {
//                val responseData = response.body()
////                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
////                    if (responseData.data!!.result!!.size > 0) {
////                        campaignList.addAll(responseData.data!!.result as ArrayList<CampaignDataListResult>)
////                        adapter.notifyDataSetChanged()
////                    }
////                } else {
////                }
//            } catch (e: Exception) {
//                Crashlytics.logException(e)
//                Log.d("MC4kException", Log.getStackTraceString(e))
//            }
//        }
//
//        override fun onFailure(call: Call<RewardsPersonalResponse>, t: Throwable) {
//            removeProgressDialog()
//            Crashlytics.logException(t)
//            Log.d("MC4kException", Log.getStackTraceString(t))
//        }
//    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "218f7fd8fe914c3887f508486fc9cf8e"
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData(userId!!, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

                        /*getting city data from server*/
                        fetchCityData()

                        /*setting values to components*/
                        setValuesToComponents()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

    private fun fetchCityData() {
        BaseApplication.getInstance().retrofit.create(ConfigAPIs::class.java).getCityConfigRx().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CityConfigResultResponse>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(response: BaseResponseGeneric<CityConfigResultResponse>) {
                if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                    if (response.data!!.result != null && response!!.data!!.result != null && response!!.data!!.result.cityData.isNotEmpty()) {
                        val currentCity = SharedPrefUtils.getCurrentCityModel(saveAndContinueListener as RewardsContainerActivity)
                        (response!!.data!!.result.cityData).forEach {
                            if (AppConstants.ALL_CITY_NEW_ID != it.id) {
                                cityList.add(it)
                            }
                            if (AppConstants.OTHERS_NEW_CITY_ID == it.id) {
                                if (currentCity.name != null && "Others" != currentCity.name && currentCity.id == AppConstants.OTHERS_CITY_ID) {
                                    cityList.get(cityList.size - 1).cityName = ("Others(" + currentCity.name + ")")
                                }
                            }
                        }

                        (cityList).forEach {
                            val cId = Integer.parseInt(it.id!!.replace("city-", ""))
                            it.isSelected = currentCity.id == cId
                        }
                    }
                } else {

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
            }
        })
    }

    fun showDatePickerDialog(isShowTillCurrent: Boolean, isShowFutureDate: Boolean = false, isForParent: Boolean = false) {
        val newFragment = RewardsPersonalInfoFragment.DatePickerFragment()
        var bundle = Bundle()
        bundle.putBoolean("is_show_current_only", isShowTillCurrent)
        bundle.putBoolean("is_show_future_only", isShowFutureDate)
        bundle.putBoolean("is_for_parent", isForParent)
        newFragment.arguments = bundle
        newFragment.show(activity!!.supportFragmentManager, "datePicker")
    }


    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        internal var cancel: Boolean = false
        internal val c = Calendar.getInstance()
        internal var curent_year = c.get(Calendar.YEAR)
        internal var current_month = c.get(Calendar.MONTH)
        internal var current_day = c.get(Calendar.DAY_OF_MONTH)
        var isShowTillCurrent: Boolean = false
        var isShowFutureOnly: Boolean = false
        var isForParent: Boolean = false


        @SuppressLint("NewApi")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val dlg = DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day)

            if (arguments != null) {
                isShowTillCurrent = arguments!!.getBoolean("is_show_current_only", false)
                isShowFutureOnly = arguments!!.getBoolean("is_show_future_only", false)
                isForParent = arguments!!.getBoolean("is_for_parent", false)
            }
            dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (isShowTillCurrent) {
                dlg.datePicker.maxDate = c.timeInMillis
            }

            if (isShowFutureOnly) {
                dlg.datePicker.minDate = c.timeInMillis
                c.add(Calendar.YEAR, 1)
                dlg.datePicker.maxDate = c.timeInMillis
            }

            if (isForParent) {
                c.add(Calendar.YEAR, -16)
                dlg.datePicker.maxDate = c.timeInMillis
            }

            return dlg
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            if (RewardsPersonalInfoFragment.textView != null) {
                val sel_date = "" + day + "-" + (month + 1) + "-" + year
//                if (chkTime(sel_date)) {
                RewardsPersonalInfoFragment.textView.setText("" + day + "-" + (month + 1) + "-" + year)
//                } else {
//                    //RewardsPersonalInfoFragment.textView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year)
//                }
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

    private fun validateChildData(): Boolean {
        Log.e("text value", " " + RewardsPersonalInfoFragment.textKidsDOB.text + " " + linearKidsEmptyView.visibility)
        if (linearKidsEmptyView.visibility == View.VISIBLE && RewardsPersonalInfoFragment.textKidsDOB.text.isBlank()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_dob)), Toast.LENGTH_SHORT).show()
            return false
        } else if (linearKidsEmptyView.visibility == View.GONE) {
            linearKidsEmptyView.visibility = View.VISIBLE
            textDeleteChild.visibility = View.VISIBLE
            if (linearKidsDetail.childCount > 0) {
                for (i in 0..linearKidsDetail.childCount - 1) {
                    linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textDeleteChild).visibility = View.VISIBLE
                }
            }
            return false
        } else if (linearKidsEmptyView.visibility == View.VISIBLE) {
            textDeleteChild.visibility = View.VISIBLE
        }

        return true
    }

    fun createKidsDetailDynamicView(gender: Int? = null, date: String = "", name: String? = "", shouldDelteShow: Boolean = true) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val indexView = inflater.inflate(R.layout.dynamic_child_view, null)
        var textHeader = indexView.findViewById<TextView>(R.id.textHeader)
        var textDelete = indexView.findViewById<TextView>(R.id.textDeleteChild)
        var editKidsName = indexView.findViewById<EditText>(R.id.editKidsName)
        if (shouldDelteShow) {
            textDelete.visibility = View.VISIBLE
        } else {
            textDelete.visibility = View.GONE
        }

        textDelete.setOnClickListener {
            for (i in 0..linearKidsDetail.childCount) {
                if (linearKidsEmptyView.visibility == View.VISIBLE) {
                    if (linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textDeleteChild) == it) {
                        linearKidsDetail.removeViewAt(i)
                        break
                    }
                } else {
                    if (linearKidsDetail.childCount == 1) {
                        textDelete.visibility = View.GONE
                    } else {
                        if (linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textDeleteChild) == it) {
                            linearKidsDetail.removeViewAt(i)
                            break
                        }
                    }
                }
            }
            if (linearKidsEmptyView.visibility == View.GONE && linearKidsDetail.childCount == 1) {
                linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild).visibility = View.GONE
            } else if (linearKidsEmptyView.visibility == View.VISIBLE && linearKidsDetail.childCount == 0) {
                textDeleteChild.visibility = View.GONE
            }

        }
        var spinnerGender = indexView.findViewById<Spinner>(R.id.spinnerGender)
        var textDOB = indexView.findViewById<TextView>(R.id.textKidsDOB)

        //textHeader.setText(String.format(resources.getString(R.string.kids_number), linearKidsDetail.childCount+1))
        val genderList = java.util.ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        textDOB.setOnClickListener {
            RewardsPersonalInfoFragment.textView = it as TextView
            showDatePickerDialog(true, false)
        }

        if (gender != null && !date.isNullOrEmpty()) {
            textDOB.text = date
            spinnerGender.setSelection(gender)
            if (!name.isNullOrEmpty()) {
                editKidsName.setText(name)
            }
        } else {
            textDOB.text = RewardsPersonalInfoFragment.textKidsDOB.text
            spinnerGender.setSelection(this.spinnerGender.selectedItemPosition)
            editKidsName.text = this.editKidsName.text
            this.spinnerGender.setSelection(0)
            RewardsPersonalInfoFragment.textKidsDOB.text = ""
            this.editKidsName.setText("")
        }

        linearKidsDetail.addView(indexView)
    }

    private fun setFloatingLayout(preSelectedItems: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            floatingInterest.removeAllViews()
            if (preSelectedItems.isNotEmpty()) {
                textEditInterest.visibility = View.VISIBLE
                linearInterest.visibility = View.VISIBLE
                editInterest.visibility = View.GONE
            } else {
                textEditInterest.visibility = View.GONE
                linearInterest.visibility = View.GONE
                editInterest.visibility = View.VISIBLE
            }
            preSelectedInterest.clear()
            preSelectedItems.forEach {
                var name = if (Constants.TypeOfInterest.findByName(it) != null) {
                    Constants.TypeOfInterest.findByName(it)
                } else {
                    null
                }
                if (name != null) {
                    preSelectedInterest.add(name)
                }
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.shape_rewards_border_rectangular, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingInterest.addView(subsubLL)
            }
        } else if (popupType == Constants.PopListRequestType.LANGUAGE.name) {
            floatingLanguage.removeAllViews()
            if (preSelectedItems.isNotEmpty()) {
                textEditLanguage.visibility = View.VISIBLE
                linearLanguage.visibility = View.VISIBLE
                editLanguage.visibility = View.GONE
            } else {
                textEditLanguage.visibility = View.GONE
                linearLanguage.visibility = View.GONE
                editLanguage.visibility = View.VISIBLE
            }
            preSelectedLanguage.clear()
            preSelectedItems.forEach {
                var name = if (Constants.TypeOfLanguagesWithContent.findByName(it) != null) {
                    Constants.TypeOfLanguages.findByName(it)
                } else {
                    null
                }
                if (name != null) {
                    preSelectedLanguage.add(name)
                }
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.shape_rewards_border_rectangular, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingLanguage.addView(subsubLL)
            }
        }
    }


    /*fetch data from server*/
    private fun validateReferralCode() {
        if (!editReferralCode.text.trim().isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).validateReferralCode(editReferralCode.text.toString()!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ReferralCodeResult>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<ReferralCodeResult>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response!!.data!!.result != null) {
                        if (response!!.data!!.result.is_valid) {
                            // editReferralCode.setText(response.data!!.result.referral_code)
                            //  textReferCodeError.visibility = View.GONE
                            textReferCodeError.setTextColor(activity!!.resources.getColor(R.color.green_dark))
                            textReferCodeError.setText("Successfully Applied")
                            editReferralCode.isEnabled = true
                            textApplyReferral.isEnabled = false
                        } else {
                            textReferCodeError.visibility = View.VISIBLE
                            textReferCodeError.setText("Code is not valid")
                            textReferCodeError.setTextColor(activity!!.resources.getColor(R.color.campaign_refer_code_error))
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

/*    fun fetchLangLat(address: String) {
        val coder = Geocoder(activity as RewardsContainerActivity)
        var addresses: List<Address>
        (activity as RewardsContainerActivity).runOnUiThread {
            addresses = coder.getFromLocationName(address, 5)
            if (addresses == null || addresses.isNotEmpty() || addresses.isNullOrEmpty()) {

            } else {
                val location: Address = addresses.get(0)
                lat = location.getLatitude()
                lng = location.getLongitude()
                Log.i("lat", lat.toString())
                Log.i("lat", lng.toString())
            }
        }
    }*/

}

