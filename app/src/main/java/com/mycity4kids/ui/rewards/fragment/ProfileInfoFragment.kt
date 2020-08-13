package com.mycity4kids.ui.rewards.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.UserHandleAvailabilityResponse
import com.mycity4kids.models.request.UpdateUserDetailsRequest
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.CityInfoItem
import com.mycity4kids.models.response.ImageUploadResponse
import com.mycity4kids.models.response.KidsModel
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI
import com.mycity4kids.ui.activity.ChangePasswordActivity
import com.mycity4kids.ui.activity.OTPActivity
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.fragment.CityListingDialogFragment
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.dialog.PickerDialogFragment
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.GenericFileProvider
import com.mycity4kids.utils.StringUtils
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.Date
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apmem.tools.layouts.FlowLayout
import retrofit2.Call
import retrofit2.Callback

const val ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111
const val ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113

class ProfileInfoFragment : BaseFragment(),
    CityListingDialogFragment.IChangeCity, PickerDialogFragment.OnClickDoneListener {

    private var isNewRegistration: Boolean = false
    var address: String? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onItemClick(selectedValueName: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            // preSelectedInterest = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        } else if (popupType == Constants.PopListRequestType.LANGUAGE.name) {
            // preSelectedDurables = selectedValue
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

    private lateinit var containerView: View
    private lateinit var profileImageView: ImageView
    private lateinit var editImageView: ImageView
    private lateinit var aboutEditText: EditText
    private lateinit var handleNameTextView: TextView
    private lateinit var changePasswordTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var textSaveAndContinue: TextView
    private lateinit var saveAndContinueListener: SaveAndContinueListener
    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var labelFirstName: TextView
    private lateinit var labelLastName: TextView
    private lateinit var editPhone: TextView
    private lateinit var editAddNumber: TextView
    private lateinit var editEmail: EditText
    private lateinit var referralMainLayout: RelativeLayout
    private lateinit var editReferralCode: EditText
    private lateinit var userHandleTextView: EditText
    private lateinit var checkTextView: TextView
    private lateinit var textReferCodeError: TextView
    private lateinit var spinnernumberOfKids: AppCompatSpinner
    private lateinit var checkAreYouExpecting: AppCompatCheckBox
    private lateinit var editLocation: EditText
    private lateinit var textVerify: TextView
    private lateinit var textApplyReferral: TextView
    private var apiGetResponse: UserDetailResult = UserDetailResult()
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
    private lateinit var layoutWorking: RelativeLayout
    private lateinit var linearKidsDetail: LinearLayout
    private lateinit var radioGroupWorkingStatus: RadioGroup
    private var preSelectedInterest = ArrayList<String>()
    private var preSelectedInterestForPosting = ArrayList<String>()
    private var preSelectedLanguage = ArrayList<String>()
    private lateinit var editInterest: EditText
    private lateinit var linearInterest: LinearLayout
    private lateinit var floatingInterest: FlowLayout
    private lateinit var editLanguage: EditText
    private lateinit var linearLanguage: LinearLayout
    private lateinit var floatingLanguage: FlowLayout
    private lateinit var textEditInterest: TextView
    private lateinit var textEditLanguage: TextView
    private lateinit var userAvailabilityResultTextView: TextView
    private lateinit var textAddChild: TextView
    private lateinit var radioGroupAreMother: RadioGroup
    private lateinit var layoutDynamicNumberOfKids: LinearLayout
    private lateinit var textDeleteChild: TextView
    private lateinit var linearKidsEmptyView: LinearLayout
    private lateinit var langLayout: RelativeLayout
    private lateinit var interestLayout: RelativeLayout
    private lateinit var editKidsName: EditText
    private var isComingFromCampaign = false
    private var isComingFromRewards = false
    private var isHandleChecked = false
    private var referralCode: String = ""
    private var defaultHandle: String = ""
    private lateinit var spinAdapter: CustomSpinnerAdapter

    private val REQUEST_CAMERA = 0
    private val REQUEST_EDIT_PICTURE = 1
    private val PERMISSIONS_EDIT_PICTURE = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"
    private lateinit var rootView: View
    private lateinit var photoFile: File
    private lateinit var mCurrentPhotoPath: String
    private lateinit var absoluteImagePath: String
    private lateinit var imageUri: Uri
    private var spannable: SpannableString? = null
    private lateinit var profileDisclaimerTwo: TextView

    private var endIndex: Int = 0

    companion object {
        lateinit var textView: TextView
        private lateinit var textDOB: TextView
        private lateinit var textKidsDOB: TextView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.user_personal_info_fragment, container, false)

        activity?.let {
            Utils.pushGenericEvent(
                activity, "Show_MyMoney_RegistrationForm_Registered",
                SharedPrefUtils.getUserDetailModel(it).dynamoId, "ProfileInfoFragment"
            )
        }

        textReferCodeError = containerView.findViewById(R.id.textReferCodeError)
        editReferralCode = containerView.findViewById(R.id.editReferralCode)
        referralMainLayout = containerView.findViewById(R.id.referralMainLayout)
        spinnernumberOfKids = containerView.findViewById(R.id.spinnernumberOfKids)
        checkAreYouExpecting = containerView.findViewById(R.id.checkAreYouExpecting)
        profileImageView = containerView.findViewById(R.id.profileImageView)
        editImageView = containerView.findViewById(R.id.editImageView)
        userHandleTextView = containerView.findViewById(R.id.userHandleTextView)
        checkTextView = containerView.findViewById(R.id.checkTextView)
        profileDisclaimerTwo = containerView.findViewById(R.id.profile_disclaimer_two)
        userAvailabilityResultTextView =
            containerView.findViewById(R.id.userAvailabilityResultTextView)

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
                arguments!!.getString("referralCode").toString()
            } else {
                ""
            }
        }

        try {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))
                .placeholder(R.drawable.family_xxhdpi)
                .error(R.drawable.family_xxhdpi).into(profileImageView)
        } catch (e: Exception) {
            profileImageView.setImageResource(R.drawable.family_xxhdpi)
        }

        initializeXMLComponents()
        fetchRewardsData()

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
        profileDisclaimerTwo.movementMethod = LinkMovementMethod.getInstance()
        profileDisclaimerTwo.highlightColor = Color.BLUE
        profileDisclaimerTwo.setText(spannable)

        return containerView
    }

    private fun setValuesToComponents() {
        if (!apiGetResponse.firstName.isNullOrBlank()) editFirstName.setText(apiGetResponse.firstName)
        if (!apiGetResponse.lastName.isNullOrBlank()) editLastName.setText(apiGetResponse.lastName)
        if (!apiGetResponse.mobile.isNullOrBlank()) {
            editPhone.visibility = View.VISIBLE
            textVerify.visibility = View.VISIBLE
            editAddNumber.visibility = View.GONE
        } else {
            editPhone.visibility = View.GONE
            textVerify.visibility = View.GONE
            editAddNumber.visibility = View.VISIBLE
        }

        if (!apiGetResponse.email.isNullOrBlank()) {
            editEmail.setText(apiGetResponse.email)
        }

        if (apiGetResponse.emailValidated.equals("1")) {
            editEmail.isEnabled = false
            editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0)
        } else {
            Toast.makeText(
                activity,
                "Email verification is pending",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (!apiGetResponse.isUserHandleUpdated.isNullOrEmpty() && apiGetResponse.isUserHandleUpdated.equals(
                "1"
            )) {
            userHandleTextView.setText(apiGetResponse.userHandle)
            userHandleTextView.isEnabled = false
            checkTextView.visibility = View.GONE
        } else if (!apiGetResponse.isUserHandleUpdated.isNullOrEmpty() && apiGetResponse.isUserHandleUpdated.equals(
                "0"
            ) && !apiGetResponse.userHandle.isNullOrEmpty()) {
            userHandleTextView.setText(apiGetResponse.userHandle)
            defaultHandle = apiGetResponse.userHandle
            checkTextView.visibility = View.GONE
            isHandleChecked = true
        }

        if (!apiGetResponse.cityName.isNullOrBlank()) editLocation.setText(apiGetResponse.cityName)
        if (apiGetResponse.latitude != null) lat = apiGetResponse.latitude!!

        if (apiGetResponse.longitude != null) lng = apiGetResponse.longitude!!

        if (!apiGetResponse.userBio.isNullOrBlank()) aboutEditText.setText(apiGetResponse.userBio)

        if (!apiGetResponse.blogTitle.isNullOrBlank()) handleNameTextView.setText(apiGetResponse.blogTitle)
        /*if (apiGetResponse.familyType != null) {
            if (apiGetResponse.familyType == 1) {
                checkNuclear.isChecked = true
            } else if (apiGetResponse.familyType == 2) {
                checkJoint.isChecked = true
            }
        }*/
        if (apiGetResponse.preferredLanguages != null && apiGetResponse.preferredLanguages!!.size > 0) {
            floatingLanguage.removeAllViews()
            textEditLanguage.visibility = View.VISIBLE
            editLanguage.visibility = View.GONE
            linearLanguage.visibility = View.VISIBLE
            apiGetResponse.preferredLanguages!!.forEach {
                var interestName = Constants.TypeOfLanguages.findById(it)
                preSelectedLanguage.add(it)
                context?.let {
                    val subsubLL = LayoutInflater.from(context).inflate(
                        R.layout.shape_rewards_border_rectangular,
                        null
                    ) as LinearLayout
                    val catTextView = subsubLL.getChildAt(0) as TextView
                    catTextView.setText(interestName)
                    catTextView.isSelected = true
                    floatingLanguage.addView(subsubLL)
                }
            }
        } else {
            linearLanguage.visibility = View.GONE
            textEditLanguage.visibility = View.GONE
        }

        if (apiGetResponse.interests != null && apiGetResponse.interests!!.isNotEmpty()) {
            floatingInterest.removeAllViews()
            textEditInterest.visibility = View.VISIBLE
            editInterest.visibility = View.GONE
            linearInterest.visibility = View.VISIBLE
            apiGetResponse.interests!!.forEach {
                var interestName = Constants.TypeOfInterest.findById(it.toInt())
                preSelectedInterest.add(it.toString())
                context?.let {
                    val subsubLL = LayoutInflater.from(context).inflate(
                        R.layout.shape_rewards_border_rectangular,
                        null
                    ) as LinearLayout
                    val catTextView = subsubLL.getChildAt(0) as TextView
                    catTextView.setText(interestName)
                    catTextView.isSelected = true
                    floatingInterest.addView(subsubLL)
                }
            }
        } else {
            linearInterest.visibility = View.GONE
            textEditInterest.visibility = View.GONE
        }

        if (apiGetResponse.isMother != null && apiGetResponse.isMother.equals("1") && apiGetResponse.kids != null && apiGetResponse.kids!!.isNotEmpty()) {
            radioYes.isChecked = true
        } else {
            radioNo.isChecked = true
        }

        if (apiGetResponse.workStatus != null) {
            if (apiGetResponse.workStatus.equals("0")) {
                radioGroupWorkingStatus.check(R.id.radioNotWorking)
            } else if (apiGetResponse.workStatus.equals("1")) {
                radioGroupWorkingStatus.check(R.id.radiokWorking)
            }
        }

        if (apiGetResponse.gender != null) {
            val selectionPosition =
                spinAdapter.getPosition(StringUtils.firstLetterToUpperCase(apiGetResponse.gender))
            genderSpinner.setSelection(selectionPosition)
        }
        if (apiGetResponse.dob != null) {
            textDOB.setText(DateTimeUtils.getDOBMilliTimestamp(apiGetResponse.dob))
        }

        if (apiGetResponse.isExpected != null && apiGetResponse.isExpected.equals("1") && !apiGetResponse.expectedDate.isNullOrEmpty()) {
            editExpectedDate.setText(DateTimeUtils.getDOBMilliTimestamp(apiGetResponse.expectedDate))
            checkAreYouExpecting.isChecked = true
            layoutMotherExptectedDate.visibility = View.VISIBLE
        } else {
            layoutMotherExptectedDate.visibility = View.GONE
        }

        if (apiGetResponse.kids != null && apiGetResponse.kids!!.isNotEmpty()) {
            for (i in 0..apiGetResponse.kids!!.size - 1) {
                if (i == 0 && apiGetResponse.kids!!.size == 1) {
                    createKidsDetailDynamicView(
                        apiGetResponse.kids!!.get(i).gender,
                        DateTimeUtils.getDOBMilliTimestamp(apiGetResponse.kids!!.get(i).getBirthDay()),
                        apiGetResponse.kids!!.get(i).name,
                        false
                    )
                } else {
                    createKidsDetailDynamicView(
                        apiGetResponse.kids!!.get(i).gender,
                        DateTimeUtils.getDOBMilliTimestamp(apiGetResponse.kids!!.get(i).getBirthDay()),
                        apiGetResponse.kids!!.get(i).name
                    )
                }
            }
            linearKidsEmptyView.visibility = View.GONE
        } else {
            textDeleteChild.visibility = View.GONE
            linearKidsEmptyView.visibility = View.VISIBLE
        }
    }

    private fun initializeXMLComponents() {
        rootView = containerView.findViewById(R.id.rootView)
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editLastName = containerView.findViewById(R.id.editLastName)
        labelFirstName = containerView.findViewById(R.id.labelFirstName)
        labelLastName = containerView.findViewById(R.id.labelLastName)
        editPhone = containerView.findViewById(R.id.editPhone)
        editAddNumber = containerView.findViewById(R.id.editAddNumber)
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editEmail = containerView.findViewById(R.id.emailTextView)
        editLocation = containerView.findViewById(R.id.editLocation)
        textApplyReferral = containerView.findViewById(R.id.textApplyReferral)
        passwordTextView = containerView.findViewById(R.id.passwordTextView)
        changePasswordTextView = containerView.findViewById(R.id.changePasswordTextView)
        handleNameTextView = containerView.findViewById(R.id.handleNameTextView)
        aboutEditText = containerView.findViewById(R.id.aboutEditText)

        editAddNumber.setOnClickListener {
            //            varifyNumberWithFacebookAccountKit()
            val intent = Intent(activity, OTPActivity::class.java)
            startActivityForResult(intent, VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE)
        }

        /*textApplyReferral.setOnClickListener {
            validateReferralCode()
        }*/

        userHandleTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                isHandleChecked = false
                userAvailabilityResultTextView.visibility = View.GONE
                if (userHandleTextView.text.length >= 7 && !userHandleTextView.text.toString().equals(
                        defaultHandle
                    )) {
                    checkTextView.visibility = View.VISIBLE
                } else {
                    checkTextView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        checkTextView.setOnClickListener {
            if (userHandleTextView.text.length >= 7) {
                if (userHandleTextView.text.endsWith(".", false)) {
                    Toast.makeText(
                        activity,
                        "User handle cannot ends with dot(.)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    checkUserHandleAvailability()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Handle should be of minimum 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        editImageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(
                        "PERMISSIONS",
                        "storage permissions has NOT been granted. Requesting permissions."
                    )
                    requestCameraAndStoragePermissions()
                } else if (ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(
                        "PERMISSIONS",
                        "storage permissions has NOT been granted. Requesting permissions."
                    )
                    requestCameraPermission()
                } else {
                    chooseImageOptionPopUp()
                }
            } else {
                chooseImageOptionPopUp()
            }
        }

        if (!Places.isInitialized()) {
            Places.initialize(BaseApplication.getAppContext(), AppConstants.PLACES_API_KEY)
        }

        editLocation.setOnClickListener {
            val fieldsArr = arrayOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG).asList()
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldsArr)
                .setTypeFilter(TypeFilter.CITIES)
                .build(it.context)

            startActivityForResult(intent, REQUEST_SELECT_PLACE)
        }

        textVerify = containerView.findViewById(R.id.textVerify)
        textVerify.setOnClickListener {
            //            varifyNumberWithFacebookAccountKit()
            val intent = Intent(activity, OTPActivity::class.java)
            startActivityForResult(intent, VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE)
        }
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            if (prepareDataForPosting()) {
                postDataofRewardsToServer()
            }
        }

        editExpectedDate = containerView.findViewById(R.id.editExpectedDate)
        layoutWorking = containerView.findViewById(R.id.layoutWorking)
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
        textKidsDOB = containerView.findViewById(R.id.textKidsDOB)
        textDOB = containerView.findViewById(R.id.textDOB)
        textDeleteChild = containerView.findViewById(R.id.textDeleteChild)
        linearKidsEmptyView = containerView.findViewById(R.id.linearKidsEmptyView)
        langLayout = containerView.findViewById(R.id.langLayout)
        interestLayout = containerView.findViewById(R.id.interestLayout)

        textDeleteChild.setOnClickListener {
            if (linearKidsDetail.childCount > 0) {
                if (linearKidsDetail.childCount == 1) {
                    linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild)
                        .visibility = View.GONE
                    linearKidsEmptyView.visibility = View.GONE
                } else {
                    linearKidsEmptyView.visibility = View.GONE
                }
            }
        }

        (containerView.findViewById<CheckBox>(R.id.checkAreYouExpecting)).setOnCheckedChangeListener(
            object : CompoundButton.OnCheckedChangeListener {
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
            var fragment = PickerDialogFragment.newInstance(
                columnCount = 1,
                popType = Constants.PopListRequestType.INTEREST.name,
                isSingleSelection = true,
                preSelectedItemIds = preSelectedInterest,
                context = this@ProfileInfoFragment
            )
            fragmentManager?.let { it1 ->
                fragment.show(
                    it1,
                    RewardsSocialInfoFragment::class.java.simpleName
                )
            }
        }

        editInterest.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(
                columnCount = 1,
                popType = Constants.PopListRequestType.INTEREST.name,
                isSingleSelection = true,
                preSelectedItemIds = preSelectedInterest,
                context = this@ProfileInfoFragment
            )
            fragmentManager?.let { it1 ->
                fragment.show(
                    it1,
                    RewardsSocialInfoFragment::class.java.simpleName
                )
            }
        }

        textDOB.setOnClickListener {
            textView = textDOB
            showDatePickerDialog(true, isForParent = true)
        }

        textKidsDOB.setOnClickListener {
            textView = textKidsDOB
            showDatePickerDialog(true)
        }

        textEditLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(
                columnCount = 1,
                popType = Constants.PopListRequestType.LANGUAGE.name,
                isSingleSelection = true,
                preSelectedItemIds = preSelectedLanguage,
                context = this@ProfileInfoFragment
            )
            fragmentManager?.let { it1 ->
                fragment.show(
                    it1,
                    RewardsSocialInfoFragment::class.java.simpleName
                )
            }
        }

        editLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(
                columnCount = 1,
                popType = Constants.PopListRequestType.LANGUAGE.name,
                isSingleSelection = true,
                preSelectedItemIds = preSelectedLanguage,
                context = this@ProfileInfoFragment
            )
            fragmentManager?.let { it1 ->
                fragment.show(
                    it1,
                    RewardsSocialInfoFragment::class.java.simpleName
                )
            }
        }

        val genderList = ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
        genderSpinner.adapter = spinAdapter
        genderSpinner.setSelection(1)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
                genderSpinner.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        editExpectedDate.setOnClickListener {
            textView = editExpectedDate
            showDatePickerDialog(false, true)
        }

        containerView.findViewById<TextView>(R.id.textSaveAndContinue).setOnClickListener {
            if (prepareDataForPosting()) {
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

        changePasswordTextView.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserHandleAvailability() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java)
            .checkUserHandleAvailability(userHandleTextView.text.toString()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponseGeneric<UserHandleAvailabilityResponse>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<UserHandleAvailabilityResponse>) {
                    removeProgressDialog()
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        userAvailabilityResultTextView.visibility = View.VISIBLE
                        if (response.data!!.result.userId.isNullOrEmpty()) {
                            userAvailabilityResultTextView.setText("User handle available")
                            userAvailabilityResultTextView.setTextColor(resources.getColor(R.color.green_dark))
                            isHandleChecked = true
                        } else {
                            isHandleChecked = false
                            userAvailabilityResultTextView.setText("Sorry, this handle is not available.")
                            userAvailabilityResultTextView.setTextColor(resources.getColor(R.color.app_red))
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    isHandleChecked = false
                    Log.d("exception in error", e.message.toString())
                }
            })
    }

    private fun requestCameraAndStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity as RewardsContainerActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity as RewardsContainerActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity as RewardsContainerActivity,
                Manifest.permission.CAMERA
            )
        ) {

            Snackbar.make(
                rootView, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    ActivityCompat
                        .requestPermissions(
                            activity as RewardsContainerActivity, PERMISSIONS_EDIT_PICTURE,
                            REQUEST_EDIT_PICTURE
                        )
                }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                activity as RewardsContainerActivity,
                PERMISSIONS_EDIT_PICTURE,
                REQUEST_EDIT_PICTURE
            )
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity as RewardsContainerActivity,
                Manifest.permission.CAMERA
            )
        ) {
            Snackbar.make(
                rootView, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    ActivityCompat.requestPermissions(
                        activity as RewardsContainerActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA
                    )
                }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                activity as RewardsContainerActivity, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        }
    }

    fun chooseImageOptionPopUp() {
        val popup = PopupMenu(context, profileImageView)
        popup.menuInflater.inflate(R.menu.profile_image_upload_options, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            val i = item.itemId
            if (i == R.id.camera) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(context!!.packageManager) != null) {
                    // Create the File where the photo should go
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException")
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        try {
                            cameraIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                GenericFileProvider.getUriForFile(
                                    activity as RewardsContainerActivity,
                                    BaseApplication.getAppContext().applicationContext.packageName + ".my.package.name.provider",
                                    createImageFile()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        startActivityForResult(cameraIntent, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE)
                    }
                }
                true
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE)
                true
            }
        }
        popup.show()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val image = File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            dir // directory
        )

        mCurrentPhotoPath = "file:" + image.absolutePath
        absoluteImagePath = image.absolutePath
        return image
    }

    private fun startCropActivity(uri: Uri) {
        val destinationFileName = "$SAMPLE_CROPPED_IMAGE_NAME.jpg"
        Log.e("instartCropActivity", "test")

        val uCrop =
            UCrop.of(uri, Uri.fromFile(File(FacebookSdk.getCacheDir(), destinationFileName)))
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(300, 300)
        uCrop.start(activity as RewardsContainerActivity, this)
    }

    fun sendUploadProfileImageRequest(file: File) {
        showProgressDialog(getString(R.string.please_wait))
        val bao = ByteArrayOutputStream()
        val retro = BaseApplication.getInstance().retrofit
        val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
        val requestBodyFile = file.asRequestBody(MEDIA_TYPE_PNG)
        val imageType = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        // prepare call in Retrofit 2.0
        val imageUploadAPI = retro.create(ImageUploadAPI::class.java)

        val call = imageUploadAPI.uploadImage(
            // userId,
            imageType,
            requestBodyFile
        )
        // asynchronous call
        call.enqueue(object : Callback<ImageUploadResponse> {
            override fun onResponse(
                call: Call<ImageUploadResponse>,
                response: retrofit2.Response<ImageUploadResponse>
            ) {
                val statusCode = response.code()
                val responseModel = response.body()

                removeProgressDialog()
                if (responseModel!!.code != 200) {
                    // showToast(getString(R.string.toast_response_error));
                    return
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.data.result.url)) {
                        Log.i("IMAGE_UPLOAD_REQUEST", responseModel.data.result.url)
                    }
                    setProfileImage(responseModel.data.result.url)
                    Picasso.get()
                        .invalidate(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))
                    Picasso.get().load(responseModel.data.result.url)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.family_xxhdpi)
                        .error(R.drawable.family_xxhdpi).into(profileImageView)
                    SharedPrefUtils.setProfileImgUrl(
                        BaseApplication.getAppContext(),
                        responseModel.data.result.url
                    )
                }
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                //                             showToast("unable to upload image, please try again later");
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }
        )
    }

    fun setProfileImage(url: String) {
        val updateUserDetail = UpdateUserDetailsRequest()
        updateUserDetail.attributeName = "profilePicUrl"
        updateUserDetail.attributeValue = url
        updateUserDetail.attributeType = "S"
        val retrofit = BaseApplication.getInstance().retrofit
        val userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI::class.java)
        val call = userAttributeUpdateAPI.updateProfilePic(updateUserDetail)
        call.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: retrofit2.Response<UserDetailResponse>
            ) {
                if (response.body()!!.status != "success") {
                    //                    showToast(getString(R.string.toast_response_error));
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }

    fun prepareDataForPosting(): Boolean {
        if (editFirstName.text.trim().isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_first_name)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.firstName = editFirstName.text.toString()
        }

        if (editLastName.text.trim().isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_last_name)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.lastName = editLastName.text.toString()
        }
        if (aboutEditText.text.trim().isEmpty()) {
            Toast.makeText(
                activity,
                getString(R.string.app_settings_edit_profile_toast_user_bio_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.userBio = aboutEditText.text.toString()
        }

        if (isHandleChecked || (!apiGetResponse.isUserHandleUpdated.isNullOrEmpty() && apiGetResponse.isUserHandleUpdated.equals(
                "1"
            ))) {
            if (apiGetResponse.isUserHandleUpdated.isNullOrEmpty() || apiGetResponse.isUserHandleUpdated.equals(
                    "0"
                )) {
                apiGetResponse.userHandle = userHandleTextView.text.toString()
            }
        } else {
            Toast.makeText(
                activity,
                "Please check handle availability",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        //        if (BuildConfig.DEBUG) {
        //            accountKitAuthCode = "123"
        //            apiGetResponse.contact = "9999999999"
        //        }

        if (accountKitAuthCode.trim().isEmpty() && apiGetResponse.mobile != null && apiGetResponse.mobile.trim().isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_phone)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            if (apiGetResponse.mobile != null && !apiGetResponse.mobile.trim().isEmpty()) {
                apiGetResponse.mobile = apiGetResponse.mobile
                apiGetResponse.mobileToken = ""
            } else if (!accountKitAuthCode.trim().isEmpty()) {
                apiGetResponse.mobileToken = accountKitAuthCode
                apiGetResponse.mobile = ""
            }
        }

        if (isvalid()) {
            apiGetResponse.email = editEmail.text.toString().trim()
        } else {
            return false
        }

        if (editLocation.text.trim().isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_location)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.cityName = editLocation.text.toString()
            address = editLocation.text.toString()
        }

        apiGetResponse.latitude = lat
        apiGetResponse.longitude = lng

        if (radioGroupWorkingStatus.checkedRadioButtonId == R.id.radiokWorking) {
            apiGetResponse.workStatus = "1"
        } else {
            apiGetResponse.workStatus = "0"
        }

        apiGetResponse.gender = if (genderSpinner.selectedItemPosition == 0) {
            "Male"
        } else {
            "Female"
        }

        if (textDOB.text.trim().isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_dob)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.dob =
                DateTimeUtils.convertStringToMilliTimestamp(textDOB.text.toString()).toString()
        }

        preSelectedLanguage.removeAll(Collections.singleton(""))

        if (preSelectedLanguage.isEmpty()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_language)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            apiGetResponse.preferredLanguages = preSelectedLanguage
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            apiGetResponse.isMother = "1"
        } else if (radioGroupAreMother.checkedRadioButtonId == R.id.radioNo) {
            apiGetResponse.isMother = "0"
        }

        if (!preSelectedInterest.isEmpty()) {
            preSelectedInterestForPosting.clear()
            (preSelectedInterest).forEach {
                try {
                    preSelectedInterestForPosting.add(it)
                } catch (ex: Exception) {
                }
            }
            apiGetResponse.interests = preSelectedInterestForPosting
        }

        if (checkAreYouExpecting.isChecked) {
            if (editExpectedDate.text.trim().isEmpty()) {
                Toast.makeText(
                    activity,
                    resources.getString(
                        R.string.cannot_be_left_blank,
                        resources.getString(R.string.rewards_expected_date)
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
                apiGetResponse.isExpected = "1"
                apiGetResponse.expectedDate =
                    DateTimeUtils.convertStringToMilliTimestamp(editExpectedDate.text.toString())
                        .toString()
            }
        } else {
            apiGetResponse.isExpected = "0"
            apiGetResponse.expectedDate = ""
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            if (linearKidsDetail.childCount > 0) {
                var kidsList = ArrayList<KidsModel>()
                for (i in 0..linearKidsDetail.childCount) {
                    var kidsInfoResponse = KidsModel()
                    if (linearKidsDetail.getChildAt(i) != null) {
                        kidsInfoResponse.gender =
                            if (linearKidsDetail.getChildAt(i).findViewById<Spinner>(R.id.spinnerGender).selectedItemPosition == 0) {
                                "Male"
                            } else {
                                "Female"
                            }
                        kidsInfoResponse.birthDay = DateTimeUtils.convertStringToMilliTimestamp(
                            linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textKidsDOB).text.toString()
                        ).toString()
                        kidsInfoResponse.name =
                            linearKidsDetail.getChildAt(i).findViewById<EditText>(R.id.editKidsName)
                                .text.toString()
                        kidsList.add(kidsInfoResponse)
                    }
                }
                apiGetResponse.kids = kidsList
                Log.d("dob text is ", kidsList.get(0).name)
                if (linearKidsEmptyView.visibility == View.VISIBLE) {
                    if (!textKidsDOB.text.trim().isEmpty()) {
                        var kidsInfoResponse = KidsModel()
                        kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                            "Male"
                        } else {
                            "Female"
                        }
                        kidsInfoResponse.birthDay =
                            DateTimeUtils.convertStringToMilliTimestamp(textKidsDOB.text.toString())
                                .toString()
                        kidsInfoResponse.name = editKidsName.text.toString()
                        apiGetResponse.kids!!.add(kidsInfoResponse)
                    } else {
                        Toast.makeText(
                            activity,
                            resources.getString(
                                R.string.cannot_be_left_blank,
                                resources.getString(R.string.rewards_dob)
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                    }
                }
            } else {
                if (!textKidsDOB.text.trim().isEmpty()) {
                    var kidsInfoLocal = ArrayList<KidsModel>()
                    var kidsInfoResponse = KidsModel()
                    kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                        "Male"
                    } else {
                        "Female"
                    }
                    kidsInfoResponse.birthDay =
                        DateTimeUtils.convertStringToMilliTimestamp(textKidsDOB.text.toString())
                            .toString()
                    kidsInfoResponse.name = editKidsName.text.toString()
                    kidsInfoLocal.add(kidsInfoResponse)
                    apiGetResponse.kids = kidsInfoLocal
                } else {
                    Toast.makeText(
                        activity,
                        resources.getString(
                            R.string.cannot_be_left_blank,
                            resources.getString(R.string.rewards_dob)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            }
        } else {
            apiGetResponse.kids = null
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SELECT_PLACE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    if (!place.name.toString().isNullOrEmpty()) {
                        cityName = place.name.toString()
                        editLocation.setText(cityName)
                        lat = place.latLng?.latitude!!
                        lng = place.latLng?.longitude!!
                        address = cityName
                    }
                }
            }
            VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    //                    accountKitAuthCode = (data!!.getParcelableExtra(AccountKitLoginResult.RESULT_KEY) as AccountKitLoginResult).authorizationCode!!
                    accountKitAuthCode = data.getStringExtra("auth_token")!!
                    Log.d("account code ", accountKitAuthCode)
                    //                        apiGetResponse.contact = null
                    editPhone.visibility = View.VISIBLE
                    textVerify.visibility = View.VISIBLE
                    editAddNumber.visibility = View.GONE
                }
            }
            ADD_MEDIA_ACTIVITY_REQUEST_CODE -> {
                if (data == null) {
                    return
                }
                imageUri = data.getData()!!
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(imageUri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        startCropActivity(Uri.parse(mCurrentPhotoPath))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = UCrop.getOutput(data!!)
                    Log.e("resultUri", resultUri!!.toString())
                    val file2 = FileUtils.getFile(activity, resultUri)
                    sendUploadProfileImageRequest(file2)
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data!!)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("DWADWAD", status.statusMessage)
            }
            RESULT_CANCELED -> {
                Log.i("DWADWAD", "DwDWDJIWODJ WDWOIDOIWOD")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            saveAndContinueListener = context
        }
    }

    interface SaveAndContinueListener {
        fun profileOnSaveAndContinue()
    }

    /*post data to server*/
    private fun postDataofRewardsToServer() {
        val userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        Utils.pushGenericEvent(
            activity, "CTA_Submit_MyMoney_RegistrationForm_Registered",
            userId, "ProfileInfoFragment"
        )
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java)
                .sendProfileDataForAny(userId, apiGetResponse, pageValue = 4)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RewardsPersonalResponse> {
                    override fun onComplete() {
                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: RewardsPersonalResponse) {
                        if (response.code == 200) {
                            if (Constants.SUCCESS == response.status) {
                                if (apiGetResponse.emailValidated.equals("0")) {
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.verify_email_address),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                (activity as RewardsContainerActivity).finish()
                                /*if (isComingFromCampaign) {
                                    SharedPrefUtils.setIsRewardsAdded(BaseApplication.getAppContext(), "1")
                                }
    //                            saveAndContinueListener.profileOnSaveAndContinue()
                                if (isNewRegistration) {
                                    facebookEventForRegistration()
                                }*/
                            } else if (Constants.FAILURE == response.status) {
                                Toast.makeText(context, response.reason, Toast.LENGTH_LONG).show()
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

    private fun facebookEventForRegistration() {
        Log.d("FB EVENT", "MY money Register event")
        try {
            activity?.let {
                val logger = AppEventsLogger.newLogger(it)
                val bundle = Bundle()
                bundle.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "FB Ads")
                logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, bundle)
            }
        } catch (e: Exception) {
        }
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        val userId = SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java)
                .getUserDetails(userId, "yes").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseResponseGeneric<UserDetailResult>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: BaseResponseGeneric<UserDetailResult>) {
                        if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                            apiGetResponse = response.data!!.result
                            fetchCityData()
                            setValuesToComponents()
                        }
                    }

                    override fun onError(e: Throwable) {
                        removeProgressDialog()
                        Log.d("exception in error", e.message.toString())
                    }
                })
        }
    }

    private fun fetchCityData() {
        BaseApplication.getInstance().retrofit.create(ConfigAPIs::class.java).getCityConfigRx()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponseGeneric<CityConfigResultResponse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<CityConfigResultResponse>) {
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        if (response.data!!.result != null && response!!.data!!.result != null && response!!.data!!.result.cityData.isNotEmpty()) {
                            val currentCity =
                                SharedPrefUtils.getCurrentCityModel(saveAndContinueListener as RewardsContainerActivity)
                            (response!!.data!!.result.cityData).forEach {
                                if (AppConstants.ALL_CITY_NEW_ID != it.id) {
                                    cityList.add(it)
                                }
                                if (AppConstants.OTHERS_NEW_CITY_ID == it.id) {
                                    if (currentCity.name != null && "Others" != currentCity.name && currentCity.id == AppConstants.OTHERS_CITY_ID) {
                                        cityList.get(cityList.size - 1).cityName =
                                            ("Others(" + currentCity.name + ")")
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

    fun showDatePickerDialog(
        isShowTillCurrent: Boolean,
        isShowFutureDate: Boolean = false,
        isForParent: Boolean = false
    ) {
        val newFragment = ProfileInfoFragment.DatePickerFragment()
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
            val dlg = context?.let {
                DatePickerDialog(
                    it,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    this,
                    curent_year,
                    current_month,
                    current_day
                )
            }

            if (arguments != null) {
                isShowTillCurrent = arguments!!.getBoolean("is_show_current_only", false)
                isShowFutureOnly = arguments!!.getBoolean("is_show_future_only", false)
                isForParent = arguments!!.getBoolean("is_for_parent", false)
            }
            dlg?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
            if (textView != null) {
                textView.text = "" + day + "-" + (month + 1) + "-" + year
            }
        }
    }

    private fun validateChildData(): Boolean {
        Log.d("text value", " " + textKidsDOB.text + " " + linearKidsEmptyView.visibility)
        if (linearKidsEmptyView.visibility == View.VISIBLE && textKidsDOB.text.isBlank()) {
            Toast.makeText(
                activity,
                resources.getString(
                    R.string.cannot_be_left_blank,
                    resources.getString(R.string.rewards_dob)
                ),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (linearKidsEmptyView.visibility == View.GONE) {
            linearKidsEmptyView.visibility = View.VISIBLE
            textDeleteChild.visibility = View.VISIBLE
            if (linearKidsDetail.childCount > 0) {
                for (i in 0..linearKidsDetail.childCount - 1) {
                    linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textDeleteChild)
                        .visibility = View.VISIBLE
                }
            }
            return false
        } else if (linearKidsEmptyView.visibility == View.VISIBLE) {
            textDeleteChild.visibility = View.VISIBLE
        }

        return true
    }

    fun createKidsDetailDynamicView(
        gender: String? = null,
        date: String = "",
        name: String? = "",
        shouldDelteShow: Boolean = true
    ) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val indexView = inflater.inflate(R.layout.dynamic_child_view, null)
        var textHeader = indexView.findViewById<TextView>(R.id.textHeader)
        var textDelete = indexView.findViewById<TextView>(R.id.textDeleteChild)
        var editKidsName = indexView.findViewById<EditText>(R.id.editKidsName)
        if (shouldDelteShow) {
            textDelete.visibility = View.VISIBLE
        } else {
            textDelete.visibility = View.GONE
        }

        if (isNewRegistration) {
            editKidsName.visibility = View.GONE
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
                linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild)
                    .visibility = View.GONE
            } else if (linearKidsEmptyView.visibility == View.VISIBLE && linearKidsDetail.childCount == 0) {
                textDeleteChild.visibility = View.GONE
            }
        }
        var spinnerGender = indexView.findViewById<Spinner>(R.id.spinnerGender)
        var textDOB = indexView.findViewById<TextView>(R.id.textKidsDOB)

        val genderList = java.util.ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        textDOB.setOnClickListener {
            textView = it as TextView
            showDatePickerDialog(true, false)
        }

        if (gender != null && !date.isNullOrEmpty()) {
            textDOB.text = date
            val selectionPosition = spinAdapter.getPosition(gender)
            spinnerGender.setSelection(selectionPosition)
            if (!name.isNullOrEmpty()) {
                editKidsName.setText(name)
            }
        } else {
            textDOB.text = textKidsDOB.text
            spinnerGender.setSelection(this.spinnerGender.selectedItemPosition)
            editKidsName.text = this.editKidsName.text
            this.spinnerGender.setSelection(0)
            textKidsDOB.text = ""
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
                context?.let { mContext ->
                    val subsubLL = LayoutInflater.from(activity).inflate(
                        R.layout.shape_rewards_border_rectangular,
                        null
                    ) as LinearLayout
                    val catTextView = subsubLL.getChildAt(0) as TextView
                    catTextView.setText(it)
                    catTextView.isSelected = true
                    // subsubLL.tag = it
                    floatingInterest.addView(subsubLL)
                }
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
                context?.let { mContext ->
                    val subsubLL = LayoutInflater.from(context).inflate(
                        R.layout.shape_rewards_border_rectangular,
                        null
                    ) as LinearLayout
                    val catTextView = subsubLL.getChildAt(0) as TextView
                    catTextView.setText(it)
                    catTextView.isSelected = true
                    // subsubLL.tag = it
                    floatingLanguage.addView(subsubLL)
                }
            }
        }
    }

    /*private fun validateReferralCode() {
        if (!editReferralCode.text.trim().isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).validateReferralCode(editReferralCode.text.toString()!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ReferralCodeResult>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<ReferralCodeResult>) {
                    if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response!!.data!!.result != null) {
                        if (response!!.data!!.result.is_valid) {
                            textReferCodeError.setTextColor(activity!!.resources.getColor(R.color.green_dark))
                            textReferCodeError.setText("Successfully Applied")
                            editReferralCode.isEnabled = true
                            textApplyReferral.isEnabled = false
                            validReferralCode = "valid"
                            if (isAdded) {
                                Toast.makeText(activity, "Successfully Applied", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            textReferCodeError.visibility = View.VISIBLE
                            textReferCodeError.setText("Code is not valid")
                            validReferralCode = "notValid"
                            textReferCodeError.setTextColor(activity!!.resources.getColor(R.color.campaign_refer_code_error))
                            if (isAdded) {
                                Toast.makeText(activity, "Code is not valid", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    Log.d("exception in error", e.message.toString())
                }
            })
        }
    }*/

    fun isvalid(): Boolean {
        var mail = editEmail.text.toString().trim()

        if (mail.isNullOrEmpty() || !StringUtils.isValidEmail(mail)) run {
            editEmail.setFocusableInTouchMode(true)
            editEmail.setError(getString(R.string.enter_valid_email))
            editEmail.requestFocus()
            return false
        }
        return true
    }
}
