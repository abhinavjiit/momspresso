package com.mycity4kids.ui.activity.phoneLogin

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
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

class SendSMSFragment : BaseFragment(), View.OnClickListener {
    var phoneEditText: EditText? = null
    var useSmsTextView: TextView? = null
    private val CREDENTIAL_PICKER_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_send_sms, container, false)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        useSmsTextView = view.findViewById(R.id.useSmsTextView)

        useSmsTextView?.isEnabled = false

        phoneEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val s1 = phoneEditText?.text!!.toString()
                useSmsTextView?.isEnabled = s1.length >= 10
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        useSmsTextView?.setOnClickListener(this)
        phoneEditText?.requestFocus()

        requestHint()
        return view
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        activity?.let {
            val credentialsClient = Credentials.getClient(it)
            val intent = credentialsClient.getHintPickerIntent(hintRequest)
            startIntentSenderForResult(
                    intent.intentSender,
                    CREDENTIAL_PICKER_REQUEST,
                    null, 0, 0, 0, null
            )
        }
    }

    override fun onClick(v: View?) {
        when {
            v?.id == R.id.useSmsTextView -> {
                val phoneLoginRequest = PhoneLoginRequest()
                phoneLoginRequest.phone = phoneEditText?.text?.toString()

                val retrofit = BaseApplication.getInstance().retrofit
                val loginRegistrationAPI = retrofit.create(LoginRegistrationAPI::class.java)
                val call = loginRegistrationAPI.triggerSMS(phoneLoginRequest)
//                launchVerifySMSFragment("dwdwdw")
                call.enqueue(triggerSMSResponseCallback)
            }
        }
    }

    val triggerSMSResponseCallback = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
            if (response.body() == null) {
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
                    val sms_token = jObject.getJSONObject("data").getJSONObject("result").getString("sms_token")
                    activity?.let {
                        val task = SmsRetriever.getClient(it).startSmsUserConsent(null)
                    }
                    launchVerifySMSFragment(sms_token)
                } else {

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
    }

    private fun launchVerifySMSFragment(sms_token: String?) {
        val verifySMSFragment = VerifySMSFragment()
        val bundle = Bundle()
        bundle.putString("smsToken", sms_token)
        bundle.putString("phoneNumber", phoneEditText?.text?.toString())
        verifySMSFragment.arguments = bundle
        (activity as ActivityLogin).addFragment(verifySMSFragment, bundle, true, null)
    }

    override fun updateUi(response: Response?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                        if (credential.id.startsWith("+91")) {
                            phoneEditText?.setText(credential.id.substring(3))
                        } else {
                            phoneEditText?.setText(credential.id)
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
        }
    }
}