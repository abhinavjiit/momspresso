package com.mycity4kids.ui.rewards.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.*
import com.mycity4kids.models.rewardsmodels.CityConfigResultResponse
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.fragment.CityListingDialogFragment
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**editLanguage
 * A simple [Fragment] subclass.
 */

const val VERIFY_NUMBER_ACCOUNTKIT_REQUEST_CODE = 1000
const val REQUEST_SELECT_PLACE = 2000

class RewardsPersonalInfoFragment : BaseFragment(), ChangePreferredLanguageDialogFragment.OnClickDoneListener, CityListingDialogFragment.IChangeCity {
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
    private lateinit var editLocation: EditText
    //    private lateinit var editLanguage: EditText
//    private lateinit var radioGroupWorkingStatus: RadioGroup
//    private lateinit var genderSpinner: AppCompatSpinner
    //private lateinit var textDOB: TextView
    private lateinit var textVerify: TextView
    private var apiGetResponse: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private var cityList = ArrayList<CityInfoItem>()
    private var selectedCityId: Int = 0
    private var newSelectedCityId: String? = null
    private var currentCityName: String? = null
    private var cityName: String? = null
    private var accountKitAuthCode = ""

    companion object {
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

    /*setting values to components*/
    private fun setValuesToComponents() {
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
        //if (apiGetResponse.dob != null && apiGetResponse.dob!! > 0) RewardsPersonalInfoFragment.textDOB.setText(DateTimeUtils.getDateFromTimestamp(apiGetResponse.dob!!.toLong()))
        if (!apiGetResponse.location.isNullOrBlank()) editLocation.setText(apiGetResponse.location)

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

        editAddNumber.setOnClickListener {
            varifyNumberWithFacebookAccountKit()
        }

        editLocation.setOnClickListener {

            val typeFilter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(activity)
            startActivityForResult(intent, REQUEST_SELECT_PLACE)

//            var cityFragment = CityListingDialogFragment()
//            cityFragment.setTargetFragment(this, 0)
//            val _args = Bundle()
//            _args.putParcelableArrayList("cityList", cityList)
//            _args.putString("fromScreen", "rewards")
//            cityFragment.setArguments(_args)
//            val fm = childFragmentManager
//            cityFragment.show(fm, "Replies")
        }

        textVerify = containerView.findViewById(R.id.textVerify)
        textVerify.setOnClickListener {
            varifyNumberWithFacebookAccountKit()
        }
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            if (prepareDataForPosting()) {
                postDataofRewardsToServer()
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
            apiGetResponse.email = editEmail.text.toString()
        } else {
            Toast.makeText(activity, resources.getString(R.string.please_enter_a_valid, resources.getString(R.string.rewards_email)), Toast.LENGTH_SHORT).show()
            return false
        }

        if (editLocation.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_location)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.location = editLocation.text.toString()
        }

        apiGetResponse.latitude = 28.7041
        apiGetResponse.longitude = 77.1025

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
        return !editEmail.text.isNullOrBlank() && StringUtils.isValidEmail(editEmail.text.toString())
    }

    /*post data to server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        //var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            Log.e("sending json", Gson().toJson(apiGetResponse))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData(userId!!, apiGetResponse, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<SetupBlogData>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<SetupBlogData>) {
                    Log.e("response is ", Gson().toJson(response.data))
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response.data!!.msg.equals(Constants.SUCCESS_MESSAGE)) {
                        //apiGetResponse = response.data!!.result
                        saveAndContinueListener.profileOnSaveAndContinue()
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

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
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
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        //var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            BaseApplication.getInstance().retrofit.create(ConfigAPIs::class.java).getCityConfigRx().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<CityConfigResultResponse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<CityConfigResultResponse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        if (response.data!!.result != null && response!!.data!!.result != null && response!!.data!!.result.cityData.isNotEmpty()) {
                            val currentCity = SharedPrefUtils.getCurrentCityModel(activity)
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
    }

}

