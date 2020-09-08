package com.mycity4kids.ui.activity.phoneLogin

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.request.PhoneLoginRequest
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI
import com.mycity4kids.ui.activity.ActivityLogin
import com.mycity4kids.ui.activity.OTPActivity
import com.mycity4kids.ui.activity.UpdateUserHandleActivity
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class VerifySMSFragment : BaseFragment(), View.OnClickListener {

    var otpEditText1: EditText? = null
    var otpEditText2: EditText? = null
    var otpEditText3: EditText? = null
    var otpEditText4: EditText? = null
    var otpEditText5: EditText? = null
    var otpEditText6: EditText? = null
    var verifySmsTextView: TextView? = null
    var resendSmsTextView: TextView? = null
    var countdownTimerTextView: TextView? = null

    var smsToken: String? = null
    var phoneNumber: String? = null
    var mAuth: FirebaseAuth? = null
    var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    var mResendToken: ForceResendingToken? = null
    var countDownTimer: CountDownTimer? = null
    var mVerificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_verify_sms, container, false)

        otpEditText1 = view.findViewById(R.id.otpEditText1)
        otpEditText2 = view.findViewById(R.id.otpEditText2)
        otpEditText3 = view.findViewById(R.id.otpEditText3)
        otpEditText4 = view.findViewById(R.id.otpEditText4)
        otpEditText5 = view.findViewById(R.id.otpEditText5)
        otpEditText6 = view.findViewById(R.id.otpEditText6)
        verifySmsTextView = view.findViewById(R.id.verifySmsTextView)
        resendSmsTextView = view.findViewById(R.id.resendSmsTextView)
        countdownTimerTextView = view.findViewById(R.id.countdownTimerTextView)

        smsToken = arguments?.getString("smsToken")
        phoneNumber = arguments?.getString("phoneNumber")

        mAuth = FirebaseAuth.getInstance()

        activity?.let {
            verifySmsTextView?.text =
                getString(R.string.lang_sel_continue).toLowerCase().capitalize()
        }
        verifySmsTextView?.isEnabled = false

        otpEditText1?.addTextChangedListener(CustomTextWatcher(otpEditText1))
        otpEditText2?.addTextChangedListener(CustomTextWatcher(otpEditText2))
        otpEditText3?.addTextChangedListener(CustomTextWatcher(otpEditText3))
        otpEditText4?.addTextChangedListener(CustomTextWatcher(otpEditText4))
        otpEditText5?.addTextChangedListener(CustomTextWatcher(otpEditText5))
        otpEditText6?.addTextChangedListener(CustomTextWatcher(otpEditText6))

        setKeyListenerForEditText(otpEditText6, otpEditText5)
        setKeyListenerForEditText(otpEditText5, otpEditText4)
        setKeyListenerForEditText(otpEditText4, otpEditText3)
        setKeyListenerForEditText(otpEditText3, otpEditText2)
        setKeyListenerForEditText(otpEditText2, otpEditText1)

        otpEditText1?.requestFocus()
        otpEditText1?.isCursorVisible = true

        verifySmsTextView?.setOnClickListener(this)
        resendSmsTextView?.setOnClickListener(this)

        //        startSMSRetrieverAPI()

        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
            }
        }
        startPhoneNumberVerification("+$smsToken$phoneNumber")


        return view
    }


    fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            30,
            TimeUnit.SECONDS,
            fetchActivity(),
            mCallbacks as OnVerificationStateChangedCallbacks
        )
        startCounter()
    }


    private fun startCounter() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTimerTextView?.setText("" + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                countdownTimerTextView?.setText("")
                resendSmsTextView?.setEnabled(true)
            }
        }
        countDownTimer!!.start()
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            30,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            fetchActivity(),
            mCallbacks as OnVerificationStateChangedCallbacks,
            token
        )
        startCounter()
        resendSmsTextView?.setEnabled(false)
    }

    private fun verifyPhoneNumberWithCode(
        verificationId: String,
        code: String
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signOut() {
        mAuth!!.signOut()
    }


    private fun validate(): Boolean {
        if (TextUtils.isEmpty(otpEditText1?.getText().toString().trim({ it <= ' ' }))) {
            return false
        } else if (TextUtils.isEmpty(otpEditText2?.getText().toString().trim({ it <= ' ' }))) {
            return false
        } else if (TextUtils.isEmpty(otpEditText3?.getText().toString().trim({ it <= ' ' }))) {
            return false
        } else if (TextUtils.isEmpty(otpEditText4?.getText().toString().trim({ it <= ' ' }))) {
            return false
        } else if (TextUtils.isEmpty(otpEditText5?.getText().toString().trim({ it <= ' ' }))) {
            return false
        } else if (TextUtils.isEmpty(otpEditText6?.getText().toString().trim({ it <= ' ' }))) {
            return false
        }
        return true
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        var mUser = FirebaseAuth.getInstance().currentUser
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                fetchActivity(),
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        mUser = task.result!!.user
                    } else {

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        }

                        if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                            (activity as ActivityLogin).finish()
                        } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                            val intent = Intent()
                            intent.putExtra("PHONE_NUMBER", "")
                            (activity as OTPActivity).setResult(Activity.RESULT_OK, intent)
                            (activity as OTPActivity).finish()
                        } else if (activity?.javaClass?.simpleName.equals("UpdateUserHandleActivity")) {
                            (activity as UpdateUserHandleActivity).finish()
                        }
                    }
                })

        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token

                    if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                        (activity as ActivityLogin).phoneLogin(idToken)
                    } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                        val intent = Intent()
                        intent.putExtra("auth_token", idToken)
                        intent.putExtra("phone", mUser!!.phoneNumber)
                        (activity as OTPActivity).setResult(Activity.RESULT_OK, intent)
                        (activity as OTPActivity).finish()
                    } else if (activity?.javaClass?.simpleName.equals("UpdateUserHandleActivity")) {
                        val intent = Intent()
                        intent.putExtra("auth_token", idToken)
                        (activity as UpdateUserHandleActivity).setResult(
                            Activity.RESULT_OK,
                            intent
                        )
                        val fm: FragmentManager =
                            (activity as UpdateUserHandleActivity).supportFragmentManager
                        val count = fm.backStackEntryCount
                        for (i in 0 until count) {
                            fm.popBackStackImmediate()
                        }
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }

                    if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                        (activity as ActivityLogin).finish()
                    } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                        val intent = Intent()
                        intent.putExtra("PHONE_NUMBER", "")
                        (activity as OTPActivity).setResult(Activity.RESULT_OK, intent)
                        (activity as OTPActivity).finish()
                    } else if (activity?.javaClass?.simpleName.equals("UpdateUserHandleActivity")) {
                        (activity as UpdateUserHandleActivity).finish()
                    }
                }
            }
    }

    private fun fetchActivity(): Activity {
        var activities: Activity? = null
        if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
            activities = (activity as ActivityLogin)
        } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
            activities = (activity as OTPActivity)
        } else if (activity?.javaClass?.simpleName.equals("UpdateUserHandleActivity")) {
            activities = (activity as UpdateUserHandleActivity)
        }
        return activities!!
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        activity?.registerReceiver(smsVerificationReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(smsVerificationReceiver)
    }

    private fun startSMSRetrieverAPI() {
        activity?.let {
            val client = SmsRetriever.getClient(it)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                Log.d("SMS Retriever", "SUCCESS")
            }

            task.addOnFailureListener {
                Log.d("SMS Retriever", "FAILURE")
            }
        }
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        try {
                            // Get SMS message contents
                            val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server.
                            parseAndFillOTP(message)
                        } catch (e: ActivityNotFoundException) {
                            // Handle the exception ...
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    private fun setKeyListenerForEditText(currentEditText: EditText?, nextEditText: EditText?) {
        currentEditText?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                val text = currentEditText.text?.toString()
                if (text.isNullOrBlank()) {
                    setFocusable()
                    setCursorVisibility()
                    nextEditText?.text?.clear()
                    nextEditText?.isFocusableInTouchMode = true
                    nextEditText?.requestFocus()
                    nextEditText?.isCursorVisible = true
                }
            }
            false
        }
    }

    override fun onClick(v: View?) {
        when {
            v?.id == R.id.verifySmsTextView -> {
                //                verifySMS(smsToken)
                if (!TextUtils.isEmpty(mVerificationId)) {
                    verifyPhoneNumberWithCode(
                        mVerificationId!!,
                        otpEditText1?.text.toString().trim { it <= ' ' } +
                            otpEditText2?.text.toString().trim { it <= ' ' } +
                            otpEditText3?.text.toString().trim { it <= ' ' } +
                            otpEditText4?.text.toString().trim { it <= ' ' } +
                            otpEditText5?.text.toString().trim { it <= ' ' } +
                            otpEditText6?.text.toString().trim { it <= ' ' }
                    )
                } else {
                    Toast.makeText(
                        activity, "Verification id not received",
                        Toast.LENGTH_SHORT
                    )
                }
            }
            v?.id == R.id.resendSmsTextView -> {
                //                Utility.hideKeyBoardFromView(mActivity);
                if (mResendToken != null) {
                    resendVerificationCode("+$smsToken$phoneNumber", mResendToken!!)
                } else {
                    Toast.makeText(
                        activity,
                        "Resend token null",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    private fun resendOTP() {
        activity?.let {
            showProgressDialog(getString(R.string.please_wait))
        }
        countdownTimerTextView?.visibility = View.VISIBLE
        countDownTimer?.start()
        resendSmsTextView?.visibility = View.GONE

        val phoneLoginRequest = PhoneLoginRequest()
        phoneLoginRequest.phone = phoneNumber

        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
        val call = loginRegistrationAPI.triggerSMS(phoneLoginRequest)
        call.enqueue(triggerSMSResponseCallback)
    }

    val triggerSMSResponseCallback = object : Callback<ResponseBody> {
        override fun onResponse(
            call: Call<ResponseBody>,
            response: retrofit2.Response<ResponseBody>
        ) {
            removeProgressDialog()
            if (response.body() == null) {
                if (response.errorBody() != null) {
                    val resData = String(response.errorBody()!!.bytes())
                    val jObject = JSONObject(resData)
                    activity?.let {
                        //                        (activity as ActivityLogin).showToast(jObject.getString("reason"))

                        if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                            (activity as ActivityLogin).showToast(jObject.getString("reason"))
                        } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                            (activity as OTPActivity).showToast(jObject.getString("reason"))
                        }
                    }
                    return
                }
                if (response.raw() != null) {
                    val nee = NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                }
                return
            }
            try {
                if (response.isSuccessful) {
                    val resData = String(response.body()!!.bytes())
                    val jObject = JSONObject(resData)
                    smsToken =
                        jObject.getJSONObject("data").getJSONObject("result").getString("sms_token")
                } else {
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ResponseBody>, e: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun verifySMS(sms_token: String?) {
        val phoneLoginRequest = PhoneLoginRequest()
        phoneLoginRequest.verification_code =
            otpEditText1?.text?.toString() + otpEditText2?.text?.toString() + otpEditText3?.text?.toString() +
                otpEditText4?.text?.toString() + otpEditText5?.text?.toString() + otpEditText6?.text?.toString()
        phoneLoginRequest.sms_token = sms_token
        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
        val call = loginRegistrationAPI.verifySMS(phoneLoginRequest)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.body() == null) {
                    if (response.errorBody() != null) {
                        val resData = String(response.errorBody()!!.bytes())
                        val jObject = JSONObject(resData)
                        activity?.let {
                            //                            (activity as ActivityLogin).showToast(jObject.getString("reason"))

                            if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                                (activity as ActivityLogin).showToast(jObject.getString("reason"))
                            } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                                (activity as OTPActivity).showToast(jObject.getString("reason"))
                            }
                        }
                        return
                    }
                    if (response.raw() != null) {
                        val nee = NetworkErrorException(response.raw().toString())
                        FirebaseCrashlytics.getInstance().recordException(nee)
                    }
                    return
                }
                try {
                    if (response.isSuccessful) {
                        val resData = String(response.body()!!.bytes())
                        val jObject = JSONObject(resData)
                        if (response.code() == 200) {
                            val auth_token = jObject.getJSONObject("data").getJSONObject("result")
                                .getString("auth_token")
                            val mobile = jObject.getJSONObject("data").getJSONObject("result")
                                .getString("phone")
                            activity?.let {
                                //                                (activity as ActivityLogin).phoneLogin(auth_token)
                                if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                                    (activity as ActivityLogin).phoneLogin(auth_token)
                                } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                                    /* System.out.println("-------" + activity!!.supportFragmentManager.backStackEntryCount)
                                     for (i in fragmentManager!!.backStackEntryCount downTo 2) {
                                         activity!!.supportFragmentManager.popBackStack()
                                     }*/
                                    val intent = Intent()
                                    intent.putExtra("auth_token", auth_token)
                                    intent.putExtra("phone", mobile)
                                    (activity as OTPActivity).setResult(Activity.RESULT_OK, intent)
                                    (activity as OTPActivity).finish()
                                    //                                    (activity as OTPActivity).updateMobile(auth_token)
                                    //                                    fragmentManager!!.popBackStack("ProfileInfoFragment", 0)
                                    //                                    activity!!.supportFragmentManager.popBackStackImmediate()
                                } else if (activity?.javaClass?.simpleName.equals("UpdateUserHandleActivity")) {
                                    val intent = Intent()
                                    intent.putExtra("auth_token", auth_token)
                                    (activity as UpdateUserHandleActivity).setResult(
                                        Activity.RESULT_OK,
                                        intent
                                    )
                                    val fm: FragmentManager =
                                        (activity as UpdateUserHandleActivity).supportFragmentManager
                                    val count = fm.backStackEntryCount
                                    for (i in 0 until count) {
                                        fm.popBackStackImmediate()
                                    }
                                    //                                    activity!!.supportFragmentManager.popBackStackImmediate()
                                }
                            }
                        } else if (response.code() == 401) {
                            activity?.let {
                                //                                (activity as ActivityLogin).showToast(jObject.getString("reason"))

                                if (activity?.javaClass?.simpleName.equals("ActivityLogin")) {
                                    (activity as ActivityLogin).showToast(jObject.getString("reason"))
                                } else if (activity?.javaClass?.simpleName.equals("OTPActivity")) {
                                    (activity as OTPActivity).showToast(jObject.getString("reason"))
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, e: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private inner class CustomTextWatcher(private val mEditText: EditText?) : TextWatcher {

        var beforeX: Int? = null
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeX = count
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            Log.d("COUNT ", "count = " + count + "  -- before = ")
        }

        override fun afterTextChanged(s: Editable) {
            verifySmsTextView?.isEnabled =
                !(otpEditText1?.text?.toString().isNullOrBlank() || otpEditText2?.text?.toString().isNullOrBlank() ||
                    otpEditText3?.text?.toString().isNullOrBlank() || otpEditText4?.text?.toString().isNullOrBlank() ||
                    otpEditText5?.text?.toString().isNullOrBlank() || otpEditText6?.text?.toString().isNullOrBlank())

            when {
                otpEditText1?.id == mEditText?.id -> {
                    if (!otpEditText1?.text.isNullOrBlank()) {
                        setFocusable()
                        otpEditText2?.isFocusableInTouchMode = true
                        otpEditText2?.isCursorVisible = true
                        otpEditText2?.requestFocus()
                    }
                }
                otpEditText2?.id == mEditText?.id -> {
                    if (!otpEditText2?.text.isNullOrBlank()) {
                        setFocusable()
                        otpEditText3?.isFocusableInTouchMode = true
                        otpEditText3?.isCursorVisible = true
                        otpEditText3?.requestFocus()
                    }
                }
                otpEditText3?.id == mEditText?.id -> {
                    if (!otpEditText3?.text.isNullOrBlank()) {
                        setFocusable()
                        otpEditText4?.isFocusableInTouchMode = true
                        otpEditText4?.isCursorVisible = true
                        otpEditText4?.requestFocus()
                    }
                }
                otpEditText4?.id == mEditText?.id -> {
                    if (!otpEditText4?.text.isNullOrBlank()) {
                        setFocusable()
                        otpEditText5?.isFocusableInTouchMode = true
                        otpEditText5?.isCursorVisible = true
                        otpEditText5?.requestFocus()
                    }
                }
                otpEditText5?.id == mEditText?.id -> {
                    if (!otpEditText5?.text.isNullOrBlank()) {
                        setFocusable()
                        otpEditText6?.isFocusableInTouchMode = true
                        otpEditText6?.isCursorVisible = true
                        otpEditText6?.requestFocus()
                    }
                }
                otpEditText6?.id == mEditText?.id -> {
                }
            }
        }
    }

    private fun setFocusable() {
        otpEditText1?.isFocusable = false
        otpEditText2?.isFocusable = false
        otpEditText3?.isFocusable = false
        otpEditText4?.isFocusable = false
        otpEditText5?.isFocusable = false
        otpEditText6?.isFocusable = false
    }

    private fun setCursorVisibility() {
        otpEditText1?.isCursorVisible = false
        otpEditText2?.isCursorVisible = false
        otpEditText3?.isCursorVisible = false
        otpEditText4?.isCursorVisible = false
        otpEditText5?.isCursorVisible = false
        otpEditText6?.isCursorVisible = false
    }

    /*private var countDownTimer = object : CountDownTimer(30 * 1000, 1000) {
        override fun onFinish() {
            countdownTimerTextView?.visibility = View.GONE
            resendSmsTextView?.visibility = View.VISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {
            activity?.let {
                countdownTimerTextView?.text =
                    getString(R.string.login_remaining_time, millisUntilFinished / 1000)
            }
        }
    }*/

    fun parseAndFillOTP(message: String?) {
        try {
            if (!message.isNullOrBlank()) {
                val pattern = AppConstants.OTP_REGEX.toRegex()
                val matchedResult = pattern.find(message)
                val otp = matchedResult?.value
                otpEditText1?.setText(otp?.get(0)?.toString())
                otpEditText2?.setText(otp?.get(1)?.toString())
                otpEditText3?.setText(otp?.get(2)?.toString())
                otpEditText4?.setText(otp?.get(3)?.toString())
                otpEditText5?.setText(otp?.get(4)?.toString())
                otpEditText6?.setText(otp?.get(5)?.toString())
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
