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
import com.mycity4kids.models.request.LoginRegistrationRequest
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
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
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 */
class RewardsSocialInfoFragment : BaseFragment(), IFacebookUser, GoogleApiClient.OnConnectionFailedListener, InstagramApp.OAuthAuthenticationListener {
    override fun onSuccess() {
        if (mApp!!.hasAccessToken()) {
            editInstagram.setText("INSTAGRAM CONNECTED")
            instagramAuthToken= mApp!!.accessToken
        }
    }

    override fun onFail(error: String?) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun getFacebookUser(user: String?) {
        try {
            if (user != null) {
                facebookAuthToken= user
                editFacebook.setText("FACEBOOK CONNECTED")
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
    private lateinit var apiGetResponse: RewardsDetailsResultResonse
    private var loginMode = ""
    private var mApp: InstagramApp? = null
    private var userInfoHashmap = HashMap<String, String>()
    private var callbackManager: CallbackManager? = null
    private var facebookAuthToken : String? = null
    private var instagramAuthToken : String? = null

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


        mApp = InstagramApp(activity, Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.INSTA_CALLBACK_URL)
        mApp!!.setListener(this)

//        mApp!!.setListener(object : InstagramApp.OAuthAuthenticationListener{
//            override fun onSuccess() {
//                //mApp!!.fetchUserName(handler)
//            }
//
//            override fun onFail(error: String?) {
//                Toast.makeText(activity, error, Toast.LENGTH_SHORT)
//                        .show()
//            }
//
//        })
//
        if (mApp!!.hasAccessToken()) {
            editInstagram.setText("INSTAGRAM CONNECTED")
            instagramAuthToken= mApp!!.accessToken
        }


        return containerView
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData("8ffb68f436724516850cdfdb5d064d69", 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

    private fun setValuesToComponents() {
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
            AuthenticateWithInstagram()
        }

        editInstagram.setOnClickListener {
            AuthenticateWithInstagram()
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

//        editDurables.setOnClickListener {
//            if (preSelectedDurables.isNotEmpty()) {
//                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.DURABLES.name,
//                        isSingleSelection = true, preSelectedItemIds = preSelectedDurables, context = this@RewardsSocialInfoFragment)
//                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
//            } else {
//                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.DURABLES.name,
//                        isSingleSelection = true, preSelectedItemIds = apiGetResponse?.durables!!, context = this@RewardsSocialInfoFragment)
//                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
//            }
//        }

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            //submitListener.socialOnSubmitListener()
            //postDataofRewardsToServer()
        }
    }

    private fun AuthenticateWithInstagram() {
        if (!mApp!!.hasAccessToken()) {
            mApp!!.authorize()
        }
    }

    /*fetch data from server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData("8ffb68f436724516850cdfdb5d064d69", apiGetResponse, 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

//                        /*setting values to components*/
//                        setValuesToComponents()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {

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

    fun updateFaceBookView(){
        editFacebook.setText("FACEBOOK CONNECTED")
    }

}
