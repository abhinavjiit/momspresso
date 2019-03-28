package com.mycity4kids.ui.rewards.fragment


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.customtabs.CustomTabsClient.getPackageName
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.gson.Gson
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ConnectivityUtils
import com.kelltontech.utils.ToastUtils.showToast
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.instagram.InstagramApp
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.city.City
import com.mycity4kids.models.request.LoginRegistrationRequest
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.UserDetailData
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.models.rewardsmodels.SocialAccountObject
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.dialog.PickerDialogFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.event_details_activity.*
import org.apmem.tools.layouts.FlowLayout
import java.security.MessageDigest
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class RewardsSocialInfoFragment : BaseFragment(), IFacebookUser, GoogleApiClient.OnConnectionFailedListener, InstagramApp.OAuthAuthenticationListener {
    override fun onSuccess() {
//        if (mApp!!.hasAccessToken()) {
//            editInstagram.setText(getString(R.string.rewards_social_instagram_connected))
//            instagramAuthToken = mApp!!.accessToken
//            setValuesForSocial(Constants.SocialPlatformName.instagram, instagramAuthToken!!)
//        }
    }

    override fun onFail(error: String?) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun getFacebookUser(user: String?) {
        try {
            if (user != null) {
                facebookAuthToken = user
                editFacebook.setText(getString(R.string.rewards_social_facebook_connected))
                setValuesForSocial(Constants.SocialPlatformName.facebook, facebookAuthToken!!)
            }
            removeProgressDialog()
        } catch (e: Exception) {
            // e.printStackTrace();
            removeProgressDialog()
            (activity as RewardsContainerActivity).showToast(getString(R.string.toast_response_error))
        }
    }

    override fun updateUi(response: Response?) {

    }

    private lateinit var containerView: View
    private lateinit var submitListener: SubmitListener
    private lateinit var layoutInstagram: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutTwitter: LinearLayout
    private lateinit var layoutYoutube: LinearLayout
    private lateinit var editWebsite: EditText
    private lateinit var editInstagram: EditText
    private lateinit var editFacebook: EditText
    private lateinit var editTwitter: EditText
    private lateinit var editYoutube: EditText
    private lateinit var spinnerProfession: AppCompatSpinner
    private lateinit var spinnerHouseHold: AppCompatSpinner
    private var householdList = ArrayList<String>()
    private var professionList = ArrayList<String>()
    private var mGoogleApiClient: GoogleApiClient? = null
    private var apiGetResponse: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private var loginMode = ""
    private var mApp: InstagramApp? = null
    private var userInfoHashmap = HashMap<String, String>()
    private var callbackManager: CallbackManager? = null
    private var facebookAuthToken: String? = null
    private var instagramAuthToken: String? = null

    companion object {
        fun newInstance() = RewardsSocialInfoFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_social_info, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestScopes(Scope(Scopes.PLUS_ME))
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        callbackManager = CallbackManager.Factory.create()


        /*initialize XML components*/
        initializeXMLComponents()

        /*fetch data from server*/
        fetchRewardsData()

        /**/
        return containerView
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData(userId!!, 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
                    removeProgressDialog()
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

    private fun setValuesToComponents() {
        if (apiGetResponse.socialAccounts != null && apiGetResponse.socialAccounts!!.isNotEmpty()) {
            (apiGetResponse.socialAccounts)!!.forEach {
                when (it.platform_name) {
                    Constants.SocialPlatformName.facebook.name -> {
                        if (!it.acc_link.isNullOrEmpty()) {
                            editFacebook.setText(getString(R.string.rewards_social_facebook_connected))
                        }
                    }

                    Constants.SocialPlatformName.instagram.name -> {
                        if (it.acc_link != null && !it.acc_link!!.trim().isNullOrBlank()) {
                            editInstagram.setText(it.acc_link)
                        }
                    }

                    Constants.SocialPlatformName.twitter.name -> {
                        if (it.acc_link != null && !it.acc_link!!.trim().isNullOrBlank()) {
                            editTwitter.setText(it.acc_link)
                        }
                    }

                    Constants.SocialPlatformName.youtube.name -> {
                        if (it.acc_link != null && !it.acc_link!!.trim().isNullOrBlank()) {
                            editYoutube.setText(it.acc_link)
                        }
                    }
                    Constants.SocialPlatformName.website.name -> {
                        if (it.acc_link != null && !it.acc_link!!.trim().isNullOrBlank()) {
                            editWebsite.setText(it.acc_link)
                        }
                    }
                }
            }
        }
    }

    private fun setValuesForSocial(platformName: Constants.SocialPlatformName, token: String) {
        if (apiGetResponse.socialAccounts != null && apiGetResponse.socialAccounts!!.isNotEmpty()) {

            var socialAccountsListNotContainsGivenPlatform = ArrayList<SocialAccountObject>(apiGetResponse.socialAccounts!!.filter { it -> !it.platform_name.equals(platformName.name) })
            var socialAccountsListByGivenPlatform = apiGetResponse.socialAccounts!!.filter { it -> it.platform_name.equals(platformName.name) }
            if (socialAccountsListByGivenPlatform.isNotEmpty()) {
                var localSocialAccout = SocialAccountObject()
                if (socialAccountsListByGivenPlatform.get(0).platform_name.equals(Constants.SocialPlatformName.facebook.name, true)) {
//                    SocialAccountsListByGivenPlatform.get(0).access_token = token
//                    SocialAccountsListByGivenPlatform.get(0).platform_name = platformName.name
                    localSocialAccout.platform_name = platformName.name
                    localSocialAccout.access_token = token
                    localSocialAccout.id = socialAccountsListByGivenPlatform.get(0).id
                } else {
                    localSocialAccout.platform_name = platformName.name
                    localSocialAccout.acc_link = token
                    localSocialAccout.id = socialAccountsListByGivenPlatform.get(0).id
                }
                socialAccountsListNotContainsGivenPlatform.add(localSocialAccout)
            } else {
                var localSocialAccout = SocialAccountObject()
                if (platformName.name.equals(Constants.SocialPlatformName.facebook.name, true)) {
                    localSocialAccout.access_token = token
                    localSocialAccout.platform_name = platformName.name
                    apiGetResponse.socialAccounts!!.add(localSocialAccout)
                } else {
                    localSocialAccout.acc_link = token
                    localSocialAccout.platform_name = platformName.name
                    apiGetResponse.socialAccounts!!.add(localSocialAccout)
                }
                socialAccountsListNotContainsGivenPlatform.add(localSocialAccout)
            }
            apiGetResponse.socialAccounts = socialAccountsListNotContainsGivenPlatform
        } else {
            var localSocialAccout = SocialAccountObject()
            if (platformName.name.equals(Constants.SocialPlatformName.facebook.name, true)) {
                localSocialAccout.access_token = token
                localSocialAccout.platform_name = platformName.name
                apiGetResponse.socialAccounts!!.add(localSocialAccout)
            } else {
                localSocialAccout.acc_link = token
                localSocialAccout.platform_name = platformName.name
                apiGetResponse.socialAccounts!!.add(localSocialAccout)
            }
            var localListSocialAccount = ArrayList<SocialAccountObject>()
            localListSocialAccount.add(localSocialAccout)
            apiGetResponse.socialAccounts = localListSocialAccount
        }

        //Log.e("apiGetResponse is ", Gson().toJson(apiGetResponse))
    }

    private fun initializeXMLComponents() {
        layoutInstagram = containerView.findViewById(R.id.layoutInstagram)
        layoutFacebook = containerView.findViewById(R.id.layoutFacebook)
        layoutYoutube = containerView.findViewById(R.id.layoutYoutube)
        layoutTwitter = containerView.findViewById(R.id.layoutTwitter)
        spinnerProfession = containerView.findViewById(R.id.spinnerProfession)
        spinnerHouseHold = containerView.findViewById(R.id.spinnerHouseHold)
        editInstagram = containerView.findViewById(R.id.editInstagram)
        editFacebook = containerView.findViewById(R.id.editFacebook)
        editTwitter = containerView.findViewById(R.id.editTwitter)
        editWebsite = containerView.findViewById(R.id.editWebsite)
        editYoutube = containerView.findViewById(R.id.editYoutube)

        layoutInstagram.setOnClickListener {
            //AuthenticateWithInstagram()
        }

        editInstagram.setOnClickListener {
            //AuthenticateWithInstagram()
        }

        layoutFacebook.setOnClickListener {
            if (ConnectivityUtils.isNetworkEnabled(activity)) {
                showProgressDialog(getString(R.string.please_wait))
                FacebookUtils.facebookLogin(activity, this)
            } else {
                (activity as BaseActivity).showToast(getString(R.string.error_network))
            }
        }

        editFacebook.setOnClickListener {
            if (ConnectivityUtils.isNetworkEnabled(activity)) {
                showProgressDialog(getString(R.string.please_wait))
                FacebookUtils.facebookLogin(activity, this)
            } else {
                (activity as RewardsContainerActivity).showToast(getString(R.string.error_network))
            }
        }

        var houseHoldIncomeArray = resources.getStringArray(R.array.household_income)
        houseHoldIncomeArray.forEach { str ->
            householdList.add(str)
        }
        val householdAdapter = CustomSpinnerAdapter(activity, householdList)
        spinnerHouseHold.adapter = householdAdapter
        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerHouseHold.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        var professionArray = resources.getStringArray(R.array.profession)
        professionArray.forEach { str ->
            professionList.add(str)
        }

        val professionAdapter = CustomSpinnerAdapter(activity, professionList)
        spinnerProfession.adapter = professionAdapter
        spinnerProfession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerProfession.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerHouseHold.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            if (prepareDataForPosting()) {
                postDataofRewardsToServer()
            }
        }
    }

    private fun prepareDataForPosting(): Boolean {
        if (!editInstagram.text.isNullOrEmpty()) {
            setValuesForSocial(Constants.SocialPlatformName.instagram, editInstagram.text.toString())
        }
        if (!editTwitter.text.isNullOrEmpty()) {
            setValuesForSocial(Constants.SocialPlatformName.twitter, editTwitter.text.toString())
        }
        if (!editWebsite.text.isNullOrEmpty()) {
            setValuesForSocial(Constants.SocialPlatformName.website, editWebsite.text.toString())
        }
        if (!editYoutube.text.isNullOrEmpty()) {
            setValuesForSocial(Constants.SocialPlatformName.youtube, editYoutube.text.toString())
        }

        return true
    }

    /*fetch data from server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            Log.e("body to api ", Gson().toJson(apiGetResponse))
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData(userId!!, apiGetResponse, 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<SetupBlogData>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<SetupBlogData>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response.data!!.msg.equals(Constants.SUCCESS_MESSAGE)) {
                        //apiGetResponse = response.data!!.result
                        submitListener.socialOnSubmitListener()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                }
            })
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun socialOnSubmitListener()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            removeProgressDialog()
        }
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(activity, requestCode, resultCode, data)
    }

    fun updateFaceBookView() {
        editFacebook.setText(getString(R.string.rewards_social_facebook_connected))
    }

}
