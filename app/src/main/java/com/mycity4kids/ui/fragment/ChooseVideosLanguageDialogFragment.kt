package com.mycity4kids.ui.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.LanguageSelectionData
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.ShareButtonWidget
import okhttp3.ResponseBody
import org.apmem.tools.layouts.FlowLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseVideosLanguageDialogFragment : DialogFragment() {

    private var selectedLang = MutableLiveData<ArrayList<String>>()
    private lateinit var languagesContainer: FlowLayout
    private lateinit var continueTextView: MomspressoButtonWidget
    private lateinit var cancel: ImageView
    private var languageList = ArrayList<LanguageSelectionData>()
    private var langCodes = arrayOf(
        AppConstants.LOCALE_ENGLISH,
        AppConstants.LOCALE_HINDI,
        AppConstants.LOCALE_MARATHI,
        AppConstants.LOCALE_BENGALI,
        AppConstants.LOCALE_TAMIL,
        AppConstants.LOCALE_TELUGU,
        AppConstants.LOCALE_KANNADA,
        AppConstants.LOCALE_MALAYALAM,
        AppConstants.LOCALE_GUJARATI,
        AppConstants.LOCALE_PUNJABI
    )
    private var langNameList = arrayOf(
        "English",
        "हिंदी",
        "मराठी",
        "বাংলা",
        "தமிழ்",
        "తెలుగు",
        "ಕನ್ನಡ",
        "മലയാളം",
        "ગુજરાતી",
        "ਪੰਜਾਬੀ"
    )

    private var selectedLanguage = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_vlog_lang_dialog_fragment, container, false)
        languagesContainer = view.findViewById(R.id.languagesContainer)
        continueTextView = view.findViewById(R.id.continueTextView)
        cancel = view.findViewById(R.id.cancel)
        activity?.let {
            for (i in 0..9) {
                languageList.add(
                    LanguageSelectionData(
                        regionalLanguageName = langNameList[i],
                        langCode = langCodes[i]
                    )
                )
            }
        }
        setLangData()
        continueTextView.setOnClickListener {
            if (isValid()) {
                selectedLang.value = selectedLanguage
                postVlogChoosenLanguagesToServer()
            }
        }

        cancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun isValid(): Boolean {
        if (selectedLanguage.isEmpty()) {
            return false
        }
        return true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun setLangData() {
        activity?.let {
            for (i in langCodes.indices) {
                val shareButtonWidget = ShareButtonWidget(it)
                val languageTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val params = languageTextView.layoutParams
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                languageTextView.layoutParams = params
                shareButtonWidget.tag = langCodes[i]
                shareButtonWidget.setText(langNameList[i])
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(it, R.color.app_grey))
                shareButtonWidget.setButtonRadiusInDP(10f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(it, R.color.app_grey))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        it,
                        R.color.white_color
                    )
                )
                val flowParams = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT
                )
                flowParams.setMargins(10, 10, 10, 10)
                shareButtonWidget.layoutParams = flowParams
                shareButtonWidget.setOnClickListener { view: View ->
                    if (!view.isSelected) {
                        selectedLanguage.add(view.tag.toString())
                        Log.d("selectedLang", selectedLanguage.toString())
                        view.isSelected = true
                        (view as ShareButtonWidget).setTextColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                        view.setBorderColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                    } else {
                        view.isSelected = false
                        selectedLanguage.remove(view.tag)
                        Log.d("selectedLang", selectedLanguage.toString())
                        (view as ShareButtonWidget).setTextColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_grey
                            )
                        )
                        view.setBorderColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_grey
                            )
                        )
                    }
                }
                try {
                    if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).videoPreferredLanguages.contains(
                            langCodes[i]
                        )) {
                        shareButtonWidget.isSelected = true
                        selectedLanguage.add(langCodes[i])
                        shareButtonWidget.setTextColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                        shareButtonWidget.setBorderColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                    }
                } catch (e: Exception) {

                }
                languagesContainer.addView(shareButtonWidget)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun postVlogChoosenLanguagesToServer() {
        val retrofit = BaseApplication.getInstance().retrofit
        val vlogListing = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val langData = LanguageSelectionData()
        langData.videoPreferredLanguages =
            selectedLanguage.toArray(arrayOf(String()))
        val call =
            vlogListing.postLanguages(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                4,
                langData
            )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    activity?.let {
                        (it as BaseActivity).startSyncingUserInfo()
                    }
                    dismiss()
                } catch (e: Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                try {
                    activity?.let {
                        (it as BaseActivity).startSyncingUserInfo()
                    }
                    dismiss()
                } catch (e: Exception) {

                }
            }
        })
    }

    fun selectedLanguages(): LiveData<ArrayList<String>> {
        return selectedLang
    }
}