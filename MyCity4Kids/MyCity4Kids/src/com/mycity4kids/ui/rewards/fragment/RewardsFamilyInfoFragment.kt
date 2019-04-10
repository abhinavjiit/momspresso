package com.mycity4kids.ui.rewards.fragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.AppCompatSpinner
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.easing.linear.Linear
import com.google.api.client.util.DateTime
import com.google.gson.Gson
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.DateTimeUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.UserDetailData
import com.mycity4kids.models.rewardsmodels.KidsInfoResponse
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.AdapterTaskList
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.dialog.PickerDialogFragment
import com.mycity4kids.utils.AppUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.aa_attendee_item.view.*
import kotlinx.android.synthetic.main.event_details_activity.*
import kotlinx.android.synthetic.main.fragment_rewards_family_info.*
import kotlinx.android.synthetic.main.fragment_rewards_personal_info.*
import kotlinx.android.synthetic.main.splash_activity.*
import org.apmem.tools.layouts.FlowLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsFamilyInfoFragment : BaseFragment(), PickerDialogFragment.OnClickDoneListener {
    override fun onItemClick(selectedValueName: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            //preSelectedInterest = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        } else if (popupType == Constants.PopListRequestType.LANGUAGE.name) {
            //preSelectedDurables = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        }
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
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingInterest.addView(subsubLL)
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
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingLanguage.addView(subsubLL)
            }
        }
    }

    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var submitListener: SubmitListener
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
    private lateinit var linearKidsDetail: LinearLayout
    private var apiGetResponse: RewardsDetailsResultResonse = RewardsDetailsResultResonse()
    private lateinit var radioGroupWorkingStatus: RadioGroup
    private var preSelectedInterest = ArrayList<String>()
    private var preSelectedInterestForPosting = ArrayList<Int>()
    private var preSelectedLanguage = ArrayList<String>()
    private lateinit var editInterest: EditText
    private lateinit var linearInterest: LinearLayout
    private lateinit var floatingInterest: FlowLayout
    private lateinit var editLanguage: EditText
    private lateinit var linearLanguage: LinearLayout
    private lateinit var floatingLanguage: FlowLayout
    private lateinit var textEditInterest: TextView
    private lateinit var textEditLanguage: TextView
    private lateinit var textAddChild: TextView
    private lateinit var radioGroupAreMother: RadioGroup
    private lateinit var layoutDynamicNumberOfKids: LinearLayout
    private lateinit var textDeleteChild: TextView
    private lateinit var linearKidsEmptyView: LinearLayout
    private lateinit var editKidsName : EditText

    companion object {
        lateinit var textView: TextView
        private lateinit var textDOB: TextView
        private lateinit var textKidsDOB: TextView

        @JvmStatic
        fun newInstance() =
                RewardsFamilyInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_family_info, container, false)

        /*initialize XML components*/
        initializeXMLComponents()

        /*fetch data from server*/
        fetchRewardsData()

        return containerView
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        //var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData(userId!!, 2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
                }
            })
        }
    }

    private fun setValuesToComponents() {
        if (apiGetResponse.familyType != null) {
            if (apiGetResponse.familyType == 1) {
                checkNuclear.isChecked = true
            } else if (apiGetResponse.familyType == 2) {
                checkJoint.isChecked = true
            }
        }
        if (apiGetResponse.preferred_languages != null && apiGetResponse.preferred_languages!!.size > 0) {
            floatingLanguage.removeAllViews()
            textEditLanguage.visibility = View.VISIBLE
            editLanguage.visibility = View.GONE
            linearLanguage.visibility = View.VISIBLE
            apiGetResponse.preferred_languages!!.forEach {
                var interestName = Constants.TypeOfLanguages.findById(it)
                preSelectedLanguage.add(it)
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(interestName)
                catTextView.isSelected = true
                floatingLanguage.addView(subsubLL)
            }
        } else {
            editLanguage.visibility = View.VISIBLE
            linearLanguage.visibility = View.GONE
            textEditLanguage.visibility = View.GONE

        }

        if (apiGetResponse.interest != null && apiGetResponse.interest!!.isNotEmpty()) {
            floatingInterest.removeAllViews()
            textEditInterest.visibility = View.VISIBLE
            editInterest.visibility = View.GONE
            linearInterest.visibility = View.VISIBLE
            apiGetResponse.interest!!.forEach {
                var interestName = Constants.TypeOfInterest.findById(it.toInt())
                preSelectedInterest.add(it.toString())
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(interestName)
                catTextView.isSelected = true
                floatingInterest.addView(subsubLL)
            }
        } else {
            editInterest.visibility = View.VISIBLE
            linearInterest.visibility = View.GONE
            textEditInterest.visibility = View.GONE

        }

        if (apiGetResponse.isMother != null && apiGetResponse.kidsInfo != null && apiGetResponse.kidsInfo!!.isNotEmpty()) {
            radioYes.isChecked = true
        } else {
            radioNo.isChecked = true
        }

        if (apiGetResponse.workStatus != null) {
            if (apiGetResponse.workStatus == 0) {
                radioGroupWorkingStatus.check(R.id.radioNotWorking)
            } else if (apiGetResponse.workStatus == 1) {
                radioGroupWorkingStatus.check(R.id.radiokWorking)
            }
        }

        if (apiGetResponse.gender != null) {
            genderSpinner.setSelection(apiGetResponse.gender!!)
        }
        if (apiGetResponse.dob != null) {
            RewardsFamilyInfoFragment.textDOB.setText(AppUtils.convertTimestampToDate(apiGetResponse.dob))
        }

        if (apiGetResponse.isExpecting != null && apiGetResponse.isExpecting == 1 && apiGetResponse.expectedDate != null) {
            editExpectedDate.setText(AppUtils.convertTimestampToDate(apiGetResponse.expectedDate))
            checkAreYouExpecting.isChecked = true
            layoutMotherExptectedDate.visibility = View.VISIBLE
        } else {
            layoutMotherExptectedDate.visibility = View.GONE
        }

        if (apiGetResponse.kidsInfo != null && apiGetResponse.kidsInfo!!.isNotEmpty()) {
            for (i in 0..apiGetResponse.kidsInfo!!.size - 1) {
                if (i == 0 && apiGetResponse.kidsInfo!!.size == 1) {
                    createKidsDetailDynamicView(apiGetResponse.kidsInfo!!.get(i).gender!!, AppUtils.convertTimestampToDate(apiGetResponse.kidsInfo!!.get(i).dob),apiGetResponse.kidsInfo!!.get(i).name!!, false)
                } else {
                    createKidsDetailDynamicView(apiGetResponse.kidsInfo!!.get(i).gender!!, AppUtils.convertTimestampToDate(apiGetResponse.kidsInfo!!.get(i).dob),apiGetResponse.kidsInfo!!.get(i).name!!)
                }
            }

            linearKidsEmptyView.visibility = View.GONE
        } else {
            textDeleteChild.visibility = View.GONE
            linearKidsEmptyView.visibility = View.VISIBLE
        }
    }

    private fun initializeXMLComponents() {
        editExpectedDate = containerView.findViewById(R.id.editExpectedDate)
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
        RewardsFamilyInfoFragment.textKidsDOB = containerView.findViewById(R.id.textKidsDOB)
        RewardsFamilyInfoFragment.textDOB = containerView.findViewById(R.id.textDOB)
        textDeleteChild = containerView.findViewById(R.id.textDeleteChild)
        linearKidsEmptyView = containerView.findViewById(R.id.linearKidsEmptyView)

        textDeleteChild.setOnClickListener {
            if (linearKidsDetail.childCount > 0) {
                if (linearKidsDetail.childCount == 1) {
                    linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild).visibility = View.GONE
                    linearKidsEmptyView.visibility = View.GONE
                } else {
                    linearKidsEmptyView.visibility = View.GONE
                }

            }
        }

        (containerView.findViewById<CheckBox>(R.id.checkAreYouExpecting)).setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
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
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsFamilyInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        editInterest.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsFamilyInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        RewardsFamilyInfoFragment.textDOB.setOnClickListener {
            RewardsFamilyInfoFragment.textView = RewardsFamilyInfoFragment.textDOB
            showDatePickerDialog(true)
        }

        RewardsFamilyInfoFragment.textKidsDOB.setOnClickListener {
            RewardsFamilyInfoFragment.textView = RewardsFamilyInfoFragment.textKidsDOB
            showDatePickerDialog(true)
        }


        textEditLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.LANGUAGE.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedLanguage, context = this@RewardsFamilyInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        editLanguage.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.LANGUAGE.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedLanguage, context = this@RewardsFamilyInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        val genderList = ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
        genderSpinner.adapter = spinAdapter
        genderSpinner.setSelection(1)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                genderSpinner.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        editExpectedDate.setOnClickListener {
            RewardsFamilyInfoFragment.textView = editExpectedDate
            showDatePickerDialog(false,true)
        }

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
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
    }

    private fun validateChildData(): Boolean {
        Log.e("text value", " " + RewardsFamilyInfoFragment.textKidsDOB.text + " " + linearKidsEmptyView.visibility)
        if (linearKidsEmptyView.visibility == View.VISIBLE && RewardsFamilyInfoFragment.textKidsDOB.text.isBlank()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_dob)), Toast.LENGTH_SHORT).show()
            return false
        } else if (linearKidsEmptyView.visibility == View.GONE) {
            linearKidsEmptyView.visibility = View.VISIBLE
            textDeleteChild.visibility = View.VISIBLE
            if (linearKidsDetail.childCount > 0) {
                for (i in 0..linearKidsDetail.childCount - 1) {
                    linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textDeleteChild).visibility = View.VISIBLE
                }
            }
            return false
        } else if (linearKidsEmptyView.visibility == View.VISIBLE) {
            textDeleteChild.visibility = View.VISIBLE
        }

        return true
    }

    private fun prepareDataForPosting(): Boolean {
        if (radioGroupWorkingStatus.checkedRadioButtonId == R.id.radiokWorking) {
            apiGetResponse.workStatus = 1
        } else {
            apiGetResponse.workStatus = 0
        }

        apiGetResponse.gender = if (genderSpinner.selectedItemPosition == 0) {
            0
        } else {
            1
        }

        if (RewardsFamilyInfoFragment.textDOB.text.isNullOrEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_dob)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsFamilyInfoFragment.textDOB.text.toString())
        }

        if (preSelectedLanguage.isEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_language)), Toast.LENGTH_SHORT).show()
            return false
        } else {
            apiGetResponse.preferred_languages = preSelectedLanguage
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            apiGetResponse.isMother = 1
        } else if (radioGroupAreMother.checkedRadioButtonId == R.id.radioNo) {
            apiGetResponse.isMother = 0
        }

        if (!preSelectedInterest.isEmpty()) {
            preSelectedInterestForPosting.clear()
            (preSelectedInterest).forEach {
                try {
                    preSelectedInterestForPosting.add(it.toInt())
                } catch (ex: Exception) {

                }
            }
            apiGetResponse.interest = preSelectedInterestForPosting
        }

        if (checkAreYouExpecting.isChecked) {
            if (editExpectedDate.text.isNullOrEmpty()) {
                Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_expected_date)), Toast.LENGTH_SHORT).show()
                return false
            } else {
                apiGetResponse.isExpecting = 1
                apiGetResponse.expectedDate = DateTimeUtils.convertStringToTimestamp(editExpectedDate.text.toString())
            }
        } else {
            apiGetResponse.isExpecting = 0
            apiGetResponse.expectedDate = 0
        }

        if (radioGroupAreMother.checkedRadioButtonId == R.id.radioYes) {
            if (linearKidsDetail.childCount > 0) {
                var kidsList = ArrayList<KidsInfoResponse>()
                for (i in 0..linearKidsDetail.childCount) {
                    var kidsInfoResponse = KidsInfoResponse()
                    if (linearKidsDetail.getChildAt(i) != null) {
                        kidsInfoResponse.gender = if (linearKidsDetail.getChildAt(i).findViewById<Spinner>(R.id.spinnerGender).selectedItemPosition == 0) {
                            0
                        } else {
                            1
                        }
                        kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(linearKidsDetail.getChildAt(i).findViewById<TextView>(R.id.textKidsDOB).text.toString())
                        kidsInfoResponse.name = linearKidsDetail.getChildAt(i).findViewById<EditText>(R.id.editKidsName).text.toString()
                        kidsList.add(kidsInfoResponse)
                    }
                }
                apiGetResponse.kidsInfo = kidsList
                Log.e("dob text is ", RewardsFamilyInfoFragment.textKidsDOB.text.toString())
                if (linearKidsEmptyView.visibility == View.VISIBLE) {
                    if (!RewardsFamilyInfoFragment.textKidsDOB.text.isNullOrEmpty()) {
                        //if (apiGetResponse.kidsInfo.isNullOrEmpty()) {
                            var kidsInfoResponse = KidsInfoResponse()
                            kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                                0
                            } else {
                                1
                            }
                            kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsFamilyInfoFragment.textKidsDOB.text.toString())
                            kidsInfoResponse.name = editKidsName.text.toString()
                            apiGetResponse.kidsInfo!!.add(kidsInfoResponse)
                        //}
                    } else {
                        Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_number_of_kids)), Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            } else {
                if (!RewardsFamilyInfoFragment.textKidsDOB.text.isNullOrEmpty()) {
                    var kidsInfoLocal = ArrayList<KidsInfoResponse>()
                    var kidsInfoResponse = KidsInfoResponse()
                    kidsInfoResponse.gender = if (spinnerGender.selectedItemPosition == 0) {
                        0
                    } else {
                        1
                    }
                    kidsInfoResponse.dob = DateTimeUtils.convertStringToTimestamp(RewardsFamilyInfoFragment.textKidsDOB.text.toString())
                    kidsInfoResponse.name = editKidsName.text.toString()
                    kidsInfoLocal.add(kidsInfoResponse)
                    apiGetResponse.kidsInfo = kidsInfoLocal
                } else {
                    Toast.makeText(activity, resources.getString(R.string.cannot_be_left_blank, resources.getString(R.string.rewards_number_of_kids)), Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        } else {
            apiGetResponse.kidsInfo = null
        }

        apiGetResponse.latitude = 28.7041
        apiGetResponse.longitude = 77.1025
        return true
    }

    fun createKidsDetailDynamicView(gender: Int? = null, date: String = "",name:String = "", shouldDelteShow: Boolean = true) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val indexView = inflater.inflate(R.layout.dynamic_child_view, null)
        var textHeader = indexView.findViewById<TextView>(R.id.textHeader)
        var textDelete = indexView.findViewById<TextView>(R.id.textDeleteChild)
        var editKidsName = indexView.findViewById<EditText>(R.id.editKidsName)
        if (shouldDelteShow) {
            textDelete.visibility = View.VISIBLE
        } else {
            textDelete.visibility = View.GONE
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
                linearKidsDetail.getChildAt(0).findViewById<TextView>(R.id.textDeleteChild).visibility = View.GONE
            } else if (linearKidsEmptyView.visibility == View.VISIBLE && linearKidsDetail.childCount == 0) {
                textDeleteChild.visibility = View.GONE
            }

        }
        var spinnerGender = indexView.findViewById<Spinner>(R.id.spinnerGender)
        var textDOB = indexView.findViewById<TextView>(R.id.textKidsDOB)

        //textHeader.setText(String.format(resources.getString(R.string.kids_number), linearKidsDetail.childCount+1))
        val genderList = java.util.ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        spinnerGender.adapter = spinAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerGender.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        textDOB.setOnClickListener {
            RewardsFamilyInfoFragment.textView = it as TextView
            showDatePickerDialog(true, false)
        }

        if (gender != null && !date.isNullOrEmpty()) {
            textDOB.text = date
            spinnerGender.setSelection(gender)
            if(!name.isNullOrEmpty()){
                editKidsName.setText(name)
            }
        } else {
            textDOB.text = RewardsFamilyInfoFragment.textKidsDOB.text
            spinnerGender.setSelection(this.spinnerGender.selectedItemPosition)
            editKidsName.text = this.editKidsName.text
            this.spinnerGender.setSelection(0)
            RewardsFamilyInfoFragment.textKidsDOB.text = ""
            this.editKidsName.setText("")
        }

        linearKidsDetail.addView(indexView)
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        internal var cancel: Boolean = false
        internal val c = Calendar.getInstance()
        internal var curent_year = c.get(Calendar.YEAR)
        internal var current_month = c.get(Calendar.MONTH)
        internal var current_day = c.get(Calendar.DAY_OF_MONTH)
        var isShowTillCurrent: Boolean = false
        var isShowFutureOnly: Boolean = false


        @SuppressLint("NewApi")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val dlg = DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day)

            if (arguments != null) {
                isShowTillCurrent = arguments.getBoolean("is_show_current_only", false)
                isShowFutureOnly = arguments.getBoolean("is_show_future_only", false)
            }
            dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (isShowTillCurrent) {
                dlg.datePicker.maxDate = c.timeInMillis
            }

            if(isShowFutureOnly){
                dlg.datePicker.minDate = c.timeInMillis
            }

            return dlg
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            if (RewardsFamilyInfoFragment.textView != null) {
                val sel_date = "" + day + "-" + (month + 1) + "-" + year
//                if (chkTime(sel_date)) {
                RewardsFamilyInfoFragment.textView.setText("" + day + "-" + (month + 1) + "-" + year)
//                } else {
//                    //RewardsFamilyInfoFragment.textView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year)
//                }
            }
        }

        fun chkTime(time: String): Boolean {
            var result = true
            val currentime = "" + System.currentTimeMillis() / 1000
            if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
                result = false

            return result
        }

        fun convertDate(convertdate: String): String {
            var timestamp = ""
            try {
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val dateobj = formatter.parse(convertdate)
                timestamp = "" + dateobj.time / 1000
                return timestamp
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return timestamp
        }
    }

    fun showDatePickerDialog(isShowTillCurrent: Boolean, isShowFutureDate: Boolean = false) {
        val newFragment = DatePickerFragment()
        var bundle = Bundle()
        bundle.putBoolean("is_show_current_only", isShowTillCurrent)
        bundle.putBoolean("is_show_future_only", isShowFutureDate)
        newFragment.arguments = bundle
        newFragment.show(activity.supportFragmentManager, "datePicker")
    }

    /*send data to server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
//        var userId = "6f57d7cb01fa46c89bf85e3d2ade7de3"
        if (!userId.isNullOrEmpty()) {
            Log.e("body to api ", Gson().toJson(apiGetResponse))
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData(userId!!, apiGetResponse, 2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<SetupBlogData>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<SetupBlogData>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response.data!!.msg.equals(Constants.SUCCESS_MESSAGE)) {
                        submitListener.FamilyOnSubmit()
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

    fun convertStringToTimestamp(): Long {
        return DateTimeUtils.convertStringToTimestamp(RewardsFamilyInfoFragment.textDOB.getText().toString())
    }

    interface SubmitListener {
        fun FamilyOnSubmit()
    }
}
