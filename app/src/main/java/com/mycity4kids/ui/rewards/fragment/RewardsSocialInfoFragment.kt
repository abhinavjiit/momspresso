package com.mycity4kids.ui.rewards.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.facebook.CallbackManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.instagram.InstagramApp
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse
import com.mycity4kids.models.rewardsmodels.SocialAccountObject
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class RewardsSocialInfoFragment : BaseFragment(), InstagramApp.OAuthAuthenticationListener {
    override fun onSuccess() {
    }

    override fun onFail(error: String?) {
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
    private lateinit var textlater: TextView
    private var householdList = ArrayList<String>()
    private var professionList = ArrayList<String>()
    private var apiGetResponse: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private var callbackManager: CallbackManager? = null
    private var isComingFromRewards = false
    private var isComingFromCampaign = false
    private var spannable: SpannableString? = null
    private lateinit var socialDisclaimerTwo: TextView

    companion object {
        fun newInstance(
            isComingFromRewards: Boolean = false,
            fromCampaign: Boolean = false
        ) = RewardsSocialInfoFragment().apply {
            arguments = Bundle().apply {
                this.putBoolean("isComingFromRewards", isComingFromRewards)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_social_info, container, false)

        if (arguments != null) {
            isComingFromRewards = if (arguments!!.containsKey("isComingFromRewards")) {
                arguments!!.getBoolean("isComingFromRewards")
            } else {
                false
            }

            isComingFromCampaign = if (arguments!!.containsKey("fromFromCampaign")) {
                arguments!!.getBoolean("fromFromCampaign")
            } else {
                false
            }
        }
        callbackManager = CallbackManager.Factory.create()
        initializeXMLComponents()
        fetchRewardsData()

        return containerView
    }

    private fun fetchRewardsData() {
        val userId =
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData(
                userId,
                3
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                    override fun onComplete() {
                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                        if (response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                            apiGetResponse = response.data!!.result
                            setValuesToComponents()
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
                            editFacebook.setText(it.acc_link)
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
        try {
            if (apiGetResponse.socialAccounts != null && apiGetResponse.socialAccounts!!.isNotEmpty()) {
                val socialAccountsListNotContainsGivenPlatform =
                    ArrayList<SocialAccountObject>(apiGetResponse.socialAccounts!!.filter { it ->
                        !it.platform_name.equals(platformName.name)
                    })
                val socialAccountsListByGivenPlatform =
                    apiGetResponse.socialAccounts!!.filter { it ->
                        it.platform_name.equals(
                            platformName.name
                        )
                    }
                if (socialAccountsListByGivenPlatform.isNotEmpty()) {
                    val localSocialAccout = SocialAccountObject()
                    localSocialAccout.platform_name = platformName.name
                    localSocialAccout.acc_link = token
                    localSocialAccout.id = socialAccountsListByGivenPlatform.get(0).id
                    socialAccountsListNotContainsGivenPlatform.add(localSocialAccout)
                } else {
                    val localSocialAccout = SocialAccountObject()
                    localSocialAccout.acc_link = token
                    localSocialAccout.platform_name = platformName.name
                    apiGetResponse.socialAccounts!!.add(localSocialAccout)
                    socialAccountsListNotContainsGivenPlatform.add(localSocialAccout)
                }
                apiGetResponse.socialAccounts = socialAccountsListNotContainsGivenPlatform
            } else {
                val localSocialAccout = SocialAccountObject()
                localSocialAccout.acc_link = token
                localSocialAccout.platform_name = platformName.name
                apiGetResponse.socialAccounts!!.add(localSocialAccout)
                val localListSocialAccount = ArrayList<SocialAccountObject>()
                localListSocialAccount.add(localSocialAccout)
                apiGetResponse.socialAccounts = localListSocialAccount
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun initializeXMLComponents() {
        layoutInstagram = containerView.findViewById(R.id.layoutInstagram)
        layoutFacebook = containerView.findViewById(R.id.layout_facebook)
        layoutYoutube = containerView.findViewById(R.id.layoutYoutube)
        layoutTwitter = containerView.findViewById(R.id.layoutTwitter)
        spinnerProfession = containerView.findViewById(R.id.spinnerProfession)
        spinnerHouseHold = containerView.findViewById(R.id.spinnerHouseHold)
        editInstagram = containerView.findViewById(R.id.editInstagram)
        editFacebook = containerView.findViewById(R.id.edit_facebook)
        editTwitter = containerView.findViewById(R.id.editTwitter)
        editWebsite = containerView.findViewById(R.id.editWebsite)
        editYoutube = containerView.findViewById(R.id.editYoutube)
        textlater = containerView.findViewById(R.id.textlater)
        socialDisclaimerTwo = containerView.findViewById(R.id.social_disclaimer_two)

        if (isComingFromRewards) {
            textlater.visibility = View.VISIBLE
        } else {
            textlater.visibility = View.GONE
        }

        textlater.setOnClickListener {
            submitListener.socialOnSubmitListener()
        }

        layoutInstagram.setOnClickListener {
            // AuthenticateWithInstagram()
        }

        editInstagram.setOnClickListener {
            // AuthenticateWithInstagram()
        }

        val houseHoldIncomeArray = resources.getStringArray(R.array.household_income)
        houseHoldIncomeArray.forEach { str ->
            householdList.add(str)
        }
        val householdAdapter = CustomSpinnerAdapter(activity, householdList)
        spinnerHouseHold.adapter = householdAdapter
        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
                spinnerHouseHold.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        val professionArray = resources.getStringArray(R.array.profession)
        professionArray.forEach { str ->
            professionList.add(str)
        }

        val professionAdapter = CustomSpinnerAdapter(activity, professionList)
        spinnerProfession.adapter = professionAdapter
        spinnerProfession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
                spinnerProfession.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>,
                v: View,
                position: Int,
                id: Long
            ) {
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
        socialDisclaimerTwo.movementMethod = LinkMovementMethod.getInstance()
        socialDisclaimerTwo.highlightColor = Color.BLUE
        socialDisclaimerTwo.setText(spannable)
    }

    private fun prepareDataForPosting(): Boolean {
        if (isvalid(editFacebook.text.toString(), 0)) {
            setValuesForSocial(
                Constants.SocialPlatformName.facebook,
                editFacebook.text.toString().trim()
            )
        } else {
            ToastUtils.showToast(context, "Not a valid facebook profile")
            return false
        }
        if (isvalid(editInstagram.text.toString(), 1)) {
            setValuesForSocial(
                Constants.SocialPlatformName.instagram,
                editInstagram.text.toString().trim()
            )
        } else {
            ToastUtils.showToast(context, "space is not allowed")
            return false
        }
        if (isvalid(editTwitter.text.toString().trim(), 2)) {
            setValuesForSocial(
                Constants.SocialPlatformName.twitter,
                editTwitter.text.toString().trim()
            )
        } else {
            ToastUtils.showToast(context, "not valid Twitter handle")
            return false
        }
        if (isvalid(editWebsite.text.toString(), 3)) {
            setValuesForSocial(
                Constants.SocialPlatformName.website,
                editWebsite.text.toString().trim()
            )
        } else {
            ToastUtils.showToast(context, "space is not allowed")
            return false
        }
        if (isvalid(editYoutube.text.toString().trim(), 4)) {
            setValuesForSocial(
                Constants.SocialPlatformName.youtube,
                editYoutube.text.toString().trim()
            )
        } else {
            ToastUtils.showToast(context, "space is not allowed")
            return false
        }
        return true
    }

    private fun isvalid(handle: String, accountType: Int): Boolean {

        when (accountType) {

            0 -> {
                if (handle.startsWith("https://www.facebook.com/") || handle.startsWith("http://www.facebook.com/") || handle.isEmpty()) {
                    return true
                }
            }

            2 -> {
                val pattern = Pattern.compile("^@?([a-zA-Z0-9_]){1,15}\$")
                val matcher = pattern.matcher(handle)

                if (matcher.matches() || handle.isEmpty()) {
                    return true
                }
            }

            1 -> {

                if (!handle.trim().contains(" ")) {
                    return true
                }
            }
            3 -> {

                if (!handle.trim().contains(" ")) {
                    return true
                }
            }
            4 -> {
                if (!handle.trim().contains(" ")) {
                    return true
                }
            }
        }

        return false
    }

    private fun postDataofRewardsToServer() {
        val userId =
            SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (!userId.isNullOrEmpty()) {
            Log.e("body to api ", Gson().toJson(apiGetResponse))
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiDataForAny(
                userId,
                apiGetResponse,
                3
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                object : Observer<RewardsPersonalResponse> {
                    override fun onComplete() {
                        editTwitter
                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: RewardsPersonalResponse) {
                        if (response.code == 200) {
                            if (Constants.SUCCESS == response.status) {
                                submitListener.socialOnSubmitListener()
                            } else if (Constants.FAILURE == response.status) {
                                Toast.makeText(activity, response.reason, Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        removeProgressDialog()
                    }
                })
        }
    }

    override fun onAttach(context: Context) {
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
