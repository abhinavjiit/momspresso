package com.mycity4kids.ui.activity.phoneLogin

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.request.PhoneLoginRequest
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI
import com.mycity4kids.ui.activity.ActivityLogin
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        activity?.let {
            verifySmsTextView?.text = getString(R.string.lang_sel_continue).toLowerCase().capitalize()
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
        return view
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
                verifySMS(smsToken)
            }
            v?.id == R.id.resendSmsTextView -> {
                resendOTP()
            }
        }
    }

    private fun resendOTP() {
        activity?.let {
            showProgressDialog(getString(R.string.please_wait))
        }
        countdownTimerTextView?.visibility = View.VISIBLE
        countDownTimer.start()
        resendSmsTextView?.visibility = View.GONE

        val phoneLoginRequest = PhoneLoginRequest()
        phoneLoginRequest.phone = phoneNumber

        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
        val call = loginRegistrationAPI.triggerSMS(phoneLoginRequest)
        call.enqueue(triggerSMSResponseCallback)
    }

    val triggerSMSResponseCallback = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
            removeProgressDialog()
            if (response.body() == null) {
                if (response.errorBody() != null) {
                    val resData = String(response.errorBody()!!.bytes())
                    val jObject = JSONObject(resData)
                    activity?.let {
                        (activity as ActivityLogin).showToast(jObject.getString("reason"))
                    }
                    return
                }
                if (response.raw() != null) {
                    val nee = NetworkErrorException(response.raw().toString())
                    Crashlytics.logException(nee)
                }
                return
            }
            try {
                if (response.isSuccessful) {
                    val resData = String(response.body()!!.bytes())
                    val jObject = JSONObject(resData)
                    smsToken = jObject.getJSONObject("data").getJSONObject("result").getString("sms_token")
                } else {

                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        }

        override fun onFailure(call: Call<ResponseBody>, e: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun verifySMS(sms_token: String?) {
        val phoneLoginRequest = PhoneLoginRequest()
        phoneLoginRequest.verification_code = otpEditText1?.text?.toString() + otpEditText2?.text?.toString() + otpEditText3?.text?.toString() +
                otpEditText4?.text?.toString() + otpEditText5?.text?.toString() + otpEditText6?.text?.toString()
        phoneLoginRequest.sms_token = sms_token
        val retrofit = BaseApplication.getInstance().retrofit
        val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
        val call = loginRegistrationAPI.verifySMS(phoneLoginRequest)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if (response.body() == null) {
                    if (response.errorBody() != null) {
                        val resData = String(response.errorBody()!!.bytes())
                        val jObject = JSONObject(resData)
                        activity?.let {
                            (activity as ActivityLogin).showToast(jObject.getString("reason"))
                        }
                        return
                    }
                    if (response.raw() != null) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                    }
                    return
                }
                try {
                    if (response.isSuccessful) {
                        val resData = String(response.body()!!.bytes())
                        val jObject = JSONObject(resData)
                        if (response.code() == 200) {
                            val auth_token = jObject.getJSONObject("data").getJSONObject("result").getString("auth_token")
                            activity?.let {
                                (activity as ActivityLogin).phoneLogin(auth_token)
                            }
                        } else if (response.code() == 401) {
                            activity?.let {
                                (activity as ActivityLogin).showToast(jObject.getString("reason"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, e: Throwable) {
                Crashlytics.logException(e)
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
            verifySmsTextView?.isEnabled = !(otpEditText1?.text?.toString().isNullOrBlank() || otpEditText2?.text?.toString().isNullOrBlank() ||
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

    private val countDownTimer = object : CountDownTimer(30 * 1000, 1000) {
        override fun onFinish() {
            countdownTimerTextView?.visibility = View.GONE
            resendSmsTextView?.visibility = View.VISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {
            countdownTimerTextView?.text = getString(R.string.login_remaining_time, millisUntilFinished / 1000)
        }
    }

    override fun updateUi(response: Response?) {

    }
}