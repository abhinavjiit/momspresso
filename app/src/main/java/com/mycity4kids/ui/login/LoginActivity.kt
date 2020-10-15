package com.mycity4kids.ui.login

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.interfaces.IFacebookUser
import com.mycity4kids.models.request.LoginRegistrationRequest
import com.mycity4kids.models.request.PhoneLoginRequest
import com.mycity4kids.models.request.SocialConnectRequest
import com.mycity4kids.models.response.BaseResponse
import com.mycity4kids.models.response.FBPhoneLoginResponse
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.models.user.UserInfo
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI
import com.mycity4kids.sync.CategorySyncService
import com.mycity4kids.sync.PushTokenService
import com.mycity4kids.ui.activity.CustomSignUpActivity
import com.mycity4kids.ui.activity.LoadingActivity
import com.mycity4kids.ui.activity.PhoneLoginUserDetailActivity
import com.mycity4kids.ui.activity.phoneLogin.SendSMSFragment
import com.mycity4kids.ui.fragment.ChooseLoginAccountDialogFragment
import com.mycity4kids.ui.fragment.EmailLoginFragment
import com.mycity4kids.ui.fragment.FacebookAddEmailDialogFragment
import com.mycity4kids.utils.ConnectivityUtils
import com.mycity4kids.utils.StringUtils
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class LoginActivity : BaseActivity(), IFacebookUser, View.OnClickListener {

    private val SCOPES = "https://www.googleapis.com/auth/plus.login " + " email "
    private val RECOVERABLE_REQUEST_CODE = 98
    private val RC_SIGN_IN = 9001
    private lateinit var chooseLoginAccountFragment: ChooseLoginAccountDialogFragment
    private lateinit var dialogFragment: FacebookAddEmailDialogFragment
    private lateinit var callbackManager: CallbackManager
    private lateinit var mSignInClient: GoogleSignInClient
    private var googleEmailId: String? = null
    private val accessToken = ""
    private var googleToken = ""
    private var loginMode = ""
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val options =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(
                Scope(Scopes.PLUS_ME)
            ).requestEmail().build()
        mSignInClient = GoogleSignIn.getClient(this, options)
        callbackManager = CallbackManager.Factory.create()

        facebookTextView.text =
            getString(R.string.ad_bottom_bar_facebook).toLowerCase().capitalize()
        googleTextView.text =
            getString(R.string.app_settings_edit_prefs_google).toLowerCase().capitalize()
        phoneTextView.text =
            getString(R.string.rewards_phone).toLowerCase().capitalize()

        facebookTextView.setOnClickListener(this)
        googleTextView.setOnClickListener(this)
        phoneTextView.setOnClickListener(this)
        emailTextView.setOnClickListener(this)

        headerTextView.setOnClickListener {
            if (count == 9) {
                count = 0
                val signUpSIntent = Intent(this, CustomSignUpActivity::class.java)
                startActivity(signUpSIntent)
            } else {
                count++
            }
        }
    }

    override fun onClick(v: View?) {
        when {
            v?.id == R.id.facebookTextView -> {
                Utils.shareEventTracking(this, "Login screen", "Login_Android", "Login_Fb")
                loginWithFacebook()
            }
            v?.id == R.id.googleTextView -> {
                Utils.shareEventTracking(this, "Login screen", "Login_Android", "Login_Google")
                loginWithGoogle()
            }
            v?.id == R.id.phoneTextView -> {
                Utils.shareEventTracking(this, "Login screen", "Login_Android", "Login_Phone")
                val fragment = SendSMSFragment()
                val mBundle = Bundle()
                fragment.arguments = mBundle
                addFragment(fragment, mBundle)
            }
            v?.id == R.id.emailTextView -> {
                val fragment = EmailLoginFragment()
                val mBundle = Bundle()
                fragment.arguments = mBundle
                addFragment(fragment, mBundle)
            }
        }
    }

    private fun loginWithFacebook() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.please_wait))
            FacebookUtils.facebookLogin(this, this)
        } else {
            showToast(getString(R.string.error_network))
        }
    }

    private fun loginWithGoogle() {
        val intent = mSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    fun loginWithPhone(authCode: String?) {
        loginMode = "phone"
        showProgressDialog(getString(R.string.please_wait))
        val phoneLoginRequest = PhoneLoginRequest()
        phoneLoginRequest.auth_token = authCode
        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
        val call = loginRegistrationAPI.loginWithPhoneToken(phoneLoginRequest)
        call.enqueue(phoneLoginResponseCallback)
    }

    override fun getFacebookUser(jsonObject: JSONObject?, user: String?) {
        try {
            if (user != null) {
                loginMode = "fb"
                val lr = LoginRegistrationRequest()
                lr.cityId = "" + SharedPrefUtils.getCurrentCityModel(this).id
                lr.requestMedium = "fb"
                lr.socialToken = user
                val retrofit = BaseApplication.getInstance().retrofit
                val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
                val call = loginRegistrationAPI.login(lr)
                call.enqueue(onLoginResponseReceivedListener)
            }
        } catch (e: Exception) {
            removeProgressDialog()
            showToast(getString(R.string.toast_response_error))
        }
    }

    private val phoneLoginResponseCallback: Callback<FBPhoneLoginResponse> =
        object : Callback<FBPhoneLoginResponse> {
            override fun onResponse(
                call: Call<FBPhoneLoginResponse>,
                response: Response<FBPhoneLoginResponse>
            ) {
                Log.d("SUCCESS", "" + response)
                removeProgressDialog()
                if (response.body() == null) {
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data[0].result.size > 1) {
                            chooseLoginAccountFragment = ChooseLoginAccountDialogFragment()
                            val args = Bundle()
                            args.putParcelableArrayList(
                                "accountList",
                                responseData.data[0].result as ArrayList<out Parcelable?>
                            )
                            chooseLoginAccountFragment.arguments = args
                            val fm = supportFragmentManager
                            chooseLoginAccountFragment.isCancelable = false
                            chooseLoginAccountFragment.show(fm, "Accounts")
                        } else {
                            loginWithAccount(responseData.data[0].result[0])
                        }
                    } else {
                        showToast(responseData.reason)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    showToast(getString(R.string.went_wrong))
                }
            }

            override fun onFailure(
                call: Call<FBPhoneLoginResponse>,
                t: Throwable
            ) {
                Log.d("MC4kException", Log.getStackTraceString(t))
                apiExceptions(t)
            }
        }

    fun loginWithAccount(userDetailResult: UserDetailResult) {
        val model = UserInfo()
        model.id = userDetailResult.id
        model.dynamoId = userDetailResult.dynamoId
        model.email = userDetailResult.email
        model.mc4kToken = userDetailResult.mc4kToken
        model.isValidated = userDetailResult.isValidated
        model.first_name = userDetailResult.firstName
        model.last_name = userDetailResult.lastName
        model.profilePicUrl = userDetailResult.profilePicUrl.clientApp
        model.sessionId = userDetailResult.sessionId
        model.isLangSelection = userDetailResult.isLangSelection
        model.userType = userDetailResult.userType
        model.blogTitle = userDetailResult.blogTitle
        model.isNewUser = userDetailResult.isNewUser
        val cityIdFromLocation =
            SharedPrefUtils.getCurrentCityModel(this@LoginActivity).id
        if (cityIdFromLocation == AppConstants.OTHERS_CITY_ID) {
            model.cityId = userDetailResult.cityId
        }
        model.sessionId = userDetailResult.sessionId
        model.loginMode = loginMode
        model.videoPreferredLanguages = userDetailResult.videoPreferredLanguages
        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model)
        SharedPrefUtils
            .setProfileImgUrl(
                BaseApplication.getAppContext(),
                userDetailResult.profilePicUrl.clientApp
            )
        SharedPrefUtils.setLastLoginTimestamp(
            BaseApplication.getAppContext(),
            System.currentTimeMillis()
        )
        val mixpanel =
            MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN)
        try {
            val jsonObject = JSONObject()
            jsonObject.put(
                "userId",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            )
            jsonObject.put("loginFrom", loginMode)
            mixpanel.track("UserLogin", jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (null == userDetailResult.socialTokens) { // token already expired or not yet connected with facebook
            SharedPrefUtils.setFacebookConnectedFlag(BaseApplication.getAppContext(), "1")
        } else {
            SharedPrefUtils.setFacebookConnectedFlag(
                BaseApplication.getAppContext(),
                userDetailResult.socialTokens.fb.isExpired
            )
        }
        if ("fb" == loginMode) {
            val socialConnectRequest = SocialConnectRequest()
            socialConnectRequest.token = accessToken
            socialConnectRequest.referer = "fb"
            val retrofit = BaseApplication.getInstance().retrofit
            val socialConnectAPI = retrofit.create(
                LoginRegistrationAPI::class.java
            )
            val socialConnectCall =
                socialConnectAPI.socialConnect(socialConnectRequest)
            socialConnectCall.enqueue(socialConnectResponseListener)
        }
        // facebook login with an account without email
        if (AppConstants.VALIDATED_USER != model.isValidated && "fb" == loginMode) {
            dialogFragment = FacebookAddEmailDialogFragment()
            dialogFragment.setTargetFragment(dialogFragment, 2)
            val bundle = Bundle()
            bundle.putString(AppConstants.FROM_ACTIVITY, AppConstants.ACTIVITY_LOGIN)
            dialogFragment.arguments = bundle
            dialogFragment.show(supportFragmentManager, "verify email")
        } else {
            if ("phone" == loginMode &&
                ((StringUtils.isNullOrEmpty(userDetailResult.firstName) && StringUtils
                    .isNullOrEmpty(userDetailResult.lastName)) ||
                    userDetailResult.firstName.toUpperCase().contains("XXX"))) {
                startSyncingUserInfo()
                val intent = Intent(this, PushTokenService::class.java)
                startService(intent)
                val mServiceIntent = Intent(this, CategorySyncService::class.java)
                startService(mServiceIntent)
                val intent1 = Intent(this, PhoneLoginUserDetailActivity::class.java)
                startActivity(intent1)
            } else {
                loginSuccessLoadHomePage()
            }
        }
    }

    private fun loginSuccessLoadHomePage() {
        val intent = Intent(this, PushTokenService::class.java)
        startService(intent)
        val mServiceIntent = Intent(this, CategorySyncService::class.java)
        startService(mServiceIntent)
        val intent1 = Intent(this, LoadingActivity::class.java)
        startActivity(intent1)
    }

    private var onLoginResponseReceivedListener: Callback<UserDetailResponse> =
        object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val model =
                            UserInfo()
                        model.id = responseData.data[0].result.id
                        model.dynamoId = responseData.data[0].result.dynamoId
                        model.email = responseData.data[0].result.email
                        model.mc4kToken = responseData.data[0].result.mc4kToken
                        model.isValidated = responseData.data[0].result.isValidated
                        model.first_name = responseData.data[0].result.firstName
                        model.last_name = responseData.data[0].result.lastName
                        model.profilePicUrl = responseData.data[0].result.profilePicUrl.clientApp
                        model.sessionId = responseData.data[0].result.sessionId
                        model.isLangSelection = responseData.data[0].result.isLangSelection
                        model.userType = responseData.data[0].result.userType
                        model.gender = "" + responseData.data[0].result.gender
                        model.blogTitle = responseData.data[0].result.blogTitle
                        model.isNewUser = responseData.data[0].result.isNewUser
                        val cityIdFromLocation =
                            SharedPrefUtils.getCurrentCityModel(this@LoginActivity).id
                        if (cityIdFromLocation == AppConstants.OTHERS_CITY_ID) {
                            model.cityId = responseData.data[0].result.cityId
                        }
                        model.sessionId = responseData.data[0].result.sessionId
                        model.loginMode = loginMode
                        model.videoPreferredLanguages = responseData.data[0].result.videoPreferredLanguages
                        SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), model)
                        SharedPrefUtils.setProfileImgUrl(
                            BaseApplication.getAppContext(),
                            responseData.data[0].result.profilePicUrl.clientApp
                        )
                        SharedPrefUtils.setLastLoginTimestamp(
                            BaseApplication.getAppContext(),
                            System.currentTimeMillis()
                        )
                        val mixpanel = MixpanelAPI
                            .getInstance(
                                BaseApplication.getAppContext(),
                                AppConstants.MIX_PANEL_TOKEN
                            )
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put(
                                "userId",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                            )
                            jsonObject.put("loginFrom", loginMode)
                            mixpanel.track("UserLogin", jsonObject)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (null == responseData.data[0].result.socialTokens) {
                            // token already expired or not yet connected with facebook
                            SharedPrefUtils.setFacebookConnectedFlag(
                                BaseApplication.getAppContext(),
                                "1"
                            )
                        } else {
                            SharedPrefUtils.setFacebookConnectedFlag(
                                BaseApplication.getAppContext(),
                                responseData.data[0].result.socialTokens.fb.isExpired
                            )
                        }
                        if ("fb" == loginMode) {
                            val socialConnectRequest = SocialConnectRequest()
                            socialConnectRequest.token = accessToken
                            socialConnectRequest.referer = "fb"
                            val retrofit = BaseApplication.getInstance().retrofit
                            val socialConnectAPI =
                                retrofit.create(
                                    LoginRegistrationAPI::class.java
                                )
                            val socialConnectCall =
                                socialConnectAPI.socialConnect(socialConnectRequest)
                            socialConnectCall.enqueue(socialConnectResponseListener)
                        }
                        // facebook login with an account without email
                        if (AppConstants.VALIDATED_USER != model.isValidated && "fb" == loginMode) {
                            dialogFragment = FacebookAddEmailDialogFragment()
                            val bundle = Bundle()
                            bundle.putString(
                                AppConstants.FROM_ACTIVITY,
                                AppConstants.ACTIVITY_LOGIN
                            )
                            dialogFragment.arguments = bundle
                            dialogFragment.show(supportFragmentManager, "verify email")
                        } else if (AppConstants.VALIDATED_USER != model.isValidated) {
                            showVerifyEmailDialog("Error", "Please verify your account to login")
                        } else {
                            loginSuccessLoadHomePage()
                        }
                    } else {
                        showToast(responseData.reason)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    showToast(getString(R.string.went_wrong))
                }
            }

            override fun onFailure(
                call: Call<UserDetailResponse>,
                t: Throwable
            ) {
                removeProgressDialog()
                Log.d("MC4kException", Log.getStackTraceString(t))
                FirebaseCrashlytics.getInstance().recordException(t)
                apiExceptions(t)
            }
        }

    private val socialConnectResponseListener: Callback<BaseResponse> =
        object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                Log.d("FacebookConnect", "SUCCESS")
            }

            override fun onFailure(
                call: Call<BaseResponse>,
                t: Throwable
            ) {
                Log.d("FacebookConnect", "FAILURE")
                apiExceptions(t)
            }
        }

    var onVerifyEmailLinkResendResponseReceived: Callback<UserDetailResponse> =
        object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code != 200 || Constants.SUCCESS != responseData.status) {
                        showToast(responseData.reason)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<UserDetailResponse>,
                t: Throwable
            ) {
                Log.d("MC4kException", Log.getStackTraceString(t))
                FirebaseCrashlytics.getInstance().recordException(t)
                apiExceptions(t)
            }
        }

    private fun showVerifyEmailDialog(title: String, message: String) {
        AlertDialog.Builder(this, R.style.MyAlertDialogStyle).setTitle(title).setMessage(message)
            .setPositiveButton(
                R.string.ok
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .setNegativeButton(
                R.string.resend_email
            ) { dialogInterface, i ->
                val lr = LoginRegistrationRequest()
                lr.email = SharedPrefUtils.getUserDetailModel(this).email
                val retrofit = BaseApplication.getInstance().retrofit
                val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
                val call = loginRegistrationAPI.resendVerificationLink(lr)
                call.enqueue(onVerifyEmailLinkResendResponseReceived)
                dialogInterface.dismiss()
            }.setIcon(android.R.drawable.ic_dialog_alert).show()
    }

    fun addEmail(email: String) {
        Utils.shareEventTracking(
            this,
            "Login screen",
            "Login_Android",
            "Login_Fb_Email"
        )
        showProgressDialog(getString(R.string.please_wait))
        val lr = LoginRegistrationRequest()
        lr.email = email
        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(
            LoginRegistrationAPI::class.java
        )
        val call = loginRegistrationAPI.addFacebookEmail(lr)
        call.enqueue(onAddFacebookEmailResponseReceived)
    }

    fun cancelAddEmail() {
        removeProgressDialog()
        dialogFragment.dismiss()
    }

    var onAddFacebookEmailResponseReceived: Callback<UserDetailResponse> =
        object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse?>,
                response: Response<UserDetailResponse?>
            ) {
                removeProgressDialog()
                Log.d("SUCCESS", "" + response)
                if (response.body() == null) {
                    showToast(getString(R.string.went_wrong))
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        dialogFragment.dismiss()
                        showToast(getString(R.string.verify_email))
                        loginSuccessLoadHomePage()
                    } else {
                        showToast(responseData.reason)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("Exception", Log.getStackTraceString(e))
                    showToast(getString(R.string.went_wrong))
                }
            }

            override fun onFailure(
                call: Call<UserDetailResponse>,
                t: Throwable
            ) {
                Log.d("MC4kException", Log.getStackTraceString(t))
                FirebaseCrashlytics.getInstance().recordException(t)
                apiExceptions(t)
            }
        }

    override fun onActivityResult(_requestCode: Int, _resultCode: Int, _data: Intent?) {
        super.onActivityResult(_requestCode, _resultCode, _data)
        if (_requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(_data)
            if (task.isSuccessful) {
                try {
                    val acct = task.getResult(ApiException::class.java)
                    getGooglePlusInfo(acct)
                } catch (e: ApiException) {
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            } else {
                showToast(getString(R.string.toast_response_error))
            }
        } else if (_requestCode == RECOVERABLE_REQUEST_CODE) {
            removeProgressDialog()
            val extra = _data?.extras
            googleToken = extra?.getString("authtoken")!!
            if (StringUtils.isNullOrEmpty(googleToken)) {
                return
            }
            val lr = LoginRegistrationRequest()
            lr.cityId = "" + SharedPrefUtils.getCurrentCityModel(this).id
            lr.requestMedium = "gp"
            lr.socialToken = googleToken
            val retrofit = BaseApplication.getInstance().retrofit
            val loginRegistrationAPI = retrofit.create(
                LoginRegistrationAPI::class.java
            )
            val call = loginRegistrationAPI.login(lr)
            call.enqueue(onLoginResponseReceivedListener)
        } else {
            if (_resultCode == 0) {
                removeProgressDialog()
            }
            callbackManager.onActivityResult(_requestCode, _resultCode, _data)
            FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data)
        }
    }

    private fun getGooglePlusInfo(result: GoogleSignInAccount?) {
        try {
            if (isFinishing) {
                return
            }
            loginMode = "gp"
            googleEmailId = result?.email
            if (StringUtils.isNullOrEmpty(googleEmailId)) {
                googleEmailId = "Email not fetch from google."
            }
            GetGoogleToken().execute()
        } catch (e: Exception) {
            removeProgressDialog()
            showToast(getString(R.string.toast_response_error))
            e.printStackTrace()
        }
    }

    inner class GetGoogleToken : AsyncTask<Void?, String?, String?>() {
        override fun doInBackground(vararg params: Void?): String {
            try {
                googleToken = GoogleAuthUtil.getToken(
                    this@LoginActivity,
                    googleEmailId,
                    "oauth2:" + SCOPES
                )
                return googleToken
            } catch (userAuthEx: UserRecoverableAuthException) {
                userAuthEx.printStackTrace()
                Log.d("UserRecoverAuthExceptin", userAuthEx.toString())
                // Start the user recoverable action using the intent returned
                startActivityForResult(
                    userAuthEx.intent,
                    RECOVERABLE_REQUEST_CODE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (StringUtils.isNullOrEmpty(result)) {
                return
            }
            val lr = LoginRegistrationRequest()
            lr.cityId = "" + SharedPrefUtils.getCurrentCityModel(this@LoginActivity).id
            lr.requestMedium = "gp"
            lr.socialToken = googleToken
            val retrofit = BaseApplication.getInstance().retrofit
            val loginRegistrationAPI = retrofit.create(
                LoginRegistrationAPI::class.java
            )
            val call = loginRegistrationAPI.login(lr)
            call.enqueue(onLoginResponseReceivedListener)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loginRequest(emailId: String, password: String) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.please_wait))
            loginMode = "email"
            val lr = LoginRegistrationRequest()
            lr.email = emailId
            lr.password = password
            lr.requestMedium = "custom"

            val retrofit = BaseApplication.getInstance().retrofit
            val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
            val call = loginRegistrationAPI.login(lr)
            call.enqueue(onLoginResponseReceivedListener)
        } else {
            showToast(getString(R.string.error_network))
        }
    }
}
